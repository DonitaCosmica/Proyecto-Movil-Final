package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IniciarSesionActivity extends AppCompatActivity {

    private static final String TAG = "IniciarSesionActivity";

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

        String correo = text_correo_inicio.getText().toString();
        String password = text_contrasena_inicio.getText().toString();

        if(!correo.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(correo.trim(), password.trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                Util.guardarCredenciales(IniciarSesionActivity.this, correo, password);

                                Toast.makeText(getApplicationContext(), "iniciando sesion.", Toast.LENGTH_SHORT).show();

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
        }else{
            Toast.makeText(getApplicationContext(), "Debe completar los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void irRegistrarse(View view){
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);
    }
}