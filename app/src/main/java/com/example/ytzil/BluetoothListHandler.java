package com.example.ytzil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BluetoothListHandler {
    private AppCompatActivity activity;
    private ListView deviceListView;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Map<String, String> deviceMap;
    private OnDeviceSelectedListener deviceSelectedListener;

    public BluetoothListHandler(AppCompatActivity activity, ListView deviceListView, BluetoothAdapter bluetoothAdapter) {
        this.activity = activity;
        this.deviceListView = deviceListView;
        this.bluetoothAdapter = bluetoothAdapter;
        this.deviceSelectedListener = null;
    }

    public void setOnDeviceSelectedListener(OnDeviceSelectedListener listener) {
        this.deviceSelectedListener = listener;
    }

    public void list() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        List<String> deviceList = new ArrayList<>();
        deviceMap = new HashMap<>(pairedDevices.size());

        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            deviceList.add(deviceName);
            deviceMap.put(deviceName, deviceAddress);
        }

        Toast.makeText(activity, "Mostrar dispositivos", Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, deviceList);
        deviceListView.setAdapter(adapter);

        deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDeviceAddress = getDeviceAddressAtPosition(position);
            if (selectedDeviceAddress != null && deviceSelectedListener != null) {
                connectToDevice(selectedDeviceAddress);
            }
        });
    }

    private void connectToDevice(String deviceAddress) {
        BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        String selectedDeviceName = selectedDevice.getName();
        // Aquí puedes realizar la conexión con el dispositivo seleccionado
        // ...
        //Toast.makeText(activity, "Dispositivo seleccionado: " + selectedDeviceName, Toast.LENGTH_SHORT).show();
        deviceSelectedListener.onDeviceSelected(selectedDeviceName);
    }

    public String getDeviceAddressAtPosition(int position) {
        BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
        if (position >= 0 && position < devices.length) {
            return devices[position].getAddress();
        }
        return null;
    }

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(String deviceName);
    }
}
