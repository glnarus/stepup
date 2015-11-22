package com.myurlname.stepup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Has a static method accepting a list of achievements and today's date
 * From there it will look through the list of achievements and calculate
 * a Badge object to return.  Badge object reflects current week's status as
 * well as an enhancement if there has been six weeks of at least 2X per week
 * activity.
 * lifetime
 * @author gabriel
 */
public class BadgeCalculator {
    private final static long WEEK_MS = 604800000L;
    private final static long TEN_DAYS_MS = 864000000L;
    private final static long ONE_DAY_MS = 86400000L;

    public static Badge calculateBadge (List<Achievement> achievements, Date today) {
        Badge badges [] = calculateLastSixWeeksBadges (achievements, today);
        //now iterate through the past six weeks and figure out the habit
        int lowestLevel = Integer.MAX_VALUE;
        for (int i = 0; i < badges.length; i ++) {
           if (badges[i].getBadgeLevel() < lowestLevel)
               lowestLevel = badges[i].getBadgeLevel();

        }
        badges[0].setBadgeHabit(lowestLevel);
        return badges[0];
    }

    public static List<Integer>
        getSixWeeksHistory (List<Achievement> achievements, Date today) {
        Badge badges [] = calculateLastSixWeeksBadges (achievements, today);
        List <Integer> scoreHistory = new ArrayList<>();
        for (Badge b : badges) {
            scoreHistory.add(b.getBadgeLevel());
        }
        return scoreHistory;
    }

    public static Badge[] calculateLastSixWeeksBadges (List<Achievement> achievements, Date today) {
        //badge level corresponds to only this past week.
        //badge habit corresponds to last six weeks.
        List<Achievement> weekAchv;
        if (today == null) today = new Date();
        Badge badges [] = new Badge [6];
        for (int i = 0; i < badges.length; i ++)
            badges[i] = new Badge();
        double total = 0.0;
        for (int i = 0; i < 6; i ++) {
            weekAchv = getPriorWeekAchievements(achievements,today,i);
            if ((weekAchv == null) || (weekAchv.isEmpty())) {
                badges[i].setBadgeLevel(Badge.LOWEST_LEVEL);
            }
            else {

                for (Achievement a : weekAchv) {
                    /*sum the prior week's achievements while weighting based on
                    Intensity Level to account for required days of activity per week.
                    Hard/Strenuous activity counts as 1/3, each moderate or light as 1/5
                    The 1/3 and 1/5 come from the recommendation to have 3 days or
                    5 days of activity a week depending on intensity.
                    */
                    total += a.getActivityValue();
                }
                setBadgeLevel (badges[i], total);
                //keep tabs on the habit, we need to count the lowest common
                //denominator level for the past 6 weeks and set that to the
                //habit
            }
        }
        return badges;
    }

    private static void setBadgeLevel (Badge badge, double activityLevel) {
        badge.setBadgeLevel(convertToScore(activityLevel));
    }

    //This is used to turn an activity level value (dbl) into a whole
    //number score system
    private static int convertToScore (double activityLevel) {
        /*
        [0] = No badge
        [0-.33] = Level 1
        [.33-.66] = Level 2
        [.66 - ?] = Level 3
        */
        if (activityLevel <= 0.001)
            return 0;
        else if (activityLevel <= 0.33)
            return 1;
        else if (activityLevel <= 0.66)
            return 2;
        else
           return 3;
    }

    //Method gets all Achievements within the past XX ms of today and
    //returns in a list. Useful constants are WEEK_MS and TEN_DAYS_MS
    private static List<Achievement> getPastAchievements
                                (List<Achievement> achievements, Date today,
                                 long lookBackInMs) {

        Date compDate = new Date(today.getTime()-lookBackInMs);
        List <Achievement> retList = new ArrayList<>();
        for (Achievement ach : achievements) {
            if ((ach.getActivityDate().compareTo(compDate) > 0) &&
                (ach.getActivityDate().compareTo(today) <= 0)){
                //if the activity date is later than the compare date, but
                //less than or equal to 'today's date, keep it!
                retList.add(ach);
            }
        }
        return calculateDailyScores(retList);
    }

    //Method will find all achievements with the same activity date and
    //sum their exercise values, taking into account the different intensities
    //and how that relates to how many days per week of said intensity are
    //required.
    //Note the Achievement class enforces a cap on activityValue
    //Method ALTERS parameter achievements
    //This method calculates both an aggregate Score as well as ActivityValue
    //Score is just aggregate muscle & minute factor for exercises, whereas
    //activity value factors in intensity in weighting the value of the exercise
    private static List<Achievement> calculateDailyScores (List<Achievement> achievements) {
        if (achievements == null) return null;
        Date lastDate = new Date();
        lastDate.setTime(42); //make it so lastDate won't match anything in the
                              //first iteration of the loop below
        Achievement lastAch = new Achievement();
        double dailyValue;
        List<Achievement> newList = new ArrayList<>();
        for (Achievement ach : achievements) {
            dailyValue = ach.getScore() *
                         1.0/ach.getIntensity().getDaysPerWeekRequired();
            if (ach.getActivityDate().getTime() == lastDate.getTime()) {
                lastAch.setActivityValue(lastAch.getActivityValue() + dailyValue);
                lastAch.setScore(lastAch.getScore() + ach.getScore());
                //now that we've added the same days activity value
                //notice how we don't add this to newList, it is effectively
                //skipped
            }
            else {
                lastDate.setTime(ach.getActivityDate().getTime());
                ach.setActivityValue(ach.getScore() *
                                 1.0/ach.getIntensity().getDaysPerWeekRequired());
                lastAch = ach;
                newList.add(ach);
            }
        }
        return newList;
    }

    /*
     * Calculates a List of Achievements from a particular week.
     * For this week (ie, the past 6 days plus today), set numWeeksPrior to 0.
     * For last week, set numWeeksPrior to 1, and so on
     * @param achievements [the achievements history you want searched]
     * @param today [today's date]
     * @param numWeeksPrior [how many week's prior to get?]
     * @return a sublist with just that week's achievements
     */
    private static List<Achievement> getPriorWeekAchievements
                                (List<Achievement> achievements, Date today,
                                 long numWeeksPrior) {

        List <Achievement> retList = new ArrayList<>();
        Date iDate = new Date(today.getTime());
        iDate.setTime(today.getTime() - (WEEK_MS * numWeeksPrior));
        return getPastAchievements(achievements,iDate, WEEK_MS);
   }

    /**
     * Calculates a List of ints which are the scores for the last 10 days
     * (ie, the past 9 days plus today)
     * @param achievements [the achievements history you want searched]
     * @param today [today's date; use this to pull different 10 day chunks]
     *              [if null, this will default to NOW]
     * @return a sublist with the scores of the last 10 days of combined
     *    achievements, meaning multiple activities on one day are
     *    combined into one score.  Scores are returned where the first
     *    item is the list is the most recent, and the last is 9 days ago.
     */
public static List<Integer>
        getTenDaysOfScores (List<Achievement> achievements, Date today) {
        if (today == null) today = new Date();
        //convert date to simple MM/DD/YYYY format so it is aligned to the DAY
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        today = new Date(sdf.format(today));
        List <Achievement> achList = getPastAchievements(achievements,today,
                                                          TEN_DAYS_MS);
        List <Integer> scores = new ArrayList<>();
        //initialize the array with zeros
        for (int i = 0; i < 10; i++) scores.add(0);
        for (Achievement ach : achList) {
            long diff = today.getTime() - ach.getActivityDate().getTime();
            int numDays = (int)(diff/ONE_DAY_MS);
            scores.set(numDays, convertToScore(ach.getActivityValue()));
        }
        return scores;
        }

}
