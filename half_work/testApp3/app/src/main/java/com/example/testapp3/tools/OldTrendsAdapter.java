package com.example.testapp3.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp3.DiscussActivity;
import com.example.testapp3.OtherTrendsActivity;
import com.example.testapp3.R;
import com.example.testapp3.TrendActivity;
import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.resources.TrendsButton;
import com.example.testapp3.resources.TrendsLinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OldTrendsAdapter extends ArrayAdapter<Trends> {

    private int layoutResourceId;
    private int from;

    public OldTrendsAdapter(@NonNull Context context, int layoutResourceId, @NonNull List<Trends> objects,int from) {
        super(context, layoutResourceId, objects);
        this.layoutResourceId = layoutResourceId;
        this.from = from;
    }

    static class oldTrendsViewHolder{
        ImageView headPictureImageView;
        TextView usernameTextView;
        TextView mottoTextView;
        TextView titleTextView;
        TextView textTextView;
        TrendsButton setButton;
        TrendsButton resendButton;
        TrendsButton discussButton;
        TrendsButton praiseButton;
        LinearLayout pictureSpaceLinearLayout;
        TrendsLinearLayout trendsLinearLayout;
        TrendsLinearLayout trendsSpaceLinearLayout;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Trends trends = getItem(position); // 获取元素数据
        View view;
        OldTrendsAdapter.oldTrendsViewHolder viewHolder;

        if (convertView == null) { // 这里进行的是缓冲区的View优化
            view = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
            viewHolder = new OldTrendsAdapter.oldTrendsViewHolder();
            viewHolder.headPictureImageView = view.findViewById(R.id.trendHeadPictureImageView);
            viewHolder.setButton = view.findViewById(R.id.trendSetButton);
            viewHolder.discussButton = view.findViewById(R.id.trendDiscussButton);
            viewHolder.praiseButton = view.findViewById(R.id.trendPraiseButton);
            viewHolder.textTextView = view.findViewById(R.id.trendTextTextView);
            viewHolder.titleTextView = view.findViewById(R.id.trendTitleTextView);
            viewHolder.trendsLinearLayout = view.findViewById(R.id.trendsLinearLayout);
            viewHolder.mottoTextView = view.findViewById(R.id.trendMottoTextView);
            viewHolder.resendButton = view.findViewById(R.id.trendResendButton);
            viewHolder.pictureSpaceLinearLayout = view.findViewById(R.id.trendImageSpaceLinearLayout);
            viewHolder.usernameTextView = view.findViewById(R.id.trendUsernameTextView);
            viewHolder.trendsSpaceLinearLayout = view.findViewById(R.id.oldTrendsTrendsSpace);
            // view中绑定viewHolder
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (OldTrendsAdapter.oldTrendsViewHolder) view.getTag();
        }

        // viewHolder.headPictureImageView.setImageResource();
        viewHolder.usernameTextView.setText(trends.username);
        viewHolder.mottoTextView.setText(trends.motto);
        viewHolder.titleTextView.setText(trends.title);
        viewHolder.textTextView.setText(trends.text);
        // viewHolder.pictureSpaceLinearLayout
        if(trends.isPraise) {
            String praiseButtonString = "已点赞 " + trends.praiseNumber;
            viewHolder.praiseButton.setText(praiseButtonString);
        }
        else{
            String praiseButtonString = "点赞 " + trends.praiseNumber;
            viewHolder.praiseButton.setText(praiseButtonString);
        }
        viewHolder.praiseButton.position = position;
        viewHolder.praiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrendsButton trendsButton = (TrendsButton) v;
                Trends trends = getItem(trendsButton.position);
                if (trends.isPraise){
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                    Map<String, String> request = new HashMap<>();
                    request.put("sServeType", "3");
                    request.put("sActivityId", DataKeeper.activityId);
                    request.put("sTrendsId",trends.trendsId);
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
                        Log.d("OldTrendsAdapter", "错误: 点赞信息发送失败");
                        return;
                    } else {
                        switch (respond.charAt(0)) {
                            case '0':
                                trends.isPraise = false;
                                trends.praiseNumber = trends.praiseNumber - 1;
                                trendsButton.setText("点赞 " + trends.discussNumber);
                                break;

                            case '1':
                                return;
                        }
                    }
                }
                else {
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/trends");
                    Map<String, String> request = new HashMap<>();
                    request.put("sServeType", "2");
                    request.put("sActivityId", DataKeeper.activityId);
                    request.put("sTrendsId",trends.trendsId);
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
                        Log.d("OldTrendsAdapter", "错误: 点赞信息发送失败");
                        return;
                    } else {
                        switch (respond.charAt(0)) {
                            case '0':
                                trends.isPraise = true;
                                trends.praiseNumber = trends.praiseNumber + 1;
                                trendsButton.setText("已点赞 " + trends.discussNumber);
                                break;

                            case '1':
                                return;
                        }
                    }
                }
            }
        });
        if(trends.isDiscuss){
            String discussButtonString = "已评论 " + trends.discussNumber;
            viewHolder.discussButton.setText(discussButtonString);
        }
        else{
            String discussButtonString = "评论 " + trends.discussNumber;
            viewHolder.discussButton.setText(discussButtonString);
        }
        viewHolder.discussButton.position = position;
        viewHolder.discussButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrendsButton trendsButton = (TrendsButton) v;
                Trends trends = getItem(trendsButton.position);
                if (trends.isDiscuss){
                    return;
                }
                else{
                    Intent intent = new Intent(getContext(), DiscussActivity.class);
                    intent.putExtra("trendsId",trends.trendsId);
                    intent.putExtra("position",trendsButton.position);
                    getContext().startActivity(intent);
                }
            }
        });
        viewHolder.trendsLinearLayout.position = position;
        if(from == 4) {
            viewHolder.trendsLinearLayout.staticId = trends.staticId;
        }
        viewHolder.trendsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrendsLinearLayout trendsLinearLayout = (TrendsLinearLayout) view;
                Intent intent = new Intent(getContext(), OtherTrendsActivity.class);
                intent.putExtra("from", from);
                if(from == 4) {
                    intent.putExtra("staticId",trendsLinearLayout.staticId);
                }
                else{
                    intent.putExtra("position",trendsLinearLayout.position);
                }
                getContext().startActivity(intent);
            }
        });
        viewHolder.trendsSpaceLinearLayout.position = position;
        if(from == 4){
            viewHolder.trendsSpaceLinearLayout.trendsId = trends.trendsId;
        }
        viewHolder.trendsSpaceLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrendsLinearLayout trendsLinearLayout = (TrendsLinearLayout) v;
                Intent intent = new Intent(getContext(), TrendActivity.class);
                Trends trends = getItem(trendsLinearLayout.position);
                if(from == 4) {
                    intent.putExtra("from", -1);
                    intent.putExtra("trendsId",trendsLinearLayout.trendsId);
                    intent.putExtra("staticId",trends.staticId);
                    intent.putExtra("username",trends.username);
                    intent.putExtra("motto",trends.motto);
                    intent.putExtra("title",trends.title);
                    intent.putExtra("text",trends.text);
                    intent.putExtra("praiseNumber",trends.praiseNumber);
                    intent.putExtra("discussNumber",trends.discussNumber);
                    intent.putExtra("picture",trends.picture);
                    intent.putExtra("isPraise",trends.isPraise);
                    intent.putExtra("isDiscuss",trends.isDiscuss);
                }
                else{
                    intent.putExtra("from",0);
                    intent.putExtra("position",trendsLinearLayout.position);
                }
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}
