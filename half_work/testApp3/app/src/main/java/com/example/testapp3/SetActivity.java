package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;

import org.w3c.dom.Text;

public class SetActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView mottoTextView;
    private TextView maleTextView;
    private TextView birthTextView;
    private TextView staticIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
    }

    public void initView(){
        usernameTextView = findViewById(R.id.setUsernameTextView);
        mottoTextView = findViewById(R.id.setMottoTextView);
        maleTextView = findViewById(R.id.setMaleTextView);
        birthTextView = findViewById(R.id.setBirthTextView);
        staticIdTextView = findViewById(R.id.setStaticIdTextView);

        usernameTextView.setText(DataKeeper.username);
        mottoTextView.setText(DataKeeper.motto);
        if(DataKeeper.male.equals("1")) {
            maleTextView.setText("男");
        }
        if(DataKeeper.male.equals("2")){
            maleTextView.setText("女");
        }
        if(DataKeeper.male.equals("0")){
            maleTextView.setText("未登记");
        }
        if(DataKeeper.birth.equals("0")) {
            birthTextView.setText("未设置");
        }
        else{
            String[] birthStrings = DataKeeper.birth.split("/");
            if (birthStrings.length != 3) {
                Log.d("SetActivity", "错误: 出生信息");
                return;
            } else {
                String birthString = birthStrings[0] + "年" + birthStrings[1] + "月" + birthStrings[2] + "日";
                birthTextView.setText(birthString);
            }
        }
        staticIdTextView.setText(DataKeeper.staticId);

        return;
    }

    public void onClickToSetHeadPictureButton(View view){

    }

    public void onClickToSetBirthButton(View view){
        
    }

    public void onClickToSetMottoButton(View view){

    }

    public void onCLickToSetUsernameButton(View view){

    }

    public void onClickToSetMaleButton(View view){

    }

    public void onClickSetFinishButton(View view){
        finish();
    }
}