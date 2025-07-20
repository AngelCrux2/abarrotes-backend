package com.example.tienda.modelo;


public class CompraDTO {
    private String codigo;
    private int cantidadSeleccionada;
    private double costo;

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidadSeleccionada() {
        return cantidadSeleccionada;
    }

    public void setCantidadSeleccionada(int cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }
}

