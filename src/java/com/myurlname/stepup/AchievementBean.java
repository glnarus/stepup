package com.myurlname.stepup;

import java.io.Serializable;

/**
 * Stores achievement data in memory so that we can further send it to the 
 * database if it checks out as valid data.  Class also has a validate method 
 * to verify the user entered proper data
 * @author gabriel
 */
public class AchievementBean implements Serializable {
    private String activity;
    private String intensity;
    private String minutes;
    private String notes;
    private String dateActivity;
    private String dateRecorded;

    public AchievementBean () {    
    }
    
    public AchievementBean (String activity, String intensity, String minutes,
                            String notes, String dateActivity) {
        this.activity = activity;
        this.intensity = intensity;
        this.minutes = minutes;
        this.notes = notes;
        this.dateActivity = dateActivity;
    }
    
    public AchievementBean (String activity, String intensity, String minutes,
                            String notes, String dateActivity, String dateRecorded) {
        this (activity, intensity, minutes, notes, dateActivity);
        this.dateRecorded = dateRecorded;
    }    
    
    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDateActivity() {
        return dateActivity;
    }

    public void setDateActivity(String dateActivity) {
        this.dateActivity = dateActivity;
    }

    public String getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(String dateRecorded) {
        this.dateRecorded = dateRecorded;
    }
    
    
    
}
