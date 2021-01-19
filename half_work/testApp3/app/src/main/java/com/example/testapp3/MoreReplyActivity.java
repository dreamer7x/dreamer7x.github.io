package com.example.testapp3;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.testapp3.resources.Discuss;
import com.example.testapp3.resources.Reply;
import com.example.testapp3.tools.ReplyAdapter;

import java.util.ArrayList;
import java.util.List;

public class MoreReplyActivity extends AppCompatActivity {

    private Discuss discuss;
    private ListView moreReplyListView;
    private List<Reply> replyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_reply);
        initView();
    }

//    discussString = discussString + discuss.staticId + "<spa>"
//    discussString = discussString + discuss.username + "<spa>";
//    discussString = discussString + discuss.motto + "<spa>";
//    discussString = discussString + discuss.headPicture + "<spa>";
//    discussString = discussString + discuss.praiseNumber + "<spa>";
//    discussString = discussString + discuss.replyNumber + "<spa>";
//    discussString = discussString + (discuss.isPraise ? "true" : "false") + "<spa>";
//    discussString = discussString + (discuss.isReply ? "true" : "false") + "<spa>";

    public void initView(){
        replyList = new ArrayList<>();
        Intent intent = getIntent();
        String replyString = intent.getStringExtra("replyString");
        String[] replyStrings = replyString.split("<spa>");
        if(replyStrings.length % 4 != 0){
            Log.d("MoreReplyActivity","错误: 回复信息获取不全");
            finish();
        }
        for(int i = 0;i < replyStrings.length / 4;i++){
            replyList.add(new Reply(replyStrings[4 * i],
                    replyStrings[4 * i + 1],
                    Integer.parseInt(replyStrings[4 * i + 2]),
                    replyStrings[4 * i + 3].equals("true")));
        }
        String discussString = intent.getStringExtra("discussString");
        String[] discussStrings = discussString.split("<spa>");
        if(discussStrings.length != 8){
            Log.d("MoreReplyActivity","错误: 评论信息获取不全");
            finish();
        }
        discuss = new Discuss(discussStrings[0], discussStrings[1],
                Integer.parseInt(discussStrings[3]),Integer.parseInt(discussStrings[4]),
                discussStrings[5].equals("true"),discussStrings[6].equals("true"));
        discuss.headPicture = discussStrings[2];
        moreReplyListView = findViewById(R.id.moreReplyListView);
        ReplyAdapter replyAdapter = new ReplyAdapter(this,R.layout.trend_listview_resource01,replyList,
                discuss.staticId,discuss.username,discuss.motto,discuss.headPicture, discuss.discuss,
                discuss.praiseNumber,discuss.replyNumber,discuss.isPraise,discuss.isReply);
        moreReplyListView.setAdapter(replyAdapter);
    }
}