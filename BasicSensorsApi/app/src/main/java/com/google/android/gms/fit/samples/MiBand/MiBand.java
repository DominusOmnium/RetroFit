package com.google.android.gms.fit.samples.MiBand;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;

import com.google.android.gms.fit.samples.common.logger.Log;

import java.util.UUID;

public class MiBand {

    Context context;
    BluetoothGatt gatt;

    public MiBand(Context c)
    {
        context = c;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startScan(ScanCallback callback) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null == adapter) {
            return;
        }
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (null == scanner) {
            return;
        }
        scanner.startScan(callback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void stopScan(ScanCallback callback) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null == adapter) {
            return;
        }
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (null == scanner) {
            return;
        }
        scanner.stopScan(callback);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void enableNotifications(BluetoothGattCharacteristic chrt) {
        gatt.setCharacteristicNotification(chrt, true);
        for (BluetoothGattDescriptor descriptor : chrt.getDescriptors()){
            if (descriptor.getUuid().equals(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))) {
                Log.i("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + descriptor.getUuid().toString());
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
        }
        chrt.setValue(new byte[]{0x01, 0x8, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45});
        gatt.writeCharacteristic(chrt);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connect(BluetoothDevice device)
    {
        for (ParcelUuid uuid : device.getUuids())
        {
            enableNotifications(gatt.getService(uuid.getUuid()).getCharacteristic(uuid.getUuid()));
        }
    }
}
