package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import static com.example.testapp.R.drawable.textview01;
import static com.example.testapp.R.drawable.textview02;

public class ClockInTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_test);
    }

    public void initView(){
    }

    public void setClockInButton(View view){

        /*
        HttpConnection connection = new HttpConnection("http://10.132.61.36:5000/register");

        Map<String,String> data = new HashMap<String,String>();
        data.put("flag","0");
        connection.sendPOST(data);
        */

        String date = "2020 12 11 5 0110000000000000000000000000000";
        DateTools dateTools = new DateTools(date);
        int year = dateTools.getYear();
        int month = dateTools.getMonth();
        int day = dateTools.getDay();
        int week = dateTools.getWeek();
        int firstWeek = week - (day % 7 - 1);
        if (firstWeek == 8){
            firstWeek = 1;
        }
        String attendanceInfo = "";
        int i = 0;
        for(i = 0;i < (firstWeek - 1);i++){
            attendanceInfo = attendanceInfo + '0';
        }
        attendanceInfo = attendanceInfo + dateTools.getDailyAttendanceInfo();
        int lastMonthDaysNumber;
        int monthDays = dateTools.getMonthDaysNumber(month);
        if(month == 1){
            lastMonthDaysNumber = dateTools.getMonthDaysNumber(12);
        }
        else{
            lastMonthDaysNumber = dateTools.getMonthDaysNumber(month - 1);
        }
        int[] days = new int[(firstWeek - 1) + monthDays];
        Log.d("ClockInTestActivity","运行正常1" + ' ' + days.length);
        int j = 0;
        Log.d("ClockInTestActivity","运行正常2" + ' ' + firstWeek);
        for(i = 0; i < (firstWeek - 1);i++){
            days[i] = lastMonthDaysNumber - firstWeek + 2 + j;
            j++;
        }
        j = 0;
        for(i = firstWeek - 1; i <= monthDays;i++){
            days[i] = i;
            j++;
        }
        Log.d("ClockInTestActivity","运行正常3");

        String answer = "";
        for(i = 0;i < days.length;i++){
            answer = answer + ' ' + days[i];
        }
        Log.d("ClockInTestActivity",answer);

        TableRow tableRow = findViewById(R.id.tableRow01);
        /*
        {
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setWidth(82);
            textView.setHeight(82);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setBackgroundResource(textview01);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);

            textView.setText("1");
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }

        {
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setWidth(82);
            textView.setHeight(82);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setBackgroundResource(textview01);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);

            textView.setText("2");
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        */

        i = 0;
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(82);
            textView.setHeight(82);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            if(attendanceInfo.charAt(i * 7 + j) == '1'){
                textView.setBackgroundResource(textview01);
            }
            else{
                textView.setBackgroundResource(textview02);
            }

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);

            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        tableRow = findViewById(R.id.tableRow02);
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(82);
            textView.setHeight(82);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setBackgroundResource(textview01);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);

            if(attendanceInfo.charAt(i * 7 + j) == '1'){
                textView.setBackgroundResource(textview01);
            }
            else{
                textView.setBackgroundResource(textview02);
            }

            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        tableRow = findViewById(R.id.tableRow03);
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(82);
            textView.setHeight(82);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setBackgroundResource(textview01);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);

            if(attendanceInfo.charAt(i * 7 + j) == '1'){
                textView.setBackgroundResource(textview01);
            }
            else{
                textView.setBackgroundResource(textview02);
            }

            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        tableRow = findViewById(R.id.tableRow04);
        for(j = 0;j < 7;j++){
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            textView.setWidth(82);
            textView.setHeight(82);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setBackgroundResource(textview01);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);

            if(attendanceInfo.charAt(i * 7 + j) == '1'){
                textView.setBackgroundResource(textview01);
            }
            else{
                textView.setBackgroundResource(textview02);
            }

            String data = "";
            data = data + days[i * 7 + j];
            textView.setText(data);
            linearLayout.addView(textView);
            tableRow.addView(linearLayout);
        }
        i++;

        if(days.length > 28){
            tableRow = findViewById(R.id.tableRow05);
            for(j = 0;j < days.length - 28;j++){
                TextView textView = new TextView(this);
                textView.setTextSize(18);
                textView.setWidth(82);
                textView.setHeight(82);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.WHITE);
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textView.setBackgroundResource(textview01);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setGravity(Gravity.CENTER);

                if(attendanceInfo.charAt(i * 7 + j) == '1'){
                    textView.setBackgroundResource(textview01);
                }
                else{
                    textView.setBackgroundResource(textview02);
                }

                String data = "";
                data = data + days[i * 7 + j];
                textView.setText(data);
                linearLayout.addView(textView);
                tableRow.addView(linearLayout);
            }
        }
        /*days[i * 7 + j]*/
    }
}