package com.flowershop.filter;

import com.flowershop.model.User;
import com.flowershop.util.RoleConstants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/staff/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        String requiredRole = null;

        if (path.startsWith("/admin")) {
            requiredRole = RoleConstants.ADMIN;
        } else if (path.startsWith("/staff")) {
            requiredRole = RoleConstants.STAFF;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || !currentUser.isStatus()) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roleName = currentUser.getRoleName();

        boolean allowed =
                requiredRole.equalsIgnoreCase(roleName)
                || RoleConstants.ADMIN.equalsIgnoreCase(roleName);

        if (!allowed) {
            request.setAttribute("errorMessage",
                    "Bạn không có quyền truy cập trang này.");

            request.getRequestDispatcher("/WEB-INF/jsp/error/403.jsp")
                    .forward(request, response);
            return;
        }

        chain.doFilter(req, resp);
    }
}