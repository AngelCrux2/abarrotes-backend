package com.example.tienda.controlador;
import org.springframework.ui.Model;

import com.example.tienda.modelo.Usuarios;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControlador {


    @GetMapping("/login")
    public String mostrarLogin() {
        return "login/login";
    }
    @GetMapping("/logout")
    public String logoutExitoso(Model model) {
        model.addAttribute("mensaje", "Has cerrado sesión correctamente.");
        return "login/login";
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }
    @GetMapping("/error/403")
    public String accesoDenegado() {
        return "error/403";
    }

}
