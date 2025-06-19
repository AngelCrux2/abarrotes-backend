package com.example.tienda.servicio;

import com.example.tienda.repositorio.UsuariosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuariosDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UsuariosRepositorio usuariosRepositorio;


    @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        return usuariosRepositorio.findByNombre(nombre)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombre));
    }

    public UsuariosDetailsServiceImpl(UsuariosRepositorio usuariosRepositorio) {
        this.usuariosRepositorio = usuariosRepositorio;
    }
}
