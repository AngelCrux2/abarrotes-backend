package com.example.tienda.controlador;

import com.example.tienda.modelo.Boveda;
import com.example.tienda.modelo.GastoProveedor;
import com.example.tienda.modelo.RetiroBoveda;
import com.example.tienda.modelo.Usuarios;
import com.example.tienda.repositorio.BovedaRepositorio;
import com.example.tienda.repositorio.GastoProveedorRepositorio;
import com.example.tienda.repositorio.RetiroBovedaRepositorio;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BovedaControlador {

    @Autowired
    BovedaRepositorio bovedaRepo;
    @Autowired
    GastoProveedorRepositorio gastoRepo;
    @Autowired
    RetiroBovedaRepositorio retiroRepo;

    @GetMapping("/boveda")
    public String deudores(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        Boveda boveda = bovedaRepo.findFirst().orElse(new Boveda(BigDecimal.ZERO));
        model.addAttribute("boveda", boveda);

        List<GastoProveedor> gastos = gastoRepo.findAllByOrderByFechaDesc();
        model.addAttribute("gastos", gastos);

        List<RetiroBoveda> retiros = retiroRepo.findAllByOrderByFechaDesc();
        model.addAttribute("retiros", retiros);
        return "fin_dia/boveda";

    }

    @Transactional
    @PostMapping("/boveda/modificar")
    public String modificarBoveda(
            @RequestParam BigDecimal nuevoTotal,
            @RequestParam String descripcion,
            Authentication authentication,
            Model model) {

        if (nuevoTotal == null || nuevoTotal.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("error", "El monto no puede ser negativo ni nulo.");
            return "redirect:/boveda";
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            model.addAttribute("error", "La descripción es obligatoria.");
            return "redirect:/boveda";
        }

        Boveda boveda = bovedaRepo.findFirst()
                .orElseGet(() -> {
                    Boveda nuevaBoveda = new Boveda(BigDecimal.ZERO);
                    return bovedaRepo.save(nuevaBoveda);
                });


        BigDecimal totalAnterior = boveda.getTotalAcumulado();
        BigDecimal diferencia = nuevoTotal.subtract(totalAnterior);

        boveda.setTotalAcumulado(nuevoTotal);
        bovedaRepo.save(boveda);

        String nombreUsuario = "Sistema";
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            nombreUsuario = usuario.getNombre() + " " + usuario.getApellido();
        }

        if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
            GastoProveedor ajuste = new GastoProveedor(
                    nombreUsuario,
                    diferencia.abs(),
                    descripcion + (diferencia.compareTo(BigDecimal.ZERO) < 0 ? " (Reducción)" : " (Incremento)"),
                    LocalDate.now(),
                    "correcion"
            );
            gastoRepo.save(ajuste);
        }

        return "redirect:/boveda";
    }

    @Transactional
    @PostMapping("/boveda/retirar")
    public String retirarDeBoveda(
            @RequestParam BigDecimal montoRetiro,
            @RequestParam String descripcion,
            Authentication authentication,
            Model model) {

        if (montoRetiro == null || montoRetiro.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "El monto debe ser mayor a cero.");
            return "redirect:/boveda";
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            model.addAttribute("error", "La descripción es obligatoria.");
            return "redirect:/boveda";
        }


        String nombreUsuario = "Sistema";
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            nombreUsuario = usuario.getNombre() + " " + usuario.getApellido();
        }


        Boveda boveda = bovedaRepo.findFirst()
                .orElseGet(() -> bovedaRepo.save(new Boveda(BigDecimal.ZERO)));

        if (boveda.getTotalAcumulado().compareTo(montoRetiro) < 0) {
            model.addAttribute("error", "Fondos insuficientes en la bóveda.");
            return "redirect:/boveda";
        }

        boveda.setTotalAcumulado(boveda.getTotalAcumulado().subtract(montoRetiro));
        bovedaRepo.save(boveda);


        GastoProveedor retiro = new GastoProveedor(
                nombreUsuario,
                montoRetiro,
                descripcion + " (Retiro manual)",
                LocalDate.now(),
                "retiro"
        );
        gastoRepo.save(retiro);

        return "redirect:/boveda";
    }



}
