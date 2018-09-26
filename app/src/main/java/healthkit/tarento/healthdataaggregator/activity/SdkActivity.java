package healthkit.tarento.healthdataaggregator.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.HeartRateNotifyListener;
import com.zhaoxiaodan.miband.model.UserInfo;

import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.adapter.DeviceListAdapter;
import healthkit.tarento.healthdataaggregator.listeners.ItemClickListener;

public class SdkActivity extends AppCompatActivity implements ItemClickListener, HeartRateNotifyListener, View.OnClickListener {
    MiBand miband;
    BluetoothDevice device;

    DeviceListAdapter listAdapter;
    RecyclerView recyclerView;
    Button btnHeartRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk);
        recyclerView = findViewById(R.id.list2);
        btnHeartRate = findViewById(R.id.btn);
        btnHeartRate.setOnClickListener(this);
        listAdapter = new DeviceListAdapter();
        miband = new MiBand(this);

        miband.setHeartRateScanListener(this);
        setupListView();
        MiBand.startScan(scanCallback);
    }

    private void setupListView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listAdapter);
        listAdapter.setItemClickListener(this);
    }


    private String TAG = "NITISH";
    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {


            if (result.getDevice().getName() != null) {
                device = result.getDevice();
                listAdapter.addDevice(device);
                Log.e(TAG, "Bluetooth name:" + device.getName() + ",uuid:"
                        + device.getUuids() + ",add:"
                        + device.getAddress() + ",type:"
                        + device.getType() + ",bondState:"
                        + device.getBondState() + ",rssi:" + result.getRssi());
                MiBand.stopScan(scanCallback);


            }

//            MiBand.stopScan(scanCallback);
            // 根据情况展示
        }
    };

    @Override
    public void onItemClick(BluetoothDevice mBluetoothDevice) {

        miband.connect(mBluetoothDevice, new ActionCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e(TAG, "connect success");
                UserInfo userInfo = new UserInfo(20111111, 1, 27, 180, 91, "Nitish", 0);
                miband.setUserInfo(userInfo);
            }

            @Override
            public void onFail(int errorCode, String msg) {
                Log.e(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
            }
        });
    }

    @Override
    public void onNotify(int heartRate) {
        Log.d(TAG, "heart rate: " + heartRate);
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: " );
        miband.startHeartRateScan();
    }
}
