package com.example.proyectomovilfinal.paginas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaInicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaInicio extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PaginaInicio() {
        // Required empty public constructor
    }

    public static PaginaInicio newInstance() {
        PaginaInicio fragment = new PaginaInicio();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagina_inicio, container, false);

        return view;
    }
}