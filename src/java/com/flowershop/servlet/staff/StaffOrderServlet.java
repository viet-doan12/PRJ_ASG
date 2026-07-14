package com.flowershop.servlet.staff;

import com.flowershop.dao.FlowerDAO;
import com.flowershop.dao.OrderDAO;
import com.flowershop.dao.OrderDetailDAO;
import com.flowershop.model.Flower;
import com.flowershop.model.Order;
import com.flowershop.model.OrderDetail;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet dành cho nhân viên (Staff) quản lý đơn hàng.
 * Chức năng: Xem đơn / Đổi trạng thái / Chuẩn bị hàng.
 *
 * Hỗ trợ các action:
 *  - list          : danh sách đơn hàng (mặc định), có tìm kiếm + lọc trạng thái + phân trang
 *  - detail        : xem chi tiết 1 đơn hàng (kèm danh sách sản phẩm trong đơn)
 *  - updateStatus  : đổi trạng thái đơn hàng theo lựa chọn tự do trên form chi tiết
 *  - prepare       : hành động nhanh "Chuẩn bị hàng" - chuyển Pending -> Confirmed chỉ với 1 click
 *
 * Lưu ý: KHÔNG có action xóa đơn - nhân viên không có quyền xóa đơn hàng.
 */
@WebServlet(name = "StaffOrderServlet", urlPatterns = {"/staff/orders"})
public class StaffOrderServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/jsp/staff/stafforder.jsp";
    private static final int PAGE_SIZE = 10;

    // Các trạng thái staff được phép chuyển tới (không cho phép quay lại "Pending")
    private static final String[] ALLOWED_TARGET_STATUS = {
        "Confirmed", "Shipping", "Completed", "Cancelled"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        FlowerDAO flowerDAO = new FlowerDAO();

        try {
            String action = request.getParameter("action");
            if (action == null) {
                action = "list";
            }

            switch (action) {
                case "detail":
                    showOrderDetail(request, response, orderDAO, orderDetailDAO, flowerDAO);
                    break;
                case "updateStatus":
                    updateStatus(request, response, orderDAO);
                    break;
                case "prepare":
                    prepareOrder(request, response, orderDAO);
                    break;
                case "list":
                default:
                    listOrders(request, response, orderDAO);
                    break;
            }
        } finally {
            orderDAO.closeConnection();
            orderDetailDAO.closeConnection();
            flowerDAO.closeConnection();
        }
    }

    // ================= action=list (Xem đơn) =================
    private void listOrders(HttpServletRequest request, HttpServletResponse response, OrderDAO orderDAO)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");

        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isBlank()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Order> orderList = orderDAO.searchOrders(keyword, status, page, PAGE_SIZE);
        int totalRecords = orderDAO.countSearchOrders(keyword, status);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        request.setAttribute("orderList", orderList);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        // order == null -> stafforder.jsp sẽ tự hiển thị chế độ danh sách
        request.setAttribute("order", null);

        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    // ================= action=detail (Xem đơn - chi tiết) =================
    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response,
            OrderDAO orderDAO, OrderDetailDAO orderDetailDAO, FlowerDAO flowerDAO)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=list");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            Order order = orderDAO.getOrderById(orderId);

            if (order == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn hàng #" + orderId);
                listOrders(request, response, orderDAO);
                return;
            }

            List<OrderDetail> detailList = orderDetailDAO.getOrderDetailsByOrderId(orderId);

            for (OrderDetail d : detailList) {
                Flower f = flowerDAO.getFlowerById(d.getFlowerID());
                if (f != null) {
                    request.setAttribute("flowerName_" + d.getFlowerID(), f.getFlowerName());
                    request.setAttribute("flowerImage_" + d.getFlowerID(), f.getImage());
                } else {
                    request.setAttribute("flowerName_" + d.getFlowerID(), "Sản phẩm đã bị xóa");
                }
            }

            request.setAttribute("order", order);
            request.setAttribute("detailList", detailList);
            request.setAttribute("allowedStatusList", ALLOWED_TARGET_STATUS);
            request.getRequestDispatcher(VIEW).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=list");
        }
    }

    // ================= action=updateStatus (Đổi trạng thái) =================
    private void updateStatus(HttpServletRequest request, HttpServletResponse response, OrderDAO orderDAO)
            throws IOException {

        String idParam = request.getParameter("orderID");
        String newStatus = request.getParameter("status");

        if (idParam == null || newStatus == null || newStatus.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=list");
            return;
        }

        // Chỉ cho phép đổi sang các trạng thái nằm trong danh sách được phép
        boolean valid = false;
        for (String s : ALLOWED_TARGET_STATUS) {
            if (s.equalsIgnoreCase(newStatus)) {
                valid = true;
                break;
            }
        }

        try {
            int orderId = Integer.parseInt(idParam);
            if (valid) {
                orderDAO.updateStatus(orderId, newStatus);
            }
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=detail&id=" + orderId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=list");
        }
    }

    // ================= action=prepare (Chuẩn bị hàng - shortcut Pending -> Confirmed) =================
    private void prepareOrder(HttpServletRequest request, HttpServletResponse response, OrderDAO orderDAO)
            throws IOException {

        String idParam = request.getParameter("orderID");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=list");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            Order order = orderDAO.getOrderById(orderId);

            // Chỉ cho phép "Chuẩn bị hàng" khi đơn đang ở trạng thái Pending
            if (order != null && "Pending".equalsIgnoreCase(order.getStatus())) {
                orderDAO.updateStatus(orderId, "Confirmed");
            }
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=detail&id=" + orderId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/orders?action=list");
        }
    }

    @Override
    public String getServletInfo() {
        return "Staff servlet for viewing orders, updating status and preparing goods";
    }
}