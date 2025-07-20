package com.example.tienda.seguridad;

import com.example.tienda.modelo.Departamento;
import com.example.tienda.repositorio.DepartamentoRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class departamentos {

    @Autowired
    private DepartamentoRepositorio departamentoRepository;

    @PostConstruct
    public void init() {
        if (departamentoRepository.count() == 0) {
            List<String> nombres = List.of(
                    "Abarrotes", "Lácteos", "Carnes frías", "Frutas y verduras", "Panadería",
                    "Bebidas", "Botanas", "Limpieza", "Higiene personal", "Enlatados",
                    "Congelados", "Cereales y granos", "Mascotas", "Dulcería"
            );

            for (String nombre : nombres) {
                departamentoRepository.save(new Departamento(nombre));
            }
        }
    }
}
