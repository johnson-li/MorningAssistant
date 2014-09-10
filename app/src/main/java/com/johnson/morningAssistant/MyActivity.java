package com.johnson.morningAssistant;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.service.ServiceManager;

import java.util.Calendar;
import java.util.Date;


public class MyActivity extends Activity {
    public static final String LOG_TAG = "johnsonLog";
    public static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Button voiceSetting = (Button)findViewById(R.id.voiceSetting);
        Button manualSetting = (Button)findViewById(R.id.manualSetting);
        manualSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startMorningAssistant();
            finish();
            }
        });
        Button clear = (Button)findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMorningAssistant();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void startMorningAssistant() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        setAlarmClock(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        startNotification(hour, minute);
        startServiceManager();
    }

    void stopMorningAssistant() {
        clearAlarmClock();
        stopNotification();
        stopServiceManager();
    }

    void setAlarmClock(int hour, int minute, int second) {
        AlarmClockManager.clearAlarm(this);
        Uri uri = AlarmClockManager.addAlarm(this);
        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
        AlarmClockManager.setAlarm(this, alarmId, true, hour, minute, second, "label", new AlarmClock.DaysOfWeek().addAll());
    }

    void clearAlarmClock() {
        AlarmClockManager.clearAlarm(this);
    }

    void startNotification(int hour, int minute) {
        Log.i(MyActivity.LOG_TAG, "start notification");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        /*
        *   this should work on api 16 or later
        * */
//        Notification.Builder notificationBuilder = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle("service manager").setContentText("this is a test");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Morning Assistant").setContentText(hour + ":" + minute);
        Intent intent = new Intent(this, MyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    void stopNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    void startServiceManager() {
        Intent intent = new Intent(this, ServiceManager.class);
        intent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.INIT);
        this.startService(intent);
    }

    void stopServiceManager() {
        Intent intent = new Intent(this, ServiceManager.class);
        stopService(intent);
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

    @Deprecated
    void serviceManagerTest() {
        Intent intent = new Intent(this, ServiceManager.class);
//        this.stopService(intent);
        this.startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(MyActivity.LOG_TAG, "service manager connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(MyActivity.LOG_TAG, "service manager disconnected");
            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Deprecated
    void calendarTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 59);
        calendar.add(Calendar.SECOND, 1);
        Log.d("johnson", calendar.get(Calendar.MINUTE) + "");
    }

    @Deprecated
    void alarmManagerTest() {
        Log.d("johnson", "i am here");
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MyActivity.this, MyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 5000, pendingIntent);
    }

    @Deprecated
    void notificationTest() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "Notification", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //闪光时间，毫秒
        CharSequence contentTitle ="督导系统标题"; // 通知栏标题
        CharSequence contentText ="督导系统内容"; // 通知栏内容
        Intent notificationIntent =new Intent(MyActivity.this, MyActivity.class); // 点击该通知后要跳转的Activity
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);

        notificationManager.notify(0, notification);
    }

    @Deprecated
    void accelerometerTest() {
//        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        final PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
//
//        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//
//        for (Sensor sensor: sensorList) {
//            Log.d(LOG_TAG, sensor.getName());
//        }
//        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent event) {
//                float x = event.values[0];
//                float y = event.values[1];
//                float z = event.values[2];
//                Log.d(LOG_TAG, x + ", " + y + ", " + z + ", " + powerManager.isScreenOn());
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//        }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        Intent intent = new Intent(this, AlarmService.class);
//        startService(intent);
    }
}
