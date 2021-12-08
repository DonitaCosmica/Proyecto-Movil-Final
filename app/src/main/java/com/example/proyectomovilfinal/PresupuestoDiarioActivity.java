package com.example.proyectomovilfinal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomovilfinal.data.DatosUsuario;
import com.example.proyectomovilfinal.data.TipoUsuario;
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

    private EditText mEditPresupuestoDiario;

    private DatosUsuario mDatosUsuario;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto_diario);

        mEditPresupuestoDiario = findViewById(R.id.edit_presupuesto_diario);
        Button btnConfirmarPresupuesto = findViewById(R.id.btn_confirmar_presupuesto);
        Button btnCancelarPresupuesto = findViewById(R.id.btn_cancelar_presupuesto);

        mFirestore = FirebaseFirestore.getInstance();


        mDatosUsuario = new DatosUsuario(
                "pXqACrhyoqZpdw8n11n64749YuI2",
                "Juan",
                "Ranchero de Vaca",
                "juan@su.rancho.com",
                25,
                200.0,
                TipoUsuario.NORMAL
        );

        btnConfirmarPresupuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float presupuesto = Float.parseFloat(mEditPresupuestoDiario.getText().toString());
                guardarPresupuestoDiario(presupuesto);
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

    private void guardarPresupuestoDiario(float nuevoPresupuesto) {

        mDatosUsuario.setPresupuesto(nuevoPresupuesto);

        Log.i(TAG, "El nuevo presupuesto para el usuario con ID "
                + mDatosUsuario.getIdUsuario() + " es $" + mDatosUsuario.getPresupuesto());

        mFirestore.collection(DatosUsuario.NOMBRE_COLECCION_FIRESTORE)
                .document(mDatosUsuario.getIdUsuario())
                .update(mDatosUsuario.asDoc());
    }
}