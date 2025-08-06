package com.example.tienda.servicio;

import com.example.tienda.modelo.Departamento;
import com.example.tienda.modelo.DepartamentoDTO;
import com.example.tienda.modelo.Productos;
import com.example.tienda.repositorio.DepartamentoRepositorio;
import com.example.tienda.repositorio.ProductosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    @Autowired
    DepartamentoRepositorio departamentoRepositorio;

    @Autowired
    ProductosRepositorio productosRepositorio;

    public List<Departamento> findAll() {
        return departamentoRepositorio.findAll();
    }

    public Departamento buscarPorId(Long id) {
        return departamentoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
    }
    @Transactional(readOnly = true)
    public List<DepartamentoDTO> obtenerVentasPorDepartamento() {
        List<Object[]> raw = departamentoRepositorio.obtenerVentasPorDepartamentoRaw();
        return raw.stream()
                .map(r -> new DepartamentoDTO((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

}

