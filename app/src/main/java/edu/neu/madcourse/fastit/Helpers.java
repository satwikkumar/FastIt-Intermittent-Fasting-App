package edu.neu.madcourse.fastit;

public class Helpers {

    public static FastingCycle getFastingCycleForNum(int num){
        switch (num){
            case 1: return FastingCycle.TWELVE_HOUR_CYCLE;
            case 2: return FastingCycle.FOURTEEN_HOUR_CYCLE;
            case 3: return FastingCycle.SIXTEEN_HOUR_CYCLE;
            case 4: return FastingCycle.EIGHTEEN_HOUR_CYCLE;
            case 5: return FastingCycle.TWENTY_HOUR_CYCLE;
            case 6: return FastingCycle.TWENTY_TWO_HOUR_CYCLE;
            case 7: return FastingCycle.TWENTY_FOUR_HOUR_CYCLE;
            default: return FastingCycle.INVALID_CYCLE;
        }
    }

    public static String getStringForFastingCycle(FastingCycle cycle){
        switch (cycle.getId()){
            case 1: return "12 hour cycle";
            case 2: return "14 hour cycle";
            case 3: return "16 hour cycle";
            case 4: return "18 hour cycle";
            case 5: return "20 hour cycle";
            case 6: return "22 hour cycle";
            case 7: return "24 hour cycle";
            default: return "";
        }
    }
}
