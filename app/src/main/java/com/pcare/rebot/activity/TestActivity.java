package com.pcare.rebot.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.rebot.R;

/**
 * @Author: gl
 * @CreateDate: 2019/11/29
 * @Description:
 */
public class TestActivity extends BaseActivity {
    private EditText editText;
    private String id;
    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initView() {
        super.initView();
        editText = findViewById(R.id.user_id);
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }
    public void detect(View view) {
        id = editText.getText().toString();
        startActivity(new Intent(this, FaceActivity.class)
                .putExtra("type",0)
                .putExtra("userId", id));
    }

    public void verify(View view) {
        startActivity(new Intent(this, FaceActivity.class)
                .putExtra("type", 1));
    }
}
