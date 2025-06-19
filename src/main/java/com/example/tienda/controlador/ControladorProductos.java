package com.example.tienda.controlador;

import com.example.tienda.modelo.Productos;
import com.example.tienda.modelo.Usuarios;
import com.example.tienda.repositorio.ProductosRepositorio;
import com.example.tienda.servicio.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ControladorProductos {

    @Autowired
    private ProductosService productosService;
    private ProductosRepositorio productosRepositorio;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @PostMapping("/dashboard")
    public String guardarProducto(@ModelAttribute("producto") Productos producto) {
        productosService.guardarOActualizarProducto(producto);
        return "redirect:/dashboard";
    }

    @GetMapping("/productos")
    public String productos2(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "productos";
    }

    @PostMapping("/productos")
    public String productos_nav(){
        return "redirect:/productos";
    }

    @GetMapping("/agregarproductos")
    public String productos3(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "agregarproductos";
    }

    @PostMapping("/agregarproductos")
    public String productos_agregar_nav(@ModelAttribute("producto") Productos producto){
        productosService.guardarOActualizarProducto(producto);
        return "redirect:/agregarproductos";
    }

    @GetMapping("/buscarproductos")
    public String productos4(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "buscarproductos";
    }

    @PostMapping("/buscarproductos")
    public String productos_buscar_nav(@ModelAttribute("producto") Productos producto){
        productosService.guardarOActualizarProducto(producto);
        return "redirect:/buscarproductos";
    }

    @GetMapping("/editarproducto/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id,
                                           Authentication authentication,
                                           Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }

        Productos producto = productosService.buscarPorId(id); // Asegúrate de tener este método

        if (producto == null) {
            model.addAttribute("error", "Producto no encontrado.");
            return "redirect:/buscarproductos";
        }

        model.addAttribute("producto", producto);
        return "editarproducto";
    }



    @PostMapping("/editarproducto")
    public String guardarProductoEditado(@ModelAttribute("producto") Productos producto,
                                         RedirectAttributes redirectAttributes) {
        try {
            productosService.guardarOActualizarProducto(producto); // Debe aceptar producto con ID
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado con éxito.");
            return "redirect:/buscarproductos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "redirect:/editarproducto/" + producto.getId();
        }
    }

    @DeleteMapping("/api/productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productosService.eliminarPorId(id); // Asegúrate de que este método exista
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar producto");
        }
    }






}
