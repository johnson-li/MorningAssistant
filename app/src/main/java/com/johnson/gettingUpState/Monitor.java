package com.johnson.gettingUpState;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.johnson.service.ServiceManager;

/**
 * Created by johnson on 9/11/14.
 * An abstract class that all monitor should extend
 */
public abstract class Monitor extends Thread{
    Handler handler;
    Context mContext;
    static String LOG_TAG = Monitor.class.getSimpleName();
    public Monitor(Handler handler, Context mContext) {
        this.mContext = mContext;
        this.handler = handler;
    }

    @Override
    public final void run() {
        try {
            startMonitor();

        }
        catch (InterruptedException e) {
            Log.i(LOG_TAG, "interrupted exception received, the thread should be interrupted: " + getClassName());
        }
    }

    void sendGettingUp(boolean status) {
        Message message = new Message();
        if (status) {
            message.what = ServiceManager.GETTING_UP_SUCCESS;
        }
        else {
            message.what = ServiceManager.GETTING_UP_FAILED;
        }
        handler.sendMessage(message);
    }

    abstract void startMonitor() throws InterruptedException;

    public abstract String getClassName();
}
