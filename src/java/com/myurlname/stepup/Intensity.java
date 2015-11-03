package com.myurlname.stepup;

/**
 * An object indicating the intensity of a workout; light, moderate, hard, 
 * strenuous
 * @author gabriel
 */
public class Intensity {
    private final int INT_LIGHT = 0;
    private final int INT_MODERATE = 10;
    private final int INT_HARD = 20;
    private final int INT_STRENUOUS = 30;
    private int intensity;
    
    public Intensity (int intensity) {
        if (isAnIntensity (intensity))
            this.intensity = intensity;
        else
            throw new UnsupportedOperationException 
                        ("Unsupported intensity level: " + intensity);
        
    }
    
    public Intensity (String intensity) {
        switch (intensity) {
            case "Light":
                this.intensity = INT_LIGHT;
                break;
            case "Moderate":
                this.intensity = INT_MODERATE;
                break;
            case "Hard":
                this.intensity = INT_HARD;
                break;
            case "Strenuous":
                this.intensity = INT_STRENUOUS;
                break;       
            default:
                throw new UnsupportedOperationException 
                        ("Unsupported intensity level: " + intensity);
        }                
    }    
    
    @Override
    public String toString () {
        switch (this.intensity) {
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
    
}
