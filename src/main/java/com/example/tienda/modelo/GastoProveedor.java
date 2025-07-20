package com.example.tienda.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public class GastoProveedor {
    @Id
    @GeneratedValue
    private Long id;
    private String proveedor;
    private BigDecimal monto;
    private String descripcion;
    private LocalDate fecha;
    @Column(nullable = false)
    private String origen;

    public GastoProveedor() {}

    public GastoProveedor(String proveedor, BigDecimal monto, String descripcion, LocalDate fecha, String origen) {
        this.proveedor = proveedor;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.origen = origen;
    }

    public Long getId() {
        return id;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
