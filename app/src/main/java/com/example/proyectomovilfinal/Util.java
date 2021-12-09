package com.example.proyectomovilfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.example.proyectomovilfinal.data.TipoGasto;
import com.example.proyectomovilfinal.data.TipoUsuario;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    private static final String TAG = "Util";

    public static final DecimalFormat fDinero = new DecimalFormat("0.00");

    public static DateFormat fFecha = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());

    public static final double PRESUPUESTO_MAXIMO = 1000000.0;

    //region Keys
    public static final String KEY_ARG_ID_USUARIO = "ID_USUARIO";
    public static final String KEY_ARG_TIPO_USUARIO = "TIPO_USUARIO";

    public static final String TIPO_CATEGORIA = "TIPO_DE_CATEGORIA";
    //endregion

    public static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 101;

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

    public static TipoGasto tipoDesdeString(final String tipoStr) {
        switch (tipoStr) {
            case "ENTRETENIMIENTO":
                return TipoGasto.ENTRETENIMIENTO;
            case "EXTRA":
                return TipoGasto.EXTRA;

            default: return TipoGasto.NECESARIO;
        }
    }

    /**
     * Convierte un número entero entre 0 y 2 en un valor del enum {@link TipoGasto}.
     *
     * @param opcion un número entero que represente un valor de {@link TipoGasto}
     * @return el valor de {@link TipoGasto} correspondiente.
     * @throws IllegalArgumentException si el número no tiene el valor de ninguno del enum.
     */
    public static TipoGasto getTipoDeGasto(int opcion) {
        switch (opcion) {
            case 0:
                return TipoGasto.NECESARIO;
            case 1:
                return TipoGasto.ENTRETENIMIENTO;
            case 2:
                return TipoGasto.EXTRA;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Date obtenerFechaActual() {
        Calendar fechaHoraDeHoy = Calendar.getInstance();
        fechaHoraDeHoy.set(Calendar.HOUR, 0);
        fechaHoraDeHoy.set(Calendar.MINUTE, 0);
        fechaHoraDeHoy.set(Calendar.SECOND, 0);
        fechaHoraDeHoy.set(Calendar.MILLISECOND, 0);

        return fechaHoraDeHoy.getTime();
    }
}
