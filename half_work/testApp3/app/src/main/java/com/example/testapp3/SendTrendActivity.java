package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.tools.HttpConnection;

import java.util.HashMap;
import java.util.Map;

public class SendTrendActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText textEditText;
    private Button addPictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_trend);
        initView();
    }

    public void initView(){
        addPictureButton = findViewById(R.id.sendTrendAddPictureButton);
        titleEditText = findViewById(R.id.sendTrendTitleTextView);
        textEditText = findViewById(R.id.sendTrendTextEditText);
    }

    public void onCLickSendTrendFinishButton(View view){
        finish();
    }

    public void onClickSendTrendSendButton(View view){
        // HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/picture")
        // 上传图片数据 获取图片文件名

        String title = String.valueOf(titleEditText.getText());
        if(title.equals("null")){
            titleEditText.setHintTextColor(Color.rgb(223,78,78));
            titleEditText.setText("标题为空");
            return;
        }
        String text = String.valueOf(textEditText.getText());
        if(text.equals("null")){
            textEditText.setHintTextColor(Color.rgb(223,78,78));
            textEditText.setText("内容为空");
            return;
        }
        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
        Map<String,String> request = new HashMap<>();
        request.put("sActivityId", DataKeeper.activityId);
        request.put("sTitle",title);
        request.put("sText",text);
        request.put("sServeType","5");
        connection.sendPOST(request);
        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        if(respond == null){
            Log.d("SendTrendActivity","错误: 发布动态反馈结果为空");
            return;
        }
        else{
            switch (respond.charAt(0)){
                case '0':
                    String trendsId = respond.substring(1);
                    Trends trends = new Trends(trendsId);
                    trends.isPraise = false;
                    trends.staticId = DataKeeper.staticId;
                    trends.username = DataKeeper.username;
                    trends.motto = DataKeeper.motto;
                    trends.headPicturePosition = DataKeeper.headPicture;
                    // trends.picture = 保存图片信息
                    DataKeeper.oldTrendsList.add(0,trends);
                    finish();

                case '1':
                    Log.d("MainActivity", "要求重新登录");
                    setContentView(R.layout.answer);
                    TextView textView = findViewById(R.id.answerTextView);
                    textView.setText("要求重新登陆");
                    return;
            }
        }
    }
}