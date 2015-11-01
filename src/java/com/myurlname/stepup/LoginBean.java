package com.myurlname.stepup;

import java.io.Serializable;

/**
 * LoginBean is an authenticator for the StepUp webapp.  It follows the
 * following rules:
 * Username : 3 to 15 characters long, only letters, digits, and underscore okay
 * Password : 6 to 15 characters long, no SQL or HTML injection allowed
 * @author gabriel
 */
public class LoginBean implements Serializable {
    private final String username, password;
    
    public LoginBean (String username, String password) {
        this.username = username;
        this.password = password;        
    }
    
    public boolean validate () {        
        if (!validateUsername(username)) {
            return false;
        }
        if (!validatePassword(password)) {
            return false;
        }                      
        return true;        
    }
    
    public static boolean validateUsername (String username) {
        if (username == null) return false;
        return username.matches("^\\w{3,15}$");
        
    }
    
    public static boolean validatePassword (String password) {
        if (password == null) return false;
        return password.matches("^[^'\"&<>]{6,15}$");        
    }    
}
