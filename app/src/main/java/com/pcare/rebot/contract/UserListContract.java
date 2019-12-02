package com.pcare.rebot.contract;

import com.pcare.common.base.IView;
import com.pcare.common.entity.NetResponse;

import io.reactivex.SingleObserver;

/**
 * @Author: gl
 * @CreateDate: 2019/11/28
 * @Description:
 */
public interface UserListContract {
    interface Model{
        void deleteUser(String userId, SingleObserver<NetResponse> observer);
    }
    interface View extends IView {
        void refreshList(String msg);
    }
    interface Presenter{
        void deleteUser(String userId);
    }
}
