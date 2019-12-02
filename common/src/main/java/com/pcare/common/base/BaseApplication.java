package com.pcare.common.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.LinkedList;

/**
 * @Author: gl
 * @CreateDate: 2019/10/16
 * @Description: 基础application
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;
    private final String TAG = "LogCallback";
    private boolean debug = true;
    public static BaseApplication getInstance() {
        if(null == instance){
            synchronized (BaseApplication.class){
                if(null == instance){
                    instance = new BaseApplication();
                }
                return instance;
            }
        }else
            return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 进行科大讯飞SDK的初始化
        instance = this;
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "="+Constant.APPID_XFYUN);
        initARouter();
    }
    public boolean isDebug() {
        return debug;
    }
    private void initARouter() {
        if (isDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
    }

    /**
     * 返回一个存储所有存在的activity的列表
     *
     * @return
     */
//    public LinkedList<BaseCommonActivity> getActivityList() {
//        if (mActivityList == null) {
//            mActivityList = new LinkedList<>();
//        }
//        return mActivityList;
//    }
    /**
     * 退出所有activity
     */
//    public static void killAll() {
//        Intent intent = new Intent(BaseCommonActivity.ACTION_RECEIVER_ACTIVITY);
//        intent.putExtra("type", "killAll");
//        getContext().sendBroadcast(intent);
//    }
}
