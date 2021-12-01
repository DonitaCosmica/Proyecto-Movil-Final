package com.example.proyectomovilfinal.data;

import androidx.annotation.NonNull;

import java.util.Date;

public class Gasto {

    private String mIdUsuario;
    private double mCantidad;
    private int mCategoria;
    private TipoGasto mTipo;
    private String mDescripcion;
    private Date mFecha;

    public Gasto(String idUsuario, double cantidad, String descripcion, Date fecha, int categoria, TipoGasto tipo) {
        mIdUsuario = idUsuario;
        mCantidad = cantidad;
        mDescripcion = descripcion;
        mFecha = fecha;
        mCategoria = categoria;
        mTipo = tipo;
    }

    @NonNull
    @Override
    public String toString() {
        return "El " + mFecha.toString() + " el usuario gasto " + mCantidad;
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

    public int getCategoria() {
        return mCategoria;
    }

    public void setCategoria(int categoria) {
        mCategoria = categoria;
    }

    public TipoGasto getTipo() {
        return mTipo;
    }

    public void setTipo(TipoGasto tipo) {
        mTipo = tipo;
    }
}
