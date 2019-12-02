package com.pcare.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.pcare.common.R;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description: 自定义选择框
 */
public class ScreenCheckBox extends CheckBox {
    public ScreenCheckBox(Context context) {
        super(context);
        setTextStyle();
        setOnCheckChangeListener();
    }
    public ScreenCheckBox(Context context,String text) {
        super(context);
        setTextStyle();
        setOnCheckChangeListener();
        if(null != text)
            setText(text);
    }

    public ScreenCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
//        inflate(context, R.layout.view_checkbox, null);
        setTextStyle();
        setOnCheckChangeListener();

    }

    private void setOnCheckChangeListener(){
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setText("√"+getText());
                    buttonView.setTextColor(Color.parseColor("#d52c34"));
                }else if(getText().toString().contains("√")){
                    setText(getText().subSequence(1,getText().length()));
                    setTextColor(Color.parseColor("#000000"));
                }
            }
        });
    }
    private void setTextStyle() {
        setBackgroundResource(R.drawable.bg_checkbox);
        setButtonTintMode(PorterDuff.Mode.CLEAR);
        setGravity(TEXT_ALIGNMENT_CENTER);
        setPadding(20,10,20,10);
        if(isChecked()){
            setText("√" + getText());
            setTextColor(Color.parseColor("#d52c34"));
        }
    }
    public String getTextString(){
        if(getText().toString().contains("√")){
            return getText().toString().substring(1);
        }
        return getText().toString();
    }
}
