package com.myurlname.stepup;

/**
 * Class User holds the user object information for a StepUp user.
 * @author gabriel
 */
public class User implements java.io.Serializable {
    private String username;
    private int userId;

    public User (String usernameIn) {
        this.setUsername(usernameIn);        
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
