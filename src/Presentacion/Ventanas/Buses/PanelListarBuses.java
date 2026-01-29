package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Panel para listar la flota de buses
 * Requisitos: ru21v1.1, ru22v1.1, ru23v1.1, ru24v1.1
 * Muestra información del socio propietario mediante JOIN
 */
public class PanelListarBuses extends JPanel {

    private BusService busService;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltroEstado;
    private JTextField txtFiltroBase;

    // Colores del tema
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color BG_CARD = new Color(21, 44, 82);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);
    private static final Color BORDER_COLOR = new Color(45, 80, 130);
    private static final Color ESTADO_ACTIVO = new Color(46, 204, 113);
    private static final Color ESTADO_INACTIVO = new Color(231, 76, 60);
    private static final Color ESTADO_MANT = new Color(241, 196, 15);

    public PanelListarBuses() {
        this.busService = new BusService();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con controles
        add(construirPanelSuperior(), BorderLayout.NORTH);

        // Tabla
        add(construirPanelTabla(), BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarBuses();
    }

    /**
     * Panel superior con filtros y botones
     */
    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        // ===== FILTROS (IZQUIERDA) =====
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.setBackground(BG_PANEL);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = new JLabel("Listado de Flota");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        filtros.add(lblTitulo);

        // ===== CONTROLES (DERECHA) =====
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panelControles.setOpaque(false);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setForeground(TEXT_SECONDARY);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cmbFiltroEstado.setBackground(BG_CARD);
        cmbFiltroEstado.setForeground(Color.WHITE);

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

        JButton btnBuscar = crearBoton("Buscar");
        btnBuscar.addActionListener(e -> aplicarFiltros());

        JButton btnActualizar = crearBoton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> cargarBuses());

        panelControles.add(lblEstado);
        panelControles.add(cmbFiltroEstado);
        panelControles.add(lblBase);
        panelControles.add(txtFiltroBase);
        panelControles.add(btnBuscar);
        panelControles.add(btnActualizar);

        panel.add(filtros, BorderLayout.WEST);
        panel.add(panelControles, BorderLayout.EAST);

        return panel;
    }

    /**
     * Panel con tabla
     */
    private JScrollPane construirPanelTabla() {
        // Modelo de tabla con columnas incluyendo información del socio
        String[] columnas = {
                "Placa", "Disco", "Modelo", "Año",
                "Socio Dueño", "Teléfono Socio", "Base", "Estado"
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
        tabla.setRowHeight(32);
        tabla.setBackground(BG_CARD);
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(BORDER_COLOR);
        tabla.setSelectionBackground(PRIMARY_COLOR);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(20, 50, 100));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Renderizador para columna de estado (con colores)
        tabla.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
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

        // Ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);   // Placa
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);  // Disco
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);   // Modelo
        tabla.getColumnModel().getColumn(3).setPreferredWidth(50);   // Año
        tabla.getColumnModel().getColumn(4).setPreferredWidth(200);  // Socio Dueño
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);  // Teléfono
        tabla.getColumnModel().getColumn(6).setPreferredWidth(120);  // Base
        tabla.getColumnModel().getColumn(7).setPreferredWidth(100);  // Estado

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(BG_CARD);

        return scrollPane;
    }

    /**
     * Cargar todos los buses
     */
    private void cargarBuses() {
        modeloTabla.setRowCount(0);

        ResultadoOperacion resultado = busService.listarTodosBuses();

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<Bus> buses = (List<Bus>) resultado.getDatos();

            for (Bus bus : buses) {
                Object[] fila = {
                        bus.getPlaca(),
                        bus.getMarca(),
                        bus.getModelo(),
                        bus.getAnioFabricacion(),
                        bus.getNombresPropietario(),      // ← Nombre del socio (JOIN)
                        bus.getTelefonoPropietario(),     // ← Teléfono del socio (JOIN)
                        bus.getBaseAsignada(),
                        bus.getEstado()
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
     * Aplicar filtros
     */
    private void aplicarFiltros() {
        String estado = (String) cmbFiltroEstado.getSelectedItem();
        String base = txtFiltroBase.getText().trim();

        // Si ambos están vacíos, cargar todos
        if ("Todos".equals(estado) && base.isEmpty()) {
            cargarBuses();
            return;
        }

        modeloTabla.setRowCount(0);

        // Preparar filtros
        String filtroEstado = "Todos".equals(estado) ? null : estado;
        String filtroBase = base.isEmpty() ? null : base;

        ResultadoOperacion resultado = busService.listarBusesFiltrado(filtroBase, filtroEstado);

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<Bus> buses = (List<Bus>) resultado.getDatos();

            for (Bus bus : buses) {
                Object[] fila = {
                        bus.getPlaca(),
                        bus.getMarca(),
                        bus.getModelo(),
                        bus.getAnioFabricacion(),
                        bus.getNombresPropietario(),
                        bus.getTelefonoPropietario(),
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
     * Crear botón con estilo
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(PRIMARY_COLOR);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(120, 32));
        return boton;
    }
}