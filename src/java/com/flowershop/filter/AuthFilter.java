package com.flowershop.filter;

import com.flowershop.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // Những URL bắt buộc phải đăng nhập
    private static final String[] PROTECTED_PREFIXES = {
        "/admin",
        "/staff",
        "/checkout",
        "/orders",
        "/profile"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());

        // ==========================================================
        // Bỏ qua tài nguyên tĩnh
        // ==========================================================
        if (path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/fonts/")
                || path.startsWith("/assets/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".ico")
                || path.endsWith(".svg")) {

            chain.doFilter(req, resp);
            return;
        }

        // ==========================================================
        // Không yêu cầu đăng nhập
        // ==========================================================
        if (!requiresLogin(path)) {
            chain.doFilter(req, resp);
            return;
        }

        // ==========================================================
        // Kiểm tra Session
        // ==========================================================
        HttpSession session = request.getSession(false);
        User currentUser = null;

        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }

        // ==========================================================
        // Chưa đăng nhập hoặc tài khoản bị khóa
        // ==========================================================
        if (currentUser == null || !currentUser.isStatus()) {

            if (session != null) {
                session.invalidate();
            }

            String returnUrl = path;

            if (request.getQueryString() != null) {
                returnUrl += "?" + request.getQueryString();
            }

            response.sendRedirect(
                    contextPath
                    + "/login?returnUrl="
                    + URLEncoder.encode(returnUrl, StandardCharsets.UTF_8));

            return;
        }

        // ==========================================================
        // Đã đăng nhập
        // ==========================================================
        chain.doFilter(req, resp);
    }

    /**
     * Kiểm tra URL có yêu cầu đăng nhập hay không (Đã sửa lỗi chặn nhầm đường dẫn trùng tên)
     */
    private boolean requiresLogin(String path) {
        for (String prefix : PROTECTED_PREFIXES) {
            // Khớp chính xác (ví dụ: /orders) HOẶC là đường dẫn con trực thuộc (ví dụ: /orders/detail)
            if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                return true;
            }
        }
        return false;
    }
}