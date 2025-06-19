package com.example.tienda.repositorio;

import com.example.tienda.modelo.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepositorio extends JpaRepository<Usuarios,Long> {
    Optional<Usuarios> findByNombre(String nombre);
}

