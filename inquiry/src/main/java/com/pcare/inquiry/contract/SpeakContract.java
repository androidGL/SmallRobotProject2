package com.pcare.inquiry.contract;

import androidx.recyclerview.widget.RecyclerView;

import com.pcare.common.base.IView;

import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.ResponseBody;


/**
 * @Author: gl
 * @CreateDate: 2019/10/25
 * @Description:
 */
public interface SpeakContract {
    interface Model{
        void ask(String question, DisposableSingleObserver<ResponseBody> observer);
        void speak(String text, DisposableSingleObserver<ResponseBody> observer);
    }
    interface View extends IView {
        void showToast(String toast);
        void setAdapter(RecyclerView.Adapter adapter);

        void notifyAdapter();
    }
    interface Presenter{
        void useClickType();//使用UI交互的方式
        void useSpeakType();//使用语音交互
        void useAskType();//问问题形式

//        void speakListener(MotionEvent event);
        void startSpeak();
        void stopSpeak();
        void setSpeakStr(String s);
        void destoty();//回收内存

        void sendMsg(String msg);

    }
}
