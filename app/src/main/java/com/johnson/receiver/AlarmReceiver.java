package com.johnson.receiver;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.morningAssistant.AlarmActivity;
import com.johnson.morningAssistant.MyActivity;
import com.johnson.service.ServiceManager;

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
        AlarmClockManager.setNextAlarm(this);
        Intent activityIntent = new Intent(this, AlarmActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);
        notifyServiceManager();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(MyActivity.LOG_TAG, "broadcast received...");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(MyActivity.LOG_TAG, "alarm receiver destroyed...");
        super.onDestroy();
    }

    void notifyServiceManager() {
        if (!isServiceManagerRunning()) {
            Log.e(MyActivity.LOG_TAG, "service manager is not running!");
        }
        Intent intent = new Intent(this, ServiceManager.class);
        intent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.ALERT);
        startService(intent);
    }

    boolean isServiceManagerRunning() {
        return isServiceRunning(ServiceManager.class);
    }

    boolean isServiceRunning(Class clazz) {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
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
