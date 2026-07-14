package com.flowershop.servlet.customer;

import com.flowershop.dao.OrderDetailDAO;
import com.flowershop.dao.ReviewDAO;
import com.flowershop.model.Review;
import com.flowershop.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Xử lý việc gửi đánh giá sản phẩm từ trang chi tiết hoa.
 * POST /review
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/review"})
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String flowerIdParam = request.getParameter("flowerID");
        String orderIdParam = request.getParameter("orderID");
        String ratingParam = request.getParameter("rating");
        String comment = request.getParameter("comment");

        if (user == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/flower?id=" + flowerIdParam);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int flowerId = parseIntSafe(flowerIdParam, -1);
        int orderId = parseIntSafe(orderIdParam, -1);
        int rating = parseIntSafe(ratingParam, -1);

        if (flowerId <= 0 || orderId <= 0 || rating < 1 || rating > 5) {
            session.setAttribute("session_error", "Dữ liệu đánh giá không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/flower?id=" + flowerId);
            return;
        }

        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        ReviewDAO reviewDAO = new ReviewDAO();

        try {
            // Xác nhận lại (phía server) quyền đánh giá - không tin dữ liệu form gửi lên
            int verifiedOrderId = orderDetailDAO.findCompletedOrderIdForReview(user.getUserID(), flowerId);

            if (verifiedOrderId <= 0 || verifiedOrderId != orderId) {
                session.setAttribute("session_error", "Bạn chỉ có thể đánh giá sản phẩm đã mua và nhận hàng thành công.");
                response.sendRedirect(request.getContextPath() + "/flower?id=" + flowerId);
                return;
            }

            if (reviewDAO.hasReviewed(orderId, flowerId, user.getUserID())) {
                session.setAttribute("session_error", "Bạn đã đánh giá sản phẩm này rồi.");
                response.sendRedirect(request.getContextPath() + "/flower?id=" + flowerId);
                return;
            }

            Review review = new Review();
            review.setOrderID(orderId);
            review.setFlowerID(flowerId);
            review.setUserID(user.getUserID());
            review.setRating(rating);
            review.setComment(comment != null ? comment.trim() : "");
            review.setStatus(true);

            int newReviewId = reviewDAO.addReview(review);

            if (newReviewId > 0) {
                session.setAttribute("session_message", "Cảm ơn bạn đã đánh giá sản phẩm!");
            } else {
                session.setAttribute("session_error", "Gửi đánh giá thất bại. Vui lòng thử lại.");
            }

            response.sendRedirect(request.getContextPath() + "/flower?id=" + flowerId);
        } finally {
            orderDetailDAO.closeConnection();
            reviewDAO.closeConnection();
        }
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}