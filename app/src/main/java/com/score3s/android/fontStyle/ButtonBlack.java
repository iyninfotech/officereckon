package com.score3s.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class ButtonBlack extends Button {

    public ButtonBlack(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ButtonBlack(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonBlack(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_black.ttf");
        setTypeface(tf);

    }
}