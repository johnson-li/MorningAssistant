package com.johnson.alarmClock;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

/**
 * Created by johnson on 9/3/14.
 * Fuck, why should i use a database to record alarm clock for that there is only one instance!!!
 */
public class AlarmClockProvider extends ContentProvider{
    SQLiteOpenHelper sqLiteOpenHelper;

    public static final String AUTHORITY = "com.johnson.morningAssistant";
    public static final String ALARM_TABLE = "alarm";
    public static final String ALARM_URI_STR = "content://com.johnson.morningAssistant/alarm";
    static final int ALARM = 1;
    static final int ALARM_ID = 2;
    static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, ALARM_TABLE, ALARM);
        uriMatcher.addURI(AUTHORITY, ALARM_TABLE + "/#", ALARM_ID);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        int match = uriMatcher.match(uri);
        switch (match) {
            case ALARM:
                sqLiteQueryBuilder.setTables(ALARM_TABLE);
                break;
            case ALARM_ID:
                sqLiteQueryBuilder.setTables(ALARM_TABLE);
                sqLiteQueryBuilder.appendWhere(AlarmClock.Column.ALARM_ID + "=");
                sqLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor == null) {
            Log.e(MyActivity.LOG_TAG, "alarm query error");
        }
        else {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case ALARM:
                count = sqLiteDatabase.delete(ALARM_TABLE, selection, selectionArgs);
                break;
            case ALARM_ID:
                String segment = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    selection = AlarmClock.Column.ALARM_ID + "=" + segment;
                }
                else {
                    selection = AlarmClock.Column.ALARM_ID + "=" + segment + " and (" + selection + ")";
                }
                count = sqLiteDatabase.delete(ALARM_TABLE, selection, selectionArgs);
                break;
            default:
                throw  new  IllegalArgumentException("cannot delete from uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != ALARM) {
            throw new IllegalArgumentException("Cannot insert into URI: " + uri);
        }
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        }
        else {
            contentValues = new ContentValues();
        }
        if (!contentValues.containsKey(AlarmClock.Column.ENABLE.toString())) {
            contentValues.put(AlarmClock.Column.ENABLE.toString(), false);
        }
        if (!contentValues.containsKey(AlarmClock.Column.HOUR.toString())) {
            contentValues.put(AlarmClock.Column.HOUR.toString(), 0);
        }
        if (!contentValues.containsKey(AlarmClock.Column.MINUTE.toString())) {
            contentValues.put(AlarmClock.Column.MINUTE.toString(), 0);
        }
        if (!contentValues.containsKey(AlarmClock.Column.SECOND.toString())) {
            contentValues.put(AlarmClock.Column.SECOND.toString(), 0);
        }
        if (!contentValues.containsKey(AlarmClock.Column.DAY_OF_WEEK.toString())) {
            contentValues.put(AlarmClock.Column.DAY_OF_WEEK.toString(), 0);
        }
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        long row = sqLiteDatabase.insert(ALARM_TABLE, AlarmClock.Column.LABEL.toString(), contentValues);
        return ContentUris.withAppendedId(Uri.parse(ALARM_URI_STR), row);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        long row;
        int count;
        switch (match) {
            case ALARM_ID:
                String segment = uri.getPathSegments().get(1);
                row = Long.parseLong(segment);
                count = sqLiteDatabase.update(ALARM_TABLE, values, AlarmClock.Column.ALARM_ID + "=" + row, null);
                break;
            default:
                throw new UnsupportedOperationException("update uri error: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case ALARM:
                return "vnd.android.cursor.dir/alarm";
            case ALARM_ID:
                return "vnd.android.cursor.item/alarm";
            default:
                throw new IllegalArgumentException("Unknown uri");
        }
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        static final String DATABASE_NAME = "alarms.db";
        static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table alarm (" +
                    AlarmClock.Column.ALARM_ID + " integer primary key, " +
                    AlarmClock.Column.HOUR + " integer, " +
                    AlarmClock.Column.MINUTE + " integer, " +
                    AlarmClock.Column.SECOND + " integer, " +
                    AlarmClock.Column.ENABLE + " integer, " +
                    AlarmClock.Column.DAY_OF_WEEK + " integer, " +
                    AlarmClock.Column.LABEL + " TEXT);");

            String insertPrefix = "insert into alarm (" + AlarmClock.Column.HOUR +
                    ", " + AlarmClock.Column.MINUTE +
                    ", " + AlarmClock.Column.SECOND +
                    ", " + AlarmClock.Column.ENABLE +
                    ", " + AlarmClock.Column.DAY_OF_WEEK + ") values ";
            db.execSQL(insertPrefix + "(7, 0, 0, 1 , 127)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.v(MyActivity.LOG_TAG, "upgrading database version from " + oldVersion + " to " + newVersion);
            db.execSQL("drop table if exists alarms");
            onCreate(db);
        }

    }
}
