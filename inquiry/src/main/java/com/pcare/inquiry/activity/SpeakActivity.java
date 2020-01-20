package com.pcare.inquiry.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pcare.common.base.BaseActivity;
import com.pcare.inquiry.R;
import com.pcare.inquiry.contract.SpeakContract;
import com.pcare.inquiry.presenter.SpeakPresenter;

/**
 * @Author: gl
 * @CreateDate: 2019/10/28
 * @Description:
 */
public class SpeakActivity extends BaseActivity<SpeakPresenter> implements SpeakContract.View {

    private RecyclerView QuestionListView;
    private View bottomView;
    private EditText bottomEditText;
    private ImageView bottomIcon;
    private TextView bottomSpeak;
    private TextView questionFinish;
    private int speakType = 0;//0表示语音，1表示打字
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
        switch (getIntent().getExtras().getString("type")){
            case "select":
                bottomView.setVisibility(View.GONE);
                questionFinish.setVisibility(View.VISIBLE);
                presenter.useClickType();

                break;
            case "speak":
                presenter.useSpeakType();
                initBottomView();
                break;
            case "ask":
                presenter.useAskType();
                initBottomView();
                break;
        }

    }
    private void initBottomView(){
        bottomSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    presenter.startSpeak();
                    bottomSpeak.setText(getResources().getString(R.string.request_bottom_end));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    presenter.stopSpeak();
                    bottomSpeak.setText(getResources().getString(R.string.request_bottom));
                }
                return true;
            }
        });
        bottomIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speakType == 0){
                    speakType = 1;
                    bottomIcon.setImageDrawable(getDrawable(R.mipmap.talk));
                    bottomEditText.setVisibility(View.VISIBLE);
                    bottomSpeak.setVisibility(View.GONE);
                }else {
                    speakType = 0;
                    bottomIcon.setImageDrawable(getDrawable(R.mipmap.edittext));
                    bottomEditText.setVisibility(View.GONE);
                    bottomSpeak.setVisibility(View.VISIBLE);
                }
            }
        });
        bottomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    if(!TextUtils.isEmpty(bottomEditText.getText().toString())) {
                        hideInput();
                        presenter.sendMsg(bottomEditText.getText().toString());
                        bottomEditText.setText("");
                    }else {
                        Toast.makeText(getSelfActivity(),"发送内容不能为空",Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }
    private void hideInput(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void initView() {
        QuestionListView = findViewById(R.id.question_list);
        bottomView = findViewById(R.id.request_bottom);
        bottomEditText = findViewById(R.id.request_bottom_edit);
        bottomIcon = findViewById(R.id.request_bottom_icon);
        bottomSpeak = findViewById(R.id.request_bottom_talk);
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
