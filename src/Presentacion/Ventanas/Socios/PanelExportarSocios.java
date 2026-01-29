package Presentacion.Ventanas.Socios;

import Logica.Entidades.Socio;
import Logica.Servicios.ResultadoOperacion;
import Logica.Servicios.SocioService;
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
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel para exportar listado de socios a Excel
 * Requisito: ru6v1.1
 */
public class PanelExportarSocios extends JPanel {

    // 🎨 Colores del tema (UNA SOLA VEZ)
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);

    private final SocioService socioService;
    private JComboBox<String> cmbFormato;
    private JTextField txtRuta;
    private JTextArea txtLog;

    public PanelExportarSocios() {
        this.socioService = new SocioService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(construirPanelExportacion(), BorderLayout.CENTER);
    }

    private JPanel construirPanelExportacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Exportar Listado de Socios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 10, 15, 10);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(crearLabel("Formato:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cmbFormato = new JComboBox<>(new String[]{"Excel (.xlsx)"});
        cmbFormato.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbFormato, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(crearLabel("Ruta:"), gbc);

        gbc.gridx = 1;
        txtRuta = crearTextField(25);
        txtRuta.setText(System.getProperty("user.home") + "/Socios_Export.xlsx");
        panel.add(txtRuta, gbc);

        gbc.gridx = 2;
        JButton btnExaminar = crearBoton("Examinar", SECONDARY_COLOR);
        btnExaminar.setPreferredSize(new Dimension(100, 35));
        btnExaminar.addActionListener(e -> seleccionarRuta());
        panel.add(btnExaminar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JButton btnExportar = crearBoton("📤 Exportar Listado", SUCCESS_COLOR);
        btnExportar.setPreferredSize(new Dimension(220, 45));
        btnExportar.addActionListener(e -> exportarSocios());
        panel.add(btnExportar, gbc);

        gbc.gridy = 4;
        panel.add(crearLabel("Registro de exportación:"), gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        txtLog = new JTextArea(8, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtLog);
        panel.add(scroll, gbc);

        return panel;
    }

    private void seleccionarRuta() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Socios_Export.xlsx"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".xlsx")) ruta += ".xlsx";
            txtRuta.setText(ruta);
        }
    }

    private void exportarSocios() {
        ResultadoOperacion resultado = socioService.listarSocios();
        if (!resultado.isExito()) {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            exportarAExcel((List<Socio>) resultado.getDatos(), txtRuta.getText());
            JOptionPane.showMessageDialog(this, "Exportación exitosa", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void exportarAExcel(List<Socio> socios, String ruta) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Socios Propietarios");

    // ===== ESTILO ENCABEZADO (POI FONT) =====
    org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerFont.setColor(IndexedColors.WHITE.getIndex());

    CellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    headerStyle.setFont(headerFont);

    // Encabezados
   String[] columnas = {
    "Código", "Reg. Municipal", "Cédula", "Nombres Completos",
    "Celular", "Placa Bus", "Estado"
};


    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < columnas.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columnas[i]);
        cell.setCellStyle(headerStyle);
    }

    int rowNum = 1;
    for (Socio socio : socios) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(socio.getCodigoSocio());
        row.createCell(1).setCellValue(socio.getRegistroMunicipal());
        row.createCell(2).setCellValue(socio.getCedula());
        row.createCell(3).setCellValue(socio.getNombresCompletos());
        row.createCell(4).setCellValue(socio.getNumeroCelular());
        row.createCell(5).setCellValue(
                socio.getPlacaBusAsociado() == null ? "Sin asignar" : socio.getPlacaBusAsociado()
        );
    }

    for (int i = 0; i < columnas.length; i++) {
        sheet.autoSizeColumn(i);
    }

    try (FileOutputStream out = new FileOutputStream(ruta)) {
        workbook.write(out);
    }
    workbook.close();
}


    private JLabel crearLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    private JTextField crearTextField(int c) {
        JTextField f = new JTextField(c);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    private JButton crearBoton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
