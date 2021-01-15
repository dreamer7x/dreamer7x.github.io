package com.example.testapp3.data;

import com.example.testapp3.resources.AttentionsFansUser;
import com.example.testapp3.resources.FriendsChatUser;
import com.example.testapp3.resources.FriendsFriendsUser;
import com.example.testapp3.resources.Trends;

import java.util.List;

public class DataKeeper {
    // 动态Id
    public static String activityId = "0";

    // 用户 基础社交身份信息
    public static String username;
    public static String motto;
    public static String male;
    public static String birth;
    public static String staticId;
    public static String clockIn;
    public static String headPicture;
    public static int attentionsNumber;
    public static int fansNumber;
    public static List<AttentionsFansUser> attentionsUsers;
    public static List<AttentionsFansUser> fansUsers;

    // 好友界面 好友数据
    public static List<FriendsChatUser> friendsChatUserList;
    public static List<FriendsFriendsUser> friendsFriendsUserList;
    public static List<Trends> oldTrendsList;
    public static List<Trends> mineTrendsList;
    public static List<Trends> collectTrendsList;

    // 用以在浏览相关动态的过程中提供缓存机制
    public static List<Trends> trendsPool;

    // 动态界面 动态数据

    // 权威时间
    public static String serverTime;
}

