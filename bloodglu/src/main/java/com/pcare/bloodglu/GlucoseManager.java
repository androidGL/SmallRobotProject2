
package com.pcare.bloodglu;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.entity.NetResponse;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.common.oem.battery.BatteryManager;
import com.pcare.common.oem.battery.BatteryManagerCallbacks;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.GluTableController;
import com.pcare.common.table.UserDao;

import java.util.Calendar;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import no.nordicsemi.android.ble.common.callback.glucose.GlucoseMeasurementDataCallback;
import no.nordicsemi.android.ble.common.data.RecordAccessControlPointData;
import no.nordicsemi.android.ble.data.Data;


@SuppressWarnings("unused")
public class GlucoseManager extends BatteryManager<BatteryManagerCallbacks> {
	private static final String TAG = "GlucoseManager";

	/** Glucose service UUID */
	public final static UUID GLS_SERVICE_UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
	/** Glucose Measurement characteristic UUID */
	private final static UUID GM_CHARACTERISTIC = UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb");
	/** Record Access Control Point characteristic UUID */
	private final static UUID RACP_CHARACTERISTIC = UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb");

	private BluetoothGattCharacteristic mGlucoseMeasurementCharacteristic;
	private BluetoothGattCharacteristic mRecordAccessControlPointCharacteristic;

	private Handler mHandler;
	private static GlucoseManager mInstance;
	private Context mContext;
	private RefreshCallBack refreshCallBack;

	/**
	 * Returns the singleton implementation of GlucoseManager.
	 */
	public static GlucoseManager getGlucoseManager(final Context context) {
		if (mInstance == null)
			mInstance = new GlucoseManager(context);
		return mInstance;
	}

	protected interface RefreshCallBack{
		void getNewRecord(GlucoseEntity entity);
	}

	public GlucoseManager setCallBack(RefreshCallBack callBack){
		this.refreshCallBack = callBack;
		return this;
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
							record.setGlucoseConcentration(String.valueOf(glucoseConcentration != null ? glucoseConcentration : 0));
							record.setSampleType(type != null ? type : 0);
							record.setSampleLocation(sampleLocation != null ? sampleLocation : 0);
							record.setStatus( status != null ? status.value : 0);
							record.setUserId(UserDao.getCurrentUserId());
							//如果存在新的记录，则回调
							if(!GluTableController.getInstance(mContext).isExistSameItem(record)) {
								Log.i("Table-----","true");
								refreshCallBack.getNewRecord(record);
							}
							Log.i("TableReceived-----",record.toString());
						}
					});

			enableNotifications(mGlucoseMeasurementCharacteristic).enqueue();
		}

		@Override
		public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
			final BluetoothGattService service = gatt.getService(GLS_SERVICE_UUID);
			if (service != null) {
				mGlucoseMeasurementCharacteristic = service.getCharacteristic(GM_CHARACTERISTIC);
				mRecordAccessControlPointCharacteristic = service.getCharacteristic(RACP_CHARACTERISTIC);
			}
			getAllRecords();
			return mGlucoseMeasurementCharacteristic != null && mRecordAccessControlPointCharacteristic != null;
		}

		@Override
		protected void onDeviceDisconnected() {
			mGlucoseMeasurementCharacteristic = null;
			mRecordAccessControlPointCharacteristic = null;
		}
	};


	public void getAllRecords() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;
		writeCharacteristic(mRecordAccessControlPointCharacteristic, RecordAccessControlPointData.reportNumberOfAllStoredRecords()).enqueue();
	}

	public void refreshRecords() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;
		getAllRecords();
	}

	/**
	 * Sends abort operation signal to the device.
	 */
	public void abort() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;
		writeCharacteristic(mRecordAccessControlPointCharacteristic, RecordAccessControlPointData.abortOperation()).enqueue();

	}

	/**
	 * Sends the request to delete all data from the device. A Record Access Control Point
	 * indication with status code Success (or other in case of error) will be send.
	 */
	public void deleteAllRecords() {
		if (mRecordAccessControlPointCharacteristic == null)
			return;
		writeCharacteristic(mRecordAccessControlPointCharacteristic, RecordAccessControlPointData.deleteAllStoredRecords()).enqueue();

	}


}
