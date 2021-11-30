package com.example.proyectomovilfinal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragmentoPagina = new PaginaInicio();

        if(savedInstanceState == null && findViewById(R.id.fragmento_pagina) != null)
        {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmento_pagina, fragmentoPagina, null)
                    .commit();
        }
    }
}