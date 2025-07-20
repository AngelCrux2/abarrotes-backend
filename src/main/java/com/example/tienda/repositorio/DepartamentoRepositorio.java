package com.example.tienda.repositorio;

import com.example.tienda.modelo.Departamento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepositorio extends JpaRepository<Departamento, Long> {
    Optional<Departamento> findByNombreIgnoreCase(String nombre);

    @Query(value = """
    SELECT d.nombre AS nombreDepartamento,
           SUM(cd.cantidad) AS totalVendido
    FROM compra_detalle cd
    JOIN productos p ON cd.producto_id = p.id
    JOIN departamento d ON p.departamento_id = d.id
    GROUP BY d.nombre
    ORDER BY totalVendido DESC
""", nativeQuery = true)
    List<Object[]> obtenerVentasPorDepartamentoRaw();


}

