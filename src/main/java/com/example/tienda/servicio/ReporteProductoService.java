package com.example.tienda.servicio;

import com.example.tienda.modelo.ProductoMVendido;
import com.example.tienda.modelo.Productos;
import com.example.tienda.repositorio.ProductoMVendidoRepositorio;
import com.example.tienda.repositorio.ProductosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteProductoService {

    @Autowired
    private ProductoMVendidoRepositorio reporteRepo;
    @Autowired
    private ProductosRepositorio productosRepositorio;

    public List<ProductoMVendido> obtenerProductosMasVendidosPorSemana() {
        List<Object[]> resultados = reporteRepo.obtenerProductosMasVendidosPorSemanaRaw();
        Map<Integer, ProductoMVendido> masVendidosPorSemana = new HashMap<>();

        for (Object[] row : resultados) {
            String nombre = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            Integer semana = ((Number) row[2]).intValue();

            if (!masVendidosPorSemana.containsKey(semana)) {
                masVendidosPorSemana.put(semana, new ProductoMVendido(nombre, total, semana));
            }
        }

        return new ArrayList<>(masVendidosPorSemana.values());
    }
    public List<ProductoMVendido> obtenerTodosLosProductosVendidosPorSemana() {
        List<Object[]> resultados = reporteRepo.obtenerProductosMasVendidosPorSemanaRaw();
        List<ProductoMVendido> lista = new ArrayList<>();

        for (Object[] row : resultados) {
            String nombre = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            Integer semana = ((Number) row[2]).intValue();

            lista.add(new ProductoMVendido(nombre, total, semana));
        }

        return lista;
    }

    public List<Productos> obtenerProductosNoVendidosEnUltimos7Dias() {
        return productosRepositorio.findProductosNoVendidosEnUltimos7Dias();
    }
}
