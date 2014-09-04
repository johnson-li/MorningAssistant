package com.johnson.alarmClock;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by johnson on 9/1/14.
 * This class defines the information of an alarm clock
 */
public class AlarmClock implements Parcelable{
    public static final String CONTENT_URI_STR = "content://com.johnson.morningAssistant/alarm";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STR);
    public static final String DEFAULT_SORT_ORDER = Column.HOUR + ", " + Column.MINUTE + ", " + Column.SECOND + " ASC";
    int alarmId;
    boolean enable;
    int hour;
    int minute;
    int second;
    DaysOfWeek daysOfWeek;
    String label;

    public static final Creator<AlarmClock> CREATOR = new Creator<AlarmClock>() {
        @Override
        public AlarmClock createFromParcel(Parcel source) {
            return new AlarmClock(source);
        }

        @Override
        public AlarmClock[] newArray(int size) {
            return new AlarmClock[size];
        }
    };

    public AlarmClock(Cursor cursor) {
        alarmId = cursor.getInt(Column.ALARM_ID.ordinal());
        enable = cursor.getInt(Column.ENABLE.ordinal()) != 0;
        hour = cursor.getInt(Column.HOUR.ordinal());
        minute = cursor.getInt(Column.MINUTE.ordinal());
        second = cursor.getInt(Column.SECOND.ordinal());
        Log.d(MyActivity.LOG_TAG, "second" + second);
        daysOfWeek = new DaysOfWeek(cursor.getInt(Column.DAY_OF_WEEK.ordinal()));
        label = cursor.getString(Column.LABEL.ordinal());
    }

    public AlarmClock(int alarmId, int hour, int minute, int second, DaysOfWeek daysOfWeek, String label, boolean enable) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.daysOfWeek = daysOfWeek;
        this.label = label;
        this.enable = enable;
    }

    /*
    *   The data structure of parcel is highly dependent on the data order.
    *   So I use the order of enumerate class Column's order to maintain it
    *   by running through the columns list in Column.values()
    * */
    public AlarmClock(Parcel parcel) {
        for (Column column : Column.values()) {
            switch (column) {
                case ALARM_ID:
                    alarmId = parcel.readInt();
                    break;
                case ENABLE:
                    enable = parcel.readInt() != 0;
                    break;
                case HOUR:
                    hour = parcel.readInt();
                    break;
                case MINUTE:
                    minute = parcel.readInt();
                    break;
                case SECOND:
                    second = parcel.readInt();
                    break;
                case LABEL:
                    label = parcel.readString();
                    break;
                case DAY_OF_WEEK:
                    daysOfWeek = new DaysOfWeek(parcel.readInt());
                    break;
                default:
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (Column column : Column.values()) {
            switch (column) {
                case ALARM_ID:
                    dest.writeInt(alarmId);
                    break;
                case ENABLE:
                    dest.writeInt(enable ? 1 : 0);
                    break;
                case HOUR:
                    dest.writeInt(hour);
                    break;
                case MINUTE:
                    dest.writeInt(minute);
                    break;
                case SECOND:
                    dest.writeInt(second);
                    break;
                case LABEL:
                    dest.writeString(label);
                    break;
                case DAY_OF_WEEK:
                    dest.writeInt(daysOfWeek.toInt());
                    break;
                default:
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Parcel getParcel() {
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        return parcel;
    }

    static class DaysOfWeek {
        private Set<DayOfWeek> dayOfWeekSet = new HashSet<DayOfWeek>();
        public DaysOfWeek(int n) {
            for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
                if (((n >> dayOfWeek.ordinal()) & 1) == 1) {
                    dayOfWeekSet.add(dayOfWeek);
                }
            }
        }

        public DaysOfWeek(){

        }

        public int toInt() {
            int value = 0;
            for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
                if (dayOfWeekSet.contains(dayOfWeek)) {
                    value += 1 << dayOfWeek.ordinal();
                }
            }
            return value;
        }

        public boolean contain(Calendar calendar) {
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            DayOfWeek dayOfWeek = int2day(day);
            return dayOfWeekSet.contains(dayOfWeek);
        }

        public DaysOfWeek addDay(int day) {
            switch (day) {
                case Calendar.SUNDAY:
                    dayOfWeekSet.add(DayOfWeek.SUNDAY);
                    break;
                case Calendar.MONDAY:
                    dayOfWeekSet.add(DayOfWeek.MONDAY);
                    break;
                case Calendar.TUESDAY:
                    dayOfWeekSet.add(DayOfWeek.TUESDAY);
                    break;
                case Calendar.WEDNESDAY:
                    dayOfWeekSet.add(DayOfWeek.WEDNESDAY);
                    break;
                case Calendar.THURSDAY:
                    dayOfWeekSet.add(DayOfWeek.THURSDAY);
                    break;
                case Calendar.FRIDAY:
                    dayOfWeekSet.add(DayOfWeek.FRIDAY);
                    break;
                case Calendar.SATURDAY:
                    dayOfWeekSet.add(DayOfWeek.SATURDAY);
                    break;
                default:
            }
            return this;
        }

        public void deleteDay(int day) {
            switch (day) {
                case Calendar.SUNDAY:
                    dayOfWeekSet.remove(DayOfWeek.SUNDAY);
                    break;
                case Calendar.MONDAY:
                    dayOfWeekSet.remove(DayOfWeek.MONDAY);
                    break;
                case Calendar.TUESDAY:
                    dayOfWeekSet.remove(DayOfWeek.TUESDAY);
                    break;
                case Calendar.WEDNESDAY:
                    dayOfWeekSet.remove(DayOfWeek.WEDNESDAY);
                    break;
                case Calendar.THURSDAY:
                    dayOfWeekSet.remove(DayOfWeek.THURSDAY);
                    break;
                case Calendar.FRIDAY:
                    dayOfWeekSet.remove(DayOfWeek.FRIDAY);
                    break;
                case Calendar.SATURDAY:
                    dayOfWeekSet.remove(DayOfWeek.SATURDAY);
                    break;
                default:
            }
        }

        public boolean isEmpty() {
            return dayOfWeekSet.isEmpty();
        }

        DayOfWeek int2day(int day) {
            switch (day) {
                case Calendar.MONDAY:
                    return DayOfWeek.MONDAY;
                case Calendar.TUESDAY:
                    return DayOfWeek.TUESDAY;
                case Calendar.WEDNESDAY:
                    return DayOfWeek.WEDNESDAY;
                case Calendar.THURSDAY:
                    return DayOfWeek.THURSDAY;
                case Calendar.FRIDAY:
                    return DayOfWeek.FRIDAY;
                case Calendar.SATURDAY:
                    return DayOfWeek.SATURDAY;
                case Calendar.SUNDAY:
                    return DayOfWeek.SUNDAY;
                default:
                    Log.e(MyActivity.LOG_TAG, "error to translate day of week in calendar to my day of week format");
                    return null;
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (DayOfWeek dayOfWeek: dayOfWeekSet) {
                stringBuilder.append(dayOfWeek.toString()).append(" | ");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 3);
            }
            return stringBuilder.toString();
        }
    }

    public static String[] getColumnValues() {
        Column column[] = Column.values();
        String columnStr[] = new String[column.length];
        for (int i = 0; i < column.length; i++) {
            columnStr[i] = column[i].toString();
        }
        return columnStr;
    }

    public long getNextAlertTime() {
        return getAlertCalendar().getTimeInMillis();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("alarm time: ").append(hour).append(":").append(minute).append(":").append(second).append(";\t");
        stringBuilder.append("alarm days: ").append(daysOfWeek.toString());
        return stringBuilder.toString();
    }

    public Calendar getAlertCalendar() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());
        Calendar alertCalendar = Calendar.getInstance();
        alertCalendar.setTimeInMillis(System.currentTimeMillis());
        alertCalendar.set(Calendar.HOUR, hour);
        alertCalendar.set(Calendar.MINUTE, minute);
        alertCalendar.set(Calendar.SECOND, second);
        if (alertCalendar.before(nowCalendar)) {
            alertCalendar.add(Calendar.DATE, 1);
        }
        if (daysOfWeek.isEmpty()) {
            enable = false;
            return alertCalendar;
        }
        while (!daysOfWeek.contain(alertCalendar)) {
            alertCalendar.add(Calendar.DATE, 1);
        }
        return alertCalendar;
    }

    enum Column {
        ALARM_ID, HOUR, MINUTE, SECOND, DAY_OF_WEEK, LABEL, ENABLE
    }

    enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
}
