package com.example.ytzil;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BluetoothPermissionHandler {
    private AppCompatActivity activity;
    private BluetoothAdapter BA;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE_BT = 2;

    public BluetoothPermissionHandler(AppCompatActivity activity, BluetoothAdapter bluetoothAdapter) {
        this.activity = activity;
        this.BA = bluetoothAdapter;
    }

    public void enableBluetooth() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        } else {
            BA.enable();
            Toast.makeText(activity, "Encendido", Toast.LENGTH_SHORT).show();
        }
    }

    public void disableBluetooth() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        } else {
            BA.disable();
            Toast.makeText(activity, "Apagado", Toast.LENGTH_SHORT).show();
        }
    }

    public void enableDiscoverable() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, REQUEST_DISCOVERABLE_BT);
        } else {
            // Tu código para habilitar la visibilidad
        }
    }
}
