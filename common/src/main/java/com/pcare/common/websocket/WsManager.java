package com.pcare.common.websocket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.pcare.common.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @Author: gl
 * @CreateDate: 2020/1/16
 * @Description:
 */
public class WsManager implements IWsManager {
    private final int RECONNECT_STEP = 10000;
    private String mUrl;
    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private int mCurrentStatus ;
    private int reconnectCount = 3;
    private WsStatusListener statusListener;
    private volatile boolean heartTag = true;
    private final String HEARTMSG = "{\"text\":\"heartbeat_info\"}";

    private Handler heartHandler = new Handler();
    private Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            if(heartTag) {
                send(HEARTMSG);//发送一个心跳包给服务器
                heartHandler.postDelayed(this, RECONNECT_STEP);
            }
        }
    };

    private void sendHeart(){
        if(null != heartHandler) {
            heartHandler.removeCallbacks(heartRunnable);
            heartHandler.post(heartRunnable);
        }
    }

    private WebSocketListener mWebSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mWebSocket = webSocket;
            setCurrentStatus(WsStatus.CONNECTING);
//            cancelReconnect();
            if (null != statusListener) {
                statusListener.onOpen(response);
            }

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            setCurrentStatus(WsStatus.CONNECTED);

            LogUtil.i("onMessage"+text);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonObject = jsonObject.optJSONObject("audio");
            if (null != statusListener)
                statusListener.onMessage(jsonObject.optString("data"));
            //最后一条消息，关闭心跳包
            if("2".equals(jsonObject.optString("status"))){
                heartTag = false;
                heartHandler.removeCallbacks(heartRunnable);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            if (null != statusListener)
                statusListener.onMessage(bytes);

        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            heartTag = false;
            heartHandler.removeCallbacks(heartRunnable);
            setCurrentStatus(WsStatus.CLOSECONNECT);
            if (null != statusListener)
                statusListener.onClosing(code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            heartTag = false;
            heartHandler.removeCallbacks(heartRunnable);
            setCurrentStatus(WsStatus.CLOSECONNECT);
            if (null != statusListener)
                statusListener.onClosed(reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
//            reConnect();
            setCurrentStatus(WsStatus.CLOSECONNECT);
            if(null != heartHandler) {
                heartTag = false;
                heartHandler.removeCallbacks(heartRunnable);
            }
            if (null != statusListener)
                statusListener.onFailure(t, response);
        }
    };

    public WsManager(Builder builder) {
        this.mOkHttpClient = builder.client;
        this.mUrl = builder.url;
        this.statusListener = builder.listener;
    }

//    private void reConnect() {
//        heartHandler.removeCallbacks(heartRunnable);
//        mWebSocket.cancel();//取消掉以前的长连接
//        buildConnect();//创建一个新的连接
//        if (statusListener != null) {
//            statusListener.onReconnect();
//        }
//        setCurrentStatus(WsStatus.RECONNECT);
//        heartHandler.postDelayed(heartRunnable, RECONNECT_STEP);
//        reconnectCount++;
//    }


    /**
     * 初始化WebSocket
     */
    private void initWebSocket() {
        if (null == mOkHttpClient) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//返回连接失败时重试
                    .build();
        }
        if (null == mRequest) {
            mRequest = new Request.Builder().url(mUrl).build();
        }
        mOkHttpClient.dispatcher().cancelAll();
        synchronized (WsManager.class) {
            mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
        }
    }

    private synchronized void buildConnect() {
        //TODO:判断网络是否连接
        if (!isConnected() && getCurrentStatus() != WsStatus.CONNECTING) {
            setCurrentStatus(WsStatus.CLOSECONNECT);
            initWebSocket();
        }
    }

    private void closeConnect() {
        heartHandler.removeCallbacks(heartRunnable);
        heartRunnable = null;
        heartHandler = null;
        if (getCurrentStatus() == WsStatus.CLOSECONNECT) {
            return;
        }
        if (null != mOkHttpClient) {
            //取消掉之前的所有请求
            mOkHttpClient.dispatcher().cancelAll();
        }
        mWebSocket.close(WsStatus.CLOSE_NORMAL, WsStatus.CLOSE_NORMAL_REASON);
        statusListener.onClosed("stopConnect");
        setCurrentStatus(WsStatus.CLOSECONNECT);
    }

    @Override
    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    @Override
    public void startConnect() {
        LogUtil.i("startConnect");
        buildConnect();
    }


    @Override
    public void stopConnect() {
        closeConnect();
    }

    @Override
    public boolean isConnected() {
        return mCurrentStatus == WsStatus.CONNECTED || mCurrentStatus==WsStatus.CONNECTING;
    }

    @Override
    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    @Override
    public void setCurrentStatus(int status) {
        this.mCurrentStatus = status;
    }

    @Override
    public boolean send(Object o) {
        boolean isSend = false;
        if(!o.toString().equals(HEARTMSG)){
            heartTag = true;
            sendHeart();
        }
        LogUtil.i("getCurrentStatus():"+getCurrentStatus()+"   Object:"+o.toString());

        if(null != mWebSocket && isConnected()){
            if(o instanceof String){
                isSend = mWebSocket.send((String)o);
            }else if(o instanceof ByteString){
                isSend = mWebSocket.send((ByteString) o);
            }
            LogUtil.i("isSend:"+isSend);
            //发送消息失败，重新连接
            if(!isSend && null != heartHandler){
                heartTag = false;
                heartHandler.removeCallbacks(heartRunnable);
            }
        }
        return isSend;
    }

    /**
     * 记录当前Socket的连接状态，CLOSE_NORMAL必须为1000
     */
    private static final class WsStatus {
        final static int CONNECTING = 1;
        final static int CONNECTED = 2;
        final static int RECONNECT = 3;
        final static int CLOSECONNECT = -1;
        final static int CLOSE_NORMAL = 1000;
        final static String CLOSE_NORMAL_REASON = "normalClose";
    }


    public static final class Builder {
        private String url;
        private OkHttpClient client;
        private WsStatusListener listener;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder listener(WsStatusListener listener) {
            this.listener = listener;
            return this;
        }

        public WsManager build() {
            return new WsManager(this);
        }

    }
}
