package com.andreasmoe.findmyairplanemodephone;

import android.bluetooth.le.*;
import android.bluetooth.*;
import android.os.ParcelUuid;
import java.util.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv;
    private static final int REQ_CODE_BT_PERMS = 1;
    private BluetoothLeScanner scanner;
    private List<String> seenAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv = new TextView(this);
        setContentView(tv);

        seenAddresses = new ArrayList<String>();
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
                    byte[] data = record.getManufacturerSpecificData().get(0x004C);
                    if (data == null || data.length < 23) {
                        return;
                    }
                    int major = ((data[18] & 0xFF) << 8) | (data[19] & 0xFF);
                    int minor = ((data[20] & 0xFF) << 8) | (data[21] & 0xFF);
                    int txPower = (int) data[22];
                    if (!seenAddresses.contains(address)) {
                        tv.append("\nAddress: " + address);
                        tv.append("\ndata: " + bytesToHex(data));
                        tv.append("\ndatalength: " + data.length);
                        tv.append("\nmajor: " + major);
                        tv.append("\nminor: " + minor);
                        tv.append("\ntxPower: " + txPower);
                        tv.append("\n");
                        seenAddresses.add(address);
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

    private static String bytesToHex(byte[] data) {
        if (data == null) return "";
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format(Locale.US, "%02X", b));
        }
        return sb.toString();
    }
}

