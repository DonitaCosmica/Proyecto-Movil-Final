package com.example.proyectomovilfinal.paginas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.AgregarGastoActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.data.Pasos;
import com.example.proyectomovilfinal.data.Subcategoria;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaInicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaInicio extends Fragment {

    private static final String TAG = "PaginaInicio";

    private TextView mTxtGastoTotalHoy;
    private TextView mTxtPasosTotales;

    private RecyclerView mRecyclerView;
    private AdapterHistorialGastos mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PaginaInicio() {}

    public static PaginaInicio newInstance() {
        PaginaInicio fragment = new PaginaInicio();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        obtenerGastoTotal();
        obtenerTotalDePasosHoy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagina_inicio, container, false);

        mTxtGastoTotalHoy = view.findViewById(R.id.gasto_dia_actual);
        mTxtPasosTotales = view.findViewById(R.id.pasos_dia_actual);

        initRecycler(view);

        return view;
    }

    private void obtenerGastoTotal() {

        String idUsuario = mAuth.getUid();

        Date hoy = Util.obtenerFechaActual();

        if (idUsuario == null) return;

        mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Gasto.CAMPO_ID_USUARIO, idUsuario)
                .whereGreaterThanOrEqualTo(Gasto.CAMPO_FECHA, hoy)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                    {
                        double gastoTotal = 0;

                        for (QueryDocumentSnapshot doc : task.getResult())
                        {
                            if (doc.getDouble(Gasto.CAMPO_CANTIDAD) != null) {
                                gastoTotal += doc.getDouble(Gasto.CAMPO_CANTIDAD);
                            }
                        }

                        String gastoConFormato = Util.fDinero.format(gastoTotal);
                        mTxtGastoTotalHoy.setText(gastoConFormato);

                    } else {
                        Log.d(TAG, "Error obteniendo documentos");
                    }
                });
    }

    private void obtenerTotalDePasosHoy() {

        String idUsuario = FirebaseAuth.getInstance().getUid();
        Date hoy = Util.obtenerFechaActual();

        mFirestore.collection(Pasos.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Pasos.CAMPO_ID_USUARIO, idUsuario)
                .whereGreaterThanOrEqualTo(Pasos.CAMPO_FECHA, hoy)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Pasos pasos = Pasos.fromDoc(doc);

                        String pasosTotalesConFormato = Util.fCantidad.format(pasos.getCantidad());

                        mTxtPasosTotales.setText(pasosTotalesConFormato);
                    }
                    else {
                        if (getContext() != null) {
                            mTxtPasosTotales.setText(getString(R.string.cero));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "No se pudo obtener el total de pasos: ", e);
                    mTxtPasosTotales.setText("--");
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        obtenerSubcategorias();

        if (mAdapter != null) {
            mAdapter.startListening();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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

    private void obtenerSubcategorias() {

        String idUsuario = FirebaseAuth.getInstance().getUid();
        PaginaHistorialGastos.mSubcategoriasUsuario.clear();

        mFirestore.collection(Subcategoria.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Subcategoria.CAMPO_ID_USUARIO, idUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Subcategoria subcategoria = Subcategoria.fromDoc(doc);
                        PaginaHistorialGastos.mSubcategoriasUsuario.put(
                                subcategoria.getIdSubcategoria(),
                                subcategoria.getNombre()
                        );
                    }

                    Log.i(TAG, "Subcategorias obtenidas: " + PaginaHistorialGastos.mSubcategoriasUsuario.size());

                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error obteniendo lista de subcategorias: ", e);
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