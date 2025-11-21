package com.andreasmoe.findmyairplanemodephone;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanRecord;
import android.os.ParcelUuid;
import java.util.List;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv;
    private static final int REQ_CODE_BT_PERMS = 1;
    private BluetoothLeScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv = new TextView(this);
        tv.setTextSize(24);
        setContentView(tv);
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager != null ? manager.getAdapter() : null;

        if (adapter == null || !adapter.isEnabled()) {
            tv.setText("Bluetooth not available on this device or off.\n");
            return;
        }

        scanner = adapter.getBluetoothLeScanner();
        if (scanner == null || 
            checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || 
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            tv.append("BLE permissions not granted or BLE scanner is not available.\n");
            return;
        }

        tv.append("Starting BLE scan (devices will appear below)...\n");
        scanner.startScan(scanCallback);
    }

    private final ScanCallback scanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    String address = result.getDevice().getAddress();
                    ScanRecord record = result.getScanRecord();
                    int rssi = result.getRssi();
                    if (address != null && record != null) {
                        return;
                    }
                    String advName = record.getDeviceName();
                    if (advName != null) {
                        tv.append(address + " " + advName + " " + rssi + " dBm\n");
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    tv.append("Scan failed: " + errorCode + "\n");
                }
            };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.stopScan(scanCallback);
        }
    }
}
