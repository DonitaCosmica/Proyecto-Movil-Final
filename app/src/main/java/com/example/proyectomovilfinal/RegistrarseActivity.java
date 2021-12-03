package com.example.proyectomovilfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegistrarseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText text_correo_registrarse;
    private EditText text_contrasena_registrarse;
    private EditText text_contrasenaConfirmacion_registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        text_correo_registrarse = findViewById(R.id.text_correo_registrarse);
        text_contrasena_registrarse = findViewById(R.id.text_contrasena_registrarse);
        text_contrasenaConfirmacion_registrarse = findViewById(R.id.text_contrasenaConfirmacion_registrarse);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            currentUser.reload();
        }
        
    }

    public void registrarUsuario(View view) {


        if (!text_correo_registrarse.getText().toString().isEmpty() && !text_contrasena_registrarse.toString().isEmpty()
                && !text_contrasenaConfirmacion_registrarse.getText().toString().isEmpty()) {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher mather = pattern.matcher(text_correo_registrarse.getText().toString());
            boolean correo = mather.matches();
            if(correo == true){
            if (text_contrasena_registrarse.length() >= 8 && text_contrasena_registrarse.length() <= 12 &&
                    text_contrasenaConfirmacion_registrarse.length() >= 8 && text_contrasenaConfirmacion_registrarse.length() <= 12) {
                if (text_contrasena_registrarse.getText().toString().equals(text_contrasenaConfirmacion_registrarse.getText().toString())) {
                    //Pattern patron = Pattern.compile(".+[0-9]+.+");
                    Pattern patron = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
                    Matcher matcher = patron.matcher(text_contrasena_registrarse.getText().toString());
                    boolean numero = matcher.matches();
                    if (numero == true) {
                        mAuth.createUserWithEmailAndPassword(text_correo_registrarse.getText().toString().trim(), text_contrasena_registrarse.getText().toString().trim())
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            Toast.makeText(RegistrarseActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(i);
                                            //updateUI(user);
                                        } else {
                                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                Toast.makeText(RegistrarseActivity.this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(RegistrarseActivity.this, "Error, verifica los datos", Toast.LENGTH_SHORT).show();
                                                updateUI(null);
                                            }
                                        }
                                    }

                                    private void updateUI(Object o) {
                                    }
                                });
                    } else {
                        Toast.makeText(RegistrarseActivity.this, "La contraseña debe incluir almenos 1 numero", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrarseActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(RegistrarseActivity.this, "La contraseña debe contener de 8 a 12 caracteres", Toast.LENGTH_SHORT).show();
            }
            }else {
                Toast.makeText(RegistrarseActivity.this, "El correo no tiene un formato correcto", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(RegistrarseActivity.this, "Los espacios estan vacios", Toast.LENGTH_SHORT).show();
        }
    }

    public void irIniciarSesion(View view){
        Intent i = new Intent(this, IniciarSesionActivity.class);
        startActivity(i);
    }
}