package com.pcare.inquiry.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.pcare.common.base.BasePresenter;
import com.pcare.common.util.TTSUtil;
import com.pcare.inquiry.R;
import com.pcare.inquiry.adapter.QuestionSelectAdapter;
import com.pcare.inquiry.adapter.QuestionSpeakAdapter;
import com.pcare.inquiry.contract.SpeakContract;
import com.pcare.inquiry.entity.MsgEntity;
import com.pcare.inquiry.entity.QuestionEnity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/10/25
 * @Description:
 */
public class SpeakPresenter extends BasePresenter<SpeakContract.View> implements SpeakContract.Presenter {

    private final String TAG = "MajorSpeakActivity";
    private List<String> queationTitleList = new ArrayList<>();
    private List<MsgEntity> msgEntityList = new ArrayList<>();
    private  RecyclerView.Adapter selectAdapter;
    private RecognizerListener mRecoListener;
    private String speechText;
    private Activity activity;
    public SpeakPresenter(SpeakContract.View view) {
        super(view);
        activity = (Activity) view;
        init();
    }

    @Override
    public void useClickType() {
        List<QuestionEnity> questionList = new ArrayList<QuestionEnity>();
        String[] list_answer =activity .getResources().getStringArray(R.array.list_answer);
        for (int i=0;i<queationTitleList.size();i++){
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(list_answer[i].split("，")));
            questionList.add(new QuestionEnity(queationTitleList.get(i),list,true));
        }
        setSpeakStr(queationTitleList.get(0));
        selectAdapter = new QuestionSelectAdapter(activity, questionList);
        getView().setAdapter(selectAdapter);
    }

    @Override
    public void useSpeakType() {
        int num = (int) Math.random()*(queationTitleList.size()-1);
        setSpeakStr(queationTitleList.get(num));
        msgEntityList.add(new MsgEntity(queationTitleList.get(num),2));
        selectAdapter = new QuestionSpeakAdapter(activity,msgEntityList);
        getView().setAdapter(selectAdapter);
    }

    @Override
    public void startSpeak() {
        speechText = "";
        TTSUtil.getInstance(activity.getApplicationContext()).startSpeech(mRecoListener);
    }

    @Override
    public void stopSpeak() {
        TTSUtil.getInstance(activity.getApplicationContext()).stopSpeech();
    }


    @Override
    public void setSpeakStr(String s) {
        TTSUtil.getInstance(activity.getApplicationContext()).speaking(s);
    }

    @Override
    public void destoty() {
//        if(null != speechToTextUtil) speechToTextUtil.destotySpeechToSpeak();
//        TextToSpeechUtil.destoty();
    }

    private  void init(){
        queationTitleList.addAll(Arrays.asList(activity.getResources().getStringArray(R.array.list_question)));
        // 听写监听器
        mRecoListener = new RecognizerListener() {
            //isLast等于true 时会话结束。
            public void onResult(RecognizerResult results, boolean isLast) {
                Log.e(TAG, results.getResultString());
                speechText = speechText + TTSUtil.parseIatResult(results.getResultString());
                showTip("结果1" + speechText);
                if(isLast) {
                    showTip("结果2" + speechText);
                    msgEntityList.add(new MsgEntity(speechText, 1));
                    selectAdapter.notifyDataSetChanged();
                    if(null != queationTitleList && queationTitleList.size()>0) {
                        msgEntityList.add(new MsgEntity(queationTitleList.get(0), 2));
                        selectAdapter.notifyDataSetChanged();
                        TTSUtil.getInstance(activity.getApplicationContext()).speaking(queationTitleList.get(0));
                        queationTitleList.remove(0);
                    }
                }
            }

            // 会话发生错误回调接口
            public void onError(SpeechError error) {
                showTip(error.getPlainDescription(true)) ;
                // 获取错误码描述
                Log. e(TAG, "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
            }

            // 开始录音
            public void onBeginOfSpeech() {
                showTip(" 开始录音 ");
            }

            //volume 音量值0~30， data音频数据
            public void onVolumeChanged(int volume, byte[] data) {
                showTip(" 声音改变了 ");
            }

            // 结束录音
            public void onEndOfSpeech() {
            }

            // 扩展用接口
            public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
            }
        };
    }
    private void showTip(String info){
        Log.i(TAG,info);
    }
}
