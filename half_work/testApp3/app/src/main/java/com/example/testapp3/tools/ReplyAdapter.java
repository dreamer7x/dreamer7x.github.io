package com.example.testapp3.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp3.R;
import com.example.testapp3.resources.Discuss;
import com.example.testapp3.resources.Reply;
import com.example.testapp3.resources.Trends;
import com.example.testapp3.resources.TrendsButton;
import com.example.testapp3.resources.TrendsLinearLayout;

import java.util.List;
import java.util.zip.Inflater;

public class ReplyAdapter extends ArrayAdapter<Reply> {

    // 组件
    private int discussLayoutResource;
    private int replyLayoutResource;

    // 参数
    private String staticId;
    private String userName;
    private String motto;
    private String headPicturePosition;
    private String discussString;
    private int praiseNumber;
    private int replyNumber;
    private boolean isPraise;
    private boolean isReply;

    public ReplyAdapter(@NonNull Context context, int resource, @NonNull List<Reply> objects,
                        String staticId, String userName,String motto,String headPicturePosition,
                        String discussString,
                        int praiseNumber,int replyNumber, boolean isPraise,boolean isReply){
        super(context, resource, objects);
        this.staticId = staticId;
        this.userName = userName;
        this.motto = motto;
        this.headPicturePosition = headPicturePosition;
        this.discussString = discussString;
        this.discussLayoutResource = R.layout.trend_listview_resource01;
        this.replyLayoutResource = R.layout.trend_listview_resource04;
        this.praiseNumber = praiseNumber;
        this.replyNumber = replyNumber;
        this.isPraise = isPraise;
        this.isReply = isReply;
    }

    class ViewHolder {
        public boolean isFirst = false;
    }

    class DiscussViewHolder extends ViewHolder{
        public TextView usernameTextView;
        public ImageView headPictureImageView;
        public TextView mottoTextView;
        public TextView discussTextView;
        public TrendsButton praiseButton;
        public TrendsButton replyButton;
    }

    class ReplyViewHolder extends ViewHolder{
        public TextView usernameTextView;
        public TextView replyTextView;
        public TrendsButton praiseButton;
    }

//  sRespond1 = staticId<spa1>discussString<spa1>praiseNumber<spa>discussNumber<spa>
//  isPraise<spa>isDiscuss<spa>
//  sRespond2 = staticId<spa>replyString<spa>isPraise<spa1>
//  sRespond = sRespond1 + sRespond2

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 第一个为评论内容
        if (position == 0) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.trend_listview_resource01, parent, false);
            // 设定一定的间隔来和回复区分开来
            View spaceView = view.findViewById(R.id.trendListViewResourceSpaceView);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 30;
            spaceView.setLayoutParams(params);
            DiscussViewHolder viewHolder = new DiscussViewHolder();
            viewHolder.isFirst = true;
            viewHolder.usernameTextView = view.findViewById(R.id.trendListViewResourceUsernameTextView);
            viewHolder.mottoTextView = view.findViewById(R.id.trendListViewResourceMottoTextView);
            viewHolder.discussTextView = view.findViewById(R.id.trendListViewResourceDiscussTextView);
            viewHolder.headPictureImageView = view.findViewById(R.id.trendListViewResourceHeadPicture);
            viewHolder.praiseButton = view.findViewById(R.id.trendListViewResourcePraiseButton);
            viewHolder.replyButton = view.findViewById(R.id.trendListViewResourceReplyButton);
            view.setTag(viewHolder);
            viewHolder.usernameTextView.setText(userName);
            viewHolder.mottoTextView.setText(motto);
            viewHolder.discussTextView.setText(discussString);
            // viewHolder.headPictureImageView
            if(isPraise) {
                viewHolder.praiseButton.setText("已点赞 " + praiseNumber);
            }
            else{
                viewHolder.praiseButton.setText("点赞 " + praiseNumber);
            }
            if(isReply){
                viewHolder.replyButton.setText("已评论 " + replyNumber);
            }
            else{
                viewHolder.replyButton.setText("评论 " + replyNumber);
            }
            return view;
        } else {
            Reply reply = getItem(position - 1);
            if (convertView != null) {
                ViewHolder viewHolder  = (ViewHolder) convertView.getTag();
                if(viewHolder.isFirst){
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.trend_listview_resource04,
                            parent, false);
                    // 设定一定的间隔来和回复区分开来
                    ReplyViewHolder replyViewHolder = new ReplyViewHolder();
                    replyViewHolder.usernameTextView = view.findViewById(R.id.trendListViewResourceReplyUsernameTextView);
                    replyViewHolder.replyTextView = view.findViewById(R.id.trendListViewResourceReplyTextView);
                    view.setTag(viewHolder);
                    replyViewHolder.usernameTextView.setText(reply.username);
                    replyViewHolder.replyTextView.setText(reply.replyString);
                    if(reply.isPraise) {
                        replyViewHolder.praiseButton.setText("已点赞: " + reply.praiseNumber);
                    }
                    else{
                        replyViewHolder.praiseButton.setText("点赞: " + reply.praiseNumber);
                    }
                    return view;
                }
                View view = convertView;
                ReplyViewHolder replyViewHolder = (ReplyViewHolder) view.getTag();
                replyViewHolder.usernameTextView.setText(reply.username);
                replyViewHolder.replyTextView.setText(reply.replyString);
                if(reply.isPraise) {
                    replyViewHolder.praiseButton.setText("已点赞: " + reply.praiseNumber);
                }
                else{
                    replyViewHolder.praiseButton.setText("点赞: " + reply.praiseNumber);
                }
                // viewHolder.headPictureImageView
                return view;
            } else {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.trend_listview_resource04, parent, false);
                // 设定一定的间隔来和回复区分开来
                ReplyViewHolder replyViewHolder = new ReplyViewHolder();
                replyViewHolder.usernameTextView = view.findViewById(R.id.trendListViewResourceUsernameTextView);
                view.setTag(replyLayoutResource);
                replyViewHolder.usernameTextView.setText(reply.username);
                replyViewHolder.replyTextView.setText(reply.replyString);
                if(reply.isPraise) {
                    replyViewHolder.praiseButton.setText("已点赞: " + reply.praiseNumber);
                }
                else{
                    replyViewHolder.praiseButton.setText("点赞: " + reply.praiseNumber);
                }
                return view;
            }
        }
    }
}
