package com.example.tienda.controlador;

import com.example.tienda.modelo.*;
import com.example.tienda.repositorio.CompraDetalleRepositorio;
import com.example.tienda.repositorio.CompraRepositorio;
import com.example.tienda.repositorio.MovimientoCreditoRepositorio;
import com.example.tienda.repositorio.ProductosRepositorio;
import com.example.tienda.servicio.DeudorService;
import com.example.tienda.servicio.MovimientoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/deudores")
public class DeudoresControlador {

    @Autowired
    private DeudorService deudorService;
    @Autowired
    private MovimientoService movimientoService;
    @Autowired
    private ProductosRepositorio productosRepo;
    @Autowired
    private MovimientoCreditoRepositorio movimientoCreditoRepositorio;
    @Autowired
    private CompraRepositorio compraRepositorio;

    private void agregarDatosUsuario(Model model, Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        agregarDatosUsuario(model, authentication);
        model.addAttribute("deudores", deudorService.listar());
        return "deudores/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication) {
        agregarDatosUsuario(model, authentication);
        model.addAttribute("deudor", new Deudor());
        return "deudores/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Deudor deudor) {
        deudorService.guardar(deudor);
        return "redirect:/deudores";
    }

    @GetMapping("/{id}")
    public String verDeudor(@PathVariable Long id, Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        Deudor deudor = deudorService.obtener(id).orElseThrow();
        model.addAttribute("deudor", deudor);

        List<MovimientoCredito> historialCompleto = deudor.getMovimientos();

        Optional<MovimientoCredito> ultimaLiquidacion = historialCompleto.stream()
                .filter(m -> m.getTipo() == MovimientoCredito.TipoMovimiento.LIQUIDACION)
                .max(Comparator.comparing(MovimientoCredito::getId));

        List<MovimientoCredito> historialFiltrado = historialCompleto.stream()
                .filter(m -> ultimaLiquidacion.isEmpty() || m.getId() > ultimaLiquidacion.get().getId())
                .sorted(Comparator.comparing(MovimientoCredito::getFecha).reversed())
                .toList();

        model.addAttribute("historial", historialFiltrado);

        return "deudores/perfil";
    }


    @Transactional
    @PostMapping("/{id}/solicitar")
    public String solicitar(@PathVariable Long id,
                            @RequestParam List<Long> productoId,
                            @RequestParam List<Integer> cantidad) {

        Deudor deudor = deudorService.obtener(id).orElseThrow();

        MovimientoCredito movimiento = new MovimientoCredito();
        movimiento.setDeudor(deudor);
        movimiento.setTipo(MovimientoCredito.TipoMovimiento.SOLICITUD);

        BigDecimal total = BigDecimal.ZERO;

        // Optimización: una sola consulta para todos los productos
        List<Productos> productos = productosRepo.findAllById(productoId);
        Map<Long, Productos> productoMap = productos.stream()
                .collect(Collectors.toMap(Productos::getId, p -> p));

        for (int i = 0; i < productoId.size(); i++) {
            Productos producto = productoMap.get(productoId.get(i));
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado con ID: " + productoId.get(i));
            }

            int qty = cantidad.get(i);
            BigDecimal precio = producto.getCosto();

            MovimientoDetalle detalle = new MovimientoDetalle();
            detalle.setProducto(producto);
            detalle.setCantidad(qty);
            detalle.setPrecioUnitario(precio);
            detalle.setMovimiento(movimiento);

            movimiento.getDetalles().add(detalle);
            total = total.add(precio.multiply(BigDecimal.valueOf(qty)));
        }

        movimiento.setMonto(total);
        movimientoService.guardar(movimiento);

        return "redirect:/deudores/" + id;
    }

    @Transactional
    @PostMapping("/{id}/abonar")
    public String abonar(@PathVariable Long id,
                         @RequestParam BigDecimal monto,
                         @RequestParam Compra.FormaPago metodoPago) {
        Deudor deudor = deudorService.obtener(id).orElseThrow();

        MovimientoCredito movimiento = new MovimientoCredito();
        movimiento.setDeudor(deudor);
        movimiento.setTipo(MovimientoCredito.TipoMovimiento.ABONO);
        movimiento.setMonto(monto);
        movimientoService.guardar(movimiento);

        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setTotal(monto);
        compra.setTipo(Compra.TipoCompra.ABONO_DEUDA);
        compra.setFormaPago(metodoPago);

        CompraDetalle detalle = new CompraDetalle();
        detalle.setCompra(compra);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(monto);
        detalle.setProducto(null);

        compra.getDetalles().add(detalle);
        compraRepositorio.save(compra);

        return "redirect:/deudores/" + id;
    }


    @Transactional
    @PostMapping("/{id}/liquidar")
    public String liquidar(@PathVariable Long id,
                           @RequestParam Compra.FormaPago metodoPago) {
        Deudor deudor = deudorService.obtener(id).orElseThrow();
        BigDecimal saldo = deudor.getSaldo();

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            MovimientoCredito mov = new MovimientoCredito();
            mov.setDeudor(deudor);
            mov.setTipo(MovimientoCredito.TipoMovimiento.LIQUIDACION);
            mov.setMonto(saldo);
            movimientoService.guardar(mov);

            Compra compra = new Compra();
            compra.setFecha(LocalDateTime.now());
            compra.setTotal(saldo);
            compra.setTipo(Compra.TipoCompra.LIQUIDACION_DEUDA);
            compra.setFormaPago(metodoPago);

            CompraDetalle detalle = new CompraDetalle();
            detalle.setCompra(compra);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(saldo);
            detalle.setProducto(null);

            compra.getDetalles().add(detalle);
            compraRepositorio.save(compra);
        }

        return "redirect:/deudores/" + id;
    }



    @PostMapping("/temp-carrito")
    @ResponseBody
    public ResponseEntity<Void> guardarTemporalCarrito(@RequestBody List<Map<String, Object>> carrito, HttpSession session) {
        session.setAttribute("carritoDeuda", carrito);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/seleccionar")
    public String seleccionarDeudor(Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);
        model.addAttribute("deudores", deudorService.listar());
        return "deudores/seleccionar";
    }

    @Transactional
    @GetMapping("/asignar/{id}")
    public String asignarDeuda(@PathVariable Long id, HttpSession session) {
        List<Map<String, Object>> carrito = (List<Map<String, Object>>) session.getAttribute("carritoDeuda");
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/deudores/" + id;
        }

        Deudor deudor = deudorService.obtener(id).orElseThrow();

        MovimientoCredito movimiento = new MovimientoCredito();
        movimiento.setDeudor(deudor);
        movimiento.setTipo(MovimientoCredito.TipoMovimiento.SOLICITUD);

        BigDecimal total = BigDecimal.ZERO;
        List<MovimientoDetalle> detalles = new ArrayList<>();

        for (Map<String, Object> item : carrito) {
            String codigo = (String) item.get("codigo");
            String nombre = (String) item.get("nombre");
            int cantidad = ((Number) item.get("cantidadSeleccionada")).intValue();

            if (codigo.startsWith("VERDURA_")) {
                BigDecimal costo = new BigDecimal(item.get("costo").toString());

                MovimientoDetalle detalle = new MovimientoDetalle();
                detalle.setProducto(null);
                detalle.setDescripcion(nombre);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(costo);
                detalle.setMovimiento(movimiento);

                detalles.add(detalle);
                total = total.add(costo.multiply(BigDecimal.valueOf(cantidad)));

            } else {
                Productos producto = productosRepo.findByCodigo(codigo)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + codigo));

                if (producto.getCantidad() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
                }

                producto.setCantidad(producto.getCantidad() - cantidad);
                productosRepo.save(producto);

                MovimientoDetalle detalle = new MovimientoDetalle();
                detalle.setProducto(producto);
                detalle.setDescripcion(null);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getCosto());
                detalle.setMovimiento(movimiento);

                detalles.add(detalle);
                total = total.add(producto.getCosto().multiply(BigDecimal.valueOf(cantidad)));
            }
        }

        movimiento.setMonto(total);
        movimiento.setDetalles(detalles);
        movimientoService.guardar(movimiento);

        session.removeAttribute("carritoDeuda");

        return "redirect:/deudores/" + id;
    }

}
