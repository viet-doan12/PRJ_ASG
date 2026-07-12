package com.flowershop.servlet.customer;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.model.Category;
import com.flowershop.model.Flower;
import com.flowershop.util.PaginationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Customer-facing catalog page: /flowers
 * Supports filtering by category, keyword search, sorting, and pagination.
 */
@WebServlet(name = "FlowerListServlet", urlPatterns = {"/flowers"})
public class FlowerListServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/jsp/customer/flower.jsp";
    private static final int PAGE_SIZE = PaginationUtil.DEFAULT_PAGE_SIZE;

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

        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("categoryID");
        String sort = request.getParameter("sort");
        int page = PaginationUtil.parsePage(request.getParameter("page"));

        List<Flower> flowers;

        if (keyword != null && !keyword.trim().isEmpty()) {
            flowers = flowerDAO.searchFlower(keyword.trim());
        } else if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                int categoryId = Integer.parseInt(categoryParam);
                flowers = flowerDAO.getFlowerByCategory(categoryId);
            } catch (NumberFormatException e) {
                flowers = flowerDAO.getAllFlowers();
            }
        } else {
            flowers = flowerDAO.getAllFlowers();
        }

        // customers should never see hidden / stopped-selling products
        List<Flower> visibleFlowers = new ArrayList<>();
        for (Flower f : flowers) {
            if (f.isStatus()) {
                visibleFlowers.add(f);
            }
        }
        flowers = visibleFlowers;

        sortFlowers(flowers, sort);

        int totalRecords = flowers.size();
        int totalPages = PaginationUtil.getTotalPages(totalRecords, PAGE_SIZE);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        int fromIndex = PaginationUtil.getOffset(page, PAGE_SIZE);
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalRecords);
        List<Flower> pageFlowers = (fromIndex < totalRecords)
                ? flowers.subList(fromIndex, toIndex)
                : new ArrayList<>();

        List<Category> categories = categoryDAO.getActiveCategories();

        request.setAttribute("flowerList", pageFlowers);
        request.setAttribute("categoryList", categories);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void sortFlowers(List<Flower> flowers, String sort) {
        if (sort == null) {
            return;
        }
        switch (sort) {
            case "price_asc":
                flowers.sort(Comparator.comparing(Flower::getPrice,
                        Comparator.nullsLast(BigDecimal::compareTo)));
                break;
            case "price_desc":
                flowers.sort(Comparator.comparing(Flower::getPrice,
                        Comparator.nullsLast(BigDecimal::compareTo)).reversed());
                break;
            case "name_asc":
                flowers.sort(Comparator.comparing(Flower::getFlowerName, String.CASE_INSENSITIVE_ORDER));
                break;
            default:
                // "default" or unrecognized value: keep the DAO's natural order
                break;
        }
    }

    @Override
    public String getServletInfo() {
        return "Customer servlet for browsing/searching the flower catalog";
    }
}