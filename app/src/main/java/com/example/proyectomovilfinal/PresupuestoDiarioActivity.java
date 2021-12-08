package com.example.proyectomovilfinal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomovilfinal.data.DatosUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * PresupuestoDiarioActivity es una actividad simple donde el usuario
 * puede introducir un nuevo presupuesto diario.
 *
 * El usuario puede confirmar o cancelar el cambio. Si lo confirma, su
 * presupuesto se actualiza en Firestore.
 */
public class PresupuestoDiarioActivity extends AppCompatActivity {

    private static final String TAG = "PresupuestoDiarioActivity";

    private FirebaseFirestore mFirestore;

    private DatosUsuario mDatosUsuario;

    private EditText mEditPresupuestoDiario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto_diario);

        mEditPresupuestoDiario = findViewById(R.id.edit_presupuesto_diario);
        Button btnConfirmarPresupuesto = findViewById(R.id.btn_confirmar_presupuesto);
        Button btnCancelarPresupuesto = findViewById(R.id.btn_cancelar_presupuesto);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario != null)
            obtenerPresupuestoDiario(usuario.getUid());

        btnConfirmarPresupuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float presupuesto = Float.parseFloat(mEditPresupuestoDiario.getText().toString());

                if (presupuesto < 0 || presupuesto > Util.PRESUPUESTO_MAXIMO) {
                    mEditPresupuestoDiario.setError(getString(R.string.err_presupuesto_fuera_rango));

                } else {
                    guardarPresupuestoDiario(presupuesto);
                }

                finish();
            }
        });

        btnCancelarPresupuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void obtenerPresupuestoDiario(final String idUsuario) {
        mFirestore.collection(DatosUsuario.NOMBRE_COLECCION_FIRESTORE).document(idUsuario)
                .get()
                .addOnSuccessListener(snapshot -> {
                    mDatosUsuario = DatosUsuario.fromDoc(snapshot);
                    mEditPresupuestoDiario.setText(String.valueOf(mDatosUsuario.getPresupuesto()));
                });
    }

    private void guardarPresupuestoDiario(float nuevoPresupuesto) {

        mDatosUsuario.setPresupuesto(nuevoPresupuesto);

        Log.i(TAG, "El nuevo presupuesto para el usuario con ID "
                + mDatosUsuario.getIdUsuario() + " es $" + mDatosUsuario.getPresupuesto());

        mFirestore.collection(DatosUsuario.NOMBRE_COLECCION_FIRESTORE)
                .document(mDatosUsuario.getIdUsuario())
                .update(mDatosUsuario.asDoc());
    }
}