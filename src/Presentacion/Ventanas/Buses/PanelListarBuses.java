package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Panel para listar la flota de buses
 * Requisitos: ru21v1.1, ru22v1.1, ru23v1.1, ru24v1.1
 * Muestra SOLO los atributos especificados en los requisitos
 */
public class PanelListarBuses extends JPanel {

    private BusService busService;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltroEstado;
    private JTextField txtFiltroBase;
    private PanelBuses parent; 

    // Colores del tema
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color BG_CARD = new Color(21, 44, 82);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);
    private static final Color BORDER_COLOR = new Color(45, 80, 130);
    
    // Colores de estados
    private static final Color ESTADO_ACTIVO = new Color(46, 204, 113);     // Verde
    private static final Color ESTADO_INACTIVO = new Color(231, 76, 60);    // Rojo
    private static final Color ESTADO_MANT = new Color(241, 196, 15);       // Amarillo
    
    // Colores para botones de acciones
    private static final Color BTN_ACTIVAR = new Color(40, 167, 69);        // Verde
    private static final Color BTN_INACTIVAR = new Color(220, 53, 69);      // Rojo
    private static final Color BTN_MANTENIMIENTO = new Color(255, 193, 7);  // Amarillo
    private static final Color BTN_EXPORTAR = new Color(25, 135, 84);       // Verde oscuro

    public PanelListarBuses(PanelBuses parent) {
        this.parent = parent;
        this.busService = new BusService();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);

        // ===== PANEL INFERIOR =====
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);

        // Izquierda: Botón Volver
        JButton btnVolver = crearBoton("⬅ Volver", PRIMARY_COLOR, null);
        btnVolver.addActionListener(e -> parent.mostrar(PanelBuses.MENU));
        
        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelIzq.setOpaque(false);
        panelIzq.add(btnVolver);

        // ✅ Derecha: Solo botón Actualizar
        JPanel panelDer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDer.setOpaque(false);

        JButton btnActualizar = crearBotonConIcono("Actualizar", PRIMARY_COLOR, 
            "/Presentacion/Recursos/icons/refresh.png");
        btnActualizar.addActionListener(e -> cargarBuses());

        panelDer.add(btnActualizar);

        panelInferior.add(panelIzq, BorderLayout.WEST);
        panelInferior.add(panelDer, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);

        cargarBuses();
    }

    private void cargarBuses() {
        modeloTabla.setRowCount(0);

        ResultadoOperacion resultado = busService.listarTodosBuses();

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<Bus> buses = (List<Bus>) resultado.getDatos();

            for (Bus bus : buses) {
                // ✅ SOLO atributos según requisitos del ERS
                Object[] fila = {
                    bus.getPlaca(),                   // placa
                    bus.getCodigoSocioFk(),          // código del socio propietario
                    bus.getMarca(),                   // marca
                    bus.getModelo(),                  // modelo
                    bus.getAnioFabricacion(),        // año de fabricación
                    bus.getCapacidadPasajeros(),     // capacidad de pasajeros
                    bus.getBaseAsignada(),           // base asignada
                    bus.getEstado()                  // estado
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
     * Panel superior con filtros + Buscar y Acciones
     */
    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        // ===== TÍTULO (IZQUIERDA) =====
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.setBackground(BG_PANEL);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = new JLabel("Listado de Flota");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);

        filtros.add(lblTitulo);

        // ===== FILTROS + BOTONES (DERECHA) =====
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panelDerecho.setOpaque(false);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setForeground(TEXT_SECONDARY);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ✅ ComboBox SIN resaltado blanco
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cmbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltroEstado.setBackground(BG_CARD);
        cmbFiltroEstado.setForeground(Color.WHITE);
        cmbFiltroEstado.setPreferredSize(new Dimension(140, 30));
        cmbFiltroEstado.setFocusable(false);  // ✅ Quita el resaltado de foco
        
        cmbFiltroEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                label.setOpaque(true);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (isSelected) {
                    label.setBackground(PRIMARY_COLOR);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(BG_CARD);
                    label.setForeground(Color.WHITE);
                }
                
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        JLabel lblBase = new JLabel("Base:");
        lblBase.setForeground(TEXT_SECONDARY);
        lblBase.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtFiltroBase = new JTextField(15);
        txtFiltroBase.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFiltroBase.setBackground(BG_CARD);
        txtFiltroBase.setForeground(Color.WHITE);
        txtFiltroBase.setCaretColor(Color.WHITE);
        txtFiltroBase.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // ✅ BOTONES: Buscar y Acciones (Actualizar va abajo)
        JButton btnBuscar = crearBotonConIcono("Buscar", PRIMARY_COLOR, 
            "/Presentacion/Recursos/icons/search.png");
        btnBuscar.addActionListener(e -> aplicarFiltros());

        JButton btnAcciones = crearBotonConIcono("Acciones", PRIMARY_COLOR, 
            "/Presentacion/Recursos/icons/settings.png");
        btnAcciones.addActionListener(e -> mostrarMenuAcciones(btnAcciones));

        panelDerecho.add(lblEstado);
        panelDerecho.add(cmbFiltroEstado);
        panelDerecho.add(lblBase);
        panelDerecho.add(txtFiltroBase);
        panelDerecho.add(btnBuscar);
        panelDerecho.add(btnAcciones);

        panel.add(filtros, BorderLayout.WEST);
        panel.add(panelDerecho, BorderLayout.EAST);

        return panel;
    }

    /**
     * Panel con tabla (✅ SOLO ATRIBUTOS DE REQUISITOS)
     */
    private JScrollPane construirPanelTabla() {
        // ✅ COLUMNAS SEGÚN REQUISITOS (ru21, ru22, ru23, ru24)
        String[] columnas = {
            "Placa",
            "Código Socio",      // código del socio propietario
            "Marca",
            "Modelo",
            "Año",               // año de fabricación
            "Capacidad",         // capacidad de pasajeros
            "Base Asignada",
            "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(32);

        // 🔹 FONDO Y TEXTO
        tabla.setBackground(BG_CARD);
        tabla.setForeground(Color.WHITE);

        // 🔹 CAJONCITOS (GRID)
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(true);
        tabla.setGridColor(new Color(40, 70, 120));
        tabla.setIntercellSpacing(new Dimension(1, 1));

        // 🔹 SELECCIÓN
        tabla.setSelectionBackground(PRIMARY_COLOR);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ===== HEADER AZUL =====
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(20, 50, 100));
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COLOR));
                return label;
            }
        });

        // ===== RENDER ESTADO CON COLORES =====
        tabla.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(Color.WHITE);

                switch (value.toString()) {
                    case "ACTIVO":
                        label.setBackground(ESTADO_ACTIVO);
                        break;
                    case "INACTIVO":
                        label.setBackground(ESTADO_INACTIVO);
                        break;
                    case "MANTENIMIENTO":
                        label.setBackground(ESTADO_MANT);
                        label.setForeground(Color.BLACK);
                        break;
                    default:
                        label.setBackground(BG_CARD);
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                }

                label.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
                return label;
            }
        });

        // ===== ANCHOS DE COLUMNAS =====
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);  // Placa
        tabla.getColumnModel().getColumn(1).setPreferredWidth(120);  // Código Socio
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);  // Marca
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);  // Modelo
        tabla.getColumnModel().getColumn(4).setPreferredWidth(60);   // Año
        tabla.getColumnModel().getColumn(5).setPreferredWidth(80);   // Capacidad
        tabla.getColumnModel().getColumn(6).setPreferredWidth(150);  // Base
        tabla.getColumnModel().getColumn(7).setPreferredWidth(120);  // Estado

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, BORDER_COLOR));
        scrollPane.getViewport().setBackground(BG_CARD);

        return scrollPane;
    }

    /**
     * Aplicar filtros
     */
    private void aplicarFiltros() {
        String estado = (String) cmbFiltroEstado.getSelectedItem();
        String base = txtFiltroBase.getText().trim();

        if ("Todos".equals(estado) && base.isEmpty()) {
            cargarBuses();
            return;
        }

        modeloTabla.setRowCount(0);

        String filtroEstado = "Todos".equals(estado) ? null : estado;
        String filtroBase = base.isEmpty() ? null : base;

        ResultadoOperacion resultado = busService.listarBusesFiltrado(filtroBase, filtroEstado);

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<Bus> buses = (List<Bus>) resultado.getDatos();

            for (Bus bus : buses) {
                Object[] fila = {
                    bus.getPlaca(),
                    bus.getCodigoSocioFk(),
                    bus.getMarca(),
                    bus.getModelo(),
                    bus.getAnioFabricacion(),
                    bus.getCapacidadPasajeros(),
                    bus.getBaseAsignada(),
                    bus.getEstado()
                };
                modeloTabla.addRow(fila);
            }

            JOptionPane.showMessageDialog(this,
                    "Listado de buses generado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * ✅ MENÚ ACCIONES CON COLORES E ICONOS
     * Se despliega ARRIBA del botón Acciones
     */
    private void mostrarMenuAcciones(JButton btnAcciones) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una unidad de la tabla.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String placa = modeloTabla.getValueAt(row, 0).toString();

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(BG_CARD);
        menu.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));

        // ✅ Botones con colores e iconos
        JMenuItem activar = crearMenuItemConColor("Activar", BTN_ACTIVAR, 
            "/Presentacion/Recursos/icons/check.png");
        activar.addActionListener(e -> cambiarEstado(placa, "ACTIVO"));

        JMenuItem inactivar = crearMenuItemConColor("Inactivar", BTN_INACTIVAR, 
            "/Presentacion/Recursos/icons/close.png");
        inactivar.addActionListener(e -> cambiarEstado(placa, "INACTIVO"));

        JMenuItem mantenimiento = crearMenuItemConColor("Mantenimiento", BTN_MANTENIMIENTO, 
            "/Presentacion/Recursos/icons/settings.png");
        mantenimiento.addActionListener(e -> cambiarEstado(placa, "MANTENIMIENTO"));

        JMenuItem exportar = crearMenuItemConColor("Exportar listado", BTN_EXPORTAR, 
            "/Presentacion/Recursos/icons/export.png");
        exportar.addActionListener(e -> exportarListadoActual());

        menu.add(activar);
        menu.add(inactivar);
        menu.add(mantenimiento);
        menu.addSeparator();
        menu.add(exportar);

        // ✅ Mostrar el menú ARRIBA del botón
        // Calcular la altura del menú para mostrarlo arriba
        int menuHeight = menu.getPreferredSize().height;
        menu.show(btnAcciones, 0, -menuHeight);
    }

    private void cambiarEstado(String placa, String estado) {
        ResultadoOperacion resultado;

        if ("ACTIVO".equals(estado)) {
            resultado = busService.activarBus(placa);
        } else if ("INACTIVO".equals(estado)) {
            resultado = busService.desactivarBus(placa);
        } else {
            resultado = busService.mantenimientoBus(placa);
        }

        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarBuses();
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarListadoActual() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Buses_Export.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String ruta = chooser.getSelectedFile().getAbsolutePath();

        String estado = (String) cmbFiltroEstado.getSelectedItem();
        String base = txtFiltroBase.getText().trim();

        String filtroEstado = "Todos".equals(estado) ? null : estado;
        String filtroBase = base.isEmpty() ? null : base;

        ResultadoOperacion resultado =
                (filtroEstado == null && filtroBase == null)
                        ? busService.listarTodosBuses()
                        : busService.listarBusesFiltrado(filtroBase, filtroEstado);

        if (!resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            exportarAExcel((List<Bus>) resultado.getDatos(), ruta);
            JOptionPane.showMessageDialog(this,
                    "El archivo de buses se ha exportado correctamente.",  // ✅ Mensaje exacto del requisito
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarAExcel(List<Bus> buses, String ruta) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Buses");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // ✅ COLUMNAS SEGÚN REQUISITOS
        String[] columnas = {
            "Placa", "Código del Socio Propietario", "Marca", "Modelo",
            "Año de Fabricación", "Capacidad de Pasajeros", "Base Asignada", "Estado"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Bus bus : buses) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bus.getPlaca());
            row.createCell(1).setCellValue(bus.getCodigoSocioFk());
            row.createCell(2).setCellValue(bus.getMarca());
            row.createCell(3).setCellValue(bus.getModelo());
            row.createCell(4).setCellValue(bus.getAnioFabricacion());
            row.createCell(5).setCellValue(bus.getCapacidadPasajeros());
            row.createCell(6).setCellValue(bus.getBaseAsignada());
            row.createCell(7).setCellValue(bus.getEstado());
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream out = new FileOutputStream(ruta)) {
            workbook.write(out);
        }
        workbook.close();
    }

    /**
     * ✅ Crear botón simple con color
     */
    private JButton crearBoton(String texto, Color color, String iconPath) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(120, 32));
        return boton;
    }

    /**
     * ✅ Crear botón con icono
     */
    private JButton crearBotonConIcono(String texto, Color color, String iconPath) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(140, 32));

        // Cargar icono
        ImageIcon icono = cargarIcono(iconPath, 16, 16);
        if (icono != null) {
            boton.setIcon(icono);
            boton.setHorizontalTextPosition(SwingConstants.RIGHT);
            boton.setIconTextGap(8);
        }

        return boton;
    }

    /**
     * ✅ Crear menu item con color e icono
     */
    private JMenuItem crearMenuItemConColor(String texto, Color color, String iconPath) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setForeground(Color.WHITE);
        item.setBackground(color);
        item.setOpaque(true);
        item.setBorderPainted(false);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Cargar icono
        ImageIcon icono = cargarIcono(iconPath, 16, 16);
        if (icono != null) {
            item.setIcon(icono);
        }

        // Hover effect
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(originalColor);
            }
        });

        return item;
    }

    /**
     * ✅ Cargar icono desde recursos
     */
    private ImageIcon cargarIcono(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar icono: " + path);
        }
        return null;
    }
}