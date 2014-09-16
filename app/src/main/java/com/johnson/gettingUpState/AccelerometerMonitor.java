package com.johnson.gettingUpState;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import com.johnson.Log;

/**
 * Created by johnson on 9/11/14.
 * Determine getting up state by data from accelerometer
 */
public class AccelerometerMonitor extends Monitor{
    public AccelerometerMonitor(Handler handler, Context mContext) {
        super(handler, mContext);
    }

    @Override
    void startMonitor() {
        SensorManager sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new AccelerometerListener(), accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public String getClassName() {
        return AccelerometerMonitor.class.getSimpleName();
    }

    class AccelerometerListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i("sensor accuracy changed: " + accuracy);
        }
    }
}
