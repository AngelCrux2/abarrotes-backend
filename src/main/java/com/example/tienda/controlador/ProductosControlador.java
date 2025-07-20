package com.example.tienda.controlador;

import com.example.tienda.modelo.Departamento;
import com.example.tienda.modelo.ProductoMVendido;
import com.example.tienda.modelo.Productos;
import com.example.tienda.modelo.Usuarios;
import com.example.tienda.servicio.DepartamentoService;
import com.example.tienda.servicio.ProductosService;
import com.example.tienda.servicio.ReporteProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductosControlador {
    @Autowired
    private ProductosService productosService;
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private ReporteProductoService reporteService;

    private void agregarDatosUsuario(Model model, Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
    }

    @GetMapping("/")
    public String productos2(Authentication authentication, Model model) {
        agregarDatosUsuario(model,authentication);
        return "producto/productos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model, Authentication auth) {
        model.addAttribute("producto", new Productos());
        model.addAttribute("departamentos", departamentoService.findAll());
        agregarDatosUsuario(model, auth);
        return "producto/agregarproductos";
    }
    @PostMapping("/nuevo")
    public String productos_agregar_nav(@ModelAttribute("producto") Productos producto, Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        if (producto.getNombre() != null) {
            producto.setNombre(producto.getNombre().toUpperCase());
        }

        try {
            productosService.guardarOActualizarProducto(producto);
            model.addAttribute("mensaje", "Producto guardado correctamente.");
            model.addAttribute("producto", new Productos());
        } catch (DataIntegrityViolationException e) {
            Productos existentePorCodigo = productosService.buscarPorCodigo(producto.getCodigo());

            Productos existentePorNombre = productosService.buscarPorNombre(producto.getNombre());

            Productos productoExistente = null;

            if (existentePorCodigo != null) {
                productoExistente = existentePorCodigo;
            } else if (existentePorNombre != null) {
                productoExistente = existentePorNombre;
            }

            if (productoExistente != null) {
                model.addAttribute("productoExistente", productoExistente);
                model.addAttribute("mostrarModal", true);
            } else {
                model.addAttribute("error", "El producto ya existe, pero no se pudo recuperar.");
            }

            model.addAttribute("producto", new Productos());
        }

        return "redirect:/productos/nuevo";
    }

    @GetMapping("/inventario")
    public String productos4(Authentication authentication, Model model) {
        agregarDatosUsuario(model,authentication);
        model.addAttribute("departamentos", departamentoService.findAll());
        return "producto/departamentos";
    }
    @PostMapping("/inventario")
    public String productos_buscar_nav(@ModelAttribute("producto") Productos producto){
        productosService.guardarOActualizarProducto(producto);
        return "redirect:/productos/inventario";
    }

    @GetMapping("/estadisticas")
    public String productos3(Authentication auth, Model model) {
        agregarDatosUsuario(model,auth);
        return "producto/estadisticas";
    }
    @PostMapping("/estadisticas")
    public String productos_nav(){
        return "redirect:/productos/estadisticas";
    }


    @PostMapping("/editarproducto")
    public String guardarProductoEditado(@ModelAttribute("producto") Productos producto,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        try {
            Productos duplicado = productosService.buscarPorCodigo(producto.getCodigo());

            if (duplicado != null && !duplicado.getId().equals(producto.getId())) {
                model.addAttribute("productoExistente", duplicado);
                model.addAttribute("mostrarModal", true);
                model.addAttribute("producto", producto);
                model.addAttribute("departamentos", departamentoService.findAll());
                return "editarproducto";
            }

            productosService.guardarOActualizarProducto(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado con éxito.");
            return "redirect:/productos/inventario";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "redirect:/productos/editarproducto/" + producto.getId();
        }
    }

    @GetMapping("/editarproducto/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Authentication authentication, Model model) {
        agregarDatosUsuario(model, authentication);

        Productos producto = productosService.buscarPorId(id);

        if (producto == null) {
            model.addAttribute("error", "Producto no encontrado.");
            return "redirect:/buscarproductos";
        }

        model.addAttribute("producto", producto);
        model.addAttribute("departamentos", departamentoService.findAll());

        return "producto/editarproducto";
    }

    @GetMapping("/departamentos/{id}")
    public String productosPorDepartamento(@PathVariable Long id, Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);
        Departamento departamento = departamentoService.buscarPorId(id);

        if (departamento == null) {
            model.addAttribute("error", "Departamento no encontrado");
            return "redirect:/";
        }

        List<Productos> productos = productosService.buscarPorDepartamento(departamento);
        model.addAttribute("productos", productos);
        model.addAttribute("departamentoNombre", departamento.getNombre());
        return "producto/productos_por_departamento";
    }


}
