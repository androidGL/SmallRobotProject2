package com.pcare.inquiry.activity;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.inquiry.R;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description: 问诊结果页面
 */
public class InquiryResultActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_inquiry_result;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }
}
