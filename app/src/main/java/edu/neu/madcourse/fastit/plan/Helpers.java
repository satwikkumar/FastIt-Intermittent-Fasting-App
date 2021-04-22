package edu.neu.madcourse.fastit.plan;

import android.text.format.DateFormat;

import java.util.Date;

import edu.neu.madcourse.fastit.plan.FastingCycle;

public class Helpers {

    public static FastingCycle getFastingCycleForNum(int num){
        switch (num){
            case 0: return FastingCycle.THIRTY_SECOND_CYCLE;
            case 1: return FastingCycle.FOURTEEN_HOUR_CYCLE;
            case 2: return FastingCycle.SIXTEEN_HOUR_CYCLE;
            case 3: return FastingCycle.EIGHTEEN_HOUR_CYCLE;
            case 4: return FastingCycle.TWENTY_HOUR_CYCLE;
            case 5: return FastingCycle.TWENTY_FOUR_HOUR_CYCLE;
        }
        return FastingCycle.INVALID_CYCLE;
    }

    public static String getStringForFastingCycle(FastingCycle cycle){
        switch (cycle.getId()){
            case 0: return "30 Seconds Test";
            case 1: return "14-10";
            case 2: return "16-8";
            case 3: return "18-6";
            case 4: return "20-4";
            case 5: return "24-0";
        }
        return "";
    }

    public static double getHoursForCycle(FastingCycle cycle){
        if(!getStringForFastingCycle(cycle).contains("-")){
            return Double.parseDouble("0.0084");
        }
        return Double.parseDouble(getStringForFastingCycle(cycle).split("-")[0]);
    }

    public static double getEndTimeFromStartTime(long startTime, FastingCycle cycle){
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

    public static String getFormattedDate(long time){
        return DateFormat.format("MM/dd hh:mm a", new Date(time)).toString();
    }

    public static long getTimeFromPercentage(long now, long endTime, double percentage){
        long duration = (endTime - now);
        double rem =  ((percentage) / 100 );
        return  (long) (rem * duration);
    }
}
