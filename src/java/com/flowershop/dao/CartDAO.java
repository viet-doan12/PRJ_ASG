package com.flowershop.dao;

import com.flowershop.model.CartItem;
import com.flowershop.model.Flower;
import com.flowershop.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý các thao tác dữ liệu liên quan đến Giỏ hàng (Bảng Carts và CartItems)
 * Kế thừa trực tiếp biến 'connection' từ DBContext
 */
public class CartDAO extends DBContext {

    // 0. LẤY DANH SÁCH SẢN PHẨM TRONG GIỎ HÀNG (JOIN với bảng Flowers)
    public List<CartItem> getCartItemsFromDB(int cartID) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT ci.FlowerID, ci.Quantity, "
                + "f.FlowerName, f.Description, f.Price, f.StockQuantity, "
                + "f.Image, f.CategoryID, f.Status, f.CreatedDate, f.UpdatedDate "
                + "FROM CartItems ci "
                + "JOIN Flowers f ON ci.FlowerID = f.FlowerID "
                + "WHERE ci.CartID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Flower flower = new Flower();
                    flower.setFlowerID(rs.getInt("FlowerID"));
                    flower.setFlowerName(rs.getString("FlowerName"));
                    flower.setDescription(rs.getString("Description"));
                    flower.setPrice(rs.getBigDecimal("Price"));
                    flower.setStockQuantity(rs.getInt("StockQuantity"));
                    flower.setImage(rs.getString("Image"));
                    flower.setCategoryID(rs.getInt("CategoryID"));
                    flower.setStatus(rs.getBoolean("Status"));
                    flower.setCreatedDate(rs.getTimestamp("CreatedDate"));
                    flower.setUpdatedDate(rs.getTimestamp("UpdatedDate"));

                    int quantity = rs.getInt("Quantity");

                    cartItems.add(new CartItem(flower, quantity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    // 0.1 ĐẾM TỔNG SỐ LƯỢNG SẢN PHẨM TRONG GIỎ (hiển thị badge trên header, nếu cần)
    public int countCartItems(int cartID) {
        String sql = "SELECT ISNULL(SUM(Quantity), 0) AS Total FROM CartItems WHERE CartID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 1. LẤY CART_ID CỦA USER (Tự động tạo mới nếu User chưa có giỏ hàng)
    public int getCartIDByUserID(int userID) {
        String selectSQL = "SELECT CartID FROM Carts WHERE UserID = ?";
        String insertSQL = "INSERT INTO Carts (UserID, CreatedDate) VALUES (?, GETDATE())";

        try (PreparedStatement psSelect = connection.prepareStatement(selectSQL)) {
            psSelect.setInt(1, userID);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CartID");
                }
            }

            try (PreparedStatement psInsert = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, userID);
                int affectedRows = psInsert.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rsKey = psInsert.getGeneratedKeys()) {
                        if (rsKey.next()) {
                            return rsKey.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 2. THÊM SẢN PHẨM VÀO GIỎ HÀNG (Cộng dồn số lượng nếu đã tồn tại)
    public boolean addItemToCart(int cartID, int flowerID, int quantity) {
        String checkSQL = "SELECT Quantity FROM CartItems WHERE CartID = ? AND FlowerID = ?";
        String updateSQL = "UPDATE CartItems SET Quantity = Quantity + ? WHERE CartID = ? AND FlowerID = ?";
        String insertSQL = "INSERT INTO CartItems (CartID, FlowerID, Quantity) VALUES (?, ?, ?)";

        try {
            try (PreparedStatement psCheck = connection.prepareStatement(checkSQL)) {
                psCheck.setInt(1, cartID);
                psCheck.setInt(2, flowerID);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement psUpdate = connection.prepareStatement(updateSQL)) {
                            psUpdate.setInt(1, quantity);
                            psUpdate.setInt(2, cartID);
                            psUpdate.setInt(3, flowerID);
                            return psUpdate.executeUpdate() > 0;
                        }
                    } else {
                        try (PreparedStatement psInsert = connection.prepareStatement(insertSQL)) {
                            psInsert.setInt(1, cartID);
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

    // 3. CẬP NHẬT SỐ LƯỢNG MỚI TRONG GIỎ HÀNG
    public boolean updateCartItemQuantity(int cartID, int flowerID, int newQuantity) {
        String sql = "UPDATE CartItems SET Quantity = ? WHERE CartID = ? AND FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartID);
            ps.setInt(3, flowerID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. XÓA MỘT SẢN PHẨM KHỎI GIỎ HÀNG
    public boolean deleteCartItem(int cartID, int flowerID) {
        String sql = "DELETE FROM CartItems WHERE CartID = ? AND FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartID);
            ps.setInt(2, flowerID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. XÓA SẠCH GIỎ HÀNG (Sử dụng sau khi Checkout đặt hàng thành công)
    public boolean clearCart(int cartID) {
        String sql = "DELETE FROM CartItems WHERE CartID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}