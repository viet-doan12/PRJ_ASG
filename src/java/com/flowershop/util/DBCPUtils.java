package com.flowershop.util;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DBCPUtils {

    private static DataSource dataSource;

    private DBCPUtils() {
    }

    public static synchronized Connection getConnection(String url, String user, String password)
            throws SQLException {

        if (dataSource == null) {

            PoolProperties p = new PoolProperties();

            p.setUrl(url);
            p.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            p.setUsername(user);
            p.setPassword(password);

            // Pool Configuration
            p.setInitialSize(5);
            p.setMaxActive(20);
            p.setMaxIdle(10);
            p.setMinIdle(5);

            p.setMaxWait(10000);

            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");

            dataSource = new DataSource();
            dataSource.setPoolProperties(p);
        }

        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}