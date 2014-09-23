package com.johnson.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by johnson on 9/23/14.
 */
public class RobotoThinTextView extends TextView{
    public RobotoThinTextView(Context context) {
        super(context);
        init();
    }

    public RobotoThinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoThinTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Thin.ttf");
        setTypeface(typeface);
    }
}
