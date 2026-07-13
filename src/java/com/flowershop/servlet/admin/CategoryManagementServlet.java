package com.flowershop.servlet.admin;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.model.Category;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/admin/categories"})
public class CategoryManagementServlet extends HttpServlet {

    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
    }

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String action = request.getParameter("action");
    if (action == null) {
        action = "list";
    }

    try {
        switch (action) {
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteCategory(request, response);
                break;
            case "list":
            default:
                listCategories(request, response);
                break;
        }
    } finally {
        categoryDAO.closeConnection();
    }
}

@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String action = request.getParameter("action");
    if (action == null) {
        action = "list";
    }

    try {
        switch (action) {
            case "insert":
                insertCategory(request, response);
                break;
            case "update":
                updateCategory(request, response);
                break;
            default:
                listCategories(request, response);
                break;
        }
    } finally {
        categoryDAO.closeConnection();
    }
}

    private void listCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String pageStr = request.getParameter("page");
        
        int page = 1;
        int pageSize = 5; // Số phần tử trên mỗi trang
        
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int offset = (page - 1) * pageSize;
        List<Category> categoryList;
        int totalRecords;
        
        if (search != null && !search.trim().isEmpty()) {
            search = search.trim();
            categoryList = categoryDAO.searchCategoriesByPage(search, offset, pageSize);
            totalRecords = categoryDAO.getTotalSearchCategories(search);
        } else {
            categoryList = categoryDAO.getCategoriesByPage(offset, pageSize);
            totalRecords = categoryDAO.getTotalCategories();
        }
        
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }
        
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);
        request.setAttribute("totalRecords", totalRecords);
        
        // Chuyển tiếp tới trang categories.jsp trong WEB-INF
        request.getRequestDispatcher("/WEB-INF/jsp/admin/categories.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("formTitle", "Add New Category");
        request.setAttribute("action", "insert");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/category-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();
        
        if (idStr == null || idStr.isEmpty()) {
            session.setAttribute("toastType", "danger");
            session.setAttribute("toastMessage", "Invalid Category ID!");
            response.sendRedirect(request.getContextPath() + "/admin/categories");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Category category = categoryDAO.getCategoryById(id);
            if (category == null) {
                session.setAttribute("toastType", "danger");
                session.setAttribute("toastMessage", "Category not found!");
                response.sendRedirect(request.getContextPath() + "/admin/categories");
                return;
            }
            request.setAttribute("category", category);
            request.setAttribute("formTitle", "Edit Category");
            request.setAttribute("action", "update");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/category-form.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            session.setAttribute("toastType", "danger");
            session.setAttribute("toastMessage", "Invalid Category ID format!");
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }

    private void insertCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");
        String statusStr = request.getParameter("status");
        
        boolean status = "true".equals(statusStr) || "on".equals(statusStr);
        HttpSession session = request.getSession();

        if (categoryName == null || categoryName.trim().isEmpty()) {
            request.setAttribute("error", "Category name cannot be empty!");
            request.setAttribute("category", new Category(0, categoryName, description, status));
            showAddForm(request, response);
            return;
        }

        Category category = new Category();
        category.setCategoryName(categoryName.trim());
        category.setDescription(description != null ? description.trim() : "");
        category.setStatus(status);

        boolean success = categoryDAO.insertCategory(category);
        if (success) {
            session.setAttribute("toastType", "success");
            session.setAttribute("toastMessage", "Category created successfully!");
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        } else {
            request.setAttribute("error", "Failed to create category! (Name might already exist)");
            request.setAttribute("category", category);
            showAddForm(request, response);
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("categoryID");
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");
        String statusStr = request.getParameter("status");

        boolean status = "true".equals(statusStr) || "on".equals(statusStr);
        HttpSession session = request.getSession();

        try {
            int id = Integer.parseInt(idStr);
            
            if (categoryName == null || categoryName.trim().isEmpty()) {
                request.setAttribute("error", "Category name cannot be empty!");
                Category category = new Category(id, categoryName, description, status);
                request.setAttribute("category", category);
                request.setAttribute("formTitle", "Edit Category");
                request.setAttribute("action", "update");
                request.getRequestDispatcher("/WEB-INF/jsp/admin/category-form.jsp").forward(request, response);
                return;
            }

            Category category = new Category(id, categoryName.trim(), description != null ? description.trim() : "", status);
            boolean success = categoryDAO.updateCategory(category);
            
            if (success) {
                session.setAttribute("toastType", "success");
                session.setAttribute("toastMessage", "Category updated successfully!");
                response.sendRedirect(request.getContextPath() + "/admin/categories");
            } else {
                request.setAttribute("error", "Failed to update category! (Name might already exist)");
                request.setAttribute("category", category);
                request.setAttribute("formTitle", "Edit Category");
                request.setAttribute("action", "update");
                request.getRequestDispatcher("/WEB-INF/jsp/admin/category-form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("toastType", "danger");
            session.setAttribute("toastMessage", "Invalid Category ID!");
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = categoryDAO.deleteCategory(id);
                if (success) {
                    session.setAttribute("toastType", "success");
                    session.setAttribute("toastMessage", "Category deleted successfully!");
                } else {
                    session.setAttribute("toastType", "danger");
                    session.setAttribute("toastMessage", "Cannot delete category! It may contain active products.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("toastType", "danger");
                session.setAttribute("toastMessage", "Invalid Category ID format!");
            }
        } else {
            session.setAttribute("toastType", "danger");
            session.setAttribute("toastMessage", "Category ID is required for deletion!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
}
