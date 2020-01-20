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
import com.pcare.common.entity.NetResponse;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.common.net.url.RetrofitUrlManager;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.GluTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.table.UserTableController;
import com.pcare.common.util.AudioTrackUtil;
import com.pcare.common.util.CommonUtil;
import com.pcare.common.util.LogUtil;
import com.pcare.common.view.CurveTrendChartView;
import com.pcare.common.websocket.WsManager;
import com.pcare.common.websocket.WsStatusListener;
import com.pcare.rebot.R;
import com.pcare.rebot.contract.UserListContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.ByteString;

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
//        getUser();
//        testGetBPMList();
//        testGetGluList();
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

    public void addGLU(View v) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(this, "输入用户ID", Toast.LENGTH_SHORT);
            return;
        }
        GluTableController.getInstance(getSelfActivity()).clear();
        GlucoseEntity entity = new GlucoseEntity();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 7, 16, 5, 00);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0049");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus(0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16, 6, 30);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0050");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus(0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16, 7, 40);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0069");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus(0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16, 10, 20);
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration("0.0049");
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus(0);
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16, 11, 33);
        entity.setTimeDate(calendar.getTime());
        entity.setGlucoseConcentration("0.0099");
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);

        entity = new GlucoseEntity();
        calendar.set(2019, 7, 16, 19, 30);
        entity.setTimeDate(calendar.getTime());
        entity.setGlucoseConcentration("0.0039");
        entity.setUserId(editText.getText().toString());
        GluTableController.getInstance(getSelfActivity()).insert(entity);
    }

    public void addBPM(View v) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(this, "输入用户ID", Toast.LENGTH_SHORT);
            return;
        }
        BPMTableController.getInstance(getApplicationContext()).clear();
        BPMEntity bpmEntity = new BPMEntity();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 7, 16, 7, 30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("123.0");
        bpmEntity.setDiastolicData("88.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 17, 20, 20);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("132.0");
        bpmEntity.setDiastolicData("87.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 18, 7, 20);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("111.0");
        bpmEntity.setDiastolicData("89.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 19, 9, 50);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("120.0");
        bpmEntity.setDiastolicData("80.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 21, 0, 0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("100.0");
        bpmEntity.setDiastolicData("85.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 23, 12, 0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("150.0");
        bpmEntity.setDiastolicData("90.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 27, 23, 0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("146.0");
        bpmEntity.setDiastolicData("80.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 28, 8, 9);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("103.0");
        bpmEntity.setDiastolicData("73.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 0, 0);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("105.0");
        bpmEntity.setDiastolicData("75.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 7, 30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("95.0");
        bpmEntity.setDiastolicData("65.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 8, 10);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("155.0");
        bpmEntity.setDiastolicData("75.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 8, 30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("120.0");
        bpmEntity.setDiastolicData("78.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 8, 31);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("102.0");
        bpmEntity.setDiastolicData("72.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 11, 30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("103.0");
        bpmEntity.setDiastolicData("73.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 16, 30);
        bpmEntity.setTimeData(calendar.getTime());
        bpmEntity.setBpmId(editText.getText().toString() + "-" + calendar.getTime());
        bpmEntity.setUserId(editText.getText().toString());
        bpmEntity.setSystolicData("105.0");
        bpmEntity.setDiastolicData("75.0");
        bpmEntity.setMeanAPData("80.0");
        bpmEntity.setPulseData("70.0");
        bpmEntity.setUnit(getString(R.string.bpm_unit_mmhg));
        BPMTableController.getInstance(getSelfActivity()).insert(bpmEntity);
        calendar.set(2019, 7, 31, 17, 30);
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

    private void testGetBPMList() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt("userId", UserDao.getCurrentUserId());
            RetrofitHelper.getInstance()
                    .getRetrofit()
                    .create(Api.class)
                    .getBPMList("query", object)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableSingleObserver<NetResponse>() {
                        @Override
                        public void onSuccess(NetResponse value) {
                            LogUtil.i(value.toString());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void testGetGluList() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt("userId", UserDao.getCurrentUserId());
            RetrofitHelper.getInstance()
                    .getRetrofit()
                    .create(Api.class)
                    .getGLUList("query", object)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableSingleObserver<NetResponse>() {
                        @Override
                        public void onSuccess(NetResponse value) {
                            LogUtil.i(value.toString());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    boolean tag;
    int position;
    SparseArray<String> array = new SparseArray<>();
    AudioTrackUtil util = new AudioTrackUtil();
    WsManager manager;
    String s = "感冒熟悉一下：感冒总体上分为普通感冒和流行性感冒，" +
            "在这里先讨论普通感冒。普通感冒，祖国医学称\"伤风\"，" +
            "是由多种病毒引起的一种呼吸道常见病，其中30%-50%是由某种血清型的鼻病毒引起，" +
            "普通感冒虽多发于初冬，但任何季节，如春天，夏天也可发生，不同季节的感冒的致病病毒并非完全一样。" +
            "流行性感冒，是由流感病毒引起的急性呼吸道传染病。" +
            "病毒存在于病人的呼吸道中，在病人咳嗽，打喷嚏时经飞沫传染给别人。" +
            "流感的传染性很强，由于这种病毒容易变异，即使是患过流感的人，当下次再遇上流感流行，他仍然会感染，所以流感容易引起暴发性流行。" +
            "一般在冬春季流行的机会较多，每次可能有20～40%的人会传染上流感。";
//        String s = "百日咳是由百日咳杆菌所致的急性呼吸道传染病。其特征为阵发性痉挛性咳嗽，咳嗽末伴有特殊的鸡鸣样吸气吼声。";

    AudioTrackUtil.OnState stateListener = new AudioTrackUtil.OnState() {
        @Override
        public void onStateChanged(AudioTrackUtil.WindState currentState) {
            LogUtil.i("aaa"+currentState.toString());
            if(currentState == AudioTrackUtil.WindState.STOP_PLAY){
                LogUtil.i("aaa"+array.size()+"array.size()"+position);
                if(position<=array.size()-1) {
                    util.startPlay(array.get(position));
                    position++;
                    return;
                }
                tag = true;
            }else if(currentState == AudioTrackUtil.WindState.PLAYING){
                tag = false;
            }
        }
    };

    public void testAudioTrack(View view) {
        tag = true;
        JSONObject object = new JSONObject();
        try {
            object.putOpt("text",s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WsStatusListener listener = new WsStatusListener() {
            @Override
            public void onOpen(Response response) {
                super.onOpen(response);
                LogUtil.i("onOpen"+response.toString());
                util.setOnStateListener(stateListener);
                manager.send(object.toString());
            }

            @Override
            public void onMessage(String text) {
                super.onMessage(text);
                array.append(array.size(),text);
                if(tag && position<=array.size()-1) {
                    util.startPlay(array.get(position));
                    position++;
                }
            }

            @Override
            public void onMessage(ByteString bytes) {
                super.onMessage(bytes);
                LogUtil.i("onMessage2"+bytes.base64());
            }

            @Override
            public void onClosing(int code, String reason) {
                super.onClosing(code, reason);
                LogUtil.i("onClosing"+reason);
            }

            @Override
            public void onClosed(String reason) {
                super.onClosed(reason);
                LogUtil.i("onClosed"+reason);
            }

            @Override
            public void onFailure(Throwable t, Response response) {
                super.onFailure(t, response);
                t.printStackTrace();
                LogUtil.i("onFailure"+response.toString());
            }

            @Override
            public void onReconnect() {
                super.onReconnect();
                LogUtil.i("onReconnect");
            }
        };

        manager = new WsManager.Builder()
                .client(new OkHttpClient().newBuilder()
                        .pingInterval(60, TimeUnit.SECONDS)//设置的60秒后断开连接
                        .retryOnConnectionFailure(true)
                        .build())
                .listener(listener)
                .url(Api.AUDIOURL+"/tts/")
                .build();
        manager.startConnect();
    }
}
