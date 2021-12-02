package com.example.proyectomovilfinal.paginas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Un fragmento que muestra una lista de gastos.
 */
public class PaginaHistorialGastos extends Fragment {

    private static final String TAG = "HISTORIAL_GASTOS";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView mRecyclerView;
    private AdapterHistorialGastos mAdapter;

    private FirebaseFirestore mFirestore;

    List<Gasto> mGastos = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaginaHistorialGastos()
    {}

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PaginaHistorialGastos newInstance(int columnCount)
    {
        PaginaHistorialGastos fragment = new PaginaHistorialGastos();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
            .limit(10)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Log.d(TAG, doc.getId() + " => " + doc.getData());
                        }
                    }
                    else {
                        Log.e(TAG, "Error obteniendo gastos: ", task.getException());
                    }
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pagina_historial_gastos, container, false);

        // Determinar el adaptador del recycler.
        initRecycler(view);

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

    private void initRecycler(View view) {
        //TODO: Usar el id real del usuario autenticado.
        final String ID_USUARIO_TEMPORAL = "pXqACrhyoqZpdw8n11n64749YuI2";

        Query query = mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Gasto.CAMPO_ID_USUARIO, ID_USUARIO_TEMPORAL)
                .limit(10)
                .orderBy(Gasto.CAMPO_FECHA, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Gasto> opciones = new FirestoreRecyclerOptions.Builder<Gasto>()
            .setQuery(query, Gasto.class)
                .build();

        mAdapter = new AdapterHistorialGastos(opciones);

        mRecyclerView = view.findViewById(R.id.lista_historial_gastos);

        // Elegir un layout manager segun el numero de columnas.
        if (mColumnCount <= 1)
        {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }
        else
        {
            mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Crear un nuevo touch helper con el callback y agregarlo al RecyclerView.
        ItemTouchHelper helper = new ItemTouchHelper(mCallback);

        helper.attachToRecyclerView(mRecyclerView);
    }

    ItemTouchHelper.SimpleCallback mCallback = new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
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
                mAdapter.notifyDataSetChanged();
                Log.i("HistorialGastos", "Eliminar gasto en la posicion " + posicion);
            }
            else if (ItemTouchHelper.RIGHT == direction)
            {
                Log.i("HistorialGastos", "Editar gasto en la posicion " + posicion);
            }
        }
    };
}