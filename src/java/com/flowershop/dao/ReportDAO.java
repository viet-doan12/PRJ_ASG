package com.flowershop.dao;

import com.flowershop.util.DBContext;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO thống kê / báo cáo cho Dashboard + Report (Phạm Đức Minh).
 * Dùng PreparedStatement, extends DBContext theo chuẩn nhóm.
 */
public class ReportDAO extends DBContext {

    // ===================== DASHBOARD COUNTS =====================

    public int countUsers() {
        return count("SELECT COUNT(*) FROM Users");
    }

    public int countActiveUsers() {
        return count("SELECT COUNT(*) FROM Users WHERE Status = 1");
    }

    public int countFlowers() {
        return count("SELECT COUNT(*) FROM Flowers");
    }

    public int countActiveFlowers() {
        return count("SELECT COUNT(*) FROM Flowers WHERE Status = 1");
    }

    public int countOrders() {
        return count("SELECT COUNT(*) FROM Orders");
    }

    /**
     * Tổng doanh thu đơn Completed.
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(TotalAmount) FROM Orders WHERE Status = 'Completed'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal(1);
                return val != null ? val : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getRevenueByDateRange(Date from, Date to) {
        String sql = "SELECT SUM(TotalAmount) FROM Orders "
                + "WHERE Status = 'Completed' AND OrderDate >= ? AND OrderDate < DATEADD(day, 1, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal val = rs.getBigDecimal(1);
                    return val != null ? val : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // ===================== 1. REVENUE BY MONTH =====================

    /**
     * Doanh thu theo tháng trong một năm (Completed).
     * Mỗi phần tử: year, month, revenue, orderCount
     */
    public List<Map<String, Object>> getRevenueByMonth(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT YEAR(OrderDate) AS Y, MONTH(OrderDate) AS M, "
                + "SUM(TotalAmount) AS Revenue, COUNT(*) AS OrderCount "
                + "FROM Orders WHERE Status = 'Completed' AND YEAR(OrderDate) = ? "
                + "GROUP BY YEAR(OrderDate), MONTH(OrderDate) "
                + "ORDER BY M";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("year", rs.getInt("Y"));
                    row.put("month", rs.getInt("M"));
                    BigDecimal rev = rs.getBigDecimal("Revenue");
                    row.put("revenue", rev != null ? rev : BigDecimal.ZERO);
                    row.put("orderCount", rs.getInt("OrderCount"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================== 2. TOP SELLING =====================

    /**
     * Top hoa bán chạy (đơn Completed).
     * flowerId, flowerName, totalSold, revenue, stockQuantity
     */
    public List<Map<String, Object>> getTopSellingFlowers(int topN) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TOP (?) F.FlowerID, F.FlowerName, F.StockQuantity, "
                + "SUM(OD.Quantity) AS TotalSold, "
                + "SUM(OD.Quantity * OD.UnitPrice) AS Revenue "
                + "FROM Flowers F "
                + "JOIN OrderDetails OD ON F.FlowerID = OD.FlowerID "
                + "JOIN Orders O ON OD.OrderID = O.OrderID "
                + "WHERE O.Status = 'Completed' "
                + "GROUP BY F.FlowerID, F.FlowerName, F.StockQuantity "
                + "ORDER BY TotalSold DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("flowerId", rs.getInt("FlowerID"));
                    row.put("flowerName", rs.getString("FlowerName"));
                    row.put("stockQuantity", rs.getInt("StockQuantity"));
                    row.put("totalSold", rs.getInt("TotalSold"));
                    BigDecimal rev = rs.getBigDecimal("Revenue");
                    row.put("revenue", rev != null ? rev : BigDecimal.ZERO);
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================== 3. INVENTORY =====================

    /**
     * Báo cáo tồn kho. lowStockThreshold: coi là sắp hết (vd 10).
     * flowerId, flowerName, stockQuantity, status, stockLevel (OUT/LOW/OK)
     */
    public List<Map<String, Object>> getInventoryReport(int lowStockThreshold) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT FlowerID, FlowerName, StockQuantity, Status "
                + "FROM Flowers ORDER BY StockQuantity ASC, FlowerName";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int stock = rs.getInt("StockQuantity");
                String level;
                if (stock <= 0) {
                    level = "OUT";
                } else if (stock <= lowStockThreshold) {
                    level = "LOW";
                } else {
                    level = "OK";
                }
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("flowerId", rs.getInt("FlowerID"));
                row.put("flowerName", rs.getString("FlowerName"));
                row.put("stockQuantity", stock);
                row.put("status", rs.getBoolean("Status"));
                row.put("stockLevel", level);
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getInventoryReport() {
        return getInventoryReport(10);
    }

    // ===================== 4. TOP CUSTOMERS (VIP) =====================

    /**
     * Top khách theo tổng chi tiêu (đơn Completed).
     * userId, fullName, email, totalOrders, totalSpent
     */
    public List<Map<String, Object>> getTopCustomers(int topN) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TOP (?) U.UserID, U.FullName, U.Email, "
                + "COUNT(O.OrderID) AS TotalOrders, "
                + "SUM(O.TotalAmount) AS TotalSpent "
                + "FROM Users U "
                + "JOIN Orders O ON U.UserID = O.UserID "
                + "WHERE O.Status = 'Completed' "
                + "GROUP BY U.UserID, U.FullName, U.Email "
                + "ORDER BY TotalSpent DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("userId", rs.getInt("UserID"));
                    row.put("fullName", rs.getString("FullName"));
                    row.put("email", rs.getString("Email"));
                    row.put("totalOrders", rs.getInt("TotalOrders"));
                    BigDecimal spent = rs.getBigDecimal("TotalSpent");
                    row.put("totalSpent", spent != null ? spent : BigDecimal.ZERO);
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================== 5. ORDER STATUS =====================

    /**
     * Số đơn theo trạng thái.
     * status, orderCount
     */
    public List<Map<String, Object>> getOrderCountByStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT Status, COUNT(*) AS OrderCount "
                + "FROM Orders GROUP BY Status ORDER BY OrderCount DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("status", rs.getString("Status"));
                row.put("orderCount", rs.getInt("OrderCount"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================== helpers =====================

    private int count(String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
