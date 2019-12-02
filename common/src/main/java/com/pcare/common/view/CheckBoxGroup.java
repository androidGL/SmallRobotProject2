package com.pcare.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.pcare.common.R;

import java.util.HashSet;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description:
 */
public class CheckBoxGroup extends LinearLayout {
    private HashSet<Integer> checkedList = new HashSet<>();

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
    private int marginLeft=10,marginTop=10;//初始化子view的间距
    public CheckBoxGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public CheckBoxGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxGroup);
        marginLeft = a.getDimensionPixelSize(R.styleable.CheckBoxGroup_childMarginLeft,10);
        marginTop = a.getDimensionPixelSize(R.styleable.CheckBoxGroup_childMarginTop,10);
        a.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);//获取最大宽度
        int x=0,y=0,row=0;//初始化group的左边距和上边距和行数,x和y也用来记录测量子view累加的宽高
        int maxHeight=0;
        for(int index = 0;index<getChildCount();index++){//循环遍历子view
            final View child = getChildAt(index);
            if(child.getVisibility()!=View.GONE){
                //UNSPECIFIED表示不限定子view大小，EXACTLY实际值，父容器指定具体的值，AT_MOST父容器提供最大值
                child.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);//测量子view宽高
                int width = child.getMeasuredWidth(),height = child.getMeasuredHeight();
                if(height>maxHeight)//获取这一行中最高的子view
                    maxHeight=height;
                x+=width+marginLeft;//将测量到的宽度添加到x中
                if(row<1){
                    y=maxHeight+marginTop;//将测量到的高度添加进来
                }
                //当累加的宽度大于最大宽度时
                if(x>maxWidth){
                    if(index != 0){//防止第一个view就大于最大宽度
                        row++;
                    }
                    if(width>=maxWidth-marginLeft){//如果这个view比最大宽度还要宽，那x直接设为最大宽度
                        x = maxWidth;
                    }else{
                        x=width+marginLeft;//否则就充值当前x
                    }
                    y+=maxHeight+marginTop;//增加行数后高度也得累加
                    maxHeight = 0;//重置最大高度，开始下一行的计算

                }
            }
        }
        setMeasuredDimension(maxWidth,y);//设置容器需要的宽度和高度
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int maxWidth=r-1;//获取最大宽度
        int x = 0,y = 0,row = 0;
        int maxHeight = 0;
        for(int index = 0;index < getChildCount();index++){
            View child=getChildAt(index);
            if(child.getVisibility()!=GONE){
                int width = child.getMeasuredWidth(),height = child.getMeasuredHeight();
                if(height>maxHeight)
                    maxHeight = height;
                x+=width+marginLeft;
                if(row<1){
                    y=maxHeight+marginTop;
                }
                if(x>maxWidth){
                    if(index!=0)
                        row++;
                    if(width>=maxWidth-marginLeft){
                        x=maxWidth;
                    }else {
                        x=width+marginLeft;
                    }
                    y+=maxHeight+marginTop;
                    maxHeight=0;
                }
                child.layout(x-width,y-height,x,y);
            }
        }


    }

    private void init() {
        mOnCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkedList.add(compoundButton.getId());
                }else {
                    checkedList.remove(compoundButton.getId());
                }
            }
        };
    }


    @Override
    public void addView(View child) {
        if(child instanceof CheckBox){
            CheckBox checkBox = (CheckBox) child;
            if(checkBox.isChecked()){
                setCheckIdList(checkBox.getId());
            }
            checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }
        super.addView(child);
    }

    private void setCheckIdList(@IdRes int id) {
        checkedList.add(id);
    }

    public void clearChecked(){
        checkedList.clear();
    }
    private void removeChecked(@IdRes int id){
        checkedList.remove(id);
    }

}
