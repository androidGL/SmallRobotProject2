package com.pcare.inquiry.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.inquiry.R;

@Route(path = "/inquiry/main")
public class MainActivity extends BaseActivity {
//    @Autowired
    public String inquiryType = "haha";

    private TextView inquiry_type;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_inquiry;

    }

    public void startRequest(View view) {
        Intent intent = new Intent(this, SpeakActivity.class);
        if (view.getId() == R.id.request_type_select) {
            intent.putExtra("type", "select");
        } else if (view.getId() == R.id.request_type_speak){
            intent.putExtra("type", "speak");
        }else {
            intent.putExtra("type", "ask");
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void start() {
        super.start();
        inquiry_type = findViewById(R.id.inquiry_type);
        inquiry_type.setText("您选择的问诊项目是："+inquiryType);
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }
}
