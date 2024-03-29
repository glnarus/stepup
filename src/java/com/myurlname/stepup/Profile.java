package com.myurlname.stepup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Profile is an object for the user's profile data.  It also has validation
 * functionality of all profile data when used to register new users or
 * to update a profile.
 * If a profile fails authentication, the errorMessage variable will hold
 * all fields that have errors - do a check for contains to see if the
 * relevant field had a problem.
 * username, password1, password2, firstname, lastname, email, phone
 * are the only fields checked.  All others are altered for sql/html injection
 * removal, but not verified against any rules.  These fields may also be null.
 * NOTE: all are in lower case.
 * @author gabriel
 */
public class Profile implements Serializable {
    private String username;
    private String password1;
    private String password2;
    private String firstName;
    private String lastName;
    private Date joinDate;
    private String email;
    private String phone;
    private String goal;
    private String reward;
    private String emailSubscribe;
    private String textSubscribe;
    private int profileId;
    private int userId;
    private String errorMessage;
    private List<String> errors;
    private byte[] imageData;
    private String imageType;

    public Profile (RegistrationBean r) {
        this.username = r.getUsername();
        this.password1 = r.getPassword1();
        this.password2 = r.getPassword2();
        this.firstName = r.getFirstName();
        this.lastName = r.getLastName();
        this.email = r.getEmail();
        this.phone = r.getPhone();
        this.goal = r.getGoal();
        this.reward = r.getReward();
        this.emailSubscribe = r.getEmailSubscribe();
        this.textSubscribe = r.getTextSubscribe();
        joinDate = new Date();
        errorMessage = "";
        errors = new ArrayList<>();
    }

    public Profile (User user,
                    String firstName, String lastName, String email,
                    String phone, String goal, String reward,
                    String emailSubscribe, String textSubscribe) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.goal = goal;
        this.reward = reward;
        this.emailSubscribe = emailSubscribe;
        this.textSubscribe = textSubscribe;
        joinDate = new Date();
        errorMessage = "";
        errors = new ArrayList<>();
    }

    public Profile (User user,
                    String firstName, String lastName, String email,
                    String phone, String goal, String reward,
                    String emailSubscribe, String textSubscribe,
                    Date joinDate) {
        this (user,firstName, lastName, email,phone,  goal,  reward,
                     emailSubscribe,textSubscribe);
        this.joinDate = joinDate;
    }

    public Profile (int userId, String userName,
                    String firstName, String lastName, String email,
                    String phone, String goal, String reward,
                    String emailSubscribe, String textSubscribe) {
        this(new User (userName, userId, 0, 0),firstName, lastName, 
             email,phone, goal, reward, emailSubscribe, textSubscribe );
    }

    public boolean validateRegistration () {
        return validate(true);
    }

    public boolean validateProfileUpdate () {
        return validate(false);
    }

    /**Checks all required fields are valid, and for non-required fields,
     * replace SQL/HTML control characters.  If there are issues, it can be
     * found in the getErrorMessage() method; all issues will be listed there
     * back to back (password, firstname, lastname, email, phone)
     * @return boolean (true = valid, false= invalid)
     */
    public boolean validate (boolean isRegistration) {
        if (isRegistration) {
            if (!LoginBean.validateUsername(username))
                    setErrorMessage("username");

            if ((!LoginBean.validatePassword(password1))
                || (!password1.equals(password2)))
                    addErrorSource("password");
        }
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if ((firstName == null) || (!firstName.matches("^[A-Za-z. -]{1,20}$")))
            addErrorSource("firstname");
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if ((lastName == null) || (!lastName.matches("^[A-Za-z -']{1,30}$")))
            addErrorSource("lasttname");

        if (email != null) {
            email = email.trim();
        }
        if ((email != null) && (email.length()>0)) {
            EmailValidator ev = EmailValidator.getInstance(false);
            if (!ev.isValid (email))
                addErrorSource("email");
        }
        if (phone != null) {
            phone = phone.trim();
        }
        if ((phone != null) && (phone.length()>0)) {
            if (phone.matches("^\\d{3}-\\d{3}-\\d{4}$")) {
                //this is the format we want, leave it alone
            }
            else if (phone.matches("^\\d{10}$")) {
                //these are just digits, let's insert the dashes
                phone = String.format("%s-%s-%s",
                                       phone.substring(0,3),
                                       phone.substring(3,6),
                                       phone.substring(6,10));
            }
            else
                addErrorSource("phone");
        }
        if (goal != null) {
            goal = goal.trim();
        }
        if ((goal != null) && (goal.length()>0)) {
            if (goal.length()>200)
                addErrorSource("goal");
            else {
                goal = StringEscapeUtils.escapeHtml4(goal);
                goal = goal.replace("'", "&#39;");
            }
        }
        if (reward != null) {
            reward = reward.trim();
        }
        if ((reward != null) && (reward.length()>0)) {
            if (reward.length() > 200)
                addErrorSource("reward");
            else {
                reward = StringEscapeUtils.escapeHtml4(reward);
                reward = reward.replace("'", "&#39;");
            }
        }

        return (getErrorMessage()==null);         

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

    public Date getJoinDate() {
        return joinDate;
    }

    public String getPrettyPrintJoinDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(this.joinDate);

    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
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

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getErrorMessage() {
        //return a human readable version of the error
        if (errors.isEmpty()) return null;
        else {
            StringBuilder sb = new StringBuilder("Required information missing and/or entered incorrectly: ");
            for (int i=0; i< errors.size(); i++) {
                if (i == 0) sb.append (errors.get(i));
                else {
                sb.append(",");
                sb.append(errors.get(i));
                }
            }
            return sb.toString();
        }
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void addErrorSource(String newError) {
        this.errors.add(newError);
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

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

}
