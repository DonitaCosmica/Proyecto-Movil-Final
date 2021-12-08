package com.example.proyectomovilfinal.data;

public enum TipoUsuario {
    NORMAL(0),
    ANALISTA(1),
    ADMINISTRADOR(2);

    private final int valor;
    TipoUsuario(int valor) { this.valor = valor; }

    public int getValor() { return valor; }
}
