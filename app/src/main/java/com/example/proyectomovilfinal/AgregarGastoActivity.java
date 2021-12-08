package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.paginas.FormularioGasto;
import com.example.proyectomovilfinal.paginas.PaginaTipoGasto;

public class AgregarGastoActivity extends AppCompatActivity {

    public static final String ID_GASTO = "ID_GASTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_gasto);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        String idGasto = i.getStringExtra(ID_GASTO);

        Fragment formularioGasto = PaginaTipoGasto.newInstance();

        if (idGasto != null && !idGasto.isEmpty()) {
            formularioGasto = FormularioGasto.newInstance(idGasto, 0, "");
        }

        if(savedInstanceState == null && findViewById(R.id.fragment_agregar_gasto) != null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_agregar_gasto, formularioGasto, null)
                    .commit();
        }
    }
}