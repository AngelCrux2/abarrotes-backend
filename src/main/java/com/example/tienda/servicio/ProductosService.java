package com.example.tienda.servicio;

import com.example.tienda.modelo.*;
import com.example.tienda.repositorio.CompraDetalleRepositorio;
import com.example.tienda.repositorio.CompraRepositorio;
import com.example.tienda.repositorio.ProductosRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProductosService {

    @Autowired
    ProductosRepositorio productosRepositorio;
    @Autowired
    CompraRepositorio compraRepositorio;
    @Autowired
    CompraDetalleRepositorio compraDetalleRepositorio;

    public List<Productos> buscarPorNombreOCodigoParcial(String valor, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return productosRepositorio.buscarPorNombreOCodigoParcial(valor, pageable);
    }

    public Productos guardarOActualizarProducto(Productos producto) {
        return productosRepositorio.save(producto);
    }

    public Productos buscarPorId(Long id) {
        return productosRepositorio.findByid(id)
                .orElse(null);
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

    public Optional<Productos> buscarPorCodigoExacto(String codigo) {
        return productosRepositorio.findByCodigo(codigo);
    }

    @Transactional
    public void registrarCompraConFormaPago(List<CompraDTO> compras, Compra.FormaPago formaPago) {
        BigDecimal total = BigDecimal.ZERO;
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setTipo(Compra.TipoCompra.VENTA);
        compra.setFormaPago(formaPago);

        List<CompraDetalle> detalles = new ArrayList<>();

        for (CompraDTO dto : compras) {
            Optional<Productos> optProducto = productosRepositorio.findByCodigo(dto.getCodigo());

            CompraDetalle detalle = new CompraDetalle();
            detalle.setCompra(compra);
            detalle.setCantidad(dto.getCantidadSeleccionada());

            if (optProducto.isPresent()) {
                Productos producto = optProducto.get();

                if (producto.getCantidad() < dto.getCantidadSeleccionada()) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
                }

                producto.setCantidad(producto.getCantidad() - dto.getCantidadSeleccionada());
                productosRepositorio.save(producto);

                BigDecimal precioUnitario = producto.getCosto();
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(dto.getCantidadSeleccionada()));
                total = total.add(subtotal);

                detalle.setProducto(producto);
                detalle.setPrecioUnitario(precioUnitario);
            } else if (dto.getCodigo().startsWith("VERDURA_")) {
                BigDecimal precioUnitario = BigDecimal.valueOf(dto.getCosto());
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(dto.getCantidadSeleccionada()));
                total = total.add(subtotal);

                detalle.setProducto(null);
                detalle.setPrecioUnitario(precioUnitario);
            } else {
                throw new RuntimeException("Producto con código " + dto.getCodigo() + " no encontrado.");
            }

            detalles.add(detalle);
        }

        compra.setTotal(total);
        compra.setDetalles(detalles);
        compraRepositorio.save(compra);
    }

    public List<ProductoMVendido> productosMasVendidosPorSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23,59,59);

        List<Object[]> resultados = compraDetalleRepositorio.findProductosMasVendidosPorSemana(inicio, fin);
        List<ProductoMVendido> reporte = new ArrayList<>();

        for (Object[] fila : resultados) {
            String nombre = (String) fila[0];
            Long total = ((Number) fila[1]).longValue();
            Integer semana = ((Number) fila[2]).intValue();

            reporte.add(new ProductoMVendido(nombre, total, semana));
        }
        return reporte;
    }

    public Productos buscarPorCodigo(String codigo) {
        return productosRepositorio.findByCodigo(codigo).orElse(null);
    }

    public Productos buscarPorNombre(String nombre) {
        return productosRepositorio.findByNombreIgnoreCase(nombre).orElse(null);
    }

    public List<Productos> buscarSimilaresPorNombre(String nombre) {
        return productosRepositorio.findByNombreContainingIgnoreCase(nombre);
    }
    public List<Productos> buscarPorDepartamento(Departamento departamento) {
        return productosRepositorio.findByDepartamento(departamento);
    }
}
