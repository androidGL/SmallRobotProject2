package com.pcare.rebot.model;

import com.pcare.common.entity.NetResponse;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.rebot.contract.UserListContract;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: gl
 * @CreateDate: 2019/11/28
 * @Description: 处理用户列表的model类
 */
public class UserListModel implements UserListContract.Model {

    @Override
    public void deleteUser(String userId, SingleObserver<NetResponse> observer) {
        RetrofitHelper.getInstance()
                .getRetrofit()
                .create(Api.class)
                .getUserList(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(observer);
    }
}
