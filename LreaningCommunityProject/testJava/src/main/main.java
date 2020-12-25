package main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class main {
    public static void main(String[] args) {
        /*
        String content = "179772@qq.com";

        String pattern = "^\\d{6,10}@qq.com\\s*$";

        boolean isMatch = Pattern.matches(pattern, content);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
         */

        /*
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
        */

        /*
        HttpConnection connection = new HttpConnection("https://csdnimg.cn/medal/qixiebiaobing1@240.png");
        Map<String,String> request = new HashMap<String,String>();
        request.put("sServeType","0");
        request.put("sActivityId","000001");
        request.put("sPicturePosition","000001");
        connection.sendGET();
        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        File file = new File("c:/users/dreamer7x/desktop/picture", "000001.png");
        try{
            file.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        byte[] pictureData = respond.getBytes();
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(pictureData,0,pictureData.length);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(respond);

         */
        /*
        String url = "https:10.131.234.226:5000/picture/qixiebiaobing1@240.png";
        URL uri = null;
        try {
            uri = new URL(url);
            InputStream in = null;
            in = uri.openStream();
            FileOutputStream fo = null;//文件输出流
            fo = new FileOutputStream(new File("c:/users/dreamer7x/desktop/picture", "000001.png"));
            byte[] buf = new byte[1024];
            int length = 0;
            System.out.println("开始下载:" + url);
            while (true) {
                if (!((length = in.read(buf, 0, buf.length)) != -1)) break;
                fo.write(buf, 0, length);
            }
            in.close();
            fo.close();
            System.out.println(url + "下载完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭流
        */


        /*
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferReader;
        try{
            String data = null;
            URL url = new URL("http://10.131.234.226:5000/picture");

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            connection.setRequestProperty("accept", "*");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            outputStream = connection.getOutputStream();
            outputStream.write("sServeType=0&sActivityId=000001&sPicturePosition=000001".getBytes());
            outputStream.flush();
            
            inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            FileOutputStream fo = new FileOutputStream(new File("c:/users/dreamer7x/desktop/picture", "000001.png"));
            while (true) {
                if (!((length = inputStream.read(buffer, 0, buffer.length)) != -1)) break;
                fo.write(buffer, 0, length);
            }
        }
        catch(MalformedURLException e){
            System.out.println("HttpConnectionDemo" + "错误: URL类对象构造错误");
            e.printStackTrace();
        }
        catch(IOException e){
            System.out.println("HttpConnectionDemo" + "错误: URL创建连接失败");
            e.printStackTrace();
        }
        finally{
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                connection.disconnect();
            }
        }

        /*
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferReader;
        try{
            String data = null;
            URL url = new URL("http://10.131.234.226:5000/picture");

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            connection.setRequestProperty("accept", "*");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            outputStream = connection.getOutputStream();
            outputStream.write("sServeType=0&sActivityId=000003&sPicturePosition=000001".getBytes());
            outputStream.flush();

            inputStream = connection.getInputStream();
            byte[] respond = inputStream.readAllBytes();
            System.out.println(respond);
            if(respond[0] == '0'){
                FileOutputStream fo = new FileOutputStream(new File("c:/users/dreamer7x/desktop/picture", "000001.png"));
                System.arraycopy(respond,1,respond,0,respond.length - 1);
                fo.write(respond);
            }

        }
        catch(MalformedURLException e){
            System.out.println("HttpConnectionDemo" + "错误: URL类对象构造错误");
            e.printStackTrace();
        }
        catch(IOException e){
            System.out.println("HttpConnectionDemo" + "错误: URL创建连接失败");
            e.printStackTrace();
        }
        finally{
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                connection.disconnect();
            }
        }
        */
        String content = "179772@qq.com";

        String pattern = "^\\d{6,10}@qq.com\\s*$";

        boolean isMatch = Pattern.matches(pattern, content);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);

        String data = "<spa>nihao nihao";
        String[] datas = data.split("<spa>");
        System.out.println(datas[0].equals("")); // 使用equals方法来判断是否是空字符串
        for(int i = 0;i < datas.length;i++){
            System.out.println(datas[i] + '|');
        }

        String c = "nihao";
        c = null;
        if(c == null){
            System.out.println("yes");
        }

        c = "0";
        String string = c.substring(1);
        System.out.println(string);
        String[] d = c.split("\\s");
        for(int i = 0;i < d.length;i++) {
            System.out.println(d[i]);
        }

        if(d[0].equals("")){
            System.out.println("yes");
        }

        String a = "ji n n d s";
        String[] strings;
        strings = a.split("\\s");
        a = "";
        for(int i = 0;i < strings.length;i ++){
            a = a + strings[i] + " ";
        }
        System.out.println(a);

        a = "I want to say<spa2>";
        String[] b = a.split("<spa2>");
        System.out.println(b.length);
     }
}
