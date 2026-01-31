package Presentacion.Ventanas.Socios;

import javax.swing.*;
import java.awt.*;

public class PanelMenuSocios extends JPanel {

    // 🎨 Colores (IGUALES A BUSES)
    private static final Color BTN_BG = new Color(16, 32, 58, 200);
    private static final Color BTN_HOVER = new Color(22, 42, 72, 200);
    private static final Color BTN_BORDER = new Color(40, 80, 140);

    public PanelMenuSocios(PanelSocios parent) {
        setLayout(new BorderLayout());

        // ===== GRID DE BOTONES =====
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 35, 25, 35);

        gbc.gridx = 0;
        gbc.gridy = 0;
        grid.add(crearBoton(
                "Registrar Socio",
                "/Presentacion/Recursos/icons/R.png",
                () -> parent.mostrarVista(PanelSocios.REGISTRAR)
        ), gbc);

        gbc.gridx = 1;
        grid.add(crearBoton(
                "Consultar Socio",
                "/Presentacion/Recursos/icons/C.png",
                () -> parent.mostrarVista(PanelSocios.CONSULTAR)
        ), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        grid.add(crearBoton(
                "Actualizar Datos",
                "/Presentacion/Recursos/icons/A.png",
                () -> parent.mostrarVista(PanelSocios.ACTUALIZAR)
        ), gbc);

        gbc.gridx = 1;
        grid.add(crearBoton(
                "Gestión de Listado",
                "/Presentacion/Recursos/icons/L.png",
                () -> parent.mostrarVista(PanelSocios.LISTADO)
        ), gbc);

        // ===== FONDO (MISMO PATRÓN QUE BUSES) =====
        JPanel fondo = new PanelConFondo("/Presentacion/Recursos/fondo_socios.png");
        fondo.add(grid);

        add(fondo, BorderLayout.CENTER);
    }

    // ===== BOTÓN DASHBOARD =====
    private JButton crearBoton(String texto, String iconPath, Runnable action) {

        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getModel().isRollover() ? BTN_HOVER : BTN_BG);
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

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);

 

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());

        return btn;
    }

    // ===== PANEL DE FONDO (IMAGEN BRILLANTE) =====
    class PanelConFondo extends JPanel {

        private Image imagen;

        public PanelConFondo(String ruta) {
            imagen = new ImageIcon(getClass().getResource(ruta)).getImage();
            setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), null);

            // 🔥 OVERLAY MUY SUAVE PARA QUE LA IMAGEN SE VEA BRILLANTE
            // Cambiado de (10, 20, 40, 80) a (10, 20, 40, 25) - mucho más transparente
            g2.setColor(new Color(10, 20, 40, 25));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
        }

    }
}