package com.example.phonesockettest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;

import com.example.phonesockettest.R;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;


//添加依赖时注意，高版本Android 需要在settings.gradle中添加镜像和依赖
public class PhoneSocketService extends Service {
    private String TAG = "szp";
    private IRegister<IServerActionListener, IServerManager> server;
    private IServerManager serverManager;
    private IClient mClient;  //通过这个由服务器往客户端发送数据
    /**端口号，需要Phone端与Car端一致*/
    public static final int SOCKET_PORT = 21798;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        server = OkSocket.server(SOCKET_PORT);//创建服务
        serverManager = server.registerReceiver(iServerActionListener);//注册回调
        serverManager.listen();//开启监听
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //服务保活  ，通过状态栏通知 提升进程优先级 尽量保证服务不会被杀掉
    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_background)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.drawable.ic_launcher_background) // 设置状态栏内的小图标
                .setContentText("SocketTest") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
            // 前台服务notification适配
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                            "notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; // 设置为默认通知音
        startForeground(110, notification);
    }

    IServerActionListener iServerActionListener = new IServerActionListener() {
        @Override
        public void onServerListening(int serverPort) {
            Log.d(TAG,"服务器启动完成.正在监听端口:" + serverPort);
        }
        @Override
        public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {

            Log.d(TAG, client.getUniqueTag() + " 客户端已连接");
            mClient = client;
            client.addIOCallback(new IClientIOCallback() {
                @Override
                public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {

                    String str = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
                    if(str.equals("pause")){ //是否是心跳返回包,若是心跳包则发送应答包
                        Log.d(TAG,"收到客户端"+client.getUniqueTag()+"的心跳数据："+str);

                        client.send(new ISendable() {
                            @Override
                            public byte[] parse() {
                                byte[] body = "ack".getBytes(Charset.defaultCharset()); // 心跳响应数据
                                ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
                                bb.order(ByteOrder.BIG_ENDIAN);
                                bb.putInt(body.length);
                                bb.put(body);
                                return bb.array();
                            }
                        });
                    }else {
                        Log.d(TAG,"收到客户端"+client.getUniqueTag()+"的指令");
                    }
                }
                @Override
                public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
                    Log.d(TAG,"发送数据到客户端:"+new String(sendable.parse(),Charset.forName("utf-8")));
                }
            });

        }
        @Override
        public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
            Log.d(TAG, client.getUniqueTag() + " 客户端已断开连接");
            client.removeAllIOCallback();
        }
        @Override
        public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
            Log.d(TAG, "服务器即将关闭");
        }
        @Override
        public void onServerAlreadyShutdown(int serverPort) {
            Log.d(TAG, "服务器已经关闭,serverPort="+serverPort);
        }

    };




}
