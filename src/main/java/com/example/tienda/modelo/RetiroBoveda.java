package com.example.tienda.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public class RetiroBoveda {
    @Id @GeneratedValue
    private Long id;
    private BigDecimal monto;
    private LocalDate fecha;

    public RetiroBoveda(BigDecimal monto, LocalDate fecha) {
        this.monto = monto;
        this.fecha = fecha;
    }

    public RetiroBoveda() {

    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
