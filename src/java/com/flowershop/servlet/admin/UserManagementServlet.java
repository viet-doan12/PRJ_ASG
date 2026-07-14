package com.flowershop.servlet.admin;

import com.flowershop.dao.RoleDAO;
import com.flowershop.dao.UserDAO;
import com.flowershop.model.Role;
import com.flowershop.model.User;
import com.flowershop.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UserServlet", urlPatterns = {"/admin/users"})
public class UserManagementServlet extends HttpServlet {

    // Không còn field cấp lớp / init() - userDAO, roleDAO được tạo mới
    // và đóng lại ngay trong từng request.

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        UserDAO userDAO = new UserDAO();
        RoleDAO roleDAO = new RoleDAO();

        try {
            switch (action) {
                case "add":
                    showAddForm(request, response, roleDAO);
                    break;
                case "edit":
                    showEditForm(request, response, userDAO, roleDAO);
                    break;
                case "lock":
                    lockUser(request, response, userDAO);
                    break;
                case "unlock":
                    unlockUser(request, response, userDAO);
                    break;
                case "delete":
                    lockUser(request, response, userDAO);
                    break;
                case "list":
                default:
                    listUsers(request, response, userDAO, roleDAO);
                    break;
            }
        } finally {
            userDAO.closeConnection();
            roleDAO.closeConnection();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        UserDAO userDAO = new UserDAO();
        RoleDAO roleDAO = new RoleDAO();

        try {
            switch (action) {
                case "insert":
                    insertUser(request, response, userDAO, roleDAO);
                    break;
                case "update":
                    updateUser(request, response, userDAO, roleDAO);
                    break;
                default:
                    listUsers(request, response, userDAO, roleDAO);
                    break;
            }
        } finally {
            userDAO.closeConnection();
            roleDAO.closeConnection();
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response,
            UserDAO userDAO, RoleDAO roleDAO)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        if (search != null) {
            search = search.trim();
            if (search.isEmpty()) {
                search = null;
            }
        }

        Integer roleId = parseIntegerOrNull(request.getParameter("roleId"));
        Boolean status = parseBooleanOrNull(request.getParameter("status"));

        int page = 1;
        int pageSize = 10;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int offset = (page - 1) * pageSize;
        List<User> userList = userDAO.getUsersPaged(offset, pageSize, search, roleId, status);
        int totalRecords = userDAO.countUsers(search, roleId, status);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        List<Role> roleList = roleDAO.getAllRoles();

        request.setAttribute("userList", userList);
        request.setAttribute("roleList", roleList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("roleId", roleId);
        request.setAttribute("status", status == null ? "" : String.valueOf(status));

        request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response, RoleDAO roleDAO)
            throws ServletException, IOException {
        request.setAttribute("formTitle", "Add New User");
        request.setAttribute("action", "insert");
        request.setAttribute("roleList", roleDAO.getAllRoles());
        if (request.getAttribute("user") == null) {
            User blank = new User();
            blank.setStatus(true);
            request.setAttribute("user", blank);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/user-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response,
            UserDAO userDAO, RoleDAO roleDAO)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();

        if (idStr == null || idStr.isEmpty()) {
            setToast(session, "danger", "Invalid User ID!");
            response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            User user = userDAO.getUserById(id);
            if (user == null) {
                setToast(session, "danger", "User not found!");
                response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
                return;
            }
            user.setPassword(null);
            request.setAttribute("user", user);
            request.setAttribute("formTitle", "Edit User");
            request.setAttribute("action", "update");
            request.setAttribute("roleList", roleDAO.getAllRoles());
            request.getRequestDispatcher("/WEB-INF/jsp/admin/user-form.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            setToast(session, "danger", "Invalid User ID format!");
            response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
        }
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response,
            UserDAO userDAO, RoleDAO roleDAO)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String roleIdStr = request.getParameter("roleID");
        String statusStr = request.getParameter("status");

        boolean status = "true".equals(statusStr) || "on".equals(statusStr);

        User formUser = new User();
        formUser.setFullName(fullName != null ? fullName.trim() : "");
        formUser.setEmail(email != null ? email.trim() : "");
        formUser.setPhone(phone != null ? phone.trim() : "");
        formUser.setAddress(address != null ? address.trim() : "");
        formUser.setStatus(status);

        Integer roleId = parseIntegerOrNull(roleIdStr);
        if (roleId != null) {
            formUser.setRoleID(roleId);
        }

        String error = validateUserForm(formUser, password, true, null, userDAO, roleDAO);
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("user", formUser);
            showAddForm(request, response, roleDAO);
            return;
        }

        formUser.setPassword(ValidationUtil.hashPassword(password));
        int newId = userDAO.insertUser(formUser);
        if (newId > 0) {
            setToast(session, "success", "User created successfully!");
            response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
        } else {
            request.setAttribute("error", "Failed to create user! Email may already exist.");
            request.setAttribute("user", formUser);
            showAddForm(request, response, roleDAO);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response,
            UserDAO userDAO, RoleDAO roleDAO)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String idStr = request.getParameter("userID");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String roleIdStr = request.getParameter("roleID");
        String statusStr = request.getParameter("status");

        boolean status = "true".equals(statusStr) || "on".equals(statusStr);

        try {
            int userId = Integer.parseInt(idStr);

            User formUser = new User();
            formUser.setUserID(userId);
            formUser.setFullName(fullName != null ? fullName.trim() : "");
            formUser.setEmail(email != null ? email.trim() : "");
            formUser.setPhone(phone != null ? phone.trim() : "");
            formUser.setAddress(address != null ? address.trim() : "");
            formUser.setStatus(status);

            Integer roleId = parseIntegerOrNull(roleIdStr);
            if (roleId != null) {
                formUser.setRoleID(roleId);
            }

            String error = validateUserForm(formUser, password, false, userId, userDAO, roleDAO);
            if (error != null) {
                request.setAttribute("error", error);
                request.setAttribute("user", formUser);
                request.setAttribute("formTitle", "Edit User");
                request.setAttribute("action", "update");
                request.setAttribute("roleList", roleDAO.getAllRoles());
                request.getRequestDispatcher("/WEB-INF/jsp/admin/user-form.jsp").forward(request, response);
                return;
            }

            boolean success = userDAO.updateUser(formUser);
            if (success) {
                if (password != null && !password.trim().isEmpty()) {
                    userDAO.updatePassword(userId, ValidationUtil.hashPassword(password.trim()));
                }
                setToast(session, "success", "User updated successfully!");
                response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
            } else {
                request.setAttribute("error", "Failed to update user!");
                request.setAttribute("user", formUser);
                request.setAttribute("formTitle", "Edit User");
                request.setAttribute("action", "update");
                request.setAttribute("roleList", roleDAO.getAllRoles());
                request.getRequestDispatcher("/WEB-INF/jsp/admin/user-form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            setToast(session, "danger", "Invalid User ID!");
            response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
        }
    }

    private void lockUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws IOException {
        HttpSession session = request.getSession();
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            setToast(session, "danger", "User ID is required!");
            response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean success = userDAO.lockUser(id);
            if (success) {
                setToast(session, "success", "User locked successfully!");
            } else {
                setToast(session, "danger", "Failed to lock user!");
            }
        } catch (NumberFormatException e) {
            setToast(session, "danger", "Invalid User ID format!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
    }

    private void unlockUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws IOException {
        HttpSession session = request.getSession();
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            setToast(session, "danger", "User ID is required!");
            response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean success = userDAO.unlockUser(id);
            if (success) {
                setToast(session, "success", "User unlocked successfully!");
            } else {
                setToast(session, "danger", "Failed to unlock user!");
            }
        } catch (NumberFormatException e) {
            setToast(session, "danger", "Invalid User ID format!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
    }

    private String validateUserForm(User user, String password, boolean passwordRequired,
            Integer currentUserId, UserDAO userDAO, RoleDAO roleDAO) {
        if (!ValidationUtil.isValidFullName(user.getFullName())) {
            return "Full name is required (2-100 characters).";
        }
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            return "Please provide a valid email address.";
        }

        if (passwordRequired) {
            if (!ValidationUtil.isValidPassword(password)) {
                return "Password must be at least 6 characters and contain both letters and numbers.";
            }
            if (userDAO.isEmailExists(user.getEmail())) {
                return "Email already exists!";
            }
        } else {
            if (password != null && !password.trim().isEmpty()
                    && !ValidationUtil.isValidPassword(password.trim())) {
                return "Password must be at least 6 characters and contain both letters and numbers.";
            }
            User existingByEmail = userDAO.getUserByEmail(user.getEmail());
            if (existingByEmail != null && currentUserId != null
                    && existingByEmail.getUserID() != currentUserId.intValue()) {
                return "Email already exists for another user!";
            }
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()
                && !ValidationUtil.isValidPhone(user.getPhone())) {
            return "Phone must be a valid Vietnamese number (10 digits, starts with 0).";
        }

        if (user.getRoleID() <= 0 || roleDAO.getRoleById(user.getRoleID()) == null) {
            return "Please select a valid role.";
        }

        return null;
    }

    private Integer parseIntegerOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBooleanOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        if ("true".equalsIgnoreCase(value.trim())) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(value.trim())) {
            return Boolean.FALSE;
        }
        return null;
    }

    private void setToast(HttpSession session, String type, String message) {
        session.setAttribute("toastType", type);
        session.setAttribute("toastMessage", message);
    }
}