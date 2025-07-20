package com.example.tienda.repositorio;


import com.example.tienda.modelo.FinDia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FinDiaRepositorio extends JpaRepository<FinDia, Long> {
    Optional<FinDia> findFirstByOrderByFechaDesc();

    Optional<FinDia> findByFecha(LocalDate fecha);

}
