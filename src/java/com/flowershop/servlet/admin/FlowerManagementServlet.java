package com.flowershop.servlet.admin;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.model.Category;
import com.flowershop.model.Flower;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listFlowers(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "insert":
                insertFlower(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "update":
                updateFlower(request, response);
                break;
            case "delete":
                deleteFlower(request, response);
                break;
            default:
                listFlowers(request, response);
                break;
        }
    }

    private void listFlowers(HttpServletRequest request, HttpServletResponse response)
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

        request.setAttribute("flowerList", flowers);
        loadCategories(request);
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        loadCategories(request);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private void insertFlower(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Flower f = buildFlowerFromRequest(request, false);
            int newId = flowerDAO.insertFlower(f);

            if (newId > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=insertSuccess");
            } else {
                request.setAttribute("error", "Insert failed. Please try again.");
                request.setAttribute("flower", f);
                loadCategories(request);
                request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price, stock quantity, or category ID.");
            loadCategories(request);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int flowerID = Integer.parseInt(request.getParameter("id"));
            Flower f = flowerDAO.getFlowerById(flowerID);

            if (f == null) {
                response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=notFound");
                return;
            }

            request.setAttribute("flower", f);
            loadCategories(request);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=invalidId");
        }
    }

    private void updateFlower(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Flower f = buildFlowerFromRequest(request, true);
            boolean success = flowerDAO.updateFlower(f);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/flowers?action=list&msg=updateSuccess");
            } else {
                request.setAttribute("error", "Update failed. Please try again.");
                request.setAttribute("flower", f);
                loadCategories(request);
                request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid flower ID, price, stock quantity, or category ID.");
            loadCategories(request);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
        }
    }

    private void deleteFlower(HttpServletRequest request, HttpServletResponse response)
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


    private Flower buildFlowerFromRequest(HttpServletRequest request, boolean isUpdate)
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

    private void loadCategories(HttpServletRequest request) {
        List<Category> categories = categoryDAO.getActiveCategories();
        request.setAttribute("categoryList", categories);
    }

    @Override
    public String getServletInfo() {
        return "Admin servlet for Flower CRUD management";
    }
}