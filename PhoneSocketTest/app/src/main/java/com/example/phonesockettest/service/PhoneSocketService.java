package com.example.phonesockettest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;


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

import java.io.IOException;
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
    private MediaProjectionManager mMediaProjectionManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        server = OkSocket.server(SOCKET_PORT);//创建服务
        serverManager = server.registerReceiver(iServerActionListener);//注册回调
        serverManager.listen();//开启监听
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int resultCode = intent.getIntExtra("code", -1);
        Intent resultData = intent.getParcelableExtra("data");
        startProject(resultCode, resultData);
        return super.onStartCommand(intent, flags, startId);

    }
    // 录屏开始后进行编码推流
    private void startProject(int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            return;
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


//    public void startEncode() {
//        //声明MediaFormat，创建视频格式。
//        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, width, height);
//        //描述视频格式的内容的颜色格式
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        //比特率（比特/秒）
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
//        //帧率
//        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
//        //I帧的频率
//        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
//        try {
//            //创建编码MediaCodec 类型是video/hevc
//            mediaCodec = MediaCodec.createEncoderByType(enCodeType);
//            //配置编码器
//            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//            //创建一个目的surface来存放输入数据
//            Surface surface = mediaCodec.createInputSurface();
//            //获取屏幕流
//            mediaProjection.createVirtualDisplay("screen", width, height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
//                    , surface, null, null);
//        } catch (IOException e) {
//            Log.d(TAG,"initEncode IOException");
//            e.printStackTrace();
//        }
//        //启动子线程
//        this.start();
//    }
//
//    @Override
//    public void run() {
//        //编解码器立即进入刷新子状态
//        mediaCodec.start();
//        //缓存区的元数据
//        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//        //子线程需要一直运行，进行编码推流，所以要一直循环
//        while (play) {
//            //查询编码输出
//            int outPutBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, timeOut);
//            if (outPutBufferId >= 0) {
//                //获取编码之后的数据输出流队列
//                ByteBuffer byteBuffer = mediaCodec.getOutputBuffer(outPutBufferId);
//                //添加上vps,sps,pps
//                reEncode(byteBuffer, bufferInfo);
//                //处理完成，释放ByteBuffer数据
//                mediaCodec.releaseOutputBuffer(outPutBufferId, false);
//            }
//        }
//    }
//
//    private void reEncode(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
//        //偏移4 00 00 00 01为分隔符需要跳过
//        int offSet = 4;
//        if (byteBuffer.get(2) == 0x01) {
//            offSet = 3;
//        }
//        //计算出当前帧的类型
//        int type = (byteBuffer.get(offSet) & 0x7E) >> 1;
//        if (type == NAL_VPS) {
//            //保存vps sps pps信息
//            vps_pps_sps = new byte[bufferInfo.size];
//            byteBuffer.get(vps_pps_sps);
//        } else if (type == NAL_I) {
//            //将保存的vps sps pps添加到I帧前
//            final byte[] bytes = new byte[bufferInfo.size];
//            byteBuffer.get(bytes);
//            byte[] newBytes = new byte[vps_pps_sps.length + bytes.length];
//            System.arraycopy(vps_pps_sps, 0, newBytes, 0, vps_pps_sps.length);
//            System.arraycopy(bytes, 0, newBytes, vps_pps_sps.length, bytes.length);
//            //将重新编码好的数据发送出去
//            socketService.sendData(newBytes);
//        } else {
//            //B帧 P帧 直接发送
//            byte[] bytes = new byte[bufferInfo.size];
//            byteBuffer.get(bytes);
//            socketService.sendData(bytes);
//        }
//    }
//
//    public void stopEncode() {
//        play = false;
//    }

}
