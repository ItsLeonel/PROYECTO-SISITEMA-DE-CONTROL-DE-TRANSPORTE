package Presentacion.Ventanas;

import Logica.Servicios.AuditoriaService;
import Logica.Servicios.SessionContext;
import Presentacion.Recursos.SidebarButton;

import Presentacion.Ventanas.Buses.PanelBuses;
import Presentacion.Ventanas.GestionSistema.PanelGestionSistema;
import Presentacion.Ventanas.Transporte.PanelTransporte;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Ventana Principal del Sistema - DISEÑO MODERNO OSCURO
 * Basado en el diseño con fondo de imagen y cards glassmorphism
 */
public class VentanaPrincipal extends JFrame {

    // ========== COLORES DEL DISEÑO ==========
    private static final Color TOP_BAR_BG = new Color(15, 23, 42); // Azul oscuro top bar
    private static final Color SIDEBAR_TOP = new Color(20, 28, 48); // Inicio gradiente sidebar
    private static final Color SIDEBAR_BOTTOM = new Color(15, 23, 42); // Fin gradiente sidebar
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color TEXT_GRAY = new Color(203, 213, 225);

    // === Cards ===
    public static final String CARD_DASHBOARD = "DASHBOARD";
    public static final String CARD_GESTION_SISTEMA = "GESTION_SISTEMA";
    public static final String CARD_BUSES = "BUSES";
    public static final String CARD_TRANSPORTE = "TRANSPORTE";
    public static final String CARD_TURNOS = "TURNOS";
    public static final String CARD_SOCIOS = "SOCIOS";
    public static final String CARD_AUDITORIA = "AUDITORIA";

    private final JPanel panelCentral;
    private final CardLayout cardLayout;
    private final Map<String, SidebarButton> navButtons = new LinkedHashMap<>();

    private final String usuario;
    private final String rol;

    public VentanaPrincipal(String usuario, String rol) {
        this.usuario = usuario;
        this.rol = rol;

        setTitle("SCTET - Sistema de Control de Transporte");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP BAR =====
        add(construirTopBar(), BorderLayout.NORTH);

        // ===== Sidebar =====
        add(construirSidebar(), BorderLayout.WEST);

        // ===== Panel central =====
        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(TOP_BAR_BG);

        // Agregar módulos según permisos
        agregarModulosSegunPermisos();

        add(panelCentral, BorderLayout.CENTER);

        // Seleccionar Dashboard por defecto
        seleccionar(CARD_DASHBOARD);
    }

    // =============================
    // AGREGAR MÓDULOS SEGÚN PERMISOS
    // =============================
    private void agregarModulosSegunPermisos() {
        if (SessionContext.tienePermiso("MOD_DASHBOARD")) {
            safeAddCard(CARD_DASHBOARD, () -> new PanelDashboard(usuario, rol));
        }

        if (SessionContext.tienePermiso("MOD_GESTION_SISTEMA")) {
            safeAddCard(CARD_GESTION_SISTEMA, PanelGestionSistema::new);
        }

        if (SessionContext.tienePermiso("MOD_UNIDADES")) {
            safeAddCard(CARD_BUSES, PanelBuses::new);
        }

        if (SessionContext.tienePermiso("MOD_TRANSPORTE")) {
            safeAddCard(CARD_TRANSPORTE, PanelTransporte::new);
        }

        if (SessionContext.tienePermiso("MOD_TURNOS")) {
            safeAddCard(CARD_TURNOS, PanelTurnos::new);
        }

        if (SessionContext.tienePermiso("MOD_SOCIOS")) {
            safeAddCard(CARD_SOCIOS, () -> new Presentacion.Ventanas.Socios.PanelSocios());
        }

        if (SessionContext.tienePermiso("MOD_AUDITORIA")) {
            safeAddCard(CARD_AUDITORIA, PanelAuditoria::new);
        }
    }

    private void safeAddCard(String name, Supplier<JComponent> factory) {
        try {
            panelCentral.add(factory.get(), name);
        } catch (Throwable ex) {
            panelCentral.add(panelError(name, ex), name);
            ex.printStackTrace();
        }
    }

    private JPanel panelError(String modulo, Throwable ex) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(TOP_BAR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel("Error cargando módulo: " + modulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(Color.WHITE);

        JTextArea area = new JTextArea(ex.toString());
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setForeground(Color.WHITE);
        area.setBackground(new Color(30, 41, 59));

        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // =============================
    // TOP BAR - DISEÑO OSCURO
    // =============================
    private JPanel construirTopBar() {
        JPanel top = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(TOP_BAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        top.setPreferredSize(new Dimension(0, 55));
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 41, 59)));

        // ===== IZQUIERDA: LOGO + TITULO =====
        JLabel logoEmpresa = cargarLogo("/Presentacion/Recursos/logo_empresa.png", 40, 40);

        JLabel titulo = new JLabel("SISTEMA DE CONTROL DE TRANSPORTE");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(TEXT_WHITE);

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        izquierda.setOpaque(false);

        if (logoEmpresa != null)
            izquierda.add(logoEmpresa);
        izquierda.add(titulo);

        // ===== DERECHA: USUARIO (opcional) =====
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        derecha.setOpaque(false);

        JLabel iconUsuario = cargarLogo("/Presentacion/Recursos/icons/usyer.png", 32, 32);
        if (iconUsuario != null)
            derecha.add(iconUsuario);

        // Descomentar si quieres mostrar usuario y rol
        // JLabel lblUsuario = new JLabel(usuario + " | " + rol);
        // lblUsuario.setForeground(TEXT_GRAY);
        // lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // derecha.add(lblUsuario);

        top.add(izquierda, BorderLayout.WEST);
        top.add(derecha, BorderLayout.EAST);

        return top;
    }

    private JLabel cargarLogo(String path, int maxW, int maxH) {
        var url = getClass().getResource(path);
        if (url == null)
            return null;

        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();

        double scale = Math.min(
                (double) maxW / icon.getIconWidth(),
                (double) maxH / icon.getIconHeight());

        Image scaled = img.getScaledInstance(
                (int) (icon.getIconWidth() * scale),
                (int) (icon.getIconHeight() * scale),
                Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    // =============================
    // SIDEBAR - DISEÑO OSCURO CON GRADIENTE
    // =============================
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente oscuro
                GradientPaint gp = new GradientPaint(
                        0, 0, SIDEBAR_TOP,
                        0, getHeight(), SIDEBAR_BOTTOM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)),
                BorderFactory.createEmptyBorder(20, 0, 20, 0)));

        // ===== Navegación =====
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Agregar opciones de navegación según permisos
        if (SessionContext.tienePermiso("MOD_DASHBOARD")) {
            addNavButton(nav, "Dashboard", CARD_DASHBOARD, "/Presentacion/Recursos/icons/dashboard.png");
        }

        if (SessionContext.tienePermiso("MOD_SOCIOS")) {
            addNavButton(nav, "Socios", CARD_SOCIOS, "/Presentacion/Recursos/icons/socios.png");
        }

        if (SessionContext.tienePermiso("MOD_UNIDADES")) {
            addNavButton(nav, "Buses", CARD_BUSES, "/Presentacion/Recursos/icons/bus.png");
        }

        if (SessionContext.tienePermiso("MOD_TRANSPORTE")) {
            addNavButton(nav, "Transporte", CARD_TRANSPORTE, "/Presentacion/Recursos/icons/transport.png");
        }

        if (SessionContext.tienePermiso("MOD_TURNOS")) {
            addNavButton(nav, "Turnos", CARD_TURNOS, "/Presentacion/Recursos/icons/turnos.png");
        }

        if (SessionContext.tienePermiso("MOD_GESTION_SISTEMA")) {
            addNavButton(nav, "Gestión del Sistema", CARD_GESTION_SISTEMA, "/Presentacion/Recursos/icons/settings.png");
        }

        if (SessionContext.tienePermiso("MOD_AUDITORIA")) {
            addNavButton(nav, "Auditoría", CARD_AUDITORIA, "/Presentacion/Recursos/icons/auditoria.png");
        }

        nav.add(Box.createVerticalGlue());

        // Botón Cerrar Sesión
        JButton logout = crearBotonNav("Cerrar sesión", "/Presentacion/Recursos/icons/logout.png");
        logout.addActionListener(e -> cerrarSesion());
        nav.add(logout);
        nav.add(Box.createVerticalStrut(10));

        sidebar.add(nav, BorderLayout.CENTER);
        return sidebar;
    }

    private void addNavButton(JPanel nav, String text, String card, String iconPath) {
        JButton btn = crearBotonNav(text, iconPath);
        btn.addActionListener(e -> seleccionar(card));

        // Guardar referencia para cambiar estado seleccionado
        SidebarButton sidebarBtn = new SidebarButton(text, iconPath);
        sidebarBtn.addActionListener(e -> seleccionar(card));
        navButtons.put(card, sidebarBtn);

        nav.add(btn);
        nav.add(Box.createVerticalStrut(5));
    }

    private JButton crearBotonNav(String texto, String iconPath) {
        JButton btn = new JButton(" " + texto) {
            private boolean hover = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo hover
                if (hover) {
                    g2.setColor(new Color(30, 41, 59, 200));
                    g2.fillRoundRect(8, 0, getWidth() - 16, getHeight(), 8, 8);
                }

                super.paintComponent(g);
            }

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }
        };

        // Cargar icono
        ImageIcon icon = cargarIcono(iconPath, 20, 20);
        if (icon != null) {
            btn.setIcon(icon);
        }

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(230, 42));
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        return btn;
    }

    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        try {
            var url = getClass().getResource(ruta);
            if (url == null)
                return null;

            Image img = new ImageIcon(url).getImage();
            Image scaled = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    private void seleccionar(String card) {
        navButtons.forEach((k, v) -> v.setSelectedState(k.equals(card)));
        cardLayout.show(panelCentral, card);
    }

    // =============================
    // LOGOUT
    // =============================
    private void cerrarSesion() {
        if (!SessionContext.isLogged()) {
            JOptionPane.showMessageDialog(this, "No se pudo cerrar sesión: no existe una sesión activa.");
            return;
        }

        AuditoriaService.registrar("Sistema", "LOGOUT", "OK", "Usuario=" + usuario + ", Rol=" + rol);
        SessionContext.logout();

        JOptionPane.showMessageDialog(this, "Sesión cerrada correctamente.");

        dispose();
        new VentanaLogin().setVisible(true);
    }
}