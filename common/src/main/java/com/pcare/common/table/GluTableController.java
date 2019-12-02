package com.pcare.common.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pcare.common.entity.DaoMaster;
import com.pcare.common.entity.DaoSession;
import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.entity.GlucoseEntityDao;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/2
 * @Description: 操控血糖数据库的类
 */
public class GluTableController {
    private final String NAME = "glu.db";
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
    private GlucoseEntityDao glucoseEntityDao;

    private static GluTableController mDbController;

    /**
     * 获取单例
     */
    public static GluTableController getInstance(Context context){
        if(mDbController == null){
            synchronized (GluTableController.class){
                if(mDbController == null){
                    mDbController = new GluTableController(context);
                }
            }
        }
        return mDbController;
    }
    /**
     * 初始化
     * @param context
     */
    public GluTableController(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context,NAME, null);
        mDaoMaster =new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        glucoseEntityDao = mDaoSession.getGlucoseEntityDao();
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase(){
        if(mHelper == null){
            mHelper = new DaoMaster.DevOpenHelper(context,NAME,null);
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
            mHelper =new DaoMaster.DevOpenHelper(context,NAME,null);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db;
    }

    /**
     * 会自动判定是插入还是替换
     * @param glucoseEntity
     */
    public void insertOrReplace(GlucoseEntity glucoseEntity){
        if(glucoseEntity.getUserId().equals(UserDao.getCurrentUserId()))
            glucoseEntityDao.insertOrReplace(glucoseEntity);
        else {

        }
    }
    /**插入一条记录，表里面要没有与之相同的记录
     *
     * @param glucoseEntity
     */
    public long insert(GlucoseEntity glucoseEntity){
        return  glucoseEntityDao.insert(glucoseEntity);
    }

    /**
     * 插入列表
     * @param glucoseEntityList
     */
    public void setList(List<GlucoseEntity> glucoseEntityList){
        glucoseEntityDao.insertInTx(glucoseEntityList,false);
    }

    /**
     * 更新数据
     * @param glucoseEntity
     */
    public void update(GlucoseEntity glucoseEntity){
        GlucoseEntity mOldGluEntity = glucoseEntityDao.queryBuilder().where(GlucoseEntityDao.Properties.GluId.eq(glucoseEntity.getGluId())).build().unique();//拿到之前的记录
        if(mOldGluEntity !=null){
            glucoseEntityDao.update(glucoseEntity);
        }else {
            glucoseEntityDao.insert(glucoseEntity);
        }
    }
    /**
     * 按条件查询数据
     */
    public List<GlucoseEntity> searchByUserId(String userId){
        List<GlucoseEntity> bpmEntities = (List<GlucoseEntity>) glucoseEntityDao.queryBuilder().where(GlucoseEntityDao.Properties.UserId.eq(userId)).list();
        return bpmEntities;
    }
    /**
     * 查询所有数据
     */
    public List<GlucoseEntity> searchAll(){
        List<GlucoseEntity> glucoseEntities= glucoseEntityDao.queryBuilder().list();
        return glucoseEntities;
    }

    public GlucoseEntity getBPM(String gluId){
        return glucoseEntityDao.queryBuilder().where(GlucoseEntityDao.Properties.GluId.eq(gluId)).build().unique();
    }
    /**
     * 删除数据
     */
    public void deleteById(String gluId){
        glucoseEntityDao.queryBuilder().where(GlucoseEntityDao.Properties.GluId.eq(gluId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    /**
     * 删除所有数据
     */
    public void clear(){
        glucoseEntityDao.deleteAll();
    }
}
