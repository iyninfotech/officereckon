package com.infozealrecon.android.fontStyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class EdittextLight extends EditText {

    public EdittextLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EdittextLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EdittextLight(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "lato_light.ttf");
        setTypeface(tf);

    }
}