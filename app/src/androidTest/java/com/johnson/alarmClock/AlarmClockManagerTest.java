package com.johnson.alarmClock;

import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

import java.util.Calendar;
import java.util.Date;

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
        Uri uri1 = AlarmClockManager.addAlarm(mContext);
        Uri uri2 = AlarmClockManager.addAlarm(mContext);
        int alarmId1 = Integer.valueOf(uri1.getPathSegments().get(1));
        int alarmId2 = Integer.valueOf(uri2.getPathSegments().get(1));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 5);
        AlarmClockManager.setAlarm(mContext, alarmId1, true, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), "label", new AlarmClock.DaysOfWeek());
        calendar.add(Calendar.SECOND, 5);
        AlarmClockManager.setAlarm(mContext, alarmId2, true, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), "label", new AlarmClock.DaysOfWeek());
        AlarmClockManager.enableAlarm(mContext, alarmId1);
        AlarmClockManager.enableAlarm(mContext, alarmId2);
        AlarmClock alarmClock1 = AlarmClockManager.getAlarm(mContext, alarmId1);
        AlarmClock alarmClock2 = AlarmClockManager.getAlarm(mContext, alarmId2);
        assertEquals(alarmClock1.enable, true);
        assertEquals(alarmClock2.enable, true);
        try {
            Thread.sleep(7 * 1000);
            alarmClock1 = AlarmClockManager.getAlarm(mContext, alarmId1);
            assertEquals(false, alarmClock1.enable);
            Thread.sleep(5 * 1000);
            alarmClock2 = AlarmClockManager.getAlarm(mContext, alarmId2);
            assertEquals(false, alarmClock2.enable);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testClearAlarm() throws Exception{
        AlarmClockManager.addAlarm(mContext);
        AlarmClockManager.clearAlarm(mContext);
        assertEquals(AlarmClockManager.getAlarmCursor(mContext).isAfterLast(), true);
    }
}