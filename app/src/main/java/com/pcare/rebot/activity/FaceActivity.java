package com.pcare.rebot.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.net.Api;
import com.pcare.common.table.UserDao;
import com.pcare.common.table.UserTableController;
import com.pcare.common.util.FaceUtil;
import com.pcare.rebot.R;

public class FaceActivity extends BaseActivity {

    private TextureView textureView;
    private FaceUtil faceUtil;
    private int type;


    @Override
    public int getLayoutId() {
        return R.layout.activity_face;
    }

    @Override
    public void start() {
        super.start();
        textureView = findViewById(R.id.look_container);
        faceUtil = new FaceUtil(this, textureView, this.getResources().getConfiguration().orientation)
                .setTimeOut(10000)
                .setFaceDetectListener(new FaceUtil.FaceDetectListener() {
                    @Override
                    public void detectSucess() {
                        if (getIntent().hasExtra("resource") && "register".equals(getIntent().getStringExtra("resource"))) {
                            Toast.makeText(getApplicationContext(), "添加新用户成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FaceActivity.this, MainActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void detectFail() {
                        Toast.makeText(getApplicationContext(), "人脸注册失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setFaceCompareListener(new FaceUtil.FaceCompareListener() {
                    @Override
                    public void compareSucess(String userId) {
                        if (getIntent().hasExtra("resource") && "login".equals(getIntent().getStringExtra("resource"))) {
                            if (UserDao.get(getSelfActivity()).setCurrentUser(userId)) {
                                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(FaceActivity.this, MainActivity.class));
                            }

                        }

                        finish();
                    }

                    @Override
                    public void compareFail() {
                        Toast.makeText(getApplicationContext(), "人脸登陆失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            faceUtil.init(Api.FACEURL, getIntent().getStringExtra("userId"));
        } else {
            faceUtil.init(Api.FACEURL, null);
        }
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void stop() {
        if (null != faceUtil)
            faceUtil.closeSession();
    }

    public void back(View view) {
        stop();
        finish();
    }

    @Override
    protected void onStop() {
        stop();
        super.onStop();
    }
}
