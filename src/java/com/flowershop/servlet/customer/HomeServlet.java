package com.flowershop.servlet.customer;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.model.Category;
import com.flowershop.model.Flower;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Displays the homepage: category navigation bar + featured/best-selling flowers.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private static final String HOME_VIEW = "/WEB-INF/jsp/customer/homepage.jsp";

    private FlowerDAO flowerDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        flowerDAO = new FlowerDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.getActiveCategories();
        List<Flower> featuredFlowers = flowerDAO.getTopSellingFlower(8);

        request.setAttribute("categories", categories);
        request.setAttribute("featuredFlowers", featuredFlowers);

        request.getRequestDispatcher(HOME_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Customer servlet for the homepage";
    }
}