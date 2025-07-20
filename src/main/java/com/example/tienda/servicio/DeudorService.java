package com.example.tienda.servicio;

import com.example.tienda.modelo.Deudor;
import com.example.tienda.repositorio.DeudorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeudorService {
    @Autowired
    private DeudorRepositorio deudorRepositorio;
    public List<Deudor> listar() { return deudorRepositorio.findAll(); }
    public Optional<Deudor> obtener(Long id) { return deudorRepositorio.findById(id); }
    public Deudor guardar(Deudor d) { return deudorRepositorio.save(d); }
}