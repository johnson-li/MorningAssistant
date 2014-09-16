package com.johnson.alarmStrategy;

/**
 * Created by johnson on 9/15/14.
 */
public class Strategy {
    static int targetTimeHour;
    static int targetTimeMinute;
    static int targetTimeSecond;

    public static void setTargetTime(int hour, int minute, int second) {
        targetTimeHour = hour;
        targetTimeMinute = minute;
        targetTimeSecond = second;
    }
}
