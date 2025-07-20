package com.example.tienda.servicio;

import com.example.tienda.modelo.*;
import com.example.tienda.repositorio.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FinDiaService {

    @Autowired
    private CompraRepositorio compraRepositorio;

    @Autowired
    private GastoProveedorRepositorio gastoRepo;

    @Autowired
    private RetiroBovedaRepositorio retiroRepo;

    @Autowired
    private BovedaRepositorio bovedaRepo;

    @Autowired
    private FinDiaRepositorio finDiaRepo;


    @Transactional
    public void registrarGastoProveedor(String proveedor, BigDecimal montoCaja, BigDecimal montoBoveda, String descripcion) {
        LocalDate hoy = LocalDate.now();

        if (montoCaja != null && montoCaja.compareTo(BigDecimal.ZERO) > 0) {
            GastoProveedor gastoCaja = new GastoProveedor(
                    proveedor,
                    montoCaja,
                    descripcion + " (Caja)",
                    hoy,
                    "caja"
            );
            gastoRepo.save(gastoCaja);
        }

        if (montoBoveda != null && montoBoveda.compareTo(BigDecimal.ZERO) > 0) {
            Boveda boveda = bovedaRepo.findFirst()
                    .orElseGet(() -> bovedaRepo.save(new Boveda(BigDecimal.ZERO)));

            if (boveda.getTotalAcumulado().compareTo(montoBoveda) < 0) {
                throw new IllegalArgumentException("Fondos insuficientes en la bóveda para cubrir el gasto del proveedor.");
            }

            RetiroBoveda retiro = new RetiroBoveda();
            retiro.setMonto(montoBoveda);
            retiro.setFecha(hoy);
            retiroRepo.save(retiro);

            GastoProveedor gastoBoveda = new GastoProveedor(
                    proveedor,
                    montoBoveda,
                    descripcion + " (Bóveda)",
                    hoy,
                    "boveda"
            );
            gastoRepo.save(gastoBoveda);

            boveda.setTotalAcumulado(boveda.getTotalAcumulado().subtract(montoBoveda));
            bovedaRepo.save(boveda);
        }
    }

    @Transactional
    public void registrarFinDiaConSaldo(LocalDate fecha, BigDecimal montoRetiro, Usuarios usuario, BigDecimal saldoInicialMostradoEnPantalla) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        BigDecimal ventasFisicas = compraRepositorio.sumTotalByFechaExcluyendoTransferencia(inicio, fin);
        if (ventasFisicas == null) ventasFisicas = BigDecimal.ZERO;

        BigDecimal transferencias = compraRepositorio.sumTotalTransferenciasByFecha(inicio, fin);
        if (transferencias == null) transferencias = BigDecimal.ZERO;

        BigDecimal totalVentas = ventasFisicas.add(transferencias);

        BigDecimal totalGastos = gastoRepo.restGastosCajaPorFecha(fecha);
        if (totalGastos == null) totalGastos = BigDecimal.ZERO;

        BigDecimal dineroEnCaja = saldoInicialMostradoEnPantalla.add(ventasFisicas).subtract(totalGastos);

        if (montoRetiro.compareTo(BigDecimal.ZERO) < 0 || montoRetiro.compareTo(dineroEnCaja) > 0) {
            throw new IllegalArgumentException("Monto de retiro inválido");
        }

        Boveda boveda = bovedaRepo.findFirst().orElseGet(() -> bovedaRepo.save(new Boveda(BigDecimal.ZERO)));
        boveda.setTotalAcumulado(boveda.getTotalAcumulado().add(montoRetiro));
        bovedaRepo.save(boveda);

        BigDecimal saldoFinal = dineroEnCaja.subtract(montoRetiro);

        FinDia finDia = new FinDia();
        finDia.setFecha(fecha);
        finDia.setSaldoInicial(saldoInicialMostradoEnPantalla);
        finDia.setTotalVentas(totalVentas);
        finDia.setTotalGastos(totalGastos);
        finDia.setRetiro(montoRetiro);
        finDia.setSaldoFinal(saldoFinal);
        finDiaRepo.save(finDia);

        Optional<GastoProveedor> ingresoExistenteOpt = gastoRepo.findByFechaAndProveedorAndOrigen(fecha, "FIN DÍA", "ingreso");

        if (ingresoExistenteOpt.isPresent()) {
            GastoProveedor ingresoExistente = ingresoExistenteOpt.get();
            ingresoExistente.setMonto(montoRetiro);
            ingresoExistente.setDescripcion("Ingreso de efectivo actualizado del fin de día a la bóveda");
            gastoRepo.save(ingresoExistente);
        } else {
            GastoProveedor ingresoNuevo = new GastoProveedor();
            ingresoNuevo.setFecha(fecha);
            ingresoNuevo.setProveedor("FIN DÍA");
            ingresoNuevo.setDescripcion("Ingreso de efectivo del fin día a la bóveda");
            ingresoNuevo.setOrigen("ingreso");
            ingresoNuevo.setMonto(montoRetiro);
            gastoRepo.save(ingresoNuevo);
        }
    }













}
