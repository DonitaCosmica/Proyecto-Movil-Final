package com.example.proyectomovilfinal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.paginas.PaginaHistorialGastos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static final String ID_CANAL_RECORDATORIOS = "canal_recordatorios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        crearCanalDeNotificaciones();

        // TEMPORAL: Mostrar pagina de historial por default, sin navegacion.
        // TODO: Implementar navegacion en MainActivity.
//       Fragment fragmentoPagina = new PaginaInicio();
          Fragment fragmentoPagina = new PaginaHistorialGastos();
//        Fragment fragmentoPagina = new PaginaHistorialPasos();

        if(savedInstanceState == null && findViewById(R.id.fragmento_pagina) != null)
        {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmento_pagina, fragmentoPagina, null)
                    .commit();
        }

        FloatingActionButton fabAgregarGasto = findViewById(R.id.fab_agregar_gasto);
        fabAgregarGasto.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AgregarGastoActivity.class);
            startActivity(intent);
        });
    }


    private void crearCanalDeNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = getString(R.string.nombre_canal_notif);
            String descripcion = getString(R.string.descripcion_canal_notif);
            int importantcia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(ID_CANAL_RECORDATORIOS, nombre, importantcia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);

            Log.i("MainActivity", "Canal creado");
        }
    }

    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, IniciarSesionActivity.class));
        }
    }

}