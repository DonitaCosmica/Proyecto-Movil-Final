package com.example.proyectomovilfinal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    
    private FirebaseAuth mAuth;

    private String mCorreo = null;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crearCanalDeNotificaciones();

        mAuth = FirebaseAuth.getInstance();

        obtenerCredenciales();

        if (mCorreo != null && !mCorreo.isEmpty()) {
            intentarLogin();
        } else {
            Intent i = new Intent(SplashScreen.this, IniciarSesionActivity.class);
            startActivity(i);
            finish();
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

    private void intentarLogin() {

        mAuth.signInWithEmailAndPassword(mCorreo, mPassword)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // El inicio de sesion fue exitoso, redirige a la aplicacion.
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);

                } else {
                    // El inicio de sesion falló; envia al usuario a IniciarSesionActivity.
                    Log.i(TAG, "Llendo a iniciar sesion");
                    Intent i = new Intent(SplashScreen.this, IniciarSesionActivity.class);
                    startActivity(i);
                }

                finish();
            });
    }

    /**
     * Obtiene las credenciales guardadas en la instancia de SharedPreferences.
     *
     * Si no hay credenciales guardadas, no modifica nada.
     */
    private void obtenerCredenciales() {

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.nombre_archivo_preferencias), Context.MODE_PRIVATE);

        mCorreo = sharedPref.getString(getString(R.string.pref_key_email_usuario), "");
        
        //TODO: Validar el string obtenido de SharedPreferences.
        if (!mCorreo.isEmpty()) {
            mPassword = sharedPref.getString(getString(R.string.pref_key_password_usuario), "");
            
            Log.i(TAG, "Credenciales obtenidas para el usuario: " + mCorreo);
        } else {
            Log.w(TAG, "No hay credenciales de un usuario, email: " + mCorreo);
        }
    }
}