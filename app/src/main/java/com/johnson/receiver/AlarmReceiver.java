package com.johnson.receiver;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Parcel;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.johnson.Log;
import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.morningAssistant.AlarmActivity;
import com.johnson.morningAssistant.MyActivity;
import com.johnson.morningAssistant.R;
import com.johnson.service.AlarmNotificationService;
import com.johnson.service.ServiceManager;
import com.johnson.utils.ShakeData;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

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
        Log.d("handling intent...");
        AlarmClock alarmClock = getAlarmClock(intent);
        AlarmClockManager.setAlarm(this, alarmClock);
        AlarmClockManager.setNextAlarm(this);
        if (((PowerManager)getSystemService(Context.POWER_SERVICE)).isScreenOn()) {
            notifyNotification();
        }
        else {
            notifyActivity();
        }
        notifyServiceManager();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("broadcast received...");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("alarm receiver destroyed...");
        super.onDestroy();
    }

    void notifyNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Morning Assistant").setContentText("alarming...");
        Intent intent = new Intent(this, AlarmNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent).setDeleteIntent(pendingIntent)
                .setTicker("alarming...").setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(2, builder.build());
        waitForShake();
    }

    void waitForShake() {
        new Thread() {
            @Override
            public void run() {
                final ShakeData shakeData = new ShakeData();
                final SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
                Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        float a = x * x + y * y + z * z;
                        float abs = Math.abs(a - 97);
                        shakeData.add(new Date().getTime(), abs);
                        if (shakeData.triggered()) {
                            sensorManager.unregisterListener(this);
                            Intent startIntent = new Intent(AlarmReceiver.this, ServiceManager.class);
                            startIntent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.NOTIFICATION);
                            startService(startIntent);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                }, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }.start();
    }

    void notifyActivity() {
        Intent activityIntent = new Intent(this, AlarmActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);
    }

    void notifyServiceManager() {
        if (!isServiceManagerRunning()) {
            Log.e("service manager is not running!");
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
            Log.e("intent parcel received error");
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return AlarmClock.CREATOR.createFromParcel(parcel);
    }
}
