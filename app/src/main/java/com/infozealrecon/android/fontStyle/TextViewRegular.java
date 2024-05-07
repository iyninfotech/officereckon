package com.infozealrecon.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRegular extends TextView {

    public TextViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRegular(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_regular.ttf");
        setTypeface(tf);

    }
}