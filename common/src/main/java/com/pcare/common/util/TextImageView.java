package com.pcare.common.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcare.common.R;

/**
 * @Author: gl
 * @CreateDate: 2019/11/15
 * @Description:
 */
public class TextImageView extends LinearLayout {
    private ImageView mImageView ;
    private TextView mTextView ;
    private int imageSrc;
    private String text;
    private int textColor;
    private int imgSize,textSize;
    public TextImageView(Context context
    ) {
        this(context,null);
    }

    public TextImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public TextImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = inflate(getContext(),R.layout.view_text_image,this);
        mImageView = inflate.findViewById(R.id.view_image);
        mTextView = inflate.findViewById(R.id.view_text);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TextImageView);
        text = array.getString(R.styleable.TextImageView_text);
        textColor = array.getColor(R.styleable.TextImageView_textColor,Color.BLACK);
        imageSrc = array.getResourceId(R.styleable.TextImageView_imageSrc,R.mipmap.help);
        imgSize = array.getDimensionPixelSize(R.styleable.TextImageView_imageSize,0);
        textSize = array.getDimensionPixelSize(R.styleable.TextImageView_textSize,0);
        init();
    }
    private void init() {
        this.setText(text);
        this.setTextColor(textColor);
        this.setTextSize(textSize);
        this.setImgResource(imageSrc);
        this.setImgSize(imgSize);
    }
    public void setImgResource(int resourceID) {
        if (resourceID == 0) {
            this.mImageView.setImageResource(0);
        } else {
            this.mImageView.setImageResource(resourceID);
        }
    }

    public void setImgResource(Drawable drawable) {
        this.mImageView.setImageDrawable(drawable);
    }
    public void setText(String text) {
        this.mTextView.setText(text);
    }

    private void setTextColor(int color) {
        if (color == 0) {
            this.mTextView.setTextColor(Color.BLACK);
        } else {
            this.mTextView.setTextColor(color);
        }
    }
    public void setTextSize(int textSize){
        if(textSize == 0){
            return;
        }
        this.mTextView.setTextSize(textSize);
    }

    public void setImgSize(int imgSize) {
        if(imgSize == 0)
            return;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize,
                imgSize);
        this.mImageView.setLayoutParams(params);

    }

}
