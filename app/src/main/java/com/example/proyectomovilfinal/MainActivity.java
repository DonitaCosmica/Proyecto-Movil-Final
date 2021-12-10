package com.example.proyectomovilfinal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.proyectomovilfinal.data.TipoUsuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

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

        revisarPermisoGoogleFit();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Util.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE && mAuth.getCurrentUser() != null) {
                enqueueStepCountWork(mAuth.getCurrentUser().getUid());
            } else {
                // El resultado no fue de GoogleFit.
                Log.i(TAG, "Otro resultado recibido, no de Google Fit");
            }
        } else {
            Log.i(TAG, "El permiso para acceder a GoogleFit no fue otorgado.");
        }
    }

    private void revisarPermisoGoogleFit() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Log.i(TAG, "La app no tiene los permisos necesarios.");
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
//                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION)
        }

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        Log.i(TAG, "FitnessOptions creado.");

        final GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        Log.i(TAG, "Cuenta obtenida");

        if(!GoogleSignIn.hasPermissions(account, fitnessOptions)) {

            GoogleSignIn.requestPermissions(
                    MainActivity.this,
                    Util.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
            );
        }
        else if (mAuth.getCurrentUser() != null){

            Log.i(TAG, "Accediendo a google Fit.");
            enqueueStepCountWork(mAuth.getCurrentUser().getUid());
        }
    }

    private void enqueueStepCountWork(String idUsuario) {
        // Especificar que el WorkRequest necesita conexión a internet.
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Crear nuevo Data con los datos de entrada del WorkRequest.
        Data datosPasosRequest = new Data.Builder()
                .putString(Util.KEY_ARG_ID_USUARIO, idUsuario)
                .build();

        // Crear un nuevo WorkRequest con un periodo de 15 minutos.
        PeriodicWorkRequest enviarPasosRequest = new PeriodicWorkRequest.Builder(
                StepCountWorker.class, 1, TimeUnit.HOURS)
                .addTag("firebase-pasos")
                .setInputData(datosPasosRequest)
                .setConstraints(restricciones)
                .setInitialDelay(10, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "update_step_count",
                ExistingPeriodicWorkPolicy.KEEP,
                enviarPasosRequest);

        Log.i(TAG, "StepCountWorker enqueued.");
    }
}
