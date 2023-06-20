package com.example.ytzil;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class Principal extends AppCompatActivity {
    private TextView ritmo, linea;
    private boolean enc = false;
    Button conf, his;
    FloatingActionButton bluet;
    SeekBar seekExtra;
    private WebSocketClient webSocketClient;
    private boolean isVibrating = false;
    private Vibrator vibrator;
    private Handler handler;
    private Runnable runnable;
    private int initialPadding;
    private ValueAnimator paddingAnimator;

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

        seekExtra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // No se requiere ninguna acción al cambiar el progreso del seekbar
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No se requiere ninguna acción al iniciar el seguimiento del seekbar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = (seekBar.getProgress()-seekBar.getMax()/2)*10;
                sendExtraValue(progress);
            }
        });

        connectWebSocket();
        setupHeartbeatAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVibration();
        stopHeartbeatAnimation();
    }

    private void init() {
        conf = findViewById(R.id.config);
        his = findViewById(R.id.hist);
        bluet = findViewById(R.id.bluet);
        ritmo = findViewById(R.id.textView5);
        ritmo.setText("00");
        linea = findViewById(R.id.textView4);
        linea.setBackgroundColor(getResources().getColor(R.color.verde));
        seekExtra = findViewById(R.id.seekBar);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://52.71.18.223:3003"); // Reemplaza con la dirección IP y el puerto correctos
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Principal.this, "Conexión exitosa", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                try {
                    String data = "";
                    for (int i = 0; i < message.length(); i++) {
                        if ((int) message.charAt(i) != 10) {
                            data = data + message.charAt(i);
                        } else {
                            break;
                        }
                    }
                    int pulso = Integer.parseInt(data);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ritmo.setText(String.valueOf(pulso));
                            guardar(pulso);

                            if (pulso > 120 && !isVibrating) {
                                startVibration();
                                startHeartbeatAnimation(250); // Duración de la animación: 250 ms
                                linea.setBackgroundColor(getResources().getColor(R.color.rojo));
                            } else if (pulso <= 120 && isVibrating) {
                                stopVibration();
                                startHeartbeatAnimation(500); // Duración de la animación: 500 ms
                                linea.setBackgroundColor(getResources().getColor(R.color.verde));
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Principal.this, "Conexión cerrada: " + reason, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Principal.this, "Error de conexión: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        webSocketClient.connect();
    }

    private void guardar(int pulso) {
        String prom, edo;
        String reg = new Date().toString();
        prom = String.valueOf(pulso);
        edo = (pulso > 120) ? "En Riesgo" : "Bueno";

        try {
            OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("historial", Context.MODE_APPEND));
            fout.write("Fecha y hora del registro: " + reg + "\t Promedio de Ppm: " + prom + "\t Estado: " + edo + " \n");
            fout.close();
            //Toast.makeText(this, "Historial guardado", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("Archivos", "Error al almacenar información");
        }
    }

    private void startVibration() {
        isVibrating = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isVibrating) {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(1000);
                    }
                    handler.postDelayed(this, 2000); // Vibrar durante 1 segundo y luego esperar 1 segundo antes de repetirse
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void stopVibration() {
        isVibrating = false;
        handler.removeCallbacks(runnable);
        vibrator.cancel();
    }

    private void setupHeartbeatAnimation() {
        FrameLayout frameCorazon = findViewById(R.id.frameCorazon);
        initialPadding = getResources().getDimensionPixelSize(R.dimen.initial_padding);
        frameCorazon.setPadding(initialPadding, initialPadding, initialPadding, initialPadding);

        paddingAnimator = ValueAnimator.ofInt(initialPadding, 2 * initialPadding);
        paddingAnimator.setDuration(500);
        paddingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        paddingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        paddingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                frameCorazon.setPadding(animatedValue, animatedValue, animatedValue, animatedValue);
            }
        });
        paddingAnimator.start();
    }

    private void startHeartbeatAnimation(int duration) {
        paddingAnimator.setDuration(duration);
        paddingAnimator.start();
    }

    private void stopHeartbeatAnimation() {
        paddingAnimator.cancel();
        FrameLayout frameCorazon = findViewById(R.id.frameCorazon);
        frameCorazon.setPadding(initialPadding, initialPadding, initialPadding, initialPadding);
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void sendExtraValue(int value) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(String.valueOf(value));
        }
    }
}
