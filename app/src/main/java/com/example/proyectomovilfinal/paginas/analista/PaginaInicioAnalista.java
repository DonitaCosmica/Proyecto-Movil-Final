package com.example.proyectomovilfinal.paginas.analista;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaInicioAnalista#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaInicioAnalista extends Fragment {

    private static final String TAG = "PaginaListaUsuarios";

    TextView usuariosTotales;
    TextView analistasTotales;

    int countPresupuesto90 = 0;
    int countPresupuesto70 = 0;
    int countPresupuesto50 = 0;
    int countPresupuestoMenor50 = 0;

    private RecyclerView mRecyclerView;
    //TODO: Cambiar tipo de adapter.
    private AdapterHistorialGastos mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PaginaInicioAnalista() {}

    public static PaginaInicioAnalista newInstance() {
        PaginaInicioAnalista fragment = new PaginaInicioAnalista();
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
        View view = inflater.inflate(R.layout.fragment_pagina_inicio_analista, container, false);

//        initRecycler(view);
        PieChart pieChart = view.findViewById(R.id.pieChart);

        usuariosTotales = view.findViewById(R.id.usuariosTotales);
        analistasTotales = view.findViewById(R.id.analistasTotales);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("usuarios").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                double presupuesto90 = 0;
                double presupuesto70 = 0;
                double presupuesto50 = 0;

                double presupuesto = 0;
                int gastoTotal = 0;

                for (QueryDocumentSnapshot documentSnapshot : value){
                    Log.i("Pire Chart", documentSnapshot.getId());

                    if (documentSnapshot.getId().equals("1TpYgTdSaIRCObC2JJjvXfdVz1v1")){

                        if (documentSnapshot.contains("presupuesto")) {

                            presupuesto = documentSnapshot.getDouble("presupuesto");

                            presupuesto90 = presupuesto * 0.9;
                            presupuesto70 = presupuesto * 0.7;
                            presupuesto50 = presupuesto * 0.5;

                            if(documentSnapshot.contains("gastosPorTipo")){

                                List<Double> gastoDia = (List<Double>) documentSnapshot.get("gastosPorTipo");

                                for (int i = 0; i < gastoDia.size(); i++){

                                    gastoTotal += gastoDia.get(i);
                                }

                                if (gastoTotal >= presupuesto90){

                                    countPresupuesto90++;

                                }else if (gastoTotal >= presupuesto70 && gastoTotal < presupuesto90 ){

                                    countPresupuesto70++;

                                }else if (gastoTotal >= presupuesto50 && gastoTotal < presupuesto70){

                                    countPresupuesto50++;

                                }else if (gastoTotal < presupuesto50){

                                    countPresupuestoMenor50++;
                                }
                            }
                        }
                    }
                }

                ArrayList<PieEntry> usuarios = new ArrayList<>();

                usuarios.add(new PieEntry(countPresupuesto90, "90 - 100% cumplen"));
                usuarios.add(new PieEntry(countPresupuesto70, "70 - 89.9% cumplen"));
                usuarios.add(new PieEntry(countPresupuesto50, "50 - 60.9% cumplen"));
                usuarios.add(new PieEntry(countPresupuestoMenor50, "Menos 50% cumplen"));

                PieDataSet pieDataSet = new PieDataSet(usuarios, "usuarios");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);

                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.animate();
            }
        });

        mFirestore.collection("usuarios").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                int countUsuario = 0;
                int countAnalista = 0;
                Long tipo = 0L;

                for (QueryDocumentSnapshot documentSnapshot : value){

                    if (documentSnapshot.contains("tipo")){

                        tipo = documentSnapshot.getLong("tipo");

                        if (tipo == 0){

                            countUsuario++;

                        }else if (tipo == 1){

                            countAnalista++;

                        }
                    }
                }

                usuariosTotales.setText("Usuarios totales: " + String.valueOf(countUsuario));
                analistasTotales.setText("Analistas totales: " + String.valueOf(countAnalista));
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