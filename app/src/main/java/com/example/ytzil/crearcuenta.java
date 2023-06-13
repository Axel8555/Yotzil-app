package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class crearcuenta extends AppCompatActivity {
    Button cc;
    EditText cuenta, contra, nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crearcuenta);
        cc = findViewById(R.id.sesion);
        nombre = findViewById(R.id.name);
        cuenta = findViewById(R.id.username);
        contra = findViewById(R.id.password);

        cc.setOnClickListener(v -> {
            Toast.makeText(this, "Cuenta Registrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(intent, 0);
        });
    }
}