package healthkit.tarento.healthdataaggregator.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import healthkit.tarento.healthdataaggregator.adapter.DeviceListAdapter;
import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.listeners.ItemClickListener;
import healthkit.tarento.healthdataaggregator.utility.Utils;

import static healthkit.tarento.healthdataaggregator.utility.Utils.convertFromInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ItemClickListener {

    Button startScan, stopScan;
    RecyclerView rvDeviceList;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    UUID HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D);
    UUID HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37);
    UUID HEART_RATE_CONTROL_POINT_CHAR_UUID = convertFromInteger(0x2A39);
    UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902);

    private DeviceListAdapter mDeviceListAdapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startScan = findViewById(R.id.btnStart);
        stopScan = findViewById(R.id.btnStop);
        startScan.setOnClickListener(this);
        stopScan.setOnClickListener(this);

        rvDeviceList = findViewById(R.id.list);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mDeviceListAdapter = new DeviceListAdapter();
        setUpRecyclerView();
        setUpBluetooth();

    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvDeviceList.setLayoutManager(layoutManager);
        rvDeviceList.setAdapter(mDeviceListAdapter);
        mDeviceListAdapter.setItemClickListener(this);

    }


    private void setUpBluetooth() {
        if (mBluetoothAdapter != null || !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }


    private void scanForBleDevices() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }

        /*Log.e("NITISH", "scanForBleDevices: start" );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utils.scheduleJob(getApplicationContext());
        }*/


    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: "+device.getName() );
                            if (device.getName() != null) {
                                /*mBluetoothDevice = device;*/
                                mDeviceListAdapter.addDevice(device);
                                stopScan();

                            }
                        }
                    });
                }
            };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                scanForBleDevices();
//                googleFitScan();
                break;

            case R.id.btnStop:
                stopScan();
                break;
        }
    }

    private void stopScan() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter != null) {
                    Log.e(TAG, "run: "+mBluetoothAdapter.getScanMode() );
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }
        },5000);

    }

    @Override
    public void onItemClick(BluetoothDevice mBluetoothDevice) {
        connectGatt(mBluetoothDevice);
        Toast.makeText(this, "NAME ---->" + mBluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
    }

    private void connectGatt(BluetoothDevice mBluetoothDevice) {
        mBluetoothDevice.createBond();
        mBluetoothGatt = mBluetoothDevice.connectGatt(this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.e(TAG, "onConnectionStateChange: 1" );
                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                intentAction = ACTION_GATT_CONNECTED;
                    mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                    Log.e(TAG, "Connected to GATT server.");
                    Log.e(TAG, "Attempting to start service discovery:" +
                            gatt.discoverServices());

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = STATE_DISCONNECTED;
                    Log.e(TAG, "Disconnected from GATT server.");
//                broadcastUpdate(intentAction);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.e(TAG, "onServicesDiscovered: 1" );
               /* List<BluetoothGattService> gattServices = gatt.getServices();
                for (BluetoothGattService service:gattServices){
                    Log.e(TAG, "onServicesDiscovered: getUuid  "+service.getUuid() );
                    Log.e(TAG, "onServicesDiscovered: getType  "+service.getType() );
                    Log.e(TAG, "onServicesDiscovered: getInstanceId  "+service.getInstanceId() );
                }*/
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(HEART_RATE_SERVICE_UUID)
                                .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID);
                BluetoothGattDescriptor descriptor =
                        characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);

                descriptor.setValue(
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.e(TAG, "onCharacteristicWrite: 1" +characteristic.getUuid());
                Log.e(TAG, "onCharacteristicWrite: 1" +characteristic.getValue());
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                Log.e(TAG, "onCharacteristicRead: 1");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.e(TAG, "onCharacteristicChanged: 1" );
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.e(TAG, "onDescriptorWrite: 1" );
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(HEART_RATE_SERVICE_UUID)
                                .getCharacteristic(HEART_RATE_CONTROL_POINT_CHAR_UUID);
                characteristic.setValue(new byte[]{1, 1});
                gatt.writeCharacteristic(characteristic);
            }
        });

    }


    private String TAG = "NITISH";
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG, "onConnectionStateChange: " );
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                Log.e(TAG, "Connected to GATT server.");
                Log.e(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.e(TAG, "Disconnected from GATT server.");
//                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e(TAG, "onServicesDiscovered: " );

            List<BluetoothGattService> gattServices = gatt.getServices();
            for (BluetoothGattService service:gattServices){
                Log.e(TAG, "onServicesDiscovered: getUuid  "+service.getUuid() );
                Log.e(TAG, "onServicesDiscovered: getType  "+service.getType() );
                Log.e(TAG, "onServicesDiscovered: getInstanceId  "+service.getInstanceId() );
            }
            /*BluetoothGattCharacteristic characteristic =
                    gatt.getService(HEART_RATE_SERVICE_UUID)
                            .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID);
            BluetoothGattDescriptor descriptor =
                    characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);

            descriptor.setValue(
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);*/
   /*         if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                *//*gatt.getServices();*//*
                Log.e(TAG, "onServicesDiscovered GATT_SUCCESS: " + status);
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }*/
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "onDescriptorWrite: " );
            BluetoothGattCharacteristic characteristic =
                    gatt.getService(HEART_RATE_SERVICE_UUID)
                            .getCharacteristic(HEART_RATE_CONTROL_POINT_CHAR_UUID);
            characteristic.setValue(new byte[]{1, 1});
            gatt.writeCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*processData(characteristic.getValue());*/

            Log.e(TAG, "onCharacteristicChanged: ");
        }
    };


    BleScanCallback bleScanCallbacks = new BleScanCallback() {
        @Override
        public void onDeviceFound(BleDevice device) {
//            Log.e(TAG, "onDeviceFound: " + device.getName());
            // A device that provides the requested data types is available
        }

        @Override
        public void onScanStopped() {
            // The scan timed out or was interrupted
        }
    };


    private void googleFitScan() {
      /*  Task<Void> response = Fitness.getBleClient(this,
                GoogleSignIn.getLastSignedInAccount(this))
                .startBleScan(Arrays.asList(DataType.TYPE_STEP_COUNT_CUMULATIVE),
                        1000, bleScanCallbacks);*/
    }


}
