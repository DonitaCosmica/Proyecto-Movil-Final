package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.proyectomovilfinal.data.TipoUsuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private TipoUsuario mTipoUsuario;
  
    private static final int VISTA_INICIO = R.id.fragmento_inicio;
    private static final int VISTA_HISTORIAL_GASTOS = R.id.fragmento_gastos;
    private static final int VISTA_HISTORIAL_PASOS = R.id.fragmento_pasos;
    private static final int VISTA_PERFIL = R.id.fragmento_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent originalIntent = getIntent();
        if (originalIntent != null) {
            int tipo = originalIntent.getIntExtra(Util.KEY_ARG_TIPO_USUARIO, 0);
            mTipoUsuario = Util.getTipoFromValor(tipo);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //region Configurar navegacion
        // Navegacion con barra inferior.
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        navView.setBackground(null);
        navView.getMenu().clear();

        AppBarConfiguration appBarConfig;

        NavController controladorNav = Navigation.findNavController(this, R.id.fragmento_navegacion);

        // Determinar la grafica de navegacion segun el tipo de usuario.
        switch (mTipoUsuario) {
            case ANALISTA:
                controladorNav.setGraph(R.navigation.analista_navigation);

                appBarConfig = new AppBarConfiguration.Builder(
                        VISTA_INICIO,
                        VISTA_HISTORIAL_GASTOS,
                        VISTA_HISTORIAL_PASOS,
                        VISTA_PERFIL
                ).build();

                navView.inflateMenu(R.menu.menu_nav_default);
                navView.getMenu().getItem(2).setEnabled(false);
                break;

            case ADMINISTRADOR:
                controladorNav.setGraph(R.navigation.admin_navigation);
                appBarConfig = new AppBarConfiguration.Builder(
                        VISTA_HISTORIAL_PASOS,
                        VISTA_PERFIL
                ).build();

                navView.inflateMenu(R.menu.menu_nav_admin);
                break;

            case NORMAL:
            default:
                controladorNav.setGraph(R.navigation.default_navigation);
                appBarConfig = new AppBarConfiguration.Builder(
                        VISTA_INICIO,
                        VISTA_PERFIL
                ).build();

                navView.inflateMenu(R.menu.menu_nav_default);
                break;
        }

        if (getSupportActionBar() != null) {
            NavigationUI.setupActionBarWithNavController(this, controladorNav, appBarConfig);
        }

        NavigationUI.setupWithNavController(navView, controladorNav);
        //endregion

        // El botón agrega un nuevo gasto, está presente en todos los views de navegación.
        FloatingActionButton fabAgregarGasto = findViewById(R.id.fab_agregar_gasto);
        fabAgregarGasto.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AgregarGastoActivity.class);
            startActivity(intent);
        });
    }
}
