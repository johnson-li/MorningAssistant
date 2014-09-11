package com.johnson.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.johnson.gettingUpState.AccelerometerMonitor;
import com.johnson.gettingUpState.Monitor;
import com.johnson.morningAssistant.MyActivity;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by johnson on 8/31/14.
 * This is the service running background as the rule of alarm clock
 */
public class ServiceManager extends Service{
    public static final String INTENT_TYPE = "intentType";
    public static final String LOG_TAG = ServiceManager.class.getSimpleName();
    public static final int GETTING_UP_SUCCESS = 2;
    public static final int GETTING_UP_FAILED = 1;
    public static Set<Class> monitors = new HashSet<Class>();
    static {
        monitors.add(AccelerometerMonitor.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(MyActivity.LOG_TAG, "service manager bound...");
        return new ServiceManagerBinder();
    }

    @Override
    public void onCreate() {
        Log.i(MyActivity.LOG_TAG, "service manager created...");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(MyActivity.LOG_TAG, "service manager started...");
        parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(MyActivity.LOG_TAG, "service manager destroyed...");
        super.onDestroy();
    }

    void parseIntent(Intent intent) {
        if (intent == null) return;
        IntentType intentType = (IntentType)intent.getSerializableExtra(INTENT_TYPE);
        final Set<Monitor> monitorSet = new HashSet<Monitor>();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GETTING_UP_SUCCESS) {
                    for (Monitor monitor: monitorSet) {
                        if (monitor != null && monitor.isAlive()) {
                            monitor.interrupt();
                        }
                    }
                    checkMonitorStatus(monitorSet);
                    handleGettingUp();
                }
            }
        };
        switch (intentType) {
            case INIT:
                Log.i(LOG_TAG, "initiated");
                break;
            case ALERT:
                Log.i(LOG_TAG, "alert message received");
                for (Class clazz: monitors) {
                    try {
                        Constructor constructor = clazz.getConstructor(new Class[]{Handler.class});
                        Monitor monitor = (Monitor)constructor.newInstance(handler);
                        monitorSet.add(monitor);
                        monitor.start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case INTERRUPT:
                Log.i(LOG_TAG, "interrupted");

                break;
            default:
        }
    }

    void checkMonitorStatus(Set<Monitor> monitorSet) {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (Monitor monitor: monitorSet) {
            if (monitor != null) {
                Log.d(LOG_TAG, monitor.getName() + ": " + monitor.isAlive());
            }
        }
    }

    void handleGettingUp() {
        Log.i(LOG_TAG, "successfully getting up");
    }

    class ServiceManagerBinder extends Binder {
         public ServiceManager getServiceManager() {
             return ServiceManager.this;
         }
    }

    public enum IntentType {
        INIT, ALERT, INTERRUPT
    }
}
