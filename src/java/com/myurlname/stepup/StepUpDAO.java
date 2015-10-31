package com.myurlname.stepup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * StepUpDAO is a data access object for the StepUp Web App.
 * Currently, it implements all data storage in a derby database
 * using JDBC commands directly.
 */
public class StepUpDAO {
    
    private Connection CONN;
    private String lastError;
    
    public StepUpDAO (String jdbcUrl) {
        try {
            CONN = DriverManager.getConnection(jdbcUrl);
            lastError = null;
        }
        catch (SQLException sqle) {
            lastError = sqle.getMessage();
        }            
    }
    
    /** Use after calling a DAO method to get error information.
     * returns NULL if no error.
     * @return NULL or error message
     */
    public String getLastError () {
        return lastError;
    }
    
    /**Authenticate assumes the username and password fields
     * have already been validated (ie, no SQL injection, html
     * injection).
     * @param username
     * @param password
     * @return User object
     */
    public User authenticate (String username, String password) {
        User user = null;
        String sql = "SELECT * FROM USERS WHERE USERNAME = '" + 
                     username + "' AND PASSWORD = '" + password + "'";
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            if (rs.next()) {
                user = new User(rs.getString("username"));
                user.setUserId(rs.getInt("id"));
            }
            lastError = null;
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException sqle) {}
            if (stat != null)
                try {
                    stat.close();
                } catch (SQLException sqle) {}
        }
        return user;                      
    }
    
    public void close ()
    {
        try {
            if (CONN != null)
                CONN.close();
        }
        catch (Exception e) {
            lastError = e.getMessage();            
        }
    }
    
}
