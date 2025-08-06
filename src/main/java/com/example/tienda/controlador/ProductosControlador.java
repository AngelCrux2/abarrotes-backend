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
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
    }

    private Productos verificarProductoDuplicado(Productos producto) {
        Productos porCodigo = productosService.buscarPorCodigo(producto.getCodigo());
        Productos porNombre = productosService.buscarPorNombre(producto.getNombre());

        if (porCodigo != null && !porCodigo.getId().equals(producto.getId())) {
            return porCodigo;
        }
        if (porNombre != null && !porNombre.getId().equals(producto.getId())) {
            return porNombre;
        }
        return null;
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
    public String productos_agregar_nav(@ModelAttribute("producto") Productos producto,
                                        RedirectAttributes redirectAttributes,
                                        Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        if (producto.getNombre() != null) {
            producto.setNombre(producto.getNombre().toUpperCase());
        }

        try {
            Productos duplicado = verificarProductoDuplicado(producto);
            if (duplicado != null) {
                // Pasamos el duplicado como atributo para mostrar modal en la vista
                redirectAttributes.addFlashAttribute("productoExistente", duplicado);
                redirectAttributes.addFlashAttribute("mostrarModal", true);
                redirectAttributes.addFlashAttribute("producto", producto);
                return "redirect:/productos/nuevo";
            }

            productosService.guardarOActualizarProducto(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto guardado correctamente.");
            return "redirect:/productos/nuevo";

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("producto", producto);
            return "redirect:/productos/nuevo";
        }
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
                                         Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        try {
            Productos duplicado = verificarProductoDuplicado(producto);
            if (duplicado != null) {
                model.addAttribute("productoExistente", duplicado);
                model.addAttribute("mostrarModal", true);
                model.addAttribute("producto", producto);
                model.addAttribute("departamentos", departamentoService.findAll());
                return "producto/editarproducto";
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
