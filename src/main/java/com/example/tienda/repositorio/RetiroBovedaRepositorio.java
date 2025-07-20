package com.example.tienda.repositorio;

import com.example.tienda.modelo.RetiroBoveda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetiroBovedaRepositorio extends JpaRepository<RetiroBoveda, Long> {

    List<RetiroBoveda> findAllByOrderByFechaDesc();
}
