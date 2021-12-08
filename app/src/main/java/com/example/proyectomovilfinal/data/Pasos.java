package com.example.proyectomovilfinal.data;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;

public class Pasos {

    public static final String NOMBRE_COLECCION_FIRESTORE = "pasos";

    public static final String CAMPO_ID_USUARIO = "idUsuario";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_CANTIDAD = "cantidad";

    private String mIdUsuario;
    private Date mFecha;
    private int mCantidad;

    private Pasos() {}

    public Pasos(String idUsuario, Date fecha, int cantidad) {
        mIdUsuario = idUsuario;
        mFecha = fecha;
        mCantidad = cantidad;
    }

    public HashMap<String, Object> asDoc() {
        HashMap<String, Object> documento = new HashMap<>();

        documento.put(CAMPO_ID_USUARIO, mIdUsuario);
        documento.put(CAMPO_CANTIDAD, mCantidad);
        documento.put(CAMPO_FECHA, mFecha);

        return documento;
    }

    public static Pasos fromDoc(DocumentSnapshot doc) {

        if (!doc.exists()) return new Pasos();

        Pasos registroDePasos = new Pasos(
            doc.getString(CAMPO_ID_USUARIO),
            doc.getDate(CAMPO_FECHA),
            Math.toIntExact(doc.getLong(CAMPO_CANTIDAD))
        );

        return registroDePasos;
    }

    //region Get/Set
    public String getIdUsuario() {
        return mIdUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        mIdUsuario = idUsuario;
    }

    public Date getFecha() {
        return mFecha;
    }

    public void setFecha(Date fecha) {
        mFecha = fecha;
    }

    public int getCantidad() {
        return mCantidad;
    }

    public void setCantidad(int cantidad) {
        mCantidad = cantidad;
    }
    //endregion
}
