package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.testapp3.resources.Discuss;
import com.example.testapp3.resources.Reply;
import com.example.testapp3.tools.ReplyAdapter;

import java.util.List;

public class MoreReplyActivity extends AppCompatActivity {

    private ListView moreReplyListView;
    private Discuss discuss;
    private List<Discuss> discussList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_reply);
        initView();
    }

    public void initView(){
        Intent intent = getIntent();
        String replyString = intent.getStringExtra("replyString");
        String[] replyStrings = replyString.split("<spa>");
        if(replyStrings.length % 4 != 0){
            Log.d("MoreReplyActivity","错误: 回复信息获取不全");
            finish();
        }
        for(int i = 0;i < replyStrings.length / 4;i++){
            discussList.add(Discuss(replyStrings[i * 4],replyStrings[i * 4 + 1],replyStrings[i * 4 + 2]));
            discussList.add
        }
        moreReplyListView = findViewById(R.id.moreReplyListView);
        ReplyAdapter replyAdapter = ReplyAdapter(this,R.layout.trend_listview_resource01,);
        moreReplyListView.setAdapter(replyAdapter);
    }
}