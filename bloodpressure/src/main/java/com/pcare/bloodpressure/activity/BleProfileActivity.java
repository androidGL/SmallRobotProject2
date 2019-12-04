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
package com.pcare.bloodpressure.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;


import com.pcare.bloodpressure.R;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.oem.battery.LoggableBleManager;
import com.pcare.common.oem.scanner.ExtendedBluetoothDevice;
import com.pcare.common.oem.scanner.ScannerUtil;

import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.log.ILogSession;
import no.nordicsemi.android.log.LocalLogSession;
import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;


public abstract class BleProfileActivity extends BaseActivity implements BleManagerCallbacks {
	private static final String TAG = "BaseProfileActivity";

	private static final String SIS_CONNECTION_STATUS = "connection_status";
	private static final String SIS_DEVICE_NAME = "device_name";
	protected static final int REQUEST_ENABLE_BT = 2;

	private LoggableBleManager<? extends BleManagerCallbacks> mBleManager;

	private TextView mDeviceNameView;
	private Button mConnectButton;
	private ILogSession mLogSession;

	private boolean mDeviceConnected = false;
	private String mDeviceName;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ensureBLESupported();
		if (!isBLEEnabled()) {
			showBLEDialog();
		}
		initView();
		mBleManager = initializeManager();
		setUpView();
	}



	/**
	 * Called after the view and the toolbar has been created.
	 */
	protected final void setUpView() {
		// set GUI
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mConnectButton = findViewById(R.id.action_connect);
		mDeviceNameView = findViewById(R.id.device_name);
	}

	@Override
	public void onBackPressed() {
		mBleManager.disconnect().enqueue();
		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SIS_CONNECTION_STATUS, mDeviceConnected);
		outState.putString(SIS_DEVICE_NAME, mDeviceName);
	}

	@Override
	protected void onRestoreInstanceState(final @NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mDeviceConnected = savedInstanceState.getBoolean(SIS_CONNECTION_STATUS);
		mDeviceName = savedInstanceState.getString(SIS_DEVICE_NAME);

		if (mDeviceConnected) {
			mConnectButton.setText(R.string.action_disconnect);
		} else {
			mConnectButton.setText(R.string.action_connect);
		}
	}


	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return true;
	}

	/**
	 * Called when user press CONNECT or DISCONNECT button. See layout files -> onClick attribute.
	 */
	public void onConnectClicked(final View view) {
		if (isBLEEnabled()) {
			if (!mDeviceConnected) {
				setDefaultUI();
				showDeviceScanningDialog(getFilterUUID());
			} else {
				mBleManager.disconnect().enqueue();
			}
		} else {
			showBLEDialog();
		}
	}
	/**
	 * Shows the scanner fragment.
	 *
	 * @param filter               the UUID filter used to filter out available devices. The fragment will always show all bonded devices as there is no information about their
	 *                             services
	 * @see #getFilterUUID()
	 */
	private void showDeviceScanningDialog(final UUID filter) {
		runOnUiThread(() -> {
			ScannerUtil.getInstance().onCreate(getApplicationContext(),filter,scanCallback);
//			final ScannerFragment dialog = ScannerFragment.getInstance();
//			dialog.show(getSupportFragmentManager(),"scan_fragment");
		});
	}
	private ScanCallback scanCallback = new ScanCallback() {
		@Override
		public void onScanResult(final int callbackType, final ScanResult result) {
			// do nothing
		}

		@Override
		public void onBatchScanResults(final List<ScanResult> results) {
			if(results.size()>0){
				final ExtendedBluetoothDevice d = new ExtendedBluetoothDevice(results.get(0));
				onDeviceSelected(d.device,d.name);
			}
//			for(ScanResult result : results){
//				if("C2:18:08:06:2D:46".equals(result.getDevice().getAddress())){
//					final ExtendedBluetoothDevice d = new ExtendedBluetoothDevice(result);
//					onDeviceSelected(d.device,d.name);
//				}
//			}
		}

		@Override
		public void onScanFailed(final int errorCode) {
			// should never be called
		}
	};


	/**
	 * Returns the title resource id that will be used to create logger session. If 0 is returned (default) logger will not be used.
	 *
	 * @return the title resource id
	 */
	protected int getLoggerProfileTitle() {
		return 0;
	}

	/**
	 * This method may return the local log content provider authority if local log sessions are supported.
	 *
	 * @return local log session content provider URI
	 */
	protected Uri getLocalAuthorityLogger() {
		return null;
	}

	/**
	 * This method returns whether autoConnect option should be used.
	 *
	 * @return true to use autoConnect feature, false (default) otherwise.
	 */
	protected boolean shouldAutoConnect() {
		return false;
	}

	public void onDeviceSelected(final BluetoothDevice device, final String name) {
		final int titleId = getLoggerProfileTitle();
		if (titleId > 0) {
			mLogSession = Logger.newSession(getApplicationContext(), getString(titleId), device.getAddress(), name);
			// If nRF Logger is not installed we may want to use local logger
			if (mLogSession == null && getLocalAuthorityLogger() != null) {
				mLogSession = LocalLogSession.newSession(getApplicationContext(), getLocalAuthorityLogger(), device.getAddress(), name);
			}
		}
		mDeviceName = name;
		mBleManager.setLogger(mLogSession);
		mBleManager.connect(device)
				.useAutoConnect(shouldAutoConnect())
				.retry(3, 100)
				.enqueue();
	}

	@Override
	public void onDeviceConnecting(@NonNull final BluetoothDevice device) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mDeviceNameView.setText(mDeviceName != null ? mDeviceName : BleProfileActivity.this.getString(R.string.not_available));
				mConnectButton.setText(R.string.action_connecting);
			}
		});
	}

	@Override
	public void onDeviceConnected(@NonNull final BluetoothDevice device) {
		mDeviceConnected = true;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectButton.setText(R.string.action_disconnect);
			}
		});
	}

	@Override
	public void onDeviceDisconnecting(@NonNull final BluetoothDevice device) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectButton.setText(R.string.action_disconnecting);
			}
		});
	}

	@Override
	public void onDeviceDisconnected(@NonNull final BluetoothDevice device) {
		mDeviceConnected = false;
		mBleManager.close();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectButton.setText(R.string.action_connect);
				mDeviceNameView.setText(BleProfileActivity.this.getDefaultDeviceName());
			}
		});
	}

	@Override
	public void onLinkLossOccurred(@NonNull final BluetoothDevice device) {
		mDeviceConnected = false;
	}

	@Override
	public void onServicesDiscovered(@NonNull final BluetoothDevice device, boolean optionalServicesFound) {
		// this may notify user or show some views
	}

	@Override
	public void onDeviceReady(@NonNull final BluetoothDevice device) {
		// empty default implementation
	}

	@Override
	public void onBondingRequired(@NonNull final BluetoothDevice device) {
		showToast(R.string.bonding);
	}

	@Override
	public void onBonded(@NonNull final BluetoothDevice device) {
		showToast(R.string.bonded);
	}

	@Override
	public void onBondingFailed(@NonNull final BluetoothDevice device) {
		showToast(R.string.bonding_failed);
	}

	@Override
	public void onError(@NonNull final BluetoothDevice device, @NonNull final String message, final int errorCode) {
		Log.i(TAG, "Error occurred: " + message + ",  error code: " + errorCode);
		showToast(message + " (" + errorCode + ")");
	}

	@Override
	public void onDeviceNotSupported(@NonNull final BluetoothDevice device) {
		showToast(R.string.not_supported);
	}

	/**
	 * Shows a message as a Toast notification. This method is thread safe, you can call it from any thread
	 *
	 * @param message a message to be shown
	 */
	protected void showToast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BleProfileActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Shows a message as a Toast notification. This method is thread safe, you can call it from any thread
	 *
	 * @param messageResId an resource id of the message to be shown
	 */
	protected void showToast(final int messageResId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BleProfileActivity.this, messageResId, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Initializes the Bluetooth Low Energy manager. A manager is used to communicate with profile's services.
	 *
	 * @return the manager that was created
	 */
	protected abstract LoggableBleManager<? extends BleManagerCallbacks> initializeManager();

	/**
	 * Restores the default UI before reconnecting
	 */
	protected abstract void setDefaultUI();

	/**
	 * Returns the default device name resource id. The real device name is obtained when connecting to the device. This one is used when device has
	 * disconnected.
	 *
	 * @return the default device name resource id
	 */
	protected abstract int getDefaultDeviceName();

	/**
	 * The UUID filter is used to filter out available devices that does not have such UUID in their advertisement packet. See also:
	 * {@link #isChangingConfigurations()}.
	 *
	 * @return the required UUID or <code>null</code>
	 */
	protected abstract UUID getFilterUUID();


	private void ensureBLESupported() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	protected boolean isBLEEnabled() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		final BluetoothAdapter adapter = bluetoothManager.getAdapter();
		return adapter != null && adapter.isEnabled();
	}

	protected void showBLEDialog() {
		final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	}
	public void back(View v){
		finish();
	}
}
