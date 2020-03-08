package com.pcare.inquiry.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.pcare.common.base.BasePresenter;
import com.pcare.common.util.AudioTrackUtil;
import com.pcare.common.util.AudioWSUtil;
import com.pcare.common.util.LogUtil;
import com.pcare.common.util.TTSUtil;
import com.pcare.inquiry.R;
import com.pcare.inquiry.adapter.QuestionSelectAdapter;
import com.pcare.inquiry.adapter.QuestionSpeakAdapter;
import com.pcare.inquiry.contract.SpeakContract;
import com.pcare.inquiry.entity.MsgEntity;
import com.pcare.inquiry.entity.QuestionEnity;
import com.pcare.inquiry.model.SpeakModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.ResponseBody;

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
    private SpeakContract.Model model;
    DisposableSingleObserver<ResponseBody> askObserver,speakObserver;
    private int type = 0;//0表示select，1表示speak，2表示ask
    private AudioTrackUtil audioTrackUtil = new AudioTrackUtil();

    public SpeakPresenter(SpeakContract.View view) {
        super(view);
        activity = (Activity) view;
        model = new SpeakModel();
    }

    @Override
    public void useClickType() {
        type = 0;
        queationTitleList.addAll(Arrays.asList(activity.getResources().getStringArray(R.array.list_question)));
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
        type = 1;
        queationTitleList.addAll(Arrays.asList(activity.getResources().getStringArray(R.array.list_question)));
        initRecoListener();
        setSpeakStr(queationTitleList.get(0));
        msgEntityList.add(new MsgEntity(queationTitleList.get(0),2));
        queationTitleList.remove(0);
        selectAdapter = new QuestionSpeakAdapter(activity,msgEntityList);
        getView().setAdapter(selectAdapter);
    }

    @Override
    public void useAskType() {
        type = 2;
        initRecoListener();
        setSpeakStr("请问有什么需要帮助您的？");
        msgEntityList.add(new MsgEntity("请问有什么需要帮助您的？",2));
        selectAdapter = new QuestionSpeakAdapter(activity,msgEntityList);
        getView().setAdapter(selectAdapter);
    }

    /**
     * 开始监听人说话
     */
    @Override
    public void startSpeak() {
        stopSpeaking();
        speechText = "";
        TTSUtil.getInstance(activity.getApplicationContext()).startSpeech(mRecoListener);
    }

    /**
     * 停止监听人说话
     */
    @Override
    public void stopSpeak() {
        TTSUtil.getInstance(activity.getApplicationContext()).stopSpeech();
    }

    /**
     * 停止语音播报
     */
    private void stopSpeaking(){
        AudioWSUtil.getInstance().stopSpeaking();
//        TTSUtil.getInstance(activity.getApplicationContext()).stopSpeaking();
    }

    /**
     * 开始语音播报
     * @param s
     */
    @Override
    public void setSpeakStr(String s) {
        AudioWSUtil.getInstance().speaking(s);
//        speakObserver = new DisposableSingleObserver<ResponseBody>() {
//            @Override
//            public void onSuccess(ResponseBody value) {
//                JSONObject jsonObject;
//                try {
//                    String s = value.string();
//                    jsonObject = new JSONObject(s);
//                    audioTrackUtil.startPlay(jsonObject.optString("data"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//        };
//        addDisposable(speakObserver);
//        model.speak(s,speakObserver);
        //方式1，讯飞
//        TTSUtil.getInstance(activity.getApplicationContext()).speaking(s);
    }

    @Override
    public void destoty() {
//        stopSpeaking();
        AudioWSUtil.getInstance().destory();
    }

    @Override
    public void sendMsg(String msg) {
        stopSpeaking();
        speechText = msg;
        msgEntityList.add(new MsgEntity(speechText, 1));
        selectAdapter.notifyDataSetChanged();
        addMsg();
    }

    private void addMsg(){
        //问诊模式
        if(type == 1) {
            if (null != queationTitleList && queationTitleList.size() > 0) {
                msgEntityList.add(new MsgEntity(queationTitleList.get(0), 2));
                selectAdapter.notifyDataSetChanged();
                setSpeakStr(queationTitleList.get(0));
                queationTitleList.remove(0);
            }
        }else if(type == 2) {
            //问问题的模式
            askObserver = new DisposableSingleObserver<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody value) {
                    try {
                        String result = value.string();
                        result = result.substring(1,result.length()-1);
                        if(TextUtils.isEmpty(result)){
                            result = "对不起，我不明白您的问题。";
                        }
                        msgEntityList.add(new MsgEntity(result, 2));
                        selectAdapter.notifyDataSetChanged();
                        setSpeakStr(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }
            };
            addDisposable(askObserver);
            model.ask(speechText,askObserver);
        }
    }

    private  void initRecoListener(){
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
                    addMsg();
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
