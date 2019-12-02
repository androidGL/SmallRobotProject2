package com.pcare.common.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pcare.common.entity.DaoMaster;
import com.pcare.common.entity.DaoSession;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.entity.UserEntityDao;

import org.greenrobot.greendao.Property;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/11/20
 * @Description: 操作数据库的工作类
 */
public class UserTableController {
    /**
     * Helper
     */
    private DaoMaster.DevOpenHelper mHelper;//获取Helper对象
    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * DaoMaster
     */
    private DaoMaster mDaoMaster;
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     * 上下文
     */
    private Context context;
    /**
     * dao
     */
    private UserEntityDao userEntityDao;

    private static UserTableController mDbController;

    /**
     * 获取单例
     */
    public static UserTableController getInstance(Context context){
        if(mDbController == null){
            synchronized (UserTableController.class){
                if(mDbController == null){
                    mDbController = new UserTableController(context);
                }
            }
        }
        return mDbController;
    }
    /**
     * 初始化
     * @param context
     */
    public UserTableController(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context,"person.db", null);
        mDaoMaster =new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        userEntityDao = mDaoSession.getUserEntityDao();
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase(){
        if(mHelper == null){
            mHelper = new DaoMaster.DevOpenHelper(context,"person.db",null);
        }
        SQLiteDatabase db =mHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     * @return
     */
    private SQLiteDatabase getWritableDatabase(){
        if(mHelper == null){
            mHelper =new DaoMaster.DevOpenHelper(context,"person.db",null);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db;
    }

    /**
     * 会自动判定是插入还是替换
     * @param userEntity
     */
    public void insertOrReplace(UserEntity userEntity){
        userEntityDao.insertOrReplace(userEntity);
    }
    /**插入一条记录，表里面要没有与之相同的记录
     *
     * @param userEntity
     */
    public long insert(UserEntity userEntity){
        return  userEntityDao.insert(userEntity);
    }

    /**
     * 插入列表
     * @param userEntityList
     */
    public void setList(List<UserEntity> userEntityList){
        userEntityDao.insertInTx(userEntityList,false);
    }

    /**
     * 更新数据
     * @param userEntity
     */
    public void update(UserEntity userEntity){
        UserEntity mOldUserEntity = getUser(userEntity.getUserId());//拿到之前的记录
        if(mOldUserEntity !=null){
            userEntityDao.update(mOldUserEntity);
        }
    }
    /**
     * 按条件查询列表数据
     */
    public List<UserEntity> searchListByWhere(Property property, String value){
        List<UserEntity>userEntitys = (List<UserEntity>) userEntityDao.queryBuilder().where(property.eq(value)).list();
        return userEntitys;
    }

    /**
     * 按条件查询单项数据
     * @return
     */
    public UserEntity getUserByWhere(Property property, String value){
        return userEntityDao.queryBuilder().where(property.eq(value)).build().unique();
    }
    /**
     * 查询所有数据
     */
    public List<UserEntity> searchAll(){
        List<UserEntity>userEntitys=userEntityDao.queryBuilder().orderDesc(UserEntityDao.Properties.Id).list();
        return userEntitys;
    }

    /**
     * 获取当前用户信息
     * @return
     */
    public UserEntity getCurrentUser(){
        return userEntityDao.queryBuilder().where(UserEntityDao.Properties.CurrentUser.eq(true)).build().unique();
    }

    public UserEntity getUser(String userId){
        if(null == userId)
            return null;
        return userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(userId)).build().unique();
    }
    /**
     * 删除数据
     */
    public void deleteById(String userId){
        userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(userId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    /**
     * 删除所有数据
     */
    public void clear(){
        userEntityDao.deleteAll();
    }


}
