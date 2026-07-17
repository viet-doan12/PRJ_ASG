package com.flowershop.filter;

import com.flowershop.dao.CategoryDAO;
import com.flowershop.model.Category;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

/**
 * Nạp sẵn danh sách danh mục (categories) vào request cho MỌI request,
 * để header.jsp luôn hiển thị được thanh danh mục ở tất cả các trang,
 * không riêng gì trang chủ.
 */
@WebFilter("/*")
public class GlobalDataFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();

        // Bỏ qua các request tới file tĩnh (css/js/ảnh/font) để tránh query DB thừa
        if (!uri.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff2?|ttf)$")) {
            CategoryDAO categoryDAO = new CategoryDAO();
            try {
                List<Category> categories = categoryDAO.getActiveCategories();
                request.setAttribute("categories", categories);
            } finally {
                categoryDAO.closeConnection();
            }
        }

        chain.doFilter(req, res);
    }
}