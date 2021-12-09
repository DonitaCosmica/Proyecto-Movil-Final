package com.example.proyectomovilfinal.data;

import com.example.proyectomovilfinal.Util;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class Subcategoria {

    public static final String NOMBRE_COLECCION_FIRESTORE = "categorias";

    // Nombres de los campos en los documentos de Firestore.
    public static final String CAMPO_ID_USUARIO = "idUsuario";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_TIPO = "tipo";

    private String mIdSubcategoria;
    private String mIdUsuario;
    private String mNombre;
    private TipoGasto mTipo;

    private Subcategoria() {}

    public Subcategoria(String idUsuario, String nombre, TipoGasto tipo) {
        mIdUsuario = idUsuario;
        mNombre = nombre;
        mTipo = tipo;
    }

    public HashMap<String, Object> asDoc() {
        HashMap<String, Object> documento = new HashMap<>();

        documento.put(CAMPO_ID_USUARIO, mIdUsuario);
        documento.put(CAMPO_NOMBRE, mNombre);
        documento.put(CAMPO_TIPO, mTipo);

        return documento;
    }

    public static Subcategoria fromDoc(DocumentSnapshot doc) {

        if (!doc.exists()) return new Subcategoria();

        Subcategoria subcategoria = new Subcategoria(
            doc.getString(CAMPO_ID_USUARIO),
            doc.getString(CAMPO_NOMBRE),
            Util.getTipoDeGasto(Math.toIntExact(doc.getLong(CAMPO_TIPO)))
        );

        subcategoria.setIdSubcategoria(doc.getId());

        return subcategoria;
    }

    //region Get/Set
    public String getIdUsuario() {
        return mIdUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        mIdUsuario = idUsuario;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String nombre) {
        mNombre = nombre;
    }

    public String getIdSubcategoria() {
        return mIdSubcategoria;
    }

    public void setIdSubcategoria(String idSubcategoria) {
        mIdSubcategoria = idSubcategoria;
    }

    public TipoGasto getTipo() {
        return mTipo;
    }

    public void setTipo(TipoGasto tipo) {
        mTipo = tipo;
    }
    //endregion
}
