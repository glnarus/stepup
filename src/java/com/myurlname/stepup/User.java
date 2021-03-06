package com.myurlname.stepup;

/**
 * Class User holds the user object information for a StepUp user.
 * @author gabriel
 */
public class User implements java.io.Serializable {
    private String username;
    private int userId;
    private Badge badge;
    private Profile profile;

    public User (String username, int userId, int level, int habit) {
        this.username = username;
        this.userId = userId;
        this.badge = new Badge (level, habit);
        profile = null;
    }

    public User (String username, int userId, int level, int habit, Profile p) {
        this.username = username;
        this.userId = userId;
        this.badge = new Badge (level, habit);
        profile = p;
    }

    public User () { }

    public User (String username, int userId, Badge badge, Profile p) {
        this.username = username;
        this.userId = userId;
        profile = p;
        this.badge = badge;
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

    @Override
    public String toString () {
        return this.username;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
