package com.pcare.bloodpressure;


import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.oem.bpm.BPMActivity;

public class MainActivity extends BaseActivity {
    @Autowired
    public String key1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_bp;
    }

    @Override
    public void start() {
        super.start();
        Toast.makeText(this, "收到传送过来的数据：" + key1, Toast.LENGTH_LONG).show();
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void toOEM(View view) {
        startActivity(new Intent(this, BPMActivity.class));
//        startActivity(new Intent(this, GlucoseActivity.class));
    }
}
