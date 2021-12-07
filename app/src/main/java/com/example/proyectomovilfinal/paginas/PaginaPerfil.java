package com.example.proyectomovilfinal.paginas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.PresupuestoDiarioActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.DatosUsuario;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Una subclase de {@link Fragment} que muestra la informacion
 * del usuario actual en el GUI.
 * EL metodo de factory {@link PaginaPerfil#newInstance} puede ser
 * usado para crear nuevas instancias de este fragmento.
 */
public class PaginaPerfil extends Fragment {

    private FirebaseFirestore mFirestore;
    //TODO: Obtener datos del usuario de Firestore.
    DatosUsuario mDatosUsuario = new DatosUsuario(
            "pXqACrhyoqZpdw8n11n64749YuI2",
            "Juan",
            "Ranchero de Vaca",
            "juan@su.rancho.com",
            25,
            200.0,
            0
    );

    public PaginaPerfil() {
        // Constructor publico default requerido.
    }

    /**
     * Crea una nueva instancia de {@link PaginaPerfil} usando
     * los argumentos especificados.
     *
     * @return Una nueva instancia de FormularioGasto.
     */
    public static PaginaPerfil newInstance() {
        PaginaPerfil fragment = new PaginaPerfil();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Obtener argumentos del Bundle, si los hay
        }

        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagina_perfil, container, false);

        ImageButton btnEditarPresupuesto = view.findViewById(R.id.btn_editar_presupuesto);
        btnEditarPresupuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PresupuestoDiarioActivity.class);
                startActivity(intent);
            }
        });

        Button btnCerrarSesion = view.findViewById(R.id.btn_temporal_cerrar_sesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.reiniciarCredenciales(getActivity());
                Toast.makeText(getActivity(), "Moooo", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}