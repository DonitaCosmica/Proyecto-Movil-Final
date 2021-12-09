package com.example.proyectomovilfinal.paginas;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;

/**
 * A fragment representing a list of Items.
 */
public class PaginaHistorialPasos extends Fragment {

    // TODO: Definir si hacen falta los parametros del fragmento.
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaginaHistorialPasos()
    {}

    // TODO: Inicializacion con parametros
    @SuppressWarnings("unused")
    public static PaginaHistorialPasos newInstance(int columnCount)
    {
        PaginaHistorialPasos fragment = new PaginaHistorialPasos();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pagina_historial_pasos, container, false);

        // Set the adapter
        RecyclerView recyclerView = view.findViewById(R.id.lista_historial_pasos);

        if (recyclerView != null)
        {
            Context context = view.getContext();

            if (mColumnCount <= 1)
            {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            else
            {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
//            recyclerView.setAdapter(new AdapterHistorialPasos(DummyContent.ITEMS));
        }

        return view;
    }
}