package com.example.inquiryproject;

import android.os.Bundle;

public class NcpScreenActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView.loadUrl(Api.ncpURL);
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
