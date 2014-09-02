package com.johnson.alarmClock;

import junit.framework.TestCase;

public class DaysOfWeekTest extends TestCase {

    public void testToInt() throws Exception {
        /*
        *   DaysOfWeek int valid value ranges from 0 to ox7f,
        *   exceeding digits are ignored
        * */
        for (int i = 0; i < 0xff; i++) {
            AlarmClock.DaysOfWeek daysOfWeek = new AlarmClock.DaysOfWeek(i);
            assertEquals(daysOfWeek.toInt(), i & 0x7f);
        }
    }
}