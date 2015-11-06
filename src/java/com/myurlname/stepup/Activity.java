package com.myurlname.stepup;

/**
 * An activity is running, swimming, etc.  This class defines constants for
 * all activities and includes a print string method to display the activity
 * name
 * @author gabriel
 */
public class Activity {
    private int activityNumber;
    private int muscleFactor; //ranges from 1 to 10 in terms of muscle
                              //recruitment
        
    public final int ACT_BALLSPORTS = 0;
    public final int ACT_BIKING = 100;
    public final int ACT_BOXING = 200;    
    public final int ACT_CALISTHENICS = 300;
    public final int ACT_CLIMBING = 400;
    public final int ACT_DANCE = 500; 
    public final int ACT_HIKING = 600;         
    public final int ACT_MISCCARDIO = 700;
    public final int ACT_RUNNING = 800;
    public final int ACT_SWIMMING = 900;
    public final int ACT_WALKING = 1000;
    public final int ACT_WEIGHTS = 1100;    
    public final int ACT_YOGA = 1200;    
    //!IMPORTANT!
    //If adding activities, be sure to update ALL methods below as well
    
        
    public Activity (int activityNumber) {
        if (isAnActivity(activityNumber))
            this.activityNumber = activityNumber;
        else
            throw new UnsupportedOperationException 
                        ("Unsupported activity selected: " + activityNumber);
        
    }
    
    public Activity (String activityName) {
        activityName = activityName.toUpperCase();
        switch (activityName) {
            case "BALL SPORTS":
                activityNumber = ACT_BALLSPORTS;
                break;
            case "BICYCLING":
                activityNumber = ACT_BIKING;
                break;                
            case "BOXING":
                activityNumber = ACT_BOXING;
                break;
            case "CALISTHENICS":
                activityNumber = ACT_CALISTHENICS;
                break;                
            case "CLIMBING":
                activityNumber = ACT_CLIMBING;
                break;
            case "DANCE":
                activityNumber = ACT_DANCE;
                break;                
            case "HIKING":
                activityNumber = ACT_HIKING;
                break;
            case "MISC CARDIO":
                activityNumber = ACT_MISCCARDIO;
                break;                
            case "RUNNING":
                activityNumber = ACT_RUNNING;
                break;
            case "SWIMMING":
                activityNumber = ACT_SWIMMING;
                break;                
            case "WALKING":
                activityNumber = ACT_WALKING;
                break;
            case "WEIGHTS":
                activityNumber = ACT_WEIGHTS;
                break;                
            case "YOGA":
                activityNumber = ACT_YOGA;
                break;   
            default:
                throw new UnsupportedOperationException 
                        ("Unsupported activity selected: " + activityName);                                            
            
        }
        isAnActivity(activityNumber); //run this method to set the 
                                      //muscleFactor
    }
    
    @Override
    public String toString () {
        switch (activityNumber) {
            case ACT_BALLSPORTS:
                return "Ball Sports";    
            case ACT_BIKING:
                return "Bicycling";      
            case ACT_BOXING:
                return "Boxing";
            case ACT_CALISTHENICS:
                return "Calisthenics";
            case ACT_CLIMBING:
                return "Climbing";
            case ACT_DANCE:
                return "Dance";
            case ACT_HIKING:
                return "Hiking";
            case ACT_MISCCARDIO:
                return "Misc Cardio";
            case ACT_RUNNING:
                return "Running";
            case ACT_SWIMMING:
                return "Swimming";
            case ACT_WALKING:
                return "Walking";
            case ACT_WEIGHTS:
                return "Weights";
            case ACT_YOGA:
                return "Yoga";
            default:
                return "Unknown Activity";        
        }
    }
    
    private boolean isAnActivity (int number) {
        boolean answer;
        switch (number) {
            case ACT_BALLSPORTS:
                muscleFactor = 6;
                answer = true;
                break;                
            case ACT_BIKING:
                muscleFactor = 6;
                answer = true;
                break;                
            case ACT_BOXING:
                muscleFactor = 10;
                answer = true;
                break;                
            case ACT_CALISTHENICS:
                muscleFactor = 8;
                answer = true;
                break;
            case ACT_CLIMBING:
                muscleFactor = 8;
                answer = true;
                break;
            case ACT_DANCE:
                muscleFactor = 10;
                answer = true;
                break;
            case ACT_HIKING:
                muscleFactor = 4;
                answer = true;
                break;
            case ACT_MISCCARDIO:
                muscleFactor = 8;
                answer = true;
                break;
            case ACT_RUNNING:
                muscleFactor = 9;
                answer = true;
                break;
            case ACT_SWIMMING:
                muscleFactor = 10;
                answer = true;
                break; 
            case ACT_WALKING:
                muscleFactor = 3;
                answer = true;
                break;
            case ACT_WEIGHTS:
                muscleFactor = 5;
                answer = true;
                break;
            case ACT_YOGA:
                muscleFactor = 7;
                answer = true;
                break;
            default:
                answer = false;
                break;                            
        }
        return answer;             
    }

    public int getMuscleFactor() {
        return muscleFactor;
    }
}
