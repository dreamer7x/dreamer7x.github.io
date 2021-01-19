package com.example.testapp3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.AttentionsFansUser;
import com.example.testapp3.resources.Discuss;
import com.example.testapp3.resources.FriendsChatUser;
import com.example.testapp3.resources.FriendsFriendsUser;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.tools.FriendsChatUsersAdapter;
import com.example.testapp3.tools.FriendsFriendsUserAdapter;
import com.example.testapp3.tools.HttpConnection;
import com.example.testapp3.tools.OldTrendsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.WHITE;

// 主页
public class MainWindowsActivity extends AppCompatActivity {

    // 元素资源
    private View mainView;
    private ViewPager mainWindow;
    private List<View> viewList = new ArrayList<View>();
    private Button[] buttons = new Button[4];

    // 参数
    private int layoutIndex;
    private int friendsListViewIndex;
    private static boolean isGetAttentionsFans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        initView();
        initFriendsView();
        initTrendsView();
        initAttentionsFans();
    }

    // 创建滑动界面
    public void initView(){
        // 这里模拟用户已经成功登录并获得动态Id
        isGetAttentionsFans = false;

        // 获取用户个人的基础数据
        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
        Map<String, String> request = new HashMap<>();
        request.put("sActivityId", DataKeeper.activityId);
        request.put("sServeType", "0");
        connection.sendPOST(request);
        while (connection.getOnWork() != 2) {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        if (respond == null) {
            Log.d("MainWindowsActivity","错误: 基础数据 获取为空");
            Log.d("MainActivity", "网络未连接");
            setContentView(R.layout.answer);
            TextView textView = findViewById(R.id.answerTextView);
            textView.setText("网络未连接");
            return;
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
                    DataKeeper.male = datas[4];
                    DataKeeper.birth = datas[5];
                    DataKeeper.staticId = datas[6];
                    Log.d("MainWindowsActivity",DataKeeper.username + ' '
                            + DataKeeper.motto + ' '
                            + DataKeeper.clockIn + ' '
                            + DataKeeper.headPicture + ' '
                            + DataKeeper.male + ' '
                            + DataKeeper.birth);
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
                    case 3:
                        initAttentionsFans();
                        break;
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
        DataKeeper.friendsChatUserList = new ArrayList<>();
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
                Log.d("MainWindowsActivity", "错误: 历史聊天数据获取为空");
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

        FriendsChatUsersAdapter friendsUsersAdapter = new FriendsChatUsersAdapter(this,
                R.layout.friends_listview_resource,
                DataKeeper.friendsChatUserList);

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
                                DataKeeper.friendsChatUserList.add(new FriendsChatUser(friendsUserSigns[4 * i],
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
                        if(chatStrings.length != 1 && (chatStrings.length % 2) != 0){
                            Log.d("MainWindowsActivity","错误: 更新数据获取不全");
                            break;
                        }
                        Log.d("MainWindowsActivity",chatString);
                        for (int i = 0;i < (chatStrings.length / 2);i++){
                            for(int j = 0; j < DataKeeper.friendsChatUserList.size(); j++){
                                if(DataKeeper.friendsChatUserList.get(j).staticId.equals(chatStrings[2 * i])){
                                    if(DataKeeper.friendsChatUserList.get(j).isDrivingPassive.equals("0")) {
                                        String[] newChatStrings = chatStrings[2 * i + 1].split("<spa2>");
                                        DataKeeper.friendsChatUserList.get(j).newChatNumber = newChatStrings.length;
                                        DataKeeper.friendsChatUserList.get(j).newChat = newChatStrings[newChatStrings.length - 1];
                                    }
                                    else{
                                        String[] newChatStrings = chatStrings[2 * i + 1].split("<spa1>");
                                        DataKeeper.friendsChatUserList.get(j).newChatNumber = newChatStrings.length;
                                        DataKeeper.friendsChatUserList.get(j).newChat = newChatStrings[newChatStrings.length - 1];
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

        // 初始化 friends 界面
        View view = viewList.get(1);
        ListView listView = view.findViewById(R.id.friendsListView);
        listView.setAdapter(friendsUsersAdapter);
        friendsListViewIndex = 0;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataKeeper.friendsChatUserList.get(position).newChatNumber = 0;
                DataKeeper.friendsChatUserList.get(position).newChat = "";

                view.findViewById(R.id.newChatNumberTextView).setBackgroundColor(WHITE);
                TextView textView = (TextView)view.findViewById(R.id.newChatTextView);
                textView.setText("");

                Intent intent = new Intent(MainWindowsActivity.this,ChatActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        // 获取好友 静态Id 信息
        DataKeeper.friendsFriendsUserList = new ArrayList<>();
        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_friends");
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
                Log.d("MainWindowsActivity","错误: 好友数据获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        String[] friendsStrings = respond.substring(1).split("<spa>");
                        if(friendsStrings.length != 2){
                            Log.d("MainWindowsActivity","错误: 好友信息获取不全");
                            break;
                        }
                        if(!friendsStrings[0].equals("")){
                            String[] friendsStrings1 = friendsStrings[0].split("\\s");
                            for(int i = 0;i < friendsStrings1.length;i++) {
                                DataKeeper.friendsFriendsUserList.add(new FriendsFriendsUser(friendsStrings1[i],false));
                            }
                        }
                        if(!friendsStrings[1].equals("")){
                            String[] friendsStrings2 = friendsStrings[1].split("\\s");
                            for(int i = 0;i < friendsStrings2.length;i++){
                                DataKeeper.friendsFriendsUserList.add(new FriendsFriendsUser(friendsStrings2[i],true));
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

        // 获取好友自我介绍
        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_friends");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","1");
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
                Log.d("MainWindowsActivity","错误: 好友自我介绍获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() != 1) {
                            String friendsIntroduce = respond.substring(1);
                            String[] friendsIntroduces = friendsIntroduce.split("\\s");
                            if(friendsIntroduces.length % 2 != 0){
                                Log.d("MainWindowsActivity","错误: 好友自我介绍获取不全");
                                break;
                            }
                            for(int i = 0;i < friendsIntroduces.length / 2;i++){
                                for(int j = 0;j < DataKeeper.friendsFriendsUserList.size();j++){
                                    if(DataKeeper.friendsFriendsUserList.get(j).staticId.equals(friendsIntroduces[i * 2])){
                                        DataKeeper.friendsFriendsUserList.get(j).introduce = friendsIntroduces[i * 2 + 1];
                                        break;
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

        // 获取 好友身份信息
        if(DataKeeper.friendsFriendsUserList.size() != 0){
            String requestString = "[" + DataKeeper.friendsFriendsUserList.get(0).staticId;
            for (int i = 1; i < DataKeeper.friendsFriendsUserList.size(); i++) {
                requestString = requestString + "," + DataKeeper.friendsFriendsUserList.get(i).staticId;
            }
            requestString = requestString + "]";
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
            Map<String, String> request = new HashMap<>();
            request.put("sActivityId", DataKeeper.activityId);
            request.put("sServeType", "1");
            request.put("sStaticId", requestString);
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
                Log.d("MainWindowsActivity", "错误: 好友身份数据获取为空");
            } else {
                switch (respond.charAt(0)) {
                    case '0':
                        String friendsString = respond.substring(1);
                        String[] friendsStrings = friendsString.split("\\s");
                        if (friendsStrings.length % 4 != 0) {
                            Log.d("MainWindowsActivity", "错误: 好友身份数据获取不全");
                            return;
                        }
                        for (int i = 0; i < friendsStrings.length / 4; i++) {
                            for(int j = 0;j < DataKeeper.friendsFriendsUserList.size();j++){
                                if(DataKeeper.friendsFriendsUserList.get(j).staticId.equals(friendsStrings[i * 4])){
                                    DataKeeper.friendsFriendsUserList.get(j).username = friendsStrings[i * 4 + 1];
                                    DataKeeper.friendsFriendsUserList.get(j).motto = friendsStrings[i * 4 + 2];
                                    DataKeeper.friendsFriendsUserList.get(j).headPicturePosition = friendsStrings[i * 4 + 3];
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
    }

    // 初始化 动态栏
    public void initTrendsView() {
        DataKeeper.oldTrendsList = new ArrayList<>();
        DataKeeper.collectTrendsList = new ArrayList<>();
        DataKeeper.mineTrendsList = new ArrayList<>();

        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_trends");
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
                Log.d("MainWindowsActivity", "错误: 获取动态历史信息为空");
                return;
            } else {
                switch (respond.charAt(0)) {
                    case '0':
                        if (respond.length() == 1) {
                            break;
                        }
                        String[] trendsIds = respond.substring(1).split("<spa>");
                        Log.d("MainWindowsActivity", trendsIds[0] + " " + trendsIds[1]);
                        String[] mineTrendsIds = trendsIds[0].split("\\s");
                        for (int i = 0; i < mineTrendsIds.length; i++) {
                            DataKeeper.mineTrendsList.add(new Trends(mineTrendsIds[i]));
                        }
                        String[] collectTrendsIds = trendsIds[1].split("\\s");
                        for (int i = 0; i < collectTrendsIds.length; i++) {
                            DataKeeper.collectTrendsList.add(new Trends(collectTrendsIds[i]));
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

        List<String> trendsIdList = new ArrayList<>();
        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_trends");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
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
                Log.d("MainWindowsActivity","错误: 相关历史动态信息获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            Log.d("MainWindowsActivity","警告: 相关历史动态信息获取为空");
                            break;
                        }
                        String[] oldTrendsId = respond.substring(1).split("\\s");
                        for(int i = 0;i < oldTrendsId.length;i++){
                            trendsIdList.add(oldTrendsId[i]);
                            DataKeeper.oldTrendsList.add(new Trends(oldTrendsId[i]));
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
                DataKeeper.oldTrendsList,
                0);

        if (trendsIdList.size() != 0) {
            {
                String trendsId = "";
                for (int i = 0; i < trendsIdList.size(); i++) {
                    trendsId = trendsId + trendsIdList.get(i) + ",";
                }
                trendsId = trendsId.substring(0, trendsId.length() - 1);
                trendsId = "[" + trendsId + "]";
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                Map<String, String> request = new HashMap<>();
                request.put("sActivityId", DataKeeper.activityId);
                request.put("sTrendsId", trendsId);
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
                    Log.d("MainWindowsActivity", "错误: 历史动态具体信息获取为空");
                    return;
                } else {
                    switch (respond.charAt(0)) {
                        case '0':
                            if (respond.length() == 1) {
                                Log.d("MainWindowsActivity", "警告: 未获得历史动态具体信息");
                                break;
                            }
                            String trendsString = respond.substring(1);
                            String[] trendsStrings = trendsString.split("<spa1>");
                            if (trendsStrings.length % 2 != 0) {
                                Log.d("MainWindowsActivity", "错误: 历史动态具体信息获取不全");
                                break;
                            }
                            String testString = "";
                            for (int i = 0; i < trendsStrings.length; i++) {
                                testString = testString + " " + trendsStrings[i];
                            }
                            Log.d("MainWindowsActivity", testString);
                            for (int i = 0; i < trendsStrings.length / 2; i++) {
                                for (int j = 0; j < DataKeeper.oldTrendsList.size(); j++) {
                                    if (DataKeeper.oldTrendsList.get(j).trendsId.equals(trendsStrings[i * 2])) {
                                        String[] trendStrings = trendsStrings[i * 2 + 1].split("<spa>");
                                        DataKeeper.oldTrendsList.get(j).staticId = trendStrings[0];
                                        DataKeeper.oldTrendsList.get(j).title = trendStrings[1];
                                        DataKeeper.oldTrendsList.get(j).text = trendStrings[2];
                                        DataKeeper.oldTrendsList.get(j).picture = trendStrings[3];
                                        DataKeeper.oldTrendsList.get(j).praiseNumber = Integer.parseInt(trendStrings[4]);
                                        DataKeeper.oldTrendsList.get(j).discussNumber = Integer.parseInt(trendStrings[5]);
                                        if (trendStrings[6].equals("true")) {
                                            DataKeeper.oldTrendsList.get(j).isPraise = true;
                                        } else {
                                            DataKeeper.oldTrendsList.get(j).isPraise = false;
                                        }
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

            if(DataKeeper.oldTrendsList.size() != 0){
                String staticId = "";
                for (int i = 0; i < DataKeeper.oldTrendsList.size(); i++) {
                    if (!DataKeeper.oldTrendsList.get(i).staticId.equals("")) {
                        staticId = staticId + DataKeeper.oldTrendsList.get(i).staticId + ",";
                    }
                }
                Log.d("MainWindowsActivity", staticId);
                staticId = "[" + staticId.substring(0, staticId.length() - 1) + "]";
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_sign");
                Map<String, String> request = new HashMap<>();
                request.put("sActivityId", DataKeeper.activityId);
                request.put("sServeType", "1");
                request.put("sStaticId", staticId);
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
                    Log.d("MainWindowsActivity", "错误: 获取动态发布者身份获取为空");
                } else {
                    switch (respond.charAt(0)) {
                        case '0':
                            if (respond.length() == 1) {
                                break;
                            }
                            String[] identitySigns = respond.substring(1).split("\\s");
                            if (identitySigns.length % 4 != 0) {
                                Log.d("MainWindowsActivity", "错误: 动态发布者身份获取不全");
                                break;
                            }
                            for (int i = 0; i < identitySigns.length / 4; i++) {
                                for (int j = 0; j < DataKeeper.oldTrendsList.size(); j++) {
                                    if (DataKeeper.oldTrendsList.get(j).staticId.equals(identitySigns[4 * i])) {
                                        DataKeeper.oldTrendsList.get(j).username = identitySigns[4 * i + 1];
                                        DataKeeper.oldTrendsList.get(j).motto = identitySigns[4 * i + 2];
                                        DataKeeper.oldTrendsList.get(j).headPicturePosition = identitySigns[4 * i + 3];
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
        }

        View view = viewList.get(2);
        ListView listView = view.findViewById(R.id.oldTrendsListView);
        listView.setAdapter(oldTrendsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainWindowsActivity.this, TrendActivity.class);
                intent.putExtra("from",0);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public void initAttentionsFans(){
        if(isGetAttentionsFans){
            return;
        }
        DataKeeper.attentionsUsers = new ArrayList<>();
        DataKeeper.fansUsers = new ArrayList<>();

        List<String> attentionsStaticId = new ArrayList<>();
        List<String> fansStaticId = new ArrayList<>();
        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_attentions_fans");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","0");
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
                Log.d("MainWindowsActivity","错误: 相关关注内容获取为空");
                return;
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            break;
                        }
                        String identityAttentionsFansString = respond.substring(1);
                        String[] identityAttentionsFansStrings = identityAttentionsFansString.split("<spa1>");
                        String[] identityAttentionsFansNumbers = identityAttentionsFansStrings[0].split("\\s");
                        DataKeeper.attentionsNumber = Integer.parseInt(identityAttentionsFansNumbers[0]);
                        DataKeeper.fansNumber = Integer.parseInt(identityAttentionsFansNumbers[1]);
                        if(identityAttentionsFansStrings[1].substring(0,6).equals("<spa2>")){
                            String[] identityFansStaticId = identityAttentionsFansStrings[1].split("<spa2>");
                            String[] identityFansStaticIds = identityFansStaticId[0].split("\\s");
                            for(int i = 0;i < identityFansStaticIds.length;i++){
                                DataKeeper.fansUsers.add(new AttentionsFansUser(identityFansStaticIds[i]));
                            }
                            fansStaticId.addAll(Arrays.asList(identityFansStaticIds));
                            break;
                        }
                        String[] identityAttentionsFansStaticIds = identityAttentionsFansStrings[1].split("<spa2>");
                        if(identityAttentionsFansStaticIds.length == 1){
                            String[] identityAttentionsStaticIds = identityAttentionsFansStaticIds[0].split("\\s");
                            for(int i = 0;i < identityAttentionsFansStaticIds.length;i++){
                                DataKeeper.attentionsUsers.add(new AttentionsFansUser(identityAttentionsStaticIds[i]));
                            }
                            attentionsStaticId.addAll(Arrays.asList(identityAttentionsStaticIds));
                        }
                        else {
                            String[] identityFansStaticIds = identityAttentionsFansStaticIds[0].split("\\s");
                            String[] identityAttentionsStaticIds = identityAttentionsFansStaticIds[1].split("\\s");
                            for(int i = 0;i < identityAttentionsStaticIds.length;i++){
                                DataKeeper.attentionsUsers.add(new AttentionsFansUser(identityAttentionsStaticIds[i]));
                            }
                            attentionsStaticId.addAll(Arrays.asList(identityAttentionsStaticIds));
                            for(int i = 0;i < identityFansStaticIds.length;i++){
                                DataKeeper.fansUsers.add(new AttentionsFansUser(identityFansStaticIds[i]));
                            }
                            fansStaticId.addAll(Arrays.asList(identityFansStaticIds));
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

        if(attentionsStaticId.size() != 0){
            String staticId = "";
            for(int i = 0;i < attentionsStaticId.size();i++){
                staticId = staticId + attentionsStaticId.get(i) + ",";
            }
            staticId = "[" + staticId.substring(0,staticId.length() - 1) + "]";
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl+ "/identity_sign");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","1");
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
                Log.d("MainWindowsActivity","错误: 关注用户身份信息获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            Log.d("MainWindowsActivity","警告: 关注用户身份信息获取为空");
                            break;
                        }
                        String[] identitySigns = respond.substring(1).split("\\s");
                        if(identitySigns.length % 4 != 0){
                            Log.d("MainWindowsActivity","错误: 关注用户身份信息获取不全");
                            break;
                        }
                        for(int i = 0;i < identitySigns.length / 4;i++){
                            for(int j = 0;j < DataKeeper.attentionsUsers.size();j++){
                                if(DataKeeper.attentionsUsers.get(j).staticId.equals(identitySigns[i * 4])){
                                    DataKeeper.attentionsUsers.get(j).username = identitySigns[i * 4 + 1];
                                    DataKeeper.attentionsUsers.get(j).motto = identitySigns[i * 4 + 2];
                                    DataKeeper.attentionsUsers.get(j).headPicturePosition = identitySigns[i * 4 + 3];
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

        if(fansStaticId.size() != 0){
            String staticId = "";
            for(int i = 0;i < fansStaticId.size();i++){
                staticId = staticId + fansStaticId.get(i) + ",";
            }
            staticId = "[" + staticId.substring(0,staticId.length() - 1) + "]";
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl+ "/identity_sign");
            Map<String,String> request = new HashMap<>();
            request.put("sActivityId",DataKeeper.activityId);
            request.put("sServeType","1");
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
                Log.d("MainWindowsActivity","错误: 关注用户身份信息获取为空");
            }
            else{
                switch (respond.charAt(0)){
                    case '0':
                        if(respond.length() == 1){
                            Log.d("MainWindowsActivity","警告: 关注用户身份信息获取为空");
                            break;
                        }
                        String[] identitySigns = respond.substring(1).split("\\s");
                        if(identitySigns.length % 4 != 0){
                            Log.d("MainWindowsActivity","错误: 关注用户身份信息获取不全");
                            break;
                        }
                        for(int i = 0;i < identitySigns.length / 4;i++){
                            for(int j = 0;j < DataKeeper.fansUsers.size();j++){
                                if(DataKeeper.fansUsers.get(j).staticId.equals(identitySigns[i * 4])){
                                    DataKeeper.fansUsers.get(j).username = identitySigns[i * 4 + 1];
                                    DataKeeper.fansUsers.get(j).motto = identitySigns[i * 4 + 2];
                                    DataKeeper.fansUsers.get(j).headPicturePosition = identitySigns[i * 4 + 3];
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

        isGetAttentionsFans = true;
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

    public void onClickToFriendsChatButton(View view){
        if(friendsListViewIndex == 0){
            return;
        }
        if(friendsListViewIndex == 1){
            ListView listView = findViewById(R.id.friendsListView);
            FriendsChatUsersAdapter friendsUsersAdapter = new FriendsChatUsersAdapter(this,
                    R.layout.friends_listview_resource,
                    DataKeeper.friendsChatUserList);
            listView.setAdapter(friendsUsersAdapter);
            friendsListViewIndex = 0;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DataKeeper.friendsChatUserList.get(position).newChatNumber = 0;
                    DataKeeper.friendsChatUserList.get(position).newChat = "";

                    view.findViewById(R.id.newChatNumberTextView).setBackgroundColor(WHITE);
                    TextView textView = (TextView)view.findViewById(R.id.newChatTextView);
                    textView.setText("");

                    Intent intent = new Intent(MainWindowsActivity.this,ChatActivity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            });
            Button toFriendsFriendsButton = findViewById(R.id.toFriendsFriendsButton);
            toFriendsFriendsButton.setBackgroundColor(Color.rgb(221,221,221));
            toFriendsFriendsButton.setTextColor(WHITE);
            Button toFriendsChatButton = findViewById(R.id.toFriendsChatButton);
            toFriendsChatButton.setBackgroundColor(WHITE);
            toFriendsChatButton.setTextColor(Color.rgb(153,153,153));
        }
    }

    public void onClickToFriendsFriendsButton(View view){
        if(friendsListViewIndex == 1){
            return;
        }
        if(friendsListViewIndex == 0){
            ListView listView = findViewById(R.id.friendsListView);
            FriendsFriendsUserAdapter friendsFriendsUserAdapter = new FriendsFriendsUserAdapter(this,
                    R.layout.friends_friends_listview_resource,
                    DataKeeper.friendsFriendsUserList);
            listView.setAdapter(friendsFriendsUserAdapter);
            friendsListViewIndex = 1;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String staticId = DataKeeper.friendsFriendsUserList.get(position).staticId;
                    Intent intent = new Intent(MainWindowsActivity.this,OtherTrendsActivity.class);
                    intent.putExtra("staticId",staticId);
                    intent.putExtra("from",6);
                    startActivity(intent);
                }
            });
            Button toFriendsChatButton = findViewById(R.id.toFriendsChatButton);
            toFriendsChatButton.setBackgroundColor(Color.rgb(221,221,221));
            toFriendsChatButton.setTextColor(WHITE);
            Button toFriendsFriendsButton = findViewById(R.id.toFriendsFriendsButton);
            toFriendsFriendsButton.setBackgroundColor(WHITE);
            toFriendsFriendsButton.setTextColor(Color.rgb(153,153,153));
        }
    }

    public void onClickToMineTrendsLinearLayout(View view){
        Intent intent = new Intent(MainWindowsActivity.this,MineTrendsActivity.class);
        startActivity(intent);
    }

    public void onClickToSendTrendButton(View view){
        Intent intent = new Intent(MainWindowsActivity.this,SendTrendActivity.class);
        startActivity(intent);
    }

    public void onClickToAttentionsLinearLayout(View view){
        Intent intent = new Intent(MainWindowsActivity.this,AttentionsFansActivity.class);
        intent.putExtra("from",0);
        startActivity(intent);
    }

    public void onClickToFansLinearLayout(View view){
        Intent intent = new Intent(MainWindowsActivity.this,AttentionsFansActivity.class);
        intent.putExtra("from",1);
        startActivity(intent);
    }

    public void onClickFindAccompanyButton(View view){

    }

    // 返回意图接收器
    @Override
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data == null){
            return;
        }
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    boolean respond = data.getBooleanExtra("isSendDiscuss",false);
                    int position = data.getIntExtra("position",-1);
                    String discussString = data.getStringExtra("discussString");
                    if(respond && position != -1) {
                        DataKeeper.oldTrendsList.get(position).isDiscuss = true;
                        View view = viewList.get(2);
                        ListView listView = view.findViewById(R.id.oldTrendsListView);
                        OldTrendsAdapter oldTrendsAdapter =  (OldTrendsAdapter) listView.getAdapter();
                        oldTrendsAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }
}