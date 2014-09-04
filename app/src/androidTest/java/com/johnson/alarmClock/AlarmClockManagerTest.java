package com.johnson.alarmClock;

import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

import java.util.Calendar;

public class AlarmClockManagerTest extends AndroidTestCase {

    public void testSetAlarm() throws Exception {
        AlarmClockManager.addAlarm(mContext);
        Uri uri = AlarmClockManager.addAlarm(mContext);
        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
        AlarmClockManager.addAlarm(mContext);
        AlarmClock.DaysOfWeek daysOfWeek = new AlarmClock.DaysOfWeek();
        daysOfWeek.addDay(Calendar.MONDAY).addDay(Calendar.TUESDAY).addDay(Calendar.WEDNESDAY).addDay(Calendar.THURSDAY);
        AlarmClockManager.setAlarm(mContext, alarmId, true, 12, 30, 0, "label", daysOfWeek);
        Cursor cursor = AlarmClockManager.getAlarmCursor(mContext, alarmId);
        Log.d(MyActivity.LOG_TAG, "clock id: " + cursor.getInt(0));
        assertEquals(cursor.getInt(AlarmClock.Column.ALARM_ID.ordinal()), alarmId);
        AlarmClock alarmClock = AlarmClockManager.getAlarm(mContext, alarmId);
        Log.d(MyActivity.LOG_TAG, alarmClock.toString());
        assertEquals(alarmClock.hour, 12);
        assertEquals(alarmClock.minute, 30);
        assertEquals(alarmClock.second, 0);
        assertEquals(alarmClock.label, "label");
        assertEquals(alarmClock.enable, true);
        assertEquals(alarmClock.daysOfWeek.toInt(), daysOfWeek.toInt());
    }

    public void testEnableAlarm() {
        Uri uri = AlarmClockManager.addAlarm(mContext);
        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
        AlarmClockManager.setAlarm(mContext, alarmId, true, 12, 30, 0, "label", new AlarmClock.DaysOfWeek());
        AlarmClockManager.enableAlarm(mContext, alarmId);
        AlarmClock alarmClock = AlarmClockManager.getAlarm(mContext, alarmId);
        assertEquals(alarmClock.enable, false);
    }

    public void testClearAlarm() throws Exception{
        AlarmClockManager.addAlarm(mContext);
        AlarmClockManager.clearAlarm(mContext);
        assertEquals(AlarmClockManager.getAlarmCursor(mContext).isAfterLast(), true);
    }
}