package com.flowershop.dao;

import com.flowershop.model.Payment;
import com.flowershop.util.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO extends DBContext {

    // 1. LƯU THÔNG TIN THANH TOÁN MỚI
    public boolean insertPayment(Payment payment) {
        String sql = "INSERT INTO Payments (OrderID, PaymentMethod, PaymentStatus, TransactionCode, PaymentDate) "
                   + "VALUES (?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, payment.getOrderID());
            ps.setString(2, payment.getPaymentMethod());
            ps.setString(3, payment.getPaymentStatus());
            ps.setString(4, payment.getTransactionCode()); 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. LẤY THÔNG TIN THANH TOÁN THEO ĐƠN HÀNG (OrderID)
    public Payment getPaymentByOrderId(int orderID) {
        String sql = "SELECT * FROM Payments WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. CẬP NHẬT TRẠNG THÁI THANH TOÁN
    public boolean updatePaymentStatus(int orderID, String status, String transactionCode) {
        String sql = "UPDATE Payments SET PaymentStatus = ?, TransactionCode = ? WHERE OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, transactionCode);
            ps.setInt(3, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. NÂNG CẤP: LẤY TOÀN BỘ DANH SÁCH LỊCH SỬ THANH TOÁN (Dành cho Admin)
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM Payments ORDER BY PaymentDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 5. NÂNG CẤP: TÌM KIẾM THEO MÃ TRANSACTION CODE (Dùng khi đối soát với ví VNPAY/Momo)
    public Payment getPaymentByTransactionCode(String code) {
        String sql = "SELECT * FROM Payments WHERE TransactionCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm phụ trợ map dữ liệu nhằm tránh viết lặp lại code và xử lý an toàn lỗi thư viện của Leader
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentID(rs.getInt("PaymentID"));
        payment.setOrderID(rs.getInt("OrderID"));
        payment.setPaymentMethod(rs.getString("PaymentMethod"));
        payment.setPaymentStatus(rs.getString("PaymentStatus"));
        payment.setTransactionCode(rs.getString("TransactionCode"));
        payment.setPaymentDate(rs.getTimestamp("PaymentDate"));

        return payment;
    }
}