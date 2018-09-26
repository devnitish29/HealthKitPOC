package healthkit.tarento.healthdataaggregator.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.listeners.ItemClickListener;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<BluetoothDevice>();
    ItemClickListener mItemClickListener;

    public DeviceListAdapter() {
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        if (!mBluetoothDeviceList.contains(bluetoothDevice)){
            mBluetoothDeviceList.add(bluetoothDevice);
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_ble_device, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (mBluetoothDeviceList != null && mBluetoothDeviceList.size() > 0) {

            holder.txtBleName.setText(mBluetoothDeviceList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(mBluetoothDeviceList.get(position));
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mBluetoothDeviceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtBleName;
        ImageView imgBleType;
        TextView txtBleStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBleName = itemView.findViewById(R.id.name);
            imgBleType = itemView.findViewById(R.id.type);
        }
    }

}
