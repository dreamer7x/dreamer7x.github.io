package com.example.testapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ImageView headImageView;

    private String activityId;

    //工具
    /*
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    Log.d("LoginActivity","获取到图片");
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if(bitmap != null){
                        headImageView.setImageBitmap(bitmap);
                    }
                    break;

                default:
                    break;
            }
        }
    };

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initLauncher();
        setContentView(R.layout.title);
    }

    public void initLauncher() {
        activityId = "000001";
        PublicDataKeeper.activityId = activityId;
        headImageView = findViewById(R.id.titleHeadImageView);

        HttpConnection connection = new HttpConnection(ParameterKeeper.httpUrl + "/identity_sign");
        Map<String, String> request = new HashMap<String, String>();
        request.put("sServeType", "1");
        request.put("sActivityId", activityId);
        connection.sendPOST(request);

        while (connection.getOnWork() != 2) {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String respond = connection.getData();
        switch (respond.charAt(0)) {
            case '0':
                String data = respond.substring(1);
                String[] datas = data.split("\\s");
                PublicDataKeeper.username = datas[0];
                PublicDataKeeper.motto = datas[1];
                PublicDataKeeper.clockIn = datas[2];
                PublicDataKeeper.headPicturePosition = datas[3];
                break;

            case '1':
                Log.d("MainActivity", "要求重新登录");
                setContentView(R.layout.activity_answer);
                TextView textView = findViewById(R.id.answer_TextView);
                textView.setText("要求重新登陆");
        }

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getImageData(PublicDataKeeper.headPicturePosition);
                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }).start();

         */
    }

    /*
    public Bitmap getImageData(String picturePosition){
        HttpURLConnection connection1 = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        Bitmap imgData = null;
        try{
            String data = null;
            URL url = new URL("https://csdnimg.cn/medal/chizhiyiheng@240.png");

            connection1 = (HttpURLConnection)url.openConnection();
            connection1.setRequestMethod("GET");
            connection1.setConnectTimeout(5000);
            connection1.setReadTimeout(5000);
            connection1.setDoOutput(true);

            connection1.setRequestProperty("accept", "*");
            connection1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            outputStream = connection1.getOutputStream();
            outputStream.write(("sServeType=0&sActivityId=000001&sPicturePosition=" + picturePosition).getBytes());
            outputStream.flush();

            inputStream = connection1.getInputStream();
            int responseCode = connection1.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode){
                imgData = BitmapFactory.decodeStream(inputStream);
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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection1 != null) {
                connection1.disconnect();
            }
        }
        return imgData;
    }
    */

    public void onClickToClockInButton(View view){
        Intent intent = new Intent(LoginActivity.this,ClockInActivity.class);
        startActivity(intent);
    }

    public void onClickToMineButton(View view){
        Intent intent = new Intent(LoginActivity.this,MineActivity.class);
        startActivity(intent);
    }

    public void onClickToFriendsButton(View view){
        Intent intent = new Intent(LoginActivity.this,FriendsActivity.class);
        startActivity(intent);
    }

    public void onClickToTrendsButton(View view){
        Intent intent = new Intent(LoginActivity.this,TrendsActivity.class);
        startActivity(intent);
    }
}