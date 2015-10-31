package com.myurlname.stepup;

/**
 * LoginBean is an authenticator for the StepUp webapp.  It follows the
 * following rules:
 * Username : 3 to 15 characters long, only letters, digits, and underscore okay
 * Password : 6 to 15 characters long, no SQL or HTML injection allowed
 * @author gabriel
 */
public class LoginBean {
    private final String username, password;
    
    public LoginBean (String username, String password) {
        this.username = username;
        this.password = password;        
    }
    
    public boolean validate () {        
        if (!username.matches("^\\w{3,15}$")) {
            return false;
        }
        if (!password.matches("^[^'\"&<>]{6,15}$")) {
            return false;
        }                      
        return true;        
    }
}
