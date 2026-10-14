package com.lifelink.servlet;

import com.lifelink.dao.DBUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Runs once when the web application starts (Servlet life cycle at application level).
 * Creates the database tables and shares the context-param settings with every JSP.
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        // Reading parameters from web.xml <context-param>
        ctx.setAttribute("appName", ctx.getInitParameter("appName"));
        ctx.setAttribute("currency", ctx.getInitParameter("currencySymbol"));
        try {
            DBUtil.initSchema();
            ctx.log("Database ready.");
        } catch (Exception e) {
            ctx.log("Database setup failed", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("Application stopped.");
    }
}
