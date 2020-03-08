package com.example.inquiryproject;
import android.os.Bundle;

public class GuoActivity extends BaseWebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.loadUrl(Api.guoURL);
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
