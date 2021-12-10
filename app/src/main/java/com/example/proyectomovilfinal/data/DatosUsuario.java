package com.example.proyectomovilfinal.data;

import com.example.proyectomovilfinal.Util;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DatosUsuario {

    public static final String NOMBRE_COLECCION_FIRESTORE = "usuarios";

    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_APELLIDO = "apellido";
    public static final String CAMPO_EMAIL = "email";
    public static final String CAMPO_EDAD = "edad";
    public static final String CAMPO_PRESUPUESTO = "presupuesto";
    public static final String CAMPO_TIPO = "tipo";
    public static final String CAMPO_PASOS_PROMEDIO = "pasosPromedio";
    public static final String CAMPO_GASTOS_POR_TIPO = "gastosPorTipo";
    public static final String CAMPO_FECHA_REGISTRO = "fecha";

    private String mIdUsuario;
    private String mNombre;
    private String mApellido;
    private String mEmail;
    private int mEdad;
    private double mPresupuesto;
    private TipoUsuario mTipo;

    private double mPasosPromedio;
    private List<Double> mGastoDiarioPorTipo;

    private Date mFechaRegistro;

    private DatosUsuario() {}

    public DatosUsuario(
            String idUsuario, String nombre, String apellido, String email,
            int edad, double presupuesto, TipoUsuario tipo) {
        mIdUsuario = idUsuario;
        mNombre = nombre;
        mApellido = apellido;
        mEdad = edad;
        mEmail = email;
        mPresupuesto = presupuesto;
        mTipo = tipo;

        mPasosPromedio = 0;
        mGastoDiarioPorTipo = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));

        mFechaRegistro = new Date();
    }

    public HashMap<String, Object> asDoc() {
        HashMap<String, Object> documento = new HashMap<>();

        documento.put(CAMPO_NOMBRE, mNombre);
        documento.put(CAMPO_APELLIDO, mApellido);
        documento.put(CAMPO_EMAIL, mEmail);
        documento.put(CAMPO_EDAD, mEdad);
        documento.put(CAMPO_PRESUPUESTO, mPresupuesto);
        documento.put(CAMPO_TIPO, mTipo.getValor());
        documento.put(CAMPO_PASOS_PROMEDIO, mPasosPromedio);
        documento.put(CAMPO_GASTOS_POR_TIPO, mGastoDiarioPorTipo);
        documento.put(CAMPO_FECHA_REGISTRO, mFechaRegistro);

        return documento;
    }

    public static DatosUsuario fromDoc(DocumentSnapshot doc) {

        if (!doc.exists()) return new DatosUsuario();

        DatosUsuario datosUsuario = new DatosUsuario(
            doc.getId(),
            doc.getString(CAMPO_NOMBRE),
            doc.getString(CAMPO_APELLIDO),
            doc.getString(CAMPO_EMAIL),
            Math.toIntExact(doc.getLong(CAMPO_EDAD)),
            doc.getDouble(CAMPO_PRESUPUESTO),
            Util.getTipoFromValor(doc.getLong(CAMPO_TIPO))
        );

        datosUsuario.mIdUsuario = doc.getId();
        datosUsuario.mFechaRegistro = doc.getDate(CAMPO_FECHA_REGISTRO);

        datosUsuario.mGastoDiarioPorTipo = (List<Double>) doc.get(CAMPO_GASTOS_POR_TIPO);
        datosUsuario.mPasosPromedio = doc.getDouble(CAMPO_PASOS_PROMEDIO);

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
        return mTipo.getValor();
    }

    public void setTipo(int tipo) {
        mTipo = Util.getTipoFromValor(tipo);
    }

    public Date getFechaRegistro() {
        return mFechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        mFechaRegistro = fechaRegistro;
    }

    public double getPasosPromedio() {
        return mPasosPromedio;
    }

    public void setPasosPromedio(double pasosPromedio) {
        mPasosPromedio = pasosPromedio;
    }

    public List<Double> getGastoDiarioPorTipo() {
        return mGastoDiarioPorTipo;
    }

    public void setGastoDiarioPorTipo(List<Double> gastoDiarioPorTipo) {
        mGastoDiarioPorTipo = gastoDiarioPorTipo;
    }

    //endregion
}
