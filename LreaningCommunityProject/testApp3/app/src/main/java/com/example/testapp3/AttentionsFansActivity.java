package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.tools.AttentionsFansAdapter;

public class AttentionsFansActivity extends AppCompatActivity {

    // 参数
    private static boolean isRenew;
    private int position;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attentions_fans);
        initView();
    }

    public void initView(){
        listView = findViewById(R.id.attentionsFansListView);

        Intent intent = new Intent();
        switch (intent.getIntExtra("from",0)){
            case 0: {
                AttentionsFansAdapter attentionsFansAdapter = new AttentionsFansAdapter(this,
                        R.layout.attentions_listview_resource,
                        DataKeeper.attentionsUsers);
                listView.setAdapter(attentionsFansAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(AttentionsFansActivity.this,
                                OtherTrendsActivity.class);
                        intent.putExtra("staticId", DataKeeper.attentionsUsers.get(position).staticId);
                        startActivity(intent);
                    }
                });
                position = 0;
            }
                break;

            case 1: {
                AttentionsFansAdapter attentionsFansAdapter = new AttentionsFansAdapter(this,
                        R.layout.attentions_listview_resource,
                        DataKeeper.fansUsers);
                listView.setAdapter(attentionsFansAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(AttentionsFansActivity.this,
                                OtherTrendsActivity.class);
                        intent.putExtra("staticId", DataKeeper.fansUsers.get(position).staticId);
                        startActivity(intent);
                    }
                });
            }
                position = 1;
                break;
        }
    }

    // 事件监听
    public void onClickToAttentionsButton(View view){
        if(position == 0){
            return;
        }
        AttentionsFansAdapter attentionsFansAdapter = new AttentionsFansAdapter(this,
                R.layout.attentions_listview_resource,
                DataKeeper.attentionsUsers);
        listView.setAdapter(attentionsFansAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AttentionsFansActivity.this,
                        OtherTrendsActivity.class);
                intent.putExtra("staticId", DataKeeper.attentionsUsers.get(position).staticId);
                startActivity(intent);
            }
        });
        position = 0;
        return;
    }

    public void onClickToFansButton(View view){
        if(position == 1){
            return;
        }
        AttentionsFansAdapter attentionsFansAdapter = new AttentionsFansAdapter(this,
                R.layout.attentions_listview_resource,
                DataKeeper.fansUsers);
        listView.setAdapter(attentionsFansAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AttentionsFansActivity.this,
                        OtherTrendsActivity.class);
                intent.putExtra("staticId", DataKeeper.fansUsers.get(position).staticId);
                startActivity(intent);
            }
        });
        position = 1;
        return;
    }

    public void onClickAttentionsFansFinishButton(View view){
        finish();
    }
}