package com.pcare.common.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pcare.common.R;

/**
 * @Author: gl
 * @CreateDate: 2019/11/27
 * @Description: 实现一个通用的Dialog
 */
public class CommonAlertDialog extends Dialog {
    private final String TITLE;
    private final String MESSAGE;
    private final String CONFIRMTEXT;
    private final String CANCLETEXT;
    private final OnCancleClickListener onCancleClickListener;
    private final OnConfirmClickListener onConfirmClickListener;

    public interface OnConfirmClickListener{
        void onClick(View view);
    }
    public interface OnCancleClickListener{
        void onClick(View view);
    }

    private CommonAlertDialog(@NonNull Context context, String TITLE, String MESSAGE, String CONFIRMTEXT, String CANCLETEXT,
                             OnCancleClickListener onCancleClickListener, OnConfirmClickListener onConfirmClickListener) {
        super(context,R.style.AlertDialogUtil);
        this.TITLE = TITLE;
        this.MESSAGE = MESSAGE;
        this.CONFIRMTEXT = CONFIRMTEXT;
        this.CANCLETEXT = CANCLETEXT;
        this.onCancleClickListener = onCancleClickListener;
        this.onConfirmClickListener = onConfirmClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_common_dialog);
        initView();
    }
    public static Builder Builder(Context context){
        return new Builder(context);
    }
    private void initView() {
        Button btnConfirm = findViewById(R.id.btn_confirm);
        Button btnCancel = findViewById(R.id.btn_cancel);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvMessage = findViewById(R.id.tv_message);

        if (!TextUtils.isEmpty(TITLE)) {
            tvTitle.setText(TITLE);
        }
        if (!TextUtils.isEmpty(MESSAGE)) {
            tvMessage.setText(MESSAGE);
        }
        if (!TextUtils.isEmpty(CONFIRMTEXT)) {
            btnConfirm.setText(CONFIRMTEXT);
        }
        if (!TextUtils.isEmpty(CANCLETEXT)) {
            btnCancel.setText(CANCLETEXT);
        }
        btnConfirm.setOnClickListener(v -> {
            if(null != onConfirmClickListener){
                onConfirmClickListener.onClick(v);
            }
            dismiss();
        });
        btnCancel.setOnClickListener(v -> {
            if(null != onCancleClickListener){
                onCancleClickListener.onClick(v);
            }
            dismiss();
        });
    }
    public CommonAlertDialog shown(){
        show();
        return this;
    }
    public static class Builder{
        private String mTitle;
        private String mMessage;
        private String mConfirmText;
        private String mCancelText;
        private OnConfirmClickListener mOnConfirmClickListener;
        private OnCancleClickListener mOnCancleClickListener;
        private Context mContext;

        private Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setTitle(String title){
            this.mTitle = title;
            return this;
        }
        public Builder setMessage(String message){
            this.mMessage = message;
            return this;
        }

        public Builder setConfirmText(String mConfirmText) {
            this.mConfirmText = mConfirmText;
            return this;
        }

        public Builder setCancelText(String mCancelText) {
            this.mCancelText = mCancelText;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener mOnConfirmClickListener) {
            this.mOnConfirmClickListener = mOnConfirmClickListener;
            return this;
        }

        public Builder setOnCancleClickListener(OnCancleClickListener mOnCancelClickListener) {
            this.mOnCancleClickListener = mOnCancelClickListener;
            return this;
        }
        public CommonAlertDialog build(){
            return new CommonAlertDialog(mContext,mTitle,mMessage,mConfirmText,mCancelText,
                    mOnCancleClickListener,mOnConfirmClickListener);
        }
    }
}
