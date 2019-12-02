package com.pcare.bloodglu;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.table.UserDao;
import com.pcare.common.view.UserListDialog;


public class MainActivity extends BaseActivity {
    private TextView userNameText;
    @Autowired
    public String key1;
    @Override
    public void start() {
        super.start();
        userNameText = findViewById(R.id.user_name);
        userNameText.setText(UserDao.get(getApplicationContext()).getCurrentUser().getUserName());
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_main_glu;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
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
}
