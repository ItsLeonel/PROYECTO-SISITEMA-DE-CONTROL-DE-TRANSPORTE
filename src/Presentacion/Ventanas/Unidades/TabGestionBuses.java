package Presentacion.Ventanas.Unidades;

import Logica.Entidades.Base;
import Logica.Entidades.Bus;
import Logica.Servicios.AuditoriaService;
import Logica.Servicios.BaseService;
import Logica.Servicios.BusService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.LEFT;

/**
 * Pestaña 1: Gestión de Buses
 * - Tabla principal con todos los buses
 * - Filtros por base y estado
 * - Acciones contextuales por fila (menú desplegable)
 * - Botón principal: Registrar Bus
 */
public class TabGestionBuses extends JPanel {

    // ===== SERVICIOS =====
    private final BusService busService = new BusService();
    private final BaseService baseService = new BaseService();

    // ===== UI =====
    private DefaultTableModel model;
    private JTable tabla;
    private JComboBox<String> cbBase;
    private JComboBox<String> cbEstado;
    private JCheckBox chkDisponibles;

    // ===== COLORES =====
    private final Color BG_MAIN   = new Color(11, 22, 38);
    private final Color BG_PANEL  = new Color(18, 36, 64);
    private final Color BG_CARD   = new Color(21, 44, 82);
    private final Color BTN_MAIN  = new Color(33, 90, 190);
    private final Color BTN_SEC   = new Color(25, 65, 140);
    private final Color TXT_SEC   = new Color(190, 200, 215);
    private final Color BORDER    = new Color(45, 80, 130);
    private final Color ESTADO_ACTIVO = new Color(46, 204, 113);
    private final Color ESTADO_DESACTIVADO = new Color(231, 76, 60);
    private final Color ESTADO_MANT = new Color(241, 196, 15);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public TabGestionBuses() {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);

        // Cargar datos iniciales
        try {
            refrescarTabla(busService.listar(null, null));
        } catch (Exception e) {
            refrescarTabla(new ArrayList<>());
        }
    }

    // =====================================================
    // BARRA SUPERIOR (Filtros + Botones)
    // =====================================================
    private JPanel crearBarraSuperior() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        // ===== FILTROS (IZQUIERDA) =====
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.setBackground(BG_PANEL);
        filtros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        cbBase = new JComboBox<>();
        cbBase.setBackground(BG_CARD);
        cbBase.setForeground(Color.WHITE);
        cbBase.addItem("Todas");
        cargarBasesEnFiltro();

        cbEstado = new JComboBox<>(new String[]{"Todos", "ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cbEstado.setBackground(BG_CARD);
        cbEstado.setForeground(Color.WHITE);

        chkDisponibles = new JCheckBox("Solo disponibles");
        chkDisponibles.setForeground(Color.WHITE);
        chkDisponibles.setOpaque(false);
        chkDisponibles.setFocusPainted(false);

        JButton btnBuscar = boton("Buscar");
        btnBuscar.addActionListener(e -> aplicarFiltros());

        filtros.add(label("Base:"));
        filtros.add(cbBase);
        filtros.add(label("Estado:"));
        filtros.add(cbEstado);
        filtros.add(chkDisponibles);
        filtros.add(btnBuscar);

        // ===== BOTONES PRINCIPALES (DERECHA) =====
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panelBotones.setOpaque(false);

       

        JButton btnAcciones = crearBotonAcciones();

       
        panelBotones.add(btnAcciones);

        panel.add(filtros, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.EAST);

        return panel;
    }

    private JButton crearBotonAcciones() {
        JButton btn = new JButton("Acciones");
        
        URL url = getClass().getResource("/Presentacion/Recursos/icons/tools.png");
        if (url != null) {
            btn.setIcon(new ImageIcon(url));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(10);
        }
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(BTN_SEC);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        
        btn.addActionListener(e -> mostrarMenuAccionesGlobal(btn));
        
        return btn;
    }

    private void mostrarMenuAccionesGlobal(JButton boton) {
        // Verificar que haya una fila seleccionada
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecciona un bus de la tabla primero.",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = model.getValueAt(row, 0).toString();

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(BG_PANEL);
        menu.setBorder(BorderFactory.createLineBorder(BORDER));

        // Menú con colores e iconos
        JMenuItem itemBase = crearMenuItemConIconoYColor("Cambiar base", "map.png", new Color(52, 152, 219));
        JMenuItem itemAct = crearMenuItemConIconoYColor("Activar", "power_on.png", new Color(46, 204, 113));
        JMenuItem itemDes = crearMenuItemConIconoYColor("Desactivar", "power_off.png", new Color(231, 76, 60));
        JMenuItem itemMant = crearMenuItemConIconoYColor("Mantenimiento", "tools.png", new Color(241, 196, 15));

        itemBase.addActionListener(e -> actualizarBase(codigo));
        itemAct.addActionListener(e -> cambiarEstado(codigo, "ACTIVO"));
        itemDes.addActionListener(e -> cambiarEstado(codigo, "INACTIVO"));
        itemMant.addActionListener(e -> cambiarEstado(codigo, "MANTENIMIENTO"));

        menu.add(itemBase);
        menu.addSeparator();
        menu.add(itemAct);
        menu.add(itemDes);
        menu.add(itemMant);

        menu.show(boton, 0, boton.getHeight());
    }

    private JMenuItem crearMenuItemConIconoYColor(String texto, String nombreIcono, Color color) {
        JMenuItem item = new JMenuItem(texto);
        
        // Intentar cargar el icono
        URL url = getClass().getResource("/Presentacion/Recursos/icons/" + nombreIcono);
        if (url != null) {
            ImageIcon icono = new ImageIcon(url);
            java.awt.Image img = icono.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            item.setIcon(new ImageIcon(img));
        }
        
        item.setBackground(BG_PANEL);
        item.setForeground(color); // Color distintivo
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return item;
    }

    // =====================================================
    // CUERPO (Tabla)
    // =====================================================
    private JPanel crearCuerpo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // ===== TABLA =====
        model = new DefaultTableModel(
            new String[]{"Código", "Placa", "Base", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false; // Toda la tabla no editable
            }
        };

        tabla = new JTable(model);
        tabla.setRowHeight(38);
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(BG_CARD);
        tabla.setGridColor(BORDER);
        tabla.setSelectionBackground(BTN_MAIN);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFocusable(false);

        // Renderizador por defecto
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBackground(BG_CARD);
        cellRenderer.setForeground(Color.WHITE);
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        for (int i = 0; i < 4; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Columna Estado (colores)
        tabla.getColumnModel().getColumn(3).setCellRenderer(new EstadoCellRenderer());

        // Header
        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

                JLabel lbl = new JLabel(value.toString());
                lbl.setOpaque(true);
                lbl.setHorizontalAlignment(LEFT);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setBackground(new Color(20, 50, 100));
                lbl.setForeground(Color.WHITE);
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(BG_CARD);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =====================================================
    // RENDERIZADOR DE ESTADO (con colores)
    // =====================================================
    private class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

            JLabel lbl = new JLabel(value.toString());
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(Color.WHITE);

            switch (value.toString()) {
                case "ACTIVO":
                    lbl.setBackground(ESTADO_ACTIVO);
                    break;
                case "INACTIVO":
                    lbl.setBackground(ESTADO_DESACTIVADO);
                    break;
                case "MANTENIMIENTO":
                    lbl.setBackground(ESTADO_MANT);
                    lbl.setForeground(Color.BLACK);
                    break;
                default:
                    lbl.setBackground(BG_CARD);
            }

            if (isSelected) {
                lbl.setBackground(table.getSelectionBackground());
                lbl.setForeground(table.getSelectionForeground());
            }

            lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return lbl;
        }
    }

    // =====================================================
    // LÓGICA DE NEGOCIO (TU CÓDIGO ORIGINAL)
    // =====================================================
    
    private void registrarBus() {
        DialogBus d = new DialogBus(this, "Registrar bus", null);
        d.setVisible(true);

        Bus nuevo = d.getResult();
        if (nuevo == null) return;

        try {
            busService.registrar(nuevo);
            AuditoriaService.registrar("Unidades", "REGISTRAR_BUS", "OK", "Código=" + nuevo.getCodigo());
            refrescarTablaSilencioso();
            JOptionPane.showMessageDialog(this,
                "Unidad registrada correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            AuditoriaService.registrar("Unidades", "REGISTRAR_BUS", "ERROR", e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltros() {
        String base = cbBase.getSelectedItem().equals("Todas") ? null : cbBase.getSelectedItem().toString();
        String estado = cbEstado.getSelectedItem().equals("Todos") ? null : cbEstado.getSelectedItem().toString();

        try {
            List<Bus> lista = chkDisponibles.isSelected()
                ? busService.listarDisponibles(base)
                : busService.listar(base, estado);

            refrescarTabla(lista);

            String mensaje = chkDisponibles.isSelected()
                ? "Listado de buses disponibles generado correctamente."
                : "Listado de buses generado correctamente.";

            JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            refrescarTabla(new ArrayList<>());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarBase(String codigo) {
        try {
            List<Base> bases = baseService.listarActivas();
            if (bases == null || bases.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay bases operativas activas configuradas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPanel panel = new JPanel(new GridLayout(3, 1, 6, 6));
            JLabel lblCodigo = new JLabel("Código del bus: " + codigo);
            JComboBox<String> cbBases = new JComboBox<>(
                bases.stream().map(Base::getNombre).toArray(String[]::new)
            );

            panel.add(lblCodigo);
            panel.add(new JLabel("Nueva base asignada:"));
            panel.add(cbBases);

            int opt = JOptionPane.showConfirmDialog(this, panel, "Actualizar base asignada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opt != JOptionPane.OK_OPTION) return;

            String nuevaBase = cbBases.getSelectedItem().toString();
            busService.actualizarBase(codigo, nuevaBase);
            refrescarTablaSilencioso();

            JOptionPane.showMessageDialog(this,
                "Base asignada del bus actualizada correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstado(String codigo, String estado) {
        try {
            String mensaje = "";

            if ("ACTIVO".equals(estado)) {
                busService.activar(codigo);
                mensaje = "Unidad activada correctamente.";
            } else if ("INACTIVO".equals(estado)) {
                busService.desactivar(codigo);
                mensaje = "Bus desactivado correctamente.";
            } else if ("MANTENIMIENTO".equals(estado)) {
                busService.mantenimiento(codigo);
                mensaje = "Bus enviado a mantenimiento correctamente.";
            }

            refrescarTablaSilencioso();
            JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // UTILS
    // =====================================================
    
    public void refrescarTabla() {
        refrescarTablaSilencioso();
    }

    private void refrescarTablaSilencioso() {
        String base = cbBase.getSelectedItem().equals("Todas") ? null : cbBase.getSelectedItem().toString();
        String estado = cbEstado.getSelectedItem().equals("Todos") ? null : cbEstado.getSelectedItem().toString();

        try {
            List<Bus> lista = chkDisponibles.isSelected()
                ? busService.listarDisponibles(base)
                : busService.listar(base, estado);
            refrescarTabla(lista);
        } catch (Exception e) {
            refrescarTabla(new ArrayList<>());
        }
    }

    private void refrescarTabla(List<Bus> buses) {
        model.setRowCount(0);
        for (Bus b : buses) {
            model.addRow(new Object[]{
                b.getCodigo(),
                b.getPlaca(),
                b.getBase(),
                b.getEstado()
            });
        }
    }

    private void cargarBasesEnFiltro() {
        try {
            List<String> bases = baseService.listarNombres();
            for (String base : bases) {
                cbBase.addItem(base);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar bases operativas:\n" + e.getMessage(),
                "Error",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // =====================================================
    // UI HELPERS
    // =====================================================
    
    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(TXT_SEC);
        return l;
    }

    private JButton boton(String txt) {
        JButton b = new JButton(txt);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(BTN_MAIN);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return b;
    }

    private JButton botonPrimario(String txt) {
        JButton b = boton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return b;
    }

    private JButton botonConIcono(String texto, String nombreIcono) {
        JButton b = new JButton(texto);
        
        // Intentar cargar el icono
        URL url = getClass().getResource("/Presentacion/Recursos/icons/" + nombreIcono);
        if (url != null) {
            b.setIcon(new ImageIcon(url));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setIconTextGap(10);
        }
        
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(BTN_MAIN);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        
        return b;
    }
}