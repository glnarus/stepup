package com.myurlname.stepup;

import java.io.Serializable;

/**
 * An object indicating the intensityFactor of a workout; light, moderate, hard, 
 strenuous
 * @author gabriel
 */
public class Intensity implements Serializable {
    private String intensityFactor;
    public final static String INT_LIGHT = "Light";
    public final static String INT_MODERATE = "Moderate";
    public final static String INT_HARD = "Hard";
    public final static String INT_STRENUOUS = "Strenuous";
    private int minimumMinutes;
    private int daysPerWeekRequired; //days per week
    
    public Intensity () {
        this (INT_LIGHT);
    }

    public Intensity (String intensity) {
        if (intensity.equalsIgnoreCase(INT_LIGHT)) {
            minimumMinutes = 40;
            daysPerWeekRequired = 5;
            intensityFactor = INT_LIGHT;
        }
        else if (intensity.equalsIgnoreCase(INT_MODERATE)) {
            minimumMinutes = 30;
            daysPerWeekRequired = 5;
            intensityFactor = INT_MODERATE;
        }
        else if (intensity.equalsIgnoreCase(INT_HARD)) {
            minimumMinutes = 25;
            daysPerWeekRequired = 3;
            intensityFactor = INT_HARD;
        }
        else if (intensity.equalsIgnoreCase(INT_STRENUOUS)) {
            minimumMinutes = 20;
            daysPerWeekRequired = 3;
            intensityFactor = INT_STRENUOUS;
        }        
        else {
                throw new UnsupportedOperationException 
                        ("Unsupported intensity level: " + intensity);
        }                
    }    
        
    @Override
    public String toString () {
       return this.intensityFactor;
    }

    public static boolean isIntensity (String text) {
        if (text.equalsIgnoreCase(INT_LIGHT)) {
            return true;
        }
        else if (text.equalsIgnoreCase(INT_MODERATE)) {
            return true;
        }
        else if (text.equalsIgnoreCase(INT_HARD)) {
            return true;
        }
        else if (text.equalsIgnoreCase(INT_STRENUOUS)) {
            return true;
        }        
        else {
            return false;
        }                             
        
    }

    public int getMinimumMinutes() {
        return minimumMinutes;
    }

    public int getDaysPerWeekRequired() {
        return daysPerWeekRequired;
    }

    
}
