package com.johnson.morningAssistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.johnson.service.ServiceManager;

/**
 * Created by johnson on 9/10/14.
 * This activity shows when the alarm time is arrived
 */
public class AlarmActivity extends Activity{
    static String LOG_TAG = AlarmActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        startRing();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void stopAlert() {
        stopRing();
        Intent intent = new Intent(this, ServiceManager.class);
        intent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.INTERRUPT);
        startService(intent);
        finish();
    }

    void startRing() {
        Log.d(LOG_TAG, "start ring...");
    }

    void stopRing() {
        Log.d(LOG_TAG, "stop ring...");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(LOG_TAG, "key pressed with code: " + getKeyName(keyCode));
        stopAlert();
        return true;
    }

    String getKeyName(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return "KEYCODE_VOLUME_UP";
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return "KEYCODE_VOLUME_DOWN";
            case KeyEvent.KEYCODE_BACK:
                return "KEYCODE_BACK";
            case KeyEvent.KEYCODE_MENU:
                return "KEYCODE_MENU";
            default:
                return String.valueOf(keyCode);
        }
    }
}
