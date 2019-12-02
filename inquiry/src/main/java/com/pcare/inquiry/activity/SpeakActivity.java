package com.pcare.inquiry.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.util.TTSUtil;
import com.pcare.inquiry.R;
import com.pcare.inquiry.adapter.QuestionSpeakAdapter;
import com.pcare.inquiry.contract.SpeakContract;
import com.pcare.inquiry.entity.MsgEntity;
import com.pcare.inquiry.presenter.SpeakPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/10/28
 * @Description:
 */
public class SpeakActivity extends BaseActivity<SpeakPresenter> implements SpeakContract.View {

    private RecyclerView QuestionListView;
    private TextView bottomSpeak;
    private TextView questionFinish;
    @Override
    public int getLayoutId() {
        return R.layout.activity_speak;
    }

    @Override
    protected SpeakPresenter bindPresenter() {
        return new SpeakPresenter((SpeakContract.View) getSelfActivity());
    }

    @Override
    public void start() {
        super.start();
        if ("select".equals(getIntent().getExtras().getString("type"))) {
            bottomSpeak.setVisibility(View.GONE);
            questionFinish.setVisibility(View.VISIBLE);
            presenter.useClickType();
        }else {//否则是对话形式
            presenter.useSpeakType();
            bottomSpeak.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        presenter.startSpeak();
                    }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        presenter.stopSpeak();
                    }
                    return true;
                }
            });
        }

    }

    @Override
    public void initView() {
        QuestionListView = findViewById(R.id.question_list);
        bottomSpeak = findViewById(R.id.request_bottom);
        questionFinish = findViewById(R.id.request_finish);
    }


    @Override
    public void showToast(String toast) {

    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        QuestionListView.setLayoutManager(new LinearLayoutManager(this));
        QuestionListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyAdapter() {
    }

    @Override
    protected void onDestroy() {
        presenter.destoty();
        super.onDestroy();
    }

    public void toInquiryResultPage(View view) {
        startActivity(new Intent(this,InquiryResultActivity.class));
    }
}
