package com.myurlname.stepup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * @return User object with username and userId set
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
                user = new User(rs.getString("username"), rs.getInt("id"));
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
    
    /**Call this method to register a new user, it builds the Profile and
     * User table entries for a user, and if successful, returns a valid User
     * object complete with username and userId already set.  If anything
     * goes wrong, it will return null and you can read about the failure
     * with the getLastError() method.
     * @param Profile object
     * @return User object
     */
    public User register(Profile p) {
    String userSql = "INSERT INTO USERS (username,password) VALUES (?,?)";
    String profSql = "INSERT INTO PROFILES (firstname,lastname,";
    profSql += "email,phone,userid,goal,reward) VALUES (?,?,?,?,?,?,?)";
    String updateSql = "UPDATE Users SET profileid = ? WHERE id = ?";
    PreparedStatement pstatUser = null, pstatProf = null, pstatUpdate = null;
    ResultSet userRs = null, profRs = null;
    int userId = 0, profileId = 0;
    User user = null;
    if (!p.validateRegistration()) return null; //controller should already 
                                            //do this, but check anyway
    try {
        pstatUser = CONN.prepareStatement(userSql, 
                                          Statement.RETURN_GENERATED_KEYS);
        pstatUser.setString(1, p.getUsername());
        pstatUser.setString(2, p.getPassword1());
        pstatUser.executeUpdate();
        userRs = pstatUser.getGeneratedKeys();
        if (userRs.next())
            userId = userRs.getInt(1);
        pstatProf = CONN.prepareStatement(profSql, 
                                          Statement.RETURN_GENERATED_KEYS);

        pstatProf.setString(1, p.getFirstName());
        pstatProf.setString(2, p.getLastName());
        pstatProf.setString(3, p.getEmail());
        pstatProf.setString(4, p.getPhone());
        pstatProf.setInt(5, userId);
        pstatProf.setString(6, p.getGoal());
        pstatProf.setString(7, p.getReward());           
        pstatProf.executeUpdate();

        profRs = pstatProf.getGeneratedKeys();
        if (profRs.next())
            profileId = profRs.getInt(1);
        pstatUpdate = CONN.prepareStatement(updateSql);
        pstatUpdate.setInt(1, profileId);
        pstatUpdate.setInt(2, userId);
        pstatUpdate.executeUpdate();  
        lastError = null;
        user = new User(p.getUsername(), userId);
    } catch (SQLException sqle) {
        lastError = sqle.getMessage();
    } finally {
        if (userRs != null)
            try { userRs.close(); } catch (SQLException sqle) {}
        if (profRs != null)
            try { profRs.close(); } catch (SQLException sqle){}
        if (pstatUser != null)
            try { pstatUser.close(); } catch (SQLException sqle) {}
        if (pstatProf != null)
            try { pstatProf.close(); } catch (SQLException sqle) {}
        if (pstatUpdate != null)
            try { pstatUpdate.close(); } catch (SQLException sqle) {}
    }
    return user;
}
    
    /** Writes a pre-validated Achievement to the database and returns
     * an ID for that achievement.  If anything goes wrong, the ID will
     * be negative and you can find error information using getLastError()
     * @param user object that this achievement belongs to
     * @param achievement object for the achievement
     * @return achievementID (or -1 if error)
     */
    public int addAchievement (Achievement achievement) {        
        String sql = "INSERT INTO ACHIEVEMENTS (exercise,duration,";
        sql += "intensity, score, notes,userid,dateoccurred) ";
        sql += "VALUES (?,?,?,?,?,?,?)";       
        PreparedStatement pstat = null;
        ResultSet rs = null;
        int achievementId = -1;
        
        if (!achievement.validate()) {
            lastError = "Invalid achievement";
            return -1; //controller should already do this, but check anyway
        }                                                 
        try {
            pstat = CONN.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstat.setString(1, achievement.getActivity().toString());
            pstat.setInt(2, achievement.getMinutes());
            pstat.setString(3, achievement.getIntensity().toString());
            pstat.setInt(4, achievement.getScore());
            pstat.setString(5, achievement.getNotes());
            pstat.setInt(6, achievement.getUser().getUserId());
            pstat.setDate(7, new java.sql.Date (achievement.getActivityDate().getTime()));
            pstat.executeUpdate();
            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                achievementId = rs.getInt(1);
                lastError = null;
            }
            else
                lastError = "Unable to save achievement";                        
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
        } finally {
            if (rs != null)
                try { rs.close(); } catch (SQLException sqle) {}            
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}            
        }
        return achievementId;        
    }
    
    public List<Achievement> getAllAchievementsByDate() {
        return getAchievementsByDate ("%");
    }
    
    public User getUserById (int userId) {
        User user = null;
        String sql = "SELECT * FROM USERS WHERE ID = " + userId;
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getInt("id"));
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

    public List<Achievement> getAchievementsByDate(String username) {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "SELECT * FROM Achievements JOIN Users ON Achievements.userid = ";
        sql += "Users.id WHERE username LIKE '%s' ORDER BY dateoccurred DESC";
        sql = String.format(sql, username);
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
                String exercise = rs.getString("exercise");
                int minutes = rs.getInt("duration");
                String intensity = rs.getString("intensity");
                int score = rs.getInt("score");
                String notes = rs.getString("notes");
                Date dateOccurred = new Date(rs.getDate("dateoccurred").getTime());
                Date dateRecorded = new Date(rs.getDate("daterecorded").getTime());
                
                Activity objActivity = new Activity (exercise);
                Intensity objIntensity = new Intensity (intensity);
                
                Achievement achievement = new Achievement (objActivity, minutes,
                        objIntensity, score, notes, dateOccurred, dateRecorded);                
                User user = getUserById(rs.getInt("userid"));
                achievement.setUser(user);
                achievements.add(achievement);
            }
            lastError = null;
        } catch (SQLException sqle) {
            achievements = null;
            lastError = sqle.getMessage();
        }
          catch (Exception e) {
              //something else went wrong in trying to make the model objects
              lastError = "Error parsing database entry";
              achievements = null;
          }
            finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException sqle) {}
            if (stat != null)
                try {
                    stat.close();
                } catch (SQLException sqle) {}            
        }
        return achievements;   
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
