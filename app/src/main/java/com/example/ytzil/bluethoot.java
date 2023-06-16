package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class bluethoot extends AppCompatActivity {
    CheckBox enable, visible;
    Button atras, buscar;
    ListView listabh;

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE_BT = 2;
    private Map<String, String> deviceMap; // Mapa para vincular el nombre del dispositivo con la dirección MAC
    private Socket socket;
    private BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluethoot);
        init();
        BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
            Toast.makeText(this, "No se pudo realizar la acción", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (BA.isEnabled()) {
            enable.setChecked(true);
        }

        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (ActivityCompat.checkSelfPermission(bluethoot.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(bluethoot.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                    } else {
                        BA.disable();
                        Toast.makeText(bluethoot.this, "Apagado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intenton = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intenton, REQUEST_ENABLE_BT);
                    Toast.makeText(bluethoot.this, "Encendido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(bluethoot.this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(bluethoot.this, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, REQUEST_DISCOVERABLE_BT);
                    } else {
                        Intent setvisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        setvisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivityForResult(setvisible, REQUEST_DISCOVERABLE_BT);
                        Toast.makeText(bluethoot.this, "Visible", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list();
            }
        });

        listabh.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDeviceAddress = deviceMap.get(parent.getItemAtPosition(position));
            String selectedDeviceName = (String) parent.getItemAtPosition(position);
            // Aquí puedes realizar la conexión con el dispositivo seleccionado
            Toast.makeText(bluethoot.this, "Dispositivo seleccionado: " + selectedDeviceName, Toast.LENGTH_SHORT).show();

            // Realizar conexión con el programa en Go
            try {
                socket = new Socket(selectedDeviceAddress, 8888); // Reemplaza "8888" con el puerto correcto
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Leer y mostrar los mensajes recibidos en un Toast
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message;
                            while ((message = reader.readLine()) != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(bluethoot.this, "Mensaje recibido: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(bluethoot.this, "Error al conectar con el dispositivo", Toast.LENGTH_SHORT).show();
            }
        });

        atras.setOnClickListener(v -> {
            // Cerrar la conexión y liberar recursos
            try {
                if (socket != null)
                    socket.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(v.getContext(), Principal.class);
            startActivityForResult(intent, 0);
        });

    }

    private void list() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        } else {
            pairedDevices = BA.getBondedDevices();
            ArrayList<String> list = new ArrayList<>();
            deviceMap = new HashMap<>(); // Inicializar el mapa

            for (BluetoothDevice bt : pairedDevices) {
                String deviceName = bt.getName();
                String deviceAddress = bt.getAddress();
                list.add(deviceName);
                deviceMap.put(deviceName, deviceAddress); // Agregar la entrada al mapa (nombre del dispositivo -> dirección MAC)
            }
            Toast.makeText(this, "Mostrar dispositivos", Toast.LENGTH_SHORT).show();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            listabh.setAdapter(adapter);
        }
    }

    public String getLocalBluetoothName() {
        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }
        String name = BA.getName();
        if (name == null) {
            name = BA.getAddress();
        }
        return name;
    }

    private void init() {
        atras = findViewById(R.id.atrasb);
        enable = findViewById(R.id.activo);
        visible = findViewById(R.id.vis);
        listabh = findViewById(R.id.listab);
        buscar = findViewById(R.id.buscar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enable.setChecked(true);
            } else {
                enable.setChecked(false);
                Toast.makeText(this, "Permiso de Bluetooth no concedido", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_DISCOVERABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent setvisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                setvisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    visible.setChecked(false);
                    return;
                }
                visible.setChecked(true);
                startActivityForResult(setvisible, REQUEST_DISCOVERABLE_BT);
                Toast.makeText(bluethoot.this, "Visible", Toast.LENGTH_SHORT).show();
            } else {
                visible.setChecked(false);
                Toast.makeText(this, "Permiso de Bluetooth no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
