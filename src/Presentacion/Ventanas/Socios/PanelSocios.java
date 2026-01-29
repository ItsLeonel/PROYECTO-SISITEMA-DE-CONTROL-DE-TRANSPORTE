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

    // Color del tema
    private static final Color PRIMARY_COLOR = new Color(15, 23, 42); // azul oscuro elegante


    public PanelSocios() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // Header
        add(construirHeader(), BorderLayout.NORTH);

        // CardLayout
        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setBackground(new Color(245, 247, 250));

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
    header.setBackground(PRIMARY_COLOR);

    // Borde inferior + padding (EN UNO SOLO)
    header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 64, 175)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
    ));

    JLabel titulo = new JLabel("Gestión de Socios Propietarios");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
    titulo.setForeground(Color.WHITE);

    JLabel subtitulo = new JLabel(
            "");
    subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    subtitulo.setForeground(new Color(255, 255, 255, 200));

    JPanel textos = new JPanel();
    textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
    textos.setOpaque(false);
    textos.add(titulo);
    textos.add(Box.createVerticalStrut(5));
    textos.add(subtitulo);

    header.add(textos, BorderLayout.WEST);
    return header;
}

}
