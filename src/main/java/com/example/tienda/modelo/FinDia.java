package com.example.tienda.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class FinDia {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate fecha;
    private BigDecimal saldoInicial;
    private BigDecimal totalVentas;
    private BigDecimal totalGastos;
    private BigDecimal retiro;
    private BigDecimal saldoFinal;


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

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(BigDecimal totalGastos) {
        this.totalGastos = totalGastos;
    }

    public BigDecimal getRetiro() {
        return retiro;
    }

    public void setRetiro(BigDecimal retiro) {
        this.retiro = retiro;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

}


