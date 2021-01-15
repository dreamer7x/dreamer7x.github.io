package com.example.testapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.testapp3.data.DataKeeper;
import com.example.testapp3.data.ParameterKeeper;
import com.example.testapp3.tools.DateTools;
import com.example.testapp3.tools.HttpConnection;

import java.util.HashMap;
import java.util.Map;

public class ClockInActivity extends AppCompatActivity {

    //数据
    private String clockInString;
    private int day;
    private int firstWeek;
    private int rowPosition;
    private int listPosition;

    //工具
    private Button clockInButton;
    private TextView todayTextView;
    private TextView monthTextView;
    private TextView yearTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in);
        initView();
    }

    public void initView() {
        clockInString = DataKeeper.clockIn;
        clockInButton = findViewById(R.id.clockInButton);
        monthTextView = findViewById(R.id.clockInMonthTextView);
        yearTextView = findViewById(R.id.clockInYearTextView);

        // 获取权威时间
        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/clock_in");
        Map<String, String> request = new HashMap<String, String>();
        request.put("sServeType", "0");
        request.put("sActivityId", DataKeeper.activityId);
        connection.sendPOST(request);
        while (connection.getOnWork() != 2) {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        if(respond == null){
            Log.d("ClockInActivity", "错误: 打卡信息获取错误");
            return;
        }
        DateTools dateTools = new DateTools(respond);
        int month = dateTools.getMonth();
        monthTextView.setText(String.valueOf(month));
        day = dateTools.getDay();
        int week = dateTools.getWeek();
        int monthDays = dateTools.getMonthDaysNumber(month);
        int year = dateTools.getYear();
        yearTextView.setText(String.valueOf(year));
        Log.d("ClockInActivity","monthDays: " + monthDays + "\n"
                + "week: " + week + "\n"
                + "month: " + month + "\n"
                + "day: " + day);

        firstWeek = week - (day % 7 - 1); // 获取当月 第一天星期数
        if(firstWeek <= 0){
            firstWeek = firstWeek + 7;
        }
        if (firstWeek == 8) {
            firstWeek = 1;
        }
        Log.d("ClockInActivity","firstWeek: " + firstWeek);

        int lastMonthDaysNumber; // 获取 上个月最大天数
        if (month == 1) {
            lastMonthDaysNumber = dateTools.getMonthDaysNumber(12);
        } else {
            lastMonthDaysNumber = dateTools.getMonthDaysNumber(month - 1);
        }

        int size = firstWeek - 1 + monthDays; // 获取 length
        int length;
        if (size % 7 != 0) {
            length = size / 7 + 1;
        } else {
            length = size / 7;
        }
        Log.d("ClockInActivity", "size: " + size + "\n"
                + "length: " + length);

        int[] days = new int[length * 7]; // 获取 text 输出流
        int i;
        int j = 0;
        for (i = 0; i < (firstWeek - 1); i++) {
            days[i] = lastMonthDaysNumber - firstWeek + 2 + j;
            j++;
        }
        j = 1;
        for (i = firstWeek - 1; i < size; i++) {
            days[i] = j;
            j++;
        }
        j = 1;
        for (i = size; i < length * 7; i++) {
            days[i] = j;
            j++;
        }
        int[] clockInInfo = new int[length * 7]; // 获取 样式 输出流
        for (i = 0; i < firstWeek - 1; i++) {
            clockInInfo[i] = -1;
        }
        j = 0;
        for (i = firstWeek - 1; i < size; i++) {
            if (clockInString.charAt(j) == '0') {
                clockInInfo[i] = 0;
            } else {
                clockInInfo[i] = 1;
            }
            j++;
        }
        for (i = size; i < length * 7; i++) {
            clockInInfo[i] = -1;
        }

        TableRow tableRow = findViewById(R.id.clockInTableRow01);
        i = 0;
        for (j = 0; j < 7; j++) {
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]) {
                case -1:
                    textView.setTextColor(Color.rgb(221, 221, 221));
                    break;
                case 1:
                    textView.setBackgroundResource(R.drawable.corner_style_grey01);
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
        for (j = 0; j < 7; j++) {
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]) {
                case 1:
                    textView.setBackgroundResource(R.drawable.corner_style_grey01);
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
        for (j = 0; j < 7; j++) {
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]) {
                case 1:
                    textView.setBackgroundResource(R.drawable.corner_style_grey01);
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
        for (j = 0; j < 7; j++) {
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(90);
            textView.setHeight(90);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            switch (clockInInfo[i * 7 + j]) {
                case 1:
                    textView.setBackgroundResource(R.drawable.corner_style_grey01);
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

        if (length > 4) {
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
                        textView.setTextColor(Color.rgb(221, 221, 221));
                        break;
                    case 1:
                        textView.setBackgroundResource(R.drawable.corner_style_grey01);
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

            if (length > 5) {
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
                            textView.setTextColor(Color.rgb(221, 221, 221));
                            break;
                        case 1:
                            textView.setBackgroundResource(R.drawable.corner_style_grey01);
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

        // 获取 当天日期 TextView
        if((day + firstWeek - 1) % 7 != 0) {
            rowPosition = (day + firstWeek - 1)/7 + 1;
            listPosition = (day + firstWeek - 1)%7;
        }
        else{
            rowPosition = (day + firstWeek - 1)/7;
            listPosition = 7;
        }
        TableLayout tableLayout = findViewById(R.id.clockInTableLayout);
        TableRow tableRow02 = (TableRow) tableLayout.getChildAt(rowPosition);
        LinearLayout linearLayout = (LinearLayout) tableRow02.getChildAt(listPosition - 1);
        todayTextView = (TextView) linearLayout.getChildAt(0);

        if(clockInString.charAt(day - 1) == '1'){
            todayTextView.setTextColor(Color.WHITE);
            todayTextView.setBackgroundResource(R.drawable.corner_style_grey01);
            clockInButton.setText("已打卡");
        }
    }

    public void onClickClockInButton(View view){
        if(clockInString.charAt(day - 1) == '1'){
            return;
        }
        HttpConnection connection = new HttpConnection(ParameterKeeper.dataHttpUrl + "/clock_in");
        Map<String,String> request = new HashMap<String, String>();
        request.put("sActivityId",DataKeeper.activityId);
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
        if(respond == null){
            setContentView(R.layout.answer);
            return;
        }
        switch (respond.charAt(0)){
            case '0':
                // 修改 日期样式
                todayTextView.setTextColor(Color.WHITE);
                todayTextView.setBackgroundResource(R.drawable.corner_style_grey01);
                StringBuilder stringBuilder = new StringBuilder(DataKeeper.clockIn);
                stringBuilder.setCharAt(day,'1');
                DataKeeper.clockIn = stringBuilder.toString();
                clockInString = DataKeeper.clockIn;
                clockInButton.setText("已打卡");
                break;
            case '1':
                clockInButton.setText("已打卡");
                break;
        }
    }

    public void onClickFinishClockInButton(View view){
        finish();
    }
}