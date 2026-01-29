package Presentacion.Ventanas.GestionSistema;

import Logica.Servicios.GestionSistemaService;
import Logica.DAO.RolDAO;
import javax.swing.table.JTableHeader;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class PanelRoles extends JPanel {

    // ================= COLORES =================
    private static final Color AZUL_FONDO  = new Color(12, 32, 64);
    private static final Color AZUL_PANEL  = new Color(18, 45, 90);
    private static final Color AZUL_ACCION = new Color(15, 32, 58);
    private static final Color VERDE_ACTIVO =  new Color(46, 204, 113);
    
    //    private final Color ESTADO_ACTIVO = new Color(46, 204, 113);      // verde
//private final Color ESTADO_INACTIVO = new Color(231, 76, 60);     // rojo
//private final Color ESTADO_MANT = new Color(241, 196, 15);    

    // ================= LOGICA =================
    private final GestionSistemaService gs = new GestionSistemaService();

    // ================= UI =================
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelRoles() {
        setLayout(new BorderLayout(14, 14));
        setBackground(AZUL_FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(titulo(), BorderLayout.NORTH);
        add(panelTabla(), BorderLayout.CENTER);
        add(panelAcciones(), BorderLayout.EAST);

        cargarTabla();
    }

    // =====================================================
    // TITULO
    // =====================================================
    private JPanel titulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel t = new JLabel("Gestión de Roles");
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(Color.WHITE);

        p.add(t, BorderLayout.WEST);
        return p;
    }

    // =====================================================
    // TABLA
    // =====================================================
    private JScrollPane panelTabla() {
    modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Estado"}, 0
    ) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    tabla = new JTable(modelo);
    estilizarHeader(tabla);
    tabla.setRowHeight(32);
    tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tabla.setForeground(Color.WHITE);

    // 🔥 FONDO AZUL TOTAL
    tabla.setBackground(new Color(16, 40, 80));
    tabla.setSelectionBackground(new Color(40, 90, 160));
    tabla.setGridColor(new Color(40, 80, 140));

    // 🔥 IMPORTANTE: que pinte todo el alto
    tabla.setFillsViewportHeight(true);

    // ===== HEADER SIN RELIEVE =====
    JTableHeader header = tabla.getTableHeader();
    header.setBackground(AZUL_PANEL);
    header.setForeground(Color.WHITE);
    header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    header.setBorder(BorderFactory.createEmptyBorder());
    header.setOpaque(true);

    // ===== SCROLLPANE AZUL =====
    JScrollPane sp = new JScrollPane(tabla);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.getViewport().setBackground(new Color(16, 40, 80));
    sp.setBackground(new Color(16, 40, 80));

    // Scrollbar (opcional pero recomendado)
    sp.getVerticalScrollBar().setUnitIncrement(16);

    // Renderer estado
    tabla.getColumnModel()
            .getColumn(2)
            .setCellRenderer(new EstadoRenderer());

    return sp;
}
private void estilizarHeader(JTable tabla) {
    JTableHeader header = tabla.getTableHeader();

    header.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
        JLabel l = new JLabel(value.toString());
        l.setOpaque(true);
        l.setBackground(new Color(18, 45, 90)); // 🔥 AZUL HEADER
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBorder(BorderFactory.createLineBorder(new Color(40, 80, 140)));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    });

    header.setReorderingAllowed(false);
    header.setResizingAllowed(false);
}



    // =====================================================
    // ACCIONES (CON PNG)
    // =====================================================
  private JPanel panelAcciones() {
    JPanel p = new JPanel(new GridLayout(3, 1, 14, 14));
    p.setBackground(AZUL_PANEL);
    p.setPreferredSize(new Dimension(230, 0));

    p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Acciones",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 12),
            Color.WHITE
    ));

    JButton btnCrear = botonAccion(
            "Crear rol",
            "/Presentacion/Recursos/icons/add.png"
    );
    JButton btnRenombrar = botonAccion(
            "Renombrar Rol",
            "/Presentacion/Recursos/icons/edit.png"
    );
    JButton btnEstado = botonAccion(
            "Activar - Desactivar Rol",
            "/Presentacion/Recursos/icons/power.png"
    );

    btnCrear.addActionListener(e -> crearRol());
    btnRenombrar.addActionListener(e -> renombrarRol());
    btnEstado.addActionListener(e -> cambiarEstado());

    p.add(btnCrear);
    p.add(btnRenombrar);
    p.add(btnEstado);

    return p;
}


    // =====================================================
    // LOGICA
    // =====================================================
    private void cargarTabla() {
        modelo.setRowCount(0);
        List<RolDAO.RolRow> roles = gs.listarRoles();

        for (RolDAO.RolRow r : roles) {
            modelo.addRow(new Object[]{
                    r.id,
                    r.nombre,
                    r.activo ? "ACTIVO" : "INACTIVO"
            });
        }
    }

    private Long getIdSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) return null;
        return Long.parseLong(tabla.getValueAt(row, 0).toString());
    }

  private void crearRol() {

    JTextField txtNombre = new JTextField();
    JComboBox<String> cbEstado = new JComboBox<>(new String[]{
            "Activo",
            "Inactivo"
    });

    JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
    p.add(new JLabel("Nombre del nuevo rol:"));
    p.add(txtNombre);
    p.add(new JLabel("Estado del rol:"));
    p.add(cbEstado);

    int ok = JOptionPane.showConfirmDialog(
            this,
            p,
            "Crear rol",
            JOptionPane.OK_CANCEL_OPTION
    );

    if (ok != JOptionPane.OK_OPTION) return;

    String nombre = txtNombre.getText().trim();
    boolean activo = "Activo".equals(cbEstado.getSelectedItem());

    try {
        gs.crearRol(nombre, activo);

        JOptionPane.showMessageDialog(this,
                "Nombre de rol actualizado correctamente.");

        cargarTabla();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

private void renombrarRol() {

    int row = tabla.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this,
                "No se pudo actualizar el rol: el rol no existe.",
                "Renombrar rol",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 🔹 Nombre actual desde la tabla
    String nombreActual = tabla.getValueAt(row, 1).toString().trim();

    // 🔹 Pedir nuevo nombre
    String nuevoNombre = JOptionPane.showInputDialog(
            this,
            "Nuevo nombre de rol:",
            nombreActual
    );

    // 👉 Cancelar: no hacer nada
    if (nuevoNombre == null) return;

    // 👉 Vacío o solo espacios: mantener el nombre anterior (SIN mensaje)
    if (nuevoNombre.trim().isEmpty()) return;

    // 🔹 Llamada al service (RGS10.2)
    String mensaje = gs.actualizarNombreRol(nombreActual, nuevoNombre.trim());

    // 🔹 Mostrar mensaje devuelto por el service
    JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Resultado",
            JOptionPane.INFORMATION_MESSAGE
    );

    // 🔹 Refrescar tabla
    cargarTabla();
}




   private void cambiarEstado() {

    int row = tabla.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this,
                "No se pudo activar el rol: el rol no existe.");
        return;
    }

    String nombreRol = tabla.getValueAt(row, 1).toString().trim();
    String estado = tabla.getValueAt(row, 2).toString();

    String mensaje;

    if ("INACTIVO".equals(estado)) {
        // 👉 RGS10.3 – ACTIVAR
        mensaje = gs.activarRolPorNombre(nombreRol);
    } else {
        // 👉 RGS10.4 – DESACTIVAR
        mensaje = gs.desactivarRolPorNombre(nombreRol);
    }

    JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Resultado",
            JOptionPane.INFORMATION_MESSAGE
    );

    cargarTabla();
}

    // =====================================================
    // BOTONES + ICONOS
    // =====================================================
    private JButton botonAccion(String texto, String iconPath) {
        JButton b = new JButton(texto);

        ImageIcon icon = icono(iconPath, 26, 26);
        if (icon != null) {
            b.setIcon(icon);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setIconTextGap(14);
        }

        b.setBackground(AZUL_ACCION);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setPreferredSize(new Dimension(200, 42));

        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);

        return b;
    }

    private ImageIcon icono(String ruta, int w, int h) {
        try {
            var url = getClass().getResource(ruta);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // =====================================================
    // RENDERER ESTADO
    // =====================================================
    private class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setForeground(Color.WHITE);
            l.setOpaque(true);

            if ("ACTIVO".equals(value)) {
                l.setBackground(VERDE_ACTIVO);
            } else {
                l.setBackground(new Color(220, 70, 60));
            }
            return l;
        }
    }
}
