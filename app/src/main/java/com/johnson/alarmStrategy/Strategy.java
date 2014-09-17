package com.johnson.alarmStrategy;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.service.ServiceManager;
import com.johnson.utils.Preferences;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by johnson on 9/15/14.
 */
public class Strategy {
    static int targetTimeHour;
    static int targetTimeMinute;
    static int targetTimeSecond;
    static Context context;

    public static void setContext(Context mContext) {
        context = mContext;
    }

    public static void setTargetTime(int hour, int minute, int second) {
        targetTimeHour = hour;
        targetTimeMinute = minute;
        targetTimeSecond = second;
        addAlarm(hour, minute, second, Preferences.getAdvancedTime());
    }

    public static void addAlarm() {
        addAlarm(Preferences.getAlarmHour(), Preferences.getAlarmMinute(), 0, Preferences.getAdvancedTime());
    }

    public static void addAlarm(int hour, int minute, int second, int ahead) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.add(Calendar.MINUTE, 0 - ahead);
        addAlarm(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public static void addAlarm(int hour, int minute, int second) {
        AlarmClockManager.clearAlarm(context);
        Uri uri = AlarmClockManager.addAlarm(context);
        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
        AlarmClockManager.setAlarm(context, alarmId, true, hour, minute, second, "label", new AlarmClock.DaysOfWeek().addAll());
    }

    public static void setLastAlarm() {
        AlarmClockManager.clearAlarm(context);
        Uri uri = AlarmClockManager.addAlarm(context);
        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
        AlarmClockManager.setAlarm(context, alarmId, true, targetTimeHour, targetTimeMinute, targetTimeSecond, "last", new AlarmClock.DaysOfWeek().addAll());

    }

    public static void setNextAlarm() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        now.setTime(new Date());
        target.setTime(new Date());
        target.set(Calendar.HOUR, targetTimeHour);
        target.set(Calendar.MINUTE, targetTimeMinute);
        target.set(Calendar.SECOND, targetTimeSecond);
        if (target.after(now)) {
            Date nowDate = now.getTime();
            Date targetDate = target.getTime();
            long interval = (targetDate.getTime() - nowDate.getTime()) / 2;
            if (interval < 1000 * 60 * 2) {
                setLastAlarm();
            }
            else {
                target.setTime(new Date(nowDate.getTime() + interval));
                addAlarm(target.get(Calendar.HOUR), target.get(Calendar.MINUTE), target.get(Calendar.SECOND));
            }
        }
        else {
            setTomorrowAlarm();
        }
    }

    /*
    *   just set tomorrow's alarm
    * */
    public static void gettingUpSuccessfully() {
        setTomorrowAlarm();
        AlarmRecord.addGettingUpTime(new Date());
    }

    public static void setTomorrowAlarm() {
        Intent intent = new Intent(context, ServiceManager.class);
        intent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.SET_ALARM);
        context.startService(intent);
    }
}
