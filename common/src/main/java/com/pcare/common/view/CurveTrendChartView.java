package com.pcare.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pcare.common.R;
import com.pcare.common.util.CommonUtil;

import java.util.Date;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/5
 * @Description:
 */
public class CurveTrendChartView extends View {
    private Context mContext;

    //总宽高
    private int mWidth;
    private int mHeight;
    //Y轴宽高
    private int mYWidth = 100,mYHeight;
    //X轴宽高
    private int mXWidth,mXHeight = 50;

    //虚线的画笔
    private Paint mDottedLinePaint;
    private Path mDottedLinePath;
    private int mDottedLineColor = Color.BLUE;
    //曲线的画笔
    private Paint mCurvePaint;
    private int mCurveColor = Color.RED;
    //曲线圆点的画笔
    private Paint mPointPaint;
    private int mPointColor = Color.BLACK;
    private int pointSize = 8;

    //文字的画笔
    private Paint mTextPaint;
    private int mTextColor = Color.BLACK;
    private int mTextSize = 30;




    // Y轴的数据源
    private List<Double> mYDataList;
    private double minData,maxData;
    // Y轴数据的单位
    private String mYDataUnit = "mmHg";
    private float mYUnitHeight = 30;
    //y轴每个item的高度
    private float mYItemHeight;

    //X轴的数据源
    private List<Integer> mXDataList;
    //X轴数据的单位
    private String mXDataUnit = "日期";
    //X轴每个item的宽度
    private float mXItemWidth;

    //曲线的数据源
    private List<Double> curveLineDataList;


    public CurveTrendChartView(Context context) {
        this(context,null);
    }

    public CurveTrendChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public CurveTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.CurveTrendChartView);
        array.recycle();
        initPaint();
    }
    public void setYListData(List<Double> yListData){
        this.mYDataList = yListData;
        if(yListData.size()>0) {
            minData = yListData.get(yListData.size() - 1);
            maxData = yListData.get(0);
        }
        invalidate();
    }
    public void setXListData(List<Integer> xListData){
        this.mXDataList = xListData;
        invalidate();
    }
    public void setCurveListData(List<Double> curveListData){
        this.curveLineDataList = curveListData;
        invalidate();
    }

    private void initPaint(){
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setAntiAlias(true);//抗锯齿效果
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setColor(mDottedLineColor);
        mDottedLinePaint.setStrokeWidth(2);
        mDottedLinePath= new Path();

        mCurvePaint = new Paint();
        mCurvePaint.setAntiAlias(true);
        mCurvePaint.setStyle(Paint.Style.STROKE);
        mCurvePaint.setColor(mCurveColor);

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(mPointColor);
        mPointPaint.setStrokeWidth(pointSize);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initData(){
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mYHeight = mHeight - mXHeight;
        mXWidth = mWidth - mYWidth;
        mYItemHeight = (mHeight - mXHeight)/mYDataList.size();
        mXItemWidth = (mWidth - mYWidth - 70)/mXDataList.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDottedLine(canvas);
        drawYLineText(canvas);
        drawXLineText(canvas);
        drawCurceLine(canvas);
    }
    private void drawDottedLine(Canvas canvas){
        int count = mYDataList.size();
        if(count>0){
            canvas.save();
            //虚线效果：先画20的实线，再画10的空白，开始绘制的偏移值为4
            mDottedLinePaint.setPathEffect(new DashPathEffect(new float[]{20,10},4));
            mDottedLinePaint.setStrokeWidth(1f);
            float startY;
            for(int i = 0;i < count;i++){
                startY = i* mYItemHeight + mYItemHeight /2 + mYUnitHeight;
                mDottedLinePath.reset();
                mDottedLinePath.moveTo(mYWidth,startY);
                mDottedLinePath.lineTo(mWidth,startY);
                canvas.drawPath(mDottedLinePath,mDottedLinePaint);
            }
            canvas.restore();

        }
    }
    private void drawYLineText(Canvas canvas) {
        int count = mYDataList.size();
        if(count>0){
            canvas.save();
            float startY;
            float baseline;
            Paint.FontMetricsInt metrics =mTextPaint.getFontMetricsInt();
            for(int i = 0;i<count;i++){
                startY = (i+1)* mYItemHeight;
                String text = String.valueOf(mYDataList.get(i));
                baseline = (startY*2- mYItemHeight -metrics.bottom-metrics.top)/2 + mYUnitHeight;
                canvas.drawText(text,mYWidth/2,baseline,mTextPaint);
            }
            canvas.drawText(mYDataUnit,mYWidth/2,mTextSize,mTextPaint);
            canvas.restore();
        }
    }

    private void drawXLineText(Canvas canvas){
        int count = mXDataList.size();
        if(count > 0){
            canvas.save();
            float startX;
            for(int i = 0;i<count;i++){
                startX = mYWidth + i * mXItemWidth +mXItemWidth/2 ;
                canvas.drawText(String.valueOf((mXDataList.get(i))),startX,mHeight,mTextPaint);
            }
            canvas.drawText("日期",mWidth - mXItemWidth/2,mHeight,mTextPaint);
            canvas.restore();
        }
    }

    private void drawCurceLine(Canvas canvas){
        int count = curveLineDataList.size();
        if(count>0){
            canvas.save();
            mDottedLinePath.reset();
            float stopX,stopY;
            float baseHeight = mYItemHeight /2 + mYUnitHeight-pointSize/2+1;
            float totalHeight = mHeight - mXHeight- mYItemHeight;
            mDottedLinePath.moveTo(mXItemWidth/2+mYWidth, (float) (totalHeight*((curveLineDataList.get(0) - maxData)/(minData-maxData)))+ baseHeight);
            canvas.drawPoint(mXItemWidth/2+mYWidth, (float) (totalHeight*((curveLineDataList.get(0) - maxData)/(minData-maxData)))+ baseHeight,mPointPaint);
            for(int i = 1;i<count;i++){
                stopX = mYWidth+i*mXItemWidth + mXItemWidth/2;
                stopY = (float) (totalHeight*((curveLineDataList.get(i) - maxData)/(minData-maxData)) + baseHeight);
                mDottedLinePath.lineTo(stopX,stopY);
                canvas.drawPoint(stopX,stopY,mPointPaint);
            }
            canvas.drawPath(mDottedLinePath,mCurvePaint);

            canvas.restore();
        }
    }
    private class CurveTrendLine{
        int mLineColor;//线的颜色
        int mPointColor;//点的颜色
        List<Double> mDataList;
    }


}
