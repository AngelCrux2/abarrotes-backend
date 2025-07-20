package com.example.tienda.repositorio;

import com.example.tienda.modelo.ReportePDF;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportePdfRepositorio extends JpaRepository<ReportePDF, Long> {
    List<ReportePDF> findAllByOrderByFechaGeneracionDesc();
}
