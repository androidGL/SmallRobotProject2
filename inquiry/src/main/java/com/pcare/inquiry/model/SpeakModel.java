package com.pcare.inquiry.model;

import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.common.net.url.RetrofitUrlManager;
import com.pcare.inquiry.contract.SpeakContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @Author: gl
 * @CreateDate: 2020/1/2
 * @Description:
 */
public class SpeakModel implements SpeakContract.Model {

    @Override
    public void ask(String question, DisposableSingleObserver<ResponseBody> observer) {
        RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_ASK,Api.ASKURL);
        RetrofitHelper.getInstance()
                .getRetrofit()
                .create(Api.class)
                .ask(question)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(observer);
    }

    @Override
    public void speak(String text, DisposableSingleObserver<ResponseBody> observer) {
        RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_AUDIO,Api.AUDIOURL);
        RetrofitHelper.getInstance()
                .getRetrofit()
                .create(Api.class)
                .playAudio(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(observer);
    }


}
