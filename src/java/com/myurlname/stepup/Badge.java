package com.myurlname.stepup;

import java.io.Serializable;

/**
 * The status symbol indicating progress of exercise activity.  Calculated by
 * the BadgeCalculator
 * @author gabriel
 */
public class Badge implements Serializable {
    private int badgeLevel; //1, 2, or 3
    private int badgeHabit; //1,2, 3
    public final static String [] LEVELS = {"No Badge", "Grasshopped", "Rabbit",
                                                            "Kangaroo"};
    public final static String [] HABITS = {"Idle", "Bronze", "Silver", "Gold"};
    public final static int LOWEST_LEVEL = 0;
    public final static int LOWEST_HABIT = 0;

    public Badge (int level, int habit) {
        if ((level >= 0) && (level < LEVELS.length))
            if ((habit >= 0) && (habit < HABITS.length)){
                badgeLevel = level;
                badgeHabit = habit;
            }
    }

    public Badge () {
        this.badgeLevel = 0;
        this.badgeHabit = 0;
    }

    public int getBadgeLevel() {
        return badgeLevel;
    }

    public String getBadgeLevelName() {
        return LEVELS[badgeLevel];
    }


    public int getBadgeHabit() {
        return badgeHabit;
    }

    public String getBadgeHabitName() {
        return HABITS[badgeHabit];
    }

    public void setBadgeLevel(int badgeLevel) {
        if ((badgeLevel >= 0) && (badgeLevel < LEVELS.length))
                this.badgeLevel = badgeLevel;
        else
            throw new UnsupportedOperationException
                    ("Unsupported badge level: " + badgeLevel);
    }

    public void setBadgeHabit(int badgeHabit) {
        if ((badgeHabit >= 0) && (badgeHabit < HABITS.length))
                this.badgeHabit = badgeHabit;
        else
            throw new UnsupportedOperationException
                    ("Unsupported badge habit: " + badgeHabit);
    }

    @Override
    public String toString () {
        return getBadgeHabitName() + " " + getBadgeLevelName();

    }
}
