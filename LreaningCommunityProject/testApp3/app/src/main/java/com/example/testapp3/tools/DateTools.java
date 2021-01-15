package com.example.testapp3.tools;

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
        String monthString = dateStrings[1];
        String dayString = dateStrings[2];
        String weekString = dateStrings[3];

        year = Integer.valueOf(yearString);

        if(monthString.charAt(0) == '0'){
            month = Integer.valueOf(monthString.substring(1));
        }
        else{
            month = Integer.valueOf(monthString);
        }

        if(dayString.charAt(0) == '0') {
            day = Integer.valueOf(dayString.substring(1));
        }
        else{
            day = Integer.valueOf(dateString);
        }

        if(weekString.charAt(0) == '0') {
            week = Integer.valueOf(weekString.substring(1));
        }
        else{
            week = Integer.valueOf(weekString);
        }

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
}
