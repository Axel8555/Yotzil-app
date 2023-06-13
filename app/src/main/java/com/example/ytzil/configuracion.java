package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class configuracion extends AppCompatActivity {
    Button atras, perfil, quejas, ajus, cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        init();

        atras.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Principal.class);
            startActivityForResult(intent, 0);
        });

        perfil.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), perfil.class);
            startActivityForResult(intent, 0);
        });

        quejas.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Quejas.class);
            startActivityForResult(intent, 0);
        });

        ajus.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ajustes.class);
            startActivityForResult(intent, 0);
        });

        cerrar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(intent, 0);
        });
    }

    private void init(){
        perfil=findViewById(R.id.perfil);
        quejas=findViewById(R.id.sugerencias);
        atras=findViewById(R.id.atras);
        ajus=findViewById(R.id.Ajustes);
        cerrar=findViewById(R.id.cerrar);
    }
}