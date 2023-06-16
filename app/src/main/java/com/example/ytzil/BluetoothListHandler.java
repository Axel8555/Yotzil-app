package com.example.ytzil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BluetoothListHandler {
    private AppCompatActivity activity;
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Map<String, String> deviceMap;

    public BluetoothListHandler(AppCompatActivity activity, ListView listView, BluetoothAdapter bluetoothAdapter) {
        this.activity = activity;
        this.listView = listView;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void list() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();
        deviceMap = new HashMap<>(); // Inicializar el mapa

        for (BluetoothDevice bt : pairedDevices) {
            String deviceName = bt.getName();
            String deviceAddress = bt.getAddress();
            list.add(deviceName);
            deviceMap.put(deviceName, deviceAddress); // Agregar la entrada al mapa (nombre del dispositivo -> dirección MAC)
        }
        Toast.makeText(activity, "Mostrar dispositivos", Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDeviceAddress = getSelectedDeviceAddress(position);
            if (selectedDeviceAddress != null) {
                // Aquí puedes realizar la conexión con el dispositivo seleccionado
                BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(selectedDeviceAddress);
                String selectedDeviceName = selectedDevice.getName();
                Toast.makeText(activity, "Dispositivo seleccionado: " + selectedDeviceName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getSelectedDeviceAddress(int position) {
        BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
        if (position >= 0 && position < devices.length) {
            return devices[position].getAddress();
        }
        return null;
    }
}
