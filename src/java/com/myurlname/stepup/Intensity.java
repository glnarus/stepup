package com.myurlname.stepup;

/**
 * An object indicating the intensityFactor of a workout; light, moderate, hard, 
 strenuous
 * @author gabriel
 */
public class Intensity {
    private final int INT_LIGHT = 1;
    private final int INT_MODERATE = 4;
    private final int INT_HARD = 7;
    private final int INT_STRENUOUS = 10;
    private int intensityFactor;
    
    public Intensity (int intensity) {
        if (isAnIntensity (intensity))
            intensityFactor = intensity;
        else
            throw new UnsupportedOperationException 
                        ("Unsupported intensity level: " + intensity);
        
    }
    
    public Intensity (String intensity) {
        switch (intensity) {
            case "Light":
                intensityFactor = INT_LIGHT;
                break;
            case "Moderate":
                intensityFactor = INT_MODERATE;
                break;
            case "Hard":
                intensityFactor = INT_HARD;
                break;
            case "Strenuous":
                intensityFactor = INT_STRENUOUS;
                break;       
            default:
                throw new UnsupportedOperationException 
                        ("Unsupported intensity level: " + intensity);
        }                
    }    
    
    @Override
    public String toString () {
        switch (intensityFactor) {
            case INT_LIGHT: 
                return "Light";
            case INT_MODERATE: 
                return "Moderate";
            case INT_HARD:
                return "Hard";
            case INT_STRENUOUS:
                return "Strenuous";
            default:
                return "Unknown intensity level";        
        }
    }
    private boolean isAnIntensity (int number) {
        switch (number) {
            case INT_LIGHT:
                return true;
            case INT_MODERATE:
                return true;
            case INT_HARD: 
                return true;
            case INT_STRENUOUS: 
                return true;
            default:
                return false;
        }        
        
    }

    public int getIntensityFactor() {
        return intensityFactor;
    }
    
}
