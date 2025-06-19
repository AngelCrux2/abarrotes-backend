package com.example.tienda.controlador;

import com.example.tienda.modelo.Usuarios;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FinDiaControlador {

    @GetMapping("/fin-dia")
    public String productos2(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "findia";
    }

    @PostMapping("/fin-dia")
    public String productos_nav(){
        return "redirect:/findia";
    }
}
