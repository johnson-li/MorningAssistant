package com.johnson.morningAssistant;

import android.content.Intent;
import android.test.AndroidTestCase;

public class AlarmActivityTest extends AndroidTestCase {

    public void testStopAlert() throws Exception {
        Intent intent = new Intent(mContext, AlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}