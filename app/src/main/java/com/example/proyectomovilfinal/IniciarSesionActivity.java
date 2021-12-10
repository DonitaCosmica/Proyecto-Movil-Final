package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomovilfinal.data.DatosUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniciarSesionActivity extends AppCompatActivity {

    private static final String TAG = "IniciarSesionActivity";

    private FirebaseAuth mAuth;

    private EditText mEditTxtCampoCorreo;
    private EditText mEditTxtCampoPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEditTxtCampoCorreo = findViewById(R.id.edit_login_correo);
        mEditTxtCampoPassword = findViewById(R.id.edit_login_password);

        Button btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        btnIniciarSesion.setOnClickListener(view -> {
            iniciarSesion();
        });

        TextView txtIrARegsistro = findViewById(R.id.link_ir_a_registrarse);
        txtIrARegsistro.setOnClickListener(view -> {
            irARegistro();
        });
    }

    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void iniciarSesion(){
        // Obtener valores de los campos.
        String correo = mEditTxtCampoCorreo.getText().toString().trim();
        String password = mEditTxtCampoPassword.getText().toString().trim();

        // Validar que el correo no este vacio
        if(correo.isEmpty()){
            mEditTxtCampoCorreo.setError(getString(R.string.err_campo_vacio));
            mEditTxtCampoCorreo.requestFocus();
            return;
        }

        // Validar el formato del correo.
        Pattern pattern = Pattern.compile(getString(R.string.regex_validar_email));
        // Comparar
        Matcher mather = pattern.matcher(correo);
        // Asignar resultado a un bool.
        boolean correoValido = mather.matches();

        // Validar si el correo cumple el formato requerido.
        if(!correoValido){
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
        // Comparar
        Matcher matcher = patron.matcher(password);
        // Asignar resultado a un bool.
        boolean passValida = matcher.matches();

        // Si la contraseña no cumple con el formato marca error.
        if (!passValida) {
            mEditTxtCampoPassword.setError(getString(R.string.err_formato_password));
            mEditTxtCampoPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {

                    // El inicio de sesion es exitoso, obtener el usuario de Firebase Auth.
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) return;

                    // Guardar credenciales en SharedPreferences.
                    Util.guardarCredenciales(IniciarSesionActivity.this, correo, password);

                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.inicio_sesion_en_proceso),
                            Toast.LENGTH_SHORT
                    ).show();

                    final Intent i = new Intent(getApplicationContext(), MainActivity.class);

                    // Obtener tipo de usuario.
                    FirebaseFirestore.getInstance().collection(DatosUsuario.NOMBRE_COLECCION_FIRESTORE)
                            .document(user.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {

                                DatosUsuario datosUsuario = DatosUsuario.fromDoc(documentSnapshot);
                                Log.i(TAG, "Tipo de usuario: " + datosUsuario.getTipo());
                                i.putExtra(Util.KEY_ARG_TIPO_USUARIO, datosUsuario.getTipo());
                                startActivity(i);
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error obteniendo tipo de usuario");
                                startActivity(i);
                            });
                })
                .addOnFailureListener(e -> {
                    // El inicio de sesion fallo.
                    Log.w(TAG, "signInWithEmail:failure", e);

                    if (e instanceof FirebaseAuthInvalidUserException) {
                        // No hay un usuario con el correo especificado.
                        Toast.makeText(getApplicationContext(), getString(R.string.err_usuario_no_registrado), Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // La contraseña es incorrecta.
                        Toast.makeText(getApplicationContext(), getString(R.string.err_credenciales), Toast.LENGTH_SHORT).show();
                    } else {
                        // Hubo otro tipo de error.
                        Toast.makeText(getApplicationContext(), getString(R.string.err_inicio_sesion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irARegistro(){
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);
    }
}