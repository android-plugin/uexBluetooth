package org.zywx.wbpalmstar.widgetone.uexBluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){

        }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

        }
    }

    public interface BluetoothStateCallback{
        void onStateChanged(int state);//1,已连接上；2，已断开
    }
}
