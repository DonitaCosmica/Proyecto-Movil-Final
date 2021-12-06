package com.example.proyectomovilfinal.paginas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.AgregarGastoActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Un fragmento que muestra una lista de gastos.
 */
public class PaginaHistorialGastos extends Fragment {

    private static final String TAG = "HISTORIAL_GASTOS";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private RecyclerView mRecyclerView;
    private AdapterHistorialGastos mAdapter;

    private FirebaseFirestore mFirestore;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaginaHistorialGastos()
    {}

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

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Crear un nuevo touch helper con el callback y agregarlo al RecyclerView.
        ItemTouchHelper helper = new ItemTouchHelper(mCallback);

        helper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnItemClickListener(new AdapterHistorialGastos.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documento, int posicion) {
                String idGasto = documento.getId();
                Intent intent = new Intent(getActivity(), AgregarGastoActivity.class);
                intent.putExtra(AgregarGastoActivity.ID_GASTO, idGasto);
                startActivity(intent);
            }
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