package com.myurlname.stepup;

import java.util.ArrayList;
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

    public static Badge calculateBadge (List<Achievement> achievements, Date today) {
        //badge level corresponds to only this past week.
        //badge habit corresponds to last six weeks.
        List<Achievement> weekAchv;
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

        //now iterate through the past six weeks and figure out the habit
        int lowestLevel = Integer.MAX_VALUE;
        for (int i = 0; i < badges.length; i ++) {
           if (badges[i].getBadgeLevel() < lowestLevel)
               lowestLevel = badges[i].getBadgeLevel();

        }
        badges[0].setBadgeHabit(lowestLevel);
        return badges[0];
    }

    private static void setBadgeLevel (Badge badge, double score) {
        /*
        [0] = No badge
        [0-.33] = Level 1
        [.33-.66] = Level 2
        [.66 - ?] = Level 3
        */
        if (score <= 0.001)
            badge.setBadgeLevel(0);
        else if (score <= 0.33)
            badge.setBadgeLevel(1);
        else if (score <= 0.66)
            badge.setBadgeLevel(2);
        else
            badge.setBadgeLevel(3);
    }

    //Method gets all Achievements within the past 7 days of today and
    //returns in a list
    private static List<Achievement> getWeeksAchievements
                                (List<Achievement> achievements, Date today) {

        Date compDate = new Date(today.getTime()-WEEK_MS);
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
    private static List<Achievement> calculateDailyScores (List<Achievement> achievements) {
        if (achievements == null) return null;
        Date lastDate = new Date();
        lastDate.setTime(lastDate.getTime() + WEEK_MS); //put this a week in
        //the future to avoid matching the first activity date in the list
        Achievement lastAch = new Achievement();
        double dailyValue = 0.0;
        List<Achievement> newList = new ArrayList<>();
        for (Achievement ach : achievements) {
            dailyValue = ach.getScore() *
                         1/ach.getIntensity().getDaysPerWeekRequired();
            if (ach.getActivityDate().getTime() == lastDate.getTime()) {
                lastAch.setActivityValue(lastAch.getActivityValue() + dailyValue);
                //now that we've added the same days activity value
                //notice how we don't add this to newList, it is effectively
                //skipped
            }
            else {
                lastDate.setTime(ach.getActivityDate().getTime());
                ach.setActivityValue(ach.getScore() *
                                 1/ach.getIntensity().getDaysPerWeekRequired());
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

        List <Achievement> achList;
        List <Achievement> retList = new ArrayList<>();
        Date iDate = new Date(today.getTime());
        iDate.setTime(today.getTime() - (WEEK_MS * numWeeksPrior));
        return getWeeksAchievements(achievements,iDate);
   }

}