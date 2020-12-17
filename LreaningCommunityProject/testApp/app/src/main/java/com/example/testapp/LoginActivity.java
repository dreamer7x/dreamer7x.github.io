package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class LoginActivity extends AppCompatActivity {

    //参数
    private Boolean isAuto;
    private Boolean isGetCheckData;

    //邮箱 密码 动态码
    private String email;
    private String password;
    private String activityId;

    //工具
    private int checkDataTimeout;
    private Button checkDataButton;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initLauncher();
        checkLoginData();

        if(isAuto){
            switch(autoLogin()){
                case -1: {
                    setContentView(R.layout.activity_password_login);
                    EditText mailEditText = findViewById(R.id.emailEditText);
                    mailEditText.setText(email);
                    EditText passwordEditText = findViewById(R.id.passwordEditText);
                    passwordEditText.setText(password);
                }
                    break;

                case 0:
                    PublicDataKeeper.activityId = this.activityId;
                    Intent intent = new Intent(LoginActivity.this,TitleActivity.class);
                    startActivity(intent);
                    break;

                case 1: // 邮箱不正确
                    setContentView(R.layout.activity_password_login);
                    break;

                case 2: { // 密码不正确
                    setContentView(R.layout.activity_password_login);
                    EditText mailEditText = findViewById(R.id.emailEditText);
                    mailEditText.setText(email);
                }
                    break;

                case 3: { //动态码不正确要求进行验证
                    setContentView(R.layout.activity_email_login_register);
                    EditText mailEditText = findViewById(R.id.emailEditText);
                    mailEditText.setText(email);
                }
                    break;
            }
        }
        else{
            setContentView(R.layout.activity_password_login);
        }
    }

    public void initLauncher(){
        email = "";
        password = "";
        isAuto = false;
        isGetCheckData = true;

        handler = handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 10:
                        Log.d("LoginActivity","验证码修改");
                        if(checkDataTimeout > 0) {
                            checkDataTimeout--;
                            checkDataButton.setText("" + checkDataTimeout);
                            handler.sendEmptyMessageDelayed(10,1000);
                        }
                        else{
                            handler.sendEmptyMessageDelayed(11,1000);
                        }
                        break;

                    case 11:
                        Log.d("LoginActivity","可获取验证码");
                        checkDataButton.setTextColor(0xFF444444);
                        checkDataButton.setText("获取验证码");
                        isGetCheckData = true;
                        break;
                }
            }
        };
    }

    public void checkLoginData(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        email = sharedPreferences.getString("email","");
        password = sharedPreferences.getString("password","");
        activityId = sharedPreferences.getString("activityId","");

        if(activityId == ""){
            activityId = "000000";
        }

        if(email != "" && Pattern.matches(ParameterKeeper.emailRegularExpression,email)){
            isAuto = true;
        }
    }

    /*
    返回值：
    -1 连接错误
     */
    public int autoLogin(){
        HttpConnection connection = new HttpConnection(ParameterKeeper.httpUrl + "/login");

        Map<String,String> requestData = new HashMap<String,String>();
        requestData.put("sServeType","0");
        requestData.put("sEmail",email);
        requestData.put("sPassword",password);
        requestData.put("sActivityId",activityId);
        connection.sendPOST(requestData);

        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String respondData = connection.getData();
        if(respondData == null) {
            return -1;
        }
        else {
            switch (respondData.charAt(0)) {
                case '0':
                    activityId = respondData.substring(1, 7);
                    return 0;

                default:
                    return (int)respondData.charAt(0) - 48;
            }
        }
    }

    public void onClickToEmailLoginButton(View view){
        EditText emailEditText = findViewById(R.id.emailEditText);
        String emailString = emailEditText.getText().toString();
        setContentView(R.layout.activity_email_login_register);
        if(emailString != null){
            emailEditText = findViewById(R.id.emailEditText);
            emailEditText.setText(emailString);
        }
    }

    public void onClickPasswordLoginButton(View view){
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        CheckBox rememberPasswordCheckBox = findViewById(R.id.remember_password_checkbox01);
        String emailString = emailEditText.getText().toString();
        if (emailString == null){
            emailEditText.setHintTextColor(0xFFDD2E2E);
            emailEditText.setHint("邮箱为空");
            return;
        }
        if (!Pattern.matches(ParameterKeeper.emailRegularExpression, emailString)){
            emailEditText.setText("");
            emailEditText.setHintTextColor(0xFFDD2E2E);
            emailEditText.setHint("邮箱格式不符合规范");
            return;
        }
        String passwordString = passwordEditText.getText().toString();
        if (passwordEditText == null){
            passwordEditText.setHintTextColor(0xFFDD2E2E);
            passwordEditText.setHint("密码为空");
            return;
        }
        HttpConnection connection = new HttpConnection(ParameterKeeper.httpUrl + "/login");
        Map<String, String> request = new HashMap<String, String>();
        request.put("sServeType", "0");
        request.put("sEmail", emailString);
        request.put("sPassword", passwordString);
        request.put("sActivityId", activityId);
        connection.sendPOST(request);
        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String respond = connection.getData();
        if(respond == null){
            Log.d("LoginActivity","普通登录失败,respond为空");
            return;
        }
        switch (respond.charAt(0)){
            case '0':
                activityId = respond.substring(1,7);
                PublicDataKeeper.activityId = activityId;
                if(rememberPasswordCheckBox.isChecked()){
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email",emailString);
                    editor.putString("password",passwordString);
                    editor.putString("activityId",activityId);
                    editor.apply();
                }
                else{
                    if(isAuto){

                    }
                }
                Intent intent = new Intent(LoginActivity.this,TitleActivity.class);
                startActivity(intent);
                return;

            case '1':
            case '2':
                emailEditText.setText("");
                passwordEditText.setText("");
                emailEditText.setHintTextColor(0xFFDD2E2E);
                emailEditText.setHint("邮箱或密码不正确");
                return;

            case '3':
                setContentView(R.layout.activity_email_login_register);
                emailEditText = findViewById(R.id.emailEditText);
                emailEditText.setText(emailString);
                emailEditText.setHint("要求重新验证");
                return;
        }
    }

    public void onClickToPasswordLoginButton(View view){
        EditText emailEditText = findViewById(R.id.emailEditText);
        String emailString = emailEditText.getText().toString();
        setContentView(R.layout.activity_password_login);
        emailEditText = findViewById(R.id.emailEditText);
        if (emailString != null){
            emailEditText.setText(emailString);
        }
        return;
    }

    public void onClickEmailLoginButton(View view){
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText checkDataEditText = findViewById(R.id.checkDataEditText);
        CheckBox rememberCheckBox = findViewById(R.id.remember_password_checkbox02);
        String emailString = emailEditText.getText().toString();
        if(emailEditText == null){
            emailEditText.setHintTextColor(0xFFDD2E2E);
            emailEditText.setHint("邮箱为空");
            return;
        }
        if(!Pattern.matches(ParameterKeeper.emailRegularExpression,emailString)){
            emailEditText.setHintTextColor(0xFFDD2E2E);
            emailEditText.setHint("邮箱格式不正确");
            return;
        }
        String checkData = checkDataEditText.getText().toString();
        if(checkData == null){
            checkDataEditText.setHintTextColor(0xFFDD2E2E);
            checkDataEditText.setHint("验证码为空");
            return;
        }

        HttpConnection connection01 = new HttpConnection(ParameterKeeper.httpUrl + "/login");
        Map<String,String> request01 = new HashMap<String, String>();
        request01.put("sServeType","2");
        request01.put("sEmail",emailString);
        request01.put("sCheckData",checkData);
        connection01.sendPOST(request01);

        while(connection01.getOnWork() != 2){
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String respond = connection01.getData();
        if(respond == null){
            Log.d("LoginActivity","邮箱验证登录失败,respond为空");
            return;
        }
        switch(respond.charAt(0)){
            case '0':
                activityId = respond.substring(1,7);
                PublicDataKeeper.activityId = activityId;
                if(rememberCheckBox.isChecked()){
                    HttpConnection connection02 = new HttpConnection(ParameterKeeper.httpUrl + "/login");
                    Map<String,String> request02 = new HashMap<String, String>();
                    request02.put("sServeType","3");
                    request02.put("sActivityId",activityId);
                    connection02.sendPOST(request02);
                    while(connection02.getOnWork() != 2){
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    respond = connection02.getData();
                    if(respond == null){
                        Log.d("LoginActivity","邮箱验证自动登录失败,respond为空");
                        return;
                    }
                    switch (respond.charAt(0)) {
                        case '0':
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email",emailString);
                            editor.putString("password",respond.substring(1));
                            editor.putString("activityId",activityId);
                            editor.apply();
                            return;

                        case '1':
                            return;
                    }
                }
                Intent intent = new Intent(LoginActivity.this,TitleActivity.class);
                startActivity(intent);
                return;

            case '1':
                checkDataEditText.setText("");
                checkDataEditText.setHintTextColor(0xFFDD2E2E);
                checkDataEditText.setText("邮箱不存在，请重新获取验证码");
                return;

            case '2':
                checkDataEditText.setText("");
                checkDataEditText.setHintTextColor(0xFFDD2E2E);
                checkDataEditText.setHint("验证码错误");
                return;
        }
    }

    public void onClickGetCheckDataButton(View view){
        if(isGetCheckData == false){
            return;
        }
        EditText emailEditText = findViewById(R.id.emailEditText);
        String emailString = emailEditText.getText().toString();
        if (emailEditText == null){
            emailEditText.setHintTextColor(0xFFDD2E2E);
            emailEditText.setHint("邮箱为空");
            return;
        }
        if (!Pattern.matches(ParameterKeeper.emailRegularExpression,emailString)){
            emailEditText.setHintTextColor(0xFFDD2E2E);
            emailEditText.setHint("邮箱格式不正确");
            return;
        }

        HttpConnection connection = new HttpConnection(ParameterKeeper.httpUrl + "/login");
        Map<String,String> request = new HashMap<String,String>();
        request.put("sServeType","1");
        request.put("sEmail",emailString);
        connection.sendPOST(request);

        while(connection.getOnWork() != 2){
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String respond = connection.getData();
        if(respond == null){
            Log.d("LoginActivity","邮箱验证登录失败,respond为空");
            return;
        }
        switch (respond.charAt(0)){
            case '0':
                isGetCheckData = false;
                checkDataTimeout = 60;
                checkDataButton = findViewById(R.id.get_check_data_button);
                Log.d("LoginActivity",checkDataTimeout + "");
                Thread thread01 = new Thread(){
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(10);
                    }
                };
                thread01.start();
                return;

            case '1':
                isGetCheckData = false;
                checkDataTimeout = Integer.parseInt(respond.substring(1));
                Log.d("LoginActivity",checkDataTimeout + "");
                checkDataButton = findViewById(R.id.get_check_data_button);
                Thread thread02 = new Thread(){
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(10);
                    }
                };
                thread02.start();
        }
    }

    public void onClickForgetPasswordButton(View view){
        EditText emailEditText = findViewById(R.id.emailEditText);
        String emailString = emailEditText.getText().toString();
        setContentView(R.layout.activity_email_login_register);
        if(emailString != null){
            emailEditText = findViewById(R.id.emailEditText);
            emailEditText.setText(emailString);
        }
    }
}