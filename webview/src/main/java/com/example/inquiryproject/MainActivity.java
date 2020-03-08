package com.example.inquiryproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {
    private final String GUOPAGE = "guo", MEDICINEPAGE = "medicine", DIAGNOSEPAGE = "diagnose",NCP_SCREEN="ncp_screen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    public void toNextPage(View view) {
        Intent intent;
        switch (view.getTag().toString()) {
            case GUOPAGE:
                intent = new Intent(this, GuoActivity.class);
                break;
            case DIAGNOSEPAGE:
                intent = new Intent(this, DiagnoseActivity.class);
                break;
            case MEDICINEPAGE:
                intent = new Intent(this, MedicineActivity.class);
                break;
                case NCP_SCREEN:
                    intent = new Intent(this,NcpScreenActivity.class);
                    break;
            default:
                intent = new Intent(this, MedicineActivity.class);
                break;
        }
        startActivity(intent);
    }
    public void openWake(View view) {
        TtsUtil.startSpeak(getApplicationContext(),"打开了唤醒功能");
        WakeUtil.start(getApplicationContext());
    }
    public void closeWake(View view) {
        TtsUtil.startSpeak(getApplicationContext(),"关闭了唤醒功能");
        WakeUtil.stop();
    }
    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
////激活WebView为活跃状态，能正常执行网页的响应
//webView.onResume() ；
//
//        //当页面被失去焦点被切换到后台不可见状态，需要执行onPause
////通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
//        webView.onPause()；
//
//        //当应用程序(存在webview)被切换到后台时，这个方法不仅仅针对当前的webview而是全局的全应用程序的webview
////它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
//        webView.pauseTimers()
////恢复pauseTimers状态
//        webView.resumeTimers()；
//
//        //销毁Webview
////在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
////但是注意：webview调用destory时,webview仍绑定在Activity上
////这是由于自定义webview构建时传入了该Activity的context对象
////因此需要先从父容器中移除webview,然后再销毁webview:
//        rootLayout.removeView(webView);
//        webView.destroy();


////是否可以后退
//Webview.canGoBack()
////后退网页
//        Webview.goBack()
//
////是否可以前进
//        Webview.canGoForward()
////前进网页
//        Webview.goForward()
//
////以当前的index为起始点前进或者后退到历史记录中指定的steps
////如果steps为负数则为后退，正数则为前进
//        Webview.goBackOrForward(intsteps)


////清除网页访问留下的缓存
////由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
//Webview.clearCache(true);
//
////清除当前webview访问的历史记录
////只会webview访问历史记录里的所有记录除了当前访问记录
//        Webview.clearHistory()；
//
//        //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
//        Webview.clearFormData()；


//    //声明WebSettings子类
//    WebSettings webSettings = webView.getSettings();
//
////如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//webSettings.setJavaScriptEnabled(true);
//
////支持插件
//        webSettings.setPluginsEnabled(true);
//
////设置自适应屏幕，两者合用
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//
////缩放操作
//        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
//        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
//        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//
////其他细节操作
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
//        webSettings.setAllowFileAccess(true); //设置可以访问文件
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式


