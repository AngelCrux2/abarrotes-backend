package com.example.tienda.controlador;

import ch.qos.logback.core.model.Model;
import com.example.tienda.modelo.DepartamentoDTO;
import com.example.tienda.modelo.ProductoMVendido;
import com.example.tienda.modelo.Productos;
import com.example.tienda.servicio.DepartamentoService;
import com.example.tienda.servicio.ProductosService;
import com.example.tienda.servicio.ReporteProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos/reportes")
public class ReporteProductoController {
    @Autowired
    private ReporteProductoService reporteService;
    @Autowired
    private DepartamentoService departamentoService;


    @GetMapping("/mas-vendidos")
    public List<ProductoMVendido> getProductosMasVendidos() {
        return reporteService.obtenerProductosMasVendidosPorSemana();
    }
    @GetMapping("/todos-vendidos")
    public List<ProductoMVendido> obtenerTodosLosProductosVendidos() {
        return reporteService.obtenerTodosLosProductosVendidosPorSemana();
    }
    @GetMapping("/ventas-por-departamento")
    public List<DepartamentoDTO> getVentasPorDepartamento() {
        return departamentoService.obtenerVentasPorDepartamento();
    }
    @GetMapping("/no-vendidos-7dias")
    public List<Productos> obtenerProductosNoVendidos() {
        return reporteService.obtenerProductosNoVendidosEnUltimos7Dias();
    }
}
