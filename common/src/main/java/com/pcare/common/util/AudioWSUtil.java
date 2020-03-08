package com.pcare.common.util;

import android.util.SparseArray;

import com.pcare.common.net.Api;
import com.pcare.common.websocket.WsManager;
import com.pcare.common.websocket.WsStatusListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.ByteString;

/**
 * @Author: gl
 * @CreateDate: 2020/1/20
 * @Description:
 */
public class AudioWSUtil {
    private boolean tag;//是否需要讲话
    private int position;
    private SparseArray<String> array = new SparseArray<>();
    private AudioTrackUtil audioTrackUtil = new AudioTrackUtil();
    private WsManager manager;
    private static AudioWSUtil audioWSUtil;
    private JSONObject object = new JSONObject();

    public static AudioWSUtil getInstance(){
        if(null == audioWSUtil){
            synchronized (AudioWSUtil.class){
                if(null == audioWSUtil)
                    audioWSUtil = new AudioWSUtil();
            }
        }
        return audioWSUtil;
    }
    private AudioTrackUtil.OnState stateListener = new AudioTrackUtil.OnState() {
        @Override
        public void onStateChanged(AudioTrackUtil.WindState currentState) {
            if(currentState == AudioTrackUtil.WindState.STOP_PLAY){
                if(position<=array.size()-1) {
                    audioTrackUtil.startPlay(array.get(position));
                    position++;
                    return;
                }
                tag = true;
            }else if(currentState == AudioTrackUtil.WindState.PLAYING){
                tag = false;
            }
        }
    };

    private WsStatusListener listener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            super.onOpen(response);
            LogUtil.i("onOpen"+response.toString());
            audioTrackUtil.setOnStateListener(stateListener);
            manager.send(object.toString());
        }

        @Override
        public void onMessage(String text) {
            super.onMessage(text);
            array.append(array.size(),text);
            if(tag && position<=array.size()-1) {
                audioTrackUtil.startPlay(array.get(position));
                position++;
            }
        }

        @Override
        public void onMessage(ByteString bytes) {
            super.onMessage(bytes);
            LogUtil.i("onMessage2"+bytes.base64());
        }

        @Override
        public void onClosing(int code, String reason) {
            super.onClosing(code, reason);
            LogUtil.i("onClosing"+reason);
        }

        @Override
        public void onClosed(String reason) {
            super.onClosed(reason);
            LogUtil.i("onClosed"+reason);
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            super.onFailure(t, response);
            t.printStackTrace();
            LogUtil.i("onFailure");
        }

        @Override
        public void onReconnect() {
            super.onReconnect();
            LogUtil.i("onReconnect");
        }
    };
    public void speaking(String text){
        tag = true;
        array.clear();
        position = 0;
        try {
            object.putOpt("text",text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(null != manager && manager.isConnected()){
            manager.send(object.toString());
            return;
        }

        manager = new WsManager.Builder()
                .client(new OkHttpClient().newBuilder()
//                        .pingInterval(60, TimeUnit.SECONDS)//设置的60秒后断开连接
                        .retryOnConnectionFailure(true)
                        .build())
                .listener(listener)
                .url(Api.AUDIOURL+"/tts/")
                .build();
        manager.startConnect();

    }

    public void stopSpeaking() {
        //停止讲话
        audioTrackUtil.stopPlay();
        position = 999;
    }
    public void destory(){
        stopSpeaking();
        manager.stopConnect();
    }
}
