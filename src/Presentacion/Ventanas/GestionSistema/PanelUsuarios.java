package Presentacion.Ventanas.GestionSistema;

import Logica.DAO.RolDAO;
import Logica.DAO.UsuarioDAO;
import Logica.Servicios.GestionSistemaService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelUsuarios extends JPanel {

    // ================= COLORES =================
    private static final Color AZUL_FONDO = new Color(12, 32, 64);
    private static final Color AZUL_PANEL = new Color(18, 45, 90);
    private static final Color AZUL_BOTON = new Color(32, 80, 200);
    private static final Color AZUL_ACCION = new Color(15, 32, 58);
    private static final Color AZUL_CARD = new Color(18, 45, 90);
    private static final Color BORDE_ACCION = new Color(60, 90, 140);

    // ================= LOGICA =================
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final GestionSistemaService gs = new GestionSistemaService();

    // ================= UI =================
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtLogin;
    private JComboBox<String> cbRol;

    public PanelUsuarios() {
        setLayout(new BorderLayout(12, 12));
        setBackground(AZUL_FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel izquierda = new JPanel(new BorderLayout(12, 12));
        izquierda.setOpaque(false);
        izquierda.add(panelConsulta(), BorderLayout.NORTH);
        izquierda.add(panelTabla(), BorderLayout.CENTER);

        add(izquierda, BorderLayout.CENTER);
        add(panelAcciones(), BorderLayout.EAST);
    }

    // =====================================================
    // CONSULTA
    // =====================================================
    private JPanel panelConsulta() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AZUL_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel titulo = new JLabel("Consulta de usuarios");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 0));

        wrapper.add(titulo, BorderLayout.NORTH);
        wrapper.add(card, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtLogin = new JTextField(14);
        cbRol = new JComboBox<>();
        cargarRoles();

        JButton bLogin = botonAzul("Consultar Usuario");
        JButton bRol = botonAzul("Consultar por Rol");
        JButton bAll = botonAzul("Listar Usuarios");
        JButton bRolesUsuario = botonAzul("Ver roles del usuario");

        // Fila 1: usuario
        c.gridx = 0; c.gridy = 0;
        card.add(labelBlanco("Nombre de usuario:"), c);
        c.gridx = 1;
        card.add(txtLogin, c);
        c.gridx = 2;
        card.add(bLogin, c);

        // Fila 2: rol
        c.gridx = 0; c.gridy = 1;
        card.add(labelBlanco("Rol:"), c);
        c.gridx = 1;
        card.add(cbRol, c);
        c.gridx = 2;
        card.add(bRol, c);

        // Fila 3: ver todos
        c.gridx = 2; c.gridy = 2;
        card.add(bAll, c);
        
        // Fila 4: ver roles del usuario
        c.gridy = 3;
        card.add(bRolesUsuario, c);

        // ===== EVENTOS =====
        bAll.addActionListener(e -> verTodos());
        bLogin.addActionListener(e -> consultarPorUsuario());
        bRol.addActionListener(e -> consultarPorRol());
        bRolesUsuario.addActionListener(e -> verRolesDelUsuario());

        return wrapper;
    }

    // =====================================================
    // TABLA
    // =====================================================
    private JPanel panelTabla() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);

        JLabel titulo = new JLabel("Listado de usuarios");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // ✅ CORRECCIÓN: Solo 4 columnas - Usuario, Correo, Rol, Estado
        modelo = new DefaultTableModel(
                new String[]{"Usuario", "Correo", "Rol", "Estado"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        estilizarTabla(tabla);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 90, 140), 1));
        sp.getViewport().setBackground(new Color(15, 32, 58));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(15, 32, 58));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 90, 140), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        card.add(sp, BorderLayout.CENTER);

        contenedor.add(titulo, BorderLayout.NORTH);
        contenedor.add(card, BorderLayout.CENTER);

        cargarTabla(usuarioDAO.listarParaTabla());

        return contenedor;
    }

    private void estilizarTabla(JTable tabla) {
        Color fondo = new Color(15, 32, 58);
        Color borde = new Color(60, 90, 140);
        Color seleccion = new Color(45, 90, 160);

        tabla.setBackground(fondo);
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(borde);
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);
        tabla.setSelectionBackground(seleccion);
        tabla.setSelectionForeground(Color.WHITE);

        JTableHeader header = tabla.getTableHeader();
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setOpaque(true);
        headerRenderer.setBackground(new Color(18, 45, 90));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBorder(BorderFactory.createLineBorder(borde, 1));

        for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(Color.WHITE);
                setBackground(isSelected ? seleccion : fondo);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        };

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(cell);
        }
    }

    // =====================================================
    // ACCIONES
    // =====================================================
    private JPanel panelAcciones() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(AZUL_PANEL);
        contenedor.setPreferredSize(new Dimension(300, 9999));

        contenedor.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Acciones",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        ));

        JPanel grid = new JPanel(new GridLayout(3, 2, 18, 18));
        grid.setBackground(AZUL_PANEL);
        grid.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        grid.add(itemAccion("Registrar usuario", "/Presentacion/Recursos/icons/add.png", this::registrarUsuario));
        grid.add(itemAccion("Actualizar correo electrónico", "/Presentacion/Recursos/icons/edit.png", this::actualizarCorreo));
        grid.add(itemAccion("Activar Desactivar", "/Presentacion/Recursos/icons/power.png", this::cambiarEstado));
        grid.add(itemAccion("Asignar rol", "/Presentacion/Recursos/icons/rolis.png", this::asignarRol));
        grid.add(itemAccion("Restablecer Contraseña", "/Presentacion/Recursos/icons/reset.png", this::restablecerContrasena));

        contenedor.add(grid, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel itemAccion(String texto, String iconPath, Runnable accion) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AZUL_ACCION);
        card.setPreferredSize(new Dimension(140, 105));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_ACCION, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String htmlText = "<html><center>" + texto.replace(" ", "<br>") + "</center></html>";

        JButton btn = new JButton(htmlText);

        ImageIcon icon = icono(iconPath, 32, 32);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setIconTextGap(6);
        }

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(AZUL_ACCION);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.addActionListener(e -> accion.run());

        card.add(btn, BorderLayout.CENTER);
        return card;
    }

    // =====================================================
    // LOGICA DE CONSULTAS
    // =====================================================
    private void verTodos() {
        List<UsuarioDAO.UsuarioTablaRow> data = usuarioDAO.listarParaTabla();
        
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen usuarios registrados en el sistema.");
            modelo.setRowCount(0);
            return;
        }
        
        cargarTabla(data);
    }

    private void consultarPorUsuario() {
        String login = txtLogin.getText().trim();

        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró un usuario registrado con el nombre de usuario ingresado.");
            return;
        }

        var fila = usuarioDAO.listarParaTabla().stream()
                .filter(u -> u.login.equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);

        if (fila == null) {
            JOptionPane.showMessageDialog(this, "No se encontró un usuario registrado con el nombre de usuario ingresado.");
            modelo.setRowCount(0);
            return;
        }

        cargarTabla(List.of(fila));
    }

    private void consultarPorRol() {
        String rol = (String) cbRol.getSelectedItem();

        if (rol == null || rol.isBlank()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.");
            return;
        }

        List<UsuarioDAO.UsuarioTablaRow> data = usuarioDAO.listarParaTablaPorRol(rol);

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen usuarios asociados al rol seleccionado.");
            modelo.setRowCount(0);
            return;
        }

        cargarTabla(data);
    }

    private void verRolesDelUsuario() {
        String login = txtLogin.getText().trim();

        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudo realizar la consulta: el usuario no existe.");
            return;
        }

        var fila = usuarioDAO.listarParaTabla().stream()
                .filter(u -> u.login.equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);

        if (fila == null) {
            JOptionPane.showMessageDialog(this, "No se pudo realizar la consulta: el usuario no existe.");
            return;
        }

        modelo.setRowCount(0);

        if (fila.roles == null || fila.roles.isBlank()) {
            JOptionPane.showMessageDialog(this, "No existen roles asociados al usuario seleccionado.");
            return;
        }

        // ✅ CORRECCIÓN: Solo 4 columnas
        modelo.addRow(new Object[]{
                fila.login,  // Usuario
                fila.correo,
                fila.roles,
                fila.activo ? "ACTIVO" : "INACTIVO"
        });

        JOptionPane.showMessageDialog(this, "Consulta de roles del usuario realizada correctamente.");
    }

    // =====================================================
    // LOGICA DE ACCIONES
    // =====================================================
    private void registrarUsuario() {
        JTextField user = new JTextField();
        JTextField mail = new JTextField();

        JComboBox<String> cbRoles = new JComboBox<>();
        for (String r : rolDAO.listarNombresRoles()) cbRoles.addItem(r);

        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.add(new JLabel("Nombre de usuario:")); p.add(user);
        p.add(new JLabel("Correo electrónico:")); p.add(mail);
        p.add(new JLabel("Rol inicial:")); p.add(cbRoles);
        p.add(new JLabel("Estado:")); p.add(cbEstado);

        if (JOptionPane.showConfirmDialog(this, p, "Registrar usuario",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String msg = gs.registrarUsuario(
                user.getText(),
                mail.getText(),
                (String) cbRoles.getSelectedItem(),
                (String) cbEstado.getSelectedItem()
        );

        JOptionPane.showMessageDialog(this, msg);
        cargarTabla(usuarioDAO.listarParaTabla());
    }

    private void actualizarCorreo() {
        String login = obtenerLoginSeleccionado();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "No se puede actualizar el correo electrónico: el usuario no se encuentra registrado");
            return;
        }

        JTextField correo = new JTextField();
        JPanel p = new JPanel(new GridLayout(1, 2));
        p.add(new JLabel("Nuevo correo:"));
        p.add(correo);

        if (JOptionPane.showConfirmDialog(this, p, "Actualizar correo",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String msg = gs.actualizarCorreo(login, correo.getText());
        JOptionPane.showMessageDialog(this, msg);
        cargarTabla(usuarioDAO.listarParaTabla());
    }

    private void cambiarEstado() {
        String login = obtenerLoginSeleccionado();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la tabla.");
            return;
        }

        String msg;
        if (usuarioDAO.estaActivoPorLogin(login)) {
            msg = gs.desactivarUsuario(login);
        } else {
            msg = gs.activarUsuario(login);
        }

        JOptionPane.showMessageDialog(this, msg);
        cargarTabla(usuarioDAO.listarParaTabla());
    }

    private void asignarRol() {
        // ✅ CORRECCIÓN: Necesitamos obtener el ID del usuario desde el login
        String login = obtenerLoginSeleccionado();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la tabla.");
            return;
        }

        // Buscar el ID del usuario por su login
        var usuario = usuarioDAO.listarParaTabla().stream()
                .filter(u -> u.login.equals(login))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "No se pudo encontrar el usuario seleccionado.");
            return;
        }

        JComboBox<String> cbRoles = new JComboBox<>();
        for (String r : rolDAO.listarNombresRoles()) {
            cbRoles.addItem(r);
        }

        if (cbRoles.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No existen roles disponibles para asignar.");
            return;
        }

        JPanel p = new JPanel(new GridLayout(1, 2, 10, 10));
        p.add(new JLabel("Rol:"));
        p.add(cbRoles);

        int ok = JOptionPane.showConfirmDialog(this, p, "Asignar rol al usuario", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String rolSeleccionado = (String) cbRoles.getSelectedItem();
        if (rolSeleccionado == null || rolSeleccionado.isBlank()) return;

        try {
            gs.asignarRolAUsuario(usuario.id, rolSeleccionado);
            cargarTabla(usuarioDAO.listarParaTabla());
            JOptionPane.showMessageDialog(this, "Rol asignado al usuario correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void restablecerContrasena() {
        String login = obtenerLoginSeleccionado();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la tabla.");
            return;
        }

        String msg = gs.restablecerContrasena(login);
        JOptionPane.showMessageDialog(this, msg);
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private void cargarTabla(List<UsuarioDAO.UsuarioTablaRow> data) {
        modelo.setRowCount(0);
        for (UsuarioDAO.UsuarioTablaRow u : data) {
            // ✅ CORRECCIÓN: Solo 4 columnas - Usuario, Correo, Rol, Estado
            modelo.addRow(new Object[]{
                    u.login,  // Usuario
                    u.correo,
                    (u.roles == null || u.roles.isBlank()) ? "SIN ROL" : u.roles,
                    u.activo ? "ACTIVO" : "INACTIVO"
            });
        }
    }

    private void cargarRoles() {
        cbRol.removeAllItems();
        for (String r : rolDAO.listarNombresRoles()) cbRol.addItem(r);
    }

    private String obtenerLoginSeleccionado() {
        int row = tabla.getSelectedRow();
        // ✅ CORRECCIÓN: Ahora "Usuario" (login) está en la columna 0
        if (row >= 0) return tabla.getValueAt(row, 0).toString();
        return null;
    }

    private JLabel labelBlanco(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JButton botonAzul(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(AZUL_BOTON);
        b.setForeground(Color.WHITE);
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
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}