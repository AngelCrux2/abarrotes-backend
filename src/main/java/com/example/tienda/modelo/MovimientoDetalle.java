package com.example.tienda.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class MovimientoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cantidad;
    private BigDecimal precioUnitario;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = true)
    private Productos producto;

    @ManyToOne
    private MovimientoCredito movimiento;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public MovimientoCredito getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoCredito movimiento) {
        this.movimiento = movimiento;
    }
}

