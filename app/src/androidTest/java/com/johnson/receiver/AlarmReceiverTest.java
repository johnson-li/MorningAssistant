package com.johnson.receiver;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.johnson.Log;
import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.utils.ShakeData;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiverTest extends AndroidTestCase {

//    public void testOnHandleIntent() {
//        AlarmClockManager.clearAlarm(mContext);
//        Uri uri = AlarmClockManager.addAlarm(mContext);
//        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, 5);
//        AlarmClockManager.setAlarm(mContext, alarmId, true, calendar.get(Calendar.HOUR),
//                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), "label",
//                new AlarmClock.DaysOfWeek());
//
//    }

    public void testShake() throws Exception{
        SensorManager sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            ShakeData shakeData = new ShakeData();
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float a = x * x + y * y + z * z;
                shakeData.add(new Date().getTime(), Math.abs(a - 97));
                if (shakeData.triggered()) {
                    Log.e("***********************************************************");
                }
                Log.d(Math.abs(a - 97));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        Thread.sleep(5 * 1000);
        Log.d("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        sensorManager.unregisterListener(sensorEventListener);
        Thread.sleep(15 * 1000);
    }
}