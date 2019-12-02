package com.pcare.rebot.activity;

import android.view.TextureView;
import android.view.View;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.net.Api;
import com.pcare.common.table.UserDao;
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
        faceUtil = new FaceUtil(this,textureView)
                .setFaceDetectListener(new FaceUtil.FaceDetectListener() {
                    @Override
                    public void detectSucess() {
                        finish();
                    }

                    @Override
                    public void detectFail() {

                    }
                });
        type = getIntent().getIntExtra("type",0);
        if(type == 0){
            faceUtil.init(Api.FACEURL, getIntent().getStringExtra("userId"));
//            faceUtil.init(Api.FACEURL, UserDao.getCurrentUserId());
        }else {
            faceUtil.init(Api.FACEURL,null);
        }
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void stop() {
        if(null!=faceUtil)
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
