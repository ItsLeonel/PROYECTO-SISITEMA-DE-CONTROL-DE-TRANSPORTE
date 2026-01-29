package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel para exportar listados de buses a Excel
 * Requisito: ru18+19+20v1.1
 */
public class PanelExportarBuses extends JPanel {

    private BusService busService;
    private JComboBox<String> cmbEstado;
    private JTextField txtBase;
    private JTextField txtRuta;
    private JTextArea txtLog;

    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color SUCCESS_COLOR = new Color(0, 150, 136);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);

    public PanelExportarBuses() {
        this.busService = new BusService();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(construirFormulario(), BorderLayout.CENTER);
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Exportar Listado de Buses");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 10, 15, 10);

        // Estado
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(crearLabel("Estado:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cmbEstado = new JComboBox<>(new String[]{"Todos", "ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbEstado.setBackground(new Color(21, 44, 82));
        cmbEstado.setForeground(Color.WHITE);
        panel.add(cmbEstado, gbc);

        gbc.gridwidth = 1;

        // Base
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(crearLabel("Base (opcional):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtBase = crearTextField(25);
        panel.add(txtBase, gbc);

        gbc.gridwidth = 1;

        // Ruta
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(crearLabel("Ruta:"), gbc);

        gbc.gridx = 1;
        txtRuta = crearTextField(25);
        txtRuta.setText(System.getProperty("user.home") + "/Buses_Export.xlsx");
        panel.add(txtRuta, gbc);

        gbc.gridx = 2;
        JButton btnExaminar = new JButton("Examinar");
        btnExaminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExaminar.setForeground(Color.WHITE);
        btnExaminar.setBackground(new Color(108, 117, 125));
        btnExaminar.setBorderPainted(false);
        btnExaminar.setFocusPainted(false);
        btnExaminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExaminar.setPreferredSize(new Dimension(100, 35));
        btnExaminar.addActionListener(e -> seleccionarRuta());
        panel.add(btnExaminar, gbc);

        // Botón Exportar
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(25, 10, 10, 10);
        JButton btnExportar = new JButton("📤 Exportar a Excel");
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setBackground(SUCCESS_COLOR);
        btnExportar.setBorderPainted(false);
        btnExportar.setFocusPainted(false);
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportar.setPreferredSize(new Dimension(200, 45));
        btnExportar.addActionListener(e -> exportarBuses());
        panel.add(btnExportar, gbc);

        // Log
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(crearLabel("Registro de exportación:"), gbc);

        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtLog = new JTextArea(8, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(21, 44, 82));
        txtLog.setForeground(Color.WHITE);
        txtLog.setBorder(BorderFactory.createLineBorder(new Color(45, 80, 130), 1));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(45, 80, 130), 1));
        panel.add(scrollLog, gbc);

        return panel;
    }

    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar ubicación de exportación");
        fileChooser.setSelectedFile(new java.io.File("Buses_Export.xlsx"));

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".xlsx")) {
                ruta += ".xlsx";
            }
            txtRuta.setText(ruta);
        }
    }

    private void exportarBuses() {
        String ruta = txtRuta.getText().trim();

        if (ruta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor especifique la ruta de exportación.",
                    "Ruta requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        agregarLog("Iniciando exportación de buses...");

        String estado = (String) cmbEstado.getSelectedItem();
        String base = txtBase.getText().trim();

        String filtroEstado = "Todos".equals(estado) ? null : estado;
        String filtroBase = base.isEmpty() ? null : base;

        ResultadoOperacion resultado = filtroBase != null || filtroEstado != null
                ? busService.listarBusesFiltrado(filtroBase, filtroEstado)
                : busService.listarTodosBuses();

        if (!resultado.isExito()) {
            agregarLog("ERROR: " + resultado.getMensaje());
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        @SuppressWarnings("unchecked")
        List<Bus> buses = (List<Bus>) resultado.getDatos();

        try {
            exportarAExcel(buses, ruta);
            agregarLog("✅ Exportación completada exitosamente.");
            agregarLog("📁 Archivo guardado en: " + ruta);
            JOptionPane.showMessageDialog(this,
                    "El archivo de buses se ha exportado correctamente.",
                    "Exportación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            agregarLog("ERROR: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al exportar el archivo: " + e.getMessage(),
                    "Error de exportación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarAExcel(List<Bus> buses, String ruta) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Buses");

        // Estilo para encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
headerFont.setColor(IndexedColors.WHITE.getIndex());
headerFont.setBold(true);
headerStyle.setFont(headerFont);


        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] columnas = {
                "Placa", "Marca", "Modelo", "Año", "Capacidad",
                "Socio Propietario", "Teléfono Socio", "Base Asignada", "Estado"
        };

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Agregar datos
        int rowNum = 1;
        for (Bus bus : buses) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bus.getPlaca());
            row.createCell(1).setCellValue(bus.getMarca());
            row.createCell(2).setCellValue(bus.getModelo());
            row.createCell(3).setCellValue(bus.getAnioFabricacion());
            row.createCell(4).setCellValue(bus.getCapacidadPasajeros());
            row.createCell(5).setCellValue(bus.getNombresPropietario());
            row.createCell(6).setCellValue(bus.getTelefonoPropietario());
            row.createCell(7).setCellValue(bus.getBaseAsignada());
            row.createCell(8).setCellValue(bus.getEstado());
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir archivo
        try (FileOutputStream fileOut = new FileOutputStream(ruta)) {
            workbook.write(fileOut);
        }

        workbook.close();

        agregarLog("Total de buses exportados: " + buses.size());
    }

    private void agregarLog(String mensaje) {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        txtLog.append("[" + timestamp + "] " + mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JTextField crearTextField(int columnas) {
        JTextField field = new JTextField(columnas);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(21, 44, 82));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
}