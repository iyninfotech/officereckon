package com.infozealrecon.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class ButtonBold extends Button {

    public ButtonBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ButtonBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonBold(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_bold.ttf");
        setTypeface(tf);

    }
}