package com.johnson.morningAssistant;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;


public class MyActivity extends Activity {
    public static String LOG_TAG = "johnsonLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        calendarTest();
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
