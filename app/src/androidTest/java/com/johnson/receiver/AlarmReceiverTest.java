package com.johnson.receiver;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.johnson.alarmClock.AlarmClock;
import com.johnson.alarmClock.AlarmClockManager;

import java.util.Calendar;

public class AlarmReceiverTest extends AndroidTestCase {

    public void testOnHandleIntent() {
        AlarmClockManager.clearAlarm(mContext);
        Uri uri = AlarmClockManager.addAlarm(mContext);
        int alarmId = Integer.valueOf(uri.getPathSegments().get(1));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        AlarmClockManager.setAlarm(mContext, alarmId, true, calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), "label",
                new AlarmClock.DaysOfWeek());

    }

}