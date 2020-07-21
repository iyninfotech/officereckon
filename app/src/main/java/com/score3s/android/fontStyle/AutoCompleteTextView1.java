package com.score3s.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class AutoCompleteTextView1 extends AutoCompleteTextView {

    public AutoCompleteTextView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AutoCompleteTextView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCompleteTextView1(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_black.ttf");
        setTypeface(tf);

    }
}