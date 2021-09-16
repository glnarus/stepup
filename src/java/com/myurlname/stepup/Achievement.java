package com.myurlname.stepup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.text.StringEscapeUtils;

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
    private double score;
    private String notes;
    private Date activityDate;
    private Date recordedDate;
    private User user;
    private int achievementId;
    private double activityValue; //how much towards the weekly goal is this?
    private static final double CAP = 1.252;
    private static final double MAX_DAILY_VALUE = 0.3334;

    /**Caution: This constructor is only used from the database
     * and thus does NOT have any character escapes because data in the
     * database is already deemed safe.  ONLY call this from the StepUpDAO
     * object (ie, database).
     * @param activity
     * @param minutes
     * @param intensity
     * @param score
     * @param notes
     * @param activityDate
     * @param recordedDate
     */
    public Achievement (Activity activity, int minutes, Intensity intensity,
                        double score, String notes, Date activityDate,
                                                   Date recordedDate, int id) {

        this.notes = notes;
        this.activityDate = activityDate;
        this.recordedDate = recordedDate;
        this.minutes = minutes;
        this.activity = activity;
        this.intensity = intensity;
        this.score = score;
        this.achievementId = id;
    }

    public Achievement (AchievementBean bean) {
        try {
            activity = new Activity (bean.getActivity());
            intensity = new Intensity (bean.getIntensity());
            minutes = Integer.parseInt(bean.getMinutes());
            score = calculateScore (activity,intensity,minutes);
            if (bean.getNotes() != null) {
                notes = StringEscapeUtils.escapeHtml4(bean.getNotes());
                notes = notes.replace("'", "&#39;");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");//matches format that HTML input type 'date' uses         
            String strDate = bean.getDateActivity();
            if (strDate != null) {
                strDate = strDate.replace('-', '/');
                strDate = strDate.replace('.', '/');
                strDate = strDate.replace('\\', '/');
                activityDate = sdf.parse(strDate);
            }
            strDate = bean.getDateRecorded();
            if (strDate != null) {
                strDate = strDate.replace('-', '/');
                strDate = strDate.replace('.', '/');
                strDate = strDate.replace('\\', '/');
                recordedDate = sdf.parse(strDate);
            }
            else
                recordedDate = new Date();  //not really used, it's set by dbase
        }
        catch (Exception e) {
            //something went wrong with the inputs, so let's null out stuff
            //so that the validator will return false
            activity = null;
            intensity = null;
            minutes = 0;
            score = 0;
            notes = null;
            activityDate = null;
            recordedDate = null;
        }
    }


    public Achievement () {
    }

    public boolean validate () {
        Date today = new Date();
        if (minutes <= 0)
            return false;
        if (notes.length() > 200)
            return false;
        if (recordedDate == null)
            return false;
        if (recordedDate.compareTo(today)> 0)
            return false;
        if (activityDate == null)
            return false;
        if (activityDate.compareTo(today)> 0)
            return false;
        if (activity == null)
            return false;
        if (intensity == null)
            return false;
        return true;
    }

    private double calculateScore (Activity activity, Intensity intensity,
                                                                int minutes) {
        /*the ALGORITHM for calculating the worth of an exercise
        (muscleFactor 1 <=> 10).  We want maximum results when MF is 10.
        This means we take (MuscleFactor / 10)
        We also have the concept of duration.  Duration required to get full
        credit is based on intensity.

        (minutes/IntensityMinutes)
        Multiply the two to get a score between 0 and 1
        score = muscleFactor/10  *  minutes/intensityMinutes
        Cap scores more than 1 to CAP (this allows light/moderate exercises to
        skip a day (do 4 instead of 5) if they exercise a lot)
        Examples:
        Running at moderate intensity for 20 min = 10/10 * 20/30 = 0.66
        Climbing strenuously for 15 minutes = 8/10 * 15/20 = 0.6
        Hiking lightly for 6 hours = 4/10 * 240/30 = CAP (see top of file)
        */
        int muscleF = activity.getMuscleFactor();
        int minMinutes = intensity.getMinimumMinutes();
        double tempScore = muscleF / (double) Activity.ACT_MAXMF;
        tempScore = tempScore * (minutes/ (double) intensity.getMinimumMinutes());
        if (tempScore > CAP)
            tempScore = CAP;
        else if (tempScore < 0)
            tempScore = 0.0;
        return tempScore;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(int achievementId) {
        this.achievementId = achievementId;
    }

    public String getPrettyPrintActivityDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(this.activityDate);

    }

    public String getPrettyPrintRecordedDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(this.recordedDate);

    }

    @Override
    public String toString () {
        String text =
            String.format("%s: %s for %d minutes at %s intensity",
                           getPrettyPrintActivityDate(),this.activity,
                           this.minutes,this.intensity);

        if (!(this.notes == null) && (this.notes.length()>0))
            text = text + " [" + this.notes + "]";
        return text;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        if (score < 0)
            this.score = 0.0;
        else if (score > CAP) {
            this.score = CAP;
        }
        else
            this.score = score;
    }

    public double getActivityValue() {
        return activityValue;
    }

    public void setActivityValue(double activityValue) {
        if (activityValue < 0)
                this.activityValue = 0.0;
        else if (activityValue >  MAX_DAILY_VALUE)
            this.activityValue = MAX_DAILY_VALUE;
        else
            this.activityValue = activityValue;
    }
}
