package com.example.phonesockettest;

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

    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
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
//                if (netI.getDisplayName().equals("wlan0") || netI.getDisplayName().equals("eth0")){}
//                Log.i("szp","开始获取当前设备的ip: "+getIpAddressString());
                Log.i("szp","获取当前连接的WiFi的IP："+getWifiIp());
//                getWifiApState();
//                printHotIp();
            }
        });
        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("szp","开始发送信息给另一个设备");
            }
        });
    }



//    public int getWifiApState() {
//        try {
//            Method method = wifiManager.getClass().getMethod("getWifiApState");
//            WifiInfo info = wifiManager.getConnectionInfo();
//            int i = (Integer) method.invoke(wifiManager);
//            Log.i("szp","wifi state: " + i+"wifiIP: "+getCorrectIPAddress(info.getIpAddress()));
//            return i;
//        } catch (Exception e) {
//            Log.e("szp","Cannot get WiFi AP state" + e);
//            return WIFI_AP_STATE_FAILED;
//        }
//    }

    //不知Android12 以上的版本 获取不到
//    private ArrayList<String> getConnectedHotIP() {
//        ArrayList<String> connectedIP = new ArrayList<String>();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(
//                    "/proc/net/arp"));
//            Log.i("szp","try: ");
//            String line;
//            while ((line = br.readLine()) != null) {
//                Log.i("szp","ip1: "+line);
//                String[] splitted = line.split(" +");
//                if (splitted != null && splitted.length == 4) {
//                    String ip = splitted[0];
//                    Log.i("szp","i2p: "+ip);
//                    connectedIP.add(ip);
//                }
//            }
//        } catch (Exception e) {
//            Log.i("szp","Exception: "+e);
//            e.printStackTrace();
//        }
//        return connectedIP;
//    }
    //输出连接到当前设备的IP地址
//    public void printHotIp() {
//
//        ArrayList<String> connectedIP = getConnectedHotIP();
//        StringBuilder resultList = new StringBuilder();
//        for (String ip : connectedIP) {
//            resultList.append(ip);
//            resultList.append("\n");
//        }
//        tvIp.setText(resultList);
//        Log.d("szp","resultList="+resultList);
//    }


    //只能获取本机的 ip
//    public static String getIpAddressString() {
//        try {
//            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
//                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
//                NetworkInterface netI = enNetI.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = netI
//                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public String getWifiIp() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return null;
        }
        if (!wifiManager.isWifiEnabled()) {
            return null;
        }
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        DhcpInfo dhcpinfo = wifiManager.getDhcpInfo();
        String serverAddress = getCorrectIPAddress(dhcpinfo.serverAddress);

        Log.e("ww","Wifi-Ip:" + serverAddress);

        return serverAddress;
    }
    /**
     * 将获取的int转为真正的ip地址
     **/
    private static String getCorrectIPAddress(int iPAddress) {
        StringBuilder sb = new StringBuilder();
        sb.append(iPAddress & 0xFF).append(".");
        sb.append((iPAddress >> 8) & 0xFF).append(".");
        sb.append((iPAddress >> 16) & 0xFF).append(".");
        sb.append((iPAddress >> 24) & 0xFF);
        return sb.toString();
    }

}