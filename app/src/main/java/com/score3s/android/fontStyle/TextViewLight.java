package com.score3s.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewLight extends TextView {

    public TextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewLight(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_light.ttf");
        setTypeface(tf);

    }
}