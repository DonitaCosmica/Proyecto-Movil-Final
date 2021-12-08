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
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
  
    private static final int VISTA_INICIO = R.id.fragmento_inicio;
    private static final int VISTA_HISTORIAL_GASTOS = R.id.fragmento_gastos;
    private static final int VISTA_HISTORIAL_PASOS = R.id.fragmento_pasos;
    private static final int VISTA_PERFIL = R.id.fragmento_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        
        // Navegacion con barra inferior.
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        navView.setBackground(null);
        navView.getMenu().getItem(2).setEnabled(false);

        //TODO: Cambiar los fragmentos y el nav graph de la barra segun el tipo de usuario.
        AppBarConfiguration configAppBar = new AppBarConfiguration.Builder(
            VISTA_INICIO,
            VISTA_HISTORIAL_GASTOS,
            VISTA_HISTORIAL_PASOS,
            VISTA_PERFIL
        ).build();

        NavController controladorNav = Navigation.findNavController(this, R.id.fragmento_navegacion);
        NavigationUI.setupActionBarWithNavController(this, controladorNav, configAppBar);
        NavigationUI.setupWithNavController(navView, controladorNav);

        // El botón agrega un nuevo gasto.
        FloatingActionButton fabAgregarGasto = findViewById(R.id.fab_agregar_gasto);
        fabAgregarGasto.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AgregarGastoActivity.class);
            startActivity(intent);
        });
    }
}
