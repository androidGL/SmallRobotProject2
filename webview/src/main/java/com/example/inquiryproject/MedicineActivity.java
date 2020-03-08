package com.example.inquiryproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MedicineActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView.loadUrl(Api.medicineURL);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_diagnose;
    }

    @Override
    public void initWebView() {
        mWebView = findViewById(R.id.webview);
    }

}
