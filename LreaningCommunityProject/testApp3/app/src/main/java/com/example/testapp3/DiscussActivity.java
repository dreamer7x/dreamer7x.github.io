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
import com.example.testapp3.tools.HttpConnection;

import java.util.HashMap;
import java.util.Map;

public class DiscussActivity extends AppCompatActivity {

    // 参数
    private String trendsId;

    // 组件
    private EditText discussEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        initView();
    }

    public void initView(){
        discussEditText = findViewById(R.id.discussEditText);
        Intent intent = getIntent();
        trendsId = intent.getStringExtra("trendsId");
        if(trendsId == null){
            Log.d("DiscussActivity","错误: 动态参数未提供");
            finish();
        }
    }

    // 监听器
    public void onClickSendDiscussButton(View view){
        String content = String.valueOf(discussEditText.getText());
        if(content.equals("null")){
            discussEditText.setHint("评论为空");
            return;
        }
        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
        Map<String,String> request = new HashMap<>();
        request.put("sActivityId", DataKeeper.activityId);
        request.put("sServeType","4");
        request.put("sDiscuss",content);
        request.put("sTrendId",trendsId);
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
            Log.d("DiscussActivity","错误: 发送评论失败");
            Intent intent = new Intent();
            intent.putExtra("isSendDiscuss",false);
            setResult(RESULT_OK,intent);
            finish();
        }
        else{
            switch (respond.charAt(0)){
                case '0':
                    Intent intent = new Intent();
                    intent.putExtra("isSendDiscuss",true);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;

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