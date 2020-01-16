package com.pcare.rebot.presenter;

import android.util.Log;
import android.widget.Toast;

import com.pcare.common.base.BasePresenter;
import com.pcare.common.entity.NetResponse;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.UserTableController;
import com.pcare.common.util.LogUtil;
import com.pcare.rebot.contract.RegisterContract;
import com.pcare.rebot.model.RegisterModel;

import io.reactivex.SingleObserver;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * @Author: gl
 * @CreateDate: 2019/11/18
 * @Description:
 */
public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {

    private RegisterContract.Model model;

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
        model = new RegisterModel();
    }

    @Override
    public void register(UserEntity u) {
        u.setUserId("user"+ (UserTableController.getInstance(getView().getSelfActivity()).searchAll().size()+1));
        DisposableSingleObserver observer =new DisposableSingleObserver<NetResponse<UserEntity>>(){

            @Override
            public void onSuccess(NetResponse<UserEntity> value) {
                if(value.getStatus() == 1) {
                    getView().saveUser(value.getData());
                }else {
                    Toast.makeText(getView().getSelfActivity(),value.getMsg(),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };
        addDisposable(observer);
        model.register(u,observer);
    }

    @Override
    public void editUser(UserEntity u) {
//        getView().editUser(u);
        DisposableSingleObserver observer = new DisposableSingleObserver<NetResponse>() {
            @Override
            public void onSuccess(NetResponse response) {
                if(response.getStatus() == 1) {
                    getView().editUser((UserEntity) response.getData());
                }else {

                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };
        addDisposable(observer);
        model.editUser(u,observer);

    }
}
