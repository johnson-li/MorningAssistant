package com.example.johnson.morningAssistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.johnson.service.MonitorService;

import java.util.List;


public class MyActivity extends Activity {
    public static String LOG_TAG = "johnsonLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

//        TextView textView = (TextView)findViewById(R.id.textView);
//
//        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        final PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
//
//        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//
//        for (Sensor sensor: sensorList) {
//            Log.d(LOG_TAG, sensor.getName());
////            textView.append(sensor.getName() + "\n");
////            textView.setText("123");
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
        Intent intent = new Intent(this, MonitorService.class);
        startService(intent);
        

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
}
