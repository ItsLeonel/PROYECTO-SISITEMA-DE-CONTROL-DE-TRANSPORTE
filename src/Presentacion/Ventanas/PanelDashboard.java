package Presentacion.Ventanas;

import Logica.Servicios.SessionContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Dashboard con Diseño Glassmorphism Azul
 * Paneles flotantes semitransparentes sobre imagen de fondo
 */
public class PanelDashboard extends JPanel {

    // ===== PALETA NAVY/AZUL OSCURO (como Gestión de Buses) =====
    private static final Color AZUL_OSCURO = new Color(15, 23, 42);        // Fondo base muy oscuro
    private static final Color AZUL_CARD = new Color(30, 58, 90, 200);     // Cards navy semitransparentes
    private static final Color AZUL_HOVER = new Color(40, 70, 110, 220);   // Hover
    private static final Color AZUL_ACENTO = new Color(59, 130, 246);      // Acentos azul brillante
    private static final Color AZUL_CLARO = new Color(148, 163, 184);      // Texto secundario gris-azul
    private static final Color BLANCO = new Color(248, 250, 252);          // Texto principal

    private final String usuario;
    private final String rol;
    
    private int totalBuses = 0;
    private int busesActivos = 0;
    private int totalSocios = 0;
    private int sociosActivos = 0;

    public PanelDashboard(String usuario, String rol) {
        this.usuario = usuario;
        this.rol = rol;
        
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(AZUL_OSCURO);

        cargarEstadisticas();

        // Panel con fondo
        JPanel fondoPanel = new PanelConImagenDeFondo("/Presentacion/Recursos/fondo_buses.png");
        fondoPanel.setLayout(new BorderLayout());
        fondoPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        contenido.add(crearHeader());
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(crearKPIs());
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(crearAccesosRapidos());

        fondoPanel.add(contenido, BorderLayout.NORTH);
        add(fondoPanel, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = crearPanelGlass();
        header.setLayout(new BorderLayout(20, 0));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Bienvenido, " + usuario);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(BLANCO);

        JLabel subtitulo = new JLabel("Panel de Control del Sistema de Transporte");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(AZUL_CLARO);

        String fecha = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"))
        );
        JLabel lblFecha = new JLabel(fecha.substring(0, 1).toUpperCase() + fecha.substring(1));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFecha.setForeground(new Color(226, 232, 240));

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(8));
        izq.add(subtitulo);
        izq.add(Box.createVerticalStrut(8));
        izq.add(lblFecha);

        JPanel der = crearPanelGlass();
        der.setLayout(new BoxLayout(der, BoxLayout.Y_AXIS));
        der.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        der.setPreferredSize(new Dimension(280, 0));
        der.setBackground(new Color(30, 58, 138, 120));

        JLabel lblUsuario = new JLabel(usuario);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUsuario.setForeground(BLANCO);

        JLabel lblRol = new JLabel(rol);
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRol.setForeground(AZUL_CLARO);

        JLabel lblModulos = new JLabel(SessionContext.getPermisos().size() + " módulos disponibles");
        lblModulos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblModulos.setForeground(new Color(203, 213, 225));

        der.add(lblUsuario);
        der.add(Box.createVerticalStrut(6));
        der.add(lblRol);
        der.add(Box.createVerticalStrut(6));
        der.add(lblModulos);

        header.add(izq, BorderLayout.CENTER);
        header.add(der, BorderLayout.EAST);

        return header;
    }

    private JPanel crearKPIs() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 16, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        panel.add(crearKPI("Buses Totales", String.valueOf(totalBuses), 
            busesActivos + " activos", "/Presentacion/Recursos/icons/bus.png", AZUL_ACENTO));
        panel.add(crearKPI("Socios Propietarios", String.valueOf(totalSocios), 
            sociosActivos + " activos", "/Presentacion/Recursos/icons/socios.png", new Color(16, 185, 129)));
        panel.add(crearKPI("Rutas Activas", "8", 
            "En operación", "/Presentacion/Recursos/icons/transport.png", new Color(245, 158, 11)));
        panel.add(crearKPI("Sistema", "OK", 
            "Servicios activos", "/Presentacion/Recursos/icons/settings.png", new Color(139, 92, 246)));

        return panel;
    }

    private JPanel crearKPI(String titulo, String valor, String detalle, String iconPath, Color colorAcento) {
        JPanel card = crearPanelGlass();
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Solo textos, sin iconos
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(AZUL_CLARO);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblValor.setForeground(BLANCO);

        JLabel lblDetalle = new JLabel(detalle);
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalle.setForeground(new Color(203, 213, 225));

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(12));
        textos.add(lblValor);
        textos.add(Box.createVerticalStrut(6));
        textos.add(lblDetalle);

        card.add(textos, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearAccesosRapidos() {
        JPanel container = new JPanel(new GridLayout(1, 2, 16, 0));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Panel izquierdo: Condiciones de Operación (Clima)
        JPanel panelClima = crearPanelGlass();
        panelClima.setLayout(new BorderLayout(0, 18));
        panelClima.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel tituloClima = new JLabel("🌤️ Condiciones de Operación");
        tituloClima.setFont(new Font("Segoe UI", Font.BOLD, 19));
        tituloClima.setForeground(BLANCO);

        JPanel contenidoClima = new JPanel();
        contenidoClima.setOpaque(false);
        contenidoClima.setLayout(new BoxLayout(contenidoClima, BoxLayout.Y_AXIS));

        // Ubicación
        JLabel lblUbicacion = new JLabel("Quito, Ecuador");
        lblUbicacion.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblUbicacion.setForeground(BLANCO);

        // Temperatura
        JLabel lblTemperatura = new JLabel("18°C | Parcialmente nublado");
        lblTemperatura.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTemperatura.setForeground(AZUL_CLARO);

        // Pronóstico
        JPanel pronostico = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pronostico.setOpaque(false);
        
        JPanel indicadorVerde = new JPanel();
        indicadorVerde.setPreferredSize(new Dimension(14, 14));
        indicadorVerde.setBackground(new Color(16, 185, 129));
        
        JLabel lblPronostico = new JLabel("  Condiciones normales");
        lblPronostico.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPronostico.setForeground(new Color(16, 185, 129));
        
        pronostico.add(indicadorVerde);
        pronostico.add(lblPronostico);

        // Detalles compactos
        JPanel detalles = new JPanel(new GridLayout(3, 1, 0, 10));
        detalles.setOpaque(false);

        detalles.add(crearDetalleClimaSimple("Visibilidad: Buena"));
        detalles.add(crearDetalleClimaSimple("Vientos: 15 km/h"));
        detalles.add(crearDetalleClimaSimple("Lluvia: 20%"));

        // Ensamblar
        contenidoClima.add(lblUbicacion);
        contenidoClima.add(Box.createVerticalStrut(8));
        contenidoClima.add(lblTemperatura);
        contenidoClima.add(Box.createVerticalStrut(20));
        contenidoClima.add(pronostico);
        contenidoClima.add(Box.createVerticalStrut(20));
        contenidoClima.add(detalles);

        panelClima.add(tituloClima, BorderLayout.NORTH);
        panelClima.add(contenidoClima, BorderLayout.CENTER);

        // Panel derecho: Info Sistema (más compacto)
        JPanel panelInfo = crearPanelGlass();
        panelInfo.setLayout(new BorderLayout(0, 18));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel tituloInfo = new JLabel("ℹ️ Información del Sistema");
        tituloInfo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        tituloInfo.setForeground(BLANCO);

        JPanel contenidoInfo = new JPanel();
        contenidoInfo.setOpaque(false);
        contenidoInfo.setLayout(new BoxLayout(contenidoInfo, BoxLayout.Y_AXIS));

        contenidoInfo.add(crearInfoItemSimple("Versión", "SCTET v1.0.0"));
        contenidoInfo.add(Box.createVerticalStrut(18));
        contenidoInfo.add(crearInfoItemSimple("Base de Datos", "MySQL 8.0"));
        contenidoInfo.add(Box.createVerticalStrut(18));
        contenidoInfo.add(crearInfoItemSimple("Sesión Iniciada", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        contenidoInfo.add(Box.createVerticalStrut(20));

        // Estado
        JPanel estado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        estado.setOpaque(false);
        
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(12, 12));
        indicator.setBackground(new Color(16, 185, 129));
        
        JLabel lblEstado = new JLabel("  Conexión estable");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(new Color(16, 185, 129));
        
        estado.add(indicator);
        estado.add(lblEstado);
        contenidoInfo.add(estado);

        panelInfo.add(tituloInfo, BorderLayout.NORTH);
        panelInfo.add(contenidoInfo, BorderLayout.CENTER);

        container.add(panelClima);
        container.add(panelInfo);

        return container;
    }

    private JLabel crearDetalleClimaSimple(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(203, 213, 225));
        return lbl;
    }

    private JPanel crearInfoItemSimple(String etiqueta, String valor) {
        JPanel item = new JPanel();
        item.setOpaque(false);
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEtiqueta.setForeground(AZUL_CLARO);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(BLANCO);

        item.add(lblEtiqueta);
        item.add(Box.createVerticalStrut(4));
        item.add(lblValor);

        return item;
    }

    private JPanel crearPanelGlass() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AZUL_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JLabel cargarIcono(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar icono: " + path);
        }
        return null;
    }

    private void cargarEstadisticas() {
        try (Connection conn = Logica.Conexiones.ConexionBD.conectar()) {
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) as total, SUM(CASE WHEN estado = 'ACTIVO' THEN 1 ELSE 0 END) as activos FROM buses")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalBuses = rs.getInt("total");
                    busesActivos = rs.getInt("activos");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) as total, SUM(CASE WHEN estado = 'ACTIVO' THEN 1 ELSE 0 END) as activos FROM socios_propietarios")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalSocios = rs.getInt("total");
                    sociosActivos = rs.getInt("activos");
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
        }
    }

    private static class PanelConImagenDeFondo extends JPanel {
        private Image imagenFondo;

        public PanelConImagenDeFondo(String rutaImagen) {
            try {
                URL url = getClass().getResource(rutaImagen);
                if (url != null) {
                    imagenFondo = new ImageIcon(url).getImage();
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar imagen: " + rutaImagen);
            }
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(AZUL_OSCURO);
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (imagenFondo != null) {
                int w = getWidth();
                int h = getHeight();
                int iw = imagenFondo.getWidth(this);
                int ih = imagenFondo.getHeight(this);
                
                if (iw > 0 && ih > 0) {
                    double scale = Math.max((double) w / iw, (double) h / ih);
                    int nw = (int) (iw * scale);
                    int nh = (int) (ih * scale);
                    int x = (w - nw) / 2;
                    int y = (h - nh) / 2;
                    
                    g2.drawImage(imagenFondo, x, y, nw, nh, this);
                    
                    // Overlay más oscuro para mejor contraste
                    g2.setColor(new Color(10, 20, 35, 220));
                    g2.fillRect(0, 0, w, h);
                }
            }
            g2.dispose();
        }
    }
}