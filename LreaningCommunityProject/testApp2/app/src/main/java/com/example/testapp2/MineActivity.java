package com.example.testapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        initView();
    }

    public void initView(){

    }

    public void onClickToMineTrends(View view){
        Intent intent = new Intent(MineActivity.this,MineTrendsActivity.class);
        startActivity(intent);
    }
}