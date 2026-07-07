package com.flowershop.dao;

import com.flowershop.util.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Giả định bạn có một lớp Model tên là CartItem hoặc Cart để chứa dữ liệu.
// Nếu leader đặt tên khác (ví dụ: CartModel), bạn hãy sửa tên class này lại cho khớp nhé.
import com.flowershop.model.CartItem; 

public class CartDAO extends DBContext {

    // 1. LẤY DANH SÁCH SẢN PHẨM TRONG GIỎ HÀNG CỦA MỘT USER
    public List<CartItem> getCartByUserId(int userID) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT * FROM Cart WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    // Giả định các hàm set/get theo cấu trúc phổ biến của Model
                    item.setCartID(rs.getInt("CartID"));
                    item.setUserID(rs.getInt("UserID"));
                    item.setFlowerID(rs.getInt("FlowerID"));
                    item.setQuantity(rs.getInt("Quantity"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THÊM SẢN PHẨM VÀO GIỎ HÀNG (Hoặc tăng số lượng nếu sản phẩm đã có sẵn)
    public boolean addToCart(int userID, int flowerID, int quantity) {
        // Kiểm tra xem bông hoa này đã có trong giỏ hàng của user chưa
        String checkSql = "SELECT Quantity FROM Cart WHERE UserID = ? AND FlowerID = ?";
        String insertSql = "INSERT INTO Cart (UserID, FlowerID, Quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE Cart SET Quantity = Quantity + ? WHERE UserID = ? AND FlowerID = ?";

        try {
            // Kiểm tra trước
            try (PreparedStatement psCheck = connection.prepareStatement(checkSql)) {
                psCheck.setInt(1, userID);
                psCheck.setInt(2, flowerID);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        // Nếu đã có mặt trong giỏ, tiến hành cộng dồn số lượng (Update)
                        try (PreparedStatement psUpdate = connection.prepareStatement(updateSql)) {
                            psUpdate.setInt(1, quantity);
                            psUpdate.setInt(2, userID);
                            psUpdate.setInt(3, flowerID);
                            return psUpdate.executeUpdate() > 0;
                        }
                    } else {
                        // Nếu chưa có, tạo mới một dòng trong giỏ hàng (Insert)
                        try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                            psInsert.setInt(1, userID);
                            psInsert.setInt(2, flowerID);
                            psInsert.setInt(3, quantity);
                            return psInsert.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. CẬP NHẬT SỐ LƯỢNG SẢN PHẨM (Khi khách thay đổi số lượng ở trang giỏ hàng)
    public boolean updateQuantity(int userID, int flowerID, int newQuantity) {
        String sql = "UPDATE Cart SET Quantity = ? WHERE UserID = ? AND FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, userID);
            ps.setInt(3, flowerID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. XÓA MỘT SẢN PHẨM KHỎI GIỎ HÀNG (Khi bấm nút "Xóa" hoặc Icon thùng rác)
    public boolean removeFromCart(int userID, int flowerID) {
        String sql = "DELETE FROM Cart WHERE UserID = ? AND FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, flowerID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. XÓA SẠCH GIỎ HÀNG (Cực kỳ quan trọng: Dùng để dọn giỏ ngay sau khi khách bấm "Thanh toán thành công")
    public boolean clearCart(int userID) {
        String sql = "DELETE FROM Cart WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}