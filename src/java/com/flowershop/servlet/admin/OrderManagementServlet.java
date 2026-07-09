package com.flowershop.servlet.admin;

import com.flowershop.dao.FlowerDAO;
import com.flowershop.dao.OrderDAO;
import com.flowershop.dao.OrderDetailDAO;
import com.flowershop.model.Flower;
import com.flowershop.model.Order;
import com.flowershop.model.OrderDetail;
import com.flowershop.util.PaginationUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet quản lý đơn hàng dành cho Admin.
 * - GET  : xem danh sách đơn hàng (search + filter + phân trang), xem chi tiết 1 đơn.
 * - POST : cập nhật trạng thái đơn hàng, xóa đơn hàng.
 *
 * @author ADMIN
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/admin/order"})
public class OrderManagementServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final FlowerDAO flowerDAO = new FlowerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("detail".equals(action)) {
            showOrderDetail(request, response);
        } else {
            listOrders(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            updateStatus(request, response);
        } else if ("delete".equals(action)) {
            deleteOrder(request, response);
        } else {
            listOrders(request, response);
        }
    }

    // ================== DANH SÁCH ĐƠN HÀNG (Search + Filter + Pagination) ==================
    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        int page = PaginationUtil.parsePage(request.getParameter("page"));
        int pageSize = PaginationUtil.DEFAULT_PAGE_SIZE;

        int totalRecords = orderDAO.countSearchOrders(keyword, status);
        int totalPages = PaginationUtil.getTotalPages(totalRecords, pageSize);
        page = PaginationUtil.normalizePage(page, totalPages);

        List<Order> orderList = orderDAO.searchOrders(keyword, status, page, pageSize);

        request.setAttribute("orderList", orderList);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        StringBuilder baseUrl = new StringBuilder(request.getContextPath()).append("/admin/order");
        StringBuilder qs = new StringBuilder();
        if (keyword != null && !keyword.trim().isEmpty()) {
            qs.append("keyword=").append(java.net.URLEncoder.encode(keyword.trim(), "UTF-8")).append("&");
        }
        if (status != null && !status.trim().isEmpty()) {
            qs.append("status=").append(java.net.URLEncoder.encode(status.trim(), "UTF-8")).append("&");
        }
        if (qs.length() > 0) {
            qs.setLength(qs.length() - 1); // bỏ dấu & thừa ở cuối
            baseUrl.append("?").append(qs);
        }
        request.setAttribute("baseUrl", baseUrl.toString());

        request.getRequestDispatcher("/WEB-INF/jsp/admin/orders.jsp").forward(request, response);
    }

    // ================== CHI TIẾT 1 ĐƠN HÀNG ==================
    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int orderID = parseIntSafe(request.getParameter("id"), -1);

        if (orderID <= 0) {
            redirectWithError(request, response, "Mã đơn hàng không hợp lệ.");
            return;
        }

        Order order = orderDAO.getOrderById(orderID);
        if (order == null) {
            redirectWithError(request, response, "Không tìm thấy đơn hàng #" + orderID + ".");
            return;
        }

        List<OrderDetail> detailList = orderDetailDAO.getOrderDetailsByOrderId(orderID);

        // Lấy kèm thông tin tên hoa cho từng dòng chi tiết để hiển thị ở JSP
        for (OrderDetail d : detailList) {
            Flower flower = flowerDAO.getFlowerById(d.getFlowerID());
            if (flower != null) {
                request.setAttribute("flowerName_" + d.getFlowerID(), flower.getFlowerName());
            }
        }

        request.setAttribute("order", order);
        request.setAttribute("detailList", detailList);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/order-detail.jsp").forward(request, response);
    }

    // ================== CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG ==================
    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int orderID = parseIntSafe(request.getParameter("orderID"), -1);
        String newStatus = request.getParameter("status");

        if (orderID <= 0 || newStatus == null || newStatus.trim().isEmpty()) {
            redirectWithError(request, response, "Dữ liệu cập nhật trạng thái không hợp lệ.");
            return;
        }

        boolean success = orderDAO.updateStatus(orderID, newStatus.trim());
        HttpSession session = request.getSession();

        if (success) {
            session.setAttribute("session_message", "Cập nhật trạng thái đơn hàng #" + orderID + " thành công.");
        } else {
            session.setAttribute("session_error", "Cập nhật trạng thái thất bại. Vui lòng thử lại.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/order?action=detail&id=" + orderID);
    }

    // ================== XÓA ĐƠN HÀNG ==================
    private void deleteOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int orderID = parseIntSafe(request.getParameter("orderID"), -1);
        HttpSession session = request.getSession();

        if (orderID <= 0) {
            session.setAttribute("session_error", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/order");
            return;
        }

        boolean success = orderDAO.deleteOrder(orderID);

        if (success) {
            session.setAttribute("session_message", "Đã xóa đơn hàng #" + orderID + ".");
        } else {
            session.setAttribute("session_error", "Xóa đơn hàng thất bại. Vui lòng thử lại.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/order");
    }

    // ================== HÀM HỖ TRỢ ==================
    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("session_error", message);
        response.sendRedirect(request.getContextPath() + "/admin/order");
    }
}