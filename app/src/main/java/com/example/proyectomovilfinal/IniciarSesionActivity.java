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
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniciarSesionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText text_correo_inicio;
    private EditText text_contrasena_inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        text_correo_inicio = findViewById(R.id.text_correo_inicio);
        text_contrasena_inicio = findViewById(R.id.text_contrasena_inicio);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    public void iniciarSesion(View view){
        if (!text_correo_inicio.getText().toString().isEmpty() && !text_contrasena_inicio.toString().isEmpty()) {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher mather = pattern.matcher(text_correo_inicio.getText().toString());
            boolean correo = mather.matches();
            if(correo == true){
                if (text_contrasena_inicio.length() >= 8 && text_contrasena_inicio.length() <= 12) {
                        //Pattern patron = Pattern.compile(".+[0-9]+.+");
                        Pattern patron = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
                        Matcher matcher = patron.matcher(text_contrasena_inicio.getText().toString());
                        boolean numero = matcher.matches();
                        if (numero == true) {
            mAuth.signInWithEmailAndPassword(text_correo_inicio.getText().toString().trim(), text_contrasena_inicio.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(), "iniciando sesion", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }

                        private void updateUI(FirebaseUser user) {
                        }
                    });
                        } else {
                            Toast.makeText(IniciarSesionActivity.this, "La contraseña debe incluir almenos 1 numero", Toast.LENGTH_SHORT).show();
                        }
            }else {
                Toast.makeText(IniciarSesionActivity.this, "La contraseña debe contener de 8 a 12 caracteres", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(IniciarSesionActivity.this, "El correo no tiene un formato correcto", Toast.LENGTH_SHORT).show();
        }
    }else {
        Toast.makeText(IniciarSesionActivity.this, "Los espacios estan vacios", Toast.LENGTH_SHORT).show();
    }
    }

    public void irRegistrarse(View view){
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);
    }
}