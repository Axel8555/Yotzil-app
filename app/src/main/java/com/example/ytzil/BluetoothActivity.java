package com.example.ytzil;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BluetoothActivity extends AppCompatActivity {
    private CheckBox enable, visible;
    private Button atras, buscar;
    private ListView listabh;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothPermissionHandler permissionHandler;
    private BluetoothListHandler listHandler;

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
}
