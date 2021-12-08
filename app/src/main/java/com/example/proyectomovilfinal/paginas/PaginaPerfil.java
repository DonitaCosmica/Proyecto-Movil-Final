package com.example.proyectomovilfinal.paginas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.IniciarSesionActivity;
import com.example.proyectomovilfinal.PresupuestoDiarioActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.DatosUsuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Una subclase de {@link Fragment} que muestra la informacion
 * del usuario actual en el GUI.
 * EL metodo de factory {@link PaginaPerfil#newInstance} puede ser
 * usado para crear nuevas instancias de este fragmento.
 */
public class PaginaPerfil extends Fragment {

    private static final String TAG = "PaginaPerfil";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    //TODO: Obtener datos del usuario de Firestore.
    private DatosUsuario mDatosUsuario;

    private TextView mTxtNombre;
    private TextView mTxtEmail;
    private TextView mTxtEdad;
    private TextView mTxtPresupuesto;

    private View mContenidoPerfil;
    private ProgressBar mProgressBar;

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

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            obtenerPerfilUsuario(usuario);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagina_perfil, container, false);

        mTxtNombre = view.findViewById(R.id.txt_nombre_usuario);
        mTxtEmail = view.findViewById(R.id.txt_perfil_email);
        mTxtEdad = view.findViewById(R.id.txt_perfil_edad);
        mTxtPresupuesto = view.findViewById(R.id.txt_perfil_presupuesto);

        mContenidoPerfil = view.findViewById(R.id.tarjeta_datos_usuario);
        mProgressBar = view.findViewById(R.id.progress_perfil);

        mContenidoPerfil.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

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
                Toast.makeText(getActivity(), "Moooo", Toast.LENGTH_SHORT).show();
                Util.reiniciarCredenciales(getActivity());

                Intent i = new Intent(getActivity(), IniciarSesionActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return view;
    }

    private void mostrarPerfil(@NonNull DatosUsuario datosUsuario) {

        String nombreCompleto = datosUsuario.getNombre() + datosUsuario.getApellido();
        String presupuestoConFormato = "$" + Util.fDinero.format(datosUsuario.getPresupuesto());

        mTxtNombre.setText(nombreCompleto);
        mTxtEmail.setText(datosUsuario.getEmail());
        mTxtEdad.setText(String.valueOf(datosUsuario.getEdad()));
        mTxtPresupuesto.setText(presupuestoConFormato);

        mContenidoPerfil.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void obtenerPerfilUsuario(@NonNull FirebaseUser usuario) {

        String idUsuario = usuario.getUid();

        mFirestore.collection(DatosUsuario.NOMBRE_COLECCION_FIRESTORE)
                .document(idUsuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mDatosUsuario = DatosUsuario.fromDoc(documentSnapshot);

                        mostrarPerfil(mDatosUsuario);

                        Log.i(TAG, "Perfil del usuario obtenido.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No se pudo obtener el perfil del usuario", e);
                    }
                });
    }
}