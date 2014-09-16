package com.johnson.utils;

import junit.framework.TestCase;

public class ShakeDataTest extends TestCase {
    public void testAll() throws Exception {
        ShakeData shakeData = new ShakeData();
        shakeData.add(1, 10);
        shakeData.add(2, 20);
        assertEquals(15, (int)shakeData.getAverage());
        shakeData.add(shakeData.timeWindow + 2, 30);
        assertEquals(25, (int)shakeData.getAverage());
        assertFalse(shakeData.triggered());
        shakeData.add(shakeData.timeWindow + 3, 500);
        assertTrue(shakeData.triggered());
    }
}