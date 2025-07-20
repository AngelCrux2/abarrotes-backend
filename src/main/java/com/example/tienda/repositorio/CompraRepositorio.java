package com.example.tienda.repositorio;

import com.example.tienda.modelo.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CompraRepositorio extends JpaRepository<Compra, Long> {
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE DATE(c.fecha) = :fecha")
    BigDecimal sumTotalByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE c.fecha >= :inicio AND c.fecha < :fin AND c.formaPago <> 'TRANSFERENCIA'")
    BigDecimal sumTotalByFechaExcluyendoTransferencia(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE c.fecha >= :inicio AND c.fecha < :fin AND c.formaPago = 'TRANSFERENCIA'")
    BigDecimal sumTotalTransferenciasByFecha(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );


}
