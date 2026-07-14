package com.flowershop.servlet.admin;

import com.flowershop.dao.ReportDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Dashboard — tổng quan metrics (Phạm Đức Minh).
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    // Không còn field cấp lớp / init() - reportDAO được tạo mới và đóng lại
    // ngay trong từng request.

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showDashboard(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showDashboard(request, response);
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        ReportDAO reportDAO = new ReportDAO();

        int totalUsers = 0;
        int activeUsers = 0;
        int totalFlowers = 0;
        int activeFlowers = 0;
        int totalOrders = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        List<Map<String, Object>> topFlowers = new ArrayList<>();
        List<Map<String, Object>> orderStatusSummary = new ArrayList<>();
        List<Map<String, Object>> topCustomers = new ArrayList<>();

        try {
            totalUsers = reportDAO.countUsers();
            activeUsers = reportDAO.countActiveUsers();
            totalFlowers = reportDAO.countFlowers();
            activeFlowers = reportDAO.countActiveFlowers();
            totalOrders = reportDAO.countOrders();
            totalRevenue = reportDAO.getTotalRevenue();
            if (totalRevenue == null) {
                totalRevenue = BigDecimal.ZERO;
            }
            topFlowers = reportDAO.getTopSellingFlowers(5);
            if (topFlowers == null) {
                topFlowers = new ArrayList<>();
            }
            orderStatusSummary = reportDAO.getOrderCountByStatus();
            if (orderStatusSummary == null) {
                orderStatusSummary = new ArrayList<>();
            }
            topCustomers = reportDAO.getTopCustomers(5);
            if (topCustomers == null) {
                topCustomers = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.");
            totalRevenue = BigDecimal.ZERO;
            topFlowers = new ArrayList<>();
            orderStatusSummary = new ArrayList<>();
            topCustomers = new ArrayList<>();
        } finally {
            reportDAO.closeConnection();
        }

        for (Map<String, Object> row : topFlowers) {
            row.put("revenueDisplay", formatVnd(row.get("revenue")));
        }
        for (Map<String, Object> row : topCustomers) {
            row.put("totalSpentDisplay", formatVnd(row.get("totalSpent")));
        }

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("activeUsers", activeUsers);
        request.setAttribute("totalFlowers", totalFlowers);
        request.setAttribute("activeFlowers", activeFlowers);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalRevenueDisplay", formatVnd(totalRevenue));
        request.setAttribute("topFlowers", topFlowers);
        request.setAttribute("orderStatusSummary", orderStatusSummary);
        request.setAttribute("topCustomers", topCustomers);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(request, response);
    }

    private String formatVnd(Object value) {
        BigDecimal amount = BigDecimal.ZERO;
        if (value instanceof BigDecimal) {
            amount = (BigDecimal) value;
        } else if (value instanceof Number) {
            amount = BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value != null) {
            try {
                amount = new BigDecimal(value.toString());
            } catch (NumberFormatException ignored) {
                amount = BigDecimal.ZERO;
            }
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(amount.setScale(0, RoundingMode.HALF_UP));
    }

    @Override
    public String getServletInfo() {
        return "Admin Dashboard Servlet";
    }
}