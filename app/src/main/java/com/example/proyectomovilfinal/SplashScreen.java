package com.example.proyectomovilfinal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crearCanalDeNotificaciones();

        //TODO: Verificar si hay credenciales o si el usuario ya inicio sesion
        // Ya existen las "llaves" para SharedPreferences en strings.xml.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() == null) {
            Log.i("Splash", "el uchuario es nulos");
            startActivity(new Intent(SplashScreen.this, IniciarSesionActivity.class));
        }else{
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Crea un nuevo canal para notificaciones si la version del SDK es +26.
     */
    private void crearCanalDeNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = getString(R.string.nombre_canal_notif);
            String descripcion = getString(R.string.descripcion_canal_notif);
            int importantcia = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel canal = new NotificationChannel(getString(R.string.id_canal_notif), nombre, importantcia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);

            Log.i("MainActivity", "Canal creado");
        }
    }
}