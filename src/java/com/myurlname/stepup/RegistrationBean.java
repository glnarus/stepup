package com.myurlname.stepup;

import java.io.Serializable;


/**
 * Holds registration info so that if a user needs to iterate on the 
 * registration page due to invalid/missing data, he or she will not need to 
 * re-enter already existing information.
 * @author gabriel
 */
public class RegistrationBean implements Serializable {
    private String username;
    private String password1;
    private String password2;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String goal;
    private String reward;
    private String emailSubscribe;
    private String textSubscribe;    
    
    public RegistrationBean (String username, String password1, 
            String password2, String firstName, String lastName, String email,
            String phone, String goal, String reward, 
            String emailSubscribe, String textSubscribe) {
        this.username = username;
        this.password1 = password1;
        this.password2 = password2;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.goal = goal;
        this.reward = reward;
        this.emailSubscribe = emailSubscribe;
        this.textSubscribe = textSubscribe;               
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getEmailSubscribe() {
        return emailSubscribe;
    }

    public void setEmailSubscribe(String emailSubscribe) {
        this.emailSubscribe = emailSubscribe;
    }

    public String getTextSubscribe() {
        return textSubscribe;
    }

    public void setTextSubscribe(String textSubscribe) {
        this.textSubscribe = textSubscribe;
    }
}
