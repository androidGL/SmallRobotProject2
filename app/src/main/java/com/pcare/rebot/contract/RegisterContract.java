package com.pcare.rebot.contract;

import com.pcare.common.base.IView;
import com.pcare.common.entity.NetResponse;
import com.pcare.common.entity.UserEntity;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * @Author: gl
 * @CreateDate: 2019/11/18
 * @Description:
 */
public interface RegisterContract {
    interface Model{
        void register(UserEntity userInfo, DisposableSingleObserver<NetResponse<UserEntity>> observer);
        void editUser(UserEntity userInfo, DisposableSingleObserver<NetResponse<UserEntity>> observer);
    }
    interface View extends IView{
        void saveUser(UserEntity userInfo);
        void editUser(UserEntity userEntity);
    }
    interface Presenter{
        void register(UserEntity u);
        void editUser(UserEntity u);
    }
}
