package com.myurlname.stepup;

import java.io.Serializable;

/**
 * NewSquadName is an authenticator for the StepUp webapp.  It follows the
 * following rules:
 * SquadName : 3 to 50 characters long, only letters, digits, and underscore okay.  No SQL or HTML injection allowed
 * @author gabriel
 */
public class NewSquadName implements Serializable {
    private final String newSquadName;
    
    public NewSquadName (String newSquadName) {
        this.newSquadName = newSquadName;        
    }
    
    public String getNewSquadName () {
        return newSquadName;
    }
    
    public boolean validate () {        
        if (!validateSquadname(this.newSquadName)) {
            return false;
        }        
        return true;        
    }
    
    public static boolean validateSquadname (String squadName) {
        if (squadName == null) return false;
        //3 to 50 characters that are letters, numbers and single spaces (no leading or trailing spaces)
        return squadName.matches("^(?=.{3,50}$)(?!.* {2,})[a-zA-Z][a-zA-Z ]*[a-zA-Z]$");
        
    }
      
}