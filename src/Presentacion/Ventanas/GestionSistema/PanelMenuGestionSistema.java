package Presentacion.Ventanas.GestionSistema;

import Logica.Servicios.SessionContext;
import javax.swing.*;
import java.awt.*;

/**
 * Panel de menú principal para Gestión del Sistema
 * ESTILO IDÉNTICO AL MÓDULO DE BUSES
 */
public class PanelMenuGestionSistema extends JPanel {

    // ===== COLORES IDÉNTICOS A BUSES =====
    private static final Color BG_CARD = new Color(16, 32, 58, 220);
    private static final Color BG_CARD_HOVER = new Color(22, 42, 72);
    private static final Color BORDER_CARD = new Color(40, 80, 140);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);

    // ================= CALLBACK =================
    private final Runnable onUsuarios;
    private final Runnable onRoles;
    private final Runnable onPermisos;
    private final Runnable onCambioContrasena;

    public PanelMenuGestionSistema(
            Runnable onUsuarios,
            Runnable onRoles,
            Runnable onPermisos,
            Runnable onCambioContrasena
    ) {
        this.onUsuarios = onUsuarios;
        this.onRoles = onRoles;
        this.onPermisos = onPermisos;
        this.onCambioContrasena = onCambioContrasena;

        setLayout(new GridBagLayout());
        setOpaque(false);

        add(panelCentral());
    }

    // =====================================================
    // PANEL CENTRAL CON CARDS ESTILO BUSES
    // =====================================================
    private JPanel panelCentral() {
        // 🔥 VERIFICAR SI TIENE ACCESO AL MÓDULO COMPLETO
        boolean tieneAccesoModulo = SessionContext.tienePermiso("MOD_GESTION_SISTEMA");

        if (!tieneAccesoModulo) {
            return crearPanelSinAcceso();
        }

        // ✅ Grid 2x2 igual que Buses
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 25, 25, 25);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        // FILA 1
        gbc.gridy = 0;
        gbc.gridx = 0;
        grid.add(crearCard(
                "Usuarios",
                "Registrar y gestionar usuarios",
                "/Presentacion/Recursos/icons/usuarios.png",
                onUsuarios
        ), gbc);

        gbc.gridx = 1;
        grid.add(crearCard(
                "Roles",
                "Administrar roles del sistema",
                "/Presentacion/Recursos/icons/roles.png",
                onRoles
        ), gbc);

        // FILA 2
        gbc.gridy = 1;
        gbc.gridx = 0;
        grid.add(crearCard(
                "Permisos",
                "Gestionar permisos de roles",
                "/Presentacion/Recursos/icons/permisos.png",
                onPermisos
        ), gbc);

        gbc.gridx = 1;
        grid.add(crearCard(
                "Cambio de Contraseña",
                "Actualizar contraseña personal",
                "/Presentacion/Recursos/icons/password.png",
                onCambioContrasena
        ), gbc);

        return grid;
    }

    // =====================================================
    // PANEL SIN ACCESO
    // =====================================================
    private JPanel crearPanelSinAcceso() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(500, 200));

        JLabel mensaje = new JLabel("<html><center>" +
                "<b style='font-size:16px;'>Acceso Restringido</b><br><br>" +
                "No tienes permisos para acceder<br>" +
                "al módulo de Gestión del Sistema.<br><br>" +
                "<span style='font-size:11px;'>Contacta al administrador si necesitas acceso.</span>" +
                "</center></html>");
        mensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mensaje.setForeground(Color.WHITE);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(mensaje, BorderLayout.CENTER);
        return panel;
    }

    // =====================================================
    // CREAR CARD ESTILO BUSES (IDÉNTICO)
    // =====================================================
    private JPanel crearCard(String titulo, String descripcion, String iconPath, Runnable accion) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CARD, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ✅ Tamaño EXACTO igual que Buses: 280x150
        Dimension size = new Dimension(280, 150);
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);
        panel.setMaximumSize(size);

        // ===== ICONO =====
        JLabel lblIcono = new JLabel();
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon icon = cargarIcono(iconPath, 48, 48);
        if (icon != null) {
            lblIcono.setIcon(icon);
        } else {
            System.err.println("⚠ Icono no encontrado: " + iconPath);
        }

        // ===== TÍTULO =====
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== DESCRIPCIÓN =====
        JLabel lblDescripcion = new JLabel(
                "<html><div style='text-align:center;'>" + descripcion + "</div></html>"
        );
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescripcion.setForeground(TEXT_SECONDARY);
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== ENSAMBLAR =====
        panel.add(Box.createVerticalGlue());
        panel.add(lblIcono);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblDescripcion);
        panel.add(Box.createVerticalGlue());

        // ===== HOVER EFFECT IDÉNTICO A BUSES =====
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (accion != null) accion.run();
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                panel.setBackground(BG_CARD_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                panel.setBackground(BG_CARD);
            }
        });

        return panel;
    }

    // =====================================================
    // HELPER PARA CARGAR ICONOS
    // =====================================================
    private ImageIcon cargarIcono(String ruta, int w, int h) {
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