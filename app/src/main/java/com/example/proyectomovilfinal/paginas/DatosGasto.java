package com.example.proyectomovilfinal.paginas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatosGasto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatosGasto extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DatosGasto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatosGasto.
     */
    // TODO: Rename and change types and number of parameters
    public static DatosGasto newInstance(String param1, String param2) {
        DatosGasto fragment = new DatosGasto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_datos_gasto, container, false);

        // Configura el spinner de tipo de gasto usando un array de strings en recursos.
        Spinner spinnerTipoDeGasto = view.findViewById(R.id.spinner_tipo_gasto);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tipos_de_gasto, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTipoDeGasto.setAdapter(adapter);

        // Obtiene el boton para guardar gasto y configura su evento onClick para "guardar".
        FloatingActionButton fabGuardarGasto = view.findViewById(R.id.fab_guardar_gasto);
        fabGuardarGasto.setOnClickListener(componentView ->
        {
            Toast.makeText(getActivity(), R.string.temporal_proceso, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        return view;
    }
}