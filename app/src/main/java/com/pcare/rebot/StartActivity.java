package com.pcare.rebot;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.BPMEntity;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.common.net.url.RetrofitUrlManager;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.util.CommonUtil;
import com.pcare.common.util.TextImageView;
import com.pcare.common.view.RecyclerCoverFlow;
import com.pcare.rebot.activity.FaceActivity;
import com.pcare.rebot.activity.MainActivity;
import com.pcare.rebot.activity.RegisterActivity;
import com.pcare.rebot.activity.SettingActivity;
import com.pcare.rebot.activity.TestActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description:
 */
public class StartActivity extends BaseActivity {
    private List<UserEntity> userInfoList;
    private boolean isUpdate = false;
    private RecyclerCoverFlow coverFlow;
    private RecyclerView.Adapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    @Override
    public void start() {
        super.start();
        coverFlow = findViewById(R.id.list_flow);
        coverFlow.setAlphaItem(true);
        if (isUpdate) {
            //保存用户列表前先清空用户列表
            UserDao.get(getApplicationContext()).clearAllUser();
            userInfoList = new ArrayList<>();
            UserDao.get(getApplicationContext()).setUserList(userInfoList);
        }
        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_flow_user, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof ViewHolder) {
                    ((ViewHolder) holder).textImageView.setImgResource(getDrawable(UserDao.setImage(userInfoList.get(position).getUserType())));
                    ((ViewHolder) holder).textImageView.setText(userInfoList.get(position).getUserName());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserDao.get(getApplicationContext()).setCurrentUser(userInfoList.get(position));
                            Log.i("selectUser", userInfoList.get(position).toString());
                            toMainPage();
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return userInfoList.size();
            }
        };
        coverFlow.setAdapter(adapter);
//        getBPM();
//        getItems();
        findViewById(R.id.button_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, TestActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        recycleView();
//        test();
    }

    public void recycleView() {
        userInfoList = UserDao.get(getApplicationContext()).getUserList();
        if (userInfoList.size() <= 0) {
            findViewById(R.id.user_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.start_face_login).setVisibility(View.GONE);
            return;
        }
        findViewById(R.id.user_empty).setVisibility(View.GONE);
        findViewById(R.id.start_face_login).setVisibility(View.VISIBLE);
        int position = userInfoList.indexOf(UserDao.get(getApplicationContext()).getCurrentUser());
        adapter.notifyDataSetChanged();
        coverFlow.setAdapter(adapter);
        coverFlow.getCoverFlowLayout().scrollToPosition(position);
    }

    public void toCompareFace(View view) {
        startActivity(new Intent(this, FaceActivity.class)
                .putExtra("type", 1)
        .putExtra("resource","login"));
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextImageView textImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textImageView = itemView.findViewById(R.id.item_flow_user);
        }
    }

    public void getBPM() {
        List<BPMEntity> list = BPMTableController.getInstance(getApplicationContext()).searchAll();
        if (list.size() > 0) {
            for (BPMEntity entity : list) {
                Log.i("getBPM", entity.toString() + "-------------------------" + CommonUtil.getDateStr(entity.getTimeData()));
            }
        }
//        BPMTableController.getInstance(getApplicationContext()).insert(mBPMEntity);
    }

    public void test() {
        RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_QUESTION,Api.QUESTIONURL);
        RetrofitHelper.getInstance()
                .getRetrofit()
                .create(Api.class)
                .getAnswer("天花")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody value) {
                        try {
                            Log.i("aaa",value.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public void toRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void toMainPage() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void toSetting(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }
}
