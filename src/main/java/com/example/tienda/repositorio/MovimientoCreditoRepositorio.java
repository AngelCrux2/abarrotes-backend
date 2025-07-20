package com.example.tienda.repositorio;

import com.example.tienda.modelo.MovimientoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MovimientoCreditoRepositorio extends JpaRepository<MovimientoCredito, Long> {
    List<MovimientoCredito> findByDeudorIdOrderByFechaDesc(Long deudorId);

    List<MovimientoCredito> findByDeudorIdAndTipo(Long deudorId, MovimientoCredito.TipoMovimiento tipo);


}
