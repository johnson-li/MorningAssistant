package com.johnson.alarmStrategy;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.johnson.utils.Preferences;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by johnson on 9/17/14.
 * @TODO use database to record getting up information
 */
public class AlarmRecord {
    static final String DATA_BASE = "alarms.db";
    static final String TABLE = "alarmRecord";
    static final String ID = "_id";
    static final String DATE = "date";
    static final String TIME_HOUR = "timeHour";
    static final String TIME_MINUTE = "timeMinute";
    static final String TIME_SECOND = "timeSecond";
    static final String DUPLICATE = "duplicate";
    static final String ACTION = "action";
    static final String CREATE_TABLE = String.format("create table %s (%s integer primary key," +
            "%s text, %s integer, %s integer, %s integer, %s integer, %s text)",
            TABLE, ID, DATE, TIME_HOUR, TIME_MINUTE, TIME_SECOND, DUPLICATE, ACTION);
    static Context context;

    static void setContext(Context mContext) {
        context = mContext;
    }

    static int getFirstAlarmTime() {
        return Preferences.getAdvancedTime();
    }

    static void addGettingUpTime(Date date) {

    }

    static void insert() {
        ContentValues contentValues = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        String date = String.format("%d.%d.%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        contentValues.put(DATE, date);
    }
}

class SqliteHelper extends SQLiteOpenHelper {
    public SqliteHelper(Context context) {
        super(context, AlarmRecord.DATA_BASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlarmRecord.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exits " + AlarmRecord.TABLE);
        onCreate(db);
    }
}