package com.example.proyectomovilfinal.data;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class DatosUsuario {

    public static final String NOMBRE_COLECCION_FIRESTORE = "usarios";

    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_APELLIDO = "apellido";
    public static final String CAMPO_EMAIL = "email";
    public static final String CAMPO_EDAD = "edad";
    public static final String CAMPO_PRESUPUESTO = "presupuesto";
    public static final String CAMPO_TIPO = "tipo";

    private String mIdUsuario;
    private String mNombre;
    private String mApellido;
    private String mEmail;
    private int mEdad;
    private double mPresupuesto;
    private int mTipo;

    private DatosUsuario() {}

    public DatosUsuario(String idUsuario, String nombre, String apellido, int edad, double presupuesto, int tipo) {
        mIdUsuario = idUsuario;
        mNombre = nombre;
        mApellido = apellido;
        mEdad = edad;
        mPresupuesto = presupuesto;
        mTipo = tipo;
    }

    public HashMap<String, Object> asDoc() {
        HashMap<String, Object> documento = new HashMap<>();

        documento.put(CAMPO_NOMBRE, mNombre);
        documento.put(CAMPO_APELLIDO, mApellido);
        documento.put(CAMPO_EMAIL, mEmail);
        documento.put(CAMPO_EDAD, mEdad);
        documento.put(CAMPO_PRESUPUESTO, mPresupuesto);
        documento.put(CAMPO_TIPO, mTipo);

        return documento;
    }

    public static DatosUsuario fromDoc(DocumentSnapshot doc) {

        if (!doc.exists()) return new DatosUsuario();

        DatosUsuario datosUsuario = new DatosUsuario(
            doc.getString(CAMPO_NOMBRE),
            doc.getString(CAMPO_APELLIDO),
            doc.getString(CAMPO_EMAIL),
            Math.toIntExact(doc.getLong(CAMPO_EDAD)),
            doc.getDouble(CAMPO_PRESUPUESTO),
            Math.toIntExact(doc.getLong(CAMPO_TIPO))
        );

        datosUsuario.mIdUsuario = doc.getId();

        return datosUsuario;
    }

    //region Get/Set

    public String getIdUsuario() {
        return mIdUsuario;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String nombre) {
        mNombre = nombre;
    }

    public String getApellido() {
        return mApellido;
    }

    public void setApellido(String apellido) {
        mApellido = apellido;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public int getEdad() {
        return mEdad;
    }

    public void setEdad(int edad) {
        mEdad = edad;
    }

    public double getPresupuesto() {
        return mPresupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        mPresupuesto = presupuesto;
    }

    public int getTipo() {
        return mTipo;
    }

    public void setTipo(int tipo) {
        mTipo = tipo;
    }

    //endregion
}
