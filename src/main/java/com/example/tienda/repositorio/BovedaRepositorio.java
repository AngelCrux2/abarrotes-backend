package com.example.tienda.repositorio;

import com.example.tienda.modelo.Boveda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BovedaRepositorio extends JpaRepository<Boveda, Long> {
    @Query("SELECT b FROM Boveda b")
    Optional<Boveda> findFirst();

}
