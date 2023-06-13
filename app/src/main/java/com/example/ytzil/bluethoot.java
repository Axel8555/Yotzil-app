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

import java.util.ArrayList;
import java.util.Set;

public class bluethoot extends AppCompatActivity {
    CheckBox enable, visible;
    Button atras, buscar;
    ListView listabh;

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> paireddevaices;

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
                    }
                    BA.disable();
                    Toast.makeText(bluethoot.this, "Apagado", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intenton = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intenton, 0);
                    Toast.makeText(bluethoot.this, "Encendido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent setvisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    if (ActivityCompat.checkSelfPermission(bluethoot.this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    }
                    startActivityForResult(setvisible, 0);
                    Toast.makeText(bluethoot.this, "Visible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list();
            }
        });

        atras.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Principal.class);
            startActivityForResult(intent, 0);
        });

    }

    private void list() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        paireddevaices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : paireddevaices) {
            list.add(bt.getName());
        }
        Toast.makeText(this, "Mostrar dispositivos", Toast.LENGTH_SHORT).show();
        ArrayAdapter adaptader = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listabh.setAdapter(adaptader);
    }

    public String getLocalBluetoothName() {
        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        String name = BA.getName();
        if (name == null){
            name = BA.getAddress();
        }
        return name;
    }

    private void init(){
        atras=findViewById(R.id.atrasb);
        enable=findViewById(R.id.activo);
        visible=findViewById(R.id.vis);
        listabh=findViewById(R.id.listab);
        buscar=findViewById(R.id.buscar);
    }
}