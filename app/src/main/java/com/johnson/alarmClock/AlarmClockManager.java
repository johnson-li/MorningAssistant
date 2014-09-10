package com.johnson.alarmClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;
import com.johnson.receiver.AlarmReceiver;
import com.johnson.service.ServiceManager;

import java.util.Date;

/**
 * Created by johnson on 9/1/14.
 * This manager controls all alarm clocks and has the kernel function to set up alarm
 * clock in android system
 */
public class AlarmClockManager {
    public static String ALARM_DATA = "alarmData";
    public static String ALARM_FILTER = "com.johnson.morningAssistant.ALARM_ALERT";
    static String LOG_TAG = AlarmClockManager.class.getSimpleName();

    public static Uri addAlarm(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmClock.Column.HOUR.toString(), 8);
        return contentResolver.insert(AlarmClock.CONTENT_URI, contentValues);
    }

    public static void deleteAlarm(Context context, int alarmId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmId);
        contentResolver.delete(uri, "", null);
        setNextAlarm(context);
    }

    public static void enableAlarm(Context context, int alarmId) {
        AlarmClock alarmClock = getAlarm(context, alarmId);
        alarmClock.enable = true;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(AlarmClock.Column.ENABLE.toString(), true);
        resolver.update(ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmId), values, null, null);
        setNextAlarm(context);
    }

    public static void clearAlarm(Context context) {
        Cursor cursor = getAlarmCursor(context);
        if (!cursor.moveToFirst()) {
            cursor.close();
            disableAlarm(context);
            return;
        }
        do {
            int alarmId = cursor.getInt(AlarmClock.Column.ALARM_ID.ordinal());
            Log.d(MyActivity.LOG_TAG, "delete alarm clock " + alarmId);
            deleteAlarm(context, alarmId);
        } while (cursor.moveToNext());
        cursor.close();
    }

    public static void setNextAlarm(Context context) {
        Cursor cursor = getAlarmCursor(context);
        if (!cursor.moveToFirst()) {
            Log.w(LOG_TAG, "no alarm to set");
            disableAlarm(context);
            return;
        }
        long shortest = 0;
        AlarmClock nextAlarmClock = null;
        do {
            AlarmClock alarmClock = new AlarmClock(cursor);
            if (!alarmClock.enable) {
                continue;
            }
            long time = alarmClock.getNextAlertTime();
            if (shortest == 0 || time < shortest) {
                shortest = time;
                nextAlarmClock = alarmClock;
            }
        } while (cursor.moveToNext());
        cursor.close();
        if (null != nextAlarmClock) {
            enableAlarm(context, nextAlarmClock);
        }
        else {
            Log.i(MyActivity.LOG_TAG, "no alarm clock waiting");
            disableAlarm(context);
        }
    }

    static Cursor getAlarmCursor(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(AlarmClock.CONTENT_URI, AlarmClock.getColumnValues(), null, null, AlarmClock.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        return cursor;
    }

    static Cursor getAlarmCursor(Context context, int alarmId) {
        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmId), AlarmClock.getColumnValues(), null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public static AlarmClock getFirstAlarm(Context context) {
        Cursor cursor = getAlarmCursor(context);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                AlarmClock alarmClock = new AlarmClock(cursor);
                cursor.close();
                return alarmClock;
            }
            cursor.close();
        }
        return null;
    }

    public static AlarmClock getAlarm(Context context, int alarmId) {
        Cursor cursor = getAlarmCursor(context, alarmId);
        if (cursor == null) {
            return null;
        }
        AlarmClock alarmClock = new AlarmClock(cursor);
        cursor.close();
        return alarmClock;
    }

    public static void setAlarm(Context context, int alarmId, boolean enable, int hour, int minute, int second, String label, AlarmClock.DaysOfWeek daysOfWeek) {
        setAlarm(context, new AlarmClock(alarmId, hour, minute, second, daysOfWeek, label, enable));
    }

    public static void setAlarm(Context context, AlarmClock alarmClock) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        for (AlarmClock.Column column: AlarmClock.Column.values()) {
            switch (column) {
                case HOUR:
                    contentValues.put(column.toString(), alarmClock.hour);
                    break;
                case MINUTE:
                    contentValues.put(column.toString(), alarmClock.minute);
                    break;
                case SECOND:
                    contentValues.put(column.toString(), alarmClock.second);
                    break;
                case LABEL:
                    contentValues.put(column.toString(), alarmClock.label);
                    break;
                case ENABLE:
                    contentValues.put(column.toString(), alarmClock.enable);
                    break;
                case DAY_OF_WEEK:
                    contentValues.put(column.toString(), alarmClock.daysOfWeek.toInt());
                    break;
                default:
            }
        }
        contentResolver.update(ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmClock.alarmId), contentValues, null, null);
        setNextAlarm(context);
    }

    static void enableAlarm(Context context, AlarmClock alarmClock) {
        long wakeUpTime = alarmClock.getNextAlertTime();
        android.app.AlarmManager alarmManager = (android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Parcel parcel = Parcel.obtain();
        alarmClock.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Intent intent = new Intent(context, AlarmReceiver.class);
//        Intent intent = new Intent(context, MyActivity.class);
        intent.putExtra(ALARM_DATA, parcel.marshall());
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent);
        Log.d(MyActivity.LOG_TAG, "next alarm: " + new Date(wakeUpTime));
    }

    static void disableAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
