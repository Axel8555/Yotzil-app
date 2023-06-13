package com.example.ytzil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class historial extends AppCompatActivity {
    Button edo, conf, borrar;
    ListView listhist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        init();
        escribe();

        conf.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), configuracion.class);
            startActivityForResult(intent, 0);
        });

        edo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Principal.class);
            startActivityForResult(intent, 0);
        });

        borrar.setOnClickListener(v -> {
            try{
                OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("historial", Context.MODE_PRIVATE));
                fout.write("");
                fout.close();
            }catch (Exception ex){
                Log.e("Archivos","Error al alamcenar información");
            }
            Intent intent = new Intent(v.getContext(), historial.class);
            startActivityForResult(intent, 0);
        });
    }


    private void init(){
        edo = findViewById(R.id.edoach);
        conf = findViewById(R.id.configh);
        borrar = findViewById(R.id.borrar);
        listhist = findViewById(R.id.lista);
    }

    private void escribe(){
        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("historial")));
            ArrayList list = new ArrayList();
            String leer, a1, a2;
            while ((leer = fin.readLine())!=null) {
                list.add(leer);
            }
            ArrayAdapter adapter =  new ArrayAdapter(this, android.R.layout.simple_list_item_1,list);
            listhist.setAdapter(adapter);
            Toast.makeText(this, "Mostrando Historial", Toast.LENGTH_SHORT).show();

        }catch (Exception ex){
            Log.e("Archivos", "Error al buscar datos");
        }
    }

}