package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomovilfinal.data.DatosUsuario;
import com.example.proyectomovilfinal.data.Subcategoria;
import com.example.proyectomovilfinal.data.TipoGasto;
import com.example.proyectomovilfinal.data.TipoUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatosUsuarioActivity extends AppCompatActivity {

    private static final String TAG = "DatosUsuarioActivity";

    public static final String ID_USUARIO_DATOS = "ID_USUARIO";
    public static final String EMAIL_USUARIO_DATOS = "EMAIL_USUARIO";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private EditText mEditTxtCampoNombre;
    private EditText mEditTxtCampoApellido;
    private EditText mEditTxtCampoEdad;
    private EditText mEditTxtCampoPresupuesto;

    private String mIdUsuario;
    private String mEmailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_usuario);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser usuario = mAuth.getCurrentUser();

        if (usuario != null) {
            mIdUsuario = usuario.getUid();
            mEmailUsuario = usuario.getEmail();
        }

        mFirestore = FirebaseFirestore.getInstance();

        mEditTxtCampoNombre = findViewById(R.id.edit_datos_nombre);
        mEditTxtCampoApellido = findViewById(R.id.edit_datos_apellido);
        mEditTxtCampoEdad = findViewById(R.id.edit_datos_edad);
        mEditTxtCampoPresupuesto = findViewById(R.id.edit_datos_presupuesto);

        Button btnConfirmarDatos = findViewById(R.id.btn_confirmar_datos_usuario);
        btnConfirmarDatos.setOnClickListener(view -> {

            if (usuario != null) confirmarDatosUsuario();
        });
    }

    private void confirmarDatosUsuario() {

        String nombre = mEditTxtCampoNombre.getText().toString();
        String apellido = mEditTxtCampoApellido.getText().toString();
        int edad = Integer.parseInt(mEditTxtCampoEdad.getText().toString());
        double presupuesto = Double.parseDouble(mEditTxtCampoPresupuesto.getText().toString());

        DatosUsuario datosUsuario = new DatosUsuario(
                mIdUsuario,
                nombre,
                apellido,
                mEmailUsuario,
                edad,
                presupuesto,
                TipoUsuario.NORMAL
        );

        Subcategoria subcategoriaSnacks = new Subcategoria(
                mIdUsuario, getString(R.string.nombre_subcategorias_snacks), TipoGasto.EXTRA
        );
        mFirestore.collection(Subcategoria.NOMBRE_COLECCION_FIRESTORE)
                .add(subcategoriaSnacks.asDoc());

        mFirestore.collection(DatosUsuario.NOMBRE_COLECCION_FIRESTORE)
                .document(mIdUsuario).set(datosUsuario.asDoc())
                .addOnSuccessListener(unused -> {
                    Log.i(TAG, "Datos del usuario guardados.");

                    Intent intent = new Intent(DatosUsuarioActivity.this, MainActivity.class);
                    intent.putExtra(Util.KEY_ARG_TIPO_USUARIO, datosUsuario.getTipo());
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error guardando datos del usuario: ", e);
                });
    }
}