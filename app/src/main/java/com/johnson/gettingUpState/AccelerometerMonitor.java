package com.johnson.gettingUpState;


import android.os.Handler;

/**
 * Created by johnson on 9/11/14.
 * Determine getting up state by data from accelerometer
 */
public class AccelerometerMonitor extends Monitor{
    public AccelerometerMonitor(Handler handler) {
        super(handler);
    }

    @Override
    void startMonitor() {

    }

    @Override
    String getClassName() {
        return AccelerometerMonitor.class.getSimpleName();
    }
}
