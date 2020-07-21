package com.score3s.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBold extends TextView {

    public TextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewBold(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_bold.ttf");
        setTypeface(tf);

    }
}