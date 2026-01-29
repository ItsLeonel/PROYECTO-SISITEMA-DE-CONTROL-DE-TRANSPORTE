package Presentacion.Ventanas.Buses;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.CardLayout;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panel Principal del Módulo de Buses
 * Menú de navegación con botones grandes estilo azul marino
 */
public class PanelBuses extends JPanel {

    private CardLayout cardLayout;
    private JPanel panelContenido;
    
    // Colores del tema azul marino
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color BTN_PRIMARY = new Color(33, 90, 190);
    private static final Color BTN_SUCCESS = new Color(40, 167, 69);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);

    public PanelBuses() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // Header del módulo
        add(construirHeader(), BorderLayout.NORTH);

        // Menú de navegación
        add(construirMenu(), BorderLayout.CENTER);
    }

    /**
     * Construir header del módulo
     */
    private JPanel construirHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(BTN_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título
        JLabel titulo = new JLabel("Gestión de Buses");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        // Subtítulo
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

    /**
     * Construir menú principal con botones grandes
     */
    private JPanel construirMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Fila 1: Registrar Bus | Consultar Bus
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(crearBotonMenu(
            "Registrar Bus",
            "Registrar un nuevo bus asignándolo a un socio propietario",
            "➕",
            BTN_SUCCESS,
            e -> abrirPanel(new PanelRegistrarBus())
        ), gbc);

        gbc.gridx = 1;
        panel.add(crearBotonMenu(
            "Consultar Bus",
            "Buscar y ver detalles de un bus por placa",
            "🔍",
            BTN_PRIMARY,
            e -> abrirPanel(new PanelConsultarBus())
        ), gbc);

        // Fila 2: Listar Flota | Gestionar Estados
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(crearBotonMenu(
            "Listar Flota",
            "Ver tabla completa con todos los buses y sus propietarios",
            "📋",
            BTN_PRIMARY,
            e -> abrirPanel(new PanelListarBuses())
        ), gbc);

        gbc.gridx = 1;
        panel.add(crearBotonMenu(
            "Gestionar Estados",
            "Activar, desactivar o enviar buses a mantenimiento",
            "⚙️",
            BTN_PRIMARY,
            e -> abrirPanel(new PanelGestionarEstados())
        ), gbc);

        // Fila 3: Asignar Bases | Exportar
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(crearBotonMenu(
            "Asignar Bases",
            "Cambiar la base operativa asignada a un bus",
            "📍",
            BTN_PRIMARY,
            e -> abrirPanel(new PanelAsignarBases())
        ), gbc);

        gbc.gridx = 1;
        panel.add(crearBotonMenu(
            "Exportar Listados",
            "Exportar listados de buses a Excel",
            "📤",
            new Color(0, 150, 136),
            e -> abrirPanel(new PanelExportarBuses())
        ), gbc);

        return panel;
    }

    /**
     * Crear un botón de menú con estilo
     */
    private JPanel crearBotonMenu(String titulo, String descripcion, String icono, 
                                  Color colorFondo, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 0));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(45, 80, 130), 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icono grande
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcono.setForeground(colorFondo);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setPreferredSize(new Dimension(80, 80));

        // Textos
        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblDescripcion = new JLabel("<html>" + descripcion + "</html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(TEXT_SECONDARY);

        panelTextos.add(lblTitulo);
        panelTextos.add(Box.createVerticalStrut(8));
        panelTextos.add(lblDescripcion);

        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(panelTextos, BorderLayout.CENTER);

        // Hacer clic en cualquier parte del panel
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(null);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(25, 45, 75));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(BG_PANEL);
            }
        });

        return panel;
    }

    /**
     * Abrir un panel específico
     */
    private void abrirPanel(JPanel panel) {
        // Crear nueva ventana para el panel
        JFrame frame = new JFrame();
        frame.setTitle("Módulo de Buses");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);
    }
}