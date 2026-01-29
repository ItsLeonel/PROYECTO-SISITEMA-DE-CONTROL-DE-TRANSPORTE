package Presentacion.Ventanas.GestionSistema;
import Presentacion.Ventanas.GestionSistema.PanelRoles;

import Logica.DAO.RolDAO;
import Logica.DAO.RolPermisoDAO;
import Logica.DAO.UsuarioDAO;
import Logica.Servicios.GestionSistemaService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.util.List;
import Presentacion.Ventanas.GestionSistema.PanelUsuarios;

import Presentacion.Ventanas.GestionSistema.PanelPermisos;
import Presentacion.Ventanas.GestionSistema.PanelCambioContrasena;



public class PanelGestionSistema extends JPanel {

    // ================= COLORES =================
    private static final Color AZUL_FONDO = new Color(12, 32, 64);
    private static final Color AZUL_PANEL = new Color(18, 45, 90);
    private static final Color AZUL_BOTON = new Color(32, 80, 200);
    private static final Color AZUL_ACCION = new Color(15, 32, 58);
private static final Color AZUL_CARD = new Color(18, 45, 90);
private static final Color AZUL_TITULO = new Color(220, 230, 255);
private static final Font FUENTE_TITULO =
        new Font("Segoe UI", Font.BOLD, 14);

private static final Color BORDE_ACCION = new Color(60, 90, 140);

private JButton btnUsuarios, btnRoles, btnPermisos, btnClave;


    // ================= LOGICA =================
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final GestionSistemaService gs = new GestionSistemaService();

    // ================= CARD LAYOUT =================
    private CardLayout cardLayout;
    private JPanel panelCards;

    // ================= UI USUARIOS =================
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtId;
    private JComboBox<String> cbRol;

    public PanelGestionSistema() {
        setLayout(new BorderLayout(12, 12));
        setBackground(AZUL_FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(header(), BorderLayout.NORTH);
        add(contenido(), BorderLayout.CENTER);
        add(panelLogo(), BorderLayout.SOUTH);
    }

    // =====================================================
    // HEADER + MENU
    // =====================================================
    private JPanel header() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);

        JLabel t = new JLabel("Gestión del Sistema");
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(Color.WHITE);

        JLabel s = new JLabel("");
        s.setForeground(new Color(200, 210, 230));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.add(t);
        txt.add(s);

        h.add(txt, BorderLayout.WEST);
        h.add(menuSuperior(), BorderLayout.SOUTH);

        return h;
    }
    private String obtenerLoginSeleccionado() {
    int row = tabla.getSelectedRow();
    if (row >= 0) {
        return tabla.getValueAt(row, 3).toString(); // 👈 LOGIN
    }
    return null;
}


    // =====================================================
    // MENU SUPERIOR (CON ICONOS PNG)
    // =====================================================
private JPanel menuSuperior() {
    JPanel p = new JPanel(new GridLayout(1, 4, 12, 0));
    p.setOpaque(false);
    p.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

    JButton btnUsuarios = botonMenu(
            "Usuarios", "/Presentacion/Recursos/icons/usuarios.png");
    JButton btnRoles = botonMenu(
            "Roles", "/Presentacion/Recursos/icons/roles.png");
    JButton btnPermisos = botonMenu(
            "Permisos", "/Presentacion/Recursos/icons/permisos.png");
    JButton btnClave = botonMenu(
            "Cambio de Contraseña", "/Presentacion/Recursos/icons/password.png");

    btnUsuarios.addActionListener(e -> cardLayout.show(panelCards, "USUARIOS"));
    btnRoles.addActionListener(e -> cardLayout.show(panelCards, "ROLES"));
    btnPermisos.addActionListener(e -> cardLayout.show(panelCards, "PERMISOS"));
    btnClave.addActionListener(e -> cardLayout.show(panelCards, "CLAVE"));

    p.add(btnUsuarios);
    p.add(btnRoles);
    p.add(btnPermisos);
    p.add(btnClave);

    return p;
}


    // =====================================================
    // CONTENIDO PRINCIPAL (CARDS)
    // =====================================================
    private JPanel contenido() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setOpaque(false);

        panelCards.add(panelUsuarios(), "USUARIOS");
        panelCards.add(panelRoles(), "ROLES");
        panelCards.add(panelPermisos(), "PERMISOS");
        panelCards.add(new PanelCambioContrasena(), "CLAVE");


        root.add(panelCards, BorderLayout.CENTER);
        cardLayout.show(panelCards, "USUARIOS");

        return root;
    }

    // =====================================================
    // ===== USUARIOS (TU CÓDIGO REAL) =====
    // =====================================================
    private JPanel panelUsuarios() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);

        JPanel izquierda = new JPanel(new BorderLayout(12, 12));
        izquierda.setOpaque(false);

        izquierda.add(panelConsulta(), BorderLayout.NORTH);
        izquierda.add(panelTabla(), BorderLayout.CENTER);

        root.add(izquierda, BorderLayout.CENTER);
        root.add(panelAcciones(), BorderLayout.EAST);

        return root;
    }
 


    // =====================================================
    // ===== PLACEHOLDERS =====
    // =====================================================
    private JPanel panelRoles() {
    return new PanelRoles();
}


private JPanel panelPermisos() {
    return new PanelPermisos();
}

    private JPanel panelCambioClave() {
        return new JLabelPanel("Cambio de Contraseña");
    }

    // =====================================================
    // CONSULTA
    // =====================================================
   private JPanel panelConsulta() {

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);

    // ===== BORDE BLANCO EXTERNO =====
    JPanel card = new JPanel(new GridBagLayout());
    card.setBackground(new Color(18, 45, 90));
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
    ));

    // ===== TÍTULO =====
    JLabel titulo = new JLabel("Consulta de usuarios");
    titulo.setForeground(Color.WHITE);
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
    titulo.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 0));

    wrapper.add(titulo, BorderLayout.NORTH);
    wrapper.add(card, BorderLayout.CENTER);

    // ===== CONTENIDO =====
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(8, 8, 8, 8);
    c.fill = GridBagConstraints.HORIZONTAL;

    txtId = new JTextField(14); // aquí se ingresa el LOGIN
    cbRol = new JComboBox<>();
    cargarRoles();

    JButton bId = botonAzul("Consultar por Usuario");
    JButton bRol = botonAzul("Consultar por Rol");
    JButton bAll = botonAzul("Ver todos");
    JButton bRolesUsuario = botonAzul("Ver roles del usuario");

    // ---- fila 1: usuario
    c.gridx = 0; c.gridy = 0;
    card.add(labelBlanco("Nombre de usuario:"), c);

    c.gridx = 1;
    card.add(txtId, c);

    c.gridx = 2;
    card.add(bId, c);

    // ---- fila 2: rol
    c.gridx = 0; c.gridy = 1;
    card.add(labelBlanco("Rol:"), c);

    c.gridx = 1;
    card.add(cbRol, c);

    c.gridx = 2;
    card.add(bRol, c);

    // ---- fila 3: ver todos
    c.gridx = 2; c.gridy = 2;
    card.add(bAll, c);
    // 🔥 NUEVO BOTÓN – VER ROLES DEL USUARIO
c.gridy = 3;
card.add(bRolesUsuario, c);


    // ===== EVENTOS =====

    // 🔹 Ver todos
    bAll.addActionListener(e -> {
        cargarTabla(usuarioDAO.listarParaTabla());
    });
    

    // 🔹 Consultar por usuario (login)
    bId.addActionListener(e -> {
        String login = txtId.getText().trim();

        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No existe un usuario con el nombre de usuario ingresado.");
            return;
        }

        var fila = usuarioDAO.listarParaTabla().stream()
                .filter(u -> u.login.equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);

        if (fila == null) {
            JOptionPane.showMessageDialog(this,
                    "No existe un usuario con el nombre de usuario ingresado.");
            return;
        }

        cargarTabla(List.of(fila));
        JOptionPane.showMessageDialog(this,
                "Consulta de usuario realizada correctamente.");
    });
    bRolesUsuario.addActionListener(e -> verRolesDelUsuario());


    // 🔹 Consultar por rol (CORREGIDO)
    bRol.addActionListener(e -> {
        String rol = (String) cbRol.getSelectedItem();

        if (rol == null || rol.isBlank()) {
    JOptionPane.showMessageDialog(this,
            "No se pudo obtener el listado: el rol no existe.");
    return;
}


        List<UsuarioDAO.UsuarioTablaRow> data =
                usuarioDAO.listarParaTablaPorRol(rol);

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No existen usuarios asociados al rol seleccionado." + //
                                                "");
            modelo.setRowCount(0);
            return;
        }

        cargarTabla(data);
        JOptionPane.showMessageDialog(this,
                "Listado de usuarios obtenido correctamente." + //
                                        "");
    });

    return wrapper;
}
private void verRolesDelUsuario() {

    String login = txtId.getText().trim();

    if (login.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "No se pudo realizar la consulta: el usuario no existe.");
        return;
    }

    // 🔥 USAMOS LO QUE YA EXISTE
    var fila = usuarioDAO.listarParaTabla().stream()
            .filter(u -> u.login.equalsIgnoreCase(login))
            .findFirst()
            .orElse(null);

    if (fila == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo realizar la consulta: el usuario no existe.");
        return;
    }

    modelo.setRowCount(0);

    // 👇 SOLO UN ROL
    if (fila.roles == null || fila.roles.isBlank()) {
        JOptionPane.showMessageDialog(this,
                "No existen roles asociados al usuario seleccionado.");
        return;
    }

    modelo.addRow(new Object[]{
            fila.id,
            fila.nombre,
            fila.correo,
            fila.login,
            fila.roles,
            fila.activo ? "ACTIVO" : "INACTIVO"
    });

    JOptionPane.showMessageDialog(this,
            "Consulta de roles del usuario realizada correctamente.");
}






private JPanel panelTabla() {

    JPanel contenedor = new JPanel(new BorderLayout());
    contenedor.setOpaque(false);

    // 🔹 Título fuera de la tabla
    JLabel titulo = new JLabel("Listado de usuarios");
    titulo.setForeground(Color.WHITE);
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

    modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Correo", "Login", "Rol", "Estado"}, 0
    ) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    JTable tabla = new JTable(modelo);
    this.tabla = tabla;
    tabla.setRowHeight(28);

    // 👉 aplicar estilos (paso 2 y 3)
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

    // 🔹 Colores base
    Color fondo = new Color(15, 32, 58);
    Color borde = new Color(60, 90, 140);
    Color seleccion = new Color(45, 90, 160);

    tabla.setBackground(fondo);
    tabla.setForeground(Color.WHITE);
    tabla.setGridColor(borde);
    tabla.setShowVerticalLines(true);
    tabla.setShowHorizontalLines(true);

    // ❌ quitar resaltado blanco
    tabla.setSelectionBackground(seleccion);
    tabla.setSelectionForeground(Color.WHITE);

    // 🔹 Header azul (NO blanco)
   // ================= HEADER SIN BLANCO =================
JTableHeader header = tabla.getTableHeader();

DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
headerRenderer.setOpaque(true); // 🔥 CLAVE
headerRenderer.setBackground(new Color(18, 45, 90));
headerRenderer.setForeground(Color.WHITE);
headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
headerRenderer.setBorder(BorderFactory.createLineBorder(
        new Color(60, 90, 140), 1
));

for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
    tabla.getColumnModel()
         .getColumn(i)
         .setHeaderRenderer(headerRenderer);
}

header.setReorderingAllowed(false);
header.setResizingAllowed(false);

    // 🔹 Renderizador filas
    DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

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

    grid.add(itemAccion(
            "Registrar usuario",
            "/Presentacion/Recursos/icons/add.png",
            this::registrarUsuario
    ));

    grid.add(itemAccion(
            "Actualizar correo electrónico",
            "/Presentacion/Recursos/icons/edit.png",
            this::actualizarUsuario
    ));

    grid.add(itemAccion(
            "Activar Desactivar",
            "/Presentacion/Recursos/icons/power.png",
            this::cambiarEstadoUsuario
    ));

    grid.add(itemAccion(
            "Asignar rol",
            "/Presentacion/Recursos/icons/rolis.png",
            this::asignarRolUsuario
    ));

    grid.add(itemAccion(
            "Restablecer Contraseña",
            "/Presentacion/Recursos/icons/reset.png",
            this::restablecerClave
    ));

    // celda vacía para balance visual
   //rid.add(new JPanel());

    contenedor.add(grid, BorderLayout.NORTH);
    return contenedor;
}

private JPanel itemAccion(String texto, String iconPath, Runnable accion) {

    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(AZUL_ACCION);
    card.setPreferredSize(new Dimension(140, 105)); // 🔥 más ancho y alto

    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_ACCION, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    // 🔹 Texto en dos líneas
    String htmlText = "<html><center>" +
            texto.replace(" ", "<br>") +
            "</center></html>";

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


private void activarTab(JButton activo) {
    JButton[] todos = {btnUsuarios, btnRoles, btnPermisos, btnClave};
    for (JButton b : todos) {
        b.setBackground(AZUL_BOTON);
    }
    activo.setBackground(new Color(20, 60, 150));
}




    // =====================================================
    // LOGICA
    // =====================================================
    private Long obtenerIdSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row >= 0) return Long.parseLong(tabla.getValueAt(row, 0).toString());
        return null;
    }

  private void registrarUsuario() {
    JTextField user = new JTextField();
    JTextField mail = new JTextField();

    JComboBox<String> cbRoles = new JComboBox<>();
    for (String r : rolDAO.listarNombresRoles()) cbRoles.addItem(r);

    JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});

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


 private void actualizarUsuario() {

    String login = obtenerLoginSeleccionado();
    if (login == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo actualizar el correo electrónico: los datos ingresados no son válidos.");
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


private void cambiarEstadoUsuario() {

    String login = obtenerLoginSeleccionado();
    if (login == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo activar el usuario: el usuario no existe.");
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
private void restablecerClave() {

    String login = obtenerLoginSeleccionado();
    if (login == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo restablecer la contraseña: el usuario no existe.");
        return;
    }

    String msg = gs.restablecerContrasena(login);
    JOptionPane.showMessageDialog(this, msg);
}

private void asignarRolUsuario() {

    Long idUsuario = obtenerIdSeleccionado();
    if (idUsuario == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo modificar la asignación: el usuario o el rol no existen." + //
                                        "");
        return;
    }

    JComboBox<String> cbRoles = new JComboBox<>();
    for (String r : rolDAO.listarNombresRoles()) {
        cbRoles.addItem(r);
    }

    if (cbRoles.getItemCount() == 0) {
        JOptionPane.showMessageDialog(this,
                "No existen roles disponibles para asignar.");
        return;
    }

    JPanel p = new JPanel(new GridLayout(1, 2, 10, 10));
    p.add(new JLabel("Rol:"));
    p.add(cbRoles);

    int ok = JOptionPane.showConfirmDialog(
            this,
            p,
            "Asignar rol al usuario",
            JOptionPane.OK_CANCEL_OPTION
    );

    if (ok != JOptionPane.OK_OPTION) return;

    String rolSeleccionado = (String) cbRoles.getSelectedItem();
    if (rolSeleccionado == null || rolSeleccionado.isBlank()) return;

    try {
        // 🔥 IMPORTANTE: el service ahora elimina el rol previo y asigna SOLO uno
        gs.asignarRolAUsuario(idUsuario, rolSeleccionado);

        // refrescar tabla
        cargarTabla(usuarioDAO.listarParaTabla());

        JOptionPane.showMessageDialog(this,
                "Rol asignado al usuario correctamente." + //
                                        "",
                "Asignar rol",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}



    // =====================================================
    // HELPERS
    // =====================================================
  private void cargarTabla(List<UsuarioDAO.UsuarioTablaRow> data) {
    modelo.setRowCount(0);

    for (UsuarioDAO.UsuarioTablaRow u : data) {
        modelo.addRow(new Object[]{
                u.id,
                u.nombre,
                u.correo,
                u.login,
                (u.roles == null || u.roles.isBlank()) ? "SIN ROL" : u.roles
,
                u.activo ? "ACTIVO" : "INACTIVO"
        });
    }
}


    private void cargarRoles() {
        cbRol.removeAllItems();
        for (String r : rolDAO.listarNombresRoles()) cbRol.addItem(r);
    }


    

private JButton botonMenu(String texto, String iconPath) {
    JButton b = new JButton(texto);

    ImageIcon icon = icono(iconPath, 34, 34); // 🔥 icono claro y visible
    if (icon != null) {
        b.setIcon(icon);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.BOTTOM);
        b.setIconTextGap(6);
    }

    b.setBackground(AZUL_BOTON);
    b.setForeground(Color.WHITE);

    b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    b.setPreferredSize(new Dimension(0, 64)); // 🔥 altura ideal

    // quitar efectos feos de Swing
    b.setFocusPainted(false);
    b.setBorderPainted(false);
    b.setContentAreaFilled(true);
    b.setOpaque(true);

    return b;
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
    b.setBorderPainted(false);    // ❌ quita borde blanco
    b.setContentAreaFilled(true); // ✅ fuerza color sólido
    b.setOpaque(true);            // ✅ evita transparencia

    return b;
}
private JButton botonAccion(String texto, String iconPath) {
    JButton b = new JButton(texto);

    ImageIcon icon = icono(iconPath, 28, 28);
    if (icon != null) {
        b.setIcon(icon);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(14);
    }

    b.setBackground(AZUL_ACCION);   // 🔥 COLOR NUEVO
    b.setForeground(Color.WHITE);

    b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    b.setPreferredSize(new Dimension(220, 48));

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

    private JPanel panelLogo() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        ImageIcon logo = icono("/Presentacion/Recursos/iconos/logo_empresa.png", 160, 60);
        if (logo != null) p.add(new JLabel(logo));
        return p;
    }

    // ===== PANEL SIMPLE PARA PLACEHOLDERS =====
    private static class JLabelPanel extends JPanel {
        public JLabelPanel(String txt) {
            setLayout(new BorderLayout());
            add(new JLabel(txt, SwingConstants.CENTER), BorderLayout.CENTER);
        }
    }

  
}
