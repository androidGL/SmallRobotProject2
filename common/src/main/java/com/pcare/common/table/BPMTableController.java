package com.pcare.common.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pcare.common.entity.BPMEntity;
import com.pcare.common.entity.BPMEntityDao;
import com.pcare.common.entity.DaoMaster;
import com.pcare.common.entity.DaoSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/11/20
 * @Description: 操作数据库的工作类
 */
public class BPMTableController {
    private final String NAME = "bpm.db";
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
    private BPMEntityDao bpmEntityDao;

    private static BPMTableController mDbController;

    /**
     * 获取单例
     */
    public static BPMTableController getInstance(Context context){
        if(mDbController == null){
            synchronized (BPMTableController.class){
                if(mDbController == null){
                    mDbController = new BPMTableController(context);
                }
            }
        }
        return mDbController;
    }
    /**
     * 初始化
     * @param context
     */
    public BPMTableController(Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(context,NAME, null);
        mDaoMaster =new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        bpmEntityDao = mDaoSession.getBPMEntityDao();
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
     * @param bpmEntity
     */
    public void insertOrReplace(BPMEntity bpmEntity){
        bpmEntityDao.insertOrReplace(bpmEntity);
    }
    /**插入一条记录，表里面要没有与之相同的记录
     *
     * @param bpmEntity
     */
    public long insert(BPMEntity bpmEntity){
        return  bpmEntityDao.insert(bpmEntity);
    }

    /**
     * 插入列表
     * @param bpmEntityList
     */
    public void setList(List<BPMEntity> bpmEntityList){
        bpmEntityDao.insertInTx(bpmEntityList,false);
    }

    /**
     * 更新数据
     * @param bpmEntity
     */
    public void update(BPMEntity bpmEntity){
        BPMEntity mOldBpmEntity = bpmEntityDao.queryBuilder().where(BPMEntityDao.Properties.BpmId.eq(bpmEntity.getBpmId())).build().unique();//拿到之前的记录
        if(mOldBpmEntity !=null){
            bpmEntityDao.update(bpmEntity);
        }else {
            bpmEntityDao.insert(bpmEntity);
        }
    }
    /**
     * 按条件查询数据
     */
    public List<BPMEntity> searchByUserId(String userId){
        List<BPMEntity> bpmEntities = (List<BPMEntity>) bpmEntityDao.queryBuilder().where(BPMEntityDao.Properties.UserId.eq(userId)).list();
        return bpmEntities;
    }
    /**
     * 查询所有数据
     */
    public List<BPMEntity> searchAll(){
        List<BPMEntity>bpmEntities=bpmEntityDao.queryBuilder().list();
        return bpmEntities;
    }

    public BPMEntity getBPM(String bpmId){
        return bpmEntityDao.queryBuilder().where(BPMEntityDao.Properties.BpmId.eq(bpmId)).build().unique();
    }
    /**
     * 删除数据
     */
    public void deleteById(String bpmId){
        bpmEntityDao.queryBuilder().where(BPMEntityDao.Properties.BpmId.eq(bpmId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    /**
     * 删除所有数据
     */
    public void clear(){
        bpmEntityDao.deleteAll();
    }
}
