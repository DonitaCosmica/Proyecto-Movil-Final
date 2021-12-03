package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Verificar si hay credenciales o si el usuario ya inicio sesion

        startActivity(new Intent(this, MainActivity.class));
    }
}