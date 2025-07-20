package com.example.tienda.controlador;
import com.example.tienda.servicio.FinDiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/venta")
public class InicioControlador {

    @Autowired
    private FinDiaService finDiaService;

    private void agregarDatosUsuario(Model model, Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof com.example.tienda.modelo.Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
    }

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        agregarDatosUsuario(model, authentication);
        return "venta/dashboard";
    }

    @GetMapping("/registrar-gasto")
    public String mostrarFormularioGasto(Authentication authentication, Model model) {
        agregarDatosUsuario(model, authentication);
        return "venta/registrar-gasto";
    }

    @PostMapping("/registrar-gasto")
    public String registrarGasto(@RequestParam String proveedor, @RequestParam(required = false) String montoCaja, @RequestParam(required = false) String montoBoveda, @RequestParam String descripcion, Model model, Authentication auth) {

        try {
            BigDecimal montoVenta = (montoCaja != null && !montoCaja.isBlank()) ? new BigDecimal(montoCaja) : BigDecimal.ZERO;
            BigDecimal montoBov = (montoBoveda != null && !montoBoveda.isBlank()) ? new BigDecimal(montoBoveda) : BigDecimal.ZERO;

            finDiaService.registrarGastoProveedor(proveedor, montoVenta, montoBov, descripcion);

            return "redirect:/venta/registrar-gasto";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("montoCaja", montoCaja);
            model.addAttribute("montoBoveda", montoBoveda);
            model.addAttribute("descripcion", descripcion);
            agregarDatosUsuario(model, auth);
            return "venta/registrar-gasto";
        }
    }
}
