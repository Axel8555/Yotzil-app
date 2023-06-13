package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Quejas extends AppCompatActivity {
    Button env, can, atras;
    EditText qys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quejas);
        qys = findViewById(R.id.qys);
        env = findViewById(R.id.enviar);
        can = findViewById(R.id.cancelar);
        atras = findViewById(R.id.atrasq);

        atras.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), configuracion.class);
            startActivityForResult(intent, 0);
        });

        can.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), configuracion.class);
            startActivityForResult(intent, 0);
        });

        env.setOnClickListener(v -> {
            try {
                Thread.sleep(6*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(qys.getText().toString().isEmpty()) {
                Toast.makeText(this, "No hay mensaje", Toast.LENGTH_LONG).show();
            }
                else{
                Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), configuracion.class);
                    startActivityForResult(intent, 0); }
        });
    }
}