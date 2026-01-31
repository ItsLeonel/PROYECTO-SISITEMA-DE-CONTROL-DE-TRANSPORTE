package Presentacion.Ventanas.Buses;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Panel Principal del Módulo de Buses
 * Menú estilo dashboard (igual a Socios)
 * ✅ MEJORADO: Recarga automática de socios al cambiar de vista
 */
public class PanelBuses extends JPanel {

    // ===== CardLayout =====
    private CardLayout cardLayout;
    private JPanel panelContenido;

    // ✅ Referencias a los paneles
    private PanelRegistrarBus panelRegistrarBus;
    private PanelConsultarBus panelConsultarBus;
    private PanelListarBuses panelListarBuses;
    private PanelAsignarBases panelAsignarBases;

    // ===== Colores =====
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_CARD = new Color(16, 32, 58, 220);
    private static final Color BORDER_CARD = new Color(40, 80, 140);
    private static final Color BTN_PRIMARY = new Color(33, 90, 190);
    private static final Color BTN_SUCCESS = new Color(40, 167, 69);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);

    // ===== Vistas =====
    public static final String MENU = "MENU";
    public static final String REGISTRAR = "REGISTRAR";
    public static final String CONSULTAR = "CONSULTAR";
    public static final String LISTAR = "LISTAR";
    public static final String BASES = "BASES";

    public PanelBuses() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        add(construirHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(BG_MAIN);

        // ✅ Crear paneles y guardar referencias
        panelRegistrarBus = new PanelRegistrarBus(this);
        panelConsultarBus = new PanelConsultarBus(this);
        panelListarBuses = new PanelListarBuses(this);
        panelAsignarBases = new PanelAsignarBases(this);

        panelContenido.add(construirMenu(), MENU);
        panelContenido.add(panelRegistrarBus, REGISTRAR);
        panelContenido.add(panelConsultarBus, CONSULTAR);
        panelContenido.add(panelListarBuses, LISTAR);
        panelContenido.add(panelAsignarBases, BASES);

        add(panelContenido, BorderLayout.CENTER);
        mostrar(MENU);
    }

    // ===== HEADER =====
    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 18, 34));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 80, 140)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel titulo = new JLabel("Gestión de Buses");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Administración de la flota de buses de la compañía");
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

    // ===== MENÚ (centrado y compacto) =====
    private JPanel construirMenu() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 25, 25, 25);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        // FILA 1
        gbc.gridy = 0;
        gbc.gridx = 0;
        grid.add(crearBotonMenu(
                "Registrar Bus",
                "Registrar un nuevo bus",
                "/Presentacion/Recursos/icons/X.png",
                BTN_SUCCESS,
                e -> mostrar(REGISTRAR)
        ), gbc);

        gbc.gridx = 1;
        grid.add(crearBotonMenu(
                "Consultar Bus",
                "Buscar bus por placa",
                "/Presentacion/Recursos/icons/Z.png",
                BTN_PRIMARY,
                e -> mostrar(CONSULTAR)
        ), gbc);

        // FILA 2
        gbc.gridy = 1;
        gbc.gridx = 0;
        grid.add(crearBotonMenu(
                "Listar Flota",
                "Ver todos los buses",
                "/Presentacion/Recursos/icons/Y.png",
                BTN_PRIMARY,
                e -> mostrar(LISTAR)
        ), gbc);

        gbc.gridx = 1;
        grid.add(crearBotonMenu(
                "Asignar Bases",
                "Cambiar base operativa",
                "/Presentacion/Recursos/icons/W.png",
                BTN_PRIMARY,
                e -> mostrar(BASES)
        ), gbc);

        // Wrapper para centrar TODO el bloque
        JPanel fondo = new PanelConFondo("/Presentacion/Recursos/fondo_buses.png");
        fondo.add(grid);

        return fondo;
    }

    // ===== TARJETA DEL MENÚ (estilo Socios) =====
    JPanel crearBotonMenu(String titulo, String descripcion, String iconPath,
                          Color iconColor, ActionListener action) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CARD, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tamaño fijo (porte Socios)
        Dimension size = new Dimension(280, 150);
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);
        panel.setMaximumSize(size);

        // ICONO (tú controlas la ruta)
        JLabel lblIcono = new JLabel();
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        URL iconURL = getClass().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            lblIcono.setIcon(new ImageIcon(img));
        } else {
            System.err.println("⚠ Icono no encontrado: " + iconPath);
        }

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescripcion = new JLabel(
                "<html><div style='text-align:center;'>" + descripcion + "</div></html>"
        );
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescripcion.setForeground(TEXT_SECONDARY);
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(lblIcono);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblDescripcion);
        panel.add(Box.createVerticalGlue());

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                panel.setBackground(new Color(22, 42, 72));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                panel.setBackground(BG_CARD);
            }
        });

        return panel;
    }

    // ===== CAMBIAR VISTA =====
    /**
     * ✅ MEJORADO: Recarga socios al mostrar panel de registro
     */
    public void mostrar(String vista) {
        System.out.println("📍 Mostrando vista: " + vista);
        
        // ✅ Si va a mostrar el panel de registrar, recargar socios
        if (REGISTRAR.equals(vista) && panelRegistrarBus != null) {
            System.out.println("🔄 Iniciando recarga de socios...");
            panelRegistrarBus.recargarSocios();
        }
        
        cardLayout.show(panelContenido, vista);
        System.out.println("✅ Vista mostrada: " + vista);
    }

    class PanelConFondo extends JPanel {
        private Image imagen;

        public PanelConFondo(String ruta) {
            imagen = new ImageIcon(getClass().getResource(ruta)).getImage();
            setLayout(new GridBagLayout()); // para centrar contenido
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}