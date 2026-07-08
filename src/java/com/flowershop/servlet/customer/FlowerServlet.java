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

    private FlowerDAO flowerDAO;
    private CategoryDAO categoryDAO;
    private ReviewDAO reviewDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        flowerDAO = new FlowerDAO();
        categoryDAO = new CategoryDAO();
        reviewDAO = new ReviewDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int flowerID = Integer.parseInt(idParam);

            // ---- Flower details ----
            Flower flower = flowerDAO.getFlowerById(flowerID);
            if (flower == null || !flower.isStatus()) {
                // not found, or deactivated by admin - don't show it
                response.sendRedirect(request.getContextPath() + "/home?msg=flowerNotFound");
                return;
            }
            request.setAttribute("flower", flower);

            // ---- Category (for breadcrumb / related products) ----
            Category category = categoryDAO.getCategoryById(flower.getCategoryID());
            request.setAttribute("category", category);

            // ---- Reviews ----
            List<Review> reviews = reviewDAO.getReviewsByFlowerId(flowerID);
            request.setAttribute("reviewList", reviews);
            request.setAttribute("averageRating", reviewDAO.getAverageRating(flowerID));
            request.setAttribute("reviewCount", reviewDAO.getReviewCount(flowerID));

            // ---- Reviewer names (avoid calling DB once per review) ----
            Map<Integer, String> reviewerNames = new HashMap<>();
            for (Review r : reviews) {
                if (!reviewerNames.containsKey(r.getUserID())) {
                    User u = userDAO.getUserById(r.getUserID());
                    reviewerNames.put(r.getUserID(), (u != null) ? u.getFullName() : "Anonymous");
                }
            }
            request.setAttribute("reviewerNames", reviewerNames);

            request.getRequestDispatcher("/customer/flowerDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home?msg=invalidId");
        }
    }

    @Override
    public String getServletInfo() {
        return "Customer servlet for viewing flower details and reviews";
    }
}
