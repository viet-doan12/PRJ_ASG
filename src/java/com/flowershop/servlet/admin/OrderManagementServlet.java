package com.flowershop.servlet.admin;

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
 * Admin servlet for managing Orders.
 * Handles: ?action=list | detail | updateStatus | delete
 *
 * @author ADMIN
 */
@WebServlet(name = "OrderManagementServlet", urlPatterns = {"/admin/orders"})
public class OrderManagementServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/jsp/admin/orders.jsp";
    private static final String DETAIL_VIEW = "/WEB-INF/jsp/admin/order-detail.jsp";
    private static final int PAGE_SIZE = 10;

    // Không còn field cấp lớp / init() - 3 DAO được tạo mới và đóng lại
    // ngay trong từng request.

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
                case "delete":
                    deleteOrder(request, response, orderDAO);
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

    // ================= action=list =================
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

        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    // ================= action=detail =================
    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response,
            OrderDAO orderDAO, OrderDetailDAO orderDetailDAO, FlowerDAO flowerDAO)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            Order order = orderDAO.getOrderById(orderId);

            if (order == null) {
                request.setAttribute("order", null);
                request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
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
            request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
        }
    }

    // ================= action=updateStatus =================
    private void updateStatus(HttpServletRequest request, HttpServletResponse response, OrderDAO orderDAO)
            throws IOException {

        String idParam = request.getParameter("orderID");
        String newStatus = request.getParameter("status");

        if (idParam == null || newStatus == null || newStatus.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            orderDAO.updateStatus(orderId, newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=detail&id=" + orderId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
        }
    }

    // ================= action=delete =================
    private void deleteOrder(HttpServletRequest request, HttpServletResponse response, OrderDAO orderDAO)
            throws IOException {

        String idParam = request.getParameter("orderID");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            orderDAO.deleteOrder(orderId);
        } catch (NumberFormatException e) {
            // ignore, just fall through to list
        }
        response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
    }

    @Override
    public String getServletInfo() {
        return "Admin servlet for Order management";
    }
}