package com.flowershop.dao;

import com.flowershop.model.Review;
import com.flowershop.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho module Review (Phạm Đức Minh). Map đúng field model Review hiện có
 * (không thêm thuộc tính model).
 */
public class ReviewDAO extends DBContext {

    /**
     * Thêm review. rating nên trong khoảng 1–5 (validate ở servlet).
     *
     * @return ReviewID mới, hoặc -1 nếu lỗi
     */
    public int addReview(Review review) {
        String sql = "INSERT INTO Reviews (OrderID, FlowerID, UserID, Rating, Comment, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (review.getOrderID() > 0) {
                ps.setInt(1, review.getOrderID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setInt(2, review.getFlowerID());
            ps.setInt(3, review.getUserID());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            ps.setBoolean(6, review.isStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Soft-delete review (Status = 0).
     */
    public boolean deleteReview(int reviewId) {
        String sql = "UPDATE Reviews SET Status = 0 WHERE ReviewID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa cứng (dùng khi cần).
     */
    public boolean hardDeleteReview(int reviewId) {
        String sql = "DELETE FROM Reviews WHERE ReviewID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy review active theo hoa.
     */
    public List<Review> getReviewByFlower(int flowerId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews "
                + "WHERE FlowerID = ? AND Status = 1 "
                + "ORDER BY ReviewDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flowerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Review> getReviewsByFlowerId(int flowerId) {
        return getReviewByFlower(flowerId);
    }

    public Review getReviewById(int reviewId) {
        String sql = "SELECT * FROM Reviews WHERE ReviewID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getAverageRating(int flowerId) {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) FROM Reviews "
                + "WHERE FlowerID = ? AND Status = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getReviewCount(int flowerId) {
        String sql = "SELECT COUNT(*) FROM Reviews WHERE FlowerID = ? AND Status = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Kiểm tra khách đã đánh giá sản phẩm này cho đúng đơn hàng đó chưa.
     */
    public boolean hasReviewed(int orderId, int flowerId, int userId) {
        String sql = "SELECT 1 FROM Reviews WHERE OrderID = ? AND FlowerID = ? AND UserID = ?";
        try (java.sql.PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, flowerId);
            ps.setInt(3, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewID(rs.getInt("ReviewID"));
        r.setOrderID(rs.getInt("OrderID"));
        r.setFlowerID(rs.getInt("FlowerID"));
        r.setUserID(rs.getInt("UserID"));
        r.setRating(rs.getInt("Rating"));
        r.setComment(rs.getString("Comment"));
        r.setReviewDate(rs.getTimestamp("ReviewDate"));
        r.setStatus(rs.getBoolean("Status"));
        return r;
    }
}
