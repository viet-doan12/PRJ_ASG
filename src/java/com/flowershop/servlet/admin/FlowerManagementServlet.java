package com.flowershop.servlet.admin;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.model.Category;
import com.flowershop.model.Flower;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * Admin CRUD servlet for managing Flowers.
 * Handles: ?action=list | add | insert | edit | update | delete
 *
 * @author ADMIN
 */
@WebServlet(name = "FlowerManagementServlet", urlPatterns = {"/admin/flowers"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5MB max upload
public class FlowerManagementServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/jsp/admin/flowers.jsp";
    private static final String FORM_VIEW = "/WEB-INF/jsp/admin/flower-form.jsp";
    private static final int PAGE_SIZE = 10;

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

        FlowerDAO flowerDAO = new FlowerDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        try {
            String action = request.getParameter("action");
            if (action == null) {
                action = "list";
            }

            switch (action) {
                case "list":
                    listFlowers(request, response, flowerDAO, categoryDAO);
                    break;
                case "add":
                    showAddForm(request, response, categoryDAO);
                    break;
                case "insert":
                    insertFlower(request, response, flowerDAO, categoryDAO);
                    break;
                case "edit":
                    showEditForm(request, response, flowerDAO, categoryDAO);
                    break;
                case "update":
                    updateFlower(request, response, flowerDAO, categoryDAO);
                    break;
                case "delete":
                    deleteFlower(request, response, flowerDAO);
                    break;
                default:
                    listFlowers(request, response, flowerDAO, categoryDAO);
                    break;
            }
        } finally {
            flowerDAO.closeConnection();
            categoryDAO.closeConnection();
        }
    }

    private void listFlowers(HttpServletRequest request, HttpServletResponse response,
            FlowerDAO flowerDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("categoryID");

        List<Flower> flowers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            flowers = flowerDAO.searchFlower(keyword.trim());
        } else if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            flowers = flowerDAO.getFlowerByCategory(Integer.parseInt(categoryParam));
        } else {
            flowers = flowerDAO.getAllFlowers();
        }

        // ===== PHÂN TRANG (trong bộ nhớ) =====
        int totalRecords = flowers.size();
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        int page = parsePositiveInt(request.getParameter("page"), 1);
        if (page > totalPages) {
            page = totalPages;
        }
        if (page < 1) {
            page = 1;
        }

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalRecords);
        List<Flower> pageFlowers = (fromIndex < totalRecords)
                ? flowers.subList(fromIndex, toIndex)
                : new ArrayList<>();

        // Base URL để nút phân trang giữ nguyên keyword/categoryID đang lọc
        StringBuilder baseUrl = new StringBuilder(request.getContextPath())
                .append("/admin/flowers?action=list");
        if (keyword != null && !keyword.trim().isEmpty()) {
            baseUrl.append("&keyword=").append(URLEncoder.encode(keyword.trim(), "UTF-8"));
        }
        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            baseUrl.append("&categoryID=").append(URLEncoder.encode(categoryParam.trim(), "UTF-8"));
        }

        request.setAttribute("flowerList", pageFlowers);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("baseUrl", baseUrl.toString());

        loadCategories(request, categoryDAO);
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response, CategoryDAO categoryDAO)
            throws ServletException, IOException {

        loadCategories(request, categoryDAO);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private void insertFlower(HttpServletRequest request, HttpServletResponse response,
            FlowerDAO flowerDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {

        try {
            Flower f = buildFlowerFromRequest(request, false, flowerDAO);
            int newId = flowerDAO.insertFlower(f);

            if (newId > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=insertSuccess");
            } else {
                request.setAttribute("error", "Insert failed. Please try again.");
                request.setAttribute("flower", f);
                loadCategories(request, categoryDAO);
                request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price, stock quantity, or category ID.");
            loadCategories(request, categoryDAO);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response,
            FlowerDAO flowerDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {

        try {
            int flowerID = Integer.parseInt(request.getParameter("id"));
            Flower f = flowerDAO.getFlowerById(flowerID);

            if (f == null) {
                response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=notFound");
                return;
            }

            request.setAttribute("flower", f);
            loadCategories(request, categoryDAO);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=invalidId");
        }
    }

    private void updateFlower(HttpServletRequest request, HttpServletResponse response,
            FlowerDAO flowerDAO, CategoryDAO categoryDAO)
            throws ServletException, IOException {

        try {
            Flower f = buildFlowerFromRequest(request, true, flowerDAO);
            boolean success = flowerDAO.updateFlower(f);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=updateSuccess");
            } else {
                request.setAttribute("error", "Update failed. Please try again.");
                request.setAttribute("flower", f);
                loadCategories(request, categoryDAO);
                request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid flower ID, price, stock quantity, or category ID.");
            loadCategories(request, categoryDAO);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
        }
    }

    private void deleteFlower(HttpServletRequest request, HttpServletResponse response, FlowerDAO flowerDAO)
            throws ServletException, IOException {

        try {
            int flowerID = Integer.parseInt(request.getParameter("id"));
            boolean success = flowerDAO.deleteFlower(flowerID);

            String msg = success ? "deleteSuccess" : "deleteFailed";
            response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=" + msg);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=invalidId");
        }
    }

    private Flower buildFlowerFromRequest(HttpServletRequest request, boolean isUpdate, FlowerDAO flowerDAO)
            throws IOException, ServletException {

        Flower f = new Flower();

        if (isUpdate) {
            f.setFlowerID(Integer.parseInt(request.getParameter("flowerID")));
        }

        f.setFlowerName(request.getParameter("flowerName"));
        f.setDescription(request.getParameter("description"));
        f.setPrice(new BigDecimal(request.getParameter("price")));
        f.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
        f.setCategoryID(Integer.parseInt(request.getParameter("categoryID")));

        String status = request.getParameter("status");
        f.setStatus("true".equalsIgnoreCase(status));

        String savedFileName = handleImageUpload(request);
        if (savedFileName != null) {
            f.setImage(savedFileName);
        } else if (isUpdate) {
            Flower existing = flowerDAO.getFlowerById(f.getFlowerID());
            f.setImage(existing != null ? existing.getImage() : null);
        }

        return f;
    }

    private String handleImageUpload(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("imageFile");
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String uniqueName = System.currentTimeMillis() + "_" + originalName;

        String uploadDir = getServletContext().getRealPath("/images/flowers");
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, uploadPath.resolve(uniqueName), StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueName;
    }

    private void loadCategories(HttpServletRequest request, CategoryDAO categoryDAO) {
        List<Category> categories = categoryDAO.getActiveCategories();
        request.setAttribute("categoryList", categories);
    }

    private int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin servlet for Flower CRUD management";
    }
}