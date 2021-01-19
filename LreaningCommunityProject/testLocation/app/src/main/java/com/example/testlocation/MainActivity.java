package com.example.testlocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//    LocationManager locationManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        // 创建一个Criteria对象
//        Criteria criteria = new Criteria();
//        // 设置粗略精确度
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        // 设置是否需要返回海拔信息
//        criteria.setAltitudeRequired(false);
//        // 设置是否需要返回方位信息
//        criteria.setBearingRequired(false);
//        // 设置是否允许付费服务
//        criteria.setCostAllowed(true);
//        // 设置电量消耗等级
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);
//        // 设置是否需要返回速度信息
//        criteria.setSpeedRequired(false);
//        // 根据设置的Criteria对象，获取最符合此标准的provider对象 41
//        String currentProvider = locationManager
//                .getBestProvider(criteria, true);
//        Log.d("Location", "currentProvider: " + currentProvider);
//        // 根据当前provider对象获取最后一次位置信息 44
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
//        // 如果位置信息为null，则请求更新位置信息 46
//        if (currentLocation == null ) {
//            locationManager.requestLocationUpdates(currentProvider, 0 , 0 ,
//                    locationListener);
//        }
//        // 直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度 50
//        // 如果位置信息为null，则请求更新位置信息 46
//        if (currentLocation == null ) {
//            locationManager.requestLocationUpdates(currentProvider, 0 , 0 ,
//                    locationListener);
//        }
//        // 直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度 50
//        // 每隔10秒获取一次位置信息 51
//        while ( true ) {
//            currentLocation = locationManager
//                    .getLastKnownLocation(currentProvider);
//            if (currentLocation != null ) {
//                Log.d( "Location" , "Latitude: " + currentLocation.getLatitude());
//                Log.d( "Location" , "location: " + currentLocation.getLongitude());
//                break ;
//            } else {
//                Log.d( "Location" , "Latitude: " + 0 );
//                Log.d( "Location" , "location: " + 0 );
//            }
//            try {
//                Thread.sleep( 10000 );
//            } catch (InterruptedException e) {
//                Log.e( "Location" , e.getMessage());
//            }
//        }
//        // 解析地址并显示 69
//        Geocoder geoCoder = new Geocoder( this );
//        int latitude = ( int ) currentLocation.getLatitude();
//        int longitude = ( int ) currentLocation.getLongitude();
//        List<Address> list = null;
//        try {
//            list = geoCoder.getFromLocation(latitude, longitude,
//                    2 );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for ( int i = 0 ; i < list.size(); i++) {
//            Address address = list.get(i);
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // 创建位置监听器 85
//    private LocationListener locationListener = new LocationListener() {
//        // 位置发生改变时调用 87
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.d( "Location" , "onLocationChanged" );
//            Log.d( "Location" ,
//                    "onLocationChanged Latitude" + location.getLatitude());
//            Log.d( "Location" ,
//                    "onLocationChanged location" + location.getLongitude());
//        }
//
//        // provider失效时调用 95
//        @Override
//        public void onProviderDisabled(String provider) {
//            Log.d( "Location" , "onProviderDisabled" );
//        }
//
//        // provider启用时调用101
//        @Override
//        public void onProviderEnabled(String provider) {
//            Log.d( "Location" , "onProviderEnabled" );
//        }
//
//        // 状态改变时调用107
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.d( "Location" , "onStatusChanged" );
//        }
//    };
private LocationManager lm;
    private TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7;
    private Button btn1, btn2;
    private List<GpsSatellite> numSatlliteList = new ArrayList<>();  // 卫星信号
    private Location location;

    private LocationListener locationListener= new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 当gps定位信息发生改变时,更新定位
            updateShow(location);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // 当gpsLocationProvider可用时,更新定位
            updateShow(null);
        }
        @Override
        public void onProviderDisabled(String s) {
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.start);
        btn2 = (Button)findViewById(R.id.stop);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_4 = (TextView) findViewById(R.id.tv_4);
        tv_5 = (TextView) findViewById(R.id.tv_5);
        tv_6 = (TextView) findViewById(R.id.tv_6);
        tv_7 = (TextView) findViewById(R.id.tv_7);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    //点击事件
    public void onClick(View v) {
        if (v == btn1) {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d("MainActivity", "gps已关闭,请手动打开再试!");
            } else {
                Log.d("MainActivity", "gps定位中...");
            }
            // new 对象设置精度信息
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            String provider = lm.getBestProvider(criteria, true);
            Log.d("MainActivity",provider == null ? "null" : provider);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity","1权限获取失败要求获取权限");
                return;
            }
            location = lm.getLastKnownLocation(provider);
            updateShow(location);
            //设置动态监听,时间为两秒
            lm.requestLocationUpdates(provider,2000,0,locationListener);
            // 设置动态回调函数.statusListener是回调函数
            boolean b = lm.addGpsStatusListener(statusListener);// 注册状态信息回调.
        }else if (v == btn2){
            finish();
            // super.onDestroy();
            // super.onStop();
            // lm.removeUpdates(locationListener);
        }
    }

    // 卫星状态监听器
    GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity","2权限获取失败要求获取权限");
                return;
            }
            GpsStatus status = lm.getGpsStatus(null);
            updateGpsStatus(i,status);
        }
    };

    // 获取卫星数

    private void updateGpsStatus(int event, GpsStatus status){
        if (status == null){
        }else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
            // 获取最大的卫星数(这只是一个预设的值)
            int maxStaellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatlliteList.clear();
            int count = 0;
            while(it.hasNext() && count <= maxStaellites){
                GpsSatellite s = it.next();
                numSatlliteList.add(s);
                count++;
            }
        }else if(event == GpsStatus.GPS_EVENT_STARTED){
            // 定位开始
        }else if (event == GpsStatus.GPS_EVENT_STOPPED){
            // 定位结束
        }
    }

    // 定义更新显示的方法
    private void updateShow(Location location){
        if (location!=null){
            tv_1.setText("经度:"+location.getLongitude());
            tv_2.setText("维度:"+location.getLatitude());
            tv_3.setText("海拔:"+location.getAltitude());
            tv_4.setText("速度:"+location.getSpeed());
            tv_5.setText("方位:"+location.getBearing());
            // tv_6.setText("时间:"+Timetool.SdateAllTime_mc());
            tv_7.setText("卫星数:"+numSatlliteList.size());
        }
        else {
            tv_1.setText("地理位置位置或正在获取地理位置中...");
        }
    }

    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? true : false;
    }

    // 打开设置界面让用户自己设置
    private void openGps(){
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivityForResult(intent,0);
    }
}