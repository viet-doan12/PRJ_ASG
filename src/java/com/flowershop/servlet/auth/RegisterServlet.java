package com.flowershop.servlet.auth;

import com.flowershop.dao.RoleDAO;
import com.flowershop.dao.UserDAO;
import com.flowershop.model.Role;
import com.flowershop.model.User;
import com.flowershop.util.EmailUtil;
import com.flowershop.util.OTPUtil;
import com.flowershop.util.RoleConstants;
import com.flowershop.util.ValidationUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final String REGISTER_VIEW = "/WEB-INF/jsp/auth/register.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Nếu đã đăng nhập thì không cho đăng ký nữa (Đã giữ nguyên định hướng /home của bạn)
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Trim dữ liệu
        fullName = fullName == null ? "" : fullName.trim();
        email = email == null ? "" : email.trim();
        phone = phone == null ? "" : phone.trim();
        address = address == null ? "" : address.trim();

        // Giữ lại dữ liệu khi có lỗi
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);

        // ==========================
        // Validate
        // ==========================
        if (!ValidationUtil.isValidFullName(fullName)) {
            request.setAttribute("error", "Họ tên phải từ 2 đến 100 ký tự.");
            forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("error", "Email không hợp lệ.");
            forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            request.setAttribute("error", "Số điện thoại không hợp lệ.");
            forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự, gồm chữ và số.");
            forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.isEmailExists(email)) {
            request.setAttribute("error", "Email đã được đăng ký.");
            forward(request, response);
            return;
        }

        // ==========================
        // Lấy Role CUSTOMER
        // ==========================
        RoleDAO roleDAO = new RoleDAO();
        Role customerRole = roleDAO.getRoleByName(RoleConstants.CUSTOMER);

        if (customerRole == null) {
            request.setAttribute("error", "Không tìm thấy Role CUSTOMER.");
            forward(request, response);
            return;
        }

        // ==========================
        // Tạo User tạm
        // ==========================
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setPassword(ValidationUtil.hashPassword(password)); // Hash Password
        newUser.setRoleID(customerRole.getRoleID());
        newUser.setStatus(true);

        // ==========================
        // Sinh OTP
        // ==========================
        String otp = OTPUtil.generateOTP();
        long expireTime = OTPUtil.getExpireTime();

        // ==========================
        // Gửi Email
        // ==========================
        try {
            EmailUtil.sendOTP(email, otp);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Không thể gửi Email xác nhận. Vui lòng thử lại sau.");
            forward(request, response);
            return;
        }

        // ==========================================================
        // XÓA SESSION CŨ: Triệt tiêu hoàn toàn rủi ro Session Fixation
        // ==========================================================
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // ==========================================================
        // Khởi tạo một Session hoàn toàn mới tinh sạch sẽ
        // ==========================================================
        HttpSession session = request.getSession(true);

        // Session OTP tồn tại tối đa 5 phút
        session.setMaxInactiveInterval(5 * 60);

        session.setAttribute("tempUser", newUser);
        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", email);
        session.setAttribute("otpType", "REGISTER");
        session.setAttribute("otpExpireTime", expireTime);
        session.setAttribute("otpCreateTime", System.currentTimeMillis());
        session.setAttribute("otpRetry", 0);
        session.setAttribute("otpResend", 0);

        // ==========================
        // Sang trang Verify OTP
        // ==========================
        response.sendRedirect(request.getContextPath() + "/verify-otp");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_VIEW).forward(request, response);
    }
}