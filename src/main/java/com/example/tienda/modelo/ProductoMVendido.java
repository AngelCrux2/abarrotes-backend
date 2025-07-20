package com.example.tienda.modelo;

public class ProductoMVendido {
    private String nombreProducto;
    private Long totalVendido;
    private Integer numeroSemana;

    public ProductoMVendido(String nombreProducto, Long totalVendido, Integer numeroSemana) {
        this.nombreProducto = nombreProducto;
        this.totalVendido = totalVendido;
        this.numeroSemana = numeroSemana;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getNumeroSemana() {
        return numeroSemana;
    }

    public void setNumeroSemana(Integer numeroSemana) {
        this.numeroSemana = numeroSemana;
    }

    public Long getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(Long totalVendido) {
        this.totalVendido = totalVendido;
    }


}
