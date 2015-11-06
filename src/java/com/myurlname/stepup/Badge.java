package com.myurlname.stepup;

import java.io.Serializable;

/**
 * The status symbol indicating progress of exercise activity.  Calculated by
 * the BadgeCalculator
 * @author gabriel
 */
public class Badge implements Serializable {
    private String badgeLevel; //Grasshopper, Badger, Bear
    private String badgeHabit; //New, Ramping, Routine
    public final static String LEVEL1 = "Rabbit";
    public final static String LEVEL2 = "Goat"; //mountain goat
    public final static String LEVEL3 = "Kangaroo";
    public final static String HABIT1 = "New";
    public final static String HABIT2 = "Working";
    public final static String HABIT3 = "Confirmed";

    public Badge (String level, String habit) {
        setBadgeLevel (level);
        setBadgeHabit (habit);
    }
    
    public Badge () {}
    
    public String getBadgeLevel() {
        return badgeLevel;
    }

    public final void setBadgeLevel(String level) {
        if (!(level.equalsIgnoreCase(LEVEL1) || 
           level.equalsIgnoreCase(LEVEL2) ||
           level.equalsIgnoreCase(LEVEL3))) 
            badgeLevel = level;       
        else
            throw new UnsupportedOperationException 
                        ("Unsupported badge level: " + level);
    }

    public String getBadgeHabit() {
        return badgeHabit;
    }

    public final void setBadgeHabit(String habit) {
        if (!(habit.equalsIgnoreCase(HABIT1) || 
           habit.equalsIgnoreCase(HABIT2) ||
           habit.equalsIgnoreCase(HABIT3)))                 
            badgeHabit = habit;
        else
            throw new UnsupportedOperationException 
                        ("Unsupported badge habit: " + habit);
    }
    
    
            
            
    
}
