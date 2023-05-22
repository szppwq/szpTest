package com.example.carsockettest.bean;

public class SocketConnectStatusEvent {
    //Socket连接的两种状态 默认 0 断开  1 已连接

    public int connectStatus = 0;

    public SocketConnectStatusEvent(int status){
        this.connectStatus = status;
    }
}
