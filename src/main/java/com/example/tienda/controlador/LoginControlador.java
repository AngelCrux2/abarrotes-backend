package com.example.tienda.controlador;

import com.example.tienda.servicio.UsuariosDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.example.tienda.modelo.Usuarios;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControlador {


    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Thymeleaf: login.html
    }
    @GetMapping("/logout")
    public String logoutExitoso(Model model) {
        model.addAttribute("mensaje", "Has cerrado sesión correctamente.");
        return "login";
    }

    // Redirección post-login (si deseas tener una lógica adicional)
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "dashboard";
    }


}
