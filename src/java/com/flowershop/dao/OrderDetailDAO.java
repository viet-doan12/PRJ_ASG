package com.flowershop.dao;

import com.flowershop.model.OrderDetail;
import com.flowershop.util.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO extends DBContext {

    // 1. LẤY DANH SÁCH CHI TIẾT CỦA MỘT ĐƠN HÀNG
    public List<OrderDetail> getOrderDetailsByOrderId(int orderID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetails WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailID(rs.getInt("OrderDetailID"));
                    detail.setOrderID(rs.getInt("OrderID"));
                    detail.setFlowerID(rs.getInt("FlowerID"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    detail.setUnitPrice(rs.getBigDecimal("UnitPrice")); 
                    list.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THÊM MỚI MỘT CHI TIẾT ĐƠN HÀNG
    public boolean insertOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO OrderDetails (OrderID, FlowerID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, detail.getOrderID());
            ps.setInt(2, detail.getFlowerID());
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getUnitPrice()); 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. NÂNG CẤP: CẬP NHẬT SỐ LƯỢNG MỘT MÓN TRONG ĐƠN HÀNG (Dành cho Admin sửa đơn)
    public boolean updateQuantity(int orderDetailID, int newQuantity) {
        String sql = "UPDATE OrderDetails SET Quantity = ? WHERE OrderDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, orderDetailID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. NÂNG CẤP: XÓA MỘT MÓN KHỎI ĐƠN HÀNG (Khi chỉnh sửa đơn trước khi giao)
    public boolean deleteOrderDetail(int orderDetailID) {
        String sql = "DELETE FROM OrderDetails WHERE OrderDetailID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderDetailID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. NÂNG CẤP: THỐNG KÊ TOP SẢN PHẨM BÁN CHẠY NHẤT (Trả về danh sách ID các loại hoa)
    public List<Integer> getTopSellingFlowers(int limit) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT TOP (?) FlowerID, SUM(Quantity) AS TotalSold "
                   + "FROM OrderDetails "
                   + "GROUP BY FlowerID "
                   + "ORDER BY TotalSold DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FlowerID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}