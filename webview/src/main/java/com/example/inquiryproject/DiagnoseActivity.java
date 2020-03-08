package com.example.inquiryproject;
import android.os.Bundle;

public class DiagnoseActivity extends BaseWebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.loadUrl(Api.diagnoseURL);
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
