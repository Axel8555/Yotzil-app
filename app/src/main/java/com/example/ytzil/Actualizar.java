package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Actualizar extends AppCompatActivity {
    Button ac, cancelar;
    EditText cuenta, contra, nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);
        ac = findViewById(R.id.sesiona);
        nombre = findViewById(R.id.namea);
        cuenta = findViewById(R.id.usernamea);
        contra = findViewById(R.id.passworda);
        cancelar = findViewById(R.id.can);

        ac.setOnClickListener(v -> {
            Toast.makeText(this, "Cuenta Actualizada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(v.getContext(), perfil.class);
            startActivityForResult(intent, 0);
        });

        cancelar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), perfil.class);
            startActivityForResult(intent, 0);
        });
    }
}