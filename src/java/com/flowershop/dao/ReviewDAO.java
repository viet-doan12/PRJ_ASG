package com.flowershop.dao;

import com.flowershop.model.Review;
import com.flowershop.util.DBContext;
import com.flowershop.util.PaginationUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO extends DBContext {

    /**
     * Thêm review. rating nên trong khoảng 1-5 (validate ở servlet).
     * @return ReviewID mới, hoặc -1 nếu lỗi
     */
    public int addReview(Review review) {
        String sql = "INSERT INTO Reviews (OrderID, FlowerID, UserID, Rating, Comment, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (review.getOrderID() > 0) {
                ps.setInt(1, review.getOrderID());
            } else {
                ps.setNull(1, Types.INTEGER);
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

    /** Một khách hàng chỉ được review 1 lần cho mỗi (đơn hàng, hoa). */
    public boolean hasReviewed(int orderId, int flowerId, int userId) {
        String sql = "SELECT 1 FROM Reviews WHERE OrderID = ? AND FlowerID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, flowerId);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Khách hàng tự sửa review của mình. */
    public boolean updateReview(int reviewId, int userId, int rating, String comment) {
        String sql = "UPDATE Reviews SET Rating = ?, Comment = ?, ReviewDate = GETDATE() " +
                     "WHERE ReviewID = ? AND UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, reviewId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Soft-delete (ẩn review, giữ dữ liệu). */
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

    /** Admin: ẩn/hiện review mà không xoá. */
    public boolean updateStatus(int reviewId, boolean status) {
        String sql = "UPDATE Reviews SET Status = ? WHERE ReviewID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, reviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xoá cứng (dùng khi cần, chỉ admin). */
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

    /** Toàn bộ review đang hiển thị của 1 hoa, mới nhất trước (không phân trang). */
    public List<Review> getReviewsByFlowerId(int flowerId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE FlowerID = ? AND Status = 1 ORDER BY ReviewDate DESC";
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

    /** Bản có phân trang - dùng khi 1 hoa có quá nhiều review. */
    public List<Review> getReviewsByFlowerId(int flowerId, int page, int pageSize) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE FlowerID = ? AND Status = 1 " +
                     "ORDER BY ReviewDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flowerId);
            ps.setInt(2, PaginationUtil.getOffset(page, pageSize));
            ps.setInt(3, pageSize);
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

    /** Lịch sử review của 1 khách hàng, dùng cho trang "Review của tôi". */
    public List<Review> getReviewsByUser(int userId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE UserID = ? ORDER BY ReviewDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
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

    /** Trang kiểm duyệt của admin: tất cả review bất kể status. */
    public List<Review> getAllReviews(int page, int pageSize) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM Reviews ORDER BY ReviewDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, PaginationUtil.getOffset(page, pageSize));
            ps.setInt(2, pageSize);
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

    public int countAllReviews() {
        String sql = "SELECT COUNT(*) FROM Reviews";
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

    public double getAverageRating(int flowerId) {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) FROM Reviews WHERE FlowerID = ? AND Status = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : avg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
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