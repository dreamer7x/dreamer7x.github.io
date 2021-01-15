package com.example.testapp2;

/**
 * 该类用以保存程序执行所需要的静态配置资源
 */

public class ParameterKeeper {

    //正则表达式静态公式
    public static String emailRegularExpression = "^\\d{6,10}@qq.com\\s*$";

    //服务器网址
    public static final String httpUrl = "http://10.132.43.80:5000";

    //登录类别
    public static int loginPassword = 0;
    public static int loginEmail = 1;

}
