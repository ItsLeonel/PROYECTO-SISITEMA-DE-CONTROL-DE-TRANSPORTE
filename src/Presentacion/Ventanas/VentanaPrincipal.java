///////////////////LEONELE***********************
package Presentacion.Ventanas;

import Logica.Servicios.AuditoriaService;
import Logica.Servicios.SessionContext;
import Presentacion.Recursos.SidebarButton;
import Presentacion.Recursos.UITheme;
import Presentacion.Ventanas.GestionSistema.PanelGestionSistema;
import Presentacion.Ventanas.Transporte.PanelTransporte;
import Presentacion.Ventanas.Unidades.PanelUnidades;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class VentanaPrincipal extends JFrame {

    // === Cards ===
    public static final String CARD_DASHBOARD = "DASHBOARD";
    public static final String CARD_GESTION_SISTEMA = "GESTION_SISTEMA";
    public static final String CARD_BUSES = "BUSES";
    public static final String CARD_TRANSPORTE = "TRANSPORTE";
    public static final String CARD_TURNOS = "TURNOS";
    public static final String CARD_SANCIONES = "SANCIONES";
    public static final String CARD_AUDITORIA = "AUDITORIA";

    private final JPanel panelCentral;
    private final CardLayout cardLayout;
    private final Map<String, SidebarButton> navButtons = new LinkedHashMap<>();

    private final String usuario;
    private final String rol;
    

 public VentanaPrincipal(String usuario, String rol) {
    this.usuario = usuario;
    this.rol = rol;

    setTitle("");
    setSize(1300, 750);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // ===== TOP BAR GLOBAL (100% ANCHO) ✅
    add(construirTopBar(), BorderLayout.NORTH);

    // ===== Sidebar =====
    add(construirSidebar(), BorderLayout.WEST);

    // ===== Panel central =====
    cardLayout = new CardLayout();
    panelCentral = new JPanel(cardLayout);
    panelCentral.setBackground(new Color(245, 247, 250));

    safeAddCard(CARD_DASHBOARD, () -> new PanelDashboard(usuario, rol));
    safeAddCard(CARD_GESTION_SISTEMA, PanelGestionSistema::new);
    safeAddCard(CARD_BUSES, PanelUnidades::new);
    safeAddCard(CARD_TRANSPORTE, PanelTransporte::new);
    safeAddCard(CARD_TURNOS, PanelTurnos::new);
    safeAddCard(CARD_SANCIONES, PanelSanciones::new);
    safeAddCard(CARD_AUDITORIA, PanelAuditoria::new);

    add(panelCentral, BorderLayout.CENTER);

    seleccionar(CARD_DASHBOARD);
}


    // =============================
    // SEGURIDAD
    // =============================
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
        p.setBackground(new Color(245, 247, 250));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel("Error cargando módulo: " + modulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JTextArea area = new JTextArea(ex.toString());
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));

        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // =============================
    // TOP BAR
    // =============================
private JPanel construirTopBar() {
    JPanel top = new JPanel(new BorderLayout());
    top.setBackground(Color.WHITE);
    top.setPreferredSize(new Dimension(0, 60));
    top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

    // ===== IZQUIERDA: LOGO + TITULO =====
    JLabel logoEmpresa = cargarLogo(
            "/Presentacion/Recursos/logo_empresa.png",
            80,
            80
    );

    JLabel titulo = new JLabel("SISTEMA DE CONTROL DE TRANSPORTE");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
    titulo.setForeground(Color.BLACK);

    JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
    izquierda.setOpaque(false);

    if (logoEmpresa != null) izquierda.add(logoEmpresa);
    izquierda.add(titulo);

    // ===== DERECHA: ICONO USUARIO + TEXTO =====
    JLabel iconUsuario = cargarLogo(
            "/Presentacion/Recursos/icons/user.png",
            40,
            40
    );

    JLabel user = new JLabel(usuario + " | " + rol);
    user.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    user.setForeground(Color.DARK_GRAY);

    JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
    derecha.setOpaque(false);

    if (iconUsuario != null) derecha.add(iconUsuario);
    derecha.add(user);

    // ===== ENSAMBLAR =====
    top.add(izquierda, BorderLayout.WEST);
    top.add(derecha, BorderLayout.EAST);

    return top;
}



    private JLabel cargarLogo(String path, int maxW, int maxH) {
        var url = getClass().getResource(path);
        if (url == null) return null;

        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();

        double scale = Math.min(
                (double) maxW / icon.getIconWidth(),
                (double) maxH / icon.getIconHeight()
        );

        Image scaled = img.getScaledInstance(
                (int) (icon.getIconWidth() * scale),
                (int) (icon.getIconHeight() * scale),
                Image.SCALE_SMOOTH
        );
        return new JLabel(new ImageIcon(scaled));
    }

    // =============================
    // SIDEBAR
    // =============================
    private JPanel construirSidebar() {

        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, UITheme.SB_TOP,
                        0, getHeight(), UITheme.SB_BOTTOM
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));


        // ===== Header del sidebar =====
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel app = new JLabel("SCTET");
        app.setFont(new Font("Segoe UI", Font.BOLD, 18));
        app.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Sistema de Transporte");
        sub.setForeground(new Color(255, 255, 255, 180));

        JLabel ses = new JLabel("Usuario: " + usuario + " | Rol: " + rol);
        ses.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ses.setForeground(new Color(255, 255, 255, 160));

        header.add(app);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        header.add(Box.createVerticalStrut(8));
        header.add(ses);

        sidebar.add(header, BorderLayout.NORTH);

        // ===== Navegación =====
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addNav(nav, "Dashboard", CARD_DASHBOARD,
                "/Presentacion/Recursos/icons/dashboard.png");

        addNav(nav, "Gestión del Sistema", CARD_GESTION_SISTEMA,
                "/Presentacion/Recursos/icons/settings.png");

        addNav(nav, "Unidades", CARD_BUSES,
                "/Presentacion/Recursos/icons/bus.png");

        addNav(nav, "Transporte", CARD_TRANSPORTE,
                "/Presentacion/Recursos/icons/transport.png");

        addNav(nav, "Turnos", CARD_TURNOS,
                "/Presentacion/Recursos/icons/turnos.png");

        addNav(nav, "Sanciones", CARD_SANCIONES,
                "/Presentacion/Recursos/icons/sanciones.png");

        addNav(nav, "Auditoría", CARD_AUDITORIA,
                "/Presentacion/Recursos/icons/auditoria.png");

        nav.add(Box.createVerticalGlue());

        SidebarButton logout = new SidebarButton(
                "Cerrar sesión",
                "/Presentacion/Recursos/icons/logout.png"
        );
        logout.addActionListener(e -> cerrarSesion());
        nav.add(logout);

        sidebar.add(nav, BorderLayout.CENTER);
        return sidebar;
    }

    // =============================
    // NAV
    // =============================
    private void addNav(JPanel nav, String text, String card, String icon) {
        SidebarButton b = new SidebarButton(text, icon);
        b.addActionListener(e -> seleccionar(card));
        navButtons.put(card, b);
        nav.add(b);
        nav.add(Box.createVerticalStrut(6));
    }

    private void seleccionar(String card) {
        navButtons.forEach((k, v) -> v.setSelectedState(k.equals(card)));
        cardLayout.show(panelCentral, card);
    }

    // =============================
    // LOGOUT
    // =============================
 private void cerrarSesion() {

    // 1. Validar sesión activa
    if (!SessionContext.isLogged()) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo cerrar sesión: no existe una sesión activa."
        );
        return;
    }

    // 2. Auditoría
    AuditoriaService.registrar(
            "Sistema",
            "LOGOUT",
            "OK",
            "Usuario=" + usuario + ", Rol=" + rol
    );

    // 3. Cerrar sesión
    SessionContext.logout();

    // 4. Mensaje EXACTO del requisito
    JOptionPane.showMessageDialog(
            this,
            "Sesión cerrada correctamente."
    );

    // 5. Volver a login
    dispose();
    new VentanaLogin().setVisible(true);
}



}

