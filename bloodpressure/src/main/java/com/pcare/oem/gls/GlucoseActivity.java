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
package com.pcare.oem.gls;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.util.SparseArray;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pcare.bloodpressure.R;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.table.GluTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.view.UserListDialog;
import com.pcare.oem.profile.BleProfileExpandableListActivity;
import com.pcare.oem.profile.LoggableBleManager;

@Route(path = "/glu/main")
public class GlucoseActivity extends BleProfileExpandableListActivity implements  GlucoseManagerCallbacks {

	private BaseExpandableListAdapter mAdapter;
	private GlucoseManager mGlucoseManager;
	private TextView mBatteryLevelView;
	private RelativeLayout gluBatteryView;


	@Override
	protected void onCreateView(final Bundle savedInstanceState) {
		if (!ensureBLEExists())
			finish();
		setGUI();
	}

	private void setGUI() {
		mBatteryLevelView = findViewById(R.id.battery);
		gluBatteryView = findViewById(R.id.glu_battery);

		setListAdapter(mAdapter = new ExpandableRecordAdapter(this, mGlucoseManager));
        testHistory();
	}

	@Override
	protected LoggableBleManager<GlucoseManagerCallbacks> initializeManager() {
		GlucoseManager manager = mGlucoseManager = GlucoseManager.getGlucoseManager(getApplicationContext());
		manager.setGattCallbacks(this);
		Log.i("aaaaaaaaaaaaaaa","initializeManager:");
		return manager;
	}

	@Override
	protected int getLoggerProfileTitle() {
		return R.string.gls_feature_title;
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
	protected void setDefaultUI() {
		mGlucoseManager.clear();
		mBatteryLevelView.setText(R.string.not_available);
	}

	@Override
	public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
		super.onDeviceDisconnected(device);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBatteryLevelView.setText(R.string.not_available);
				gluBatteryView.setVisibility(View.GONE);
				Log.i("aaaaaaaaaaaaaaa","onDeviceDisconnected:");
			}
		});
	}

	@Override
	public void onOperationStarted(final BluetoothDevice device) {
		Log.i("aaaaaaaaaaaaaaa","onOperationStarted:");
	}

	@Override
	public void onOperationCompleted(final BluetoothDevice device) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i("aaaaaaaaaaaaaaa","onOperationCompleted:");
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onOperationFailed(BluetoothDevice device) {
		Log.i("aaaaaaaaaaaaaaa","onOperationFailed:");
	}

	@Override
	public void onOperationAborted(final BluetoothDevice device) {
		Log.i("aaaaaaaaaaaaaaa","onOperationAborted:");
	}

	@Override
	public void onOperationNotSupported(final BluetoothDevice device) {
		showToast(R.string.gls_operation_not_supported);
	}
	@Override
	public void onDatasetChanged(final BluetoothDevice device) {
		// Do nothing. Refreshing the list is done in onOperationCompleted
		Log.i("aaaaaaaaaaaaaaa","onDatasetChanged:"+device.getAddress()+device.getName());
	}

	@Override
	public void onNumberOfRecordsRequested(final BluetoothDevice device, final int value) {
		if (value == 0)
			showToast(R.string.gls_progress_zero);
		else
			showToast(getResources().getQuantityString(R.plurals.gls_progress, value, value));
	}

	@Override
	public void onBatteryLevelChanged(@NonNull final BluetoothDevice device, final int batteryLevel) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i("aaaaaaaaaaaaaaa","onBatteryLevelChanged:"+batteryLevel);
				mBatteryLevelView.setText(GlucoseActivity.this.getString(R.string.battery, batteryLevel));
				gluBatteryView.setVisibility(View.VISIBLE);
					mGlucoseManager.getAllRecords();

			}
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


	public void back(View v){
		finish();
	}


	public void refreshData(View view) {
		mGlucoseManager.refreshRecords();
	}

	public void testHistory(){
        List<GlucoseEntity> list = GluTableController.getInstance(getSelfActivity()).searchAll();
        for (GlucoseEntity entity : list){
            Log.i("Table-----",entity.toString());
        }
    }
}
