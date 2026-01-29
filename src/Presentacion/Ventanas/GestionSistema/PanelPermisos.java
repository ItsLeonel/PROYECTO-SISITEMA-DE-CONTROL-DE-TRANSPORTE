package Presentacion.Ventanas.GestionSistema;

import Logica.Servicios.GestionSistemaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class PanelPermisos extends JPanel {

    // ================= COLORES =================
    private static final Color AZUL_FONDO  = new Color(12, 32, 64);
    private static final Color AZUL_PANEL  = new Color(18, 45, 90);
    private static final Color AZUL_ACCION = new Color(15, 32, 58);
    private static final Color AZUL_TABLA  = new Color(18, 45, 90);

    private final GestionSistemaService gs = new GestionSistemaService();

    private JComboBox<String> cbRol;
    private JComboBox<String> cbPermiso;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelPermisos() {
        setLayout(new BorderLayout(14, 14));
        setBackground(AZUL_FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(titulo(), BorderLayout.NORTH);
        add(panelSuperior(), BorderLayout.CENTER);
        add(panelTabla(), BorderLayout.SOUTH);
    }

    // =====================================================
    // TITULO
    // =====================================================
    private JPanel titulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel t = new JLabel("Gestión de Permisos");
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(Color.WHITE);

        p.add(t, BorderLayout.WEST);
        return p;
    }
    public static class RolPermisoRow {
    public final String rol;
    public final String permiso;

    public RolPermisoRow(String rol, String permiso) {
        this.rol = rol;
        this.permiso = permiso;
    }
}


    // =====================================================
    // PANEL SUPERIOR
    // =====================================================
private JPanel panelSuperior() {
    JPanel p = new JPanel(new GridBagLayout());
    p.setBackground(AZUL_PANEL);
    p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
    ));

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(8, 8, 8, 8);
    c.fill = GridBagConstraints.HORIZONTAL;

    cbRol = new JComboBox<>();
    cbPermiso = new JComboBox<>();

    cargarRoles();
    cargarPermisos();

    JButton btnAsignar   = botonAccion("Asignar permiso");
    JButton btnRevocar   = botonAccion("Revocar permiso");
    JButton btnVer       = botonAccion("Ver permisos");
    JButton btnVerTodos  = botonAccion("Ver todos los permisos"); // 🔥 NUEVO

    btnAsignar.addActionListener(e -> asignarPermiso());
    btnRevocar.addActionListener(e -> revocarPermiso());
    btnVer.addActionListener(e -> verPermisos());
    btnVerTodos.addActionListener(e -> verTodosLosPermisos()); // 🔥 NUEVO

    // ===== FILA 0 =====
    c.gridx = 0; c.gridy = 0;
    p.add(label("Rol:"), c);
    c.gridx = 1;
    p.add(cbRol, c);

    // ===== FILA 1 =====
    c.gridx = 0; c.gridy = 1;
    p.add(label("Permiso:"), c);
    c.gridx = 1;
    p.add(cbPermiso, c);

    // ===== BOTONES =====
    c.gridx = 2; c.gridy = 0;
    p.add(btnAsignar, c);

    c.gridy = 1;
    p.add(btnRevocar, c);

    c.gridy = 2;
    p.add(btnVer, c);

    c.gridy = 3; // 🔥 NUEVA FILA
    p.add(btnVerTodos, c);

    return p;
}
private void verTodosLosPermisos() {

    modelo.setRowCount(0);

    var data = gs.listarPermisosPorRoles();

    if (data.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No existen permisos asociados a los roles.");
        return;
    }

    for (var rp : data) {
        modelo.addRow(new Object[]{
            rp.rol,
            rp.permiso
        });
    }

    JOptionPane.showMessageDialog(this,
        "Consulta de permisos realizada correctamente.");
}



    // =====================================================
    // TABLA
    // =====================================================
    private JScrollPane panelTabla() {

        modelo = new DefaultTableModel(
                new String[]{"Rol", "Permiso"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);

        // 🔥 ESTILO UNICO Y CORRECTO
        estilizarTabla(tabla);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        sp.getViewport().setBackground(AZUL_TABLA);
        sp.getViewport().setOpaque(true);
        sp.setOpaque(false);
        sp.setPreferredSize(new Dimension(0, 230));

        return sp;
    }

    // =====================================================
    // ESTILO TABLA (CLAVE)
    // =====================================================
    private void estilizarTabla(JTable tabla) {

        tabla.setRowHeight(28);
        tabla.setFillsViewportHeight(true);
 tabla.setShowGrid(true);
tabla.setGridColor(new Color(60, 90, 140)); // azul suave
tabla.setIntercellSpacing(new Dimension(1, 1));

        tabla.setBackground(AZUL_TABLA);
        tabla.setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(45, 90, 160));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = tabla.getTableHeader();

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setOpaque(true); // 🔥 evita blanco
        headerRenderer.setBackground(AZUL_ACCION);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerRenderer.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel()
                 .getColumn(i)
                 .setHeaderRenderer(headerRenderer);
        }

        header.setReorderingAllowed(false);
    }

    // =====================================================
    // LOGICA
    // =====================================================
    private void cargarRoles() {
        cbRol.removeAllItems();
        for (String r : gs.listarNombresRoles()) {
            cbRol.addItem(r);
        }
    }

    private void cargarPermisos() {
        cbPermiso.removeAllItems();
        for (String p : gs.listarCodigosPermisosActivos()) {
            cbPermiso.addItem(p);
        }
    }

    private void asignarPermiso() {
        String rol = (String) cbRol.getSelectedItem();
        String permiso = (String) cbPermiso.getSelectedItem();
        if (rol == null || permiso == null) return;

        try {
            gs.asignarPermisoARol(rol, permiso);
            JOptionPane.showMessageDialog(this, "Permiso asignado al rol correctamente." + //
                                "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void revocarPermiso() {
        String rol = (String) cbRol.getSelectedItem();
        String permiso = (String) cbPermiso.getSelectedItem();
        if (rol == null || permiso == null) return;

        try {
            gs.revocarPermisoARol(rol, permiso);
            JOptionPane.showMessageDialog(this, "Permiso revocado del rol correctamente.\r\n" + //
                                "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

  private void verPermisos() {
    modelo.setRowCount(0);

    String rol = (String) cbRol.getSelectedItem();
    if (rol == null) {
        JOptionPane.showMessageDialog(this,
            "No se pudo realizar la consulta: el rol no existe.");
        return;
    }

    try {
        List<String> permisos = gs.verPermisosDeRol(rol);

        if (permisos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No existen permisos asociados al rol seleccionado.");
            return;
        }

        for (String p : permisos) {
            modelo.addRow(new Object[]{rol, p});
        }

        // ✅ MENSAJE QUE FALTABA
        JOptionPane.showMessageDialog(this,
            "Consulta de permisos realizada correctamente.");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}


    // =====================================================
    // UI HELPERS
    // =====================================================
    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton botonAccion(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(AZUL_ACCION);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(160, 36));
        return b;
    }
}
