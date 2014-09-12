package com.johnson;

import junit.framework.TestCase;

public class LogTest extends TestCase {

    public void testGetLogClass() throws Exception {
        Log.d("123");
        Thread.sleep(1000);
    }
}