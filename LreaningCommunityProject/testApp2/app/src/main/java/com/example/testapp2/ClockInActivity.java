package com.example.testapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClockInActivity extends AppCompatActivity {
    //数据
    private String clockInString;
    private int day;
    private int firstWeek;

    //工具
    private Button clockInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_in);
        initView();
    }

    public void initView(){
        clockInString = PublicDataKeeper.clockIn;
        clockInButton = findViewById(R.id.clockInButton);

        // 获取权威时间
        HttpConnection connection = new HttpConnection(ParameterKeeper.httpUrl + "/time");
        Map<String,String> request = new HashMap<String, String>();
        request.put("sServeType","0");
        request.put("sActivityId",PublicDataKeeper.activityId);
        connection.sendPOST(request);

        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String respond = connection.getData();

        DateTools dateTools = new DateTools(respond);
        int month = dateTools.getMonth();
        day = dateTools.getDay();
        int week = dateTools.getWeek();
        int monthDays = dateTools.getMonthDaysNumber(month);

        firstWeek = week - (day % 7 - 1);
        if (firstWeek == 8){
            firstWeek = 1;
        }
        Log.d("ClockInTestActivity","firstWeek: " + ' ' + firstWeek);

        int lastMonthDaysNumber;
        if(month == 1){
            lastMonthDaysNumber = dateTools.getMonthDaysNumber(12);
        }
        else{
            lastMonthDaysNumber = dateTools.getMonthDaysNumber(month - 1);
        }
        Log.d("ClockInTestActivity","lastMonthDaysNumber: " + lastMonthDaysNumber);

        int size = firstWeek - 1 + monthDays;
        int length;
        if(size % 7 != 0){
            length = size / 7 + 1;
        }
        else{
            length = size / 7;
        }

        int[] days = new int[length * 7]; // 获取text输出流
        int i;
        int j = 0;
        for(i = 0; i < (firstWeek - 1);i++){
            days[i] = lastMonthDaysNumber - firstWeek + 2 + j;
            j++;
        }
        j = 1;
        for(i = firstWeek - 1; i < size;i++){
            days[i] = j;
            j++;
        }
        j = 1;
        for(i = size;i < length * 7;i++){
            days[i] = j;
            j++;
        }
        Log.d("ClockInTestActivity","days: " + Arrays.toString(days) + length + clockInString);

        int[] clockInInfo = new int[length * 7]; // 获取打卡流 设定TextView样式
        for(i = 0;i < firstWeek - 1;i ++){
            clockInInfo[i] = -1;
        }
        j = 0;
        for(i = firstWeek - 1;i < size;i++){
            if(clockInString.charAt(j) == '0'){
                clockInInfo[i] = 0;
            }
            else{
                clockInInfo[i] = 1;
            }
            j++;
        }
        for(i = size;i < length * 7;i++){
            clockInInfo[i] = -1;
        }
        Log.d("ClockInTestActivity","days: " + Arrays.toString(clockInInfo));

        TableRow tableRow = findViewById(R.id.clockInTableRow01);
        i = 0;
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]){
                case -1:
                    textView.setTextColor(Color.rgb(221,221,221));
                    break;
                case 1:
                    textView.setBackgroundResource(R.drawable.textview03);
                    textView.setTextColor(Color.WHITE);
                    break;
                case 0:
                    textView.setTextColor(Color.BLACK);
                    break;
            }
            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        tableRow = findViewById(R.id.clockInTableRow02);
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]){
                case 1:
                    textView.setBackgroundResource(R.drawable.textview03);
                    textView.setTextColor(Color.WHITE);
                    break;
                case 0:
                    textView.setTextColor(Color.BLACK);
                    break;
            }
            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        tableRow = findViewById(R.id.clockInTableRow03);
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]){
                case 1:
                    textView.setBackgroundResource(R.drawable.textview03);
                    textView.setTextColor(Color.WHITE);
                    break;
                case 0:
                    textView.setTextColor(Color.BLACK);
                    break;
            }
            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        tableRow = findViewById(R.id.clockInTableRow04);
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]){
                case 1:
                    textView.setBackgroundResource(R.drawable.textview03);
                    textView.setTextColor(Color.WHITE);
                    break;
                case 0:
                    textView.setTextColor(Color.BLACK);
                    break;
            }
            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        if(length > 4) {
            tableRow = findViewById(R.id.clockInTableRow05);
            for (j = 0; j < 7; j++) {
                TextView textView = new TextView(this);
                textView.setTextSize(18);
                textView.setWidth(90);
                textView.setHeight(90);
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                switch (clockInInfo[i * 7 + j]) {
                    case -1:
                        textView.setTextColor(Color.rgb(221,221,221));
                        break;
                    case 1:
                        textView.setBackgroundResource(R.drawable.textview03);
                        textView.setTextColor(Color.WHITE);
                        break;
                    case 0:
                        textView.setTextColor(Color.BLACK);
                        break;
                }
                String data = "";
                data = data + days[i * 7 + j];
                textView.setText(data);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.addView(textView);
                tableRow.addView(linearLayout);
            }
            i++;

            if(length > 5){
                tableRow = findViewById(R.id.clockInTableRow06);
                for (j = 0; j < 7; j++) {
                    TextView textView = new TextView(this);
                    textView.setTextSize(18);
                    textView.setWidth(90);
                    textView.setHeight(90);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    switch (clockInInfo[i * 7 + j]) {
                        case -1:
                            textView.setTextColor(Color.rgb(221,221,221));
                            break;
                        case 1:
                            textView.setBackgroundResource(R.drawable.textview03);
                            textView.setTextColor(Color.WHITE);
                            break;
                        case 0:
                            textView.setTextColor(Color.BLACK);
                            break;
                    }
                    String data = "";
                    data = data + days[i * 7 + j];
                    textView.setText(data);
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.addView(textView);
                    tableRow.addView(linearLayout);
                }
                i++;
            }
        }

        if (clockInString.charAt(day - 1) == '1') {
            clockInButton.setText("已打卡");
            return;
        }
        else {
            clockInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clockInString.charAt(day) == '1'){
                        return;
                    }
                    HttpConnection connection02 = new HttpConnection(ParameterKeeper.httpUrl + "/time");
                    Map<String,String> request = new HashMap<String, String>();
                    request.put("sActivityId",PublicDataKeeper.activityId);
                    request.put("sServeType","1");
                    connection.sendPOST(request);

                    while(connection.getOnWork() != 2){
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String respond = connection.getData();
                    Button button = findViewById(R.id.clockInButton);
                    switch (respond.charAt(0)){
                        case '0':
                            int rowPosition;
                            int listPosition;
                            if((day + firstWeek - 1) % 7 != 0) {
                                rowPosition = (day + firstWeek - 1)/7 + 1;
                                listPosition = (day + firstWeek - 1)%7;
                            }
                            else{
                                rowPosition = (day + firstWeek - 1)/7;
                                listPosition = 7;
                            }
                            TableLayout tableLayout = findViewById(R.id.clockInTableLayout);
                            Log.d("ClockInActivity", String.valueOf(rowPosition) + ' ' + listPosition);
                            TableRow tableRow1 = (TableRow) tableLayout.getChildAt(rowPosition);
                            LinearLayout linearLayout = (LinearLayout) tableRow1.getChildAt(listPosition - 1);
                            TextView textView = (TextView) linearLayout.getChildAt(0);
                            textView.setTextColor(Color.WHITE);
                            textView.setBackgroundResource(R.drawable.textview03);
                            StringBuilder stringBuilder = new StringBuilder(PublicDataKeeper.clockIn);
                            stringBuilder.setCharAt(day,'1');
                            PublicDataKeeper.clockIn = stringBuilder.toString();
                            button.setText("已打卡");
                            break;
                        case '1':
                            button.setText("已打卡");
                            break;
                    }
                }
            });
        }
    }

    public void onClickClockInToTitleButton(View view){
        finish();
    }
}