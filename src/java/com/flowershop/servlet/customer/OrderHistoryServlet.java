package com.flowershop.servlet.customer;

import com.flowershop.dao.FlowerDAO;
import com.flowershop.dao.OrderDAO;
import com.flowershop.dao.OrderDetailDAO;
import com.flowershop.model.Flower;
import com.flowershop.model.Order;
import com.flowershop.model.OrderDetail;
import com.flowershop.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/orders"})
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/orders");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        FlowerDAO flowerDAO = new FlowerDAO();
        try {
            String action = request.getParameter("action");
            if ("detail".equals(action)) {
                showOrderDetail(request, response, user, orderDAO, orderDetailDAO, flowerDAO);
            } else {
                showOrderHistory(request, response, user, orderDAO);
            }
        } finally {
            orderDAO.closeConnection();
            orderDetailDAO.closeConnection();
            flowerDAO.closeConnection();
        }
    }

    private void showOrderHistory(HttpServletRequest request, HttpServletResponse response,
            User user, OrderDAO orderDAO)
            throws ServletException, IOException {
        List<Order> orderList = orderDAO.getOrdersByUser(user.getUserID());
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/WEB-INF/jsp/customer/order-history.jsp").forward(request, response);
    }

    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response,
            User user, OrderDAO orderDAO, OrderDetailDAO orderDetailDAO, FlowerDAO flowerDAO)
            throws ServletException, IOException {
        int orderID = parseIntSafe(request.getParameter("id"), -1);
        Order order = orderID > 0 ? orderDAO.getOrderById(orderID) : null;

        if (order == null || order.getUserID() != user.getUserID()) {
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }

        List<OrderDetail> detailList = orderDetailDAO.getOrderDetailsByOrderId(orderID);
        for (OrderDetail d : detailList) {
            Flower flower = flowerDAO.getFlowerById(d.getFlowerID());
            if (flower != null) {
                request.setAttribute("flowerName_" + d.getFlowerID(), flower.getFlowerName());
                request.setAttribute("flowerImage_" + d.getFlowerID(), flower.getImage());
            }
        }

        request.setAttribute("order", order);
        request.setAttribute("detailList", detailList);
        request.getRequestDispatcher("/WEB-INF/jsp/customer/order-detail.jsp").forward(request, response);
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}