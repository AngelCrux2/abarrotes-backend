package com.example.tienda.modelo;

public class DepartamentoDTO {
    private String nombreDepartamento;
    private Long totalVendido;

    public DepartamentoDTO(String nombreDepartamento, Long totalVendido) {
        this.nombreDepartamento = nombreDepartamento;
        this.totalVendido = totalVendido;
    }

    public Long getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(Long totalVendido) {
        this.totalVendido = totalVendido;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
}
