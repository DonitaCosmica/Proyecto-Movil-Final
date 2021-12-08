package com.example.proyectomovilfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.example.proyectomovilfinal.data.TipoUsuario;

import java.text.DecimalFormat;

public class Util {

    private static final String TAG = "Util";

    public static final DecimalFormat fDinero = new DecimalFormat("0.00");

    public static void guardarCredenciales(Context context, String correo, String password) {

        Resources res = context.getResources();

        // Obtener el archivo de SharedPreferences.
        SharedPreferences sharedPref = context.getSharedPreferences(
                res.getString(R.string.nombre_archivo_preferencias), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Guardar las credenciales del usuario.
        editor.putString(res.getString(R.string.pref_key_email_usuario), correo);
        editor.putString(res.getString(R.string.pref_key_password_usuario), password);

        editor.apply();

        Log.i(TAG, "Credenciales guardadas para el usuario con email: " + correo);
    }

    public static void reiniciarCredenciales(Context context) {

        Resources res = context.getResources();

        // Obtener el archivo de SharedPreferences.
        SharedPreferences sharedPref = context.getSharedPreferences(
                res.getString(R.string.nombre_archivo_preferencias), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Guardar las credenciales del usuario.
        editor.putString(res.getString(R.string.pref_key_email_usuario), "");
        editor.putString(res.getString(R.string.pref_key_password_usuario), "");

        editor.apply();

        Log.i(TAG, "Credenciales borradas de SharedPreferences.");
    }

    public static TipoUsuario getTipoFromValor(long valor) {

        int valorEntero = Math.toIntExact(valor);

        switch (valorEntero) {
            case 1:
                return TipoUsuario.ANALISTA;
            case 2:
                return TipoUsuario.ADMINISTRADOR;

            default:
                return TipoUsuario.NORMAL;
        }
    }
}
