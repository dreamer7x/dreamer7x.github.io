package com.example.testapp3.resources;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TrendsLinearLayout extends LinearLayout {

    public int position;
    public String staticId;

    public TrendsLinearLayout(Context context) {
        super(context);
    }

    public TrendsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
