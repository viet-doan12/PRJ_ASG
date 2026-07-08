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
        String insertOrderSQL = "INSERT INTO Orders (UserID, OrderDate, ReceiverName, ReceiverPhone, ShippingAddress, TotalAmount, Status) "
                              + "VALUES (?, GETDATE(), ?, ?, ?, ?, ?)";
        String insertDetailSQL = "INSERT INTO OrderDetails (OrderID, FlowerID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        String updateStockSQL = "UPDATE Flowers SET StockQuantity = StockQuantity - ? WHERE FlowerID = ? AND StockQuantity >= ?";

        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;

        try {
            // Đã chuyển toàn bộ sang biến 'connection' theo đúng tư duy của bạn
            connection.setAutoCommit(false);

            // [Bước 1]: Chèn thông tin vào bảng Orders
            psOrder = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getUserID());
            psOrder.setString(2, order.getReceiverName());
            psOrder.setString(3, order.getReceiverPhone());
            psOrder.setString(4, order.getShippingAddress());
            psOrder.setBigDecimal(5, order.getTotalAmount());
            psOrder.setString(6, order.getStatus());

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
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
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
                if (rs != null) {
                    while (rs.next()) {
                        list.add(mapResultSetToOrder(rs));
                    }
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

    // 6. CẬP NHẬT TOÀN BỘ THÔNG TIN ĐƠN HÀNG
    public boolean updateOrder(Order order) {
        String sql = "UPDATE Orders SET UserID = ?, ReceiverName = ?, ReceiverPhone = ?, ShippingAddress = ?, TotalAmount = ?, Status = ? "
                   + "WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, order.getUserID());
            ps.setString(2, order.getReceiverName());
            ps.setString(3, order.getReceiverPhone());
            ps.setString(4, order.getShippingAddress());
            ps.setBigDecimal(5, order.getTotalAmount());
            ps.setString(6, order.getStatus());
            ps.setInt(7, order.getOrderID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 7. XÓA ĐƠN HÀNG
    public boolean deleteOrder(int orderID) {
        String sql = "DELETE FROM Orders WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 8. TÌM ĐƠN HÀNG THEO TRẠNG THÁI
    public List<Order> getOrdersByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE Status = ? ORDER BY OrderDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
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

    // 9. TÍNH TOÁN DOANH THU
    public double calculateRevenue() {
        String sql = "SELECT SUM(TotalAmount) AS Revenue FROM Orders WHERE Status = 'Completed'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double revenue = rs.getDouble("Revenue");
                return revenue > 0 ? revenue : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 10. TÍNH TOÁN DOANH THU THEO KHOẢNG THỜI GIAN
    public double calculateRevenueByDateRange(String startDate, String endDate) {
        String sql = "SELECT SUM(TotalAmount) AS Revenue FROM Orders "
                   + "WHERE Status = 'Completed' AND OrderDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double revenue = rs.getDouble("Revenue");
                    return revenue > 0 ? revenue : 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 11. ĐẾM SỐ ĐƠN HÀNG CỦA KHÁCH HÀNG
    public int countOrdersByUser(int userID) {
        String sql = "SELECT COUNT(*) AS TotalOrders FROM Orders WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalOrders");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 12. LẤY ID ĐƠN HÀNG VỪA TẠO
    public int getLastInsertedOrderID() {
        String sql = "SELECT TOP 1 OrderID FROM Orders ORDER BY OrderID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("OrderID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // MAPPING DATA từ ResultSet sang đối tượng Order
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
    Order order = new Order();
    order.setOrderID(rs.get.setOrderDate(rs.getTimestamp("OrderDate"));
    order.setReceiverName(rs.getString("ReceiverName"));
    order.setReceiverPhone(rs.getString("ReceiverPhone"));
    order.setShippingAddress(rs.getString("ShippingAddress"));
    order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
    order.setStatus(rs.getString("Status"));
    return order;
    }
}
