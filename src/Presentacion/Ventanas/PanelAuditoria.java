package Presentacion.Ventanas;

import Logica.Entidades.AuditoriaEvento;
import Logica.Servicios.AuditoriaService;
import Presentacion.Recursos.UITheme;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PanelAuditoria extends JPanel {

    // =====================================================
    // PALETA DE COLORES
    // =====================================================
    private static final Color BG_MAIN  = new Color(10, 20, 38);
    //private static final Color BG_MAIN = Color.WHITE;  //FONDO BLANCO DEL FONDO

    private static final Color BG_CARD  = new Color(18, 35, 64);
    private static final Color BG_TABLE = new Color(14, 30, 58);

    private static final Color BORDER   = new Color(45, 80, 130);
    private static final Color TXT_MAIN = Color.WHITE;
    private static final Color TXT_SEC  = new Color(185, 195, 210);

    private static final Color BTN_MAIN = new Color(36, 96, 200);
    private static final Color BTN_EXCEL = new Color(0, 150, 136); // verde Excel
    


    // =====================================================
    // UI
    // =====================================================
    private DefaultTableModel model;
    private JTable table;

    private JTextField txtDesde;
    private JTextField txtHasta;
    private JComboBox<String> cbModulo;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private List<AuditoriaEvento> vistaActual = new ArrayList<>();

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public PanelAuditoria() {

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // ===== HEADER =====
        JLabel titulo = new JLabel("Auditoría");
        titulo.setFont(UITheme.H1);
        titulo.setForeground(TXT_MAIN);

        JLabel desc = new JLabel("Consulta de registros de auditoría del sistema.");
        desc.setFont(UITheme.BODY);
        desc.setForeground(TXT_SEC);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titulo);
        header.add(Box.createVerticalStrut(6));
        header.add(desc);

        // ===== FILTROS =====
        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setBackground(BG_CARD);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 12);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0;
        filtros.add(label("Desde"), gc);
        txtDesde = campoFecha();
        gc.gridx = 1;
        filtros.add(txtDesde, gc);

        gc.gridx = 2;
        filtros.add(label("Hasta"), gc);
        txtHasta = campoFecha();
        gc.gridx = 3;
        filtros.add(txtHasta, gc);

        gc.gridx = 4;
        filtros.add(label("Módulo"), gc);
        cbModulo = combo(new String[]{
                "TODOS",
                "Gestión del Sistema",
                "Unidades",
                "Transporte",
                "Turnos",
                "Sanciones"
        });
        gc.gridx = 5;
        filtros.add(cbModulo, gc);

        gc.gridx = 6;
        gc.weightx = 1;
        filtros.add(Box.createHorizontalGlue(), gc);

        // ===== BOTONES =====
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        JButton btnVerTodos = boton("Ver todos", "list.png");
        JButton btnBuscar   = boton("Buscar", "search.png");
        JButton btnExportar = boton("Exportar Excel", "exccel.png");
        btnExportar.setBackground(BTN_EXCEL);
        btnExportar.setForeground(Color.WHITE);


        acciones.add(btnVerTodos);
        acciones.add(btnBuscar);
        acciones.add(btnExportar);

        JPanel barra = new JPanel(new GridLayout(2, 1, 0, 10));
        barra.setOpaque(false);
        barra.add(filtros);
        barra.add(acciones);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(barra, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // ===== TABLA =====
        model = new DefaultTableModel(new String[]{"Fecha / Hora", "Módulo", "Acción"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setBackground(BG_TABLE);
        table.setForeground(Color.WHITE);
        table.setGridColor(BORDER);
        table.setSelectionBackground(BTN_MAIN);
        table.setSelectionForeground(Color.WHITE);

JTableHeader th = table.getTableHeader();
th.setBackground(BG_CARD);
th.setForeground(Color.WHITE);
th.setFont(new Font("Segoe UI", Font.BOLD, 13));
th.setReorderingAllowed(false);
th.setResizingAllowed(false);
th.setFocusable(false);

// 🔥 LÍNEA CLAVE
th.setOpaque(true);
DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
headerRenderer.setBackground(BG_CARD);
headerRenderer.setForeground(Color.WHITE);
headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
headerRenderer.setOpaque(true);

for (int i = 0; i < table.getColumnCount(); i++) {
    table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
}



        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG_TABLE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        add(sp, BorderLayout.CENTER);

        // ===== EVENTOS =====
        btnBuscar.addActionListener(e -> cargarTablaFiltrada());
        btnVerTodos.addActionListener(e -> cargarTablaSinFiltros());
        btnExportar.addActionListener(e -> exportarVistaActualExcel());
    }

    // =====================================================
    // RA2 – VER TODOS
    // =====================================================
    private void cargarTablaSinFiltros() {

        model.setRowCount(0);
        vistaActual.clear();

        for (AuditoriaEvento ev : AuditoriaService.listarTodos()) {
            vistaActual.add(ev);
            model.addRow(new Object[]{
                    fmt.format(ev.getFechaHora()),
                    ev.getModulo(),
                    ev.getAccion()
            });
        }

        JOptionPane.showMessageDialog(this,
                vistaActual.isEmpty()
                        ? "No existen registros de auditoría para mostrar."
                        : "Consulta de registros de auditoría realizada correctamente.",
                "Auditoría",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // =====================================================
    // RA3 y RA4 – FILTROS
    // =====================================================
    private void cargarTablaFiltrada() {

        model.setRowCount(0);
        vistaActual.clear();

        String mod = (String) cbModulo.getSelectedItem();
        LocalDateTime desde = null;
        LocalDateTime hasta = null;

        try {
            if (!txtDesde.getText().trim().isEmpty())
                desde = LocalDate.parse(txtDesde.getText().trim(), fmtFecha).atStartOfDay();
            if (!txtHasta.getText().trim().isEmpty())
                hasta = LocalDate.parse(txtHasta.getText().trim(), fmtFecha).atTime(LocalTime.MAX);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use yyyy-MM-dd.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (AuditoriaEvento ev : AuditoriaService.listarTodos()) {

            boolean okModulo = coincideModulo(mod, ev.getModulo());
            boolean okFecha = true;

            if (desde != null && ev.getFechaHora().isBefore(desde)) okFecha = false;
            if (hasta != null && ev.getFechaHora().isAfter(hasta)) okFecha = false;

            if (okModulo && okFecha) {
                vistaActual.add(ev);
                model.addRow(new Object[]{
                        fmt.format(ev.getFechaHora()),
                        ev.getModulo(),
                        ev.getAccion()
                });
            }
        }

        if (desde != null || hasta != null) {
            JOptionPane.showMessageDialog(this,
                    vistaActual.isEmpty()
                            ? "No existen registros de auditoría en el rango de fechas seleccionado."
                            : "Consulta de registros de auditoría por rango de fechas realizada correctamente.",
                    "Auditoría",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!"TODOS".equals(mod)) {
            JOptionPane.showMessageDialog(this,
                    vistaActual.isEmpty()
                            ? "No existen registros de auditoría para el módulo " + mod + "."
                            : "Consulta de registros de auditoría del módulo " + mod + " realizada correctamente.",
                    "Auditoría",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =====================================================
    // RA5 – EXPORTAR EXCEL
    // =====================================================
    private void exportarVistaActualExcel() {

        if (vistaActual.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo de auditoría: no existen registros para exportar.",
                    "Auditoría",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File("auditoria.xlsx"));

        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(ch.getSelectedFile())) {

            Sheet sheet = wb.createSheet("Auditoría");

            Row h = sheet.createRow(0);
            h.createCell(0).setCellValue("Fecha/Hora");
            h.createCell(1).setCellValue("Módulo");
            h.createCell(2).setCellValue("Acción");

            int r = 1;
            for (AuditoriaEvento ev : vistaActual) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(fmt.format(ev.getFechaHora()));
                row.createCell(1).setCellValue(ev.getModulo());
                row.createCell(2).setCellValue(ev.getAccion());
            }

            for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

            wb.write(fos);

            JOptionPane.showMessageDialog(this,
                    "Archivo de auditoría exportado correctamente.",
                    "Auditoría",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // HELPERS UI
    // =====================================================
    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_SEC);
        return l;
    }

    private JTextField campoFecha() {
        JTextField t = new JTextField(10);
        t.setBackground(BG_CARD);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(BORDER));
        t.setToolTipText("yyyy-MM-dd");
        return t;
    }

    private JComboBox<String> combo(String[] data) {
        JComboBox<String> c = new JComboBox<>(data);
        c.setBackground(BG_CARD);
        c.setForeground(Color.WHITE);
        return c;
    }

    private JButton boton(String texto, String icono) {
        JButton b = new JButton(texto);
        b.setBackground(BTN_MAIN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        if (icono != null) {
            b.setIcon(new ImageIcon(
                    getClass().getResource("/Presentacion/Recursos/icons/" + icono)
            ));
            b.setIconTextGap(8);
        }
        return b;
    }

    private boolean coincideModulo(String filtroUI, String moduloEvento) {
        if ("TODOS".equalsIgnoreCase(filtroUI)) return true;
        if (moduloEvento == null) return false;

        String m = moduloEvento.toLowerCase();

        switch (filtroUI) {
            case "Gestión del Sistema": return m.contains("gestion") || m.contains("sistema");
            case "Unidades": return m.contains("bus") || m.contains("buses") || m.contains("unidades");
            case "Transporte": return m.contains("transporte");
            case "Turnos": return m.contains("turno");
            case "Sanciones": return m.contains("sancion");
            default: return false;
        }
    }
}
