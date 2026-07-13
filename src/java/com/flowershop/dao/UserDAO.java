/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flowershop.dao;

import com.flowershop.model.User;
import com.flowershop.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class UserDAO extends DBContext {

    public User getUserByEmail(String email) {
        String sql = "SELECT u.*, r.RoleName FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
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

    public User getUserById(int userId) {
        String sql = "SELECT u.*, r.RoleName FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
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

    public boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Dùng cho Register. password truyền vào PHẢI đã được hash sẵn.
     */
    public int insertUser(User u) {
        String sql = "INSERT INTO Users (FullName, Email, Password, Phone, Address, RoleID, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getAddress());
            ps.setInt(6, u.getRoleID());
            ps.setBoolean(7, u.isStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
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
     * Cập nhật thông tin cá nhân (không đổi mật khẩu) - dùng cho
     * ProfileServlet.
     */
    public boolean updateProfile(User u) {
        String sql = "UPDATE Users SET FullName = ?, Phone = ?, Address = ?, UpdatedDate = GETDATE() "
                + "WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getPhone());
            ps.setString(3, u.getAddress());
            ps.setInt(4, u.getUserID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE Users SET Password = ?, UpdatedDate = GETDATE() WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int userId, boolean status) {
        String sql = "UPDATE Users SET Status = ?, UpdatedDate = GETDATE() WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Danh sách user có phân trang + tìm kiếm theo tên/email - dùng cho trang
     * quản trị.
     */
    public List<User> getUsersPaged(int offset, int pageSize, String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.RoleName FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE (? IS NULL OR u.FullName LIKE ? OR u.Email LIKE ?) "
                + "ORDER BY u.UserID DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String like = (keyword == null || keyword.isBlank()) ? null : "%" + keyword.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, offset);
            ps.setInt(5, pageSize);
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

    public int countUsers(String keyword) {
        String sql = "SELECT COUNT(*) FROM Users u "
                + "WHERE (? IS NULL OR u.FullName LIKE ? OR u.Email LIKE ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String like = (keyword == null || keyword.isBlank()) ? null : "%" + keyword.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
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
     * Đăng nhập bằng Email và Password. Password truyền vào nên là password đã
     * hash.
     */
    public User login(String email, String password) {
        String sql = "SELECT u.*, r.RoleName "
                + "FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Email = ? "
                + "AND u.Password = ? "
                + "AND u.Status = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

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

    /**
     * Admin cập nhật thông tin người dùng
     */
    public boolean updateUser(User u) {

        String sql = "UPDATE Users "
                + "SET FullName = ?, "
                + "Email = ?, "
                + "Phone = ?, "
                + "Address = ?, "
                + "RoleID = ?, "
                + "Status = ?, "
                + "UpdatedDate = GETDATE() "
                + "WHERE UserID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getAddress());
            ps.setInt(5, u.getRoleID());
            ps.setBoolean(6, u.isStatus());
            ps.setInt(7, u.getUserID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy toàn bộ danh sách người dùng
     */
    public List<User> getAllUsers() {

        List<User> list = new ArrayList<>();

        String sql = "SELECT u.*, r.RoleName "
                + "FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "ORDER BY u.UserID DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(mapRow(rs));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<User> getUsersByRole(int roleId) {

        List<User> list = new ArrayList<>();

        String sql = "SELECT u.*, r.RoleName "
                + "FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.RoleID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, roleId);

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

    public int getTotalUsers() {

        String sql = "SELECT COUNT(*) FROM Users";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean checkOldPassword(int userId, String password) {

        String sql = "SELECT 1 "
                + "FROM Users "
                + "WHERE UserID = ? "
                + "AND Password = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePasswordByEmail(String email, String hashedPassword) {
        String sql = "UPDATE Users SET Password = ?, UpdatedDate = GETDATE() WHERE Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countNewUsersByMonth(int month, int year) {
        String sql = "SELECT COUNT(*) FROM Users WHERE MONTH(CreatedDate) = ? AND YEAR(CreatedDate) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
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
     * Admin: phân trang + tìm kiếm nâng cao (keyword, role, status).
     * roleId = null → mọi role; status = null → mọi trạng thái.
     */
    public List<User> getUsersPaged(int offset, int pageSize, String keyword,
            Integer roleId, Boolean status) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.*, r.RoleName FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (u.FullName LIKE ? OR u.Email LIKE ? OR u.Phone LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (roleId != null) {
            sql.append("AND u.RoleID = ? ");
            params.add(roleId);
        }
        if (status != null) {
            sql.append("AND u.Status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY u.UserID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                ps.setObject(idx++, p);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);
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

    public int countUsers(String keyword, Integer roleId, Boolean status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users u WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (u.FullName LIKE ? OR u.Email LIKE ? OR u.Phone LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (roleId != null) {
            sql.append("AND u.RoleID = ? ");
            params.add(roleId);
        }
        if (status != null) {
            sql.append("AND u.Status = ? ");
            params.add(status);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                ps.setObject(idx++, p);
            }
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

    /** Soft-delete / khóa tài khoản (Status = 0). */
    public boolean lockUser(int userId) {
        return updateStatus(userId, false);
    }

    /** Mở khóa tài khoản (Status = 1). */
    public boolean unlockUser(int userId) {
        return updateStatus(userId, true);
    }

    public boolean updateRole(int userId, int roleId) {
        String sql = "UPDATE Users SET RoleID = ?, UpdatedDate = GETDATE() WHERE UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

private User mapRow(ResultSet rs) throws SQLException {
    User u = new User();
    u.setUserID(rs.getInt("UserID"));
    u.setFullName(rs.getString("FullName"));
    u.setEmail(rs.getString("Email"));
    u.setPassword(rs.getString("Password"));
    u.setPhone(rs.getString("Phone"));
    u.setAddress(rs.getString("Address"));
    u.setRoleID(rs.getInt("RoleID"));
    u.setStatus(rs.getBoolean("Status"));
    u.setCreatedDate(rs.getTimestamp("CreatedDate"));
    u.setUpdatedDate(rs.getTimestamp("UpdatedDate"));
    try {
        u.setRoleName(rs.getString("RoleName"));
    } catch (SQLException ignored) {
        // cột RoleName chỉ có khi query JOIN với Roles
    }
    return u;
}
}