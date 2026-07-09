package com.flowershop.listener;

import com.flowershop.util.DBCPUtils;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Hooks into the web application lifecycle so the connection pool is
 * closed cleanly when the app is stopped/undeployed (prevents connection
 * leaks that keep the SQL Server session alive after Tomcat shuts down).
 */
@WebListener
public class DBContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().log("FlowerShop application starting up...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBCPUtils.closeDataSource();
        sce.getServletContext().log("FlowerShop application stopped - connection pool closed.");
    }
}