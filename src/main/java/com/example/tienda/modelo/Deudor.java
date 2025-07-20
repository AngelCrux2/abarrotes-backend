package com.example.tienda.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Deudor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;

    @OneToMany(mappedBy = "deudor", cascade = CascadeType.ALL)
    private List<MovimientoCredito> movimientos;

    public BigDecimal getTotalDeuda() {
        return movimientos.stream()
                .filter(m -> m.getTipo() == MovimientoCredito.TipoMovimiento.SOLICITUD)
                .flatMap(m -> m.getDetalles().stream())
                .map(MovimientoDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalAbonado() {
        return movimientos.stream()
                .filter(m -> m.getTipo() == MovimientoCredito.TipoMovimiento.ABONO || m.getTipo() == MovimientoCredito.TipoMovimiento.LIQUIDACION)
                .map(MovimientoCredito::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSaldo() {
        return getTotalDeuda().subtract(getTotalAbonado());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<MovimientoCredito> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoCredito> movimientos) {
        this.movimientos = movimientos;
    }
}
