package com.example.tienda.servicio;

import com.example.tienda.modelo.Usuarios;
import com.example.tienda.repositorio.UsuariosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepositorio usuariosRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuarios guardarUsuario(Usuarios usuarios) {
        usuarios.setStatus(1);
        String contraseñaEncryptada = passwordEncoder.encode(usuarios.getPassword());
        usuarios.setPassword(contraseñaEncryptada);
        return usuariosRepositorio.save(usuarios);
    }

    public void cambiarStatus (Long id){
        Optional<Usuarios> usuario0pt = usuariosRepositorio.findById(id);

        if (usuario0pt.isPresent()){
            Usuarios usuarios = usuario0pt.get();
            usuarios.setStatus(usuarios.getStatus() == 1 ? 0 : 1);
            usuariosRepositorio.save(usuarios);

        }
    }




}
