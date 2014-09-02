package com.johnson.alarmClock;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import com.johnson.receiver.AlarmReceiver;

/**
 * Created by johnson on 9/1/14.
 */
public class AlarmClockManager {
    public static String ALARM_DATA = "alarmData";

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
        values.put(AlarmClock.Column.ENABLE.toString(), alarmClock.enable);
        resolver.update(ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmId), values, null, null);
        setNextAlarm(context);
    }

    public static void setNextAlarm(Context context) {
        AlarmClock alarmClock = getFirstAlarm(context);
        if (alarmClock == null) {
            disableAlarm(context);
            return;
        }
        enableAlarm(context, alarmClock);
    }

    static Cursor getAlarmsCursor(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.query(AlarmClock.CONTENT_URI, AlarmClock.getColumnValues(), null, null, AlarmClock.DEFAULT_SORT_ORDER);
    }

    static Cursor getAlarmCursor(Context context, int alarmId) {
        return context.getContentResolver().query(ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmId), AlarmClock.getColumnValues(), null, null, null);
    }

    public static AlarmClock getFirstAlarm(Context context) {
        Cursor cursor = getAlarmsCursor(context);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return new AlarmClock(cursor);
            }
            cursor.close();
        }
        return null;
    }

    public static AlarmClock getAlarm(Context context, int alarmId) {
        Cursor cursor = getAlarmCursor(context, alarmId);
        return cursor == null ? null : new AlarmClock(cursor);
    }

    public static long setAlarm(Context context, AlarmClock alarmClock) {
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
                case DAY_OF_WEEK:
                    contentValues.put(column.toString(), alarmClock.daysOfWeek.toInt());
                    break;
                case LABEL:
                    contentValues.put(column.toString(), alarmClock.label);
                    break;
                case ENABLE:
                    contentValues.put(column.toString(), alarmClock.enable);
                    break;
                default:
            }
        }
        contentResolver.update(ContentUris.withAppendedId(AlarmClock.CONTENT_URI, alarmClock.alarmId), contentValues, null, null);
        setNextAlarm(context);
        return 0;
    }

    static void enableAlarm(Context context, AlarmClock alarmClock) {
        long wakeUpTime = alarmClock.getNextAlertTime();
        android.app.AlarmManager alarmManager = (android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        Parcel parcel = Parcel.obtain();
        alarmClock.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        intent.putExtra(ALARM_DATA, parcel.marshall());
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent);
    }

    static void disableAlarm(Context context) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
