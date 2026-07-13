package com.flowershop.util;

import com.flowershop.util.DBCPUtils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FPT University - PRJ30X
 */
public class DBContext {

    protected Connection connection;

    public DBContext() {
        //@Students: You are not allowed to edit this method  
        try {
            Properties properties = new Properties();
//            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("../ConnectDB.properties");
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ConnectDB.properties");
            try {
                properties.load(inputStream);
            } catch (IOException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
            String user = properties.getProperty("userID");
            String pass = properties.getProperty("password");
            String url = properties.getProperty("url");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DBCPUtils.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Trả connection về lại pool sau khi DAO dùng xong.
     * Được thêm mới hoàn toàn - không sửa gì constructor phía trên.
     * Gọi trong khối finally của servlet ngay sau khi dùng xong 1 DAO,
     * để tránh làm cạn kiệt Tomcat JDBC Connection Pool.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close(); // với connection pool, close() = trả về pool, không hủy thật
            }
        } catch (SQLException e) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "Loi khi dong connection", e);
        }
    }
}