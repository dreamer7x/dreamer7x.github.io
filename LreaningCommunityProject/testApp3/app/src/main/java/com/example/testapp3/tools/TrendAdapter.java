package com.example.testapp3.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testapp3.DiscussActivity;
import com.example.testapp3.MoreReplyActivity;
import com.example.testapp3.OtherTrendsActivity;
import com.example.testapp3.R;
import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.Discuss;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.resources.TrendsButton;
import com.example.testapp3.resources.TrendsLinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class TrendAdapter extends ArrayAdapter<Discuss> {

    // 布局资源
    private int trendResourceId;
    private int discussResourceId;
    private int replyResourceId;
    private int buttonResourceId;

    // 数据
    private String trendsId;
    private String staticId;
    private String username;
    private String motto;
    private String headPicturePosition;
    private String title;
    private String text;
    private int praiseNumber;
    private int discussNumber;
    private boolean isPraise;
    private boolean isDiscuss;

    public TrendAdapter(@NonNull Context context, int resource, @NonNull List<Discuss> objects,
                        String trendsId, String staticId,String username, String motto, String headPicturePosition,
                        String title, String text, String picturePosition,
                        boolean isPraise,boolean isDiscuss, int praiseNumber, int discussNumber) {
        super(context, resource, objects);
        this.trendResourceId = R.layout.trend_listview_resource00;
        this.discussResourceId = R.layout.trend_listview_resource01;
        this.replyResourceId = R.layout.trend_listview_resource02;
        this.buttonResourceId = R.layout.trend_listview_resource03;
        this.username = username;
        this.staticId = staticId;
        this.motto = motto;
        this.headPicturePosition = headPicturePosition;
        this.title = title;
        this.text = text;
        this.headPicturePosition = picturePosition;
        this.isPraise = isPraise;
        this.isDiscuss = isDiscuss;
        this.praiseNumber = praiseNumber;
        this.discussNumber = discussNumber;
        this.trendsId = trendsId;
    }

    class ViewHolder{
        public TextView usernameTextView;
        public ImageView headPictureImageView;
        public TextView mottoTextView;
        public TextView discussTextView;
        public TrendsButton praiseButton;
        public TrendsButton replyButton;
        public TrendsLinearLayout replyLinearLayout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(position == 0){
            View view = LayoutInflater.from(getContext()).inflate(trendResourceId,parent,false);
            // 组件
            TextView usernameTextView = view.findViewById(R.id.trendUsernameTextView);
            TextView mottoTextView = view.findViewById(R.id.trendMottoTextView);
            TextView titleTextView = view.findViewById(R.id.trendTitleTextView);
            TextView textTextView = view.findViewById(R.id.trendTextTextView);
            LinearLayout pictureLinearLayout = view.findViewById(R.id.trendPicturePosition);
            Button praiseNumberButton = view.findViewById(R.id.trendPraiseButton);
            Button discussNumberButton = view.findViewById(R.id.trendDiscussButton);
            LinearLayout headLinearLayout = view.findViewById(R.id.trendsLinearLayout);

            usernameTextView.setText(username);
            mottoTextView.setText(motto);
            titleTextView.setText(title);
            textTextView.setText(text);
            // 放置图片 pictureLinearLayout;
            if(isPraise){
                praiseNumberButton.setText("已点赞 " +praiseNumber);
            }
            else {
                praiseNumberButton.setText("点赞 " + praiseNumber);
            }
            if(isDiscuss){
                discussNumberButton.setText("已评论 " + discussNumber);
            }
            else {
                discussNumberButton.setText("评论 " + discussNumber);
            }

            // 绑定监听
            // 发布点赞
            praiseNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isPraise) {
                        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                        Map<String,String> request = new HashMap<>();
                        request.put("sActivityId", DataKeeper.activityId);
                        request.put("sTrendsId", trendsId);
                        request.put("sServeType", "3");
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
                            Log.d("TrendAdapter","错误: 点赞信息发送失败");
                            return;
                        }
                        else {
                            switch (respond.charAt(0)) {
                                case '0':
                                    Button button = (Button) view;
                                    praiseNumber--;
                                    button.setText("点赞 " + praiseNumber);
                                    isPraise = false;
                                    break;

                                case '1':
                                    break;

                                case '3':
                                    button = (Button) view;
                                    button.setText("点赞 " + praiseNumber);
                                    isPraise = false;
                                    break;
                            }
                        }
                    }
                    else{
                        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                        Map<String, String> request = new HashMap<>();
                        request.put("sActivityId", DataKeeper.activityId);
                        request.put("sTrendsId", trendsId);
                        request.put("sServeType", "2");
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
                            Log.d("TrendAdapter", "错误: 点赞信息发送失败");
                            return;
                        } else {
                            switch (respond.charAt(0)) {
                                case '0':
                                    Button button = (Button) view;
                                    praiseNumber++;
                                    button.setText("已点赞 " + praiseNumber);
                                    isPraise = true;
                                    break;

                                case '1':
                                    break;

                                case '3':
                                    button = (Button) view;
                                    button.setText("已点赞 " + praiseNumber);
                                    isPraise = true;
                                    break;
                            }
                        }
                    }
                }
            });

            // 发表评论
            discussNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDiscuss){
                        return;
                    }
                    Intent intent = new Intent(getContext(), DiscussActivity.class);
                    intent.putExtra("trendsId",trendsId);
                    getContext().startActivity(intent);
                }
            });

            // 访问空间
            headLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),OtherTrendsActivity.class);
                    intent.putExtra("from","6");
                    intent.putExtra("staticId",staticId);
                    getContext().startActivity(intent);
                }
            });

            return view;
        }
        else{
            Discuss discuss = getItem(position - 1); // 第一部分为动态内容
            ViewHolder viewHolder;
            View view;

            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(discussResourceId,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.usernameTextView = view.findViewById(R.id.trendListViewResourceUsernameTextView);
                viewHolder.mottoTextView = view.findViewById(R.id.trendListViewResourceMottoTextView);
                viewHolder.headPictureImageView = view.findViewById(R.id.trendListViewResourceHeadPicture);
                viewHolder.discussTextView = view.findViewById(R.id.trendListViewResourceDiscussTextView);
                viewHolder.praiseButton = view.findViewById(R.id.trendPraiseButton);
                viewHolder.replyButton = view.findViewById(R.id.trendListViewResourceReplyButton);
                viewHolder.replyLinearLayout = view.findViewById(R.id.trendListViewResourceLinearLayout);
                view.setTag(viewHolder);
            }
            else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.usernameTextView.setText(discuss.username);
            viewHolder.mottoTextView.setText(discuss.motto);
            // 设定相关头像
            viewHolder.discussTextView.setText(discuss.discuss);
            viewHolder.praiseButton.position = position - 1;
            viewHolder.replyButton.position = position - 1;
            viewHolder.replyLinearLayout.position = position - 1;
            viewHolder.replyLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrendsLinearLayout trendsLinearLayout = (TrendsLinearLayout)view;
                    Discuss discuss = getItem(trendsLinearLayout.position);
                    String discussString = "";
                    discussString = discussString + discuss.username + "<spa>";
                    discussString = discussString + discuss.motto + "<spa>";
                    discussString = discussString + discuss.headPicture + "<spa>";
                    discussString = discussString + discuss.praiseNumber + "<spa>";
                    discussString = discussString + discuss.replyNumber + "<spa>";
                    discussString = discussString + (discuss.isPraise ? "true" : "false") + "<spa>";
                    discussString = discussString + (discuss.isReply ? "true" : "false") + "<spa>";
                    String replyString = "";
                    for(int i = 0;i < discuss.reply.size();i++){
                        replyString = replyString + discuss.reply.get(i).staticId + "<spa>";
                        replyString = replyString + discuss.reply.get(i).replyString + "<spa>";
                        replyString = replyString + discuss.reply.get(i).praiseNumber + "<spa>";
                        replyString = replyString + (discuss.reply.get(i).isPraise ? "true" : "false") + "<spa>";
                    }
                    if(replyString.equals("")){
                        replyString = replyString.substring(0,-5);
                    }
                    Intent intent = new Intent(getContext(), MoreReplyActivity.class);
                    intent.putExtra("replyString",replyString);
                    intent.putExtra("discussString",discussString);
                    getContext().startActivity(intent);
                }
            });
            if(discuss.reply.size() != 0) {
                if(discuss.reply.size() > 3) {
                    // 筛选出获赞量排名前三的子评论
                    int[] max = new int[]{0,0,0};
                    for (int i = 0; i < discuss.reply.size(); i++) {
                        if(discuss.reply.get(max[0]).praiseNumber < discuss.reply.get(i).praiseNumber){
                            max[2] = max[1];
                            max[1] = max[0];
                            max[0] = i;
                        }
                        else{
                            if(discuss.reply.get(max[1]).praiseNumber <= discuss.reply.get(i).praiseNumber){
                                max[2] = max[1];
                                max[1] = i;
                            }
                            else{
                                if(discuss.reply.get(max[2]).praiseNumber < discuss.reply.get(i).praiseNumber){
                                    max[2] = i;
                                }
                            }
                        }
                    }
                    for(int i = 0;i < 3;i++) {
                        View replyView = LayoutInflater.from(getContext()).inflate(replyResourceId, parent, false);
                        TextView username = replyView.findViewById(R.id.trendListViewResourceUsernameTextView);
                        TextView reply = replyView.findViewById(R.id.trendListViewResourceReplyTextView);
                        username.setText(discuss.reply.get(max[i]).username);
                        reply.setText(discuss.reply.get(max[i]).replyString);
                        viewHolder.replyLinearLayout.addView(replyView);
                    }
                    View buttonView = LayoutInflater.from(getContext()).inflate(buttonResourceId,parent,false);
                    viewHolder.replyLinearLayout.addView(buttonView);
                }
                else{
                    for (int i = 0; i < discuss.reply.size(); i++) {
                        View replyView = LayoutInflater.from(getContext()).inflate(replyResourceId, parent, false);
                        TextView username = replyView.findViewById(R.id.trendListViewResourceUsernameTextView);
                        TextView reply = replyView.findViewById(R.id.trendListViewResourceReplyTextView);
                        username.setText(discuss.reply.get(i).username);
                        reply.setText(discuss.reply.get(i).replyString);
                        viewHolder.replyLinearLayout.addView(replyView);
                    }
                }
            }
            viewHolder.praiseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trend");
                    TrendsButton trendsButton = (TrendsButton)view;
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sTrendsId",trendsId);
                    request.put("sPosition",String.valueOf(trendsButton.position));
                    request.put("sServeType","7");
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
                        Log.d("TrendsAdapter","错误: 评论点赞数据发送后恢复为空");
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                discuss.isPraise = true;
                                discuss.praiseNumber = discuss.praiseNumber + 1;
                                trendsButton.setText("已点赞 " + String.valueOf(discuss.praiseNumber));
                                break;

                            case '1':
                                Log.d("MainActivity", "要求重新登录");
                                AppCompatActivity appCompatActivity = (AppCompatActivity)getContext();
                                appCompatActivity.setContentView(R.layout.answer);
                                TextView textView = appCompatActivity.findViewById(R.id.answerTextView);
                                textView.setText("要求重新登陆");
                                return;
                        }
                    }
                }
            });
            return view;
        }
    }
}
