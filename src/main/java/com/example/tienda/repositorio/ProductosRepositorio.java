package com.example.tienda.repositorio;

import com.example.tienda.modelo.Departamento;
import com.example.tienda.modelo.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductosRepositorio extends JpaRepository<Productos,Long>{
    Optional<Productos> findByCodigo(String codigo);
    Optional<Productos> findByid(Long id);
    List<Productos> findAllByOrderByNombreAsc();


    @Query("SELECT p FROM Productos p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :valor, '%')) OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :valor, '%'))")
    List<Productos> buscarPorNombreOCodigoParcial(@Param("valor") String valor, Pageable pageable);

    Optional<Productos> findByNombreIgnoreCase(String nombre);
    List<Productos> findByNombreContainingIgnoreCase(String nombre);
    List<Productos> findByDepartamento(Departamento departamento);

    @Query(value = """
    SELECT * FROM productos p
    WHERE p.id NOT IN (
        SELECT cd.producto_id
        FROM compra_detalle cd
        JOIN compra c ON cd.compra_id = c.id
        WHERE c.fecha >= CURRENT_DATE - INTERVAL '7 days'
          AND cd.producto_id IS NOT NULL
    )
""", nativeQuery = true)
    List<Productos> findProductosNoVendidosEnUltimos7Dias();


}

