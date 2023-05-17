package com.example.carsockettest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.carsockettest.util.Util;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class CarSocketService extends Service {

    private IConnectionManager mManager;
    private String TAG = "szp";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectionInfo info = new ConnectionInfo(Util.getWifiIp(this), 21798);//连接参数设置(IP,端口号)
        mManager = OkSocket.open(info);//调用OkSocket,开启这次连接的通道,拿到通道Manager
        mManager.registerReceiver(socketActionAdapter);//注册Socket行为监听器
        mManager.connect();//调用通道进行连接
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    SocketActionAdapter socketActionAdapter = new SocketActionAdapter() {
        @Override
        public void onSocketIOThreadStart(String action) {
            super.onSocketIOThreadStart(action);
        }

        @Override
        public void onSocketIOThreadShutdown(String action, Exception e) {
            super.onSocketIOThreadShutdown(action, e);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            super.onSocketDisconnection(info, action, e);
            //断开连接
            Log.d(TAG, "断开服务器连接");
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            super.onSocketConnectionSuccess(info, action);
            Log.d(TAG, "客户端连接服务器成功,开启心跳");

            //连接成功,开启心跳
            OkSocket.open(info)
                    .getPulseManager()
                    .setPulseSendable(new IPulseSendable() {
                        @Override
                        public byte[] parse() {
                            byte[] body = "pause".getBytes(Charset.defaultCharset()); // 心跳数据
                            ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
                            bb.order(ByteOrder.BIG_ENDIAN);
                            bb.putInt(body.length);
                            bb.put(body);
                            return bb.array();
                        }
                    })
                    .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            super.onSocketConnectionFailed(info, action, e);
            Log.d(TAG, "客户端连接服务器失败");
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            Log.d(TAG, "客户端读取数据回调" + str);
            if (mManager != null && str.equals("ack")) {//是否是心跳返回包,需要解析服务器返回的数据才可知道
                Log.d(TAG, "客户端喂狗");//喂狗操作
                mManager.getPulseManager().feed();
            }
            super.onSocketReadResponse(info, action, data);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            Log.d(TAG, "客户端发送数据回调");
            super.onSocketWriteResponse(info, action, data);
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            Log.d(TAG, "客户端发送心跳包");
            super.onPulseSend(info, data);
        }
    };
}
