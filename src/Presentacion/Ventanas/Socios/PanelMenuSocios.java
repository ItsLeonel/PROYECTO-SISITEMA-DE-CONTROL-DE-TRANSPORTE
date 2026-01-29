package Presentacion.Ventanas.Socios;

import javax.swing.*;
import java.awt.*;

public class PanelMenuSocios extends JPanel {

    private Image background;

    // 🎨 Colores del tema
    private static final Color BTN_BG = new Color(15, 23, 42);
    private static final Color BTN_BORDER = new Color(59, 130, 246);
    private static final Color BTN_HOVER = new Color(30, 41, 59);

    public PanelMenuSocios(PanelSocios parent) {
        setLayout(new GridBagLayout());

        // ===== CARGAR IMAGEN DE FONDO =====
        background = new ImageIcon(
                getClass().getResource("/Presentacion/Recursos/fondo_socios.png")
        ).getImage();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 35, 25, 35);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(crearBoton("Registrar Socio",
                "/Presentacion/Recursos/icons/R.png",
                () -> parent.mostrarVista(PanelSocios.REGISTRAR)), gbc);

        gbc.gridx = 1;
        add(crearBoton("Consultar Socio",
                "/Presentacion/Recursos/icons/C.png",
                () -> parent.mostrarVista(PanelSocios.CONSULTAR)), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(crearBoton("Actualizar Datos",
                "/Presentacion/Recursos/icons/A.png",
                () -> parent.mostrarVista(PanelSocios.ACTUALIZAR)), gbc);

        gbc.gridx = 1;
        add(crearBoton("Gestión de Listado",
                "/Presentacion/Recursos/icons/L.png",
                () -> parent.mostrarVista(PanelSocios.LISTADO)), gbc);
    }

    // ===== FONDO + OVERLAY OSCURO =====
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        g.setColor(new Color(10, 20, 40, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // ===== BOTÓN TIPO DASHBOARD =====
private JButton crearBoton(String texto, String iconPath, Runnable action) {

    JButton btn = new JButton(texto) {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo
            g2.setColor(getModel().isRollover()
                    ? new Color(30, 41, 59)
                    : new Color(15, 23, 42));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    btn.setPreferredSize(new Dimension(220, 120));
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setForeground(Color.WHITE);

    btn.setVerticalTextPosition(SwingConstants.BOTTOM);
    btn.setHorizontalTextPosition(SwingConstants.CENTER);
    btn.setIconTextGap(10);

    btn.setIcon(new ImageIcon(
            new ImageIcon(getClass().getResource(iconPath))
                    .getImage()
                    .getScaledInstance(32, 32, Image.SCALE_SMOOTH)
    ));

    // 🔑 CLAVES PARA MATAR EL BLANCO
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setOpaque(false);

    btn.setBorder(BorderFactory.createLineBorder(
            new Color(59, 130, 246), 1, true
    ));

    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.addActionListener(e -> action.run());

    return btn;
}

}
