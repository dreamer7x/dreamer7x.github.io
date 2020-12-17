package com.example.testapp;

import java.util.GregorianCalendar;

public class DateTools {

    private static int[] months;
    private int year;
    private int month;
    private int day;
    private int week;
    private String dailyAttendanceInfo;

    public DateTools(String dateString){
        String[] dateStrings = dateString.split("\\s+");
        String yearString = dateStrings[0];
        year = Integer.valueOf(yearString);
        String monthString = dateStrings[1];
        month = Integer.valueOf(monthString);
        String dayString = dateStrings[2];
        day = Integer.valueOf(dayString);
        String weekString = dateStrings[3];
        week = Integer.valueOf(weekString);
        dailyAttendanceInfo = dateStrings[4];

        GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        boolean isLeapYear = gregorianCalendar.isLeapYear(year);

        months = new int[]{31, isLeapYear ? 28 : 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    }

    // 获取年份
    public int getYear(){
        return year;
    }

    // 获取月份
    public int getMonth(){
        return month;
    }

    // 获取日份
    public int getDay(){
        return day;
    }

    // 获取星期
    public int getWeek(){
        return week;
    }

    //获取月份天数
    public int getMonthDaysNumber(int month){
        return months[month - 1];
    }

    // 获取打卡信息
    public String getDailyAttendanceInfo(){
        return dailyAttendanceInfo;
    }
}
