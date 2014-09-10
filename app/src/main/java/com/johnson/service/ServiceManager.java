package com.johnson.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

/**
 * Created by johnson on 8/31/14.
 * This is the service running background as the rule of alarm clock
 */
public class ServiceManager extends Service{
    public static final String INTENT_TYPE = "intentType";
    public static final String LOG_TAG = ServiceManager.class.getSimpleName();

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
        IntentType intentType = (IntentType)intent.getSerializableExtra(INTENT_TYPE);
        switch (intentType) {
            case DEFAULT:
                break;
            case INIT:
                Log.i(LOG_TAG, "initiated");
                break;
            case ALERT:
                Log.i(LOG_TAG, "alert message received");
                break;
            default:
        }
    }

    class ServiceManagerBinder extends Binder {
         public ServiceManager getServiceManager() {
             return ServiceManager.this;
         }
    }

    public enum IntentType {
        DEFAULT, INIT, ALERT
    }
}
