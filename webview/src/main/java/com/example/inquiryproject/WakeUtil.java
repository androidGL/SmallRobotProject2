package com.example.inquiryproject;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

import static android.content.Context.KEYGUARD_SERVICE;

public class WakeUtil {
    // 语音唤醒对象
    private static VoiceWakeuper mIvw;
    // 唤醒结果内容
    private static String resultString;

    // 设置门限值 ： 门限值越低越容易被唤醒
    private final static int curThresh = 1450;
    private String threshStr = "门限值：";
    private final static String keep_alive = "1";
    private final static String ivwNetMode = "0";
    private static Context mContext;
    private final static String TAG = "WakeUtil";

    private static void init(Context context) {
        // 初始化唤醒对象
        if (mIvw == null)
            mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    public static void start(Context context) {
        mContext = context;
        init(context);
        //非空判断，防止因空指针使程序崩溃
        if (mIvw == null)
            mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource(context));
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
            mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
            //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
            // 启动唤醒
            /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

            mIvw.startListening(mWakeuperListener);
				/*File file = new File(Environment.getExternalStorageDirectory().getPath() + "/msc/ivw1.wav");
				byte[] byetsFromFile = getByetsFromFile(file);
				mIvw.writeAudio(byetsFromFile,0,byetsFromFile.length);*/
            //	mIvw.stopListening();
        } else {
            Log.i(TAG,"唤醒未初始化");
        }
    }

    public static void stop() {
        if (null != mIvw)
            mIvw.stopListening();
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
        //     mIvw.writeAudio();
    }

    // 查询资源请求回调监听
    private RequestListener requestListener = new RequestListener() {
        @Override
        public void onEvent(int eventType, Bundle params) {
            // 以下代码用于获取查询会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            if(SpeechEvent.EVENT_SESSION_ID == eventType) {
             	Log.d(TAG, "sid:"+params.getString(SpeechEvent.KEY_EVENT_SESSION_ID));
            }
        }

        @Override
        public void onCompleted(SpeechError error) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            try {
                String resultInfo = new String(buffer, "utf-8");

                JSONTokener tokener = new JSONTokener(resultInfo);
                JSONObject object = new JSONObject(tokener);

                int ret = object.getInt("ret");
                if (ret == 0) {
                    String uri = object.getString("dlurl");
                    String md5 = object.getString("md5");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private static WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            Log.i("测试结果", resultString);
            wakeUpAndUnlock();//唤醒手机屏幕
            openMainPage();//打开福娃页面
            TtsUtil.startSpeak(mContext, "我在我在啊");

        }

        @Override
        public void onError(SpeechError error) {

        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch (eventType) {
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };

    private static String getResource(Context context) {
        final String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + context.getString(R.string.app_id) + ".jet");
        return resPath;
    }

    /**
     * 唤醒手机屏幕并解锁
     */
    private static void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }

    public static void getInstalledPackages(Context context){
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo pi : packages){
            Log.i("安装包",pi.applicationInfo.loadLabel(context.getPackageManager()).toString()+pi.packageName);
//            InstalledAppMap.put(pi.applicationInfo.loadLabel(mContext.getPackageManager()).toString(),
//                    pi.packageName);
        }
        Intent Newintent = new Intent();
        Newintent.setComponent(new ComponentName("com.example.inquiryproject",GuoActivity.class.getName()));//InquiryProject    com.example.inquiryproject
        context.startActivity(Newintent);
    }

    private static void openMainPage(){
        Intent Newintent = new Intent();
        Newintent.setComponent(new ComponentName("com.example.inquiryproject",MainActivity.class.getName()));//InquiryProject    com.example.inquiryproject
        mContext.startActivity(Newintent);
    }

    private static void openPage(Context context){
        Intent Newintent = new Intent();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN,null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        String packageName = InstalledAppMap.get(AppName);
        String packageName = "com.example.android.notepad";
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        ResolveInfo ri = apps.iterator().next();
        if (ri!=null){
            String className = ri.activityInfo.name;
            Newintent.setComponent(new ComponentName(packageName,className));
            Log.i("安装包hhh",packageName+"  "+className);
        }
        context.startActivity(Newintent);
    }


}
