package com.example.proyectomovilfinal.paginas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.data.TipoGasto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioGasto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioGasto extends Fragment {

    private static final String TAG = "Formulario Gasto";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner mSpinnerTipo;
    private EditText mEditCantidad;
    private EditText mEditDescripcion;

    private FirebaseFirestore mFirestore;

    public FormularioGasto() {
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
    public static FormularioGasto newInstance(String param1, String param2) {
        FormularioGasto fragment = new FormularioGasto();
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

        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_datos_gasto, container, false);

        // Configura el spinner de tipo de gasto usando un array de strings en recursos.
        mSpinnerTipo = view.findViewById(R.id.spinner_tipo_gasto);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tipos_de_gasto, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerTipo.setAdapter(adapter);

        // Bind de los campos del formulario
        mEditCantidad = view.findViewById(R.id.txt_edit_cantidad_gasto);
        mEditDescripcion = view.findViewById(R.id.txt_edit_descripcion_gasto);

        // Obtiene el boton para guardar gasto y configura su evento onClick para "guardar".
        FloatingActionButton fabGuardarGasto = view.findViewById(R.id.fab_guardar_gasto);
        fabGuardarGasto.setOnClickListener(componentView ->
        {
            GuardarGasto();
            Toast.makeText(getActivity(), R.string.temporal_proceso, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        return view;
    }

    private void GuardarGasto() {

        //TODO: Mejorar validacion y manejo de errores.
        if (mEditCantidad.getText().toString().isEmpty()) return;

        //TODO: Usar el id real del usuario para crear gasto.
        final String idUsuario = "pXqACrhyoqZpdw8n11n64749YuI2";
        final double cantidad = Double.parseDouble(mEditCantidad.getText().toString());
        final String descripcion = mEditDescripcion.getText().toString();
        final int CATEGORIA_SNACKS = 0;
        final TipoGasto tipo = getTipoDeGasto(mSpinnerTipo.getSelectedItemPosition());

        if (cantidad < 0) return;

        Gasto gasto = new Gasto(idUsuario, cantidad, tipo, CATEGORIA_SNACKS, descripcion);

        Log.i("FormularioGasto", "Tipo de gasto (" + mSpinnerTipo.getSelectedItemPosition() + "): " + gasto.getTipo());

        //TODO: Hacer mas obvio cuando hay un error en la creacion del documento en Firestore.
        mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                .add(gasto.asDoc())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Documento agregado con ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error agregando documento: ", e);
                    }
                });
    }

    private TipoGasto getTipoDeGasto(int opcion) {
        switch (opcion) {
            case 0:
                return TipoGasto.NECESARIO;
            case 1:
                return TipoGasto.ENTRETENIMIENTO;
            case 2:
                return TipoGasto.EXTRA;
            default:
                throw new IllegalArgumentException();
        }
    }
}