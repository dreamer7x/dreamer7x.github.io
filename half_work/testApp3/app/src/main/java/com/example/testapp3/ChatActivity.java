package com.example.testapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.FriendsChatUser;
import com.example.testapp3.tools.HttpConnection;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FriendsChatUser friendsChatUser;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private EditText editText;

    // 工具
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0: // 循环线程 获取新信息
                    Log.d("ChatActivity","尝试 更新聊天数据");
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/chat");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId", DataKeeper.activityId);
                    request.put("sServeType","3");
                    request.put("sDrivingPassive", friendsChatUser.isDrivingPassive);
                    request.put("sStaticId", friendsChatUser.staticId);
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
                    if(respond == null){
                        Log.d("ChatActivityId","错误: 尝试更新数据失败 更新数据获取为空");
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                if(respond.length() == 1){
                                    break;
                                }
                                if(friendsChatUser.isDrivingPassive.equals("0")) {
                                    String[] chatStrings = respond.substring(1).split("<spa2>");
                                    for(int i = 0;i < chatStrings.length;i++){
                                        LayoutInflater inflater = getLayoutInflater();
                                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                        userNameTextView.setText(friendsChatUser.username);
                                        contentTextView.setText(chatStrings[i]);
                                        linearLayout.addView(view);
                                    }
                                }
                                else{
                                    String[] chatStrings = respond.substring(1).split("<spa1>");
                                    for(int i = 0;i < chatStrings.length;i++){
                                        LayoutInflater inflater = getLayoutInflater();
                                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                        userNameTextView.setText(friendsChatUser.username);
                                        contentTextView.setText(chatStrings[i]);
                                        linearLayout.addView(view);
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
                    handler.sendEmptyMessageDelayed(0, 500);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    public void initView(){
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",-1);
        if(position == -1){
            Log.d("ChatActivity","错误: 未获得有效聊天信息索引");
            finish();
        }

        scrollView = findViewById(R.id.chatScrollView);
        linearLayout = findViewById(R.id.chatLinearLayout);
        editText = findViewById(R.id.chatSendChatEditText);
        friendsChatUser = DataKeeper.friendsChatUserList.get(position);

        LayoutInflater inflater = getLayoutInflater();

        if(!friendsChatUser.oldChat.equals("")){
            if(friendsChatUser.isDrivingPassive.equals("0")) {
                String[] oldToChats = friendsChatUser.oldChat.split("<spa2>");
                String testAnswer = "";
                for(int i = 0;i < oldToChats.length;i++) {
                    testAnswer = testAnswer + oldToChats[i] + " ";
                }
                Log.d("ChatActivity", testAnswer);
                for(int i = 0;i < oldToChats.length - 1;i++){
                    String[] chats = oldToChats[i].split("<spa1>");
                    if(chats.length == 1){
                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(friendsChatUser.username);
                        contentTextView.setText(chats[0]);
                        linearLayout.addView(view);
                    }
                    else{
                        for(int j = 0;j < chats.length - 1;j++){
                            View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                            TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                            TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                            // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                            userNameTextView.setText(DataKeeper.username);
                            contentTextView.setText(chats[j]);
                            linearLayout.addView(view);
                        }
                        if(chats[chats.length - 1].equals("")) {
                            View view = inflater.inflate(R.layout.chat_left_resource, null, false);
                            TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                            TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                            // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                            userNameTextView.setText(friendsChatUser.username);
                            contentTextView.setText(chats[chats.length - 1]);
                            linearLayout.addView(view);
                        }
                    }
                }
                if(oldToChats[oldToChats.length - 1].endsWith("<spa1>")){
                    String[] chats = oldToChats[oldToChats.length - 1].split("<spa1>");
                    for(int j = 0;j < chats.length;j++){
                        View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(DataKeeper.username);
                        contentTextView.setText(chats[j]);
                        linearLayout.addView(view);
                    }
                }
                else{
                    String[] chats = oldToChats[oldToChats.length - 1].split("<spa1>");
                    for(int j = 0;j < chats.length - 1;j++){
                        View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(DataKeeper.username);
                        contentTextView.setText(chats[j]);
                        linearLayout.addView(view);
                    }
                    View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                    userNameTextView.setText(friendsChatUser.username);
                    contentTextView.setText(chats[chats.length - 1]);
                    linearLayout.addView(view);
                }
            }
            else{
                String[] oldToChats = friendsChatUser.oldChat.split("<spa1>");
                String testAnswer = "";
                for(int i = 0;i < oldToChats.length;i++) {
                    testAnswer = testAnswer + oldToChats[i] + " ";
                }
                Log.d("ChatActivity", testAnswer);
                for(int i = 0;i < oldToChats.length - 1;i++){
                    String[] chats = oldToChats[i].split("<spa1>");
                    if(chats.length == 1){
                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(friendsChatUser.username);
                        contentTextView.setText(chats[0]);
                        linearLayout.addView(view);
                    }
                    else{
                        for(int j = 0;j < chats.length - 1;j++){
                            View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                            TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                            TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                            // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                            userNameTextView.setText(DataKeeper.username);
                            contentTextView.setText(chats[j]);
                            linearLayout.addView(view);
                        }
                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(friendsChatUser.username);
                        contentTextView.setText(chats[0]);
                        linearLayout.addView(view);
                    }
                }
                if(oldToChats[oldToChats.length - 1].length() > 5 && oldToChats[oldToChats.length - 1].endsWith("<spa2>")){
                    String[] chats = oldToChats[oldToChats.length - 1].split("<spa2>");
                    for(int j = 0;j < chats.length - 1;j++){
                        View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(DataKeeper.username);
                        contentTextView.setText(chats[j]);
                        linearLayout.addView(view);
                    }
                    View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                    userNameTextView.setText(DataKeeper.username);
                    contentTextView.setText(chats[chats.length - 1]);
                    linearLayout.addView(view);
                }
                else{
                    String[] chats = oldToChats[oldToChats.length - 1].split("<spa2>");
                    for(int j = 0;j < chats.length - 1;j++){
                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(DataKeeper.username);
                        contentTextView.setText(chats[j]);
                        linearLayout.addView(view);
                    }
                    View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                    userNameTextView.setText(friendsChatUser.username);
                    contentTextView.setText(chats[chats.length - 1]);
                    linearLayout.addView(view);
                }
            }
            handler.sendEmptyMessageDelayed(0,500);
        }
        else{
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/chat");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","1");
            request.put("sDrivingPassive", friendsChatUser.isDrivingPassive);
            request.put("sStaticId", friendsChatUser.staticId);
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
                Log.d("ChatActivity","错误: 聊天数据获取为空");
                finish();
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("ChatActivity","尝试启动更新线程");
                                    handler.sendEmptyMessageDelayed(0, 200);
                                }
                            }).start();
                            break;
                        }
                        friendsChatUser.oldChat = respond.substring(1);
                        if(friendsChatUser.isDrivingPassive.equals("0")) {
                            String[] oldToChats = friendsChatUser.oldChat.split("<spa2>");
                            String testAnswer = "";
                            for(int i = 0;i < oldToChats.length;i++) {
                                testAnswer = testAnswer + oldToChats[i] + " ";
                            }
                            Log.d("ChatActivity", testAnswer);
                            for(int i = 0;i < oldToChats.length - 1;i++){
                                String[] chats = oldToChats[i].split("<spa1>");
                                for(int j = 0;j < chats.length - 1;j++){
                                        View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                        userNameTextView.setText(DataKeeper.username);
                                        contentTextView.setText(chats[j]);
                                        linearLayout.addView(view);
                                }
                                View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                userNameTextView.setText(friendsChatUser.username);
                                contentTextView.setText(chats[chats.length - 1]);
                                linearLayout.addView(view);
                            }
                            if(oldToChats[oldToChats.length - 1].length() > 5 && oldToChats[oldToChats.length - 1].endsWith("<spa1>")){
                                String[] chats = oldToChats[oldToChats.length - 1].split("<spa1>");
                                for(int j = 0;j < chats.length;j++){
                                    View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                    userNameTextView.setText(DataKeeper.username);
                                    contentTextView.setText(chats[j]);
                                    linearLayout.addView(view);
                                }
                            }
                            else{
                                String[] chats = oldToChats[oldToChats.length - 1].split("<spa1>");
                                for(int j = 0;j < chats.length - 1;j++){
                                    View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                    userNameTextView.setText(DataKeeper.username);
                                    contentTextView.setText(chats[j]);
                                    linearLayout.addView(view);
                                }
                                View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                userNameTextView.setText(friendsChatUser.username);
                                contentTextView.setText(chats[chats.length - 1]);
                                linearLayout.addView(view);
                            }
                        }
                        else{
                            String[] oldToChats = friendsChatUser.oldChat.split("<spa1>");
                            String testAnswer = "";
                            for(int i = 0;i < oldToChats.length;i++) {
                                testAnswer = testAnswer + oldToChats[i] + " ";
                            }
                            Log.d("ChatActivity", testAnswer);
                            for(int i = 0;i < oldToChats.length - 1;i++){
                                String[] chats = oldToChats[i].split("<spa2>");
                                for(int j = 0;j < chats.length - 1;j++){
                                    View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                    userNameTextView.setText(DataKeeper.username);
                                    contentTextView.setText(chats[j]);
                                    linearLayout.addView(view);
                                }
                                View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                userNameTextView.setText(friendsChatUser.username);
                                contentTextView.setText(chats[0]);
                                linearLayout.addView(view);
                            }
                            if(oldToChats[oldToChats.length - 1].length() > 5 && oldToChats[oldToChats.length - 1].endsWith("<spa2>")){
                                String[] chats = oldToChats[oldToChats.length - 1].split("<spa2>");
                                for(int j = 0;j < chats.length - 1;j++){
                                    View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                    userNameTextView.setText(DataKeeper.username);
                                    contentTextView.setText(chats[j]);
                                    linearLayout.addView(view);
                                }
                                View view = inflater.inflate(R.layout.chat_right_resource,null,false);
                                TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                userNameTextView.setText(DataKeeper.username);
                                contentTextView.setText(chats[chats.length - 1]);
                                linearLayout.addView(view);
                            }
                            else{
                                String[] chats = oldToChats[oldToChats.length - 1].split("<spa2>");
                                for(int j = 0;j < chats.length - 1;j++){
                                    View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                    TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                    TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                    // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                    userNameTextView.setText(DataKeeper.username);
                                    contentTextView.setText(chats[j]);
                                    linearLayout.addView(view);
                                }
                                View view = inflater.inflate(R.layout.chat_left_resource,null,false);
                                TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                                TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                                // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                                userNameTextView.setText(friendsChatUser.username);
                                contentTextView.setText(chats[chats.length - 1]);
                                linearLayout.addView(view);
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
        handler.sendEmptyMessageDelayed(0,500);
        scrollView.scrollTo(0,0);
    }

    // 监听函数
    public void onClickChatSendChatEditText(View editTextView){
        String sendChat = String.valueOf(editText.getText());
        if(sendChat == null){
            return;
        }
        else{
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/chat");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId", DataKeeper.activityId);
            request.put("sServeType","2");
            request.put("sDrivingPassive", friendsChatUser.isDrivingPassive);
            request.put("sStaticId", friendsChatUser.staticId);
            request.put("sSendChat",sendChat);
            connection.sendPOST(request);
            while (connection.getOnWork() != 2){
                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String respond = connection.getData();
            if(respond == null){
                Log.d("ChatActivity","错误: 数据发送接受为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view = layoutInflater.inflate(R.layout.chat_right_resource,null,false);
                        TextView userNameTextView = view.findViewById(R.id.chatUserNameTextView);
                        TextView contentTextView = view.findViewById(R.id.chatContentTextView);
                        // ImageView headPictureImageView = view.findViewById(R.id.chatHeadPictureImageView);
                        userNameTextView.setText(DataKeeper.username);
                        contentTextView.setText(sendChat);
                        linearLayout.addView(view);
                        editText.setText("");
                        if(friendsChatUser.isDrivingPassive.equals("0")) {
                            friendsChatUser.oldChat = friendsChatUser.oldChat + "<spa1>";
                        }
                        else{
                            friendsChatUser.oldChat = friendsChatUser.oldChat + "<spa2>";
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
    }

    public void onClickChatFinishButton(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}