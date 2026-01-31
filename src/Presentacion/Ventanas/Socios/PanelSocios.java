package Presentacion.Ventanas.Socios;

import javax.swing.*;
import java.awt.*;

/**
 * Panel Principal del Módulo de Socios
 * Controla la navegación entre vistas del módulo
 */
public class PanelSocios extends JPanel {

    // Identificadores de vistas
    public static final String MENU = "MENU";
    public static final String REGISTRAR = "REGISTRAR";
    public static final String CONSULTAR = "CONSULTAR";
    public static final String ACTUALIZAR = "ACTUALIZAR";
    public static final String LISTADO = "LISTADO";

    private CardLayout cardLayout;
    private JPanel panelCards;

    // 🎨 Color base (MISMO que Buses)
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color HEADER_COLOR = new Color(10, 18, 34);

    public PanelSocios() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // Header
        add(construirHeader(), BorderLayout.NORTH);

        // CardLayout
        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setBackground(BG_MAIN);

        // Vistas
        panelCards.add(new PanelMenuSocios(this), MENU);
        panelCards.add(new PanelRegistrarSocio(), REGISTRAR);
        panelCards.add(new PanelConsultarSocio(), CONSULTAR);
        panelCards.add(new PanelActualizarSocio(), ACTUALIZAR);
        panelCards.add(new PanelListarSocios(), LISTADO);

        add(panelCards, BorderLayout.CENTER);

        mostrarVista(MENU);
    }

    public void mostrarVista(String vista) {
        cardLayout.show(panelCards, vista);
    }

    /**
     * Header del módulo
     */
    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);

        // Línea inferior fina + padding (igual a Buses)
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 80, 140)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel titulo = new JLabel("Gestión de Socios Propietarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(titulo);

        header.add(textos, BorderLayout.WEST);
        return header;
    }
}
