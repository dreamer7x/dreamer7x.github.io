package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.tools.HttpConnection;
import com.example.testapp3.tools.OldTrendsAdapter;
import com.example.testapp3.tools.OtherMineTrendsAdapter;

import java.util.HashMap;
import java.util.Map;

public class MineTrendsActivity extends AppCompatActivity {

    private LinearLayout mineTrendsBackgroundLinearLayout;
    private ImageView mineTrendsHeadPictureImageView;
    private TextView mineTrendsUsernameTextView;
    private TextView mineTrendsMottoTextView;
    private TextView mineTrendsAttentionsNumberTextView;
    private TextView mineTrendsFansNumberTextView;
    private ListView mineTrendsListView;

    private static boolean isGetMineTrends;
    private static boolean isGetCollectTrends;
    private static boolean isGetTeamTrends;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_trends);
        initView();
    }

    public void initView(){
        mineTrendsBackgroundLinearLayout = findViewById(R.id.mineTrendsBackgroundLinearLayout);
        mineTrendsHeadPictureImageView = findViewById(R.id.mineTrendsHeadPictureImageView);
        mineTrendsUsernameTextView = findViewById(R.id.mineTrendsUsernameTextView);
        mineTrendsMottoTextView = findViewById(R.id.mineTrendsMottoTextView);
        mineTrendsAttentionsNumberTextView = findViewById(R.id.mineTrendsAttentionsNumberTextView);
        mineTrendsFansNumberTextView = findViewById(R.id.mineTrendsFansNumberTextView);
        mineTrendsListView = findViewById(R.id.mineTrendsListView);

        // mineTrendsBackgroundLinearLayout
        // mineTrendsHeadPictureImageView
        mineTrendsUsernameTextView.setText(DataKeeper.username);
        mineTrendsMottoTextView.setText(DataKeeper.motto);
        mineTrendsAttentionsNumberTextView.setText(String.valueOf(DataKeeper.attentionsNumber));
        mineTrendsFansNumberTextView.setText(String.valueOf(DataKeeper.fansNumber));

        mineTrendsListView = findViewById(R.id.mineTrendsListView);

        if(isGetMineTrends == false) {
            if (DataKeeper.mineTrendsList.size() != 0) {
                String trendsId = "";
                for (int i = 0; i < DataKeeper.mineTrendsList.size(); i++) {
                    trendsId = trendsId + DataKeeper.mineTrendsList.get(i).trendsId + ",";
                }
                trendsId = "[" + trendsId.substring(0, trendsId.length() - 1) + "]";
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                Map<String, String> request = new HashMap<>();
                request.put("sActivityId", DataKeeper.activityId);
                request.put("sServeType", "0");
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
                    Log.d("MineTrendsActivity", "错误: 个人动态信息获取为空");
                } else {
                    switch (respond.charAt(0)) {
                        case '0':
                            if (respond.length() == 1) {
                                Log.d("MineTrendsActivity", "警告: 个人动态信息获取为空");
                            }
                            String[] trendsString = respond.substring(1).split("<spa1>");
                            if (trendsString.length % 2 != 0) {
                                Log.d("MineTrendsActivity", "错误: 个人动态信息获取不全");
                                break;
                            }
                            for (int i = 0; i < trendsString.length / 2; i++) {
                                for (int j = 0; j < DataKeeper.mineTrendsList.size(); j++) {
                                    if (DataKeeper.mineTrendsList.get(j).trendsId.equals(trendsString[i * 2])) {
                                        String[] trendsStrings = trendsString[i * 2 + 1].split("<spa>");
                                        DataKeeper.mineTrendsList.get(j).staticId = trendsStrings[0];
                                        DataKeeper.mineTrendsList.get(j).title = trendsStrings[1];
                                        DataKeeper.mineTrendsList.get(j).text = trendsStrings[2];
                                        DataKeeper.mineTrendsList.get(j).picture = trendsStrings[3];
                                        DataKeeper.mineTrendsList.get(j).praiseNumber = Integer.parseInt(trendsStrings[4]);
                                        DataKeeper.mineTrendsList.get(j).discussNumber = Integer.parseInt(trendsStrings[5]);
                                        if (trendsStrings[6].equals("true")) {
                                            DataKeeper.mineTrendsList.get(j).isPraise = true;
                                        } else {
                                            DataKeeper.mineTrendsList.get(j).isPraise = false;
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
            isGetMineTrends = true;
        }

        OtherMineTrendsAdapter otherMineTrendsAdapter = new OtherMineTrendsAdapter(this,
                R.layout.mine_trends_listview_resource,
                DataKeeper.mineTrendsList);
        mineTrendsListView.setAdapter(otherMineTrendsAdapter);
        position = 0;
        mineTrendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineTrendsActivity.this,OtherTrendsActivity.class);
                intent.putExtra("trendsId",DataKeeper.mineTrendsList.get(position).trendsId);
                intent.putExtra("from",1);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    // 监听函数
    public void onClickToTrendsButton(View view){
        if(position == 0){
            return;
        }
        OtherMineTrendsAdapter otherMineTrendsAdapter = new OtherMineTrendsAdapter(this,
                R.layout.mine_trends_listview_resource,
                DataKeeper.mineTrendsList);
        mineTrendsListView.setAdapter(otherMineTrendsAdapter);
        position = 0;
        mineTrendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineTrendsActivity.this, TrendActivity.class);
                intent.putExtra("trendsId",DataKeeper.mineTrendsList.get(position).trendsId);
                intent.putExtra("from",1);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    public void onClickToTeamButton(View view){
        if(position == 1){
            return;
        }
    }

    public void onClickToCollectButton(View view){
        if(position == 2){
            return;
        }
        if(isGetCollectTrends == false){
            {
                String trendsId = "";
                for (int i = 0; i < DataKeeper.collectTrendsList.size(); i++) {
                    trendsId = trendsId + DataKeeper.collectTrendsList.get(i).trendsId + ",";
                }
                if (trendsId.equals("")) {
                    return;
                }
                trendsId = "[" + trendsId.substring(0, trendsId.length() - 1) + "]";
                HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                Map<String, String> request = new HashMap<>();
                request.put("sActivityId", DataKeeper.activityId);
                request.put("sServeType", "0");
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
                    Log.d("MineTrendsActivity", "错误: 收藏动态信息获取为空");
                } else {
                    switch (respond.charAt(0)) {
                        case '0':
                            if (respond.length() == 1) {
                                Log.d("MineTrendsActivity", "警告: 收藏动态信息获取为空");
                            }
                            String[] trendsString = respond.substring(1).split("<spa1>");
                            if (trendsString.length % 2 != 0) {
                                Log.d("MineTrendsActivity", "错误: 收藏动态信息获取不全");
                            }
                            for (int i = 0; i < trendsString.length / 2; i++) {
                                for (int j = 0; j < DataKeeper.collectTrendsList.size(); j++) {
                                    if (DataKeeper.collectTrendsList.get(j).trendsId.equals(trendsString[i * 2])) {
                                        String[] trendsStrings = trendsString[i * 2 + 1].split("<spa>");
                                        DataKeeper.collectTrendsList.get(j).staticId = trendsStrings[0];
                                        DataKeeper.collectTrendsList.get(j).title = trendsStrings[1];
                                        DataKeeper.collectTrendsList.get(j).text = trendsStrings[2];
                                        DataKeeper.collectTrendsList.get(j).picture = trendsStrings[3];
                                        DataKeeper.collectTrendsList.get(j).praiseNumber = Integer.parseInt(trendsStrings[4]);
                                        DataKeeper.collectTrendsList.get(j).discussNumber = Integer.parseInt(trendsStrings[5]);
                                        if (trendsStrings[6].equals("true")) {
                                            DataKeeper.collectTrendsList.get(j).isPraise = true;
                                        } else {
                                            DataKeeper.collectTrendsList.get(j).isPraise = false;
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

            if(DataKeeper.collectTrendsList.size() != 0){
                String staticId = "";
                for(int i = 0;i < DataKeeper.collectTrendsList.size();i++){
                    staticId = staticId + DataKeeper.collectTrendsList.get(i).staticId + ",";
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
                if(respond == null){
                    Log.d("MineTrendsActivity", "错误: 收藏动态发布者用户信息获取为空");
                    return;
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
                                for(int j = 0;j < DataKeeper.collectTrendsList.size();j++){
                                    if(DataKeeper.collectTrendsList.get(j).staticId.equals(identitySigns[i *4])){
                                        DataKeeper.collectTrendsList.get(j).username = identitySigns[i * 4 +1];
                                        DataKeeper.collectTrendsList.get(j).motto = identitySigns[i * 4 + 2];
                                        DataKeeper.collectTrendsList.get(j).picture = identitySigns[i * 4 + 3];
                                        break;
                                    }
                                }
                            }
                            isGetCollectTrends = true;
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

        OldTrendsAdapter oldTrendsAdapter = new OldTrendsAdapter(this,
                R.layout.old_trends_listview_resource,
                DataKeeper.collectTrendsList,
                2);
        mineTrendsListView.setAdapter(oldTrendsAdapter);
        position = 1;
        mineTrendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineTrendsActivity.this, TrendActivity.class);
                intent.putExtra("trendsId",DataKeeper.collectTrendsList.get(position).trendsId);
                intent.putExtra("from",2); // 来自于收藏动态
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    public void onClickFinishButton(View view){
        finish();
    }

    public void onClickToSetButton(View view){
        Intent intent = new Intent(MineTrendsActivity.this,SetActivity.class);
        startActivity(intent);
    }

    public void onClickToAttentionsLinearLayout(View view){
        Intent intent = new Intent(MineTrendsActivity.this,AttentionsFansActivity.class);
        intent.putExtra("from",0);
        startActivity(intent);
        return;
    }

    public void onClickToFansLinearLayout(View view){
        Intent intent = new Intent(MineTrendsActivity.this,AttentionsFansActivity.class);
        intent.putExtra("from",1);
        startActivity(intent);
        return;
    }

}