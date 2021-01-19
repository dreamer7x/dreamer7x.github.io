package com.example.testapp3.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp3.resources.FriendsChatUser;
import com.example.testapp3.R;

import java.util.List;

public class FriendsChatUsersAdapter extends ArrayAdapter<FriendsChatUser> {

    private int layoutResourceId;

    public FriendsChatUsersAdapter(Context context, int layoutResourceId, List<FriendsChatUser> friendsChatUserListList) {
        super(context, layoutResourceId, friendsChatUserListList);
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
        FriendsChatUser friendsChatUser = getItem(position); // 获取元素数据
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
        viewHolder.usernameTextView.setText(friendsChatUser.username);
        if (friendsChatUser.newChatNumber != 0) {
            viewHolder.newChatNumberTextView.setBackgroundResource(R.drawable.round_style_red);
            viewHolder.newChatNumberTextView.setText(String.valueOf(friendsChatUser.newChatNumber));
            viewHolder.newChatTextView.setText(friendsChatUser.newChat);
        }
        return view;
    }
}
