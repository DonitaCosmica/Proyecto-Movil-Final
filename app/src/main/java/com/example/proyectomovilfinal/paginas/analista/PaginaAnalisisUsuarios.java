package com.example.proyectomovilfinal.paginas.analista;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.AgregarGastoActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

        BarChart barChart = view.findViewById(R.id.barChart);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("usuarios").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                Timestamp fecha;
                ArrayList<BarEntry> usuarios = new ArrayList<>();
                int ene = 0; int feb = 0; int mar = 0; int abr = 0; int may = 0; int jun = 0;
                int jul = 0; int ago = 0; int sep = 0; int oct = 0; int nov = 0; int dic = 0;

                for (QueryDocumentSnapshot documentSnapshot : value){

                    if (documentSnapshot.contains("fecha")) {

                        fecha = documentSnapshot.getTimestamp("fecha");
                        int mes = fecha.toDate().getMonth();

                        switch (mes){

                            case 0:
                                ene++;
                                break;
                            case 1:
                                feb++;
                                break;
                            case 2:
                                mar++;
                                break;
                            case 3:
                                abr++;
                                break;
                            case 4:
                                may++;
                                break;
                            case 5:
                                jun++;
                                break;
                            case 6:
                                jul++;
                                break;
                            case 7:
                                ago++;
                                break;
                            case 8:
                                sep++;
                                break;
                            case 9:
                                oct++;
                                break;
                            case 10:
                                nov++;
                                break;
                            case 11:
                                dic++;
                                break;
                        }
                    }
                }

                usuarios.add(new BarEntry(1, ene));
                usuarios.add(new BarEntry(2, feb));
                usuarios.add(new BarEntry(3, mar));
                usuarios.add(new BarEntry(4, abr));
                usuarios.add(new BarEntry(5, may));
                usuarios.add(new BarEntry(6, jun));
                usuarios.add(new BarEntry(7, jul));
                usuarios.add(new BarEntry(8, ago));
                usuarios.add(new BarEntry(9, sep));
                usuarios.add(new BarEntry(10, oct));
                usuarios.add(new BarEntry(11, nov));
                usuarios.add(new BarEntry(12, dic));

                IBarDataSet barDataSet = new BarDataSet(usuarios, "usuarios");
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(12f);

                BarData barData = new BarData(barDataSet);

                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("Bar Chart Example");
                barChart.animateY(2000);
            }
        });

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