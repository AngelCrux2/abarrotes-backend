package com.example.tienda.controlador;

import com.example.tienda.modelo.Compra;
import com.example.tienda.modelo.CompraDTO;
import com.example.tienda.modelo.ProductoMVendido;
import com.example.tienda.modelo.Productos;
import com.example.tienda.repositorio.ProductosRepositorio;
import com.example.tienda.servicio.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductosRestController {

    @Autowired
    private ProductosService productosService;

    @GetMapping("/buscar-parcial")
    public ResponseEntity<List<Productos>> buscarParcial(@RequestParam String valor, @RequestParam(defaultValue = "30") int limite) {
        List<Productos> productos = productosService.buscarPorNombreOCodigoParcial(valor, limite);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/comprar")
    public ResponseEntity<?> comprarEnEfectivo(@RequestBody List<CompraDTO> compra) {
        try {
            productosService.registrarCompraConFormaPago(compra, Compra.FormaPago.EFECTIVO);
            return ResponseEntity.ok("Compra en efectivo registrada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/todos")
    public List<Productos> listarTodosOrdenados() {
        return productosService.listarTodosOrdenados();
    }

    @GetMapping("/buscar-codigo")
    public ResponseEntity<Productos> buscarPorCodigo(@RequestParam String codigo) {
        return productosService.buscarPorCodigoExacto(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/mas-vendidos-semana")
    public List<ProductoMVendido> productosMasVendidosSemana(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        return productosService.productosMasVendidosPorSemana(inicio, fin);
    }

    @GetMapping("/similares")
    @ResponseBody
    public List<Productos> buscarSimilares(@RequestParam String nombre) {
        return productosService.buscarSimilaresPorNombre(nombre.toUpperCase());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        productosService.eliminarPorId(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comprar-transferencia")
    public ResponseEntity<?> comprarPorTransferencia(@RequestBody List<CompraDTO> compra) {
        try {
            productosService.registrarCompraConFormaPago(compra, Compra.FormaPago.TRANSFERENCIA);
            return ResponseEntity.ok("Compra por transferencia registrada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}


