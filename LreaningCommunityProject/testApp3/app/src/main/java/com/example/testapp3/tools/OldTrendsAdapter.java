package com.example.testapp3.tools;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp3.OtherTrendsActivity;
import com.example.testapp3.R;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.resources.TrendsButton;
import com.example.testapp3.resources.TrendsLinearLayout;

import java.util.List;

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
        String discussButtonString = "评论 " + trends.discussNumber;
        viewHolder.discussButton.setText(discussButtonString);
        if(trends.isPraise) {
            String praiseButtonString = "已点赞 " + trends.praiseNumber;
            viewHolder.praiseButton.setText(praiseButtonString);
        }
        else{
            String praiseButtonString = "点赞 " + trends.praiseNumber;
            viewHolder.praiseButton.setText(praiseButtonString);
        }
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
        return view;
    }
}
