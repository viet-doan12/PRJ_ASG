/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowershop.dao;

/**
 *
 * @author ADMIN
 */
import com.flowershop.model.Flower;
import com.flowershop.util.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class FlowerDAO extends DBContext {

    public List<Flower> getAllFlowers() {
        List<Flower> list = new ArrayList<>();
        String sql = "SELECT * FROM Flowers ORDER BY FlowerID";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToFlower(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Flower getFlowerById(int flowerID) {
        String sql = "SELECT * FROM Flowers WHERE FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, flowerID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFlower(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Flower> searchFlower(String keyword) {
        List<Flower> list = new ArrayList<>();
        String sql = "SELECT * FROM Flowers WHERE FlowerName LIKE ? AND Status = 1 ORDER BY FlowerID";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlower(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int insertFlower(Flower f) {
        String sql = "INSERT INTO Flowers (FlowerName, Description, Price, StockQuantity, "
                   + "Image, CategoryID, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, f.getFlowerName());
            ps.setString(2, f.getDescription());
            ps.setBigDecimal(3, f.getPrice());
            ps.setInt(4, f.getStockQuantity());
            ps.setString(5, f.getImage());
            ps.setInt(6, f.getCategoryID());
            ps.setBoolean(7, f.isStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateFlower(Flower f) {
        String sql = "UPDATE Flowers SET FlowerName = ?, Description = ?, Price = ?, "
                   + "StockQuantity = ?, Image = ?, CategoryID = ?, Status = ?, UpdatedDate = GETDATE() "
                   + "WHERE FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, f.getFlowerName());
            ps.setString(2, f.getDescription());
            ps.setBigDecimal(3, f.getPrice());
            ps.setInt(4, f.getStockQuantity());
            ps.setString(5, f.getImage());
            ps.setInt(6, f.getCategoryID());
            ps.setBoolean(7, f.isStatus());
            ps.setInt(8, f.getFlowerID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFlower(int flowerID) {
        String sql = "UPDATE Flowers SET Status = 0, UpdatedDate = GETDATE() WHERE FlowerID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, flowerID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Flower> getFlowerByCategory(int categoryID) {
        List<Flower> list = new ArrayList<>();
        String sql = "SELECT * FROM Flowers WHERE CategoryID = ? AND Status = 1 ORDER BY FlowerID";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, categoryID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlower(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Flower> getTopSellingFlower(int topN) {
        List<Flower> list = new ArrayList<>();
        String sql = "SELECT TOP (?) F.*, SUM(OD.Quantity) AS TotalSold "
                   + "FROM Flowers F "
                   + "JOIN OrderDetails OD ON F.FlowerID = OD.FlowerID "
                   + "JOIN Orders O ON OD.OrderID = O.OrderID "
                   + "WHERE O.Status = 'Completed' AND F.Status = 1 "
                   + "GROUP BY F.FlowerID, F.FlowerName, F.Description, F.Price, F.StockQuantity, "
                   + "F.Image, F.CategoryID, F.Status, F.CreatedDate, F.UpdatedDate "
                   + "ORDER BY TotalSold DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, topN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlower(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Flower> getTopSellingFlower() {
        return getTopSellingFlower(5);
    }

    private Flower mapResultSetToFlower(ResultSet rs) throws SQLException {
        Flower f = new Flower();
        f.setFlowerID(rs.getInt("FlowerID"));
        f.setFlowerName(rs.getString("FlowerName"));
        f.setDescription(rs.getString("Description"));
        f.setPrice(rs.getBigDecimal("Price"));
        f.setStockQuantity(rs.getInt("StockQuantity"));
        f.setImage(rs.getString("Image"));
        f.setCategoryID(rs.getInt("CategoryID"));
        f.setStatus(rs.getBoolean("Status"));
        f.setCreatedDate(rs.getTimestamp("CreatedDate"));
        f.setUpdatedDate(rs.getTimestamp("UpdatedDate"));
        return f;
    }

    public static void main(String[] args) {
        FlowerDAO dao = new FlowerDAO();

        System.out.println("=== All Flowers ===");
        for (Flower f : dao.getAllFlowers()) {
            System.out.println(f.getFlowerID() + " - " + f.getFlowerName() + " - " + f.getPrice());
        }

        System.out.println("=== Top Selling ===");
        for (Flower f : dao.getTopSellingFlower()) {
            System.out.println(f.getFlowerID() + " - " + f.getFlowerName());
        }
    }
}
