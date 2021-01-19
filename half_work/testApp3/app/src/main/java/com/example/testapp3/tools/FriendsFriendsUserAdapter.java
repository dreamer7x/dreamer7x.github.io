package com.example.testapp3.tools;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.resources.FriendsFriendsUser;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.R;
import com.example.testapp3.resources.FriendsButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsFriendsUserAdapter extends ArrayAdapter<FriendsFriendsUser> {

    private final int friendsFriendsResourceId;
    private final int friendsNewFriendsResourceId;

    public FriendsFriendsUserAdapter(Context context,
                                     int layoutResourceId,
                                     List<FriendsFriendsUser> friendsFriendsUserList) {
        super(context,layoutResourceId,friendsFriendsUserList);
        friendsFriendsResourceId = R.layout.friends_friends_listview_resource;
        friendsNewFriendsResourceId = R.layout.friends_new_friends_listview_resource;
    }

    static class friendsViewHolder{
        ImageView headPictureImageView;
        TextView usernameTextView;
        TextView mottoTextView;
    }

    static class newFriendsViewHolder {
        ImageView headPictureImageView;
        TextView usernameTextView;
        TextView introduceTextView;
        FriendsButton refuseButton;
        FriendsButton agreeButton;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FriendsFriendsUser friendsFriendsUser = getItem(position); // 获取元素数据
        View view;
        if(friendsFriendsUser.isFriend){
            friendsViewHolder viewHolder;
            if(convertView != null && convertView.getSourceLayoutResId() == friendsFriendsResourceId){
                view = convertView;
               viewHolder = (FriendsFriendsUserAdapter.friendsViewHolder) view.getTag();
            }
            else {
                view = LayoutInflater.from(getContext()).inflate(friendsFriendsResourceId,parent,false);
                viewHolder = new friendsViewHolder();
                viewHolder.headPictureImageView = view.findViewById(R.id.friendsFriendsHeadPictureImageView);
                viewHolder.usernameTextView = view.findViewById(R.id.friendsFriendsUsernameTextView);
                viewHolder.mottoTextView = view.findViewById(R.id.friendsFriendsMottoTextView);
                view.setTag(viewHolder);
            }
            //viewHolder.headPictureImageView.setImageResource();
            viewHolder.usernameTextView.setText(friendsFriendsUser.username);
            viewHolder.mottoTextView.setText(friendsFriendsUser.motto);
            return view;
        }
        else{
            newFriendsViewHolder viewHolder;
            if(convertView != null && convertView.getSourceLayoutResId() == friendsNewFriendsResourceId){
                view = convertView;
                viewHolder = (FriendsFriendsUserAdapter.newFriendsViewHolder) view.getTag();
            }
            else{
                view = LayoutInflater.from(getContext()).inflate(friendsNewFriendsResourceId,parent,false);
                viewHolder = new newFriendsViewHolder();
                viewHolder.headPictureImageView = view.findViewById(R.id.newFriendsHeadPictureImageView);
                viewHolder.usernameTextView = view.findViewById(R.id.newFriendsUserNameTextView);
                viewHolder.introduceTextView = view.findViewById(R.id.newFriendsIntroduceTextView);
                viewHolder.agreeButton = view.findViewById(R.id.newFriendsAgreeButton);
                viewHolder.refuseButton = view.findViewById(R.id.newFriendsRefuseButton);
                view.setTag(viewHolder);
            }

            //viewHolder.headPictureImageView.setImageResource();
            viewHolder.usernameTextView.setText(friendsFriendsUser.username);
            viewHolder.introduceTextView.setText(friendsFriendsUser.introduce);
            viewHolder.agreeButton.position = position;
            viewHolder.refuseButton.position = position;
            viewHolder.agreeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendsFriendsUser friendsFriendsUser = DataKeeper.
                            friendsFriendsUserList.
                            get(((FriendsButton)view).position);
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_friends");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sStaticId",friendsFriendsUser.staticId);
                    request.put("sServeType","4");
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
                        Log.d("FriendsFriendsAdapter","错误: 同意好友 返回结果为空");
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                friendsFriendsUser.isFriend = true;
                                FriendsFriendsUserAdapter.this.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            });
            viewHolder.refuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/identity_friends");
                    Map<String,String> request = new HashMap<>();
                    request.put("sActivityId",DataKeeper.activityId);
                    request.put("sStaticId",friendsFriendsUser.staticId);
                    request.put("sServeType","5");
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
                        Log.d("FriendsFriendsAdapter","错误: 同意好友 返回结果为空");
                    }
                    else{
                        switch (respond.charAt(0)){
                            case '0':
                                friendsFriendsUser.isFriend = true;
                                DataKeeper.friendsFriendsUserList.remove(((FriendsButton)view).position);
                                FriendsFriendsUserAdapter.this.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            });
            return view;
        }
    }
}
