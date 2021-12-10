package com.example.proyectomovilfinal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEditTxtCampoCorreo;
    private EditText mEditTxtCampoPassword;
    private EditText mEditTxtCampoPasswordConfirmacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEditTxtCampoCorreo = findViewById(R.id.text_correo_registrarse);
        mEditTxtCampoPassword = findViewById(R.id.text_contrasena_registrarse);
        mEditTxtCampoPasswordConfirmacion = findViewById(R.id.text_contrasenaConfirmacion_registrarse);

        Button btnRegistrarse = findViewById(R.id.btn_registrar_usuario);
        btnRegistrarse.setOnClickListener(view -> {
            registrarUsuario();
        });

        TextView btnIrIniciarSesion = findViewById(R.id.btn_ir_inicio_sesion);
        btnIrIniciarSesion.setOnClickListener(view -> {
            irIniciarSesion();
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            currentUser.reload();
        }
    }

    public void registrarUsuario() {

        String correo =  mEditTxtCampoCorreo.getText().toString().trim();
        String password = mEditTxtCampoPassword.getText().toString().trim();
        String confPassword = mEditTxtCampoPasswordConfirmacion.getText().toString().trim();

        // Validar que el correo no este vacio
        if (correo.isEmpty()) {
            mEditTxtCampoCorreo.setError(getString(R.string.err_campo_vacio));
            mEditTxtCampoCorreo.requestFocus();
            return;
        }

        // Valdar el formato del correo
        Pattern pattern = Pattern.compile(getString(R.string.regex_validar_email));
        // comparar
        Matcher mather = pattern.matcher(correo);
        // asignacion del resultado a un boleano
        boolean correoValidacion = mather.matches();

        // Si el correo no tiene formato correcto, marca error
        if (!correoValidacion) {
            mEditTxtCampoCorreo.setError(getString(R.string.err_correo_no_valido));
            mEditTxtCampoCorreo.requestFocus();
            return;
        }

        // Validar que la password no este vacia
        if (password.isEmpty()) {
            mEditTxtCampoPassword.setError(getString(R.string.err_campo_vacio));
            mEditTxtCampoPassword.requestFocus();
            return;
        }

        // Validar que la longitud del password este en el rango de caracteres.
        if (password.length() < 8 || password.length() > 12) {
            mEditTxtCampoPassword.setError(getString(R.string.err_tam_password));
            mEditTxtCampoPassword.requestFocus();
            return;
        }

        // Validar formato del password.
        Pattern patron = Pattern.compile(getString(R.string.regex_validar_contrasena));
        //comparar
        Matcher matcher = patron.matcher(password);
        //asignacion del resultado a un boleano
        boolean pass_valida = matcher.matches();

        //si la contraseña no cumple con el formato marca error
        if (!pass_valida) {
            mEditTxtCampoPassword.setError(getString(R.string.err_formato_password));
            mEditTxtCampoPassword.requestFocus();
            return;
        }

        // Validar que la confirmacion de password no este vacia
        if (confPassword.isEmpty()) {
            mEditTxtCampoPasswordConfirmacion.setError(getString(R.string.err_campo_vacio));
            mEditTxtCampoPasswordConfirmacion.requestFocus();
            return;
        }

        // Validar que la contraseña y confirmacion de contraseña sean iguales.
        if (!password.equals(confPassword)) {
            mEditTxtCampoPasswordConfirmacion.setError(getString(R.string.err_comparacion_password));
            mEditTxtCampoPasswordConfirmacion.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo.trim(), password.trim())
                .addOnSuccessListener(authResult -> {
                    // El registro fue exitoso, seguir con el proceso.
                    Log.d(TAG, "createUserWithEmail:success");

                    Toast.makeText(RegistrarseActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();

                    Util.guardarCredenciales(RegistrarseActivity.this, correo, password);

                    Intent i = new Intent(RegistrarseActivity.this, DatosUsuarioActivity.class);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {

                        // El registro falló porque ya hay un usuario existente.
                        Toast.makeText(RegistrarseActivity.this, getString(R.string.err_usuario_existente), Toast.LENGTH_SHORT).show();

                    } else {
                        // Si el registro falla por otra razón, mostrar error.
                        Log.w(TAG, "createUserWithEmail:failure", e);
                        Toast.makeText(RegistrarseActivity.this, getString(R.string.err_registro_fallido), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void irIniciarSesion(){
        Intent i = new Intent(this, IniciarSesionActivity.class);
        startActivity(i);
    }
}