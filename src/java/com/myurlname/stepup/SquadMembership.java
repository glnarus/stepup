/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myurlname.stepup;

/**
 * Encapsulates info about a single squad for a user - whether they are a member, invited, or own it,
 * and who is the owner.
 * @author Gabriel
 */
public class SquadMembership {
    
    private int squadId;
    private String squadName;
    private String ownerName;
    private boolean isOwner;
    private boolean isInvited;
    private int userId;
    private String userName;
    
    public SquadMembership (int squadId, String squadName, boolean isOwner, boolean isInvited, int userId, String userName, String ownerName) {
        this.squadId = squadId;
        this.isInvited = isInvited;
        this.isOwner = isOwner;
        this.squadName = squadName;
        this.userId = userId;
        this.userName = userName;
        this.ownerName = ownerName;
    }

    /**
     * @return the squadId
     */
    public int getSquadId() {
        return squadId;
    }

    /**
     * @param squadId the squadId to set
     */
    public void setSquadId(int squadId) {
        this.squadId = squadId;
    }

    /**
     * @return the squadName
     */
    public String getSquadName() {
        return squadName;
    }

    /**
     * @param squadName the squadName to set
     */
    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    /**
     * @return the isOwner
     */
    public boolean getIsOwner() {
        return isOwner;
    }

    /**
     * @param isOwner the isOwner to set
     */
    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    /**
     * @return the isInvited
     */
    public boolean getIsInvited() {
        return isInvited;
    }

    /**
     * @param isInvited the isInvited to set
     */
    public void setIsInvited(boolean isInvited) {
        this.isInvited = isInvited;
    }

    /**
     * @return the userid
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserId(int userid) {
        this.userId = userid;
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}
