package com.flowershop.servlet.admin;

import com.flowershop.dao.ReportDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * Admin Reports. GET/POST
 * /admin/reports?type=revenue|topselling|inventory|customer|orderstatus Xuất
 * báo cáo doanh thu theo khoảng ngày (day/week/month/custom): ?format=csv ->
 * tải file CSV, mở trực tiếp bằng Excel ?format=print -> trang HTML để in /
 * "Lưu thành PDF" qua trình duyệt
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/admin/reports"})
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String format = request.getParameter("format"); // null | "csv" | "print"

        if ("csv".equals(format) || "print".equals(format)) {
            exportRevenue(request, response, format);
            return;
        }

        showReports(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ============================================================
    // XEM BÁO CÁO TRÊN WEB (5 loại: revenue, topselling, inventory, customer, orderstatus)
    // ============================================================
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

        ReportDAO reportDAO = new ReportDAO();

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
        } finally {
            reportDAO.closeConnection();
        }

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

    // ============================================================
    // XUẤT BÁO CÁO DOANH THU THEO KHOẢNG NGÀY (day / week / month / custom)
    // ============================================================
    private void exportRevenue(HttpServletRequest request, HttpServletResponse response, String format)
            throws IOException {

        LocalDate[] range = resolveDateRange(request);
        LocalDate start = range[0];
        LocalDate end = range[1];
        String rangeLabel = start + "_den_" + end;

        ReportDAO reportDAO = new ReportDAO();
        try {
            Date sqlStart = Date.valueOf(start);
            Date sqlEnd = Date.valueOf(end);

            List<Map<String, Object>> data = reportDAO.getRevenueByDateRange(sqlStart, sqlEnd);
            BigDecimal totalRevenue = reportDAO.getTotalRevenueByDateRange(sqlStart, sqlEnd);

            if ("csv".equals(format)) {
                exportRevenueCsv(response, data, rangeLabel, totalRevenue);
            } else {
                exportRevenuePrintableHtml(response, data, rangeLabel, totalRevenue, start, end);
            }
        } finally {
            reportDAO.closeConnection();
        }
    }

    /**
     * Xuất CSV - mở trực tiếp bằng Excel, không cần thư viện ngoài.
     */
    private void exportRevenueCsv(HttpServletResponse response,
            List<Map<String, Object>> data, String rangeLabel, BigDecimal totalRevenue) throws IOException {

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=bao-cao-doanh-thu-" + rangeLabel + ".csv");

        try (java.io.OutputStream os = response.getOutputStream()) {
            // BOM giúp Excel nhận đúng UTF-8, hiển thị đúng tiếng Việt có dấu
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);

            // Bọc cùng 1 OutputStream bằng PrintWriter, KHÔNG gọi response.getWriter() nữa
            PrintWriter writer = new PrintWriter(new java.io.OutputStreamWriter(os, java.nio.charset.StandardCharsets.UTF_8));

            writer.println("Ngay,So don hang,Doanh thu (VND)");
            for (Map<String, Object> row : data) {
                writer.println(row.get("orderDay") + "," + row.get("orderCount") + "," + row.get("revenue"));
            }
            writer.println();
            writer.println("Tong cong,," + totalRevenue);

            writer.flush(); // bắt buộc - đẩy hết dữ liệu vào OutputStream trước khi try-with-resources đóng nó
        }
    }

    /**
     * Trang HTML để in / "Lưu thành PDF" qua hộp thoại in của trình duyệt
     * (Ctrl+P).
     */
    private void exportRevenuePrintableHtml(HttpServletResponse response,
            List<Map<String, Object>> data, String rangeLabel, BigDecimal totalRevenue,
            LocalDate start, LocalDate end) throws IOException {

        response.setContentType("text/html; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>");
            out.println("<title>Bao cao doanh thu</title>");
            out.println("<style>");
            out.println("body{font-family:Arial,sans-serif;padding:24px;color:#111827;}");
            out.println("h2{text-align:center;color:#198754;margin-bottom:4px;}");
            out.println("p.sub{text-align:center;color:#6b7280;margin-top:0;}");
            out.println("table{width:100%;border-collapse:collapse;margin-top:16px;}");
            out.println("th,td{border:1px solid #ccc;padding:8px 12px;text-align:left;}");
            out.println("th{background:#f0f0f0;}");
            out.println(".text-end{text-align:right;}");
            out.println(".total-row{font-weight:bold;background:#e8f5e9;}");
            out.println(".no-print{display:block;margin-bottom:16px;}");
            out.println("@media print{.no-print{display:none;}}");
            out.println("</style></head><body>");

            out.println("<div class='no-print'>");
            out.println("<button onclick='window.print()' "
                    + "style='padding:10px 20px;background:#198754;color:#fff;border:none;border-radius:6px;cursor:pointer;font-size:14px;'>"
                    + "In / Lưu thành PDF</button></div>");

            out.println("<h2>BÁO CÁO DOANH THU</h2>");
            out.println("<p class='sub'>Từ ngày " + formatDate(start) + " đến ngày " + formatDate(end) + "</p>");

            out.println("<table>");
            out.println("<tr><th>Ngày</th><th class='text-end'>Số đơn hàng</th><th class='text-end'>Doanh thu (VNĐ)</th></tr>");

            if (data.isEmpty()) {
                out.println("<tr><td colspan='3' style='text-align:center;color:#6b7280;'>Không có dữ liệu trong khoảng thời gian này.</td></tr>");
            } else {
                for (Map<String, Object> row : data) {
                    out.println("<tr><td>" + row.get("orderDay") + "</td>"
                            + "<td class='text-end'>" + row.get("orderCount") + "</td>"
                            + "<td class='text-end'>" + formatVnd(row.get("revenue")) + "</td></tr>");
                }
            }

            out.println("<tr class='total-row'><td colspan='2'>TỔNG CỘNG</td>"
                    + "<td class='text-end'>" + formatVnd(totalRevenue) + " đ</td></tr>");
            out.println("</table></body></html>");
        }
    }

    // ============================================================
    // Chuyển "range" param thành khoảng ngày [start, end] thực tế
    // ============================================================
    private LocalDate[] resolveDateRange(HttpServletRequest request) {
        String range = request.getParameter("range");
        if (range == null || range.trim().isEmpty()) {
            range = "month";
        }

        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;

        switch (range) {
            case "day":
                start = today;
                break;
            case "week":
                start = today.with(DayOfWeek.MONDAY);
                end = today.with(DayOfWeek.SUNDAY);
                break;
            case "custom":
                String s = request.getParameter("startDate");
                String e = request.getParameter("endDate");
                DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
                start = (s != null && !s.trim().isEmpty()) ? LocalDate.parse(s.trim(), fmt) : today.withDayOfMonth(1);
                end = (e != null && !e.trim().isEmpty()) ? LocalDate.parse(e.trim(), fmt) : today;
                break;
            case "month":
            default:
                start = today.withDayOfMonth(1);
                end = today.withDayOfMonth(today.lengthOfMonth());
                break;
        }

        return new LocalDate[]{start, end};
    }

    // ============================================================
    // HÀM HỖ TRỢ
    // ============================================================
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

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String getServletInfo() {
        return "Admin Reports Servlet";
    }
}
