package com.johnson.alarmClock;

import android.content.UriMatcher;
import android.net.Uri;

import junit.framework.TestCase;

public class AlarmClockProviderTest extends TestCase {
    public void testUriMatcher() throws Exception{
        AlarmClockProvider alarmClockProvider = new AlarmClockProvider();
        UriMatcher uriMatcher = alarmClockProvider.uriMatcher;
        assertEquals(AlarmClockProvider.ALARM, uriMatcher.match(Uri.parse("content://com.johnson.morningAssistant/alarm")));
        assertEquals(AlarmClockProvider.ALARM_ID, uriMatcher.match(Uri.parse("content://com.johnson.morningAssistant/alarm/3")));
        assertEquals("3", Uri.parse("content://com.johnson.morningAssistant/alarm/3").getPathSegments().get(1));
    }
}