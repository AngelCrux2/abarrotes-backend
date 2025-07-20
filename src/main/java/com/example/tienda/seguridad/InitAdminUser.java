package com.example.tienda.seguridad;

import com.example.tienda.modelo.Usuarios;
import com.example.tienda.repositorio.UsuariosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitAdminUser implements CommandLineRunner {

    @Autowired
    private UsuariosRepositorio usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean existeAdmin = usuariosRepository.findAll().stream()
                .anyMatch(user -> user.getRol() == Usuarios.Rol.ADMIN);

        if (!existeAdmin) {
            Usuarios admin = new Usuarios();
            admin.setNombre("Admin");
            admin.setApellido("registro");
            admin.setRol(Usuarios.Rol.ADMIN);
            admin.setPassword(passwordEncoder.encode("admin123"));

            usuariosRepository.save(admin);
        }
    }
}

