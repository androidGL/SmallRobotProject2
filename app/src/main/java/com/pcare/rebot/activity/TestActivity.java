package com.pcare.rebot.activity;

import android.content.Intent;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.view.CurveTrendChartView;
import com.pcare.rebot.R;

import java.util.ArrayList;
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
                .putExtra("type",0)
                .putExtra("userId", id));
    }

    public void verify(View view) {
        startActivity(new Intent(this, FaceActivity.class)
                .putExtra("type", 1));
    }

    @Override
    public void start() {
        super.start();
        testCurve();
    }

    private void testCurve(){
        CurveTrendChartView view = findViewById(R.id.curve_view);

        List<Double> list = new ArrayList<>();
        double base = 200.00;
        do{
            list.add(base);
            base -= 10;
        }while (base>=30.00);
//        list.add(0,1.00);
//        list.add(0,1.50);
//        list.add(0,2.00);
//        list.add(0,2.50);
//        list.add(0,3.00);
//        list.add(0,3.50);
        view.setYListData(list);

        List<Integer> list1 = new ArrayList<>();
        int base1 = 1;
        do{
            list1.add(base1);
            base1 += 1;
        }while (base1<=15);
        view.setXListData(list1);

        List<Double> list2 = new ArrayList<>();
        list2.add(110.00);
        list2.add(127.00);
        list2.add(145.00);
        list2.add(119.00);
        list2.add(144.00);
        view.setCurveListData(list2);

    }

    private void data(){
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
}
