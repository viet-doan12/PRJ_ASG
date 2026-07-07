package com.flowershop.servlet.customer;

import com.flowershop.dao.OrderDAO;
import com.flowershop.model.Order;
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

    private final OrderDAO orderDAO = new OrderDAO();

    // HIỂN THỊ LỊCH SỬ ĐƠN HÀNG (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("user");

        // 1. Kiểm tra đăng nhập
        if (userObj == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Lấy ID người dùng hiện tại (Thay bằng logic Model User thực tế của dự án nếu cần)
        // int userID = ((User) userObj).getUserID();
        int userID = 1; 

        // 3. Gọi DAO lấy danh sách đơn hàng đã đặt (Sắp xếp theo ngày mới nhất)
        List<Order> orderList = orderDAO.getOrdersByUser(userID);

        // 4. Đẩy dữ liệu sang trang JSP để hiển thị
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("order-history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu không xử lý các thao tác hủy đơn hay sửa đơn bằng POST ở đây thì gọi doGet
        doGet(request, response);
    }
}