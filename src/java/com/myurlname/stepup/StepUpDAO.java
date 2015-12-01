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
                user = new User(rs.getString("username"), rs.getInt("id"),
                                rs.getInt("badgelevel"),
                                rs.getInt("badgehabit"));
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
     * @param p object
     * @return User object
     */
    public User register(Profile p) {
    String userSql = "INSERT INTO USERS (username,password) VALUES (?,?)";
    String profSql = "INSERT INTO PROFILES (firstname,lastname,";
    profSql += "email,phone,userid,goal,reward,joindate) VALUES (?,?,?,?,?,?,?,?)";
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
        pstatProf.setLong(8, new Date().getTime());
        pstatProf.executeUpdate();

        profRs = pstatProf.getGeneratedKeys();
        if (profRs.next())
            profileId = profRs.getInt(1);
        pstatUpdate = CONN.prepareStatement(updateSql);
        pstatUpdate.setInt(1, profileId);
        pstatUpdate.setInt(2, userId);
        pstatUpdate.executeUpdate();
        lastError = null;
        user = new User(p.getUsername(), userId,
                        Badge.LOWEST_LEVEL, Badge.LOWEST_HABIT, p);
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

    /** Reads a profile for a given user ID and creates a Profile object
     * based on the Profile table.
     * @param user user object for the profile you want to pull up
     * @return Profile object (null if error)
     */
    public Profile getProfileFor(User user) {
        Profile profile = null;
        String sql = "SELECT * FROM PROFILES WHERE USERID = ?";
        PreparedStatement pstat = null;
        ResultSet rsData = null;
        int profileId = 0;
        try {
            pstat = CONN.prepareStatement(sql);
            pstat.setInt(1, user.getUserId());
            pstat.executeQuery();
            rsData = pstat.getResultSet();
            if (rsData.next()) {
                profile = new Profile (user,
                                       rsData.getString("firstname"),
                                       rsData.getString("lastname"),
                                       rsData.getString("email"),
                                       rsData.getString("phone"),
                                       rsData.getString("goal"),
                                       rsData.getString("reward"),
                                       rsData.getString("emailsubscribe"),
                                       rsData.getString("textsubscribe"),
                          new Date(rsData.getLong("joindate")));
                profile.setProfileId(rsData.getInt("id"));
            }
            else
                lastError = "Unable to retrieve profile";
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
        } finally {
            if (rsData != null)
                try { rsData.close(); } catch (SQLException sqle) {}
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return profile;
    }

    /** Writes a pre-validated Achievement to the database and returns
     * an ID for that achievement.  If anything goes wrong, the ID will
     * be negative and you can find error information using getLastError()
     * @param achievement object for the achievement
     * @return achievementID (or -1 if error)
     */
    public int addAchievement (Achievement achievement) {
        String sql = "INSERT INTO ACHIEVEMENTS (exercise,duration,";
        sql += "intensity, score, notes,userid,dateoccurred,daterecorded) ";
        sql += "VALUES (?,?,?,?,?,?,?,?)";
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
            pstat.setDouble(4, achievement.getScore());
            pstat.setString(5, achievement.getNotes());
            pstat.setInt(6, achievement.getUser().getUserId());
            pstat.setLong(7, achievement.getActivityDate().getTime());
            pstat.setLong(8, new Date().getTime());
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

    /** Updates the badge level/habit for a user.  Use this after adding
     * an achievement, for example.
     * @param userId user ID value
     * @param Badge object for the badge levels
     * @return -1 if error, anything else for pass
     */
    public int updateBadge (int userId, Badge badge) {
        String sql = "UPDATE Users SET badgelevel=?,badgehabit=? WHERE id=?";
        PreparedStatement pstat = null;
        try {
            pstat = CONN.prepareStatement(sql);
            pstat.setInt(1,badge.getBadgeLevel());
            pstat.setInt(2, badge.getBadgeHabit());
            pstat.setInt(3, userId);
            pstat.executeUpdate();
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return -1;
        } finally {
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return 0;
    }

    /**
     * Creates a new Post in the database.  Returns the post ID if successful
     * otherwise, returns -1
     * @param post (Post object)
     * @return post ID or -1 if unsuccessful
     */
    public int createPost(Post post) {
        //front controller should do this, but double check to be sure
        if (!post.isPostValid()) {
            this.lastError = "Tried to create a post with invalid post object";
            return -1;
        }
        String sql = "INSERT INTO Posts (content,authorid,postdate) ";
        sql += "VALUES (?,?,?)";
        PreparedStatement pstat = null;
        ResultSet rs = null;
        int postId = -1;
        try {
            pstat = CONN.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstat.setString(1,post.getContent());
            pstat.setInt(2, post.getUserId());
            pstat.setLong(3, post.getPostDate().getTime());
            pstat.executeUpdate();
            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                postId = rs.getInt(1);
                lastError = null;
            }
            else
                lastError = "Unable to save achievement";
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return -1;
        } finally {
            if (rs != null)
                try { rs.close();} catch (SQLException sqle) {}
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return 0;
    }

    /** Updates the profile for a user.  This must be done for
     * pre-existing users, user the register method for new users.
     * Note that the username and password fields will NOT be changed.
     * @param user user object.
     * @param profile object for the new Profile
     * @return null if error, returns user object with new profile object
     * attached if successful
     */
    public User updateProfile (User user, Profile profile) {
        String sql = "UPDATE PROFILES SET firstname=?,lastname=?,"
                + "email=?,phone=?,goal=?,reward=?,emailsubscribe=?,"
                + "textsubscribe=? WHERE userid = ?";
        PreparedStatement pstat = null;
        try {
            pstat = CONN.prepareStatement(sql);
            pstat.setString(1,profile.getFirstName());
            pstat.setString(2,profile.getLastName());
            pstat.setString(3,profile.getEmail());
            pstat.setString(4,profile.getPhone());
            pstat.setString(5,profile.getGoal());
            pstat.setString(6,profile.getReward());
            pstat.setString(7,profile.getEmailSubscribe());
            pstat.setString(8,profile.getTextSubscribe());
            pstat.setInt(9, user.getUserId());
            pstat.executeUpdate();
            //this was successful, so let's update the user's profile object,
            //need to fill in the profile ID & joinDate
            profile = getProfileFor (user);
            if (profile == null) return null;
            user.setProfile(profile);

        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return null;
        } finally {
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return user;
    }


    /** Updates an Achievement for a user.
     * Note that the date recorded and ID fields will never change.
     * @param ach Achievement object that holds the ID and fields to which
     * to update in the Achievement dbase table.
     * @return null if error, returns back the ach object if successful
     */
    public Achievement updateAchievement (Achievement ach) {
        String sql = "UPDATE ACHIEVEMENTS SET exercise=?,duration=?,"
                + "intensity=?,score=?,notes=?,dateoccurred=? WHERE id = ?";
        PreparedStatement pstat = null;
        try {
            pstat = CONN.prepareStatement(sql);
            pstat.setString(1,ach.getActivity().toString());
            pstat.setInt(2,ach.getMinutes());
            pstat.setString(3,ach.getIntensity().toString());
            pstat.setDouble(4,ach.getScore());
            pstat.setString(5,ach.getNotes());
            pstat.setLong(6,ach.getActivityDate().getTime());
            pstat.setInt(7, ach.getAchievementId());
            pstat.executeUpdate();
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return null;
        } finally {
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return ach;
    }

    /** Removes an achievement for a user.
     * @param ach Achievement object that holds the ID of the achievement
     * to delete.
     * @return null if error, returns back the ach object if successful
     */
    public Achievement removeAchievement (Achievement ach) {
        String sql = "DELETE FROM ACHIEVEMENTS WHERE id = " +
                                                ach.getAchievementId();
        Statement stat = null;
        try {
            stat = CONN.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return null;
        } finally {
            if (stat != null)
                try { stat.close(); } catch (SQLException sqle) {}
        }
        return ach;
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
                user = new User(rs.getString("username"), rs.getInt("id"),
                                rs.getInt("badgelevel"),
                                rs.getInt("badgehabit"));
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

    public User getUserByUserName (String username) {
        if (username == null) return null;
        User user = null;
        String sql = "SELECT * FROM USERS WHERE USERNAME = " +
                                                    "'" + username + "'";
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getInt("id"),
                                rs.getInt("badgelevel"),
                                rs.getInt("badgehabit"));
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

    public List<Achievement> getAllAchievementsByDate() {
        return getAchievementsByDate ("%");
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
                double score = rs.getDouble("score");
                String notes = rs.getString("notes");
                Date dateOccurred = new Date(rs.getLong("dateoccurred"));
                Date dateRecorded = new Date(rs.getLong("daterecorded"));
                int id = rs.getInt("id");
                Activity objActivity = new Activity (exercise);
                Intensity objIntensity = new Intensity (intensity);

                Achievement achievement = new Achievement (objActivity, minutes,
                     objIntensity, score, notes, dateOccurred, dateRecorded,id);
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

    public List<Post> getSortedPostsByDate() {
        return getUsersPostsByDate ("%");
    }

    public List<Post> getUsersPostsByDate (String username) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts JOIN Users ON Posts.authorid = ";
        sql += "Users.id WHERE username LIKE '%s' ORDER BY postdate DESC";
        sql = String.format(sql, username);
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
                String content = rs.getString("content");
                int authorId = rs.getInt("authorid");
                Date postDate = new Date(rs.getLong("postdate"));
                int postId = rs.getInt("id");
                String authorName = rs.getString("username");
                Post post = new Post (content, postDate, authorName, authorId,
                                    postId);
                posts.add(post);
            }
            lastError = null;
        } catch (SQLException sqle) {
            posts = null;
            lastError = sqle.getMessage();
        }
          catch (Exception e) {
              //something else went wrong in trying to make the model objects
              lastError = "Error parsing database entry";
              posts = null;
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
        return posts;
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
