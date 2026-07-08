package com.flowershop.dao;

import com.flowershop.model.Category;
import com.flowershop.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {

    // 1. Create - Thêm Category mới
    public boolean insertCategory(Category category) {
        String sql = "INSERT INTO Categories (CategoryName, Description, Status) VALUES (?, ?, ?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, category.getCategoryName());
            st.setString(2, category.getDescription());
            st.setBoolean(3, category.isStatus());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Read - Lấy tất cả danh mục (Dùng cho Quản lý & Homepage)
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getString("Description"),
                        rs.getBoolean("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2.1 Read - Lấy danh mục đang hoạt động (Dùng cho Homepage)
    public List<Category> getActiveCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE Status = 1";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getString("Description"),
                        rs.getBoolean("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2.2 Read - Lấy danh mục theo ID
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM Categories WHERE CategoryID = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, categoryId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("CategoryID"),
                            rs.getString("CategoryName"),
                            rs.getString("Description"),
                            rs.getBoolean("Status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. Update - Cập nhật Category
    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET CategoryName = ?, Description = ?, Status = ? WHERE CategoryID = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, category.getCategoryName());
            st.setString(2, category.getDescription());
            st.setBoolean(3, category.isStatus());
            st.setInt(4, category.getCategoryID());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Delete - Xóa (Thường thì chỉ ẩn - set status = 0, nhưng nếu yêu cầu CRUD thực sự thì dùng DELETE)
    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, categoryId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Search Category (Tìm kiếm)
    public List<Category> searchCategory(String keyword) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE CategoryName LIKE ? OR Description LIKE ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(
                            rs.getInt("CategoryID"),
                            rs.getString("CategoryName"),
                            rs.getString("Description"),
                            rs.getBoolean("Status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 6. Pagination - Phân trang (Lấy danh mục theo trang)
    public List<Category> getCategoriesByPage(int offset, int limit) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY CategoryID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, offset);
            st.setInt(2, limit);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(
                            rs.getInt("CategoryID"),
                            rs.getString("CategoryName"),
                            rs.getString("Description"),
                            rs.getBoolean("Status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. Pagination with Search
    public List<Category> searchCategoriesByPage(String keyword, int offset, int limit) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE CategoryName LIKE ? OR Description LIKE ? "
                   + "ORDER BY CategoryID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");
            st.setInt(3, offset);
            st.setInt(4, limit);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(
                            rs.getInt("CategoryID"),
                            rs.getString("CategoryName"),
                            rs.getString("Description"),
                            rs.getBoolean("Status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 8. Get total count for pagination (All)
    public int getTotalCategories() {
        String sql = "SELECT COUNT(*) FROM Categories";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 9. Get total count for pagination (Search)
    public int getTotalSearchCategories(String keyword) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE CategoryName LIKE ? OR Description LIKE ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
