package com.example.tienda.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.example.tienda.modelo.Usuarios;
import com.example.tienda.servicio.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroControlador {

    @Autowired
    private UsuariosService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model, Authentication authentication) {
        model.addAttribute("nuevoUsuario", new Usuarios());

        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }

        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("nuevoUsuario") Usuarios usuario,
                                   Model model,
                                   Authentication authentication) {

        usuarioService.guardarUsuario(usuario);
        model.addAttribute("mensaje", "Usuario registrado con éxito");
        model.addAttribute("nuevoUsuario", new Usuarios());

        // 👇 Esto es lo que falta para que NO se borre el nombre del usuario en la vista
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuarioLogeado) {
            model.addAttribute("usuario", usuarioLogeado.getNombre() + " " + usuarioLogeado.getApellido());
        }

        return "registro";
    }


}
