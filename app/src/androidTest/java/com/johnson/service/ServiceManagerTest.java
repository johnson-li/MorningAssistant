package com.johnson.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;
import android.util.Log;

import com.johnson.morningAssistant.MyActivity;

public class ServiceManagerTest extends AndroidTestCase {
    ServiceManager serviceManager;

    public void testServiceManagerBinder() {
        Log.d(MyActivity.LOG_TAG, "start test");
        Intent intent = new Intent(mContext, ServiceManager.class);
        mContext.stopService(intent);
        mContext.startService(intent);
//        mContext.bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                ServiceManager.ServiceManagerBinder serviceManagerBinder = (ServiceManager.ServiceManagerBinder)service;
//                serviceManager = serviceManagerBinder.getServiceManager();
//                Log.d(MyActivity.LOG_TAG, "service manager connected");
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                Log.d(MyActivity.LOG_TAG, "service manager disconnected");
//            }
//        }, Context.BIND_AUTO_CREATE);
    }

}