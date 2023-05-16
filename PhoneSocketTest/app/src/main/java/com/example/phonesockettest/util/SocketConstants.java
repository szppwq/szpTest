package com.example.phonesockettest.util;

/**
 * 用于Socket应用层传输的数据帧封装
 * 格式:帧头-数据类型-数据长度-数据内容-帧尾
 * 帧头:6BEE8CA0 (固定的数据，十六进制两位占一个字节，一共四个字节)
 * 帧尾:8D04A8B2 (固定的数据，十六进制两位占一个字节，一共四个字节)
 * 数据类型:一共四个字节，由实际的类型确定一个值
 * 数据长度:一共四个字节，有实际的数据长度确定
 * 数据内容:实际的类容转化为byte
 * 例：6BEE8CA0AAAAAAAABBBBBBBBCCCCCCCCCCCCCCCCCCCCCCC8D04A8B2
 */
public class SocketConstants {

    /**心跳帧:定值*/
    public static final int frameHeartPhone = 0xEFEFEFEF;
    /**心跳帧应答:定值*/
    public static final int frameHeartCar = 0xFEFEFEFE;

    /**帧头:定值*/
    public static final int frameHeader = 0x6BEE8CA0;
    /**帧尾:定值*/
    public static final int frameEnd = 0x8D04A8B2;

    /*********************Socket数据类型Phone到Car****************************/
    /**Phone给Car通讯录*/
    public static final int Socket_Phone_Contact = 0xA0000001;
    /**Phone给Car音乐信息数据*/
    public static final int Socket_Phone_Local_Music = 0xA0000002;
    /**Phone给Car开始投屏时，从Phone给Car一个通知*/
    public static final int Socket_Phone_Start_Projection = 0xA0000003;
    /**Phone给Car结束投屏时，从Phone给Car一个通知*/
    public static final int Socket_Phone_Stop_Projection = 0xA0000004;
    /**Phone给Car投屏时的视频帧数据*/
    public static final int Socket_Phone_Projection_Data = 0xA0000005;
    /**Phone给Car通话记录*/
    public static final int Socket_Phone_CallLog = 0xA0000006;
    /**Phone给Car语音识别的结果*/
    public static final int Socket_Phone_Voice_Result = 0xA0000007;

    /*********************Socket数据类型Car到Phone，十六进制以B开头****************************/
    /**Car端通知Phone端打电话*/
    public static final int Socket_Car_Call = 0xB0000001;
    /**Car端通知Phone端播放音乐*/
    public static final int Socket_Car_Music = 0xB0000002;
    /**Car端通知Phone端音乐开始播放*/
    public static final int Socket_Car_Music_Play = 0xB0000003;
    /**Car端通知Phone端音乐暂停播放*/
    public static final int Socket_Car_Music_Pause = 0xB0000004;
    /**Car端通知Phone端可以开始进行语音识别*/
    public static final int Socket_Car_Voice_Start = 0xB0000005;




}
