package Presentacion.Ventanas.Unidades;

import Logica.Entidades.Bus;
import Logica.Servicios.BaseService;
import Logica.Servicios.BusService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Pestaña 3: Reportes y Exportación
 * - Estadísticas de buses (gráfico visual)
 * - Exportación a Excel con filtros
 * - Bases cargadas dinámicamente
 */
public class TabReportes extends JPanel {

    // ===== SERVICIOS =====
    private final BusService busService = new BusService();
    private final BaseService baseService = new BaseService();

    // ===== UI =====
    private JComboBox<String> cbBase;
    private JComboBox<String> cbEstado;
    private JLabel lblTotal;
    private JLabel lblActivos;
    private JLabel lblDesactivados;
    private JLabel lblMantenimiento;
    private JPanel panelGrafico;

    // ===== COLORES =====
    private final Color BG_MAIN   = new Color(11, 22, 38);
    private final Color BG_PANEL  = new Color(18, 36, 64);
    private final Color BG_CARD   = new Color(21, 44, 82);
    private final Color BTN_EXCEL = new Color(0, 150, 136);
    private final Color TXT_SEC   = new Color(190, 200, 215);
    private final Color BORDER    = new Color(45, 80, 130);
    private final Color ESTADO_ACTIVO = new Color(46, 204, 113);
    private final Color ESTADO_DESACTIVADO = new Color(231, 76, 60);
    private final Color ESTADO_MANT = new Color(241, 196, 15);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public TabReportes() {

        setLayout(new BorderLayout(16, 16));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(crearPanelExportacion(), BorderLayout.NORTH);
        add(crearPanelEstadisticas(), BorderLayout.CENTER);

        actualizarEstadisticas();
    }

    // =====================================================
    // PANEL DE EXPORTACIÓN
    // =====================================================
    private JPanel crearPanelExportacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("📥 Exportar Listado a Excel");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ===== FILTROS =====
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelFiltros.setOpaque(false);
        panelFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cargar bases dinámicamente
        cbBase = new JComboBox<>();
        cbBase.addItem("Todas");
        cargarBasesEnFiltro();
        cbBase.setBackground(BG_CARD);
        cbBase.setForeground(Color.WHITE);

        cbEstado = new JComboBox<>(new String[]{"Todos", "ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cbEstado.setBackground(BG_CARD);
        cbEstado.setForeground(Color.WHITE);

        JButton btnExportar = botonExcelConIcono("Exportar Excel", "excel.png");
        btnExportar.addActionListener(e -> exportarExcel());

        panelFiltros.add(label("Base:"));
        panelFiltros.add(cbBase);
        panelFiltros.add(label("Estado:"));
        panelFiltros.add(cbEstado);
        panelFiltros.add(btnExportar);

        // ===== ENSAMBLAR =====
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(16));
        panel.add(panelFiltros);

        return panel;
    }

    // =====================================================
    // PANEL DE ESTADÍSTICAS
    // =====================================================
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("📊 Estadísticas Generales");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ===== CONTADORES =====
        JPanel panelContadores = new JPanel(new GridLayout(1, 4, 16, 0));
        panelContadores.setOpaque(false);
        panelContadores.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelContadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        panelContadores.add(crearTarjetaEstadistica("Total unidades", "0", Color.WHITE));
        panelContadores.add(crearTarjetaEstadistica("Activos", "0", ESTADO_ACTIVO));
        panelContadores.add(crearTarjetaEstadistica("Desactivados", "0", ESTADO_DESACTIVADO));
        panelContadores.add(crearTarjetaEstadistica("Mantenimiento", "0", ESTADO_MANT));

        // Guardar referencias a los labels
        lblTotal = (JLabel) ((JPanel) panelContadores.getComponent(0)).getComponent(2);
        lblActivos = (JLabel) ((JPanel) panelContadores.getComponent(1)).getComponent(2);
        lblDesactivados = (JLabel) ((JPanel) panelContadores.getComponent(2)).getComponent(2);
        lblMantenimiento = (JLabel) ((JPanel) panelContadores.getComponent(3)).getComponent(2);

        // ===== GRÁFICO =====
        panelGrafico = new JPanel();
        panelGrafico.setOpaque(false);
        panelGrafico.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGrafico.setPreferredSize(new Dimension(600, 200));

        // ===== ENSAMBLAR =====
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(panelContadores);
        panel.add(Box.createVerticalStrut(24));
        panel.add(panelGrafico);

        return panel;
    }

    private JPanel crearTarjetaEstadistica(String nombre, String valor, Color color) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(BG_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNombre.setForeground(TXT_SEC);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(lblNombre);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblValor);

        return tarjeta;
    }

    // =====================================================
    // ACTUALIZAR ESTADÍSTICAS
    // =====================================================
    public void actualizarEstadisticas() {
        try {
            List<Bus> todos = busService.listar(null, null);

            int total = todos.size();
            int activos = (int) todos.stream().filter(b -> "ACTIVO".equals(b.getEstado())).count();
            int desactivados = (int) todos.stream().filter(b -> "INACTIVO".equals(b.getEstado())).count();
            int mantenimiento = (int) todos.stream().filter(b -> "MANTENIMIENTO".equals(b.getEstado())).count();

            lblTotal.setText(String.valueOf(total));
            lblActivos.setText(String.valueOf(activos));
            lblDesactivados.setText(String.valueOf(desactivados));
            lblMantenimiento.setText(String.valueOf(mantenimiento));

            actualizarGrafico(activos, desactivados, mantenimiento, total);

        } catch (Exception e) {
            lblTotal.setText("0");
            lblActivos.setText("0");
            lblDesactivados.setText("0");
            lblMantenimiento.setText("0");
        }
    }

    private void actualizarGrafico(int activos, int desactivados, int mantenimiento, int total) {
        panelGrafico.removeAll();

        if (total == 0) {
            JLabel mensaje = new JLabel("No hay datos para mostrar");
            mensaje.setForeground(TXT_SEC);
            mensaje.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            panelGrafico.add(mensaje);
            panelGrafico.revalidate();
            panelGrafico.repaint();
            return;
        }

        // Crear panel de barras horizontales
        JPanel barras = new JPanel();
        barras.setLayout(new BoxLayout(barras, BoxLayout.Y_AXIS));
        barras.setOpaque(false);

        barras.add(crearBarraEstadistica("Activos", activos, total, ESTADO_ACTIVO));
        barras.add(Box.createVerticalStrut(12));
        barras.add(crearBarraEstadistica("Desactivados", desactivados, total, ESTADO_DESACTIVADO));
        barras.add(Box.createVerticalStrut(12));
        barras.add(crearBarraEstadistica("Mantenimiento", mantenimiento, total, ESTADO_MANT));

        panelGrafico.add(barras);
        panelGrafico.revalidate();
        panelGrafico.repaint();
    }

    private JPanel crearBarraEstadistica(String nombre, int cantidad, int total, Color color) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Etiqueta
        JLabel lbl = new JLabel(nombre);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(120, 30));

        // Barra de progreso
        JProgressBar barra = new JProgressBar(0, total);
        barra.setValue(cantidad);
        barra.setStringPainted(true);
        barra.setString(cantidad + " / " + total + " (" + (total > 0 ? (cantidad * 100 / total) : 0) + "%)");
        barra.setForeground(color);
        barra.setBackground(BG_CARD);
        barra.setBorder(BorderFactory.createLineBorder(BORDER));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(barra, BorderLayout.CENTER);

        return panel;
    }

    // =====================================================
    // EXPORTAR EXCEL
    // =====================================================
    private void exportarExcel() {
        String base = cbBase.getSelectedItem().equals("Todas") ? null : cbBase.getSelectedItem().toString();
        String estado = cbEstado.getSelectedItem().equals("Todos") ? null : cbEstado.getSelectedItem().toString();

        try {
            List<Bus> buses = busService.listar(base, estado);

            if (buses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de unidades: no existen unidades que coincidan con los criterios ingresados.",
                    "Error de Exportación",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser ch = new JFileChooser();
            ch.setSelectedFile(new java.io.File("unidades.xlsx"));

            if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            try (Workbook wb = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(ch.getSelectedFile())) {

                Sheet sheet = wb.createSheet("Unidades");

                // Estilo para encabezado
                CellStyle styleHeader = wb.createCellStyle();
                styleHeader.setAlignment(HorizontalAlignment.CENTER);
                styleHeader.setBorderBottom(BorderStyle.THIN);
                styleHeader.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
                
                org.apache.poi.ss.usermodel.Font font = wb.createFont();
                font.setBold(true);
                styleHeader.setFont(font);

                // Encabezados
                Row header = sheet.createRow(0);
                String[] columnas = {"Código", "Placa", "Dueño", "Marca", "Modelo", "Año", "Base", "Estado"};
                for (int c = 0; c < columnas.length; c++) {
                    Cell cell = header.createCell(c);
                    cell.setCellValue(columnas[c]);
                    cell.setCellStyle(styleHeader);
                }

                // Datos
                int rowNum = 1;
                for (Bus b : buses) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(b.getCodigo());
                    row.createCell(1).setCellValue(b.getPlaca());
                    row.createCell(2).setCellValue(b.getDueno());
                    row.createCell(3).setCellValue(b.getMarca());
                    row.createCell(4).setCellValue(b.getModelo());
                    row.createCell(5).setCellValue(b.getAnioFabricacion());
                    row.createCell(6).setCellValue(b.getBase());
                    row.createCell(7).setCellValue(b.getEstado());
                }

                // Autoajustar columnas
                for (int c = 0; c < columnas.length; c++) {
                    sheet.autoSizeColumn(c);
                }

                wb.write(fos);

                JOptionPane.showMessageDialog(this,
                    "Archivo .xlsx de unidades exportada correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de unidades: no se pudo generar el archivo de exportación.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // CARGAR BASES DINÁMICAMENTE
    // =====================================================
    private void cargarBasesEnFiltro() {
        try {
            List<String> bases = baseService.listarNombres();
            for (String base : bases) {
                cbBase.addItem(base);
            }
        } catch (Exception e) {
            // Silencioso o mostrar warning
        }
    }

    // =====================================================
    // UI HELPERS
    // =====================================================
    
    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(TXT_SEC);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private JButton botonExcelConIcono(String texto, String nombreIcono) {
        JButton b = new JButton(texto);
        
        java.net.URL url = getClass().getResource("/Presentacion/Recursos/icons/" + nombreIcono);
        if (url != null) {
            b.setIcon(new ImageIcon(url));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setIconTextGap(10);
        }
        
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(BTN_EXCEL);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return b;
    }
}