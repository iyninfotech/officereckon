package com.infozealrecon.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class ButtonMedium extends Button {

    public ButtonMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ButtonMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonMedium(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_medium.ttf");
        setTypeface(tf);

    }
}