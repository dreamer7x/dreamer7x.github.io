package com.example.testapp3.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapp3.resources.Discuss;

import java.util.List;
import java.util.zip.Inflater;

public class ReplyAdapter extends ArrayAdapter<Discuss> {

    private String userName;
    private String motto;
    private String headPicturePosition;
    private boolean isPraise;
    private boolean isReply;
    private List<Discuss> replyList;

    public ReplyAdapter(@NonNull Context context, int resource, @NonNull List<Discuss> objects,
                        String userName,String motto,String headPicturePosition){
        super(context,resource,objects);
        this.userName = userName;
        this.motto = motto;
        this.headPicturePosition = headPicturePosition;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 第一个为评论内容
        if(position == 0){
            View view = LayoutInflater.from(getContext()).inflate();
        }
        else{

        }
    }
}
