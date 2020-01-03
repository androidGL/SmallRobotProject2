package com.pcare.common.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pcare.common.R;
import com.pcare.common.table.UserDao;
import com.pcare.common.view.UserListDialog;

import butterknife.ButterKnife;

public abstract class BaseActivity<P extends IPresenter> extends Activity implements IView {
    protected P presenter;
    private TextView userNameText; //用户姓名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //强制横屏显示
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(getLayoutId());
        ARouter.getInstance().inject(this);
        initView();
        initData();
        ButterKnife.bind(this);
        presenter = bindPresenter();
        start();
        userNameText = findViewById(R.id.user_name);
        if (null != userNameText)
            userNameText.setText(UserDao.get(getApplicationContext()).getCurrentUser().getUserName());
    }

    public abstract int getLayoutId();

    // 绑定Presenter
    protected abstract P bindPresenter();

    //开始了
    public void start() {

    }

    protected void initView() {

    }
    protected void initData(){

    }


    @Override
    public Activity getSelfActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.detachView();
    }

    public void replaceUser(View view) {
        new UserListDialog(getSelfActivity())
                .setOnClickItemListener(new UserListDialog.OnClickItemListener() {
                    @Override
                    public <T> void onItemClick(String name) {
                        userNameText.setText(name);
                    }
                }).show();
    }

    public void back(View view) {
        finish();
    }
}
