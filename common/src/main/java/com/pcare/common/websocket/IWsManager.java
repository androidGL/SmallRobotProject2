package com.pcare.common.websocket;

import okhttp3.WebSocket;

/**
 * @Author: gl
 * @CreateDate: 2020/1/16
 * @Description: WebSocket管理接口
 */
public interface IWsManager {
    WebSocket getWebSocket();
    void startConnect();
    void stopConnect();
    boolean isConnected();
    int getCurrentStatus();
    void setCurrentStatus(int status);
    boolean send(Object s);
}
