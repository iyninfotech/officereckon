package com.infozealrecon.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class Spinner extends MaterialBetterSpinner {

    public Spinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Spinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Spinner(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_bold.ttf");
        setTypeface(tf);

    }
}