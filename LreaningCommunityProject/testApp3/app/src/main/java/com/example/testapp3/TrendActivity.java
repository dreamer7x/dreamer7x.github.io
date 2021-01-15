package com.example.testapp3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.Discuss;
import com.example.testapp3.resources.Reply;
import com.example.testapp3.resources.TrendsButton;
import com.example.testapp3.tools.HttpConnection;
import com.example.testapp3.tools.TrendAdapter;

import java.security.interfaces.DSAKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendActivity extends AppCompatActivity {

    // 数据
    private String trendsId;
    private String staticId;
    private String username;
    private String motto;
    private String headPicturePosition;
    private String text;
    private String title;
    private boolean isPraise;
    private boolean isDiscuss;
    private int praiseNumber;
    private int discussNumber;
    private String picturePosition;
    private List<Discuss> discussList;

    // 参数
    private int from;
    private int position;

    // 组件
    private ListView trendListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
        initView();
    }

    public void initView() {
        Intent intent = getIntent();
        from = intent.getIntExtra("from", -1);
        discussList = new ArrayList<>();
        trendListView = findViewById(R.id.trendListView);

        /**
         * -1 : 其它动态访问
         * 0 : 历史动态访问
         * 1 : 个人动态访问
         * 2 : 历史动态访问
         */
        switch (from) {
            case -1:
                trendsId = intent.getStringExtra("trendsId");
                if (trendsId == null) {
                    Log.d("TrendsActivity", "错误: 未提供完整信息");
                    finish();
                }
                username = intent.getStringExtra("username");
                motto = intent.getStringExtra("motto");
                title = intent.getStringExtra("title");
                text = intent.getStringExtra("text");
                praiseNumber = intent.getIntExtra("praiseNumber",-1);
                discussNumber = intent.getIntExtra("discussNumber",-1);
                picturePosition = intent.getStringExtra("picture");
                isPraise = intent.getBooleanExtra("isPraise",false);
                isDiscuss = intent.getBooleanExtra("isDiscuss",false);
                staticId = intent.getStringExtra("staticId");
                break;

            case 0:
                position = intent.getIntExtra("position", -1);
                if (position == -1) {
                    Log.d("TrendsActivity", "错误: 未提供完整信息");
                    finish();
                }
                trendsId = DataKeeper.oldTrendsList.get(position).trendsId;
                username = DataKeeper.oldTrendsList.get(position).username;
                motto = DataKeeper.oldTrendsList.get(position).motto;
                title = DataKeeper.oldTrendsList.get(position).title;
                text = DataKeeper.oldTrendsList.get(position).text;
                headPicturePosition = DataKeeper.oldTrendsList.get(position).headPicturePosition;
                praiseNumber = DataKeeper.oldTrendsList.get(position).praiseNumber;
                discussNumber = DataKeeper.oldTrendsList.get(position).discussNumber;
                isPraise = DataKeeper.oldTrendsList.get(position).isPraise;
                isDiscuss = DataKeeper.oldTrendsList.get(position).isDiscuss;
                staticId = DataKeeper.oldTrendsList.get(position).staticId;
                break;

            case 1:
                position = intent.getIntExtra("position", -1);
                if (position == -1) {
                    Log.d("TrendsActivity", "错误: 未提供完整信息");
                    finish();
                }
                trendsId = DataKeeper.mineTrendsList.get(position).trendsId;
                username = DataKeeper.mineTrendsList.get(position).username;
                motto = DataKeeper.mineTrendsList.get(position).motto;
                title = DataKeeper.mineTrendsList.get(position).title;
                text = DataKeeper.mineTrendsList.get(position).text;
                headPicturePosition = DataKeeper.mineTrendsList.get(position).headPicturePosition;
                praiseNumber = DataKeeper.mineTrendsList.get(position).praiseNumber;
                discussNumber = DataKeeper.mineTrendsList.get(position).discussNumber;
                isPraise = DataKeeper.mineTrendsList.get(position).isPraise;
                isDiscuss = DataKeeper.mineTrendsList.get(position).isDiscuss;
                staticId = DataKeeper.mineTrendsList.get(position).staticId;
                break;

            case 2:
                position = intent.getIntExtra("position", -1);
                if (position == -1) {
                    Log.d("TrendsActivity", "错误: 未提供完整信息");
                    finish();
                }
                trendsId = DataKeeper.collectTrendsList.get(position).trendsId;
                username = DataKeeper.collectTrendsList.get(position).username;
                motto = DataKeeper.collectTrendsList.get(position).motto;
                title = DataKeeper.collectTrendsList.get(position).title;
                text = DataKeeper.collectTrendsList.get(position).text;
                headPicturePosition = DataKeeper.collectTrendsList.get(position).headPicturePosition;
                praiseNumber = DataKeeper.collectTrendsList.get(position).praiseNumber;
                discussNumber = DataKeeper.collectTrendsList.get(position).discussNumber;
                isPraise = DataKeeper.collectTrendsList.get(position).isPraise;
                isDiscuss = DataKeeper.collectTrendsList.get(position).isDiscuss;
                staticId = DataKeeper.collectTrendsList.get(position).staticId;
                break;

            default:
                Log.d("TrendsActivity","错误: 提供参数选项不存在");
                finish();
        }

        {
            HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
            Map<String, String> request = new HashMap<>();
            request.put("sActivityId", DataKeeper.activityId);
            request.put("sServeType", "1");
            request.put("sTrendsId", trendsId);
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
                Log.d("TrendsActivity", "错误: 动态评论内容获取为空");
            } else {
                switch (respond.charAt(0)) {
                    case '0':
                        String discussString = respond.substring(1);
                        String[] discussStrings = discussString.split("<spa1>");
                        if (discussStrings.length % 4 != 0) {
                            Log.d("TrendsActivity", "错误: 动态评论内容获取不全");
                            break;
                        }
                        for (int i = 0; i < discussStrings.length / 4; i++) {
                            List<Reply> replyList = new ArrayList<>();
                            String[] numberStrings1 = discussStrings[i * 3 + 2].split("<spa>");
                            String[] numberStrings2 = new String[]{"0","0"};
                            boolean isPraise = false;
                            boolean isReply = false;
                            if(numberStrings1.length % 4 == 0) {
                                numberStrings2[0] = numberStrings1[0];
                                numberStrings2[1] = numberStrings1[1];
                                isPraise = numberStrings1[2].equals("true");
                                isReply = numberStrings1[3].equals("true");
                            }
                            if(discussStrings[3 * i + 3].equals("null")){
                                // 提供一定的容错机制 回复获取错误则不显示回复
                                discussList.add(new Discuss(discussStrings[i * 3], discussStrings[i * 3 + 1],
                                        Integer.parseInt(numberStrings2[0]), Integer.parseInt(numberStrings2[1]),
                                        isPraise, isReply));
                                continue;
                            }
                            String[] replyStrings = discussStrings[3 * i + 3].split("<spa>");
                            if (replyStrings.length % 4 != 0) {
                                Log.d("TrendsActivity", "错误: 动态回复内容获取不全");
                                // 提供一定的容错机制 回复获取错误则不显示回复
                                discussList.add(new Discuss(discussStrings[i * 3], discussStrings[i * 3 + 1],
                                        Integer.parseInt(numberStrings2[0]), Integer.parseInt(numberStrings2[1]),
                                        isPraise,isReply));
                                continue;
                            }
                            for (int j = 0; j < replyStrings.length / 4; j++) {
                                replyList.add(new Reply(replyStrings[j * 2],
                                        replyStrings[j * 2 + 1],
                                        Integer.parseInt(replyStrings[i * 2 + 2]),
                                        replyStrings[i * 2 + 3].equals("true")));
                            }
                            discussList.add(new Discuss(discussStrings[i * 3], discussStrings[i * 3 + 1],
                                    Integer.parseInt(numberStrings2[0]), Integer.parseInt(numberStrings2[1]),
                                    isPraise,isReply,
                                    replyList));
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

        TrendAdapter trendAdapter = new TrendAdapter(
                this,R.layout.trend_listview_resource00,discussList,
                trendsId,staticId,username, motto, headPicturePosition,
                title, text, picturePosition,
                isPraise,isDiscuss,praiseNumber, discussNumber);
        trendListView.setAdapter(trendAdapter);
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
                    String discussString = data.getStringExtra("discussString");
                    if(respond) {
                        isDiscuss = true;
                        discussNumber++;
                        View view = trendListView.getChildAt(0);
                        Button button = view.findViewById(R.id.trendDiscussButton);
                        button.setText("已评论 " + discussNumber);
                        if(discussString != null){
                            discussList.add(0,
                                    new Discuss(DataKeeper.username,discussString,
                                            0,0,false,false));
                        }
                    }
                }
                break;
        }
    }
}