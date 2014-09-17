package com.johnson.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.johnson.Log;
import com.johnson.alarmStrategy.Strategy;
import com.johnson.gettingUpState.AccelerometerMonitor;
import com.johnson.gettingUpState.Monitor;
import com.johnson.gettingUpState.WatchDog;
import com.johnson.morningAssistant.MyActivity;
import com.johnson.utils.Preferences;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
        monitors.add(WatchDog.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("service manager bound...");
        return new ServiceManagerBinder();
    }

    @Override
    public void onCreate() {
        Log.i("service manager created...");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("service manager started...");
        parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("service manager destroyed...");
        super.onDestroy();
    }

    void parseIntent(Intent intent) {
        if (intent == null) return;
        IntentType intentType = (IntentType)intent.getSerializableExtra(INTENT_TYPE);
        final Set<Monitor> monitorSet = new HashSet<Monitor>();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                interruptMonitors(monitorSet);
                handleGettingUp(msg.what == GETTING_UP_SUCCESS);
            }
        };
        switch (intentType) {
            case INIT:
                Log.i("initiated");
                break;
            case ALERT:
                Log.i("alert message received");
                for (Class clazz: monitors) {
                    try {
                        Constructor constructor = clazz.getConstructor(new Class[]{Handler.class, Context.class});
                        Monitor monitor = (Monitor)constructor.newInstance(handler, this);
                        monitorSet.add(monitor);
                        monitor.start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case INTERRUPT:
                Log.i("interrupted");
                Strategy.setNextAlarm();
                break;
            case NOTIFICATION:
                /*
                *   this case shows that alarm notification is cleared or clicked, so
                *   the user must have waken up.
                * */
                Log.i("notification");
                interruptMonitors(monitorSet);
                handleGettingUp(true);
                break;
            case SET_ALARM:
                Log.i("set alarm");
                /*
                *   Set alarm in a period of time.
                *   Otherwise the alarm will be set just before the target time when
                *   the user gets up before that time arrived
                * */
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Strategy.addAlarm();
                    }
                }, new Date(new Date().getTime() + Preferences.getAdvancedTime() * 60 * 1000));
        }
    }

    void interruptMonitors(Set<Monitor> monitorSet) {
        for (Monitor monitor: monitorSet) {
            if (monitor != null && monitor.isAlive()) {
                monitor.interrupt();
            }
        }
        checkMonitorStatus(monitorSet);
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
                Log.d(monitor.getClassName() + " is alive: " + monitor.isAlive());
            }
        }
        monitorSet.clear();
    }

    void handleGettingUp(boolean state) {
        if (state) {
            Log.i("successfully getting up");
        }
        else {
            Log.i("failed to get up");
        }
    }

    class ServiceManagerBinder extends Binder {
         public ServiceManager getServiceManager() {
             return ServiceManager.this;
         }
    }

    /*
    *   SET_ALARM is a trick to fix my design fault on set alarm
    * */
    public enum IntentType {
        INIT, ALERT, INTERRUPT, NOTIFICATION, SET_ALARM
    }
}
