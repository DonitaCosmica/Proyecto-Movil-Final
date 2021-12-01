package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.paginas.PaginaHistorialPasos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TEMPORAL: Mostrar pagina de historial por default, sin navegacion.
        // TODO: Implementar navegacion en MainActivity.
//        Fragment fragmentoPagina = new PaginaInicio();
//        Fragment fragmentoPagina = new PaginaHistorialGastos();
        Fragment fragmentoPagina = new PaginaHistorialPasos();

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
}