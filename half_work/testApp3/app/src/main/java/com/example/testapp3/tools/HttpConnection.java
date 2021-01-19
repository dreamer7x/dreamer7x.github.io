package com.example.testapp3.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * HttpConnectionDemo
 *
 * 权限要求:
 * <uses-permission android:name="android.permission.INTERNET"/>
 * android:usesCleartextTraffic="true"
 * 否则将报错
 *
 * 使用说明:
 * 遵循特定形式:
 * HttpConnection connection = new HttpConnection(<服务器网址> + <端口>/<服务器URL> + <端口名>);
 * Map<String,String> request = new HashMap<>();
 * ……(使用put方法将数据打包程字典格式)
 * connection.sendPOST(request);
 * while(connection.getOnWork() != 2){
 *     try{
 *          Thread.sleep(200);
 *     }
 *     catch (InterruptedException e) {
 *          e.printStackTrace();
 *     }
 * }
 * String respond = connection.getData();
 * if(respond == null){
 *     ……(网络未连接从而没有收到数据包,可以进行相关的处理)
 * }
 * else{
 *     switch(respond.charAt(0)){
 *         case '0':
 *              ……(数据包发送成功,可以进行一般操作);
 *
 *         ……(使用 case 来处理多种情况)
 *     }
 * }
 * 采用线程创建连接,当工作状态为2时,即为工作执行已产生结果数据,可以调用getData获取相关数据
 */

public class HttpConnection {

    //相关配置参数
    private String urlString;
    private int onWork;
    private int connectionConnectTimeout;
    private int connectionGetDataTimeout;

    //内存数据
    private String dataString;

    //工具
    private final Object threadLock01;
    private HttpURLConnection connection;
    private OutputStream outPutStream;
    private InputStream inputStream;
    private BufferedReader reader;
    private URL url;

    public HttpConnection(String urlString){
        onWork = 0;
        this.urlString = urlString;
        dataString = null;
        connectionConnectTimeout = 5000;
        connectionGetDataTimeout = 5000;
        threadLock01 = new Object();
        outPutStream = null;
        inputStream = null;
        reader = null;
    }

    public void setUrlString(String urlString){
        this.urlString = urlString;
    }

    public int getOnWork(){
        int status;
        synchronized (threadLock01) {
            status = this.onWork;
        }
        return status;
    }

    private void setOnWork(){
        int status;

        synchronized (threadLock01) {
            status = onWork;
        }
        switch (status){
            case 0:
                synchronized (threadLock01) {
                    onWork = 1;
                }
                break;
            case 1:
                synchronized (threadLock01) {
                    onWork = 2;
                }
                break;
            case 2:
                synchronized (threadLock01) {
                    onWork = 0;
                }
                break;
        }
    }

    private void setData(String data){
        if(getOnWork() != 1){
            return;
        }
        else{
            dataString = data;
        }
    }

    public String getData(){
        if(getOnWork() != 2){
            Log.d("HttpConnectionDemo","警告: getData方法执行警告 当前没有数据提供");
            return "";
        }
        else{
            String data = this.dataString;
            setOnWork();
            return data;
        }
    }

    public void sendPOST(Map<String,String> dataMap){
        if(getOnWork() != 0){
            Log.d("HttpConnectionDemo","错误: sendPOST方法执行失败 有其他工作正在执行");
            return;
        }
        else {
            setOnWork();
        }

        new Thread(new Runnable(){
            public void run(){
                try{
                    String data = null;
                    URL url = new URL(urlString);

                    data = "";
                    for (Object key : dataMap.keySet()) {
                        data = data + key + '=' + dataMap.get(key) + '&';
                    }
                    data = data + "last=done";

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(connectionConnectTimeout);
                    connection.setReadTimeout(connectionGetDataTimeout);
                    connection.setDoOutput(true);

                    outPutStream = connection.getOutputStream();
                    outPutStream.write(data.getBytes());
                    outPutStream.flush();

                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String dataPiece = "";
                    String dataString = "";

                    while ((dataPiece = reader.readLine()) != null) {
                        dataString = dataString + dataPiece;
                    }

                    setData(dataString);
                }
                catch(MalformedURLException e){
                    Log.d("HttpConnection","错误: URL类对象构造错误");
                    e.printStackTrace();
                }
                catch (IOException e){
                    Log.d("HttpConnection","错误: URL类对象创建错误");
                    e.printStackTrace();
                }
                finally{
                    setOnWork();
                    if(outPutStream != null) {
                        try {
                            outPutStream.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void sendGET(){
        if (getOnWork() != 0) {
            Log.d("HttpConnectionDemo", "错误: sendPOST方法执行失败 其他工作正在执行");
            return;
        }
        else {
            setOnWork();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = null;
                    URL url = new URL(urlString);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(connectionConnectTimeout);
                    connection.setReadTimeout(connectionGetDataTimeout);
                    connection.setDoOutput(true);

                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String dataPiece = "";
                    String dataString = "";

                    while ((dataPiece = reader.readLine()) != null) {
                        dataString = dataString + dataPiece;
                    }

                    setData(dataString);
                }
                catch(MalformedURLException e){
                    System.out.print("错误: URL类对象构造错误");
                    e.printStackTrace();
                }
                catch(IOException e){
                    System.out.print("错误: URL创建连接失败");
                    e.printStackTrace();
                }
                finally{
                    setOnWork();
                    if(outPutStream != null) {
                        try {
                            outPutStream.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
