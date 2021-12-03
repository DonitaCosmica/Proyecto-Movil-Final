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
 * Una subclase de {@link Fragment} que crea y modifica gastos usando
 * GUI.
 * EL metodo de factory {@link FormularioGasto#newInstance} puede ser
 * usado para crear nuevas instancias de este fragmento.
 */
public class FormularioGasto extends Fragment {

    private static final String TAG = "Formulario Gasto";

    private static final String ID_GASTO = "ID_GASTO";
    private static final DecimalFormat formatoDinero = new DecimalFormat("0.00");

    private String mIdGasto;

    // Vistas del formulario.
    private Spinner mSpinnerTipo;
    private EditText mEditCantidad;
    private EditText mEditDescripcion;

    //TODO: Usar el id y presupuesto diario reales del usuario para crear gasto.
    private final String ID_USUARIO_FAKE = "pXqACrhyoqZpdw8n11n64749YuI2";

    private Gasto mGasto;
    private boolean mNuevoGasto = true;
    private double mPresupuestoDiario;

    private FirebaseFirestore mFirestore;

    public FormularioGasto() {
        // Constructor publico vacio requerido.
    }

    /**
     * Crea una nueva instancia de {@link FormularioGasto} usando
     * los argumentos especificados.
     *
     * @param idGasto El id del gasto, para editar.
     * @return Una nueva instancia de FormularioGasto.
     */
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

        // Obtiene el id del gasto de los argumentos recibidos.
        if (getArguments() != null) {
            mIdGasto = getArguments().getString(ID_GASTO);
        }

        mFirestore = FirebaseFirestore.getInstance();

        // Si este fragmento recibe el id del gasto, significa que debe actualizarlo (en vez
        // de crear uno nuevo).
        if (mIdGasto != null && !mIdGasto.isEmpty()) {
            getGastoDeFirestore(mIdGasto);
            mNuevoGasto = false;
        }

        // Obtener presupuesto diario.
        getPresupuestoDiario(ID_USUARIO_FAKE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Infla el layout para el fragmento.
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

            if (getActivity() != null) getActivity().finish();
        });

        return view;
    }

    private void getGastoDeFirestore(final String idGasto) {
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

    private void getPresupuestoDiario(final String idUsuario) {
        mFirestore.collection("usuarios").document(idUsuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
//                        Usuario datosUsuario = Usuario.fromDoc(snapshot);

//                        mPresupuestoDiario = datosUsuario.presupuesto;
                        mPresupuestoDiario = 500.0f;
                    }
                });
    }

    private void guardarGasto() {

        //TODO: Mejorar validacion y manejo de errores.
        if (mEditCantidad.getText().toString().isEmpty()) return;

        final double cantidad = Double.parseDouble(mEditCantidad.getText().toString());
        final String descripcion = mEditDescripcion.getText().toString();
        final int CATEGORIA_SNACKS = 0;
        final TipoGasto tipo = getTipoDeGasto(mSpinnerTipo.getSelectedItemPosition());

        if (cantidad < 0) return;

        //TODO: Calcular el gasto total del dia.
        if (cantidad > mPresupuestoDiario) {
            enviarNotificacionDePresupuesto(cantidad - mPresupuestoDiario);
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

    /**
     * Envía una notificación de alerta con el presupuesto excedido.
     *
     * @param gastoExtra la diferencia entre el gasto total diario y el presupuesto.
     */
    private void enviarNotificacionDePresupuesto(double gastoExtra) {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), getString(R.string.id_canal_notif))
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

    /**
     * Convierte un número entero entre 0 y 2 en un valor del enum {@link TipoGasto}.
     *
     * @param opcion un número entero que represente un valor de {@link TipoGasto}
     * @return el valor de {@link TipoGasto} correspondiente.
     * @throws IllegalArgumentException si el número no tiene el valor de ninguno del enum.
     */
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