package com.johnson.morningAssistant;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechUnderstander;
import com.iflytek.speech.SpeechUnderstanderListener;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.TextUnderstander;
import com.iflytek.speech.UnderstanderResult;
import com.johnson.Log;
import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;
import com.johnson.alarmStrategy.Strategy;
import com.johnson.service.ServiceManager;
import com.johnson.speech.XmlParser;
import com.johnson.utils.ApkInstaller;
import com.johnson.utils.Preferences;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyActivity extends Activity {
    public static final String LOG_TAG = "johnsonLog";
    public static final int NOTIFICATION_ID = 0;
    static final int ANIMATION_DURATION = 1000;
    Toast mToast;
    InitListener initListener = new InitListener() {
        @Override
        public void onInit(ISpeechModule iSpeechModule, int i) {
            //Do nothing
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Preferences.setContext(this);
        Strategy.setContext(this);
        if (Preferences.useVoiceEngine()) {
            initVoiceEngine();
        }
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        initButton();
        if (Preferences.firstStart()) {
            startActivity(new Intent(this, ScreenSlideActivity.class));
            Preferences.finishFirstHelp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initButton() {

        findViewById(R.id.power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                *   @TODO judging whether alarm is set by service manager's status may be not accurate
                * */
                if (isServiceRunning(ServiceManager.class)) {
                    stopMorningAssistant();
                }
                else {
                    if (Preferences.useVoiceToStart()) {
                        startMorningAssistantByVoice();
                    }
                    else {
                        startMorningAssistant();
                    }
                }
            }
        });
        setPowerImage(isServiceRunning(ServiceManager.class));
    }

    void setPowerImage(boolean status) {
        showAlarmIcon(status);
        if (status) {
            setAlarmAnimation();
            findViewById(R.id.power).setBackgroundResource(R.drawable.pressed);
        }
        else {
            unsetAlarmAnimation();
            findViewById(R.id.power).setBackgroundResource(R.drawable.unpressed);
        }
    }

    void setAlarmAnimation() {
        TextView noAlarm = (TextView)findViewById(R.id.noAlarm);
        TextView alarmTime = (TextView)findViewById(R.id.alarmTime);
        String str = String.format("%2d  :  %2d", Preferences.getAlarmHour(), Preferences.getAlarmMinute());
        alarmTime.setText(str);
        AnimationSet animationSet = new AnimationSet(true);
        int height = noAlarm.getLineHeight();
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        Animation translateAnimation = new TranslateAnimation(0, 0, 0, -height);
        alphaAnimation.setDuration(ANIMATION_DURATION);
        translateAnimation.setDuration(ANIMATION_DURATION);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillAfter(true);
        noAlarm.startAnimation(animationSet);

        alphaAnimation = new AlphaAnimation(0, 1);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        alarmTime.startAnimation(animationSet);

    }

    void unsetAlarmAnimation() {
        TextView noAlarm = (TextView)findViewById(R.id.noAlarm);
        TextView alarmTime = (TextView)findViewById(R.id.alarmTime);
        AnimationSet animationSet = new AnimationSet(true);
        int height = noAlarm.getLineHeight();
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        Animation translateAnimation = new TranslateAnimation(0, 0, -height, 0);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        noAlarm.startAnimation(animationSet);

        alphaAnimation = new AlphaAnimation(1, 0);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        alarmTime.startAnimation(animationSet);

    }

    void initVoiceEngine() {
        //4d6774d0
        //5411bf4e
        if (!checkSpeechServiceInstall()) {
//            Dialog dialog = new Dialog(this);
//            LayoutInflater layoutInflater = getLayoutInflater();
//            View alertDialogView = layoutInflater.inflate(R.layout.);
//            dialog.setContentView(alertDialogView);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You should install voice engine first");
            builder.setTitle("warning");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String url = SpeechUtility.getUtility(MyActivity.this).getComponentUrl();
                    String assetsApk = "SpeechService.apk";
                    if (!processInstall(url, assetsApk)) {
                        Log.e("Voice engine initiation error");
                    }
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
        }
        SpeechUtility.getUtility(MyActivity.this).setAppid("4d6774d0");
    }

    void startMorningAssistant() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setPowerImage(true);
                addAlarmClock(hourOfDay, minute, 0);
                Preferences.setAlarmTime(hourOfDay, minute);
                startNotification(hourOfDay, minute);
                startServiceManager();
            }
        }, Preferences.getAlarmHour(), Preferences.getAlarmMinute(), Preferences.use24HourMode());
        timePickerDialog.show();
    }

    @Deprecated
    void startMorningAssistantIn5Seconds() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        addAlarmClock(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        setPowerImage(true);
        startNotification(hour, minute);
        startServiceManager();
    }

    void startMorningAssistantByVoice() {
        final int SUCCESS = 0;
        final int FAIL = -1;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESS:
                        break;
                    case FAIL:
                        showTip("speech recognition failed");
                        break;
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                SpeechUnderstander speechUnderstander = new SpeechUnderstander(MyActivity.this, initListener);
                int code = speechUnderstander.startUnderstanding(new SpeechUnderstanderListener.Stub() {
                    @Override
                    public void onVolumeChanged(int v) throws RemoteException {
                        showTip("onVolumeChanged："	+ v);
                    }

                    @Override
                    public void onError(int errorCode) throws RemoteException {
                        showTip("onError Code："	+ errorCode);
                    }

                    @Override
                    public void onEndOfSpeech() throws RemoteException {
                        showTip("onEndOfSpeech");
                    }

                    @Override
                    public void onBeginOfSpeech() throws RemoteException {
                        showTip("onBeginOfSpeech");
                    }

                    @Override
                    public void onResult(final UnderstanderResult result) throws RemoteException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != result) {
                                    String resultStr = result.getResultString();
                                    Log.d("understander result：" + result.getResultString());
                                    XmlParser xmlParser = new XmlParser(result.getResultString());
                                    if (xmlParser.getResult()) {
                                        int hour = xmlParser.getHour();
                                        int minute = xmlParser.getMinute();
                                        int second = xmlParser.getSecond();
                                        Preferences.setAlarmTime(hour, minute);
                                        addAlarmClock(hour, minute, second);
                                        startNotification(hour, minute);
                                        startServiceManager();
                                        Message message = new Message();
                                        message.what = SUCCESS;
                                        handler.sendMessage(message);
                                        return;
                                    }
                                }
                                Log.d("understander result:null");
                                Message message = new Message();
                                message.what = FAIL;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
            }
        }.start();
    }

    void stopMorningAssistant() {
        clearAlarmClock();
        stopNotification();
        stopServiceManager();
        setPowerImage(false);
    }

    void showAlarmIcon(boolean b) {
        Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
        intent.putExtra("alarmSet", b);
        sendBroadcast(intent);
    }

    void addAlarmClock(int hour, int minute, int second) {
        Strategy.setTargetTime(hour, minute, second);
        /*
        *   set alarm operations are moved into strategy
        * */
//        AlarmClockManager.clearAlarm(this);
//        Uri uri = AlarmClockManager.addAlarm(this);
//        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
//        AlarmClockManager.setAlarm(this, alarmId, true, hour, minute, second, "label", new AlarmClock.DaysOfWeek().addAll());
    }

    void clearAlarmClock() {
        AlarmClockManager.clearAlarm(this);
    }

    void startNotification(int hour, int minute) {
        Log.i("start notification");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Morning Assistant").setContentText(hour + ":" + minute);
        Intent intent = new Intent(this, MyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    void stopNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    void startServiceManager() {
        Intent intent = new Intent(this, ServiceManager.class);
        intent.putExtra(ServiceManager.INTENT_TYPE, ServiceManager.IntentType.INIT);
        this.startService(intent);
    }

    void stopServiceManager() {
        Intent intent = new Intent(this, ServiceManager.class);
        stopService(intent);
    }

    boolean isServiceRunning(Class clazz) {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    boolean checkSpeechServiceInstall(){
        String packageName = "com.iflytek.speechcloud";
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for(PackageInfo packageInfo: packages) {
            if (packageInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    boolean processInstall(String url, String assetsApk) {
        if (ApkInstaller.installFromAssets(this, assetsApk)) {
            Log.i("voice engine installation report success");
            return true;
        }
        else {
            Log.w("voice engine installation report fail");
            return false;
        }
    }
}
