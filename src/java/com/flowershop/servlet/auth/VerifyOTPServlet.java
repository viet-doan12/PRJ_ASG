package com.flowershop.servlet.auth;

import com.flowershop.dao.UserDAO;
import com.flowershop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "VerifyOTPServlet", urlPatterns = {"/verify-otp"})
public class VerifyOTPServlet extends HttpServlet {

    private static final String VERIFY_VIEW = "/WEB-INF/jsp/auth/verify-otp.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("tempUser") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        request.getRequestDispatcher(VERIFY_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        User tempUser = (User) session.getAttribute("tempUser");
        String sessionOTP = (String) session.getAttribute("otp");
        Long expireTime = (Long) session.getAttribute("otpExpireTime");
        Integer retry = (Integer) session.getAttribute("otpRetry");

        if (tempUser == null || sessionOTP == null || expireTime == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (retry == null) {
            retry = 0;
        }

        String inputOTP = request.getParameter("otp");

        // OTP hết hạn
        if (System.currentTimeMillis() > expireTime) {

            session.invalidate();

            request.setAttribute("error",
                    "Mã OTP đã hết hạn. Vui lòng đăng ký lại.");

            request.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp")
                    .forward(request, response);

            return;
        }

        // OTP sai
        if (!sessionOTP.equals(inputOTP)) {

            retry++;

            session.setAttribute("otpRetry", retry);

            if (retry >= 5) {

                session.invalidate();

                request.setAttribute("error",
                        "Bạn đã nhập sai OTP quá nhiều lần.");

                request.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp")
                        .forward(request, response);

                return;
            }

            request.setAttribute("error",
                    "OTP không đúng. Bạn còn "
                    + (5 - retry)
                    + " lần thử.");

            request.getRequestDispatcher(VERIFY_VIEW)
                    .forward(request, response);

            return;
        }

        // ==========================
        // OTP ĐÚNG
        // ==========================

        UserDAO dao = new UserDAO();

        int newId = dao.insertUser(tempUser);

        if (newId <= 0) {

            request.setAttribute("error",
                    "Không thể tạo tài khoản.");

            request.getRequestDispatcher(VERIFY_VIEW)
                    .forward(request, response);

            return;
        }

        session.invalidate();

        response.sendRedirect(
                request.getContextPath()
                        + "/login?verified=1");
    }
}