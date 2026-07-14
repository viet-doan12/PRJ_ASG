package com.flowershop.servlet.auth;

import com.flowershop.dao.UserDAO;
import com.flowershop.model.User;
import com.flowershop.util.RoleConstants;
import com.flowershop.util.ValidationUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final String LOGIN_VIEW = "/WEB-INF/jsp/auth/login.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                redirectToDashboard(user, request, response);
                return;
            }
        }

        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String returnUrl = request.getParameter("returnUrl");

        if (email != null) {
            email = email.trim();
        }

        request.setAttribute("email", email);
        request.setAttribute("returnUrl", returnUrl);

        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
            forwardToLogin(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user;

        try {
            user = userDAO.getUserByEmail(email);
        } finally {
            userDAO.closeConnection();
        }

        if (user == null || !ValidationUtil.matchesPassword(password, user.getPassword())) {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            forwardToLogin(request, response);
            return;
        }

        if (!user.isStatus()) {
            request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
            forwardToLogin(request, response);
            return;
        }

        // ===============================
        // Đăng nhập thành công
        // ===============================

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30 * 60);

        if (returnUrl != null
                && !returnUrl.isBlank()
                && returnUrl.startsWith("/")
                && !returnUrl.startsWith("//")
                && !returnUrl.startsWith("/\\")) {

            response.sendRedirect(request.getContextPath() + returnUrl);
            return;
        }

        redirectToDashboard(user, request, response);
    }

    private void redirectToDashboard(User user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String roleName = user.getRoleName();

        if (RoleConstants.ADMIN.equalsIgnoreCase(roleName)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (RoleConstants.STAFF.equalsIgnoreCase(roleName)) {
            response.sendRedirect(request.getContextPath() + "/staff/orders");
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(LOGIN_VIEW);
        rd.forward(request, response);
    }
}