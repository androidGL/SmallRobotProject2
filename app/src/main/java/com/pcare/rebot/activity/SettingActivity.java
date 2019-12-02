package com.pcare.rebot.activity;

import android.content.Intent;
import android.view.View;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.rebot.R;

/**
 * @Author: gl
 * @CreateDate: 2019/11/18
 * @Description: 设置页面
 */
public class SettingActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void toUserListActivity(View view) {
        startActivity(new Intent(this,UserListActivity.class));
    }
}
