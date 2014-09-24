package com.johnson.morningAssistant;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by johnson on 9/24/14.
 */
public class ScreenSlidePageFragment extends Fragment {
    int mPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutID = R.layout.fragment_screen_slide_page;
        switch (mPosition) {
            case 0:
                layoutID = R.layout.fragment_screen_slide_page0;
                break;
            case 1:
                layoutID = R.layout.fragment_screen_slide_page1;
                break;
            case 2:
                layoutID = R.layout.fragment_screen_slide_page2;
                break;
            case 3:
                layoutID = R.layout.fragment_screen_slide_page3;
                break;
            case 4:
                layoutID = R.layout.fragment_screen_slide_page4;
                break;
        }
        return inflater.inflate(layoutID, container, false);
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}
