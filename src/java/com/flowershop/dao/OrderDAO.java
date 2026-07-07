package com.flowershop.dao;

import com.flowershop.model.Order;
import com.flowershop.model.OrderDetail;
import com.flowershop.util.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    // 1. TẠO ĐƠN HÀNG (Sử dụng Database Transaction)
    public boolean createOrder(Order order, List<OrderDetail> orderDetails) {
        // Loại bỏ cột PaymentID khỏi câu lệnh SQL vì Model Order của leader hoàn toàn không có trường này
        String insertOrderSQL = "INSERT INTO Orders (UserID, OrderDate, TotalAmount, Status, ShippingAddress) "
                              + "VALUES (?, GETDATE(), ?, ?, ?)";
        String insertDetailSQL = "INSERT INTO OrderDetails (OrderID, FlowerID, Quantity, Price) VALUES (?, ?, ?, ?)";
        String updateStockSQL = "UPDATE Flowers SET StockQuantity = StockQuantity - ? WHERE FlowerID = ? AND StockQuantity >= ?";

        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;

        try {
            connection.setAutoCommit(false);

            // [Bước 1]: Chèn thông tin vào bảng Orders
            psOrder = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getUserID());
            psOrder.setBigDecimal(2, order.getTotalAmount()); // Đã khớp đúng kiểu BigDecimal trong Model Order
            psOrder.setString(3, order.getStatus()); 
            psOrder.setString(4, order.getShippingAddress());

            int affectedRows = psOrder.executeUpdate();
            if (affectedRows == 0) {
                connection.rollback();
                return false;
            }

            rs = psOrder.getGeneratedKeys();
            int generatedOrderId = -1;
            if (rs.next()) {
                generatedOrderId = rs.getInt(1);
            } else {
                connection.rollback();
                return false;
            }

            // [Bước 2]: Chuẩn bị chèn chi tiết đơn hàng & cập nhật tồn kho
            psDetail = connection.prepareStatement(insertDetailSQL);
            psUpdateStock = connection.prepareStatement(updateStockSQL);

            for (OrderDetail detail : orderDetails) {
                psDetail.setInt(1, generatedOrderId);
                psDetail.setInt(2, detail.getFlowerID());
                psDetail.setInt(3, detail.getQuantity());
                
                // FIX DỨT ĐIỂM ẢNH 918c46 & 919352: Chuyển sang setDouble để ăn khớp với detail.getPrice() của leader
                psDetail.setBigDecimal(4, detail.getUnitPrice());
                psDetail.addBatch();

                psUpdateStock.setInt(1, detail.getQuantity());
                psUpdateStock.setInt(2, detail.getFlowerID());
                psUpdateStock.setInt(3, detail.getQuantity());
                psUpdateStock.addBatch();
            }

            psDetail.executeBatch();
            int[] stockResults = psUpdateStock.executeBatch();

            for (int result : stockResults) {
                if (result == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psDetail != null) psDetail.close();
                if (psUpdateStock != null) psUpdateStock.close();
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 2. LẤY THÔNG TIN ĐƠN HÀNG THEO ID
    public Order getOrderById(int orderID) {
        String sql = "SELECT * FROM Orders WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. LẤY LỊCH SỬ ĐƠN HÀNG CỦA MỘT KHÁCH HÀNG
    public List<Order> getOrdersByUser(int userID) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. LẤY TẤT CẢ ĐƠN HÀNG (Dành cho Admin)
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY OrderDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 5. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    public boolean updateStatus(int orderID, String status) {
        String sql = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. TÍNH TOÁN DOANH THU
    public double calculateRevenue() {
        String sql = "SELECT SUM(TotalAmount) AS Revenue FROM Orders WHERE Status = 'Completed'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("Revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // MAPPING DATA từ ResultSet sang đối tượng Order
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("OrderID"));
        order.setUserID(rs.getInt("UserID"));
        
        // Ép kiểu Object khéo léo để khớp với thư viện java.security.Timestamp lỗi của leader
        try {
            Object timestampObj = rs.getTimestamp("OrderDate");
            order.setOrderDate((java.security.Timestamp) timestampObj);
        } catch (Exception e) {
            order.setOrderDate(null); 
        }
        
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setShippingAddress(rs.getString("ShippingAddress"));
        
        // Đã dọn sạch hoàn toàn dòng setPaymentID để đảm bảo không lỗi biên dịch
        return order;
    }
}