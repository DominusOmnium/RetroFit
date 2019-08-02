package com.google.android.gms.fit.samples.basicsensorsapi;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.fit.samples.MiBand.BluetoothIO;
import com.google.android.gms.fit.samples.MiBand.MiBand;
import com.google.android.gms.fit.samples.common.logger.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConnectToDevice extends AppCompatActivity {

    ListView lv_bluetooth;
    BluetoothIO io;
    MiBand miBand;
    BluetoothGatt gatt;
    ArrayAdapter adapter;
    List<UUID> serviceUUIDsList        = new ArrayList<>();
    List<UUID> characteristicUUIDsList = new ArrayList<>();
    List<UUID> descriptorUUIDsList     = new ArrayList<>();
    TextView tv_status;

    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_device);

        io = new BluetoothIO();

        int rc = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, rc);
        }

        miBand = new MiBand(this);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        Button b_scan = findViewById(R.id.b_start_scan);
        Button b_stop = findViewById(R.id.b_stop_scan);
        tv_status = findViewById(R.id.tv_status);

        adapter = new ArrayAdapter<String>(this, R.layout.item, new ArrayList<String>());

        final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                serviceUUIDsList = getServiceUUIDsList(result);

                gatt = device.connectGatt(ConnectToDevice.this, true, io);
                if (device.getName().equals("Mi Band 3"))
                {

                    String res = "";
                    for (ParcelUuid p : result.getScanRecord().getServiceUuids())
                    {
                        res += p.getUuid().toString() + "\n";
                        Log.i("123", p.getUuid().toString());
                    }
                    tv_status.setText(res);
                }
                String item = device.getName() + "|" + device.getAddress();
                Log.i("123", item);
                /*if (!devices.containsKey(item)) {
                    devices.put(item, device);
                    adapter.add(item);
                }*/

            }
        };



        b_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miBand.startScan(scanCallback);
            }
        });
        b_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miBand.stopScan(scanCallback);
                defineCharAndDescrUUIDs(gatt);
            }
        });

        ListView lv = findViewById(R.id.lv_bluetooth);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = ((TextView) view).getText().toString();
                if (devices.containsKey(item)) {
                    MiBand.stopScan(scanCallback);

                    BluetoothDevice device = devices.get(item);
                    miBand.connect(device);
                    /*Intent intent = new Intent();
                    intent.putExtra("device", device);
                    intent.setClass(ConnectToDevice.this, MainActivity.class);
                    ConnectToDevice.this.startActivity(intent);
                    ConnectToDevice.this.finish();*/
                }
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<UUID> getServiceUUIDsList(ScanResult scanResult)
    {
        List<ParcelUuid> parcelUuids = scanResult.getScanRecord().getServiceUuids();

        List<UUID> serviceList = new ArrayList<>();

        for (int i = 0; i < parcelUuids.size(); i++)
        {
            UUID serviceUUID = parcelUuids.get(i).getUuid();

            if (!serviceList.contains(serviceUUID))
                serviceList.add(serviceUUID);
        }

        return serviceList;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void defineCharAndDescrUUIDs(BluetoothGatt bluetoothGatt)
    {
        List<BluetoothGattService> servicesList = bluetoothGatt.getServices();

        for (int i = 0; i < servicesList.size(); i++)
        {
            BluetoothGattService bluetoothGattService = servicesList.get(i);

            if (serviceUUIDsList.contains(bluetoothGattService.getUuid()))
            {
                List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = bluetoothGattService.getCharacteristics();

                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristicList)
                {
                    characteristicUUIDsList.add(bluetoothGattCharacteristic.getUuid());
                    adapter.add(bluetoothGattCharacteristic.getUuid());
                    List<BluetoothGattDescriptor> bluetoothGattDescriptorsList = bluetoothGattCharacteristic.getDescriptors();

                    for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptorsList)
                    {
                        descriptorUUIDsList.add(bluetoothGattDescriptor.getUuid());
                    }
                }
            }
        }
    }
}