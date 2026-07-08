package com.flowershop.servlet.auth;

import com.flowershop.dao.UserDAO;
import com.flowershop.model.User;
import com.flowershop.util.EmailUtil;
import com.flowershop.util.OTPUtil;
import com.flowershop.util.ValidationUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger LOGGER
            = Logger.getLogger(ForgotPasswordServlet.class.getName());

    private static final String FORGOT_VIEW
            = "/WEB-INF/jsp/auth/forgot-password.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Đã đăng nhập thì không cần dùng chức năng quên mật khẩu
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        email = email == null ? "" : email.trim();

        request.setAttribute("email", email);

        // ==========================
        // Validate Email
        // ==========================
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("error", "Email không hợp lệ.");
            forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            request.setAttribute("error", "Email chưa được đăng ký.");
            forward(request, response);
            return;
        }

        if (!user.isStatus()) {
            request.setAttribute("error",
                    "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
            forward(request, response);
            return;
        }

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

            LOGGER.log(Level.SEVERE, "Cannot send OTP Email.", ex);

            request.setAttribute("error",
                    "Không thể gửi Email xác nhận. Vui lòng thử lại.");

            forward(request, response);
            return;
        }

        // ==================================================
        // Làm mới Session (chống Session Fixation)
        // ==================================================
        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        // ==================================================
        // Tạo Session mới
        // ==================================================
        HttpSession session = request.getSession(true);

        session.setMaxInactiveInterval(5 * 60);

        // Lưu dữ liệu phục vụ VerifyOTPServlet
        session.setAttribute("resetUser", user);

        session.setAttribute("otp", otp);

        session.setAttribute("otpEmail", email);

        session.setAttribute("otpType", "FORGOT_PASSWORD");

        session.setAttribute("otpExpireTime", expireTime);

        session.setAttribute("otpCreateTime",
                System.currentTimeMillis());

        session.setAttribute("otpRetry", 0);

        session.setAttribute("otpResend", 0);

        // Sang trang nhập OTP
        response.sendRedirect(
                request.getContextPath() + "/verify-otp");
    }

    private void forward(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(FORGOT_VIEW)
                .forward(request, response);
    }
}
