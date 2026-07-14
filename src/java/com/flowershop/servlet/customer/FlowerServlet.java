package com.flowershop.servlet.customer;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.dao.OrderDetailDAO;
import com.flowershop.dao.ReviewDAO;
import com.flowershop.dao.UserDAO;
import com.flowershop.model.Category;
import com.flowershop.model.Flower;
import com.flowershop.model.Review;
import com.flowershop.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Customer-facing servlet: displays a single flower's details and its reviews.
 *
 * @author ADMIN
 */
@WebServlet(name = "FlowerServlet", urlPatterns = {"/flower"})
public class FlowerServlet extends HttpServlet {

    private static final String DETAIL_VIEW = "/WEB-INF/jsp/customer/flower-detail.jsp";
    private static final int RELATED_LIMIT = 4;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        FlowerDAO flowerDAO = new FlowerDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        ReviewDAO reviewDAO = new ReviewDAO();
        UserDAO userDAO = new UserDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

        try {
            int flowerID = Integer.parseInt(idParam);
            Flower flower = flowerDAO.getFlowerById(flowerID);
            if (flower == null || !flower.isStatus()) {
                response.sendRedirect(request.getContextPath() + "/home?msg=flowerNotFound");
                return;
            }
            request.setAttribute("flower", flower);

            Category category = categoryDAO.getCategoryById(flower.getCategoryID());
            request.setAttribute("category", category);

            // ===== SẢN PHẨM LIÊN QUAN (cùng danh mục, loại trừ chính nó) =====
            List<Flower> relatedFlowers = new ArrayList<>();
            if (category != null) {
                List<Flower> sameCategory = flowerDAO.getFlowerByCategory(category.getCategoryID());
                for (Flower f : sameCategory) {
                    if (f.getFlowerID() != flowerID && f.isStatus()) {
                        relatedFlowers.add(f);
                        if (relatedFlowers.size() >= RELATED_LIMIT) {
                            break;
                        }
                    }
                }
            }
            request.setAttribute("relatedFlowers", relatedFlowers);

            // ===== ĐÁNH GIÁ SẢN PHẨM =====
            List<Review> reviews = reviewDAO.getReviewsByFlowerId(flowerID);
            request.setAttribute("reviewList", reviews);
            request.setAttribute("averageRating", reviewDAO.getAverageRating(flowerID));
            request.setAttribute("reviewCount", reviewDAO.getReviewCount(flowerID));

            Map<Integer, String> reviewerNames = new HashMap<>();
            for (Review r : reviews) {
                if (!reviewerNames.containsKey(r.getUserID())) {
                    User u = userDAO.getUserById(r.getUserID());
                    reviewerNames.put(r.getUserID(), (u != null) ? u.getFullName() : "Ẩn danh");
                }
            }
            request.setAttribute("reviewerNames", reviewerNames);

            // ===== ĐIỀU KIỆN CHO PHÉP VIẾT ĐÁNH GIÁ =====
            HttpSession session = request.getSession(false);
            User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

            if (sessionUser != null) {
                int eligibleOrderId = orderDetailDAO.findCompletedOrderIdForReview(sessionUser.getUserID(), flowerID);
                if (eligibleOrderId > 0 && !reviewDAO.hasReviewed(eligibleOrderId, flowerID, sessionUser.getUserID())) {
                    request.setAttribute("canReview", true);
                    request.setAttribute("reviewOrderId", eligibleOrderId);
                } else {
                    request.setAttribute("canReview", false);
                }
            } else {
                request.setAttribute("canReview", false);
            }

            request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home?msg=invalidId");
        } finally {
            flowerDAO.closeConnection();
            categoryDAO.closeConnection();
            reviewDAO.closeConnection();
            userDAO.closeConnection();
            orderDetailDAO.closeConnection();
        }
    }

    @Override
    public String getServletInfo() {
        return "Customer servlet for viewing flower details and reviews";
    }
}