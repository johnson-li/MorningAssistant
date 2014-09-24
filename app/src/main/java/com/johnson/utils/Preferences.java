package com.johnson.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.johnson.morningAssistant.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by johnson on 9/16/14.
 */
public class Preferences {
    static Context context;
    static SharedPreferences sharedPreferences;
    static String USE_VOICE_TO_START = "useVoiceToStart";
    static String USE_VOICE_ENGINE = "useVoiceEngine";
    static String ALARM_HOUR = "alarmHour";
    static String ALARM_MINUTE = "alarmMinute";

    public static void setContext(Context mContext) {
        context = mContext;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        USE_VOICE_TO_START = context.getString(R.string.useVoiceToStart);
        USE_VOICE_ENGINE = context.getString(R.string.useVoiceEngine);
        ALARM_HOUR = context.getString(R.string.alarmHour);
        ALARM_MINUTE = context.getString(R.string.alarmMinute);
    }

    public static boolean useVoiceToStart() {
        return sharedPreferences.getBoolean(context.getString(R.string.useVoiceToStart), false) && useVoiceEngine();
    }

    public static boolean useVoiceEngine() {
        return sharedPreferences.getBoolean(context.getString(R.string.useVoiceEngine), false);
    }

    public static int getAlarmHour() {
        return sharedPreferences.getInt(ALARM_HOUR, 8);
    }

    public static int getAlarmMinute() {
        return sharedPreferences.getInt(ALARM_MINUTE, 30);
    }

    public static void setAlarmTime(int hour, int minute) {
        sharedPreferences.edit().putInt(ALARM_HOUR, hour).putInt(ALARM_MINUTE, minute).apply();
    }

    public static int getAdvancedTime() {
        return Integer.valueOf(sharedPreferences.getString(context.getString(R.string.advancedTime), "15"));
    }

    public static boolean use24HourMode() {
        return getTimeFormat().equals("24");
    }

    public static String getTimeFormat() {
        return sharedPreferences.getString(context.getString(R.string.timeFormat), "24");
    }

    public static float getVolume() {
        return 10;
    }

    public static Uri getRingtone() {
        String str = sharedPreferences.getString(context.getString(R.string.ringtone), RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
        return Uri.parse(str);
    }

    public static Set<String> getKeyWords() {
        return sharedPreferences.getStringSet(context.getString(R.string.keyWords), new HashSet<String>());
    }

    public static boolean firstStart() {
        return sharedPreferences.getBoolean(context.getString(R.string.firstStart), true);
    }

    public static void finishFirstHelp() {
        sharedPreferences.edit().putBoolean(context.getString(R.string.firstStart), false).apply();
    }
}
