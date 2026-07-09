package com.flowershop.servlet.auth;

import com.flowershop.model.User;
import com.flowershop.util.EmailUtil;
import com.flowershop.util.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ResendOTPServlet", urlPatterns = {"/resend-otp"})
public class ResendOTPServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        User tempUser = (User) session.getAttribute("tempUser");

        if (tempUser == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        // Sinh OTP mới
        String otp = OTPUtil.generateOTP();

        long expireTime = OTPUtil.getExpireTime();

        try {

            EmailUtil.sendOTP(tempUser.getEmail(), otp);

        } catch (MessagingException ex) {

            request.setAttribute("error",
                    "Không thể gửi Email. Vui lòng thử lại.");

            request.getRequestDispatcher("/WEB-INF/jsp/auth/verify-otp.jsp")
                    .forward(request, response);

            return;
        }

        // Lưu OTP mới
        session.setAttribute("otp", otp);

        session.setAttribute("otpExpireTime", expireTime);

        // reset số lần nhập sai
        session.setAttribute("otpRetry", 0);

        request.setAttribute("message",
                "Đã gửi OTP mới tới Email của bạn.");

        request.getRequestDispatcher("/WEB-INF/jsp/auth/verify-otp.jsp")
                .forward(request, response);

    }

}