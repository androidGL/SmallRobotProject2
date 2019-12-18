package com.pcare.bloodpressure.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.pcare.bloodpressure.R;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.BPMEntity;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.util.CommonUtil;
import com.pcare.common.view.CurveTrendChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/6
 * @Description: 血压趋势图
 */
public class BPMTrendChartActivity extends BaseActivity {
    private CurveTrendChartView curveView;
    private TextView userName, userAge, userStature, userWeight;
    private TextView avgTimeView, avgSystolicView, avgDiastolicView, avgResultView;
    private double avgSystolicNum = 0, avgDiastolicNum = 0;
    private String startTime, endTime, unit;
    private List<BPMEntity> bpmEntities;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bpm_trend_chart;
    }

    @Override
    public void start() {
        super.start();
        drawChart();
        if (null != bpmEntities && bpmEntities.size() > 0) {
            avgSystolicNum = avgSystolicNum / bpmEntities.size();
            avgDiastolicNum = avgDiastolicNum / bpmEntities.size();
            startTime = CommonUtil.getDateStr(bpmEntities.get(0).getTimeData(), "yyyy-MM-dd");
            endTime = CommonUtil.getDateStr(bpmEntities.get(bpmEntities.size() - 1).getTimeData(), "yyyy-MM-dd");
            unit = bpmEntities.get(0).getUnit();
        }
        avgTimeView.setText("时间段：" + startTime + " 至 " + endTime);
        avgSystolicView.setText("高压平均值：" + avgSystolicNum);
        avgDiastolicView.setText("低压平均值：" + avgDiastolicNum);
    }

    private void drawChart(){
        //获取Y轴坐标点
        List<Integer> list = new ArrayList<>();
        int base = 200;
        do {
            list.add(base*100);
            base -= 10;
        } while (base >= 30);
        //获取15天内的日期
        List<Integer> list1 = new ArrayList<>();
        int base1 = 16;
        do {
            list1.add(base1);
            base1 += 1;
        } while (base1 <= 31);

        //绘制坐标点
        List<CurveTrendChartView.Value> list3 = new ArrayList<>();
        List<CurveTrendChartView.Value> list4 = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        bpmEntities = BPMTableController.getInstance(getSelfActivity()).searchAll();
        for (BPMEntity entity : bpmEntities) {
            calendar.setTime(entity.getTimeData());
            double x = calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR_OF_DAY) / 60.0;
            list3.add(new CurveTrendChartView.Value((int)x, (int) (Double.parseDouble(entity.getSystolicData())*100)));
            list4.add(new CurveTrendChartView.Value((int)x, (int) (Double.parseDouble(entity.getDiastolicData())*100)));
            avgSystolicNum += Double.parseDouble(entity.getSystolicData());
            avgDiastolicNum += Double.parseDouble(entity.getDiastolicData());
            Log.i("aaa", entity.toString());
        }

        curveView = curveView.Builder(getSelfActivity())
                .setY("mmHg",list,100.00)
                .setX("时间",list1,1)
                .addLine(curveView.Item(list3, Color.parseColor("#006400"),"低压"))
                .addLine(curveView.Item(list4,Color.RED,"高压"))
                .addBKG(new CurveTrendChartView.BkgItem(new CurveTrendChartView.Value(8000,9000), Color.parseColor("#FFF5EE"),"低压正常范围"))
                .addBKG(new CurveTrendChartView.BkgItem(new CurveTrendChartView.Value(12000,14000), Color.parseColor("#E0FFFF"),"高压正常范围"))
                .build();
    }

    @Override
    protected void initView() {
        super.initView();
        curveView = findViewById(R.id.glu_curve_view);
        userName = findViewById(R.id.chart_name);
        userAge = findViewById(R.id.chart_age);
        userStature = findViewById(R.id.chart_stature);
        userWeight = findViewById(R.id.chart_weight);
        avgSystolicView = findViewById(R.id.chart_avg_systolic);
        avgDiastolicView = findViewById(R.id.chart_avg_diastolic);
        avgResultView = findViewById(R.id.chart_avg_result);
        avgTimeView = findViewById(R.id.chart_time);
    }

    @Override
    protected void initData() {
        UserEntity user = UserDao.get(getSelfActivity()).getCurrentUser();
        userName.setText("姓名：" + user.getUserName());
        userAge.setText("年龄：" + (0 == user.getUserBirthYear() ? "无" : (Calendar.getInstance().get(Calendar.YEAR) - user.getUserBirthYear())));
        userStature.setText("身高：" + (TextUtils.isEmpty(user.getUserStature()) ? "无" : user.getUserStature()));
        userWeight.setText("体重：" + (TextUtils.isEmpty(user.getUserWeight()) ? "无" : user.getUserWeight()));
        super.initData();
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }
}
