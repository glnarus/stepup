package com.myurlname.stepup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Achievement containts an activity, date accomplished,
 * duration (minutes), intensity level, and notes
 * Class also has a validator method to ensure inputs are valid data
 * @author gabriel
 */
public class Achievement implements Serializable {
    private Activity activity;
    private int minutes;
    private Intensity intensity;
    private String notes;
    private Date activityDate;
    private Date recordedDate;
    
    public Achievement (Activity activity, int minutes, Intensity intensity,
                        String notes, Date activityDate, Date recordedDate) {
        
        if (notes != null) {
            notes = StringEscapeUtils.escapeHtml4(notes);
            this.notes = notes.replace("'", "&#39;");    
        }
        this.activityDate = activityDate;
        this.recordedDate = recordedDate;
        this.minutes = minutes;
        this.activity = activity;
        this.intensity = intensity;            
    }          
    
    public Achievement (AchievementBean bean) {
        try { 
            activity = new Activity (bean.getActivity());
            intensity = new Intensity (bean.getIntensity());
            minutes = Integer.parseInt(bean.getMinutes());
            if (bean.getNotes() != null) {
                notes = StringEscapeUtils.escapeHtml4(bean.getNotes());
                notes = notes.replace("'", "&#39;");    
            }            
            activityDate = new Date(bean.getDateActivity());
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
            recordedDate = formatter.parse(bean.getDateActivity());
        }
        catch (Exception e) {
            //something went wrong with the inputs, so let's null out stuff
            //so that the validator will return false
            activity = null;
            intensity = null;
            minutes = 0;
            notes = null;
            activityDate = null;
            recordedDate = null;
        }
    }
    
    public Achievement () {     
    }

    public boolean validate () {
        if (minutes <= 0) 
            return false;
        if (notes.length() > 200) 
            return false;
        if (recordedDate == null)
            return false;
        if (activityDate == null)
            return false;
        if (activity == null)
            return false;
        if (intensity == null)
            return false;              
        return true; 
    }
    
    
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    
    public void setActivity(String activity) {
        this.activity = new Activity (activity);
    }    

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }
    
    public void setIntensity(String intensity) {
        this.intensity = new Intensity(intensity);
    }    
    

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes != null) {
            notes = StringEscapeUtils.escapeHtml4(notes);
            this.notes = notes.replace("'", "&#39;");    
        }
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Date recordedDate) {
        this.recordedDate = recordedDate;
    }
}
