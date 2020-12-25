package com.example.testapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Freezable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FriendsActivity extends AppCompatActivity {

    private ListView friendsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initView();
    }

    public void initView(){
        friendsListView = findViewById(R.id.friendsListView);

        HttpConnection connection = new HttpConnection(ParameterKeeper.httpUrl + "/identity_chat");
        Map<String,String> request = new HashMap<String,String>();
        request.put("sActivityId",PublicDataKeeper.activityId);
        request.put("sServeType","0");
        connection.sendPOST(request);

        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String respond = connection.getData();
        String pattern = "<spa>";

    }

    public void onClickToMineButton(View view){
        Intent intent = new Intent(FriendsActivity.this,MineActivity.class);
        startActivity(intent);
    }

    public void onClickToTrendsButton(View view){
        Intent intent = new Intent(FriendsActivity.this,TrendsActivity.class);
        startActivity(intent);
    }

    public void onClickToTitleButton(View view){
        Intent intent = new Intent(FriendsActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}