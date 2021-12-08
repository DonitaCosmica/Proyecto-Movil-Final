package com.example.proyectomovilfinal.paginas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.TipoGasto;
import com.example.proyectomovilfinal.paginas.adapters.AdapterTipos;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaTipoGasto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaTipoGasto extends Fragment implements AdapterTipos.OnItemClickListener{

    private static final String TAG = "PAGINA_TIPO_GASTO";

    public PaginaTipoGasto() {
        // Constructor publico vacio requerido.
    }

    public static PaginaTipoGasto newInstance() {
        PaginaTipoGasto fragment = new PaginaTipoGasto();
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
        View view = inflater.inflate(R.layout.pagina_tipo_gasto, container, false);

        // Configurar el adaptador del recycler.
        initRecycler(view);

        return view;
    }

    private void initRecycler(View view) {

        AdapterTipos adapter = new AdapterTipos(TipoGasto.values(), this);

        RecyclerView recyclerView = view.findViewById(R.id.lista_tipos_gasto);

        // Usar un layout manager para la lista, con 1 columna.
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(TipoGasto tipoDeGasto, int posicion) {

        Log.i(TAG, "Seleccionaste un tipo de gasto: " + tipoDeGasto.toString());

        final FragmentTransaction ft = getParentFragmentManager().beginTransaction();

        final PaginaSeleccionSubcategoria siguienteFragmento = PaginaSeleccionSubcategoria
                .newInstance(tipoDeGasto.getValor());

        ft.replace(R.id.fragment_agregar_gasto, siguienteFragmento);
        ft.addToBackStack(null);
        ft.commit();
    }
}