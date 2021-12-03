package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int VISTA_INICIO = R.id.fragmento_inicio;
    private static final int VISTA_HISTORIAL_GASTOS = R.id.fragmento_gastos;
    private static final int VISTA_HISTORIAL_PASOS = R.id.fragmento_pasos;
    private static final int VISTA_PERFIL = R.id.fragmento_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_nav);

        AppBarConfiguration configAppBar = new AppBarConfiguration.Builder(
                VISTA_INICIO,
                VISTA_HISTORIAL_GASTOS,
                VISTA_HISTORIAL_PASOS,
                VISTA_PERFIL
        ).build();

        NavController controladorNav = Navigation.findNavController(this, R.id.fragmento_navegacion);
        NavigationUI.setupActionBarWithNavController(this, controladorNav, configAppBar);
        NavigationUI.setupWithNavController(navView, controladorNav);

        // TEMPORAL: Mostrar pagina de historial por default, sin navegacion.
        // TODO: Implementar navegacion en MainActivity.
//        Fragment fragmentoPagina = new PaginaInicio();
////        Fragment fragmentoPagina = new PaginaHistorialGastos();
////        Fragment fragmentoPagina = new PaginaHistorialPasos();
//
//        if (savedInstanceState == null && findViewById(R.id.fragmento_pagina) != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .add(R.id.fragmento_pagina, fragmentoPagina, null)
//                    .commit();
//        }

        // El boton agrega un nuevo gasto.
        FloatingActionButton fabAgregarGasto = findViewById(R.id.fab_agregar_gasto);
        fabAgregarGasto.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AgregarGastoActivity.class);
            startActivity(intent);
        });
    }
}