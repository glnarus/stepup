package com.myurlname.stepup;

/**
 * Class User holds the user object information for a StepUp user.
 * @author gabriel
 */
public class User implements java.io.Serializable {
    private String username;
    private int userId;
    private Profile p;

    public User (String username, int userId) {
        this.username = username; 
        this.userId = userId;
    }
    
    public User (String username, int userId, Profile p) {
        this.username = username;
        this.userId = userId;
        this.p = p;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    
}
