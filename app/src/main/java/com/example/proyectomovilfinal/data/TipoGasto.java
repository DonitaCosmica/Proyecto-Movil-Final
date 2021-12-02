package com.example.proyectomovilfinal.data;

public enum TipoGasto {
    NECESARIO(0),
    ENTRETENIMIENTO(1),
    EXTRA(2);

    private final int valor;
    TipoGasto(int valor) {
        this.valor = valor;
    }

    public int getValor() { return valor; }
}
