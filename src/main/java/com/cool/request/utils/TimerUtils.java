package com.cool.request.utils;

public class TimerUtils {
    public static String convertMilliseconds(long milliseconds) {
        double seconds = milliseconds / 1000.0;
        if (seconds > 60) {
            double minutes = seconds / 60;
            return String.format("%.2fs", minutes);
        } else if (seconds > 1) {
            return String.format("%.2fs", seconds);
        } else {
            return milliseconds + "ms";
        }
    }

}
