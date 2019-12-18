package com.pcare.bloodglu;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.GluTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.util.CommonUtil;
import com.pcare.common.view.CurveTrendChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/6
 * @Description:
 */
public class GluTrendChartActivity extends BaseActivity {
    private TextView userName, userAge, userStature, userWeight;
    private CurveTrendChartView curveView;
    private TextView avgTimeView, avgGluView;
    private double avgGluNum = 0;
    private String startTime, endTime;
    private List<GlucoseEntity> glucoseEntities;

    @Override
    public int getLayoutId() {
        return R.layout.activity_glu_trend_chart;
    }

    @Override
    public void start() {
        super.start();

        glucoseEntities = GluTableController.getInstance(getSelfActivity()).searchAll();
        if (null == glucoseEntities || glucoseEntities.size() <= 0)
            return;

        List<Integer> list = new ArrayList<>();
        int base = 100;
        do {
            list.add(base);
            base -= 5;
        } while (base >= 30);

        //获取15天内的
        List<Integer> list1 = new ArrayList<>();
        int base1 = 5;
        do {
            list1.add(base1);
            base1 += 1;
        } while (base1 <= 20);


        Calendar calendar = Calendar.getInstance();
        List<CurveTrendChartView.Value> list3 = new ArrayList<>();
        for (GlucoseEntity entity : glucoseEntities) {
            calendar.setTime(entity.getTimeDate());
            list3.add(new CurveTrendChartView.Value(calendar.get(Calendar.HOUR_OF_DAY), (int) (Double.parseDouble(entity.getGlucoseConcentration()) * 10000)));
        }

        List<CurveTrendChartView.Value> list4 = new ArrayList<>();
        for (GlucoseEntity entity : glucoseEntities) {
            calendar.setTime(entity.getTimeDate());
            list4.add(new CurveTrendChartView.Value(calendar.get(Calendar.HOUR_OF_DAY), (int) (Double.parseDouble(entity.getGlucoseConcentration()) * 10000) - 10));
            avgGluNum += Double.parseDouble(entity.getGlucoseConcentration()) * 1000;
        }
        curveView = curveView.Builder(getSelfActivity())
                .setY("mmol/l", list, 10.00)
                .setX("时间", list1, 1.0)
                .addLine(curveView.Item(list3, Color.RED, "血糖"))
                .addBKG(new CurveTrendChartView.BkgItem(new CurveTrendChartView.Value(40, 60), Color.parseColor("#E0FFFF"), "正常"))
                .addBKG(new CurveTrendChartView.BkgItem(new CurveTrendChartView.Value(30, 35), Color.parseColor("#EEE685"), "异常情况1"))
                .addBKG(new CurveTrendChartView.BkgItem(new CurveTrendChartView.Value(70, 100), Color.parseColor("#EE7600"), "异常情况2"))
                .build();

        avgGluNum = avgGluNum / glucoseEntities.size();
        calendar.setTime(glucoseEntities.get(0).getTimeDate());
        startTime = "5";
        endTime = "20";
        avgTimeView.setText("时间段：" + CommonUtil.getDateStr(glucoseEntities.get(0).getTimeDate(), "yyyy-MM-dd") + "  "+startTime + " 时 至 " + endTime + "时");
        avgGluView.setText("血糖平均值：" + String.format("%.2f",avgGluNum));
    }

    @Override
    protected void initView() {
        super.initView();
        curveView = findViewById(R.id.glu_curve_view);
        userName = findViewById(R.id.chart_name);
        userAge = findViewById(R.id.chart_age);
        userStature = findViewById(R.id.chart_stature);
        userWeight = findViewById(R.id.chart_weight);
        avgTimeView = findViewById(R.id.chart_time);
        avgGluView = findViewById(R.id.chart_avg_glu);
    }

    @Override
    protected void initData() {
        super.initData();
        UserEntity user = UserDao.get(getSelfActivity()).getCurrentUser();
        userName.setText("姓名：" + user.getUserName());
        userAge.setText("年龄：" + (0 == user.getUserBirthYear() ? "无" : (Calendar.getInstance().get(Calendar.YEAR) - user.getUserBirthYear())));
        userStature.setText("身高：" + (TextUtils.isEmpty(user.getUserStature()) ? "无" : user.getUserStature()));
        userWeight.setText("体重：" + (TextUtils.isEmpty(user.getUserWeight()) ? "无" : user.getUserWeight()));
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }
}
