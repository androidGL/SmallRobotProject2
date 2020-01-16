/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.pcare.bloodglu;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.entity.GlucoseEntityDao;
import com.pcare.common.entity.NetResponse;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.common.oem.battery.BatteryManagerCallbacks;
import com.pcare.common.oem.battery.LoggableBleManager;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.GluTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.view.CommonAlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@Route(path = "/glu/main")
public class GlucoseActivity extends BleProfileExpandableListActivity implements BatteryManagerCallbacks {

    private GlucoseRecycleAdapter recycleAdapter;
    private GlucoseManager mGlucoseManager;
    private TextView mBatteryLevelView;
    private RelativeLayout gluBatteryView;
    private RecyclerView mRecyclerView;
    private List<GlucoseEntity> glucoseEntityList = new ArrayList<>();

    @Override
    protected void initView() {
        super.initView();
        if (!ensureBLEExists())
            finish();
        mBatteryLevelView = findViewById(R.id.battery);
        gluBatteryView = findViewById(R.id.glu_battery);
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getSelfActivity()));
    }

    @Override
    public void start() {
        super.start();
        glucoseEntityList = GluTableController.getInstance(getSelfActivity()).searchAll();
        recycleAdapter = new GlucoseRecycleAdapter(getSelfActivity(), glucoseEntityList);
        mRecyclerView.setAdapter(recycleAdapter);
        testHistory();
    }

    @Override
    protected LoggableBleManager<BatteryManagerCallbacks> initializeManager() {
        GlucoseManager manager = mGlucoseManager = GlucoseManager.getGlucoseManager(getApplicationContext()).setCallBack(entity -> {
            insertToNet(entity,true);
        });
        manager.setGattCallbacks(this);
        return manager;
    }
    private void insertToNet(GlucoseEntity entity,boolean isSave){
        RetrofitHelper.getInstance().getRetrofit()
                .create(Api.class)
                .insertGLU("insert",entity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableSingleObserver<NetResponse>() {

                    @Override
                    public void onSuccess(NetResponse value) {
                        if(value.getStatus() == 1 && isSave) {
                            glucoseEntityList.add(entity);
                            recycleAdapter.notifyDataSetChanged();
                            GluTableController.getInstance(getApplicationContext()).insertOrReplace(entity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected int getAboutTextId() {
        return R.string.gls_about_text;
    }

    @Override
    protected int getDefaultDeviceName() {
        return R.string.gls_default_name;
    }

    @Override
    protected UUID getFilterUUID() {
        return GlucoseManager.GLS_SERVICE_UUID;
    }

    @Override
    public void onBatteryLevelChanged(@NonNull final BluetoothDevice device, final int batteryLevel) {
        runOnUiThread(() -> {
            mBatteryLevelView.setText(GlucoseActivity.this.getString(R.string.battery, batteryLevel));
            gluBatteryView.setVisibility(View.VISIBLE);
        });
    }

    private boolean ensureBLEExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_feature_gls;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void refreshData(View view) {
        mGlucoseManager.refreshRecords();
    }

    public void testHistory() {
        List<GlucoseEntity> list = GluTableController.getInstance(getSelfActivity()).searchAll();
        for (GlucoseEntity entity : list) {
            Log.i("Table-----", entity.getGluId() + entity.toString());
        }
    }

    public void testNet(View v){
        EditText month,day,hour,min,value;
        month = findViewById(R.id.t_month);
        day = findViewById(R.id.t_date);
        hour = findViewById(R.id.t_hour);
        min = findViewById(R.id.t_min);
        value = findViewById(R.id.t_value);
        String monthS,dayS,hourS,minS,valueS;
        monthS = month.getText().toString();
        dayS = day.getText().toString();
        hourS = hour.getText().toString();
        minS = min.getText().toString();
        valueS = value.getText().toString();
        if(TextUtils.isEmpty(monthS) || TextUtils.isEmpty(dayS) || TextUtils.isEmpty(hourS) || TextUtils.isEmpty(minS) || TextUtils.isEmpty(valueS)){
            Toast.makeText(getApplicationContext(),"得输入完整信息测试呀~",Toast.LENGTH_SHORT).show();
        }
        GlucoseEntity entity = new GlucoseEntity();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Integer.valueOf(monthS), Integer.valueOf(dayS),Integer.valueOf(hourS),Integer.valueOf(minS));
        entity.setTimeDate(calendar.getTime());
        entity.setSequenceNumber(0);
        entity.setGlucoseConcentration(valueS);
        entity.setSampleType(0);
        entity.setSampleLocation(0);
        entity.setStatus( 0);
        entity.setUserId(UserDao.getCurrentUserId());
        insertToNet(entity,true);
    }

    public void deleteAll(View view) {
        CommonAlertDialog.Builder(getSelfActivity())
                .setMessage("确认清空记录？")
                .setOnConfirmClickListener(view1 -> {
                    GluTableController.getInstance(getSelfActivity()).clear();
                    glucoseEntityList.clear();
                    recycleAdapter.notifyDataSetChanged();
                }).build().shown();
    }

    public void toTrendChartActivity(View view) {
        startActivity(new Intent(GlucoseActivity.this,GluTrendChartActivity.class));
    }
}
