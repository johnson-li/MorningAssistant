package com.johnson.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

import java.util.List;

/**
 * Created by johnson on 8/31/14.
 * This is the service running background as the rule of alarm clock
 */
public class AlarmService extends IntentService{
    public AlarmService() {
        super("Monitor Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor: sensorList) {
            Log.d(MyActivity.LOG_TAG, sensor.getName());
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        final SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        final PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        super.onStart(intent, startId);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float a = x * x + y * y + z * z;
                Log.d(MyActivity.LOG_TAG, a + ", " + powerManager.isScreenOn());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d(MyActivity.LOG_TAG, String.valueOf(event.values[0]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, light, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
