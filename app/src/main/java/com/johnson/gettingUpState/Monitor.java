package com.johnson.gettingUpState;


import android.os.Handler;
import android.util.Log;

/**
 * Created by johnson on 9/11/14.
 * An abstract class that all monitor should extend
 */
public abstract class Monitor extends Thread{
    Handler handler;
    static String LOG_TAG = Monitor.class.getSimpleName();
    public Monitor(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            startMonitor();

        }
        catch (InterruptedException e) {
            Log.i(LOG_TAG, "interrupted exception received, the thread should be interrupted: " + getClassName());
        }
    }

    abstract void startMonitor() throws InterruptedException;

    abstract String getClassName();
}
