package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.tools.HttpConnection;
import com.example.testapp3.tools.OldTrendsAdapter;
import com.example.testapp3.tools.OtherMineTrendsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherTrendsActivity extends AppCompatActivity {

    private Button toTrendsButton;
    private Button toCollectButton;
    private Button toTeamButton;
    private ImageView headPictureImageView;
    private TextView usernameTextView;
    private TextView mottoTextView;
    private TextView attentionsNumberTextView;
    private TextView fansNumberTextView;
    private Button addFriendsButton;
    private Button addAttentionButton;
    private LinearLayout backgroundLinearLayout;
    private ListView otherTrendsListView;

    // 参数
    private int position;
    private List<Trends> trendsList = new ArrayList<>();
    private List<Trends> collectTrendsList = new ArrayList<>();
    private String staticId;
    private String username;
    private String motto;
    private int attentionsNumber;
    private int fansNumber;
    private boolean isFriends;
    private boolean isAttentions;
    private boolean isGetCollectTrends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_trends);
        initView();
    }

    public void initView(){
        Intent intent = getIntent();

        usernameTextView = findViewById(R.id.otherTrendsUsernameTextView);
        mottoTextView = findViewById(R.id.otherTrendsMottoTextView);
        attentionsNumberTextView = findViewById(R.id.otherTrendsAttentionsNumberTextView);
        fansNumberTextView = findViewById(R.id.otherTrendsFansNumberTextView);
        headPictureImageView = findViewById(R.id.otherTrendsHeadPictureImageView);
        backgroundLinearLayout = findViewById(R.id.otherTrendsBackgroundLinearLayout);
        addAttentionButton = findViewById(R.id.otherTrendsAttentionButton);
        addFriendsButton = findViewById(R.id.otherTrendsFriendButton);
        toCollectButton = findViewById(R.id.otherTrendsCollectButton);
        toTrendsButton = findViewById(R.id.otherTrendsTrendsButton);
        toTeamButton = findViewById(R.id.otherTrendsTeamButton);
        otherTrendsListView = findViewById(R.id.otherTrendsListView);

        isFriends = false;
        isAttentions = false;

        /**
         * 0 = 来自历史动态
         * 1 = 来自好友动态
         * 2 = 来自收藏动态
         * 3 = 来自历史聊天动态
         * 4 = 来自关注动态
         * 5 = 来自粉丝动态
         * 6 = 来自其他动态
         */

        switch (intent.getIntExtra("from",4)){
            case 0: // 来自于历史动态的动态发布者信息访问
                int position = intent.getIntExtra("position",-1);
                if (position == -1) {
                    Log.d("OtherTrendsActivity","错误: 未获得有效的position");
                    finish();
                }
                else{
                    Log.d("OtherTrendsActivity",String.valueOf(position));
                }
                username = DataKeeper.oldTrendsList.get(position).username;
                usernameTextView.setText(username);
                motto = DataKeeper.oldTrendsList.get(position).motto;
                mottoTextView.setText(motto);
                staticId = DataKeeper.oldTrendsList.get(position).staticId;

                isAttentions = true;
                addAttentionButton.setBackgroundColor(Color.WHITE);
                addAttentionButton.setTextColor(Color.rgb(221,221,221));
                addAttentionButton.setText("- 关注");
                for(int i = 0;i < DataKeeper.friendsFriendsUserList.size();i++){
                    if(DataKeeper.friendsFriendsUserList.get(i).staticId.equals(staticId)){
                        addFriendsButton.setBackgroundColor(Color.WHITE);
                        addFriendsButton.setTextColor(Color.rgb(221,221,221));
                        addFriendsButton.setText("+ 聊天");
                        isFriends = true;
                    }
                }
                break;

            case 1: // 来自好友列表的动态访问
                position = intent.getIntExtra("position",-1);
                if(position == -1){
                    Log.d("OtherTrendsActivity","错误: 动态访问定向信息不全");
                    finish();
                }
                username = DataKeeper.friendsFriendsUserList.get(position).username;
                usernameTextView.setText(username);
                motto = DataKeeper.friendsFriendsUserList.get(position).motto;
                mottoTextView.setText(motto);
                staticId = DataKeeper.friendsFriendsUserList.get(position).staticId;

                for(int i = 0;i < DataKeeper.attentionsUsers.size();i++){
                    if(staticId.equals(DataKeeper.attentionsUsers.get(i).staticId)){
                        addAttentionButton.setBackgroundColor(Color.WHITE);
                        addAttentionButton.setTextColor(Color.rgb(221,221,221));
                        addAttentionButton.setText("- 关注");
                        isAttentions = true;
                        break;
                    }
                }
                addFriendsButton.setBackgroundColor(Color.WHITE);
                addFriendsButton.setTextColor(Color.rgb(221,221,221));
                addFriendsButton.setText("+ 聊天");
                isFriends = true;
                break;

            case 2: // 来自收藏动态列表的动态访问
                position = intent.getIntExtra("position",-1);
                if(position == -1){
                    Log.d("OtherTrendsActivity","错误: 动态访问定向信息不全");
                    finish();
                }
                username = DataKeeper.collectTrendsList.get(position).username;
                usernameTextView.setText(username);
                motto = DataKeeper.collectTrendsList.get(position).motto;
                mottoTextView.setText(motto);
                staticId = DataKeeper.collectTrendsList.get(position).staticId;

                for(int i = 0;i < DataKeeper.attentionsUsers.size();i++){
                    if(staticId.equals(DataKeeper.attentionsUsers.get(i).staticId)){
                        addAttentionButton.setBackgroundColor(Color.WHITE);
                        addAttentionButton.setTextColor(Color.rgb(221,221,221));
                        addAttentionButton.setText("- 关注");
                        isAttentions = true;
                        break;
                    }
                }
                for(int i = 0;i < DataKeeper.friendsFriendsUserList.size();i++){
                    if(DataKeeper.friendsFriendsUserList.get(i).staticId.equals(staticId)){
                        addFriendsButton.setBackgroundColor(Color.WHITE);
                        addFriendsButton.setTextColor(Color.rgb(221,221,221));
                        addFriendsButton.setText("+ 聊天");
                        isFriends = true;
                        break;
                    }
                }

            case 3:
                position = intent.getIntExtra("position",-1);
                if(position == -1){
                    Log.d("OtherTrendsActivity","错误: 动态访问定向信息不全");
                    finish();
                }
                username = DataKeeper.friendsChatUserList.get(position).username;
                usernameTextView.setText(username);
                motto = DataKeeper.friendsChatUserList.get(position).motto;
                mottoTextView.setText(motto);
                staticId = DataKeeper.friendsChatUserList.get(position).staticId;

                for(int i = 0;i < DataKeeper.attentionsUsers.size();i++){
                    if(staticId.equals(DataKeeper.attentionsUsers.get(i).staticId)){
                        addAttentionButton.setBackgroundColor(Color.WHITE);
                        addAttentionButton.setTextColor(Color.rgb(221,221,221));
                        addAttentionButton.setText("- 关注");
                        isAttentions = true;
                        break;
                    }
                }
                addFriendsButton.setBackgroundColor(Color.WHITE);
                addFriendsButton.setTextColor(Color.rgb(221,221,221));
                addFriendsButton.setText("+ 聊天");
                isFriends = true;
                break;

            case 4:
                position = intent.getIntExtra("position",-1);
                if (position == -1) {
                    Log.d("OtherTrendsActivity","错误: 动态访问定向信息不全");
                    finish();
                }
                username = DataKeeper.attentionsUsers.get(position).username;
                usernameTextView.setText(username);
                motto = DataKeeper.attentionsUsers.get(position).motto;
                mottoTextView.setText(motto);

                isAttentions = true;
                addAttentionButton.setBackgroundColor(Color.WHITE);
                addAttentionButton.setTextColor(Color.rgb(221,221,221));
                addAttentionButton.setText("- 关注");
                for(int i = 0;i < DataKeeper.friendsFriendsUserList.size();i++){
                    if(DataKeeper.friendsFriendsUserList.get(i).staticId.equals(staticId)){
                        addFriendsButton.setBackgroundColor(Color.WHITE);
                        addFriendsButton.setTextColor(Color.rgb(221,221,221));
                        addFriendsButton.setText("- 聊天");
                        isFriends = true;
                    }
                }
                break;

            case 5:
                position = intent.getIntExtra("position",-1);
                if(position == -1){
                    Log.d("OtherTrendsActivity","错误: 动态访问定向信息不全");
                    finish();
                }
                username = DataKeeper.fansUsers.get(position).username;
                usernameTextView.setText(username);
                motto = DataKeeper.fansUsers.get(position).motto;
                mottoTextView.setText(motto);

                isAttentions = false;
                isFriends = false;
                break;

            case 6:
                staticId = intent.getStringExtra("staticId");
                if(staticId == null){
                    Log.d("OtherTrendsActivity","错误: 没有传递相关关键参数");
                    finish();
                }
            {
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
                Map<String,String> request = new HashMap<>();
                request.put("sActivityId",DataKeeper.activityId);
                request.put("sStaticId",staticId);
                request.put("sServeType","1");
                connection.sendPOST(request);
                while (connection.getOnWork() != 2){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String respond = connection.getData();
                if(respond == null){
                    Log.d("OtherTrendsActivity","错误: 用户关注粉丝信息获取为空");
                }
                else{
                    switch (respond.charAt(0)){
                        case '0':
                            if(respond.length() == 1){
                                Log.d("OtherTrendsActivity","警告: 用户信息获取为空");
                                break;
                            }
                            String[] identitySigns = respond.substring(1).split("\\s");
                            if(identitySigns.length != 4){
                                Log.d("OtherTrendsActivity","警告: 用户信息获取不全");
                                finish();
                            }
                            username = identitySigns[1];
                            usernameTextView.setText(username);
                            motto = identitySigns[2];
                            mottoTextView.setText(motto);
                            break;

                        case '1':
                            Log.d("OtherActivity", "要求重新登录");
                            setContentView(R.layout.answer);
                            TextView textView = findViewById(R.id.answerTextView);
                            textView.setText("要求重新登陆");
                            return;
                    }
                }
            }
                for(int i = 0;i < DataKeeper.attentionsUsers.size();i++){
                    if(staticId.equals(DataKeeper.attentionsUsers.get(i).staticId)){
                        addAttentionButton.setBackgroundColor(Color.WHITE);
                        addAttentionButton.setTextColor(Color.rgb(221,221,221));
                        addAttentionButton.setText("- 关注");
                        isAttentions = true;
                        break;
                    }
                }
                for(int i = 0;i < DataKeeper.friendsFriendsUserList.size();i++){
                    if(staticId.equals(DataKeeper.friendsFriendsUserList.get(i).staticId)){
                        addAttentionButton.setBackgroundColor(Color.WHITE);
                        addAttentionButton.setTextColor(Color.rgb(221,221,221));
                        addAttentionButton.setText("+ 聊天");
                        isAttentions = true;
                        break;
                    }
                }
                break;
        }

        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_attentions_fans");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sStaticId",staticId);
            request.put("sServeType","1");
            connection.sendPOST(request);
            while (connection.getOnWork() != 2){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String respond = connection.getData();
            if(respond == null){
                Log.d("OtherTrendsActivity","错误: 用户关注粉丝信息获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            Log.d("OtherTrendsActivity","警告: 用户关注粉丝信息获取为空");
                            break;
                        }
                        String[] identityAttentionsFans = respond.substring(1).split("\\s");
                        attentionsNumber = Integer.parseInt(identityAttentionsFans[0]);
                        fansNumber = Integer.parseInt(identityAttentionsFans[1]);
                        attentionsNumberTextView.setText(String.valueOf(attentionsNumber));
                        fansNumberTextView.setText(String.valueOf(fansNumber));
                        break;

                    case '1':
                        Log.d("OtherTrendsActivity", "要求重新登录");
                        setContentView(R.layout.answer);
                        TextView textView = findViewById(R.id.answerTextView);
                        textView.setText("要求重新登陆");
                        return;
                }
            }
        }

        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_trends");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sStaticId",staticId);
            request.put("sServeType","3");
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
                Log.d("OtherTrendsActivity","错误: 所属动态信息为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            Log.d("OtherTrendsActivity","警告: 动态信息为空");
                            break;
                        }
                        if(respond.substring(0,5).equals("1<spa>")){
                            String[] trendsString = respond.substring(1).split("<spa>");
                            String[] collectTrendsStrings = trendsString[0].split("\\s");
                            for(int i = 0;i < collectTrendsStrings.length;i++){
                                collectTrendsList.add(new Trends(collectTrendsStrings[i]));
                            }
                        }
                        else{
                            String[] trendsString = respond.substring(1).split("<spa>");
                            if(trendsString.length == 1){
                                String[] trendsStrings = trendsString[0].split("\\s");
                                for(int i = 0;i < trendsStrings.length;i++){
                                    trendsList.add(new Trends(trendsStrings[i]));
                                }
                            }
                            else{
                                String[] trendsStrings = trendsString[0].split("\\s");
                                for(int i = 0;i < trendsStrings.length;i++){
                                    trendsList.add(new Trends(trendsStrings[i]));
                                }
                                String[] collectTrendsStrings = trendsString[1].split("\\s");
                                for(int i = 0;i < collectTrendsStrings.length;i++){
                                    collectTrendsList.add(new Trends(collectTrendsStrings[i]));
                                }
                            }
                        }
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

        if(trendsList.size() != 0){
            String trendsId = "";
            for(int i = 0;i < trendsList.size();i++){
                trendsId = trendsId + trendsList.get(i).trendsId + ",";
            }
            trendsId = "[" + trendsId.substring(0,trendsId.length() - 1) + "]";
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","0");
            request.put("sTrendsId",trendsId);
            connection.sendPOST(request);
            while (connection.getOnWork() != 2){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String respond = connection.getData();
            if(respond == null){
                Log.d("OtherTrendsActivity","错误: 所属动态具体信息获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            Log.d("OtherTrendsActivity","警告: 所属动态具体信息获取为空");
                            break;
                        }
                        String[] trendsString = respond.substring(1).split("<spa1>");
                        if(trendsString.length % 2 != 0){
                            Log.d("OtherTrendsActivity","错误: 所属动态具体信息获取不全");
                            break;
                        }
                        for(int i = 0;i < trendsString.length / 2;i++){
                            for(int j = 0;j < trendsList.size();j++) {
                                if (trendsList.get(j).trendsId.equals(trendsString[i * 2])){
                                    String[] trendsStrings = trendsString[i * 2 + 1].split("<spa>");
                                    trendsList.get(j).staticId = staticId;
                                    trendsList.get(j).title = trendsStrings[1];
                                    trendsList.get(j).text = trendsStrings[2];
                                    trendsList.get(j).picture = trendsStrings[3];
                                    trendsList.get(j).praiseNumber = Integer.parseInt(trendsStrings[4]);
                                    trendsList.get(j).discussNumber = Integer.parseInt(trendsStrings[5]);
                                    if(trendsStrings[6].equals("true")){
                                        trendsList.get(j).isPraise = true;
                                    }
                                    else{
                                        trendsList.get(j).isPraise = false;
                                    }
                                }
                            }
                        }
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

        if(collectTrendsList.size() != 0){
            String trendsId = "";
            for(int i = 0;i < collectTrendsList.size();i++){
                trendsId = trendsId + collectTrendsList.get(i).trendsId + ",";
            }
            trendsId = "[" + trendsId.substring(0,trendsId.length() - 1) + "]";
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","0");
            request.put("sTrendsId",trendsId);
            connection.sendPOST(request);
            while (connection.getOnWork() != 2){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String respond = connection.getData();
            if(respond == null){
                Log.d("OtherTrendsActivity","错误: 收藏动态具体信息获取为空");
            }
            else {
                switch (respond.charAt(0)) {
                    case '0':
                        if (respond.length() == 1) {
                            Log.d("OtherTrendsActivity","警告: 收藏动态具体信息获取为空");
                            break;
                        }
                        String[] trendsString = respond.substring(1).split("<spa1>");
                        if(trendsString.length % 2 != 0){
                            Log.d("OtherTrendsActivity","错误: 所属动态具体信息获取不全");
                            break;
                        }
                        for(int i = 0;i < trendsString.length / 2;i++){
                            for(int j = 0;j < collectTrendsList.size();j++) {
                                if (collectTrendsList.get(j).trendsId.equals(trendsString[i * 2])){
                                    String[] trendsStrings = trendsString[i * 2 + 1].split("<spa>");
                                    collectTrendsList.get(j).staticId = staticId;
                                    collectTrendsList.get(j).title = trendsStrings[1];
                                    collectTrendsList.get(j).text = trendsStrings[2];
                                    collectTrendsList.get(j).picture = trendsStrings[3];
                                    collectTrendsList.get(j).praiseNumber = Integer.parseInt(trendsStrings[4]);
                                    collectTrendsList.get(j).discussNumber = Integer.parseInt(trendsStrings[5]);
                                    if(trendsStrings[6].equals("true")){
                                        collectTrendsList.get(j).isPraise = true;
                                    }
                                    else{
                                        collectTrendsList.get(j).isPraise = false;
                                    }
                                }
                            }
                        }
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

        OtherMineTrendsAdapter otherMineTrendsAdapter = new OtherMineTrendsAdapter(this,
                R.layout.mine_trends_listview_resource,
                trendsList);
        otherTrendsListView.setAdapter(otherMineTrendsAdapter);
        position = 0;
        isGetCollectTrends = false;
    }

    // 监听函数
    public void onClickOtherTrendsFinishButton(View view){
        finish();
    }

    public void onClickToTeamButton(View view){
        if(position == 2){
            return;
        }
    }

    public void onClickToCollectTrendsButton(View view){
        if(position == 1){
            return;
        }
        if(isGetCollectTrends == false) {
            if(collectTrendsList.size() != 0){
                String staticId = "";
                for(int i = 0;i < collectTrendsList.size();i++){
                    staticId = staticId + collectTrendsList.get(i).staticId + ",";
                }
                staticId = "[" + staticId.substring(0,staticId.length() - 1) + "]";
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
                Map<String,String> request = new HashMap<>();
                request.put("sActivityId",DataKeeper.activityId);
                request.put("sServeType","1");
                request.put("sStaticId",staticId);
                connection.sendPOST(request);
                while (connection.getOnWork() != 2){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String respond = connection.getData();
                if(respond != null){
                    Log.d("MineTrendsActivity", "错误: 收藏动态发布者用户信息获取为空");
                }
                else{
                    switch (respond.charAt(0)){
                        case '0':
                            if(respond.length() == 1){
                                Log.d("MineTrendsActivity", "警告: 收藏动态发布者用户信息获取为空");
                                break;
                            }
                            String[] identitySigns = respond.substring(1).split("\\s");
                            if(identitySigns.length % 4 != 0){
                                Log.d("MineTrendsActivity", "错误: 收藏动态发布者用户信息获取不全");
                                break;
                            }
                            for(int i = 0;i < identitySigns.length / 4;i++){
                                for(int j = 0;j < collectTrendsList.size();j++){
                                    if(collectTrendsList.get(j).staticId.equals(identitySigns[i *4])){
                                        collectTrendsList.get(j).username = identitySigns[i * 4 +1];
                                        collectTrendsList.get(j).motto = identitySigns[i * 4 + 2];
                                        collectTrendsList.get(j).picture = identitySigns[i * 4 + 3];
                                        break;
                                    }
                                }
                            }
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

            OldTrendsAdapter oldTrendsAdapter = new OldTrendsAdapter(this,
                    R.layout.old_trends_listview_resource,
                    collectTrendsList,
                    4);
            otherTrendsListView.setAdapter(oldTrendsAdapter);
            position = 1;
        }
        else{
            OldTrendsAdapter oldTrendsAdapter = new OldTrendsAdapter(this,
                    R.layout.old_trends_listview_resource,
                    collectTrendsList,
                    4);
            otherTrendsListView.setAdapter(oldTrendsAdapter);
            position = 1;
        }

        addAttentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttentions){
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl
                            + "/identity_attentions_fans");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sStaticId",staticId);
                    request.put("sServeType","3");
                    connection.sendPOST(request);
                    while (connection.getOnWork() != 2){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String respond = connection.getData();
                    if(respond == null){
                        Log.d("OtherTrendsActivity","错误: 删除关注失败");
                        return;
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                Button button = (Button)view;
                                button.setBackgroundColor(Color.rgb(221,221,221));
                                button.setTextColor(Color.WHITE);
                                button.setText("+ 关注");
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
                else{
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl
                            + "/identity_attentions_fans");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sServeType","2");
                    request.put("sStaticId",staticId);
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
                        Log.d("OtherTrendsActivity","错误: 添加关注失败");
                        return;
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                Button button = (Button)view;
                                button.setBackgroundColor(Color.WHITE);
                                button.setText("- 关注");
                                button.setTextColor(Color.rgb(221,221,221));
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
        });

        addFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFriends){
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl
                            + "/identity_friends");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sStaticId",staticId);
                    request.put("sServeType","6");
                    connection.sendPOST(request);
                    String respond = connection.getData();
                    while(connection.getOnWork() != 2){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(respond == null){
                        Log.d("OtherTrendsActivity","错误: 删除好友失败");
                        return;
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                Button button = (Button)view;
                                button.setBackgroundColor(Color.rgb(221,221,221));
                                button.setTextColor(Color.WHITE);
                                button.setText("+ 好友");
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
                else{
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl
                            + "/identity_friends");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sStaticId",staticId);
                    request.put("sServeType","4");
                    connection.sendPOST(request);
                    String respond = connection.getData();
                    while(connection.getOnWork() != 2){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(respond == null){
                        Log.d("OtherTrendsActivity","错误: 添加好友失败");
                        return;
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                Button button = (Button)view;
                                button.setTextColor(Color.rgb(221,221,221));
                                button.setBackgroundColor(Color.WHITE);
                                button.setText("+ 好友");
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
        });
    }

    public void onClickToTrendsButton(View view){
        if(position == 0){
            return;
        }
        OtherMineTrendsAdapter otherMineTrendsAdapter = new OtherMineTrendsAdapter(this,
                R.layout.mine_trends_listview_resource,
                trendsList);
        otherTrendsListView.setAdapter(otherMineTrendsAdapter);
        position = 0;
    }

    public void onClickAttentionsButton(View view){

    }
}