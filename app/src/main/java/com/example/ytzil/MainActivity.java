package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button is, cc;
    EditText cuenta, contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        is = findViewById(R.id.login);
        cc = findViewById(R.id.button);
        cuenta = findViewById(R.id.username);
        contra = findViewById(R.id.password);

        cc.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), crearcuenta.class);
            startActivityForResult(intent, 0);
        });

        is.setOnClickListener(v -> {
            if(cuenta.getText().toString().isEmpty()) {
                Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_LONG).show();
            }
            else{
                if (contra.getText().toString().isEmpty()) {
                    Toast.makeText(this,"No se pudo iniciar sesión",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(v.getContext(), Principal.class);
                    startActivityForResult(intent, 0); }
            }
        });

    }

}