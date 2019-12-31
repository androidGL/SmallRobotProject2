package com.pcare.rebot.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.UserDao;
import com.pcare.common.view.CommonAlertDialog;
import com.pcare.common.view.ScreenCheckBox;
import com.pcare.common.view.YearPickerDialog;
import com.pcare.rebot.R;
import com.pcare.rebot.contract.RegisterContract;
import com.pcare.rebot.presenter.RegisterPresenter;


/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description: 注册页面
 */
public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterContract.View {

    private TextView yearView;
    private ScreenCheckBox male, female;
    private EditText infoName, infoStature, infoWeight;
    private TextView infoTypeName;
    private TextView titleView;
    private UserEntity mUserInfo;
    private int selectYear;
    private int userType;
    private String userTypeName;
    private String type = "register";//界面展示类型,register:注册；editUser:修改用户信息

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected RegisterPresenter bindPresenter() {
        return new RegisterPresenter((RegisterContract.View) getSelfActivity());
    }

    @Override
    public void initView() {
        super.initView();
        yearView = findViewById(R.id.info_year);
        male = findViewById(R.id.checkbox_male);
        female = findViewById(R.id.checkbox_female);
        infoName = findViewById(R.id.info_name);
        infoStature = findViewById(R.id.info_stature);
        infoWeight = findViewById(R.id.info_weight);
        infoTypeName = findViewById(R.id.info_type_name);
        titleView = findViewById(R.id.title);
    }

    @Override
    public void start() {
        super.start();
        mUserInfo = new UserEntity();
        if (getIntent().hasExtra("userId")) {
            type = "editUser";
            titleView.setText("修改用户信息");
            mUserInfo = UserDao.get(getApplicationContext()).getUserById(getIntent().getStringExtra("userId"));
            setViews(mUserInfo);
        }

    }

    private void setViews(UserEntity user) {
        yearView.setText(String.valueOf(user.getUserBirthYear()));
        infoName.setText(user.getUserName());
        setUserType(user.getUserType());
        infoTypeName.setText(userTypeName);
    }

    @SuppressWarnings("ResourceType")
    public void selectYear(View view) {
        YearPickerDialog yearDialog = new YearPickerDialog(getSelfActivity(), 0);
        yearDialog.setButton(Dialog.BUTTON_POSITIVE, "选好了", (dialog, which) -> {
            selectYear = yearDialog.getYear();
            yearView.setText(selectYear + "年");
        });
        yearDialog.show();
    }

    private void setUserType(int type) {
        userType = type;
        switch (type) {
            case 0:
                male.setChecked(true);
                female.setChecked(false);
                userTypeName = "爷爷";
                break;
            case 1:
                female.setChecked(true);
                male.setChecked(false);
                userTypeName = "奶奶";
                break;
            case 2:
                male.setChecked(true);
                female.setChecked(false);
                userTypeName = "叔叔";
                break;
            case 3:
                female.setChecked(true);
                male.setChecked(false);
                userTypeName = "阿姨";
                break;
        }
    }

    public void selectID(View view) {
        setUserType(Integer.parseInt(view.getTag().toString()));
        infoTypeName.setText(userTypeName);
    }

    @Override
    public void saveUser(UserEntity userInfo) {
        UserDao.get(getApplicationContext()).insertUser(userInfo);
        UserDao.get(getApplicationContext()).setCurrentUser(userInfo);
        CommonAlertDialog.Builder(this)
                .setTitle("人脸识别认证")
                .setMessage("为了更方便快捷的使用本设备，建议您进行人脸识别认证。")
                .setOnConfirmClickListener(view1 -> {
                    //跳转人脸识别界面，type为0表示注册
                    startActivity(new Intent(this, FaceActivity.class)
                            .putExtra("type", 0)
                            .putExtra("userId", userInfo.getUserId())
                            .putExtra("resource", "register"));
                    finish();
                })
                .setOnCancleClickListener(view1 -> {
                    Toast.makeText(getApplicationContext(), "添加新用户成功", Toast.LENGTH_SHORT);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .build()
                .shown();

    }

    @Override
    public void editUser(UserEntity userEntity) {
        UserDao.get(getApplicationContext()).updateUser(mUserInfo);
        finish();
    }

    public void toRegister(View view) {
        if (TextUtils.isEmpty(userTypeName)) {
            Toast.makeText(getApplicationContext(), "请您选择身份类型", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(infoName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "请您设置昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        mUserInfo.setUserName(infoName.getText().toString());
        mUserInfo.setUserType(userType);
        mUserInfo.setUserBirthYear(selectYear);
        mUserInfo.setUserWeight(infoWeight.getText().toString());
        mUserInfo.setUserStature(infoStature.getText().toString());

        if ("register".equals(type))
            presenter.register(mUserInfo);
        else
            presenter.editUser(mUserInfo);

    }
}
