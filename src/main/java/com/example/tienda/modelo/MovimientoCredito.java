package com.example.tienda.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MovimientoCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @ManyToOne
    private Deudor deudor;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoDetalle> detalles = new ArrayList<>();

    public enum TipoMovimiento {
        SOLICITUD,
        ABONO,
        LIQUIDACION
    }

    public enum MetodoPago {
        EFECTIVO,
        TRANSFERENCIA
    }

    @PrePersist
    public void prePersist() {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public Deudor getDeudor() {
        return deudor;
    }

    public void setDeudor(Deudor deudor) {
        this.deudor = deudor;
    }

    public List<MovimientoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<MovimientoDetalle> detalles) {
        this.detalles = detalles;
    }
}
