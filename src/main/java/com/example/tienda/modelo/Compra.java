package com.example.tienda.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
Este modelo Representa una venta o transacción realizada.
Se registra cada vez que se vende algo.
 */
@Entity
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private TipoCompra tipo;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<CompraDetalle> detalles = new ArrayList<>();


    public enum TipoCompra {
        VENTA,
        LIQUIDACION_DEUDA,
        ABONO_DEUDA
    }
    public enum FormaPago {
        EFECTIVO,
        TRANSFERENCIA
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }
    public TipoCompra getTipo() {
        return tipo;
    }

    public void setTipo(TipoCompra tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CompraDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<CompraDetalle> detalles) {
        this.detalles = detalles;
    }
}

