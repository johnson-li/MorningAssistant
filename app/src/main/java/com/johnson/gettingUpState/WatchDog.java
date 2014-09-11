package com.johnson.gettingUpState;

import android.os.Handler;
import android.os.Message;

import com.johnson.service.ServiceManager;


/**
 * Created by johnson on 9/11/14.
 * This thread sleep for 30 seconds, if no interrupt signal received then it tell the
 * service manager that getting up state is false
 */
public class WatchDog extends Monitor{
    public WatchDog(Handler handler) {
        super(handler);
    }

    @Override
    void startMonitor() {
        try {
            Thread.sleep(30 * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message message = new Message();
        message.what = ServiceManager.GETTING_UP_FAILED;
        handler.sendMessage(message);
    }

    @Override
    String getClassName() {
        return WatchDog.class.getSimpleName();
    }
}
