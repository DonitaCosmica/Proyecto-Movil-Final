package com.example.proyectomovilfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        String correo = mEditTxtCampoCorreo.getText().toString();
        String password = mEditTxtCampoPassword.getText().toString();

        if(!correo.isEmpty() && !password.isEmpty()) {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher mather = pattern.matcher(correo);
            boolean correoValido = mather.matches();
          
            if(correoValido){
                if (password.length() >= 8 && password.length() <= 12) {
                  
                    //Pattern patron = Pattern.compile(".+[0-9]+.+");
                    Pattern patron = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
                    Matcher matcher = patron.matcher(password);
                    boolean passwordValido = matcher.matches();
                        if (passwordValido == true) {
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
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        } else {
                            mEditTxtCampoPassword.setError(getString(R.string.err_correo_no_valido));
                        }
                } else {
                    mEditTxtCampoPassword.setError(getString(R.string.err_correo_no_valido));
                }
            } else {
                mEditTxtCampoCorreo.setError(getString(R.string.err_correo_no_valido));
            }
        } else {
            Toast.makeText(IniciarSesionActivity.this, "Los espacios estan vacios", Toast.LENGTH_SHORT).show();
        }
    }

    private void irARegistro(){
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);
    }
}