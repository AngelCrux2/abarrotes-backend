package com.example.tienda.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Boveda {
    @Id @GeneratedValue
    private Long id;
    private BigDecimal totalAcumulado;

    public Boveda() {
    }

    public Boveda(BigDecimal totalAcumulado) {
        this.totalAcumulado = totalAcumulado;
    }

    public BigDecimal getTotalAcumulado() {
        return totalAcumulado;
    }

    public void setTotalAcumulado(BigDecimal totalAcumulado) {
        this.totalAcumulado = totalAcumulado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
