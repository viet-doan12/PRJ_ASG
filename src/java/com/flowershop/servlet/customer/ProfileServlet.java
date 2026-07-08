package com.flowershop.servlet.customer;

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

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private static final String PROFILE_PAGE
            = "/WEB-INF/jsp/customer/profile.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Không cache trang Profile
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");

        UserDAO dao = new UserDAO();
        User liveUser = dao.getUserById(sessionUser.getUserID());

        // Kiểm tra tài khoản còn tồn tại hay bị khóa
        if (liveUser == null || !liveUser.isStatus()) {
            session.invalidate();
            response.sendRedirect(
                    request.getContextPath()
                    + "/login?error=account_locked");
            return;
        }

        // Luôn đồng bộ User mới nhất từ Database
        session.setAttribute("user", liveUser);

        // Load Flash Message
        loadFlashMessage(session, request);

        request.getRequestDispatcher(PROFILE_PAGE)
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");

        UserDAO dao = new UserDAO();
        User liveUser = dao.getUserById(sessionUser.getUserID());

        if (liveUser == null || !liveUser.isStatus()) {
            session.invalidate();
            response.sendRedirect(
                    request.getContextPath()
                    + "/login?error=account_locked");
            return;
        }

        String action = request.getParameter("action");

        if ("updateInfo".equals(action)) {

            updateProfile(
                    request,
                    session,
                    liveUser,
                    dao);

        } else if ("changePassword".equals(action)) {

            changePassword(
                    request,
                    session,
                    liveUser,
                    dao);
        }

        // PRG Pattern
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    /**
     * ========================================================== UPDATE PROFILE
     * ==========================================================
     */
    private void updateProfile(HttpServletRequest request,
            HttpSession session,
            User user,
            UserDAO dao) {

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        fullName = fullName == null ? "" : fullName.trim();
        phone = phone == null ? "" : phone.trim();
        address = address == null ? "" : address.trim();

        if (!ValidationUtil.isValidFullName(fullName)) {
            session.setAttribute("session_error",
                    "Họ tên phải từ 2 đến 100 ký tự.");
            saveForm(session, fullName, phone, address);
            return;
        }

        if (!phone.isEmpty()
                && !ValidationUtil.isValidPhone(phone)) {

            session.setAttribute("session_error",
                    "Số điện thoại không hợp lệ.");

            saveForm(session, fullName, phone, address);
            return;
        }

        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);

        if (dao.updateProfile(user)) {

            // Đọc lại dữ liệu mới nhất từ Database
            User updatedUser = dao.getUserById(user.getUserID());

            if (updatedUser != null) {
                session.setAttribute("user", updatedUser);
            }

            session.setAttribute(
                    "session_message",
                    "Cập nhật thông tin thành công.");

        } else {

            session.setAttribute(
                    "session_error",
                    "Không thể cập nhật thông tin.");

            saveForm(session, fullName, phone, address);
        }
    }

    /**
     * ========================================================== CHANGE
     * PASSWORD ==========================================================
     */
    private void changePassword(HttpServletRequest request,
            HttpSession session,
            User user,
            UserDAO dao) {

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        oldPassword = oldPassword == null ? "" : oldPassword.trim();
        newPassword = newPassword == null ? "" : newPassword.trim();
        confirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

        if (ValidationUtil.isEmpty(oldPassword)) {

            session.setAttribute(
                    "session_error",
                    "Vui lòng nhập mật khẩu hiện tại.");

            return;
        }

        if (!ValidationUtil.matchesPassword(
                oldPassword,
                user.getPassword())) {

            session.setAttribute(
                    "session_error",
                    "Mật khẩu hiện tại không đúng.");

            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {

            session.setAttribute(
                    "session_error",
                    "Mật khẩu mới phải có ít nhất 6 ký tự và gồm chữ, số.");

            return;
        }

        if (oldPassword.equals(newPassword)) {

            session.setAttribute(
                    "session_error",
                    "Mật khẩu mới phải khác mật khẩu cũ.");

            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            session.setAttribute(
                    "session_error",
                    "Xác nhận mật khẩu không khớp.");

            return;
        }

        String hashPassword
                = ValidationUtil.hashPassword(newPassword);

        if (dao.updatePassword(
                user.getUserID(),
                hashPassword)) {

            // Đồng bộ Password mới
            User updatedUser
                    = dao.getUserById(user.getUserID());

            if (updatedUser != null) {
                session.setAttribute("user", updatedUser);
            }

            session.setAttribute(
                    "session_message",
                    "Đổi mật khẩu thành công.");

        } else {

            session.setAttribute(
                    "session_error",
                    "Không thể đổi mật khẩu.");
        }
    }

    /**
     * ========================================================== LOAD FLASH
     * MESSAGE ==========================================================
     */
    private void loadFlashMessage(HttpSession session,
            HttpServletRequest request) {

        Object message = session.getAttribute("session_message");
        Object error = session.getAttribute("session_error");

        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("session_message");
        }

        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("session_error");

            // Khôi phục dữ liệu người dùng vừa nhập khi validate lỗi
            request.setAttribute("tmpFullName",
                    session.getAttribute("edit_fullName"));
            request.setAttribute("tmpPhone",
                    session.getAttribute("edit_phone"));
            request.setAttribute("tmpAddress",
                    session.getAttribute("edit_address"));

            session.removeAttribute("edit_fullName");
            session.removeAttribute("edit_phone");
            session.removeAttribute("edit_address");
        }
    }

    /**
     * ========================================================== SAVE FORM
     * ========================================================== Lưu dữ liệu
     * form vào Session để hiển thị lại sau khi redirect (PRG Pattern).
     */
    private void saveForm(HttpSession session,
            String fullName,
            String phone,
            String address) {

        session.setAttribute("edit_fullName", fullName);
        session.setAttribute("edit_phone", phone);
        session.setAttribute("edit_address", address);
    }

}
