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

import com.example.testapp3.resources.AttentionsFansUser;
import com.example.testapp3.R;

import java.util.List;

public class AttentionsFansAdapter extends ArrayAdapter<AttentionsFansUser> {

    private int layoutResourceId;

    public AttentionsFansAdapter(Context context, int layoutResourceId, List<AttentionsFansUser> attentionsFansUserList) {
        super(context, layoutResourceId, attentionsFansUserList);
        this.layoutResourceId = layoutResourceId;
    }

    class ViewHolder {
        ImageView headPictureImageView;
        TextView usernameTextView;
        TextView mottoTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AttentionsFansUser attentionsFansUser = getItem(position); // 获取元素数据
        View view;
        AttentionsFansAdapter.ViewHolder viewHolder;

        if (convertView == null) { // 这里进行的是缓冲区的View优化
            view = LayoutInflater.from(getContext()).inflate(layoutResourceId,
                    parent,
                    false);
            viewHolder = new AttentionsFansAdapter.ViewHolder();
            viewHolder.headPictureImageView = view.findViewById(R.id.attentionsHeadPicturePosition);
            viewHolder.usernameTextView = view.findViewById(R.id.attentionsUsernameTextView);
            viewHolder.mottoTextView = view.findViewById(R.id.attentionsMottoTextView);
            // view中绑定viewHolder
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (AttentionsFansAdapter.ViewHolder) view.getTag();
        }

        viewHolder.usernameTextView.setText(attentionsFansUser.username);
        viewHolder.mottoTextView.setText(attentionsFansUser.motto);
        // viewHolder.headPictureImageView
        return view;
    }
}