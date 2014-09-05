package com.johnson.receiver;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;

import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.morningAssistant.MyActivity;

/**
 * Created by johnson on 9/1/14.
 * This receiver receives command from google now or other apps to set/cancel alarm clock
 */
public class AlarmReceiver  extends IntentService{

    public AlarmReceiver() {
        super("Alarm Receiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(MyActivity.LOG_TAG, "handling intent...");
        AlarmClock alarmClock = getAlarmClock(intent);
        AlarmClockManager.setAlarm(this, alarmClock);
        AlarmClockManager.setNextAlarm(this); }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(MyActivity.LOG_TAG, "broadcast received...");
        super.onStart(intent, startId);
    }

    AlarmClock getAlarmClock(Intent intent) {
        byte bytes[] = intent.getByteArrayExtra(AlarmClockManager.ALARM_DATA);
        if (bytes == null) {
            Log.e(MyActivity.LOG_TAG, "intent parcel received error");
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return AlarmClock.CREATOR.createFromParcel(parcel);
    }
}
