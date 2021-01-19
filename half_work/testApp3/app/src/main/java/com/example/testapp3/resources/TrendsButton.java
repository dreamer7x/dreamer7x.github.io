package com.example.testapp3.resources;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

@SuppressLint("AppCompatCustomView")
public class TrendsButton extends Button {

    public int position;

    public TrendsButton(Context context) {
        super(context);
    }

    public TrendsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}