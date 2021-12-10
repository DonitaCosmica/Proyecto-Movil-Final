package com.example.proyectomovilfinal.paginas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.Subcategoria;
import com.example.proyectomovilfinal.paginas.adapters.AdapterSubcategorias;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * Un fragmento que muestra una cuadricula con las subcategorias disponibles.
 */
public class PaginaSeleccionSubcategoria extends Fragment {

    private static final String TAG = "SELECCION_SUBCATEGORIA";

    // Argumentos del Fragmento.
    private static final String TIPO_GASTO = "TIPO_GASTO";

    private int mTipoGasto;
    private List<Subcategoria> mSubcategorias;

    private AdapterSubcategorias mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaginaSeleccionSubcategoria()
    {}

    public static PaginaSeleccionSubcategoria newInstance(int tipoDeGasto)
    {
        PaginaSeleccionSubcategoria fragment = new PaginaSeleccionSubcategoria();
        Bundle args = new Bundle();

        args.putInt(TIPO_GASTO, tipoDeGasto);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Obtiene el tipo de gasto de los argumentos recibidos.
        if (getArguments() != null) {
            mTipoGasto = getArguments().getInt(TIPO_GASTO);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pagina_elegir_subcategoria, container, false);

        // Configurar el adaptador del recycler.
        initRecycler(view);

        FloatingActionButton fabAgregarSubcategoria = view.findViewById(R.id.fab_agregar_subcategoria);
        fabAgregarSubcategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mAuth.getCurrentUser() != null) {
                    AgregarCategoriaFragment dialogoAgregarCategoria = AgregarCategoriaFragment
                            .newInstance(mAuth.getCurrentUser().getUid(), mTipoGasto);
                    dialogoAgregarCategoria.show(getChildFragmentManager(), null);
                }
            }
        });

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

        if (mAuth.getCurrentUser() == null) return;

        String idUsuario = mAuth.getCurrentUser().getUid();

        Log.i(TAG, "ID del usuario para subcategorias: " + idUsuario);

        Query query = mFirestore.collection(Subcategoria.NOMBRE_COLECCION_FIRESTORE)
            .whereEqualTo(Subcategoria.CAMPO_ID_USUARIO, idUsuario)
            .whereEqualTo(Subcategoria.CAMPO_TIPO, Util.getTipoDeGasto(mTipoGasto).toString());

        FirestoreRecyclerOptions<Subcategoria> opciones = new FirestoreRecyclerOptions.Builder<Subcategoria>()
            .setQuery(query, Subcategoria.class)
            .build();

        mAdapter = new AdapterSubcategorias(opciones);

        RecyclerView recyclerView = view.findViewById(R.id.lista_subcategorias);

        // Usar un layout manager para 2 columnas.
        int columnCount = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), columnCount));

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((documento, posicion) -> {

            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();

            Log.i(TAG, documento.getId());

            final FormularioGasto siguienteFragmento = FormularioGasto
                    .newInstance(null, mTipoGasto, documento.getId());

            ft.replace(R.id.fragment_agregar_gasto, siguienteFragmento);
            ft.addToBackStack(null);
            ft.commit();
        });
    }
}