package com.pcare.common.table;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.pcare.common.R;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.view.UserListDialog;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/11/19
 * @Description:
 */
public class UserDao {
    private static UserDao userConstant;
    private Context context;
    private static String currentUserId;

    public UserDao(Context context) {
        this.context = context;
    }

    public static UserDao get(Context context) {
        if (null == userConstant) {
            synchronized (UserDao.class) {
                userConstant = new UserDao(context);
            }
        }
        return userConstant;
    }

    private void setCurrentUserId(String currentUserId) {
        UserDao.currentUserId = currentUserId;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public boolean setCurrentUser(UserEntity user){
        if(null != getCurrentUser())
            updateUser(getCurrentUser().setCurrentUser(false));
        updateUser(user.setCurrentUser(true));
        setCurrentUserId(user.getUserId());
        return true;
    }
    public boolean setCurrentUser(String userId){
        if (TextUtils.isEmpty(userId)||null == getUserById(userId))
            return false;
        return setCurrentUser(getUserById(userId));
    }
    public UserEntity getCurrentUser() {
        return UserTableController.getInstance(context).getCurrentUser();
    }
    public UserEntity getUserById(String userId) {
        return UserTableController.getInstance(context).getUser(userId);
    }

    public static int setImage(int userType) {
        switch (userType) {
            case 0:
                return R.mipmap.grandfather;
            case 1:
                return R.mipmap.grandmother;
            case 2:
                return R.mipmap.yongman;
            case 3:
                return R.mipmap.yongwoman;
            default:
                return R.mipmap.grandfather;
        }
    }

    public void insertUser(UserEntity userInfo) {
        UserTableController.getInstance(context).insert(userInfo);
    }
    public void updateUser(UserEntity userInfo) {
        UserTableController.getInstance(context).insertOrReplace(userInfo);
    }

    public List<UserEntity> getUserList() {
        return UserTableController.getInstance(context).searchAll();
    }

    public void setUserList(List<UserEntity> userList) {
        UserTableController.getInstance(context).setList(userList);
    }

    public void deleteUserById(String id) {
        UserTableController.getInstance(context).deleteById(id);
    }

    public void clearAllUser() {
        UserTableController.getInstance(context).clear();
    }
}
