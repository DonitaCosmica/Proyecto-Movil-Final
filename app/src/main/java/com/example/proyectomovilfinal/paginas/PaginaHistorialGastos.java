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
import com.example.proyectomovilfinal.data.DummyContent;
import com.example.proyectomovilfinal.paginas.adapters.AdapterHistorialGastos;

/**
 * A fragment representing a list of Items.
 */
public class PaginaHistorialGastos extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pagina_historial_gastos, container, false);

        // Set the adapter
        RecyclerView recyclerView = view.findViewById(R.id.lista_historial_gastos);

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
            recyclerView.setAdapter(new AdapterHistorialGastos(DummyContent.ITEMS));
        }

        return view;
    }
}