package com.example.ytzil;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements BluetoothListHandler.OnDeviceSelectedListener {
    private CheckBox enable;
    private CheckBox visible;
    private Button atras;
    private Button buscar;
    private ListView listabh;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothPermissionHandler permissionHandler;
    private BluetoothListHandler listHandler;
    private BluetoothSocket btSocket = null;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private DataReceiverThread dataReceiverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        init();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "No se pudo realizar la acción", Toast.LENGTH_SHORT).show();
            finish();
        }

        permissionHandler = new BluetoothPermissionHandler(this, bluetoothAdapter);
        listHandler = new BluetoothListHandler(this, listabh, bluetoothAdapter);
        listHandler.setOnDeviceSelectedListener(this);

        if (bluetoothAdapter.isEnabled()) {
            enable.setChecked(true);
        }

        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    permissionHandler.disableBluetooth();
                } else {
                    permissionHandler.enableBluetooth();
                }
            }
        });

        visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    permissionHandler.enableDiscoverable();
                }
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listHandler.list();
            }
        });

        atras.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Principal.class);
            startActivityForResult(intent, 0);
        });
    }

    private void init() {
        atras = findViewById(R.id.atrasb);
        enable = findViewById(R.id.activo);
        visible = findViewById(R.id.vis);
        listabh = findViewById(R.id.listab);
        buscar = findViewById(R.id.buscar);
    }

    @Override
    public void onDeviceSelected(String deviceAddress) {
        BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            btSocket = selectedDevice.createRfcommSocketToServiceRecord(mUUID);
            btSocket.connect();
            if (btSocket.isConnected()) {
                Toast.makeText(this, "Conexión establecida con el dispositivo: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                // Crea y comienza el hilo de recepción de datos
                dataReceiverThread = new DataReceiverThread(btSocket);
                dataReceiverThread.start();
                // Realiza operaciones adicionales con el socket si es necesario
            } else {
                Toast.makeText(this, "No se pudo establecer la conexión con el dispositivo", Toast.LENGTH_SHORT).show();
                btSocket.close();
                btSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al establecer la conexión con el dispositivo", Toast.LENGTH_SHORT).show();
            if (btSocket != null) {
                try {
                    btSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                btSocket = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataReceiverThread != null) {
            dataReceiverThread.interrupt();
            dataReceiverThread = null;
        }
    }

    private class DataReceiverThread extends Thread {
        private BluetoothSocket btSocket;

        public DataReceiverThread(BluetoothSocket socket) {
            btSocket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = btSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String data;
                while ((data = reader.readLine()) != null) {
                    if (!Thread.currentThread().isInterrupted()) {
                        showToast(data);
                    }
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void showToast(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
