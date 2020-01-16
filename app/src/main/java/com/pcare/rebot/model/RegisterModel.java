package com.pcare.rebot.model;

import com.pcare.common.entity.NetResponse;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.rebot.contract.RegisterContract;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @Author: gl
 * @CreateDate: 2019/11/18
 * @Description:
 */
public class RegisterModel implements RegisterContract.Model {
    @Override
    public void register(UserEntity userInfo, DisposableSingleObserver<NetResponse<UserEntity>> observer) {
        //第一个参数表示要修改哪个网络请求的BaseURL，第二个参数表示要修改成什么样的URL
//        RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_SECOND, Api.BASEURL2);
        RetrofitHelper.getInstance()
                .getRetrofit()
                .create(Api.class)
                .register(userInfo,"register")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(observer);
    }

    @Override
    public void editUser(UserEntity userInfo, DisposableSingleObserver<NetResponse<UserEntity>> observer) {
        RetrofitHelper.getInstance()
                .getRetrofit()
                .create(Api.class)
                .register(userInfo,"register")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(observer);
    }
}
