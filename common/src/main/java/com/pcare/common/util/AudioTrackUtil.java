package com.pcare.common.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;


/**
 * @Author: gl
 * @CreateDate: 2020/1/15
 * @Description: 自主研发的语音合成工具类
 */
public class AudioTrackUtil {
    private static final int AUDIO_FREQUENCY = 22050;
    private static final int PLAY_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private volatile WindState state = WindState.IDLE; // 当前状态
    private OnState onStateListener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    public synchronized void startPlay(String s) {
        if (!isIdle() || TextUtils.isEmpty(s)) {
            return;
        }

        new AudioTrackPlayThread(Base64.decode(s,Base64.DEFAULT)).start();
    }
    public void setOnStateListener(OnState listener){
        this.onStateListener = listener;
    }



    public synchronized void stopPlay() {
        if (!state.equals(WindState.PLAYING)) {
            return;
        }
        state = WindState.STOP_PLAY;
    }

    public synchronized boolean isIdle() {
        return WindState.IDLE.equals(state);
    }


    private class AudioTrackPlayThread extends Thread {
        AudioTrack track;
        int bufferSize = 10240;
        byte[] mText;

        public AudioTrackPlayThread(byte[] text) {
            setPriority(Thread.MAX_PRIORITY);
            this.mText = text;
            track = new AudioTrack(AudioManager.STREAM_MUSIC,// 指定流的类型
                    AUDIO_FREQUENCY,// 设置音频数据的採样率22050
                    PLAY_CHANNEL_CONFIG,//设置输出声道CHANNEL_OUT_STEREO为双声道立体声，CHANNEL_OUT_MONO为单声道
                    AUDIO_ENCODING,//设置音频数据块是8位还是16位。这里设置为16位。
                    bufferSize,//缓冲区大小
                    AudioTrack.MODE_STREAM);//设置模式类型，在这里设置为流类型，第二种MODE_STATIC貌似没有什么效果
        }

        @Override
        public void run() {
            super.run();
            state = WindState.PLAYING;
            notifyState(state);
            try {
                track.play();
                track.write(mText, 0, mText.length);
                track.stop();
                track.release();
            } catch (Exception e) {
                e.printStackTrace();
                notifyState(WindState.ERROR);
            }
            state = WindState.STOP_PLAY;
            notifyState(state);
            state = WindState.IDLE;
            notifyState(state);
        }

        private synchronized void notifyState(final WindState currentState) {
            if (null != onStateListener) {
                mainHandler.post(() -> onStateListener.onStateChanged(currentState));
            }
        }

    }

    public interface OnState {
        void onStateChanged(WindState currentState);
    }

    /**
     * 表示当前状态
     */
    public enum WindState {
        ERROR,
        IDLE,
        PLAYING,
        STOP_PLAY
    }

}
