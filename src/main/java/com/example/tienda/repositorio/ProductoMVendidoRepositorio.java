package com.example.tienda.repositorio;

import com.example.tienda.modelo.CompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoMVendidoRepositorio extends JpaRepository<CompraDetalle, Long> {
    @Query(value = """
        SELECT p.nombre AS nombreProducto,
               SUM(cd.cantidad) AS totalVendido,
               EXTRACT(WEEK FROM c.fecha) AS numeroSemana
        FROM compra_detalle cd
        JOIN productos p ON cd.producto_id = p.id
        JOIN compra c ON cd.compra_id = c.id
        GROUP BY numeroSemana, p.nombre
        ORDER BY numeroSemana, totalVendido DESC
        """, nativeQuery = true)
    List<Object[]> obtenerProductosMasVendidosPorSemanaRaw();
}
