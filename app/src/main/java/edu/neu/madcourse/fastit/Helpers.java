package edu.neu.madcourse.fastit;

import android.text.format.DateUtils;

import java.util.Arrays;
import java.util.Date;

public class Helpers {

    public static FastingCycle getFastingCycleForNum(int num){
        switch (num){
            case 2: return FastingCycle.FOURTEEN_HOUR_CYCLE;
            case 3: return FastingCycle.SIXTEEN_HOUR_CYCLE;
            case 4: return FastingCycle.EIGHTEEN_HOUR_CYCLE;
            case 5: return FastingCycle.TWENTY_HOUR_CYCLE;
            case 6: return FastingCycle.TWENTY_TWO_HOUR_CYCLE;
            case 7: return FastingCycle.TWENTY_FOUR_HOUR_CYCLE;
            default: return FastingCycle.TWELVE_HOUR_CYCLE;
        }
    }

    public static String getStringForFastingCycle(FastingCycle cycle){
        switch (cycle.getId()){
            case 2: return "14 hour cycle";
            case 3: return "16 hour cycle";
            case 4: return "18 hour cycle";
            case 5: return "20 hour cycle";
            case 6: return "22 hour cycle";
            case 7: return "24 hour cycle";
            default: return "12 hour cycle";
        }
    }

    public static int getHoursForCycle(FastingCycle cycle){
        return Integer.parseInt(getStringForFastingCycle(cycle).substring(0,2));
    }

    public static long getEndTimeFromStartTime(long startTime, FastingCycle cycle){
        return startTime + 3600 * 1000 * getHoursForCycle(cycle);
    }

    public static boolean isFastingCompleted(long endTime, long currentTime){
        Date endDate = new Date(endTime);
        Date currentDate = new Date(currentTime);

        return endDate.after(currentDate) || endDate.equals(currentDate);
    }

    public static int[] getHMSFromMillis(long time){
        int hours = (int) (time / (1000 * 60 * 60)) % 24;
        int minutes = (int) (time / (1000 * 60)) % 60;
        int seconds = (int) (time / 1000) % 60;

        return new int[]{hours, minutes, seconds};
    }
}
