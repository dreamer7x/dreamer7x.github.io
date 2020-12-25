package com.example.testapp3;

import java.util.List;

public class DataKeeper {
    // 动态Id
    public static String activityId = "0";

    // 用户 基础社交身份信息
    public static String username;
    public static String motto;
    public static String clockIn;
    public static String headPicture;

    // 好友界面 好友数据
    public static List<FriendsUser> friendsUserList;
    public static List<Trends> trendsList;

    // 动态界面 动态数据

    // 权威时间
    public static String serverTime;
}

