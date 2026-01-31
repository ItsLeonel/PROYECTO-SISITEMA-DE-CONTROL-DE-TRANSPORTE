package Presentacion.Ventanas.GestionSistema;

import Logica.Servicios.SessionContext;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal de Gestión del Sistema
 * ACTUALIZADO: Verifica acceso al MÓDULO completo (MOD_GESTION_SISTEMA)
 */
public class PanelGestionSistema extends JPanel {

    // ================= COLORES =================
    private static final Color AZUL_HEADER = new Color(15, 25, 40);

    // ================= CARD LAYOUT =================
    private CardLayout cardLayout;
    private JPanel panelCards;
    private JPanel panelConFondo;
    private JButton btnVolver;

    public PanelGestionSistema() {
        setLayout(new BorderLayout());
        setBackground(AZUL_HEADER);

        add(headerFijo(), BorderLayout.NORTH);
        add(contenido(), BorderLayout.CENTER);
    }

    // =====================================================
    // HEADER FIJO (SIEMPRE VISIBLE)
    // =====================================================
    private JPanel headerFijo() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AZUL_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // ===== BOTÓN VOLVER (INICIALMENTE OCULTO) =====
        btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(new Color(32, 80, 200));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setPreferredSize(new Dimension(200, 35));
        btnVolver.setVisible(false);
        btnVolver.addActionListener(e -> {
            cardLayout.show(panelCards, "INICIO");
            btnVolver.setVisible(false);
        });

        // ===== TÍTULO Y SUBTÍTULO =====
        JLabel titulo = new JLabel("Gestión del Sistema");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Administración de la flota de buses de la compañía");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(180, 190, 200));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(subtitulo);

        // ===== LAYOUT =====
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        izquierda.add(btnVolver);
        izquierda.add(Box.createHorizontalStrut(20));
        izquierda.add(textos);

        header.add(izquierda, BorderLayout.WEST);

        return header;
    }

    // =====================================================
    // CONTENIDO PRINCIPAL (CARDS)
    // =====================================================
    private JPanel contenido() {
        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setOpaque(false);

        // ===== PANEL CON FONDO DE IMAGEN (SOLO PARA INICIO) =====
        panelConFondo = new JPanel(new BorderLayout()) {
            private Image imagenFondo;

            {
                try {
                    var url = getClass().getResource("/Presentacion/Recursos/X.png");
                    if (url != null) {
                        imagenFondo = new ImageIcon(url).getImage();
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
                    setBackground(new Color(12, 32, 64));
                    setOpaque(true);
                }
            }
        };
        panelConFondo.setOpaque(false);

        // 🔥 Panel de menú principal (INICIO) - Verifica acceso al módulo
        PanelMenuGestionSistema panelMenu = new PanelMenuGestionSistema(
                () -> mostrarPanel("USUARIOS"),
                () -> mostrarPanel("ROLES"),
                () -> mostrarPanel("PERMISOS"),
                () -> mostrarPanel("CLAVE")
        );

        panelConFondo.add(panelMenu, BorderLayout.CENTER);

        // Agregar los paneles
        panelCards.add(panelConFondo, "INICIO");
        panelCards.add(crearPanelContenedor(new PanelUsuarios()), "USUARIOS");
        panelCards.add(crearPanelContenedor(new PanelRoles()), "ROLES");
        panelCards.add(crearPanelContenedor(new PanelPermisos()), "PERMISOS");
        panelCards.add(crearPanelContenedor(new PanelCambioContrasena()), "CLAVE");

        cardLayout.show(panelCards, "INICIO");

        return panelCards;
    }

    // =====================================================
    // CONTENEDOR PARA PANELES FUNCIONALES
    // =====================================================
    private JPanel crearPanelContenedor(JPanel panelInterno) {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(240, 242, 245));
        contenedor.add(panelInterno, BorderLayout.CENTER);
        return contenedor;
    }

    // =====================================================
    // MOSTRAR PANEL Y ACTIVAR BOTÓN VOLVER
    // =====================================================
    private void mostrarPanel(String nombrePanel) {
        cardLayout.show(panelCards, nombrePanel);
        btnVolver.setVisible(true);
    }
}