package com.example.testapp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FriendsUsersAdapter extends ArrayAdapter<FriendsUser> {

    private int layoutResourceId;

    public FriendsUsersAdapter(Context context, int layoutResourceId, List<FriendsUser> friendsUserListList) {
        super(context, layoutResourceId, friendsUserListList);
        this.layoutResourceId = layoutResourceId;
    }

    class ViewHolder {
        ImageView headPictureImageView;
        TextView usernameTextView;
        TextView newChatTextView;
        TextView newChatNumberTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FriendsUser friendsUser = getItem(position); // 获取元素数据
        View view;
        ViewHolder viewHolder;

        if (convertView == null) { // 这里进行的是缓冲区的View优化
            view = LayoutInflater.from(getContext()).inflate(R.layout.friends_listview_resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.headPictureImageView = view.findViewById(R.id.userHeadPictureImageView);
            viewHolder.usernameTextView = view.findViewById(R.id.userNameTextView);
            viewHolder.newChatTextView = view.findViewById(R.id.newChatTextView);
            viewHolder.newChatNumberTextView = view.findViewById(R.id.newChatNumberTextView);
            // view中绑定viewHolder
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // viewHolder.headPictureImageView.setImageResource();
        viewHolder.usernameTextView.setText(friendsUser.username);
        if (friendsUser.newChatNumber != 0) {
            viewHolder.newChatNumberTextView.setBackgroundResource(R.drawable.round_style_red);
            viewHolder.newChatNumberTextView.setText(String.valueOf(friendsUser.newChatNumber));
            viewHolder.newChatTextView.setText(friendsUser.newChat);
        }
        return view;
    }
}
