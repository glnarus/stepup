package com.myurlname.stepup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.  Utilized to initialize the DAO object
 * with a database connection.  The connection is provided via a JDBC string 
 * composed of context init parameters for easy configuration.
 *
 * @author gabriel
 */
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String dbProtocol = sc.getInitParameter("dbProtocol");
        String dbHost = sc.getInitParameter("dbHost");
        String dbPort = sc.getInitParameter("dbPort");
        String dbName = sc.getInitParameter("dbName");
        String dbUsername = sc.getInitParameter("dbUsername");
        String dbPassword = sc.getInitParameter("dbPassword");
        String jdbcUrl = 
                String.format("%s://%s:%s/%s;user=%s;password=%s", 
                   dbProtocol, dbHost, dbPort, dbName, dbUsername, dbPassword);
        StepUpDAO db = new StepUpDAO (jdbcUrl);
        sc.setAttribute("db", db);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        StepUpDAO db = (StepUpDAO)sce.getServletContext().getAttribute("db");
        db.close();
    }
}
