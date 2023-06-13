package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class perfil extends AppCompatActivity {
    Button ac, atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ac = findViewById(R.id.actual);
        atras = findViewById(R.id.atrasp);

        ac.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Actualizar.class);
            startActivityForResult(intent, 0);
        });

        atras.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), configuracion.class);
            startActivityForResult(intent, 0);
        });
    }
}