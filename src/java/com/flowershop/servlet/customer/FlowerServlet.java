package com.flowershop.servlet.customer;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.dao.ReviewDAO;
import com.flowershop.dao.UserDAO;
import com.flowershop.model.Category;
import com.flowershop.model.Flower;
import com.flowershop.model.Review;
import com.flowershop.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Customer-facing servlet: displays a single flower's details and its reviews.
 *
 * @author ADMIN
 */
@WebServlet(name = "FlowerServlet", urlPatterns = {"/flower"})
public class FlowerServlet extends HttpServlet {

    private static final String DETAIL_VIEW = "/WEB-INF/jsp/customer/flower-detail.jsp";

    // Không còn field cấp lớp / init() - 4 DAO được tạo và đóng ngay trong từng request,
    // tránh giữ 4 connection sống suốt vòng đời servlet.

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

            List<Review> reviews = reviewDAO.getReviewsByFlowerId(flowerID);
            request.setAttribute("reviewList", reviews);
            request.setAttribute("averageRating", reviewDAO.getAverageRating(flowerID));
            request.setAttribute("reviewCount", reviewDAO.getReviewCount(flowerID));

            Map<Integer, String> reviewerNames = new HashMap<>();
            for (Review r : reviews) {
                if (!reviewerNames.containsKey(r.getUserID())) {
                    User u = userDAO.getUserById(r.getUserID());
                    reviewerNames.put(r.getUserID(), (u != null) ? u.getFullName() : "Anonymous");
                }
            }
            request.setAttribute("reviewerNames", reviewerNames);
request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home?msg=invalidId");
        } finally {
            flowerDAO.closeConnection();
            categoryDAO.closeConnection();
            reviewDAO.closeConnection();
            userDAO.closeConnection();
        }
    }

    @Override
    public String getServletInfo() {
        return "Customer servlet for viewing flower details and reviews";
    }
}