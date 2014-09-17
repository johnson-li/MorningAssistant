package com.johnson;

import junit.framework.TestCase;

import java.util.Calendar;

public class LogTest extends TestCase {

    public void testGetLogClass() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -4);
        String date = String.format("%d.%d.%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Log.d(date);
        Thread.sleep(1000);
    }
}