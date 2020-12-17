package main;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

public class main {
    public static void main(String[] args){
        /*
        String content = "179772@qq.com";

        String pattern = "^\\d{6,10}@qq.com\\s*$";

        boolean isMatch = Pattern.matches(pattern, content);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
         */

        HttpConnection connection = new HttpConnection("http://10.132.56.32:5000");
        Map<String,String> request = new HashMap<String,String>();
        request.put("data","nihao");
        connection.sendPOST(request);
        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        System.out.println(respond);
    }
}
