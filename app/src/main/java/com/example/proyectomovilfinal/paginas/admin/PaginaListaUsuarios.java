package com.example.proyectomovilfinal.paginas.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.AgregarGastoActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.DatosUsuario;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;
import com.example.proyectomovilfinal.paginas.adapters.UsuariosAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaListaUsuarios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaListaUsuarios extends Fragment {

    private static final String TAG = "PaginaListaUsuarios";

    private RecyclerView mRecyclerView;
    private UsuariosAdapter mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PaginaListaUsuarios() {}

    public static PaginaListaUsuarios newInstance() {
        PaginaListaUsuarios fragment = new PaginaListaUsuarios();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagina_lista_usuarios, container, false);

        mRecyclerView = view.findViewById(R.id.recycleUsuarios);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFirestore = FirebaseFirestore.getInstance();

        Query query = mFirestore.collection("usuarios");

        FirestoreRecyclerOptions<DatosUsuario> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<DatosUsuario>().setQuery(query, DatosUsuario.class).build();

        mAdapter = new UsuariosAdapter(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAdapter != null)
            mAdapter.stopListening();
    }

}