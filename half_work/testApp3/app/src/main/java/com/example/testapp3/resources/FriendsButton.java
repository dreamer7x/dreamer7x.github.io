package com.example.testapp3.resources;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

@SuppressLint("AppCompatCustomView")
public class FriendsButton extends Button {

    public int position;

    public FriendsButton(Context context) {
        super(context);
    }

    public FriendsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
