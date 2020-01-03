package com.pcare.rebot.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.camera2.CameraManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashSet;

/**
 * @Author: gl
 * @CreateDate: 2019/12/31
 * @Description:
 */
public class FaceFinderView extends View {
    //刷新界面的时间
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    //四个绿色边角对应的长度
    private int ScreenRate;
    //四个绿色边角对应的宽度
    private static final int CORNER_WIDTH = 10;

    //扫描框中中间线的宽度
    private static final int MiDDLE_LINE_WIDTH = 6;

    //扫描框中的中间线与扫描框左右的间隙
    private static final int MIDDLE_LINE_PADDING = 5;

    //中间那条线每次刷新移动的距离
    private static final int SPEEN_DISTANCE = 5;

    //手机的屏幕密度
    private static float density;

    //画笔对象的引用
    private Paint paint;

    //中间滑动线的最顶端位置
    private int slideTop;
    //中间滑动线的最底端位置
    private int slideBottom;


    public FaceFinderView(Context context) {
        super(context);
    }

    public FaceFinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FaceFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;
        //像素转化成dp
        ScreenRate = (int) (20 * density);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //获取屏幕的宽和高
        int width = canvas.getWidth();
        int left = 100,top = 100,right = width-100,bottom = width-100;


        //画扫描框边上的角，总共8个部分
        paint.setColor(Color.GREEN);
        canvas.drawRect(left, top, left + ScreenRate,
                top + CORNER_WIDTH, paint);
        canvas.drawRect(left, top, left + CORNER_WIDTH, top
                + ScreenRate, paint);
        canvas.drawRect(right - ScreenRate, top, right,
                top + CORNER_WIDTH, paint);
        canvas.drawRect(right - CORNER_WIDTH, top, right, top
                + ScreenRate, paint);
        canvas.drawRect(left, bottom - CORNER_WIDTH, left
                + ScreenRate, bottom, paint);
        canvas.drawRect(left, bottom - ScreenRate,
                left + CORNER_WIDTH, bottom, paint);
        canvas.drawRect(right - ScreenRate, bottom - CORNER_WIDTH,
                right, bottom, paint);
        canvas.drawRect(right - CORNER_WIDTH, bottom - ScreenRate,
                right, bottom, paint);


        //绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
        slideTop += SPEEN_DISTANCE;
        if (slideTop >= bottom) {
            slideTop = top;
        }
        canvas.drawRect(left + MIDDLE_LINE_PADDING, slideTop - MiDDLE_LINE_WIDTH / 2, right - MIDDLE_LINE_PADDING, slideTop + MiDDLE_LINE_WIDTH / 2, paint);


        //只刷新扫描框的内容，其他地方不刷新
        postInvalidateDelayed(ANIMATION_DELAY, left, top,
                right, bottom);

    }
}
