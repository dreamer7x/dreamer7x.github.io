package com.example.testapp3.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp3.R;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.resources.TrendsButton;

import java.util.List;

public class OtherMineTrendsAdapter extends ArrayAdapter<Trends> {

    private int layoutResourceId;

    public OtherMineTrendsAdapter(@NonNull Context context, int resource, @NonNull List<Trends> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
    }

    static class trendsAdapterViewHolder{
        TextView titleTextView;
        TextView textTextView;
        LinearLayout pictureSpaceLinearLayout;
        TrendsButton resendButton;
        TrendsButton discussButton;
        TrendsButton praiseButton;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Trends trends = getItem(position); // 获取元素数据
        View view;
        OtherMineTrendsAdapter.trendsAdapterViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(layoutResourceId,parent,false);
            viewHolder = new trendsAdapterViewHolder();
            viewHolder.titleTextView = view.findViewById(R.id.trendTitleTextView);
            viewHolder.textTextView = view.findViewById(R.id.trendTextTextView);
            viewHolder.pictureSpaceLinearLayout = view.findViewById(R.id.trendImageSpaceLinearLayout);
            viewHolder.resendButton = view.findViewById(R.id.trendResendButton);
            viewHolder.discussButton = view.findViewById(R.id.trendDiscussButton);
            viewHolder.praiseButton = view.findViewById(R.id.trendPraiseButton);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (trendsAdapterViewHolder) convertView.getTag();
        }

        viewHolder.titleTextView.setText(trends.title);
        viewHolder.textTextView.setText(trends.text);
        // viewHolder.pictureSpaceLinearLayout
        viewHolder.discussButton.setText("评论 " + trends.discussNumber);
        if(trends.isPraise) {
            viewHolder.praiseButton.setText("已点赞 " + trends.praiseNumber);
        }
        else{
            viewHolder.praiseButton.setText("点赞 " + trends.praiseNumber);
        }
        return view;
    }
}
