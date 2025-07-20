package com.example.tienda.repositorio;

import com.example.tienda.modelo.CompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CompraDetalleRepositorio extends JpaRepository<CompraDetalle, Long> {
    @Query("SELECT cd.producto.nombre AS nombre, SUM(cd.cantidad) AS total, " +
            "FUNCTION('WEEK', c.fecha) AS semana " +
            "FROM CompraDetalle cd JOIN cd.compra c " +
            "WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY cd.producto.nombre, FUNCTION('WEEK', c.fecha) " +
            "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidosPorSemana(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);
}