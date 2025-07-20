package com.example.tienda.servicio;

import com.example.tienda.modelo.MovimientoCredito;
import com.example.tienda.repositorio.MovimientoCreditoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoService {
    @Autowired
    private MovimientoCreditoRepositorio movimientoCreditoRepositorio;
    public List<MovimientoCredito> historial(Long deudorId) {
        return movimientoCreditoRepositorio.findByDeudorIdOrderByFechaDesc(deudorId);
    }
    public MovimientoCredito guardar(MovimientoCredito m) { return movimientoCreditoRepositorio.save(m); }
    public List<MovimientoCredito> obtenerPorDeudorYTipo(Long deudorId, MovimientoCredito.TipoMovimiento tipo) {
        return movimientoCreditoRepositorio.findByDeudorIdAndTipo(deudorId, tipo);
    }
    public void eliminar(MovimientoCredito movimiento) {
        movimientoCreditoRepositorio.delete(movimiento);
    }
}