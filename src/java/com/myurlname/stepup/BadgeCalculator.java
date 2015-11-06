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
    private final static int WEEK_MS = 604800000;
    
    public static Badge calculateBadge (List<Achievement> achievements, Date today) {
        //TODO - using this past week's and the last six weeks of activity;
        //calculate the badge level and habit.
        //badge level corresponds to only this past week.
        //badge habit corresponds to last six weeks.
        
        
        
        return null;
    }
    
    
    private static List<Achievement> getLastWeekAchievements 
                                (List<Achievement> achievements, Date today) {
    
        Date compDate = new Date(today.getTime()-WEEK_MS);
        List <Achievement> retList = new ArrayList<>();
        for (Achievement ach : achievements) {
            if (ach.getActivityDate().compareTo(compDate) > 0) {
                //if the activity date is later than the compare date, save it
                retList.add(ach);                
            }
            else
                break;                           
        }
                                    
        return retList;
    }
    
    private static List<Achievement> getLastSixWeekAchievements 
                                (List<Achievement> achievements, Date today) {
        
        List <Achievement> achList;
        List <Achievement> retList = new ArrayList<>();
        Date iDate = new Date (today.getTime());
        for (int i = 0; i < 6; i ++ ) {            
            achList = getLastWeekAchievements(achievements,iDate);
            if (achList != null)
                retList.addAll(achList);
            else
                break;  
            iDate.setTime(iDate.getTime() - WEEK_MS); //look a week prior
            
        }
        return retList;
   }
    
}
