package Presentacion.Ventanas.Transporte;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import Presentacion.Ventanas.Transporte.rutas.PanelRutas;
import Presentacion.Ventanas.Transporte.bases.PanelBases;
import Presentacion.Ventanas.Transporte.intervalos.PanelIntervalos;

public class PanelTransporte extends JPanel {

    private CardLayout cardLayout;
    private JPanel panelCards;

    // ===== COLORES =====
    private static final Color BG_MAIN = new Color(15, 30, 60);
    private static final Color BG_BAR  = new Color(10, 25, 55);
    private static final Color BTN_NAV = new Color(40, 90, 180);

   public PanelTransporte() {

    setLayout(new BorderLayout());
    setBackground(BG_MAIN);

    // ===== CONTENEDOR SUPERIOR (TÍTULO + BOTONES) =====
    JPanel topContainer = new JPanel();
    topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
    topContainer.setBackground(BG_BAR);

    // ===== TÍTULO =====
    JLabel titulo = new JLabel("Transporte");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titulo.setForeground(Color.WHITE);
    titulo.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));
    titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    topContainer.add(titulo);

    // ===== BARRA DE NAVEGACIÓN =====
    JPanel barra = barraNavegacion();
    barra.setAlignmentX(Component.LEFT_ALIGNMENT);

    topContainer.add(barra);

    add(topContainer, BorderLayout.NORTH);

    // ===== CONTENIDO =====
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
    wrapper.add(panelContenido(), BorderLayout.CENTER);

    add(wrapper, BorderLayout.CENTER);
}


    // =====================================================
    // BARRA DE NAVEGACIÓN
    // =====================================================
    private JPanel barraNavegacion() {

        JPanel bar = new JPanel(new GridLayout(1, 3, 8, 0));
        bar.setBackground(BG_BAR);

        // 🔹 Separación inferior (ESTO ES CLAVE)
   bar.setBorder(BorderFactory.createEmptyBorder(8, 16, 20, 16));


        JButton btnRutas = botonNav("Rutas", "Presentacion/Recursos/icons/ruta.png");
        JButton btnBases = botonNav("Bases", "Presentacion/Recursos/icons/base.png");
        JButton btnIntervalos = botonNav("Intervalos", "Presentacion/Recursos/icons/reloj.png");

        btnRutas.addActionListener(e -> cardLayout.show(panelCards, "RUTAS"));
        btnBases.addActionListener(e -> cardLayout.show(panelCards, "BASES"));
        btnIntervalos.addActionListener(e -> cardLayout.show(panelCards, "INTERVALOS"));

        bar.add(btnRutas);
        bar.add(btnBases);
        bar.add(btnIntervalos);

        return bar;
    }

    // =====================================================
    // CONTENIDO CENTRAL (CARDS)
    // =====================================================
    private JPanel panelContenido() {

        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setOpaque(false);

        panelCards.add(new PanelRutas(), "RUTAS");
        panelCards.add(new PanelBases(), "BASES");
        panelCards.add(new PanelIntervalos(), "INTERVALOS");

        cardLayout.show(panelCards, "RUTAS");

        return panelCards;
    }

    // =====================================================
    // BOTÓN DE NAVEGACIÓN
    // =====================================================
    private JButton botonNav(String texto, String iconPath) {

        URL url = getClass().getClassLoader().getResource(iconPath);

        JButton b;
        if (url != null) {
            b = new JButton(texto, new ImageIcon(url));
        } else {
            System.err.println("⚠ Icono no encontrado: " + iconPath);
            b = new JButton(texto);
        }

        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        b.setIconTextGap(10);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);

        b.setForeground(Color.WHITE);
        b.setBackground(BTN_NAV);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 🔹 Que ocupen TODO el espacio del GridLayout
        b.setPreferredSize(null);
        b.setMaximumSize(null);

        return b;
    }
}
