package com.myurlname.stepup;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gabriel.hashingtool.*;

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
        HashingTool ht =  new Sha256Hasher();     
        String sql = "SELECT * FROM USERS WHERE USERNAME = '" + username + "'";
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (ht.isMatch(password, storedHash)) {
                    user = new User(rs.getString("username"), rs.getInt("userid"),
                                    rs.getInt("badgelevel"),
                                    rs.getInt("badgehabit"));
                }
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
    PreparedStatement pstatUser = null, pstatProf = null;
    ResultSet userRs = null, profRs = null;
    int userId = 0, profileId = 0;
    User user = null;
    HashingTool ht = new Sha256Hasher();
    if (!p.validateRegistration()) return null; //controller should already
                                            //do this, but check anyway
    try {
        pstatUser = CONN.prepareStatement(userSql,
                                          Statement.RETURN_GENERATED_KEYS);
        pstatUser.setString(1, p.getUsername());
        pstatUser.setString(2, ht.getHash(p.getPassword1()));
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
    }
    return user;
}

    /** Reads a profile for a given userID and creates a Profile object
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
                //since phone is a CHAR and not VARCHAR, it might be all spaces
                //let's get rid of that if possible
                if (profile.getPhone() != null) 
                    profile.setPhone(profile.getPhone().trim());
                profile.setProfileId(rsData.getInt("profileid"));
                Blob picBlob = rsData.getBlob("picture");
                if (picBlob != null) {
                    profile.setImageData(picBlob.getBytes(1,(int)picBlob.length()));
                    profile.setImageType(rsData.getString("pictype"));
                }
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
     * an acheivementID for that achievement.  If anything goes wrong, the achievementID will
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
     * @param userId userID value
     * @param Badge object for the badge levels
     * @return -1 if error, anything else for pass
     */
    public int updateBadge (int userId, Badge badge) {
        String sql = "UPDATE Users SET badgelevel=?,badgehabit=? WHERE userid=?";
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
     * Creates a new Post in the database.  Returns the postID if successful
     * otherwise, returns -1
     * @param post (Post object)
     * @return postID or -1 if unsuccessful
     */
    public int createPost(Post post) {
        //front controller should do this, but double check to be sure
        if (!post.isPostValid()) {
            this.lastError = "Tried to create a post with invalid post object";
            return -1;
        }                
        String sql = "INSERT INTO Posts (content,authorid,postdate,squadid) ";
        sql += "VALUES (?,?,?,?)";
        PreparedStatement pstat = null;
        ResultSet rs = null;
        int postId = -1;
        try {
            pstat = CONN.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstat.setString(1,post.getContent());
            pstat.setInt(2, post.getUserId());
            pstat.setLong(3, post.getPostDate().getTime());
            pstat.setInt(4, post.getSquadId());
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
        return postId;
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
            //need to fill in the profileID & joinDate from dbase as well as read back to verify
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
     * Note that the date recorded and achievementID fields will never change.
     * @param ach Achievement object that holds the achievementID and fields to which
     * to update in the Achievement dbase table.
     * @return null if error, returns back the ach object if successful
     */
    public Achievement updateAchievement (Achievement ach) {
        String sql = "UPDATE ACHIEVEMENTS SET exercise=?,duration=?,"
                + "intensity=?,score=?,notes=?,dateoccurred=? WHERE achievementid = ?";
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
     * @param ach Achievement object that holds the achievementID of the achievement
     * to delete.
     * @return null if error, returns back the ach object if successful
     */
    public Achievement removeAchievement (Achievement ach) {
        String sql = "DELETE FROM ACHIEVEMENTS WHERE achievementid = " +
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
        String sql = "SELECT * FROM USERS WHERE USERID = " + userId;
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getInt("userid"),
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
                user = new User(rs.getString("username"), rs.getInt("userid"),
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
        return getAchievementsByDate ("%", -1);
    }
    
    public List <Achievement> getAllAchievementsByDate (int squadId) {
        return getAchievementsByDate ("%", squadId);
    }    

    public List<Achievement> getAchievementsByDate(String username, int squadId) {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "";
        if (squadId < 0) {
            sql = "SELECT * FROM Achievements JOIN Users ON Achievements.userid = ";
            sql += "Users.userid WHERE username LIKE '%s' ORDER BY dateoccurred DESC";
            sql = String.format(sql, username);
        }
        else {
            sql = "SELECT * FROM Achievements JOIN Users ON Achievements.userid = ";
            sql += "Users.userid JOIN Squadmembers ON Users.userid=Squadmembers.memberid ";
            sql += "WHERE username LIKE '%s' AND Squadmembers.squadid = %d ";
            sql += "AND NOT Squadmembers.isinvited ORDER BY dateoccurred DESC";
            sql = String.format(sql, username, squadId);            
        }
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
                int achievementID = rs.getInt("achievementid");
                Activity objActivity = new Activity (exercise);
                Intensity objIntensity = new Intensity (intensity);

                Achievement achievement = new Achievement (objActivity, minutes,
                     objIntensity, score, notes, dateOccurred, dateRecorded,achievementID);
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
      
    public boolean isUserInSquad ( int userId, int squadId) {
        List <SquadMembership> squads = getSquadMemberships (userId);
        return squads.stream().anyMatch(sm -> (sm.getSquadId() == squadId && !sm.getIsInvited()));            
    }
    
    public boolean isUserInvitedToSquad ( int userId, int squadId) {
        List <SquadMembership> squads = getSquadMemberships (userId);
        return squads.stream().anyMatch(sm -> (sm.getSquadId() == squadId && sm.getIsInvited()));            
    }    
    
    public boolean isUserInvitedToOrMemberOfSquad ( int userId, int squadId) {
        List <SquadMembership> squads = getSquadMemberships (userId);
        return squads.stream().anyMatch(sm -> (sm.getSquadId() == squadId));            
    }       
    
    public List<SquadMembership> getAllSquadMembers (int squadId) {
        //Could probably combine this with the getSquadMembership method
        List<SquadMembership> memberships = new ArrayList<>();
        String sql = "SELECT sm.SQUADID, SQUADS.squadname, SQUADS.SQUADID, owners.USERNAME ownername, sm.ISINVITED, sm.ISOWNER, mynames.USERNAME membername, mynames.USERID memberid ";
        sql += "FROM SQUADMEMBERS sm JOIN SQUADS ON sm.squadid = Squads.squadid JOIN USERS owners ON owners.userid = squads.ownerid ";
        sql += "JOIN USERS mynames ON mynames.userid =  sm.memberid ";
        sql += "WHERE sm.SQUADID = %d ORDER BY membername ASC";
        sql = String.format(sql, squadId);
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
                String squadname = rs.getString("squadname");
                int squadid = rs.getInt("squadid");
                String ownerName = rs.getString("ownername");
                boolean isInvited = rs.getBoolean("isinvited");
                boolean isOwner = rs.getBoolean("isowner");
                String memberName = rs.getString("membername");
                int memberId = rs.getInt("memberid");
                SquadMembership sm = new SquadMembership (squadid,squadname,isOwner, isInvited, memberId,memberName,ownerName);                    
                memberships.add(sm);
            }
            lastError = null;
        } catch (SQLException sqle) {
            memberships = null;
            lastError = sqle.getMessage();
        }
          catch (Exception e) {
              //something else went wrong in trying to make the invitations
              lastError = "Error parsing database entry";
              memberships = null;
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
        return memberships;        
    }    

    public List<SquadMembership> getSquadMemberships (int userId) {

        List<SquadMembership> memberships = new ArrayList<>();      
        String sql = "SELECT sm.MEMBERID, sm.SQUADID, SQUADS.squadname, SQUADS.SQUADID, owners.USERNAME ownername, sm.ISINVITED, sm.ISOWNER, mynames.USERNAME membername ";
        sql += "FROM SQUADMEMBERS sm JOIN SQUADS ON sm.squadid = Squads.squadid JOIN USERS owners ON owners.userid = squads.ownerid ";
        sql += "JOIN USERS mynames ON mynames.userid = %d ";
        sql += "WHERE sm.memberid = %d ORDER BY squads.squadname ASC";
        sql = String.format(sql, userId, userId);
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
                String squadname = rs.getString("squadname");
                int squadid = rs.getInt("squadid");
                String ownerName = rs.getString("ownername");
                boolean isInvited = rs.getBoolean("isinvited");
                boolean isOwner = rs.getBoolean("isowner");
                String memberName = rs.getString("membername");
                SquadMembership sm = new SquadMembership (squadid,squadname,isOwner, isInvited, userId, memberName,ownerName);                    
                memberships.add(sm);
            }
            lastError = null;
        } catch (SQLException sqle) {
            memberships = null;
            lastError = sqle.getMessage();
        }
          catch (Exception e) {
              //something else went wrong in trying to make the invitations
              lastError = "Error parsing database entry";
              memberships = null;
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
        return memberships;        
    }    

    //returns true if operation successful, false if not.
    public boolean joinSquad (int userId, int squadId) {
        //first verify user is invited to the squad
        if (!isUserInvitedToSquad (userId, squadId)) {
            lastError = "user cannot join a squad they are not invited to!";
            return false;
        }
        //now "join" the squad by setting isInvited to false, thereby 'accepting' in the invitation           
        String sql = "UPDATE squadmembers SET isinvited = ? WHERE memberid = ? AND squadid = ?";        
        PreparedStatement pstat = null;
        try {
            pstat = CONN.prepareStatement(sql);
            pstat.setBoolean(1, false);            
            pstat.setInt(2, userId);
            pstat.setInt(3, squadId);
            pstat.executeUpdate();
            lastError = null;
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();            
        } finally {
            if (pstat != null) try {pstat.close();} catch (SQLException sqle) {}
            return (lastError == null);
        }
    }
    
    
        
    //returns true if operation successful, false if not.
    public boolean removeUserFromSquad (int userId, int squadId) {
        //first verify user is invited to the squad
        if (!isUserInvitedToOrMemberOfSquad (userId, squadId)) {
            lastError = "user already not member of nor invited to that squad";
            return false;
        }
        //now remove user from the squad by deleting all records in the squad membership table
        //belonging to this user and the squadId
        String sql = "DELETE FROM squadmembers WHERE memberid = ? AND squadid = ?";        
        PreparedStatement pstat = null;
        try {
            pstat = CONN.prepareStatement(sql);           
            pstat.setInt(1, userId);
            pstat.setInt(2, squadId);
            pstat.executeUpdate();
            lastError = null;
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();            
        } finally {
            if (pstat != null) try {pstat.close();} catch (SQLException sqle) {}
            return (lastError == null);
        }
    }        
    
    public List<Post> getSortedPostsByDate() {
        return getUsersPostsByDate ("%", -1);
    }
    
    public List <Post> getSortedPostsByDate (int squadId) {
        return getUsersPostsByDate ("%", squadId);
    }

    //Get all posts by a username and optionally by squad ID as well
    //if squadId is negative, then posts from all squad IDs will be returned
    public List<Post> getUsersPostsByDate (String username, int squadId) {
        List<Post> posts = new ArrayList<>();
        String sql = "";
        if (squadId < 0) {
            sql = "SELECT * FROM Posts JOIN Users ON Posts.authorid = ";
            sql += "Users.userid WHERE username LIKE '%s' ORDER BY postdate DESC";
            sql = String.format(sql, username);
        }
        else {
            sql = "SELECT * FROM Posts JOIN Users ON Posts.authorid = ";
            sql += "Users.userid WHERE username LIKE '%s' AND Posts.squadid = %d ORDER BY postdate DESC";
            sql = String.format(sql, username, squadId);
        }
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
                String content = rs.getString("content");
                int authorId = rs.getInt("authorid");
                Date postDate = new Date(rs.getLong("postdate"));
                int postId = rs.getInt("postid");
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

    public void updateImage(int userId, String mime, InputStream is) {
        String sql = "UPDATE Profiles SET pictype = ?, picture = ? WHERE userid = ?";        
        PreparedStatement pstat = null;
        try {
            pstat = CONN.prepareStatement(sql);
            pstat.setString(1, mime);
            pstat.setBlob(2, is);
            pstat.setInt(3, userId);
            pstat.executeUpdate();
            lastError = null;
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();            
        } finally {
            if (pstat != null) try {pstat.close();} catch (SQLException sqle) {}
        }
    }

    /**
     *
     * @param userId - the userID of the logged in User
     * @param followingId - the followingID of the person the User may or may not be following
     * @return Boolean object (true) if User is following 'followingId'.
     * False otherwise.  null if there's a dbase problem
     */
    public Boolean checkForFollowing (int userId, int followingId) {
        String sql = String.format(
                "SELECT * FROM FOLLOWERS WHERE FOLLOWERID=%d AND BEINGFOLLOWEDID=%d",
                userId, followingId);
        Statement stat = null;
        ResultSet rs = null;
        Boolean isFollowing = false;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            if (rs.next()) {
                isFollowing = true;
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
        if (lastError == null)
            return isFollowing;
        else
            return null;
    }

    /**
     * Inserts a row into the Squadmembers table to add a username as invited to a 
     * squad.  Method will check if username exists and if the user is already invited
     * or not.  If username does not exist or user is already invited, method returns -1, otherwise
     * returns squadid back.
     * @param InvitedUser - object holding username and squadid     
     * @return SquadId  if all goes well, -1 if not.
     * there's an error.
     */
    public int inviteUser (InvitedUser iu) {
        //front controller should do this, but double check to be sure
        User invitedUser = getUserByUserName(iu.getInvitedUsername());
        if (invitedUser == null) {
            lastError = "Username does not exist";
            return -1;
        }
        //Now check if squad ID is valid and username isn't already a member or owner
        List<SquadMembership> members = this.getAllSquadMembers (iu.getSquadId());
        if (members == null) {
            lastError = "Squad does not exist or has no owner";
            return -1;
        }
        //check username isn't already a member or owner        
        for (SquadMembership m : members) {
            if (m.getOwnerName().equals(iu.getInvitedUsername())) {
                lastError = "Cannot invite squad owner";
                return -1;
            }
            else if (m.getUserName().equals(iu.getInvitedUsername()) &&
                    m.getIsInvited()) {
                lastError = "Username already has an pending invitation to squad";
                return -1;
            }
            else if (m.getUserName().equals(iu.getInvitedUsername()) &&
                    !m.getIsInvited()) {
                lastError = "Cannot invite existing members";
                return -1;                
            }
        }
        //Now that we know this user isn't the owner, isn't invited already, and isn't a full fledged member,
        //let's do the insert to create  record for him/her!
        String sql = "INSERT INTO Squadmembers (memberid,isowner,isinvited,squadid) ";
        sql += "VALUES (?,?,?,?)";
        PreparedStatement pstat = null;
        ResultSet rs = null;
        int tableId = -1;
        try {
            pstat = CONN.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstat.setInt(1,invitedUser.getUserId());
            pstat.setBoolean(2, false);
            pstat.setBoolean(3, true);
            pstat.setInt(4,iu.getSquadId());
            pstat.executeUpdate();
            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                tableId = rs.getInt(1);
                lastError = null;
            }
            else
                lastError = "Unable to create invitation";
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return -1;
        } finally {
            if (rs != null)
                try { rs.close();} catch (SQLException sqle) {}
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return iu.getSquadId();
    }    
    
    /**
     * Inserts a row into the Followers table to allow a user to follow another
     * user's actions.  Method will check if a relationship already exists and
     * do nothing if it does.
     * @param userId - the logged in UserID
     * @param followingId - the person's userID that the logged in User wants
     * to follow
     * @return followinginstanceID of new row in Followers table if all goes well, -1 if
     * there's an error.
     */
    public int startFollowing (int userId, int followingId) {
        //front controller should do this, but double check to be sure
        if (checkForFollowing(userId,followingId)) return 0;
        if ((getUserById(userId) == null) || (getUserById(followingId) == null)) {
            lastError = "UserID or following userID does not exist";
            return -1;
        }
        String sql = "INSERT INTO Followers (beingfollowedid,followerid) ";
        sql += "VALUES (?,?)";
        PreparedStatement pstat = null;
        ResultSet rs = null;
        int tableId = -1;
        try {
            pstat = CONN.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstat.setInt(1,followingId);
            pstat.setInt(2, userId);
            pstat.executeUpdate();
            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                tableId = rs.getInt(1);
                lastError = null;
            }
            else
                lastError = "Unable to create follower relationship";
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
            return -1;
        } finally {
            if (rs != null)
                try { rs.close();} catch (SQLException sqle) {}
            if (pstat != null)
                try { pstat.close(); } catch (SQLException sqle) {}
        }
        return tableId;
    }

    /**
     * Removes a row into the Followers table to stop a user from following
     * another user's actions.  Method will check if a relationship already
     * exists and do nothing if it does not.
     * @param userId - the logged in UserID
     * @param followingId - the person's userID that the logged in User wants
     * to stop following
     * @return 0 if everything goes well, -1 if there's an error.
     */
    public int stopFollowing (int userId, int followingId) {
        //front controller should do this, but double check to be sure
        if (!checkForFollowing(userId,followingId)) return 0;
        String sql = String.format(
                "DELETE FROM FOLLOWERS "
                        + "WHERE beingfollowedid=%d AND followerid=%d",
                        followingId, userId);
        Statement stat = null;
        int result = -1;
        try {
            stat = CONN.createStatement();
            stat.executeUpdate(sql);
            result = 0;
        } catch (SQLException sqle) {
            lastError = sqle.getMessage();
        } finally {
            if (stat != null)
                try { stat.close(); } catch (SQLException sqle) {}
        }
        return result;
    }


    /**
     * Returns an ArrayList of email addresses for all the people following the
     * User identified by parameter userId
     * @param userId - the user that everyone's email is the List is following
     * @return List<String> email addresses, empty List for none, null for
     * a database problem.
     */
    public List<String> getFollowerEmails (int userId) {
        List<String> emails = new ArrayList<>();
        String sql = "SELECT email FROM Profiles JOIN Followers ON Profiles.userid = ";
        sql += "Followers.followerid WHERE beingfollowedid=%d";
        sql = String.format(sql, userId);
        Statement stat = null;
        ResultSet rs = null;
        try {
            stat = CONN.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
                String email = rs.getString("email");
                if ((email != null) && (email.length()>0))
                    emails.add(email);
            }
            lastError = null;
        } catch (SQLException sqle) {
            emails = null;
            lastError = sqle.getMessage();
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
        return emails;
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
