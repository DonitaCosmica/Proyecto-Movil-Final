package com.example.proyectomovilfinal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText text_correo_registrarse;
    private EditText text_contrasena_registrarse;
    private EditText text_contrasenaConfirmacion_registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        text_correo_registrarse = findViewById(R.id.text_correo_registrarse);
        text_contrasena_registrarse = findViewById(R.id.text_contrasena_registrarse);
        text_contrasenaConfirmacion_registrarse = findViewById(R.id.text_contrasenaConfirmacion_registrarse);

        Button btnRegistrarse = findViewById(R.id.btn_registrarse_registrarse);
        btnRegistrarse.setOnClickListener(view -> {
            registrarUsuario();
        });

        Button btnIrIniciarSesion = findViewById(R.id.btn_ir_inicio_sesion);
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

        String correo = text_correo_registrarse.getText().toString();
        String password = text_contrasena_registrarse.getText().toString();
        String confPassword = text_contrasenaConfirmacion_registrarse.getText().toString();

        if (!correo.isEmpty() && !password.isEmpty() && !confPassword.isEmpty()) {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher mather = pattern.matcher(correo);
            boolean correoValido = mather.matches();

            if (correoValido) {

                if (password.length() >= 8 && password.length() <= 12) {

                    Pattern patron = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
                    Matcher matcher = patron.matcher(password);
                    boolean passwordValido = matcher.matches();

                    if (passwordValido) {

                        if (password.equals(confPassword)) {

                            mAuth.createUserWithEmailAndPassword(correo.trim(), password.trim())
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "createUserWithEmail:success");
                                            Toast.makeText(RegistrarseActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            Util.guardarCredenciales(RegistrarseActivity.this, correo, password);

                                            Intent i = new Intent(RegistrarseActivity.this, DatosUsuarioActivity.class);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                Toast.makeText(RegistrarseActivity.this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(RegistrarseActivity.this, "Error, verifica los datos", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                        } else {
                            text_contrasenaConfirmacion_registrarse.setError("Las contraseñas no coinciden");
                        }
                    } else {
                        text_contrasena_registrarse.setError("La contraseña debe tener un número y una mayúscula");
                    }

                } else {
                    text_contrasena_registrarse.setError("La contraseña debe tener entre 8 y 12 caracteres");
                }

            } else {
                text_correo_registrarse.setError("Escribe una dirección de correo válida.");
            }


        }else {
            Toast.makeText(RegistrarseActivity.this, "Los campos estan vacios", Toast.LENGTH_SHORT).show();
        }
    }

    public void irIniciarSesion(){
        Intent i = new Intent(this, IniciarSesionActivity.class);
        startActivity(i);
    }
}