package com.pcare.rebot.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.BPMEntity;
import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.GluTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.table.UserTableController;
import com.pcare.common.util.CommonUtil;
import com.pcare.common.view.CurveTrendChartView;
import com.pcare.rebot.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

/**
 * @Author: gl
 * @CreateDate: 2019/11/29
 * @Description:
 */
public class TestActivity extends BaseActivity {
    private EditText editText;
    private String id;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initView() {
        super.initView();
        editText = findViewById(R.id.user_id);
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void detect(View view) {
        id = editText.getText().toString();
        startActivity(new Intent(this, FaceActivity.class)
                .putExtra("type", 0)
                .putExtra("userId", id));
    }

    public void verify(View view) {
        startActivity(new Intent(this, FaceActivity.class)
                .putExtra("type", 1));
    }

    @Override
    public void start() {
        super.start();
//        testCurve();
        getUser();

    }

    private void testCurve() {

        List<Double> list = new ArrayList<>();
        double base = 200.00;
        do {
            list.add(base);
            base -= 10;
        } while (base >= 30.00);
//        list.add(0,1.00);
//        list.add(0,1.50);
//        list.add(0,2.00);
//        list.add(0,2.50);
//        list.add(0,3.00);
//        list.add(0,3.50);
//        view.setYListData(list, "mmHg");
//
//        List<Integer> list1 = new ArrayList<>();
//        int base1 = 1;
//        do {
//            list1.add(base1);
//            base1 += 1;
//        } while (base1 <= 15);
//        view.setXListData(list1, "日期");
//
//        List<Double> list2 = new ArrayList<>();
//        list2.add(110.00);
//        list2.add(127.00);
//        list2.add(145.00);
//        list2.add(119.00);
//        list2.add(144.00);
//        view.setCurveListData(list2);

    }

    private void data() {
        Collection collection;//接口继承Iterable接口
        Iterable iterable;//iterable.iterator().
        List list;
        ArrayList arrayList;
        LinkedList linkedList;
        Vector vector;
        Stack stack;

        Set set;
        HashSet hashSet;
        SortedSet sortedSet;
        TreeSet treeSet;

        Queue queue;
        PriorityQueue priorityQueue;

        Map map;
        SortedMap sortedMap;
        TreeMap treeMap;
        Hashtable hashtable;
        HashMap hashMap;
        LinkedHashMap linkedHashMap;
        WeakHashMap weakHashMap;
    }

    public void addGLU(View v){
        if(TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(this, "输入用户ID", Toast.LENGTH_SHORT);
            return;
        }
        GluTableController.getInstance(getSelfActivity()).clear();
        GlucoseEntity entity = new GlucoseEntity();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 7, 16,5,00);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0049");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus( 0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16,6,30);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0050");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus( 0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16,7,40);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0069");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus( 0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16,10,20);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0049");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus( 0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16,11,33);
        entity.setTimeDate(calendar.getTime());
        entity.setGlucoseConcentration("0.0099");
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16,19,30);
        entity.setTimeDate(calendar.getTime());
        entity.setGlucoseConcentration("0.0039");
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);
    }

    public void addBPM(View v) {
        if(TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(this, "输入用户ID", Toast.LENGTH_SHORT);
            return;
        }
        BPMTableController.getInstance(getApplicationContext()).clear();
        BPMEntity bpmEntity = new BPMEntity();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 7, 16,7,30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("123.0");
        bpmEntity.setDiastolicData("88.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 17,20,20);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("132.0");
        bpmEntity.setDiastolicData("87.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 18,7,20);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("111.0");
        bpmEntity.setDiastolicData("89.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 19,9,50);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("120.0");
        bpmEntity.setDiastolicData("80.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 21,0,0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("100.0");
        bpmEntity.setDiastolicData("85.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 23,12,0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("150.0");
        bpmEntity.setDiastolicData("90.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 27,23,0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("146.0");
        bpmEntity.setDiastolicData("80.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 28,8,9);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("103.0");
        bpmEntity.setDiastolicData("73.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,0,0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("105.0");
        bpmEntity.setDiastolicData("75.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,7,30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("95.0");
        bpmEntity.setDiastolicData("65.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,8,10);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("155.0");
        bpmEntity.setDiastolicData("75.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,8,30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("120.0");
        bpmEntity.setDiastolicData("78.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,8,31);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("102.0");
        bpmEntity.setDiastolicData("72.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,11,30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("103.0");
        bpmEntity.setDiastolicData("73.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,16,30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("105.0");
        bpmEntity.setDiastolicData("75.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31,17,30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("100.0");
        bpmEntity.setDiastolicData("70.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);

    }

    public void getUser() {
        List<UserEntity> list = UserTableController.getInstance(getApplicationContext()).searchAll();
        if (list.size() > 0) {
            for (UserEntity entity : list) {
                Log.i("getUser", entity.toString());
            }
        }


        List<BPMEntity> list2 = BPMTableController.getInstance(getApplicationContext()).searchAll();
        if (list.size() > 0) {
            for (BPMEntity entity : list2) {
                Log.i("getBPMEntity", entity.toString());
            }
        }


        List<GlucoseEntity> list3 = GluTableController.getInstance(getApplicationContext()).searchAll();
        if (list.size() > 0) {
            for (GlucoseEntity entity : list3) {
                Log.i("getGlucoseEntity", entity.toString());
            }
        }
    }
}
