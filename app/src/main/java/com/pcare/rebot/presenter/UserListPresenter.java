package com.pcare.rebot.presenter;

import com.pcare.common.base.BasePresenter;
import com.pcare.common.entity.NetResponse;
import com.pcare.rebot.contract.UserListContract;
import com.pcare.rebot.model.UserListModel;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * @Author: gl
 * @CreateDate: 2019/11/28
 * @Description:
 */
public class UserListPresenter extends BasePresenter<UserListContract.View> implements UserListContract.Presenter{
    private UserListContract.Model model;
    public UserListPresenter(UserListContract.View view) {
        super(view);
        model = new UserListModel();
    }

    @Override
    public void deleteUser(String userId) {
        SingleObserver observer = new DisposableSingleObserver<NetResponse>() {
            @Override
            public void onSuccess(NetResponse value) {
                getView().refreshList(value.getMsg());
            }

            @Override
            public void onError(Throwable e) {

            }
        };
        addDisposable((Disposable) observer);
        model.deleteUser(userId,observer);
    }

}
