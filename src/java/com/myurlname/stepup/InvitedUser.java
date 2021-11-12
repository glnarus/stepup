package com.myurlname.stepup;

import org.apache.commons.text.StringEscapeUtils;
/**InvitedUser holds info for a user that we wish to invite to a squad.
 * NOTE: Username goes through HTML and SQL control character replacement whenever
 *  new content is set or a InvitedUser object is created.  
 * @author gabriel
 */
public class InvitedUser implements java.io.Serializable {
    private String invitedUsername;
    private int squadId;

    public InvitedUser(String invitedUsername, int squadId) {
        setContentSafe (invitedUsername);
        this.squadId = squadId;
    }


    public InvitedUser() {}

    public String getInvitedUsername() {
        return invitedUsername;
    }

    public boolean isInvitedUsernameValid () {
        if (getInvitedUsername() == null || getInvitedUsername().length() == 0) return false;        
        return true;
    }


    public int getSquadId() {
        return squadId;
    }

    public final void setContentSafe (String content) {
        //make final to avoid overriding by anybody
        if (content != null) {
            content = StringEscapeUtils.escapeHtml4(content);
            content = content.replace("'", "&#39;");
        }
        this.invitedUsername = content;
    }



    @Override
    public String toString() {
        return String.format("%s invited to squad ID = %d",
                this.getInvitedUsername(), this.getSquadId());
    }


    /**
     * @param squadId the squadId to set
     */
    public void setSquadId(int squadId) {
        this.squadId = squadId;
    }

    /**
     * @param invitedUsername the invitedUsername to set
     */
    public void setInvitedUsername(String invitedUsername) {
        setContentSafe (invitedUsername);
    }
}


