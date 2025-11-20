package com.andreasmoe.findmyairplanemodephone;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv = new TextView(this);
        tv.setTextSize(24);
        setContentView(tv);
        checkBluetooth();
    }

    private void checkBluetooth() {
        BluetoothManager manager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager != null ? manager.getAdapter() : null;

        if (adapter == null) {
            tv.setText("Bluetooth not available on this device.");
        } else if (!adapter.isEnabled()) {
            tv.setText(
                    "Bluetooth is available but OFF.\n"
                            + "Turn Bluetooth ON and restart the app.");
        } else {
            tv.setText(
                    "Bluetooth is available and ON.\n\n"
                            + "Next step: add BLE scan for your FSC-BP108.");
        }
    }
}
