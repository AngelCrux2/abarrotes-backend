package com.example.tienda.repositorio;

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
}

