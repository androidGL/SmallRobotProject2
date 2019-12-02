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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import java.util.Calendar;
import java.util.UUID;

import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.common.callback.RecordAccessControlPointDataCallback;
import no.nordicsemi.android.ble.common.callback.glucose.GlucoseMeasurementDataCallback;
import no.nordicsemi.android.ble.common.data.RecordAccessControlPointData;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.log.LogContract;

import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.table.GluTableController;
import com.pcare.oem.battery.BatteryManager;
import com.pcare.oem.parser.GlucoseMeasurementParser;
import com.pcare.oem.parser.RecordAccessControlPointParser;

@SuppressWarnings("unused")
public class GlucoseManager extends BatteryManager<GlucoseManagerCallbacks> {
	private static final String TAG = "GlucoseManager";

	/** Glucose service UUID */
	public final static UUID GLS_SERVICE_UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
	/** Glucose Measurement characteristic UUID */
	private final static UUID GM_CHARACTERISTIC = UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb");
	/** Record Access Control Point characteristic UUID */
	private final static UUID RACP_CHARACTERISTIC = UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb");

	private BluetoothGattCharacteristic mGlucoseMeasurementCharacteristic;
	private BluetoothGattCharacteristic mRecordAccessControlPointCharacteristic;

	private final SparseArray<GlucoseEntity> mRecords = new SparseArray<>();
	private Handler mHandler;
	private static GlucoseManager mInstance;
	private Context mContext;

	/**
	 * Returns the singleton implementation of GlucoseManager.
	 */
	public static GlucoseManager getGlucoseManager(final Context context) {
		if (mInstance == null)
			mInstance = new GlucoseManager(context);
		return mInstance;
	}

	private GlucoseManager(final Context context) {
		super(context);
		this.mContext = context;
		mHandler = new Handler();
	}

	@NonNull
	@Override
	protected BatteryManagerGattCallback getGattCallback() {
		return mGattCallback;
	}

	/**
	 * BluetoothGatt callbacks for connection/disconnection, service discovery,
	 * receiving notification, etc.
	 */
	private final BatteryManagerGattCallback mGattCallback = new BatteryManagerGattCallback() {

		@Override
		protected void initialize() {
			super.initialize();

			setNotificationCallback(mGlucoseMeasurementCharacteristic)
					.with(new GlucoseMeasurementDataCallback() {
						@Override
						public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
							log(LogContract.Log.Level.APPLICATION, "\"" + GlucoseMeasurementParser.parse(data) + "\" received");
							super.onDataReceived(device, data);
						}

						@Override
						public void onGlucoseMeasurementReceived(@NonNull final BluetoothDevice device, final int sequenceNumber,
																 @NonNull final Calendar time, @Nullable final Float glucoseConcentration,
																 @Nullable final Integer unit, @Nullable final Integer type,
																 @Nullable final Integer sampleLocation, @Nullable final GlucoseStatus status,
																 final boolean contextInformationFollows) {
							final GlucoseEntity record = new GlucoseEntity();
							record.setSequenceNumber(sequenceNumber);
							record.setTimeDate(time.getTime());
							record.setGlucoseConcentration(glucoseConcentration != null ? glucoseConcentration : 0);
							record.setSampleType(type != null ? type : 0);
							record.setSampleLocation(sampleLocation != null ? sampleLocation : 0);
							record.setStatus( status != null ? status.value : 0);

							GluTableController.getInstance(mContext).insertOrReplace(record);

							// insert the new record to storage
							mRecords.put(record.getSequenceNumber(), record);
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									// if there is no context information following the measurement data,
									// notify callback about the new record
									if (!contextInformationFollows)
										mCallbacks.onDatasetChanged(device);
								}
							});
						}
					});

			setIndicationCallback(mRecordAccessControlPointCharacteristic)
					.with(new RecordAccessControlPointDataCallback() {
						@Override
						public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
							log(LogContract.Log.Level.APPLICATION, "\"" + RecordAccessControlPointParser.parse(data) + "\" received");
							super.onDataReceived(device, data);
						}

						@Override
						public void onRecordAccessOperationCompleted(@NonNull final BluetoothDevice device, final int requestCode) {
							switch (requestCode) {
								case RACP_OP_CODE_ABORT_OPERATION:
									mCallbacks.onOperationAborted(device);
									break;
								default:
									mCallbacks.onOperationCompleted(device);
									break;
							}
						}

						@Override
						public void onRecordAccessOperationCompletedWithNoRecordsFound(@NonNull final BluetoothDevice device, final int requestCode) {
							mCallbacks.onOperationCompleted(device);
						}

						@Override
						public void onNumberOfRecordsReceived(@NonNull final BluetoothDevice device, final int numberOfRecords) {
							mCallbacks.onNumberOfRecordsRequested(device, numberOfRecords);
							if (numberOfRecords > 0) {
								if (mRecords.size() > 0) {
									final int sequenceNumber = mRecords.keyAt(mRecords.size() - 1) + 1;
									writeCharacteristic(mRecordAccessControlPointCharacteristic,
											RecordAccessControlPointData.reportStoredRecordsGreaterThenOrEqualTo(sequenceNumber))
											.enqueue();
								} else {
									writeCharacteristic(mRecordAccessControlPointCharacteristic,
											RecordAccessControlPointData.reportAllStoredRecords())
											.enqueue();
								}
							} else {
								mCallbacks.onOperationCompleted(device);
							}
						}

						@Override
						public void onRecordAccessOperationError(@NonNull final BluetoothDevice device,
																 final int requestCode, final int errorCode) {
							log(Log.WARN, "Record Access operation failed (error " + errorCode + ")");
							if (errorCode == RACP_ERROR_OP_CODE_NOT_SUPPORTED) {
								mCallbacks.onOperationNotSupported(device);
							} else {
								mCallbacks.onOperationFailed(device);
							}
						}
					});

			enableNotifications(mGlucoseMeasurementCharacteristic).enqueue();
			enableIndications(mRecordAccessControlPointCharacteristic)
					.fail(new FailCallback() {
						@Override
						public void onRequestFailed(@NonNull BluetoothDevice device, int status) {
							log(Log.WARN, "Failed to enabled Record Access Control Point indications (error " + status + ")");
						}
					})
					.enqueue();
		}

		@Override
		public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
			final BluetoothGattService service = gatt.getService(GLS_SERVICE_UUID);
			if (service != null) {
				mGlucoseMeasurementCharacteristic = service.getCharacteristic(GM_CHARACTERISTIC);
				mRecordAccessControlPointCharacteristic = service.getCharacteristic(RACP_CHARACTERISTIC);
			}
			return mGlucoseMeasurementCharacteristic != null && mRecordAccessControlPointCharacteristic != null;
		}

		@Override
		protected void onDeviceDisconnected() {
			mGlucoseMeasurementCharacteristic = null;
			mRecordAccessControlPointCharacteristic = null;
		}
	};

	/**
	 * Returns all records as a sparse array where sequence number is the key.
	 *
	 * @return the records list.
	 */
	public SparseArray<GlucoseEntity> getRecords() {
		return mRecords;
	}

	/**
	 * Clears the records list locally.
	 */
	public void clear() {
		mRecords.clear();
		mCallbacks.onOperationCompleted(getBluetoothDevice());
	}

	/**
	 * Sends the request to obtain all records from glucose device. Initially we want to notify user
	 * about the number of the records so the 'Report Number of Stored Records' is send. The data
	 * will be returned to Glucose Measurement characteristic as a notification followed by
	 * Record Access Control Point indication with status code Success or other in case of error.
	 */
	public void getAllRecords() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;

		clear();
		mCallbacks.onOperationStarted(getBluetoothDevice());
		writeCharacteristic(mRecordAccessControlPointCharacteristic, RecordAccessControlPointData.reportNumberOfAllStoredRecords())
				.with(new DataSentCallback() {
					@Override
					public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
						GlucoseManager.this.log(LogContract.Log.Level.APPLICATION, "\"" + RecordAccessControlPointParser.parse(data) + "\" sent");
					}
				})
				.enqueue();
	}

	/**
	 * Sends the request to obtain from the glucose device all records newer than the newest one
	 * from local storage. The data will be returned to Glucose Measurement characteristic as
	 * a notification followed by Record Access Control Point indication with status code Success
	 * or other in case of error.
	 * <p>
	 * Refresh button will not download records older than the oldest in the local memory.
	 * E.g. if you have pressed Last and then Refresh, than it will try to get only newer records.
	 * However if there are no records, it will download all existing (using {@link #getAllRecords()}).
	 */
	public void refreshRecords() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;

		if (mRecords.size() == 0) {
			getAllRecords();
		} else {
			mCallbacks.onOperationStarted(getBluetoothDevice());

			// obtain the last sequence number
			final int sequenceNumber = mRecords.keyAt(mRecords.size() - 1) + 1;

			writeCharacteristic(mRecordAccessControlPointCharacteristic,
					RecordAccessControlPointData.reportStoredRecordsGreaterThenOrEqualTo(sequenceNumber))
					.with(new DataSentCallback() {
						@Override
						public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
							GlucoseManager.this.log(LogContract.Log.Level.APPLICATION, "\"" + RecordAccessControlPointParser.parse(data) + "\" sent");
						}
					})
					.enqueue();
			// Info:
			// Operators OPERATOR_LESS_THEN_OR_EQUAL and OPERATOR_RANGE are not supported by Nordic Semiconductor Glucose Service in SDK 4.4.2.
		}
	}

	/**
	 * Sends abort operation signal to the device.
	 */
	public void abort() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;

		writeCharacteristic(mRecordAccessControlPointCharacteristic, RecordAccessControlPointData.abortOperation())
				.with(new DataSentCallback() {
					@Override
					public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
						GlucoseManager.this.log(LogContract.Log.Level.APPLICATION, "\"" + RecordAccessControlPointParser.parse(data) + "\" sent");
					}
				})
				.enqueue();
	}

	/**
	 * Sends the request to delete all data from the device. A Record Access Control Point
	 * indication with status code Success (or other in case of error) will be send.
	 */
	public void deleteAllRecords() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;

		clear();
		mCallbacks.onOperationStarted(getBluetoothDevice());
		writeCharacteristic(mRecordAccessControlPointCharacteristic, RecordAccessControlPointData.deleteAllStoredRecords())
				.with(new DataSentCallback() {
					@Override
					public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
						GlucoseManager.this.log(LogContract.Log.Level.APPLICATION, "\"" + RecordAccessControlPointParser.parse(data) + "\" sent");
					}
				})
				.enqueue();
	}
}
