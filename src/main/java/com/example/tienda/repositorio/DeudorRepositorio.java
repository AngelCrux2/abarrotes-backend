package com.example.tienda.repositorio;

import com.example.tienda.modelo.Deudor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeudorRepositorio extends JpaRepository<Deudor, Long> {

}