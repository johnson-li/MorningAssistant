package com.johnson.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.johnson.Log;

/**
 * Created by johnson on 9/15/14.
 */
public class AlarmNotificationService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return 0;
    }

    protected void onHandleIntent(Intent intent) {
        Log.i("alarm notification clicked or cleared");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        Intent startIntent = new Intent(this, ServiceManager.class);
        startIntent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.NOTIFICATION);
        startService(startIntent);
    }
}
