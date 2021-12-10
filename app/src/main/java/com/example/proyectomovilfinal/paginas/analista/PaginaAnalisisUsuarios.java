package com.example.proyectomovilfinal.paginas.analista;

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
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaAnalisisUsuarios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaAnalisisUsuarios extends Fragment {

    private static final String TAG = "PaginaListaUsuarios";

    private RecyclerView mRecyclerView;
    //TODO: Cambiar tipo de adapter.
    private AdapterHistorialGastos mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PaginaAnalisisUsuarios() {}

    public static PaginaAnalisisUsuarios newInstance() {
        PaginaAnalisisUsuarios fragment = new PaginaAnalisisUsuarios();
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
        View view = inflater.inflate(R.layout.fragment_pagina_analisis_usuarios, container, false);

//        initRecycler(view);

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

    private void initRecycler(View view) {

        String idUsuario = FirebaseAuth.getInstance().getUid();

        Query query = mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Gasto.CAMPO_ID_USUARIO, idUsuario)
                .orderBy(Gasto.CAMPO_FECHA, Query.Direction.DESCENDING)
                .limit(3);

        FirestoreRecyclerOptions<Gasto> opciones = new FirestoreRecyclerOptions.Builder<Gasto>()
                .setQuery(query, Gasto.class)
                .build();

        mAdapter = new AdapterHistorialGastos(opciones);

        mRecyclerView = view.findViewById(R.id.lista_historial_gastos_recientes);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Crear un nuevo touch helper con el callback y agregarlo al RecyclerView.
        ItemTouchHelper helper = new ItemTouchHelper(mCallback);

        helper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnItemClickListener((documento, posicion) -> {

            String idGasto = documento.getId();
            Intent intent = new Intent(getActivity(), AgregarGastoActivity.class);
            intent.putExtra(AgregarGastoActivity.ID_GASTO, idGasto);
            startActivity(intent);
        });
    }

    ItemTouchHelper.SimpleCallback mCallback = new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT)
    {
        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target)
        {
            return false;
        }

        // El callback determina la direccion (izquierda o derecha) del swipe y elimina o
        // modifica el elemento en esa posicion.
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {
            int posicion = viewHolder.getBindingAdapterPosition();

            if (ItemTouchHelper.LEFT == direction)
            {
                mAdapter.eliminarGasto(posicion);
                Toast.makeText(getContext(), "Gasto eliminado", Toast.LENGTH_SHORT).show();
            }
        }
    };
}