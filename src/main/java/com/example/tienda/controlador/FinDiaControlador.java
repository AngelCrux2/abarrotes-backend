package com.example.tienda.controlador;

import com.example.tienda.modelo.*;
import com.example.tienda.repositorio.*;
import com.example.tienda.servicio.FinDiaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@Controller
public class FinDiaControlador {

    @Autowired
    private GastoProveedorRepositorio gastoRepo;
    @Autowired
    private CompraRepositorio compraRepositorio;
    @Autowired
    private FinDiaService finDiaService;
    @Autowired
    private RetiroBovedaRepositorio retiroRepo;
    @Autowired
    private FinDiaRepositorio finDiaRepo;
    @Autowired
    private BovedaRepositorio bovedaRepo;

    @GetMapping("/fin-dia")
    public String findiaget(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "fin_dia/findia";
    }

    @PostMapping("/fin-dia")
    public String findiapost(){
        return "redirect:/findia";
    }

    @GetMapping("/iniciar-fin-dia")
    public String iniciarFinDia(
            Authentication authentication,
            Model model,
            @RequestParam(value = "editar", required = false, defaultValue = "false") boolean editarParam
    ) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }

        LocalDate hoy = LocalDate.now();
        Optional<FinDia> finDiaHoyOpt = finDiaRepo.findByFecha(hoy);
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        
        BigDecimal transferencias = compraRepositorio.sumTotalTransferenciasByFecha(inicio, fin);
        if (transferencias == null) transferencias = BigDecimal.ZERO;

        BigDecimal ventasFisicas = compraRepositorio.sumTotalByFechaExcluyendoTransferencia(inicio, fin);
        if (ventasFisicas == null) ventasFisicas = BigDecimal.ZERO;

        BigDecimal ventasTotales = ventasFisicas.add(transferencias);

        BigDecimal gastos = gastoRepo.restGastosCajaPorFecha(hoy);
        if (gastos == null) gastos = BigDecimal.ZERO;


        BigDecimal saldoInicialSugerido = finDiaRepo.findFirstByOrderByFechaDesc()
                .map(FinDia::getSaldoFinal)
                .orElse(BigDecimal.ZERO);

        BigDecimal dineroEnCaja;
        if (finDiaHoyOpt.isPresent()) {
            FinDia finDiaHoy = finDiaHoyOpt.get();
            dineroEnCaja = finDiaHoy.getSaldoInicial().add(ventasFisicas.subtract(gastos));
        } else {
            dineroEnCaja = saldoInicialSugerido.add(ventasFisicas.subtract(gastos));
        }

        model.addAttribute("ventasFisicas", ventasFisicas);
        model.addAttribute("transferencias", transferencias);
        model.addAttribute("ventasTotales", ventasTotales);
        model.addAttribute("gastos", gastos);
        model.addAttribute("saldoInicialSugerido", saldoInicialSugerido);
        model.addAttribute("dineroEnCaja", dineroEnCaja);
        model.addAttribute("fecha", hoy);

        if (finDiaHoyOpt.isPresent()) {
            model.addAttribute("editar", true);
            model.addAttribute("finDia", finDiaHoyOpt.get());
            model.addAttribute("mensaje", "Ya se registró el fin de día para hoy.");
            model.addAttribute("modoEdicion", editarParam);
        } else {
            model.addAttribute("editar", false);
            model.addAttribute("modoEdicion", true);
        }

        return "fin_dia/iniciar_findia";
    }



    @PostMapping("/iniciar-fin-dia")
    @Transactional
    public String finalizarFinDia(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("saldoInicialEditado") BigDecimal saldoInicialEditado,
            @RequestParam("montoRetiro") BigDecimal montoRetiro,
            Authentication authentication,
            Model model) {

        if (authentication == null || !(authentication.getPrincipal() instanceof Usuarios usuario)) {
            return "redirect:/login";
        }

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        BigDecimal ventasFisicas = compraRepositorio.sumTotalByFechaExcluyendoTransferencia(inicio, fin);
        if (ventasFisicas == null) ventasFisicas = BigDecimal.ZERO;

        BigDecimal transferencias = compraRepositorio.sumTotalTransferenciasByFecha(inicio, fin);
        if (transferencias == null) transferencias = BigDecimal.ZERO;

        BigDecimal ventasTotales = ventasFisicas.add(transferencias);

        BigDecimal gastos = gastoRepo.restGastosCajaPorFecha(fecha);
        if (gastos == null) gastos = BigDecimal.ZERO;

        BigDecimal dineroEnCaja = saldoInicialEditado.add(ventasFisicas).subtract(gastos);

        if (montoRetiro.compareTo(dineroEnCaja) > 0 || montoRetiro.compareTo(BigDecimal.ZERO) < 0) {
            return "fin_dia/error-retiro";
        }

        BigDecimal saldoFinal = dineroEnCaja.subtract(montoRetiro);

        Optional<FinDia> finDiaOpt = finDiaRepo.findByFecha(fecha);

        Boveda boveda = bovedaRepo.findFirst()
                .orElseGet(() -> bovedaRepo.save(new Boveda(BigDecimal.ZERO)));

        if (finDiaOpt.isPresent()) {
            FinDia finDia = finDiaOpt.get();

            BigDecimal retiroAnterior = finDia.getRetiro();
            BigDecimal diferenciaRetiro = montoRetiro.subtract(retiroAnterior);

            boveda.setTotalAcumulado(boveda.getTotalAcumulado().add(diferenciaRetiro));
            bovedaRepo.save(boveda);

            gastoRepo.findByFechaAndProveedorAndOrigen(fecha, "FIN DÍA", "ingreso")
                    .ifPresent(gastoRepo::delete);

            GastoProveedor ingresoBoveda = new GastoProveedor();
            ingresoBoveda.setFecha(fecha);
            ingresoBoveda.setProveedor("FIN DÍA");
            ingresoBoveda.setDescripcion("Ingreso de efectivo actualizado del fin de día a la bóveda");
            ingresoBoveda.setOrigen("ingreso");
            ingresoBoveda.setMonto(montoRetiro);
            gastoRepo.save(ingresoBoveda);

            finDia.setSaldoInicial(saldoInicialEditado);
            finDia.setRetiro(montoRetiro);
            finDia.setTotalVentas(ventasTotales);
            finDia.setTotalGastos(gastos);
            finDia.setSaldoFinal(saldoFinal);
            finDiaRepo.save(finDia);

        } else {
            finDiaService.registrarFinDiaConSaldo(fecha, montoRetiro, usuario, saldoInicialEditado);

        }

        return "redirect:/iniciar-fin-dia?success=true";
    }


    @GetMapping("/reportes-fin-dia")
    public String verReportes(Authentication authentication, Model model) throws JsonProcessingException {
        if (authentication != null && authentication.getPrincipal() instanceof Usuarios usuario) {
            model.addAttribute("usuario", usuario.getNombre() + " " + usuario.getApellido());
        }

        List<FinDia> todos = finDiaRepo.findAll(Sort.by(Sort.Direction.ASC, "fecha"));

        Optional<FinDia> diaMayorVenta = todos.stream().max(Comparator.comparing(FinDia::getTotalVentas));
        Optional<FinDia> diaMenorVenta = todos.stream().min(Comparator.comparing(FinDia::getTotalVentas));

        model.addAttribute("mayorVenta", diaMayorVenta.orElse(null));
        model.addAttribute("menorVenta", diaMenorVenta.orElse(null));

        List<String> etiquetas = todos.stream()
                .map(f -> f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM")))
                .toList();

        List<BigDecimal> valores = todos.stream()
                .map(FinDia::getTotalVentas)
                .toList();

        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        String rangoSemana = inicioSemana.format(formatter) + " - " + finSemana.format(formatter);
        model.addAttribute("rangoSemana", rangoSemana);

        Map<String, BigDecimal> porDiaSemana = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicioSemana.plusDays(i);
            BigDecimal total = todos.stream()
                    .filter(f -> f.getFecha().equals(dia))
                    .map(FinDia::getTotalVentas)
                    .findFirst()
                    .orElse(BigDecimal.ZERO);
            porDiaSemana.put(dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()), total);
        }

        Map<String, List<BigDecimal>> semanasDelMes = new LinkedHashMap<>();
        for (FinDia f : todos) {
            if (f.getFecha().getMonth().equals(hoy.getMonth())) {
                int semana = f.getFecha().get(WeekFields.of(Locale.getDefault()).weekOfMonth());
                String label = "Semana " + semana;
                semanasDelMes.computeIfAbsent(label, k -> new ArrayList<>()).add(f.getTotalVentas());
            }
        }

        List<String> etiquetasSemanas = new ArrayList<>();
        List<BigDecimal> promediosSemanas = new ArrayList<>();
        for (Map.Entry<String, List<BigDecimal>> entry : semanasDelMes.entrySet()) {
            etiquetasSemanas.add(entry.getKey());
            BigDecimal suma = entry.getValue().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            promediosSemanas.add(suma.divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP));
        }

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("etiquetas", mapper.writeValueAsString(etiquetas));
        model.addAttribute("valores", mapper.writeValueAsString(valores));
        model.addAttribute("etiquetasSemanas", mapper.writeValueAsString(etiquetasSemanas));
        model.addAttribute("promediosSemanas", mapper.writeValueAsString(promediosSemanas));

        File carpeta = new File("archivos/reportes");
        File[] archivosPdf = carpeta.exists() ? carpeta.listFiles((dir, name) -> name.endsWith(".pdf")) : new File[0];

        List<String> archivosList = archivosPdf != null
                ? Arrays.stream(archivosPdf)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .limit(5)
                .map(File::getName)
                .collect(Collectors.toList())
                : List.of();

        model.addAttribute("reportesGuardados", archivosList);


        return "fin_dia/reportes_fin_dia";
    }


    @GetMapping("/reporte-diario")
    public void generarReporteDiario(HttpServletResponse response) throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        BigDecimal transferencias = compraRepositorio.sumTotalTransferenciasByFecha(inicio, fin);
        if (transferencias == null) transferencias = BigDecimal.ZERO;

        BigDecimal ventasFisicas = compraRepositorio.sumTotalByFechaExcluyendoTransferencia(inicio, fin);
        if (ventasFisicas == null) ventasFisicas = BigDecimal.ZERO;

        BigDecimal ventasTotales = ventasFisicas.add(transferencias);

        BigDecimal totalGastosCaja = gastoRepo.restGastosCajaPorFecha(hoy);
        if (totalGastosCaja == null) totalGastosCaja = BigDecimal.ZERO;

        BigDecimal totalGastosBoveda = gastoRepo.sumBovedaByFecha(hoy);
        if (totalGastosBoveda == null) totalGastosBoveda = BigDecimal.ZERO;

        BigDecimal totalGastos = totalGastosCaja.add(totalGastosBoveda);

        BigDecimal saldoBoveda = bovedaRepo.findById(1L)
                .map(Boveda::getTotalAcumulado)
                .orElse(BigDecimal.ZERO);

        Optional<FinDia> finDiaOpt = finDiaRepo.findByFecha(hoy);
        BigDecimal montoRetiro = finDiaOpt.map(FinDia::getRetiro).orElse(BigDecimal.ZERO);

        List<String> origenes = List.of("caja", "boveda", "retiro", "correccion", "ingreso");
        List<GastoProveedor> gastos = gastoRepo.findByFechaAndOrigenIn(hoy, origenes);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, baos);
        document.open();

        Font tituloFont       = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.DARK_GRAY);
        Font subtituloFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(0, 102, 204));
        Font textoFont        = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font textoBoldFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font tablaHeaderFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        Font tablaFont        = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4});

        InputStream imageStream = getClass().getResourceAsStream("/static/img/logo.png");
        if (imageStream != null) {
            byte[] imageBytes = imageStream.readAllBytes();
            Image logo = Image.getInstance(imageBytes);
            logo.scaleToFit(80, 80);
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(logoCell);
        } else {
            PdfPCell emptyLogo = new PdfPCell(new Phrase("LOGO"));
            emptyLogo.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(emptyLogo);
        }

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.addElement(new Paragraph("REPORTE DIARIO", tituloFont));
        String fechaInfo = "Fecha: " + hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " | Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        headerCell.addElement(new Paragraph(fechaInfo, textoFont));
        headerTable.addCell(headerCell);
        document.add(headerTable);

        Paragraph separator = new Paragraph();
        separator.setSpacingAfter(15f);
        Chunk line = new Chunk(new LineSeparator(1f, 100f, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, 0));
        separator.add(line);
        document.add(separator);

        Paragraph resumenTitle = new Paragraph("RESUMEN FINANCIERO", subtituloFont);
        resumenTitle.setSpacingAfter(12f);
        document.add(resumenTitle);

        PdfPTable resumenTable = new PdfPTable(4);
        resumenTable.setWidthPercentage(100);
        resumenTable.setWidths(new float[]{3, 2, 3, 2});
        resumenTable.setSpacingAfter(15f);

        addResumenHeader(resumenTable, "VENTAS", "MONTO", tablaHeaderFont, new BaseColor(59, 89, 152));
        addResumenHeader(resumenTable, "GASTOS", "MONTO", tablaHeaderFont, new BaseColor(59, 89, 152));

        addResumenRow(resumenTable, "Ventas totales",              ventasTotales, textoBoldFont);
        addResumenRow(resumenTable, "Gastos de caja",              totalGastosCaja, textoFont);
        addResumenRow(resumenTable, "Ventas en efectivo", ventasFisicas, textoBoldFont);
        addResumenRow(resumenTable, "Gastos de bóveda",            totalGastosBoveda, textoFont);
        addResumenRow(resumenTable, "Transferencias",              transferencias, textoFont);
        addResumenRow(resumenTable, "Total gastos",                totalGastos, textoBoldFont);
        addResumenRow(resumenTable, "Ingreso a bóveda",            montoRetiro, textoFont);
        addResumenRow(resumenTable, "Total en bóveda",             saldoBoveda, textoBoldFont);

        document.add(resumenTable);

        Paragraph gastosTitle = new Paragraph("DETALLE DE GASTOS", subtituloFont);
        gastosTitle.setSpacingAfter(10f);
        document.add(gastosTitle);

        PdfPTable gastosTable = new PdfPTable(4);
        gastosTable.setWidthPercentage(100);
        gastosTable.setWidths(new float[]{25, 15, 15, 45});
        gastosTable.setSpacingBefore(5f);

        String[] headers = {"PROVEEDOR", "MONTO", "ORIGEN", "DESCRIPCIÓN"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, tablaHeaderFont));
            cell.setBackgroundColor(new BaseColor(77, 136, 204));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            gastosTable.addCell(cell);
        }

        BaseColor rowColor = new BaseColor(240, 240, 240);
        for (int i = 0; i < gastos.size(); i++) {
            GastoProveedor gasto = gastos.get(i);
            BaseColor bgColor = (i % 2 == 0) ? rowColor : BaseColor.WHITE;
            setRowBackground(gastosTable, bgColor);

            gastosTable.addCell(createCell(gasto.getProveedor(), tablaFont, Element.ALIGN_LEFT));
            gastosTable.addCell(createCell("$" + gasto.getMonto().toPlainString(), tablaFont, Element.ALIGN_RIGHT));
            gastosTable.addCell(createCell(gasto.getOrigen().toUpperCase(), tablaFont, Element.ALIGN_CENTER));
            String desc = gasto.getDescripcion() != null && !gasto.getDescripcion().isBlank() ? gasto.getDescripcion() : "-";
            gastosTable.addCell(createCell(desc, tablaFont, Element.ALIGN_LEFT));
        }

        document.add(gastosTable);

        Paragraph footer = new Paragraph();
        footer.setSpacingBefore(20f);
        footer.add(new Chunk("© " + LocalDate.now().getYear() + " - Mi Empresa | Reporte generado automáticamente",
                FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY)));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        Path path = Paths.get("archivos/reportes");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        Path filePath = path.resolve("reporte_diario_" + hoy + ".pdf");
        Files.write(filePath, baos.toByteArray());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_diario_" + hoy + ".pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }



    @GetMapping("/descargar-reporte/{nombre}")
    public void descargarReporte(@PathVariable String nombre, HttpServletResponse response) throws IOException {
        File archivo = new File("archivos/reportes/" + nombre);
        if (archivo.exists()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + nombre);
            Files.copy(archivo.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @PostMapping("/api/registrar-gasto")
    @ResponseBody
    public ResponseEntity<?> registrarGastoAjax(
            @RequestParam String proveedor,
            @RequestParam BigDecimal montoCaja,
            @RequestParam(required = false) BigDecimal montoBoveda,
            @RequestParam String descripcion
    ) {
        try {
            finDiaService.registrarGastoProveedor(proveedor, montoCaja, montoBoveda, descripcion);

            LocalDate hoy = LocalDate.now();
            BigDecimal ventasFisicas = compraRepositorio.sumTotalByFechaExcluyendoTransferencia(
                    hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
            BigDecimal gastos = gastoRepo.restGastosCajaPorFecha(hoy);

            BigDecimal saldoInicial = finDiaRepo.findFirstByOrderByFechaDesc()
                    .map(FinDia::getSaldoFinal)
                    .orElse(BigDecimal.ZERO);

            BigDecimal dineroEnCaja = saldoInicial.add(ventasFisicas != null ? ventasFisicas : BigDecimal.ZERO)
                    .subtract(gastos != null ? gastos : BigDecimal.ZERO);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("gastos", gastos.toString());
            respuesta.put("dineroEnCaja", dineroEnCaja.toString());

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    private void addResumenHeader(PdfPTable table, String title1, String title2, Font font, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(title1, font));
        cell.setBackgroundColor(color);
        cell.setPadding(7);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(title2, font));
        cell.setBackgroundColor(color);
        cell.setPadding(7);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addResumenRow(PdfPTable table, String concepto, BigDecimal monto, Font font) {
        table.addCell(createCell(concepto, font, Element.ALIGN_LEFT));
        table.addCell(createCell("$" + monto.toPlainString(), font, Element.ALIGN_RIGHT));
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private void setRowBackground(PdfPTable table, BaseColor color) {
        for (int i = 0; i < 4; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(color);
            table.addCell(cell);
        }
    }



        
}
