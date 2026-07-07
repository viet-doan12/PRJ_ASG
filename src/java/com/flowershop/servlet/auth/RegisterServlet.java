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

        // Đã đăng nhập thành viên rồi thì không cho phép quay lại trang đăng ký nữa
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // (Đã bỏ dòng setCharacterEncoding thừa do EncodingFilter đã lo)
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Trim dữ liệu an toàn
        fullName = (fullName == null) ? "" : fullName.trim();
        email = (email == null) ? "" : email.trim();
        phone = (phone == null) ? "" : phone.trim();
        address = (address == null) ? "" : address.trim();

        // Đồng bộ ném trả dữ liệu cũ về form để người dùng không phải gõ lại nếu lỗi
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);

        // ===========================
        // Nghiệp vụ Validate dữ liệu
        // ===========================
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
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
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
            request.setAttribute("error", "Email đã tồn tại hệ thống.");
            forward(request, response);
            return;
        }

        // Lấy thông tin nhóm quyền khách hàng mặc định
        RoleDAO roleDAO = new RoleDAO();
        Role customerRole = roleDAO.getRoleByName(RoleConstants.CUSTOMER);

        if (customerRole == null) {
            request.setAttribute("error", "Lỗi hệ thống: Nhóm quyền CUSTOMER chưa được khởi tạo.");
            forward(request, response);
            return;
        }

        // Khởi tạo đối tượng User tạm thời
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setPassword(ValidationUtil.hashPassword(password)); // Mã hóa mật khẩu
        newUser.setRoleID(customerRole.getRoleID());
        newUser.setStatus(true);

        // Sinh mã OTP và thời gian hết hạn
        String otp = OTPUtil.generateOTP();
        long expireTime = OTPUtil.getExpireTime();

        // Tiến hành gửi Mail kích hoạt
        try {
            EmailUtil.sendOTP(email, otp);
        } catch (MessagingException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể gửi Email xác nhận. Vui lòng kiểm tra lại cấu hình mail.");
            forward(request, response);
            return;
        }

        // ==========================================================
// Làm mới Session để tránh dùng lại OTP cũ
// ==========================================================
        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

// ==========================================================
// Tạo Session mới
// ==========================================================
        HttpSession session = request.getSession(true);

        session.setAttribute("tempUser", newUser);
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpireTime", expireTime);
        session.setAttribute("otpRetry", 0);

        // Chuyển hướng sang trang xác thực mã
        response.sendRedirect(request.getContextPath() + "/verify-otp");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_VIEW).forward(request, response);
    }
}
