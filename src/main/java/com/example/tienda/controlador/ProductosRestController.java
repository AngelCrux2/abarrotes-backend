package com.example.tienda.controlador;

import com.example.tienda.modelo.CompraDTO;
import com.example.tienda.modelo.Productos;
import com.example.tienda.servicio.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {

    @Autowired
    private ProductosService productosService;

    @GetMapping("/buscar-parcial")
    public List<Productos> buscarParcial(@RequestParam String valor, @RequestParam(defaultValue = "5") int limite) {
        return productosService.buscarPorNombreOCodigoParcial(valor, limite);
    }

    @PostMapping("/comprar")
    public ResponseEntity<?> comprar(@RequestBody List<CompraDTO> productosComprados) {
        try {
            productosService.realizarCompra(productosComprados);
            return ResponseEntity.ok().body(Map.of("mensaje", "Compra realizada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/todos")
    public List<Productos> listarTodosOrdenados() {
        return productosService.listarTodosOrdenados();
    }
}


