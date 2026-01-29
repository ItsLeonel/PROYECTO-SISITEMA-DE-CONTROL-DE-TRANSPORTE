package Presentacion.Ventanas.GestionSistema;

import Logica.DAO.UsuarioDAO;
import Logica.Servicios.GestionSistemaService;
import Logica.Servicios.SessionContext;

import javax.swing.*;
import java.awt.*;

public class PanelCambioContrasena extends JPanel {

    // ================= COLORES =================
    private static final Color AZUL_FONDO  = new Color(12, 32, 64);
    private static final Color AZUL_PANEL  = new Color(18, 45, 90);
    private static final Color AZUL_ACCION = new Color(15, 32, 58);

    // ================= LOGICA =================
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final GestionSistemaService gs = new GestionSistemaService();

    // ================= UI =================
    private JPasswordField txtActual;
    private JPasswordField txtNueva;
    private JPasswordField txtConfirmar;

    public PanelCambioContrasena() {
        setLayout(new BorderLayout(14, 14));
        setBackground(AZUL_FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(titulo(), BorderLayout.NORTH);
        add(panelFormulario(), BorderLayout.CENTER);
    }

    // =====================================================
    // TITULO
    // =====================================================
    private JPanel titulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel t = new JLabel("Cambio de Contraseña");
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(Color.WHITE);

        p.add(t, BorderLayout.WEST);
        return p;
    }

    // =====================================================
    // FORMULARIO
    // =====================================================
    private JPanel panelFormulario() {

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AZUL_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel(
                "Usuario: " + SessionContext.getUsuarioLogin()
                + " | Rol: " + SessionContext.getRol()
        );
        lblUser.setForeground(new Color(220, 220, 220));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtActual = campoPassword();
        txtNueva = campoPassword();
        txtConfirmar = campoPassword();

        JButton btnCambiar = botonAccion("Cambiar contraseña");

        // ---- layout ----
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        p.add(lblUser, c);

        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 1;
        p.add(label("Contraseña actual:"), c);
        c.gridx = 1;
        p.add(txtActual, c);

        c.gridx = 0; c.gridy = 2;
        p.add(label("Nueva contraseña:"), c);
        c.gridx = 1;
        p.add(txtNueva, c);

        c.gridx = 0; c.gridy = 3;
        p.add(label("Confirmar nueva:"), c);
        c.gridx = 1;
        p.add(txtConfirmar, c);

        c.gridx = 1; c.gridy = 4;
        p.add(btnCambiar, c);

        btnCambiar.addActionListener(e -> cambiarContrasena());

        return p;
    }

    // =====================================================
    // LOGICA
    // =====================================================
 private void cambiarContrasena() {

    if (!SessionContext.isLogged()) {
        JOptionPane.showMessageDialog(this,
            "No se pudo cambiar la contraseña: el usuario no existe.");
        return;
    }

    String login = SessionContext.getUsuarioLogin();
    long idUsuario = usuarioDAO.obtenerIdPorLogin(login);

    if (idUsuario == 0) {
        JOptionPane.showMessageDialog(this,
            "No se pudo cambiar la contraseña: el usuario no existe.");
        return;
    }

    try {
        gs.cambiarContrasena(
                idUsuario,
                new String(txtActual.getPassword()),
                new String(txtNueva.getPassword()),
                new String(txtConfirmar.getPassword())
        );

        txtActual.setText("");
        txtNueva.setText("");
        txtConfirmar.setText("");

        JOptionPane.showMessageDialog(this,
                "Contraseña actualizada correctamente.");

    } catch (Exception ex) {
        // ⚠️ AQUÍ ES CLAVE:
        // el service DEBE lanzar mensajes EXACTOS del ERS
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

    private JPasswordField campoPassword() {
        JPasswordField p = new JPasswordField(18);
        p.setBackground(new Color(245, 248, 252));
        p.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return p;
    }

    private JButton botonAccion(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(AZUL_ACCION);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(220, 40));
        return b;
    }
}
