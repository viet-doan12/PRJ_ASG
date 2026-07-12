package com.flowershop.servlet.admin;

import com.flowershop.dao.ReportDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Reports — 5 report types (Phạm Đức Minh).
 * GET/POST /admin/reports?type=revenue|topselling|inventory|customer|orderstatus
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/admin/reports"})
public class ReportServlet extends HttpServlet {

    private ReportDAO reportDAO;

    @Override
    public void init() throws ServletException {
        reportDAO = new ReportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showReports(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showReports(request, response);
    }

    private void showReports(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "revenue";
        }
        type = type.trim().toLowerCase(Locale.ROOT);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int year = parsePositiveInt(request.getParameter("year"), currentYear);
        int limit = parsePositiveInt(request.getParameter("limit"), 10);
        int threshold = parsePositiveInt(request.getParameter("threshold"), 10);

        List<Map<String, Object>> reportData = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal yearRevenue = BigDecimal.ZERO;
        int yearOrderCount = 0;

        try {
            totalRevenue = reportDAO.getTotalRevenue();
            if (totalRevenue == null) {
                totalRevenue = BigDecimal.ZERO;
            }

            switch (type) {
                case "topselling":
                    reportData = reportDAO.getTopSellingFlowers(limit);
                    break;
                case "inventory":
                    reportData = reportDAO.getInventoryReport(threshold);
                    break;
                case "customer":
                    reportData = reportDAO.getTopCustomers(limit);
                    break;
                case "orderstatus":
                    reportData = reportDAO.getOrderCountByStatus();
                    break;
                case "revenue":
                default:
                    type = "revenue";
                    reportData = reportDAO.getRevenueByMonth(year);
                    break;
            }

            if (reportData == null) {
                reportData = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu báo cáo. Vui lòng thử lại sau.");
            reportData = new ArrayList<>();
            totalRevenue = BigDecimal.ZERO;
        }

        // Pre-format money fields for JSP
        if ("revenue".equals(type)) {
            for (Map<String, Object> row : reportData) {
                Object rev = row.get("revenue");
                row.put("revenueDisplay", formatVnd(rev));
                yearRevenue = yearRevenue.add(toBigDecimal(rev));
                Object oc = row.get("orderCount");
                if (oc instanceof Number) {
                    yearOrderCount += ((Number) oc).intValue();
                }
            }
        } else if ("topselling".equals(type)) {
            for (Map<String, Object> row : reportData) {
                row.put("revenueDisplay", formatVnd(row.get("revenue")));
            }
        } else if ("customer".equals(type)) {
            for (Map<String, Object> row : reportData) {
                row.put("totalSpentDisplay", formatVnd(row.get("totalSpent")));
            }
        }

        request.setAttribute("reportType", type);
        request.setAttribute("reportData", reportData);
        request.setAttribute("year", year);
        request.setAttribute("limit", limit);
        request.setAttribute("threshold", threshold);
        request.setAttribute("currentYear", currentYear);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalRevenueDisplay", formatVnd(totalRevenue));
        request.setAttribute("yearRevenueDisplay", formatVnd(yearRevenue));
        request.setAttribute("yearOrderCount", yearOrderCount);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/reports.jsp").forward(request, response);
    }

    private int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        if (value != null) {
            try {
                return new BigDecimal(value.toString());
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        return BigDecimal.ZERO;
    }

    private String formatVnd(Object value) {
        BigDecimal amount = toBigDecimal(value);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(amount.setScale(0, RoundingMode.HALF_UP));
    }

    @Override
    public String getServletInfo() {
        return "Admin Reports Servlet";
    }
}
