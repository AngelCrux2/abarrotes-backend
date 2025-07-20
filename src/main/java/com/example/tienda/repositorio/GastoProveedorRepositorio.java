package com.example.tienda.repositorio;

import com.example.tienda.modelo.GastoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GastoProveedorRepositorio extends JpaRepository<GastoProveedor, Long> {
    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoProveedor g WHERE g.fecha = :fecha AND g.origen = 'caja'")
    BigDecimal restGastosCajaPorFecha(@Param("fecha") LocalDate fecha);
    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoProveedor g WHERE g.fecha = :fecha AND g.origen = 'boveda'")
    BigDecimal sumBovedaByFecha(@Param("fecha") LocalDate fecha);

    List<GastoProveedor> findAllByOrderByFechaDesc();

    List<GastoProveedor> findByFechaAndOrigenIn(LocalDate fecha, List<String> origenes);

    @Query("SELECT g FROM GastoProveedor g WHERE g.fecha = :fecha AND g.proveedor = :proveedor AND g.origen = :origen")
    Optional<GastoProveedor> findByFechaAndProveedorAndOrigen(
            @Param("fecha") LocalDate fecha,
            @Param("proveedor") String proveedor,
            @Param("origen") String origen);

}