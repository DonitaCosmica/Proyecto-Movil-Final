package com.example.proyectomovilfinal.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Gasto {

    public static final String NOMBRE_COLECCION_FIRESTORE = "gastos";

     // Nombres de los campos en los documentos de Firestore.
    public static final String CAMPO_ID_USUARIO = "idUsuario";
    public static final String CAMPO_CANTIDAD = "cantidad";
    public static final String CAMPO_ID_CATEGORIA = "categoria";
    public static final String CAMPO_TIPO = "tipo";
    public static final String CAMPO_DESCRIPCION = "descripcion";
    public static final String CAMPO_FECHA = "fecha";

    private String mIdUsuario;
    private double mCantidad;
    private String mIdCategoria;
    private TipoGasto mTipo;
    private String mDescripcion;

    @ServerTimestamp
    private Date mFecha;

    private Gasto(){}

    public Gasto(String idUsuario, double cantidad, TipoGasto tipo, String categoria, String descripcion) {
        mIdUsuario = idUsuario;
        mCantidad = cantidad;
        mTipo = tipo;
        mIdCategoria = categoria;
        mDescripcion = descripcion;
        mFecha = new Date();
    }

    @NonNull
    @Override
    public String toString() {
        return "El " + mFecha.toString() + " el usuario gasto " + mCantidad;
    }

    public HashMap<String, Object> asDoc() {
        HashMap<String, Object> documento = new HashMap<>();

        documento.put(CAMPO_ID_USUARIO, mIdUsuario);
        documento.put(CAMPO_CANTIDAD, mCantidad);
        documento.put(CAMPO_ID_CATEGORIA, mIdCategoria);
        documento.put(CAMPO_TIPO, mTipo);
        documento.put(CAMPO_DESCRIPCION, mDescripcion);
        documento.put(CAMPO_FECHA, mFecha);

        return documento;
    }

    public static Gasto fromDoc(DocumentSnapshot doc) {

        if (!doc.exists()) return new Gasto();

        Gasto gasto = new Gasto(
            doc.getString(CAMPO_ID_USUARIO),
            doc.getDouble(CAMPO_CANTIDAD),
            tipoDesdeString(doc.getString(CAMPO_TIPO)),
            doc.getString(CAMPO_ID_CATEGORIA),
            doc.getString(CAMPO_DESCRIPCION)
        );

        gasto.setFecha(doc.getDate(CAMPO_FECHA));
        return gasto;
    }

    // Get/Set
    public String getIdUsuario() {
        return mIdUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        mIdUsuario = idUsuario;
    }

    public double getCantidad() {
        return mCantidad;
    }

    public void setCantidad(double cantidad) {
        mCantidad = cantidad;
    }

    public String getDescripcion() {
        return mDescripcion;
    }

    public void setDescripcion(String descripcion) {
        mDescripcion = descripcion;
    }

    public Date getFecha() {
        return mFecha;
    }

    // Este setter solo existe para que el gasto pueda ser obtenido
    // de Firestore. No se debe modificar la fecha desde la aplicacion,
    // unicamente cuando se crea el gasto.
    private void setFecha(Date fecha) {
        mFecha = fecha;
    }

    public String getCategoria() {
        return mIdCategoria;
    }

    public void setCategoria(String categoria) {
        mIdCategoria = categoria;
    }

    public TipoGasto getTipo() {
        return mTipo;
    }

    public void setTipo(TipoGasto tipo) {
        mTipo = tipo;
    }

    //TODO: Crear utilidad con este metodo.
    public static TipoGasto tipoDesdeString(final String tipoStr) {
        switch (tipoStr) {
            case "ENTRETENIMIENTO":
                return TipoGasto.ENTRETENIMIENTO;
            case "EXTRA":
                return TipoGasto.EXTRA;

            default: return TipoGasto.NECESARIO;
        }
    }

    //TODO: Crear funcion de utilidad para esto.
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
}
