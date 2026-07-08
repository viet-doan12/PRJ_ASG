package com.flowershop.servlet.auth;

import com.flowershop.dao.UserDAO;
import com.flowershop.model.User;
import com.flowershop.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    private static final String RESET_VIEW
            = "/WEB-INF/jsp/auth/reset-password.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        User resetUser = (User) session.getAttribute("resetUser");

        if (otpVerified == null
                || !otpVerified
                || resetUser == null) {

            session.invalidate();

            response.sendRedirect(request.getContextPath()
                    + "/forgot-password");

            return;
        }

        request.getRequestDispatcher(RESET_VIEW)
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath()
                    + "/forgot-password");
            return;
        }

        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        User resetUser = (User) session.getAttribute("resetUser");

        if (otpVerified == null
                || !otpVerified
                || resetUser == null) {

            session.invalidate();

            response.sendRedirect(request.getContextPath()
                    + "/forgot-password");

            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        newPassword = newPassword == null ? "" : newPassword.trim();
        confirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

        // ==========================
        // Validate Password
        // ==========================
        if (!ValidationUtil.isValidPassword(newPassword)) {

            request.setAttribute("error",
                    "Mật khẩu phải có ít nhất 6 ký tự, gồm chữ và số.");

            request.getRequestDispatcher(RESET_VIEW)
                    .forward(request, response);

            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            request.setAttribute("error",
                    "Xác nhận mật khẩu không khớp.");

            request.getRequestDispatcher(RESET_VIEW)
                    .forward(request, response);

            return;
        }

        // Không cho đặt lại giống mật khẩu cũ
        if (ValidationUtil.matchesPassword(newPassword,
                resetUser.getPassword())) {

            request.setAttribute("error",
                    "Mật khẩu mới phải khác mật khẩu cũ.");

            request.getRequestDispatcher(RESET_VIEW)
                    .forward(request, response);

            return;
        }

        String hashedPassword = ValidationUtil.hashPassword(newPassword);

        UserDAO dao = new UserDAO();

        boolean success = dao.updatePassword(
                resetUser.getUserID(),
                hashedPassword);

        if (!success) {

            request.setAttribute("error",
                    "Không thể cập nhật mật khẩu. Vui lòng thử lại.");

            request.getRequestDispatcher(RESET_VIEW)
                    .forward(request, response);

            return;
        }

        // ==========================
        // Thành công
        // ==========================
        session.invalidate();

        HttpSession newSession = request.getSession(true);

        newSession.setAttribute("session_message",
                "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");

        response.sendRedirect(request.getContextPath()
                + "/login");
    }
}