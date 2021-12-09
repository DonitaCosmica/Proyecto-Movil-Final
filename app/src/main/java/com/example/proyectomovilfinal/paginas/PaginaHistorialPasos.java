package com.example.proyectomovilfinal.paginas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.Pasos;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialPasos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class PaginaHistorialPasos extends Fragment {

    private static final String TAG = "PaginaHistorialPasos";

    private AdapterHistorialPasos mAdapter;

    private FirebaseFirestore mFirestore;

    private TextView mTxtPasosTotales;

    public PaginaHistorialPasos()
    {}

    public static PaginaHistorialPasos newInstance()
    {
        PaginaHistorialPasos fragment = new PaginaHistorialPasos();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pagina_historial_pasos, container, false);

        mTxtPasosTotales = view.findViewById(R.id.txt_total_pasos_hoy_historial);

        initRecyclerView(view);

        obtenerTotalDePasosHoy();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void initRecyclerView(View view) {

        String idUsuario = FirebaseAuth.getInstance().getUid();

        Query query = mFirestore.collection(Pasos.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Pasos.CAMPO_ID_USUARIO, idUsuario)
                .orderBy(Pasos.CAMPO_FECHA, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Pasos> opciones = new FirestoreRecyclerOptions.Builder<Pasos>()
                .setQuery(query, Pasos.class)
                .build();

        RecyclerView recyclerView = view.findViewById(R.id.lista_historial_pasos);

        mAdapter = new AdapterHistorialPasos(opciones);

        if (recyclerView != null)
        {
            Context context = view.getContext();

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mAdapter);
        }
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
                        mTxtPasosTotales.setText(getString(R.string.cero));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "No se pudo obtener el total de pasos: ", e);
                    mTxtPasosTotales.setText("--");
                });
    }
}