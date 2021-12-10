package com.example.proyectomovilfinal.paginas;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.proyectomovilfinal.MainActivity;
import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.DatosUsuario;
import com.example.proyectomovilfinal.data.Gasto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

/**
 * Una subclase de {@link Fragment} que crea y modifica gastos usando
 * GUI.
 * EL metodo de factory {@link FormularioGasto#newInstance} puede ser
 * usado para crear nuevas instancias de este fragmento.
 */
public class FormularioGasto extends Fragment {

    private static final String TAG = "Formulario Gasto";

    private static final String ID_GASTO = "ID_GASTO";
    private static final String SUBCATEGORIA_GASTO = "SUBCATEGORIA_GASTO";
    private static final String TIPO_GASTO = "TIPO_GASTO";

    // Argumentos del fragment.
    private String mIdGasto;
    private int mTipoDeGasto;
    private String mIdCategoria;

    // Vistas del formulario.
    private EditText mEditCantidad;
    private EditText mEditDescripcion;

    private Gasto mGasto;
    private boolean mNuevoGasto = true;
    private double mPresupuestoDiario;
    private double mGastoTotalDia;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public FormularioGasto() {
        // Constructor publico default requerido.
    }

    /**
     * Crea una nueva instancia de {@link FormularioGasto} usando
     * los argumentos especificados.
     *
     * @param idGasto El id del gasto, para editar.
     * @return Una nueva instancia de FormularioGasto.
     */
    public static FormularioGasto newInstance(String idGasto, int tipoDeGasto, String subcategoria) {
        FormularioGasto fragment = new FormularioGasto();
        Bundle args = new Bundle();

        args.putString(ID_GASTO, idGasto);
        args.putInt(TIPO_GASTO, tipoDeGasto);
        args.putString(SUBCATEGORIA_GASTO, subcategoria);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtiene el id del gasto de los argumentos recibidos.
        if (getArguments() != null) {
            mIdGasto = getArguments().getString(ID_GASTO);
            mTipoDeGasto = getArguments().getInt(TIPO_GASTO);
            mIdCategoria = getArguments().getString(SUBCATEGORIA_GASTO);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Si este fragmento recibe el id del gasto, significa que debe actualizarlo (en vez
        // de crear uno nuevo).
        if (mIdGasto != null && !mIdGasto.isEmpty()) {
            getGastoDeFirestore(mIdGasto);
            mNuevoGasto = false;
        }

        // Obtener presupuesto diario.
        getPresupuestoDiario();
        getGastoTotal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Infla el layout para el fragmento.
        View view = inflater.inflate(R.layout.fragment_datos_gasto, container, false);

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
        fabGuardarGasto.setOnClickListener(componentView -> guardarGasto());

        return view;
    }

    private void getGastoDeFirestore(final String idGasto) {
        mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE).document(idGasto)
            .get()
            .addOnSuccessListener(snapshot -> {
                mGasto = Gasto.fromDoc(snapshot);

                mTipoDeGasto = mGasto.getTipo().getValor();
                mIdCategoria = mGasto.getCategoria();

                mEditCantidad.setText(String.valueOf(mGasto.getCantidad()));
                mEditDescripcion.setText(mGasto.getDescripcion());
            });
    }

    private void getPresupuestoDiario() {

        String idUsuario = mAuth.getUid();

        if (idUsuario != null) {

            mFirestore.collection("usuarios").document(idUsuario)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        DatosUsuario datosUsuario = DatosUsuario.fromDoc(snapshot);

                        mPresupuestoDiario = datosUsuario.getPresupuesto();
                    });
        }
    }

    private void getGastoTotal() {

        String idUsuario = mAuth.getUid();

        Date hoy = Util.obtenerFechaActual();

        if (idUsuario == null) return;

        mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Gasto.CAMPO_ID_USUARIO, idUsuario)
                .whereGreaterThanOrEqualTo(Gasto.CAMPO_FECHA, hoy)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                    {
                        double gastoTotal = 0;

                        for (QueryDocumentSnapshot doc : task.getResult())
                        {
                            if (doc.getDouble(Gasto.CAMPO_CANTIDAD) != null) {
                                gastoTotal += doc.getDouble(Gasto.CAMPO_CANTIDAD);
                            }
                        }

                        mGastoTotalDia = gastoTotal;

                    } else {
                        Log.d(TAG, "Error obteniendo documentos");
                    }
                });
    }

    private void guardarGasto() {

        if (mEditCantidad.getText().toString().isEmpty()) {
            mEditCantidad.setError(getString(R.string.err_cantidad_invalida));
            return;
        }

        final double cantidad = Double.parseDouble(mEditCantidad.getText().toString());
        final String descripcion = mEditDescripcion.getText().toString();

        if (cantidad < 0) {
            mEditCantidad.setError(getString(R.string.err_cantidad_mayor_que_0));
            return;
        }

        mGastoTotalDia += cantidad;

        if (mGastoTotalDia > mPresupuestoDiario) {
            enviarNotificacionDePresupuesto(mGastoTotalDia - mPresupuestoDiario);
        }

        String idUsuario = mAuth.getUid();

        if (idUsuario == null) return;

        if (mNuevoGasto) {
            mGasto = new Gasto(idUsuario, cantidad, Util.getTipoDeGasto(mTipoDeGasto), mIdCategoria, descripcion);

            mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                    .add(mGasto.asDoc())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Documento agregado con ID: " + documentReference.getId());
                        Toast.makeText(getActivity(), R.string.gasto_agregado, Toast.LENGTH_SHORT).show();

                        if (getActivity() != null) getActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), getString(R.string.err_registro_gasto), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error agregando documento: ", e);
                    });
        }
        else {
            mGasto.setTipo(Util.getTipoDeGasto(mTipoDeGasto));
            mGasto.setCantidad(cantidad);
            mGasto.setCategoria(mIdCategoria);
            mGasto.setDescripcion(descripcion);

            mFirestore.collection(Gasto.NOMBRE_COLECCION_FIRESTORE)
                    .document(mIdGasto)
                    .update(mGasto.asDoc())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Gasto actualizado con exito");
                        Toast.makeText(getActivity(), R.string.gasto_modificado, Toast.LENGTH_SHORT).show();

                        if (getActivity() != null) getActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), getString(R.string.err_registro_gasto), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error agregando documento: ", e);
                    });
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

        Context contexto = getActivity();

        if (contexto != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto, getString(R.string.id_canal_notif))
                    .setSmallIcon(R.drawable.icono_sin_fondo)
                    .setContentTitle(getString(R.string.titulo_notificacion_presupuesto))
                    .setContentText(getString(R.string.contenido_notificacion_presupuesto) + Util.fDinero.format(gastoExtra))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            int idNotificacion = 1;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

            notificationManager.notify(idNotificacion, builder.build());

            Log.i(TAG, "Notificacion enviada");
        }
    }
}