package com.example.tienda.servicio;

import com.example.tienda.modelo.CompraDTO;
import com.example.tienda.modelo.Productos;
import com.example.tienda.repositorio.ProductosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class ProductosService {

    @Autowired
    ProductosRepositorio productosRepositorio;

    public List<Productos> buscarPorNombreOCodigoParcial(String valor, int limite) {
        System.out.println("→ Buscando: " + valor); // Agrega este log
        Pageable pageable = PageRequest.of(0, limite);
        return productosRepositorio.buscarPorNombreOCodigoParcial(valor, pageable);
    }

    public Productos guardarOActualizarProducto(Productos producto) {
        return productosRepositorio.save(producto);
    }

    public void realizarCompra(List<CompraDTO> compras) {
        for (CompraDTO compra : compras) {
            Productos producto = productosRepositorio.findByCodigo(compra.getCodigo())
                    .orElseThrow(() -> new RuntimeException("Producto con código " + compra.getCodigo() + " no encontrado."));

            if (producto.getCantidad() < compra.getCantidadSeleccionada()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setCantidad(producto.getCantidad() - compra.getCantidadSeleccionada());
            productosRepositorio.save(producto);
        }
    }
    public Productos buscarPorId(Long id) {
        return productosRepositorio.findByid(id)
                .orElse(null); // o lanza excepción si prefieres
    }

    public void eliminarPorId(Long id) {
        if (productosRepositorio.existsById(id)) {
            productosRepositorio.deleteById(id);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
    }

    public List<Productos> listarTodosOrdenados() {
        return productosRepositorio.findAllByOrderByNombreAsc();
    }






}
