package com.example.testapp3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.zip.Inflater;

import static android.graphics.Color.WHITE;
import static android.graphics.Color.toArgb;

public class MainWindowsActivity extends AppCompatActivity {

    // 元素资源
    private View mainView;
    private ViewPager mainWindow;
    private List<View> viewList = new ArrayList<View>();
    private Button[] buttons = new Button[4];

    // 参数
    private int layoutIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        initView();
        initFriendsView();
    }

    public void initView(){
        // 这里模拟用户已经成功登录并获得动态Id
        DataKeeper.activityId = "000001";

        // 获取用户个人的基础数据
        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
        Map<String, String> request = new HashMap<>();
        request.put("sActivityId", DataKeeper.activityId);
        request.put("sServeType", "0");
        connection.sendPOST(request);
        while (connection.getOnWork() != 2) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        if (respond == null) {
            Log.d("MainWindowsActivity","错误: 基础数据 获取为空");
        }
        else {
            switch (respond.charAt(0)) {
                case '0':
                    String data = respond.substring(1);
                    String[] datas = data.split("\\s");
                    DataKeeper.username = datas[0];
                    DataKeeper.motto = datas[1];
                    DataKeeper.clockIn = datas[2];
                    DataKeeper.headPicture = datas[3];
                    Log.d("MainWindowsActivity",DataKeeper.username + ' '
                            + DataKeeper.motto + ' '
                            + DataKeeper.clockIn + ' '
                            + DataKeeper.headPicture);
                    break;

                case '1':
                    Log.d("MainActivity", "要求重新登录");
                    setContentView(R.layout.answer);
                    TextView textView = findViewById(R.id.answerTextView);
                    textView.setText("要求重新登陆");
                    return;
            }
        }

        // 构建PagerView
        // 获取基础控件
        LayoutInflater inflater = getLayoutInflater();
        mainView = inflater.inflate(R.layout.activity_main_windows,null);
        mainWindow = mainView.findViewById(R.id.main_windows);
        buttons[0] = mainView.findViewById(R.id.main_windows_button01);
        buttons[1] = mainView.findViewById(R.id.main_windows_button02);
        buttons[2] = mainView.findViewById(R.id.main_windows_button03);
        buttons[3] = mainView.findViewById(R.id.main_windows_button04);

        // 构建窗口列表
        viewList.add(inflater.inflate(R.layout.title,null));
        viewList.add(inflater.inflate(R.layout.friends,null));
        viewList.add(inflater.inflate(R.layout.trends,null));
        viewList.add(inflater.inflate(R.layout.mine,null));

        // 构建适配器
        PagerAdapter pagerAdapter = new PagerAdapter() { // 类内适配器
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(viewList.get(position));
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        mainWindow.setAdapter(pagerAdapter);
        mainWindow.setCurrentItem(0); // 设定起始界面
        layoutIndex = 0;
        mainWindow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                buttons[layoutIndex].setBackgroundColor(Color.rgb(221,221,221));
                buttons[layoutIndex].setTextColor(WHITE);
                layoutIndex = position;
                buttons[layoutIndex].setBackgroundColor(WHITE);
                buttons[layoutIndex].setTextColor(Color.rgb(221,221,221));

                switch (position){
                    case 1:
                        ListView listView = viewList.get(1).findViewById(R.id.friendsListView);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setContentView(mainView);
    }
    
    public void initFriendsView(){
        DataKeeper.friendsUserList = new ArrayList<>();
        // 获取历史聊天数据
        String[] friendsUsers;
        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_chat");
            Map<String, String> request = new HashMap<>();
            request.put("sActivityId", DataKeeper.activityId);
            request.put("sServeType", "0");
            connection.sendPOST(request);
            while (connection.getOnWork() != 2) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String respond = connection.getData();
            if (respond == null) {
                Log.d("MainWindowsActivity", "错误: 好友数据获取为空");
                friendsUsers = new String[]{};
            }
            else {
                switch (respond.charAt(0)) {
                    case '0':
                        Log.d("MainActivity", respond);
                        respond = respond.substring(1);
                        friendsUsers = respond.split("\\s");
                        break;

                    case '1':
                        Log.d("MainActivity", "要求重新登录");
                        setContentView(R.layout.answer);
                        TextView textView = findViewById(R.id.answerTextView);
                        textView.setText("要求重新登陆");
                        return;

                    default:
                        friendsUsers = new String[]{};
                }

            }
        }

        FriendsUsersAdapter friendsUsersAdapter = new FriendsUsersAdapter(this,
                R.layout.friends_listview_resource,
                DataKeeper.friendsUserList);

        // 根据更新聊天与历史聊天数据构建聊天列表
        // 获取聊天用户信息
        if (friendsUsers.length != 0) {
            if ((friendsUsers.length % 2) != 0) {
                Log.d("MainWindowsActivity", "错误: 历史聊天数据 历史聊天数据不全");
            }
            else {
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
                Map<String, String> request = new HashMap<String, String>();
                request.put("sActivityId", DataKeeper.activityId);
                request.put("sServeType", "1");
                String staticIdListString = "[" + friendsUsers[1];
                for (int i = 1; i < (friendsUsers.length / 2); i++) {
                    staticIdListString = staticIdListString + "," + friendsUsers[2 * i + 1];
                }
                staticIdListString = staticIdListString + "]";
                request.put("sStaticId", staticIdListString);
                connection.sendPOST(request);
                while (connection.getOnWork() != 2) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String respond = connection.getData();
                if (respond == null) {
                    Log.d("MainWindowsActivity", "错误: 历史聊天数据 身份获取为空");
                } else {
                    switch (respond.charAt(0)) {
                        case '0':
                            Log.d("MainActivity", respond);
                            String[] friendsUserSigns = respond.substring(1).split("\\s");
                            if ((friendsUserSigns.length % 4) != 0) {
                                Log.d("MainWindowsActivity", "错误: 历史聊天数据 身份获取数据不全");
                                break;
                            }
                            int friendsUserNumber = friendsUserSigns.length / 4;
                            Log.d("MainActivity", friendsUserSigns[0]
                                    + ' ' + friendsUserSigns[1]
                                    + ' ' + friendsUserSigns[2]
                                    + ' ' + friendsUserSigns[3]);
                            // 构建聊天数据列表
                            for (int i = 0; i < friendsUserNumber; i++) {
                                DataKeeper.friendsUserList.add(new FriendsUser(friendsUserSigns[4 * i],
                                        friendsUsers[2 * i],
                                        friendsUserSigns[4 * i + 1],
                                        friendsUserSigns[4 * i + 2],
                                        friendsUserSigns[4 * i + 3]));
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

            // 获取 更新聊天信息
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/chat");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
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
            if(respond == null){
                Log.d("MainWindowsActivity", "错误: 更新数据获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        String chatString = respond.substring(1);
                        String[] chatStrings = chatString.split("<spa>");
                        if((chatStrings.length % 2) != 0){
                            Log.d("MainWindowsActivity","错误: 更新数据获取不全");
                            break;
                        }
                        Log.d("MainWindowsActivity",chatString);
                        for (int i = 0;i < (chatStrings.length / 2);i++){
                            for(int j = 0;j < DataKeeper.friendsUserList.size();j++){
                                if(DataKeeper.friendsUserList.get(j).staticId.equals(chatStrings[2 * i])){
                                    if(DataKeeper.friendsUserList.get(j).isDrivingPassive.equals("0")) {
                                        String[] newChatStrings = chatStrings[2 * i + 1].split("<spa2>");
                                        DataKeeper.friendsUserList.get(j).newChatNumber = newChatStrings.length;
                                        DataKeeper.friendsUserList.get(j).newChat = newChatStrings[newChatStrings.length - 1];

                                        friendsUsersAdapter.getItem(j).newChatNumber = newChatStrings.length;
                                        friendsUsersAdapter.getItem(j).newChat = newChatStrings[newChatStrings.length - 1];
                                    }
                                    else{
                                        String[] newChatStrings = chatStrings[2 * i + 1].split("<spa1>");
                                        DataKeeper.friendsUserList.get(j).newChatNumber = newChatStrings.length;
                                        DataKeeper.friendsUserList.get(j).newChat = newChatStrings[newChatStrings.length - 1];

                                        friendsUsersAdapter.getItem(j).newChatNumber = newChatStrings.length;
                                        friendsUsersAdapter.getItem(j).newChat = newChatStrings[newChatStrings.length - 1];
                                    }
                                }
                            }
                        }
                        break;

                    case '1':
                        break;
                }
            }
        }

        // 初始化 friends 界面
        View view = viewList.get(1);
        ListView listView = view.findViewById(R.id.friendsListView);
        listView.setAdapter(friendsUsersAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataKeeper.friendsUserList.get(position).newChatNumber = 0;
                DataKeeper.friendsUserList.get(position).newChat = "";

                FriendsUser friendsUser = (FriendsUser) listView.getAdapter().getItem(position);
                friendsUser.newChatNumber = 0;
                friendsUser.newChat = "";

                view.findViewById(R.id.newChatNumberTextView).setBackgroundColor(WHITE);
                TextView textView = (TextView)view.findViewById(R.id.newChatTextView);
                textView.setText("");

                Intent intent = new Intent(MainWindowsActivity.this,ChatActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        /*
        if(friendsUsersList.size() != 0) {

        }
        else{
            View view = viewList.get(1);
            ListView listView = view.findViewById(R.id.friendsListView);
            TextView textView = new TextView(this);
            textView.setTextSize(64);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setText("聊天记录为空");
            listView.addView(textView);
        }
        */
    }

    public void initTrends(){

    }

    // 设定事件监听者
    public void onClickToTitleButton(View view){
        if (layoutIndex == 0){
            return;
        }
        buttons[layoutIndex].setBackgroundColor(Color.rgb(221,221,221));
        buttons[layoutIndex].setTextColor(WHITE);
        layoutIndex = 0;
        buttons[layoutIndex].setBackgroundColor(WHITE);
        buttons[layoutIndex].setTextColor(Color.rgb(221,221,221));
        mainWindow.setCurrentItem(layoutIndex);
    }

    public void onClickToFriendsButton(View view){
        if(layoutIndex == 1){
            return;
        }
        buttons[layoutIndex].setBackgroundColor(Color.rgb(221,221,221));
        buttons[layoutIndex].setTextColor(WHITE);
        layoutIndex = 1;
        buttons[layoutIndex].setBackgroundColor(WHITE);
        buttons[layoutIndex].setTextColor(Color.rgb(221,221,221));
        mainWindow.setCurrentItem(layoutIndex);
    }

    public void onClickToTrendsButton(View view){
        if (layoutIndex == 2){
            return;
        }
        buttons[layoutIndex].setBackgroundColor(Color.rgb(221,221,221));
        buttons[layoutIndex].setTextColor(WHITE);
        layoutIndex = 2;
        buttons[layoutIndex].setBackgroundColor(WHITE);
        buttons[layoutIndex].setTextColor(Color.rgb(221,221,221));
        mainWindow.setCurrentItem(layoutIndex);
    }

    public void onClickToMineButton(View view){
        if (layoutIndex == 3){
            return;
        }
        buttons[layoutIndex].setBackgroundColor(Color.rgb(221,221,221));
        buttons[layoutIndex].setTextColor(WHITE);
        layoutIndex = 3;
        buttons[layoutIndex].setBackgroundColor(WHITE);
        buttons[layoutIndex].setTextColor(Color.rgb(221,221,221));
        mainWindow.setCurrentItem(layoutIndex);
    }

    public void onClickToClockInButton(View view){
        Intent intent = new Intent(MainWindowsActivity.this,ClockInActivity.class);
        startActivity(intent);
    }
}