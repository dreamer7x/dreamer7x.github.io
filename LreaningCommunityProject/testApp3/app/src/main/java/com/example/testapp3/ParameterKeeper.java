package com.example.testapp3;

/**
 * 该类用以保存程序执行所需要的静态配置资源
 */

public class ParameterKeeper {

    // 正则表达式
    // 邮箱匹配
    public static String emailRegularExpression = "^\\d{6,10}@qq.com\\s*$";

    // 后端服务器网址
    public static final String dataHttpUrl = "http://10.131.234.226:5000";
    // 前端服务器网址
    public static final String viewHttpUrl = null;

    // 登录类别
    public static int loginPassword = 0;
    public static int loginEmail = 1;
}
