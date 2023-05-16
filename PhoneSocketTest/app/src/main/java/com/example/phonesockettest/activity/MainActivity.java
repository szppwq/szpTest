package com.example.phonesockettest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonesockettest.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private TextView tvBeginConnect, tvGetIp, tvSendMessage, tvBeginScreen;
    private TextView tvIp, tvMessage, tvMessageResult, tvConnectResult;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListener();
    }

    public void init() {
        tvBeginConnect = findViewById(R.id.begin_connect);
        tvGetIp = findViewById(R.id.tv_get_ip);
        tvSendMessage = findViewById(R.id.send_message);
        tvBeginScreen = findViewById(R.id.begin_screen);

        tvIp = findViewById(R.id.ip_info);
        tvMessage = findViewById(R.id.message_info);
        tvMessageResult = findViewById(R.id.get_message_resul);
        tvConnectResult = findViewById(R.id.connect_result);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 最后的请求码是对应回调方法的请求码
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            Toast.makeText(this, "你已经有权限了", Toast.LENGTH_LONG).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 最后的请求码是对应回调方法的请求码
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
        } else {
            Toast.makeText(this, "你已经有权限了", Toast.LENGTH_LONG).show();
        }


    }

    public void initListener(){
        tvBeginConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("szp","开始连接另一个设备");
            }
        });
        tvBeginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("szp","开始投屏");
            }
        });
        tvGetIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("szp","获取当前连接的WiFi的IP：");

            }
        });
        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("szp","开始发送信息给另一个设备");
            }
        });
    }


}