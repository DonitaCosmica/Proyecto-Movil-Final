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
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.data.Subcategoria;
import com.example.proyectomovilfinal.paginas.adapters.AdapterSubcategorias;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
    private int mColumnCount = 2;

    //TODO: Usar el id real del usuario para crear gasto.
    private final String ID_USUARIO_FAKE = "pXqACrhyoqZpdw8n11n64749YuI2";

    private AdapterSubcategorias mAdapter;

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

        mFirestore = FirebaseFirestore.getInstance();

//        obtenerSubcategorias(ID_USUARIO_FAKE);
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
                AgregarCategoriaFragment dialogoAgregarCategoria = AgregarCategoriaFragment.newInstance(mTipoGasto);
                dialogoAgregarCategoria.show(getChildFragmentManager(), null);
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

        //TODO: Usar id real del usuario autenticado.
        Query query = mFirestore.collection(Subcategoria.NOMBRE_COLECCION_FIRESTORE)
            .whereEqualTo(Subcategoria.CAMPO_ID_USUARIO, ID_USUARIO_FAKE)
            .whereEqualTo(Subcategoria.CAMPO_TIPO, Gasto.getTipoDeGasto(mTipoGasto).toString());

        FirestoreRecyclerOptions<Subcategoria> opciones = new FirestoreRecyclerOptions.Builder<Subcategoria>()
            .setQuery(query, Subcategoria.class)
            .build();

        mAdapter = new AdapterSubcategorias(opciones);

        RecyclerView recyclerView = view.findViewById(R.id.lista_subcategorias);

        // Usar un layout manager para 2 columnas.
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));

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

    private void obtenerSubcategorias(String idUsuario) {

        List<Subcategoria> subcategorias = new ArrayList<>();

        Log.i(TAG, "Obteniendo subcategorias con tipo: " + mTipoGasto);

        mFirestore.collection(Subcategoria.NOMBRE_COLECCION_FIRESTORE)
            .whereEqualTo(Subcategoria.CAMPO_ID_USUARIO, idUsuario)
            .whereEqualTo(Subcategoria.CAMPO_TIPO, mTipoGasto)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null)
                {
                    for (QueryDocumentSnapshot doc : task.getResult())
                    {
                        subcategorias.add(Subcategoria.fromDoc(doc));
                        Log.i(TAG, "Subcategoria -> " + doc.getId());
                    }

                    Log.i(TAG, "Subcategorias obtenidas: " + subcategorias.size());

                } else {
                    Log.d(TAG, "Error obteniendo documentos");
                }
            });

//        if (mAdapter != null) {
//            mAdapter.setSubcategorias(subcategorias);
//            mAdapter.notifyDataSetChanged();
//        }
    }
}