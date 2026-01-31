package Presentacion.Ventanas;

import Presentacion.Recursos.RoundedPanel;

import javax.swing.*;

import Logica.DAO.UsuarioDAO;
import Logica.Servicios.PasswordUtil;

import java.awt.*;
import java.net.URL;

public class VentanaCambioContrasena extends JDialog {

    private JPasswordField txtActual;
    private JPasswordField txtNueva;
    private JPasswordField txtConfirmar;
    private long idUsuario;

    public VentanaCambioContrasena(JFrame parent, long idUsuario) {

        super(parent, true);
        this.idUsuario = idUsuario;

        setUndecorated(true);
        setSize(520, 360);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 42, 92),
                        0, getHeight(), new Color(14, 95, 181)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setLayout(new BorderLayout());
        setContentPane(root);

        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearCentro(), BorderLayout.CENTER);
    }

    // ───────────────── HEADER ─────────────────

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel titulo = new JLabel("Cambio de contraseña");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);

        JLabel logo = new JLabel();
        URL url = getClass().getResource("/Presentacion/Recursos/logo.png");
        if (url != null) {
            logo.setIcon(new ImageIcon(
                    new ImageIcon(url).getImage().getScaledInstance(80, 32, Image.SCALE_SMOOTH)
            ));
        }

        JButton cerrar = new JButton(" X ");
        cerrar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        cerrar.setForeground(Color.WHITE);
        cerrar.setBackground(new Color(200, 50, 50));
        cerrar.setOpaque(true);
        cerrar.setContentAreaFilled(true);
        cerrar.setBorderPainted(false);
        cerrar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        cerrar.setFocusPainted(false);
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.addActionListener(e -> dispose());

        derecha.add(logo);
        derecha.add(cerrar);

        header.add(titulo, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);
        return header;
    }

    // ───────────────── CENTRO ─────────────────

    private JPanel crearCentro() {
        RoundedPanel card = new RoundedPanel(18);
        card.setPreferredSize(new Dimension(340, 260));
        card.setPanelBackground(new Color(10, 42, 92, 190));
        card.setPanelBorder(new Color(255, 255, 255, 90));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        txtActual = campoPassword();
        txtNueva = campoPassword();
        txtConfirmar = campoPassword();

        card.add(labelCampo("Contraseña actual"));
        card.add(Box.createVerticalStrut(4));
        card.add(txtActual);

        card.add(Box.createVerticalStrut(12));
        card.add(labelCampo("Nueva contraseña"));
        card.add(Box.createVerticalStrut(4));
        card.add(txtNueva);

        card.add(Box.createVerticalStrut(12));
        card.add(labelCampo("Confirmar contraseña"));
        card.add(Box.createVerticalStrut(4));
        card.add(txtConfirmar);

        card.add(Box.createVerticalStrut(18));
        card.add(botonGuardar());

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(card);

        return centro;
    }

    // ───────────────── COMPONENTES ─────────────────

    private JLabel labelCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(new Color(190, 220, 255));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JPasswordField campoPassword() {
        JPasswordField f = new JPasswordField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setPreferredSize(new Dimension(280, 38));
        f.setBackground(new Color(6, 28, 64));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setHorizontalAlignment(JTextField.CENTER);

        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 150, 220)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    // ───────────────── BOTÓN GUARDAR ─────────────────

    private JButton botonGuardar() {
        JButton b = new JButton(" Guardar cambios");
        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        b.setBackground(new Color(46, 160, 70));
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 36, 10, 36));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addActionListener(e -> {
            try {
                String actual = new String(txtActual.getPassword());
                String nueva = new String(txtNueva.getPassword());
                String confirmar = new String(txtConfirmar.getPassword());

                if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo cambiar la contraseña: los datos ingresados no son válidos.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                UsuarioDAO dao = new UsuarioDAO();
                UsuarioDAO.UsuarioRow u = dao.obtenerPorId(idUsuario);

                if (u == null) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo cambiar la contraseña: el usuario no existe.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UsuarioDAO.PasswordData pd = dao.obtenerHashSalt(idUsuario);
                String hashActual = PasswordUtil.hashSHA256(actual, pd.salt);

                if (!hashActual.equalsIgnoreCase(pd.hash)) {
                    JOptionPane.showMessageDialog(this,
                            "La contraseña temporal ingresada es incorrecta.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!nueva.equals(confirmar)) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo cambiar la contraseña: la nueva contraseña no cumple el formato requerido.", /// Contraseñas similares
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!cumplePoliticaClave(nueva)) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo cambiar la contraseña: la nueva contraseña no cumple el formato requerido.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String nuevoSalt = PasswordUtil.generarSalt();
                String nuevoHash = PasswordUtil.hashSHA256(nueva, nuevoSalt);

                dao.actualizarPassword(idUsuario, nuevoHash, nuevoSalt, false);

                JOptionPane.showMessageDialog(this,
                        "Contraseña actualizada correctamente. Acceso completo habilitado.",
                        "",
                        JOptionPane.INFORMATION_MESSAGE);

                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "",
                        "", JOptionPane.ERROR_MESSAGE);
            }
        });

        return b;
    }

    // ───────────────── ANEXO B ─────────────────

    private boolean cumplePoliticaClave(String clave) {
        if (clave == null) return false;
        if (clave.length() < 8 || clave.length() > 20) return false;
        if (clave.contains(" ")) return false;

        boolean letra = false;
        boolean numero = false;

        for (char c : clave.toCharArray()) {
            if (Character.isLetter(c)) letra = true;
            if (Character.isDigit(c)) numero = true;
        }
        return letra && numero;
    }
}
