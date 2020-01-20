package com.pcare.common.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * @Author: gl
 * @CreateDate: 2020/1/17
 * @Description:
 */
public abstract class WsStatusListener {
    public void onOpen(Response response){

    }
    public void onMessage(String text){

    }

    public void onMessage(ByteString bytes) {
    }

    public void onClosing(int code, String reason) {
    }

    public void onClosed(String reason) {
    }

    public void onFailure(Throwable t, Response response) {
    }
    public void onReconnect() {
    }


}
