package com.example.ytzil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStreamWriter;
import java.util.Date;

public class Principal extends AppCompatActivity {
    private TextView ritmo, linea;
    private boolean enc = false, ele = false;
    int i=0, ale=0;
    String[][] matriz;
    Button conf, his, play;
    FloatingActionButton bluet;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
        conf.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), configuracion.class);
            startActivityForResult(intent, 0);
        });
        his.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), historial.class);
            startActivityForResult(intent, 0);
        });
        bluet.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BluetoothActivity.class);
            startActivityForResult(intent, 0);
        });
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    guardar();
                    ele=false;
                }
                else{
                    guardar();
                    ele = true;
                    vibrar();
                }
            }
        });
    }

    private void init(){
        conf = findViewById(R.id.config);
        his = findViewById(R.id.hist);
        bluet = findViewById(R.id.bluet);
        play = findViewById(R.id.button2);
        play.setText("Empezar");
        ritmo = findViewById(R.id.textView5);
        ritmo.setText("00");
        linea = findViewById(R.id.textView4);
        linea.setBackgroundColor(getResources().getColor(R.color.gris));
        checkbox = findViewById(R.id.ele);
        matriz = new String[7][3];
        matriz[0][0]="78";
        matriz[0][1]="79";
        matriz[0][2]="80";
        matriz[1][0]="80";
        matriz[1][1]="81";
        matriz[1][2]="82";
        matriz[2][0]="82";
        matriz[2][1]="83";
        matriz[2][2]="84";
        matriz[3][0]="84";
        matriz[3][1]="85";
        matriz[3][2]="86";
        matriz[4][0]="86";
        matriz[4][1]="87";
        matriz[4][2]="88";
        matriz[5][0]="88";
        matriz[5][1]="89";
        matriz[5][2]="90";
        matriz[6][0]="80";
        matriz[6][1]="81";
        matriz[6][2]="82";
    }

    public void OnClickStart(View view){
        if(enc) {
            guardar();
            enc = false;
            linea.setBackgroundColor(getResources().getColor(R.color.gris));
            ritmo.setText("00");
            play.setText("Empezar");
        }
        else {
            enc = true;
            play.setText("Detener");
            ale = (int) (Math.random()*6);
        }
        contador();
    }

    private void contador(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(enc){
                    if (ele){
                        ritmo.setText("120");
                        linea.setBackgroundColor(getResources().getColor(R.color.rojo));
                    }
                    else {
                        linea.setBackgroundColor(getResources().getColor(R.color.verde));
                        i = (int) (Math.random() * 3);
                        ritmo.setText(matriz[ale][i]);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void vibrar(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int j=0;j<3;j++){
                    Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    try {
                        Thread.sleep(3*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void guardar(){
        String prom, edo;
        String reg = new Date().toString();
        if (ele){
            prom = "120";
            edo = "En riesgo";
        }
        else{
            prom = matriz[ale][1];
            edo = "Bueno";
        }
        try{
            OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("historial", Context.MODE_APPEND));
            fout.write("Fecha y hora del registro: " + reg + "\t Promedio de Ppm: " + prom + "\t Estado: " + edo + " \n");
            fout.close();
            Toast.makeText(this, "Historial guardado", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Log.e("Archivos","Error al alamcenar información");
        }
    }

}