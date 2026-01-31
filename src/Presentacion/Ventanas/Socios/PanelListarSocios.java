package Presentacion.Ventanas.Socios;

import Logica.Entidades.Socio;
import Logica.Servicios.ResultadoOperacion;
import Logica.Servicios.SocioService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para listar socios propietarios
 * Requisito: ru5v1.0
 */
public class PanelListarSocios extends JPanel {
    
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);

    private SocioService socioService;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltroEstado;

    public PanelListarSocios() {
        this.socioService = new SocioService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con controles
        add(construirPanelSuperior(), BorderLayout.NORTH);

        // Tabla
        add(construirPanelTabla(), BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarSocios();
    }

    /**
     * Panel superior con filtros y botones
     */
    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // ===== PANEL IZQUIERDO: VOLVER + TÍTULO =====
        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelIzq.setOpaque(false);

        JButton btnVolver = new JButton(" Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVolver.addActionListener(e -> {
            Container parent = SwingUtilities.getAncestorOfClass(PanelSocios.class, this);
            if (parent instanceof PanelSocios) {
                ((PanelSocios) parent).mostrarVista(PanelSocios.MENU);
            }
        });

        JLabel lblTitulo = new JLabel("Listado de Socios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(PRIMARY_COLOR);

        panelIzq.add(btnVolver);
        panelIzq.add(lblTitulo);

        // ===== PANEL DERECHO: FILTROS + BOTONES =====
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        panelControles.setOpaque(false);

        // Filtro de estado
        JLabel lblFiltro = new JLabel("Estado:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "ACTIVO", "INACTIVO"});
        cmbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltroEstado.setPreferredSize(new Dimension(120, 30));

        // ✅ Botón Listar (aplica el filtro)
        JButton btnListar = crearBoton("Listar", PRIMARY_COLOR);
        btnListar.addActionListener(e -> filtrarSocios());

        // Botón Actualizar
        JButton btnActualizar = crearBoton("Actualizar", PRIMARY_COLOR);
        btnActualizar.addActionListener(e -> cargarSocios());

        // Botón Exportar
        JButton btnExportar = crearBoton("Exportar Excel", PRIMARY_COLOR);
        btnExportar.addActionListener(e -> exportarSocios());

        panelControles.add(lblFiltro);
        panelControles.add(cmbFiltroEstado);
        panelControles.add(btnListar);
        panelControles.add(btnActualizar);
        panelControles.add(btnExportar);

        // ===== ENSAMBLAR =====
        panel.add(panelIzq, BorderLayout.WEST);
        panel.add(panelControles, BorderLayout.EAST);

        return panel;
    }

    /**
     * Panel con tabla
     */
    private JScrollPane construirPanelTabla() {
        // Modelo de tabla
        String[] columnas = {
                "Código", "Reg. Municipal", "Cédula", "Nombres Completos",
                "Celular", "Placa Bus", "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tabla
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(PRIMARY_COLOR);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(230, 230, 230));

        // Ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);   // Código
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);  // Reg. Municipal
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);  // Cédula
        tabla.getColumnModel().getColumn(3).setPreferredWidth(250);  // Nombres
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);  // Celular
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);  // Placa
        tabla.getColumnModel().getColumn(6).setPreferredWidth(80);   // Estado

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        return scrollPane;
    }

    /**
     * Cargar socios en la tabla
     */
    private void cargarSocios() {
        modeloTabla.setRowCount(0);

        ResultadoOperacion resultado = socioService.listarSocios();

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<Socio> socios = (List<Socio>) resultado.getDatos();

            for (Socio socio : socios) {
                Object[] fila = {
                        socio.getCodigoSocio(),
                        socio.getRegistroMunicipal(),
                        socio.getCedula(),
                        socio.getNombresCompletos(),
                        socio.getNumeroCelular(),
                        socio.getPlacaBusAsociado() != null ? socio.getPlacaBusAsociado() : "Sin asignar",
                        socio.getEstado()
                };
                modeloTabla.addRow(fila);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Filtrar socios por estado
     */
    private void filtrarSocios() {
        String filtro = (String) cmbFiltroEstado.getSelectedItem();

        if ("Todos".equals(filtro)) {
            cargarSocios();
        } else {
            modeloTabla.setRowCount(0);

            ResultadoOperacion resultado = socioService.listarSociosPorEstado(filtro);

            if (resultado.isExito()) {
                @SuppressWarnings("unchecked")
                List<Socio> socios = (List<Socio>) resultado.getDatos();

                for (Socio socio : socios) {
                    Object[] fila = {
                            socio.getCodigoSocio(),
                            socio.getRegistroMunicipal(),
                            socio.getCedula(),
                            socio.getNombresCompletos(),
                            socio.getNumeroCelular(),
                            socio.getPlacaBusAsociado() != null ? socio.getPlacaBusAsociado() : "Sin asignar",
                            socio.getEstado()
                    };
                    modeloTabla.addRow(fila);
                }
            }
        }
    }

    /**
     * Crear botón con estilo
     */
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(130, 32));
        return boton;
    }

    /**
     * Exportar socios a Excel
     */
    private void exportarSocios() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Socios_Export.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String ruta = chooser.getSelectedFile().getAbsolutePath();
        if (!ruta.endsWith(".xlsx")) {
            ruta += ".xlsx";
        }

        ResultadoOperacion resultado = socioService.listarSocios();
        if (!resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            exportarAExcel((java.util.List<Socio>) resultado.getDatos(), ruta);
            JOptionPane.showMessageDialog(this,
                    "El archivo de socios propietarios se ha exportado correctamente.",
                    "Exportación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Generar archivo Excel
     */
    private void exportarAExcel(java.util.List<Socio> socios, String ruta) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Socios Propietarios");

        // ===== ESTILO ENCABEZADO =====
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);

        String[] columnas = {
            "Código Socio",
            "Registro Municipal",
            "Cédula",
            "Nombres Completos",
            "Número Celular",
            "Placa Bus",
            "Estado"
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
            row.createCell(6).setCellValue(socio.getEstado());
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream out = new FileOutputStream(ruta)) {
            workbook.write(out);
        }
        workbook.close();
    }
}