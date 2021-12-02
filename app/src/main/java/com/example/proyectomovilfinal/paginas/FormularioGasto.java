package com.example.proyectomovilfinal.paginas;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.MainActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.Gasto;
import com.example.proyectomovilfinal.data.TipoGasto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormularioGasto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormularioGasto extends Fragment {

    private static final String TAG = "Formulario Gasto";

    private static final String ID_GASTO = "ID_GASTO";
    private static final DecimalFormat formatoDinero = new DecimalFormat("0.00");

    private String mIdGasto;

    private Spinner mSpinnerTipo;
    private EditText mEditCantidad;
    private EditText mEditDescripcion;

    private Gasto mGasto;
    private boolean mNuevoGasto = true;

    private FirebaseFirestore mFirestore;

    public FormularioGasto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idGasto Parameter 1.
     * @return A new instance of fragment DatosGasto.
     */
    // TODO: Rename and change types and number of parameters
    public static FormularioGasto newInstance(String idGasto) {
        FormularioGasto fragment = new FormularioGasto();
        Bundle args = new Bundle();
        args.putString(ID_GASTO, idGasto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIdGasto = getArguments().getString(ID_GASTO);
        }

        mFirestore = FirebaseFirestore.getInstance();

        // Si este fragmento recibe el id del gasto, significa que debe actualizarlo (en vez
        // de crear).
        if (mIdGasto != null && !mIdGasto.isEmpty()) {
            obtenerGastoDeFirestore(mIdGasto);
            mNuevoGasto = false;
        }
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

        TextView titulo = view.findViewById(R.id.titulo_agregar_gasto);

        if (mNuevoGasto)
        {
            titulo.setText(R.string.titulo_nuevo_gasto);
        } else
        {
            titulo.setText(R.string.titulo_editar_gasto);
        }

        // Obtiene el boton para guardar gasto y configura su evento onClick para "guardar".
        FloatingActionButton fabGuardarGasto = view.findViewById(R.id.fab_guardar_gasto);
        fabGuardarGasto.setOnClickListener(componentView ->
        {
            guardarGasto();
            Toast.makeText(getActivity(), R.string.temporal_proceso, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        return view;
    }

    private void obtenerGastoDeFirestore(final String idGasto) {
        mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE).document(idGasto)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        mGasto = Gasto.fromDoc(snapshot);

                        mEditCantidad.setText(String.valueOf(mGasto.getCantidad()));
                        mEditDescripcion.setText(mGasto.getDescripcion());
                        mSpinnerTipo.setSelection(mGasto.getTipo().getValor());
                    }
                });
    }

    private void guardarGasto() {

        //TODO: Mejorar validacion y manejo de errores.
        if (mEditCantidad.getText().toString().isEmpty()) return;

        //TODO: Usar el id y presupuesto diario reales del usuario para crear gasto.
        final String ID_USUARIO_FAKE = "pXqACrhyoqZpdw8n11n64749YuI2";
        final double PRESUPUESTO_DIARIO_FAKE = 500.0;

        final double cantidad = Double.parseDouble(mEditCantidad.getText().toString());
        final String descripcion = mEditDescripcion.getText().toString();
        final int CATEGORIA_SNACKS = 0;
        final TipoGasto tipo = getTipoDeGasto(mSpinnerTipo.getSelectedItemPosition());

        if (cantidad < 0) return;

        if (cantidad > PRESUPUESTO_DIARIO_FAKE) {
            enviarNotificacionDePresupuesto(cantidad - PRESUPUESTO_DIARIO_FAKE);
        }

        if (mNuevoGasto) {
            mGasto = new Gasto(ID_USUARIO_FAKE, cantidad, tipo, CATEGORIA_SNACKS, descripcion);

            //TODO: Hacer mas obvio cuando hay un error en la creacion del documento en Firestore.
            mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                    .add(mGasto.asDoc())
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
        else {
            mGasto.setTipo(tipo);
            mGasto.setCantidad(cantidad);
            mGasto.setCategoria(CATEGORIA_SNACKS);
            mGasto.setDescripcion(descripcion);

            mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                    .document(mIdGasto)
                    .update(mGasto.asDoc());
        }
    }

    private void enviarNotificacionDePresupuesto(double gastoExtra) {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), MainActivity.ID_CANAL_RECORDATORIOS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.titulo_notificacion_presupuesto))
                .setContentText(getString(R.string.contenido_notificacion_presupuesto) + formatoDinero.format(gastoExtra))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int idNotificacion = 1;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        notificationManager.notify(idNotificacion, builder.build());

        Log.i(TAG, "Notificacion enviada");
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