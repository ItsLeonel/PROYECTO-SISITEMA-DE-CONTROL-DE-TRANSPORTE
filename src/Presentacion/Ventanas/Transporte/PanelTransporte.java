package Presentacion.Ventanas.Transporte;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

import Presentacion.Ventanas.Transporte.rutas.PanelRutas;
import Presentacion.Ventanas.Transporte.bases.PanelBases;
import Presentacion.Ventanas.Transporte.plantillas.PanelPlantillasHorarias;

public class PanelTransporte extends JPanel {

    private CardLayout cardLayout;
    private JPanel panelCards;

    // ===== COLORES CONSISTENTES CON BUSES =====
    private static final Color BG_MAIN = new Color(15, 30, 60);
    private static final Color BG_CARD = new Color(16, 32, 58, 220);
    private static final Color BG_CARD_HOVER = new Color(22, 42, 72);
    private static final Color BORDER_CARD = new Color(52, 120, 246, 100);

    public PanelTransporte() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // ===== CARD LAYOUT PARA CAMBIAR PANTALLAS =====
        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setOpaque(false);

        // ===== AGREGAR PANTALLAS =====
        panelCards.add(pantallaInicio(), "INICIO");
        panelCards.add(new PanelRutas(cardLayout, panelCards), "RUTAS");
        panelCards.add(new PanelBases(cardLayout, panelCards), "BASES");
        panelCards.add(new PanelPlantillasHorarias(cardLayout, panelCards), "PLANTILLAS"); // ← CAMBIO AQUÍ

        // ===== MOSTRAR PANTALLA INICIAL =====
        cardLayout.show(panelCards, "INICIO");

        add(panelCards, BorderLayout.CENTER);
    }

    // =====================================================
    // PANTALLA INICIAL CON FONDO Y ESTILO BUSES
    // =====================================================
    private JPanel pantallaInicio() {
        // Panel con fondo de imagen
        PanelConFondo p = new PanelConFondo("/Presentacion/Recursos/fondo_transporte.png");
        p.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("Módulo de Transporte", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        gbc.insets = new Insets(0, 0, 60, 0);
        p.add(titulo, gbc);

        // ===== BOTONES ESTILO BUSES =====
        gbc.insets = new Insets(15, 0, 15, 0);

        JButton btnRutas = crearBotonMenu(
                "Gestión de Rutas",
                "ruta.png",
                "RUTAS");
        p.add(btnRutas, gbc);

        JButton btnBases = crearBotonMenu(
                "Gestión de Bases",
                "base.png",
                "BASES");
        p.add(btnBases, gbc);

        JButton btnPlantillas = crearBotonMenu(
                "Plantillas Horarias",  // ← CAMBIO DE NOMBRE AQUÍ
                "reloj.png",
                "PLANTILLAS");  // ← CAMBIO DE CARD AQUÍ
        p.add(btnPlantillas, gbc);

        return p;
    }

    // =====================================================
    // CREAR BOTÓN MENU ESTILO BUSES
    // =====================================================
    private JButton crearBotonMenu(String texto, String nombreIcono, String cardDestino) {
        JButton btn = new JButton(texto);

        // ===== ICONO =====
        Icon icono = cargarIcono(nombreIcono, 48, 48);
        if (icono != null) {
            btn.setIcon(icono);
        }

        // ===== ESTILO TIPO BUSES =====
        btn.setPreferredSize(new Dimension(450, 90));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(BG_CARD);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(20);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CARD, 2),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ===== HOVER EFFECT IGUAL A BUSES =====
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_CARD_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_CARD);
            }
        });

        // ===== ACTION =====
        btn.addActionListener(e -> cardLayout.show(panelCards, cardDestino));

        return btn;
    }

    private Icon cargarIcono(String nombre, int w, int h) {
        try {
            URL url = getClass().getResource("/Presentacion/Recursos/icons/" + nombre);
            if (url == null) return null;
            ImageIcon icono = new ImageIcon(url);
            Image img = icono.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // =====================================================
    // PANEL CON FONDO DE IMAGEN (IGUAL A BUSES)
    // =====================================================
    private class PanelConFondo extends JPanel {
        private BufferedImage imagenFondo;

        public PanelConFondo(String rutaImagen) {
            setOpaque(false);
            cargarImagen(rutaImagen);
        }

        private void cargarImagen(String ruta) {
            try {
                URL url = getClass().getResource(ruta);
                if (url != null) {
                    imagenFondo = ImageIO.read(url);
                }
            } catch (Exception e) {
                imagenFondo = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null) {
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(BG_MAIN);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}