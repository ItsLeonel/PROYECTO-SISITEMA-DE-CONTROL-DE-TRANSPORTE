package Presentacion.Ventanas;

import Presentacion.Recursos.RoundedPanel;
import Presentacion.Recursos.UITheme;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelDashboard extends JPanel {

    public PanelDashboard(String usuario, String rol) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // ===================== HEADER =====================
        RoundedPanel header = new RoundedPanel(18);
        header.setPanelBackground(UITheme.CARD);
        header.setLayout(new BorderLayout(12, 12));
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard");
        title.setFont(UITheme.H1);
        title.setForeground(UITheme.TEXT);

        JLabel subtitle = new JLabel("Acceso rápido a módulos y métricas del sistema.");
        subtitle.setFont(UITheme.BODY);
        subtitle.setForeground(UITheme.MUTED);

        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);

        RoundedPanel userCard = new RoundedPanel(16);
        userCard.setPanelBackground(new Color(248, 250, 255));
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        userCard.setPreferredSize(new Dimension(260, 0));

        JLabel userName = new JLabel(usuario);
        userName.setFont(UITheme.H2);
        userName.setForeground(UITheme.TEXT);

        JLabel userRole = new JLabel(rol);
        userRole.setFont(UITheme.SMALL);
        userRole.setForeground(UITheme.MUTED);

        userCard.add(userName);
        userCard.add(Box.createVerticalStrut(4));
        userCard.add(userRole);

        header.add(left, BorderLayout.CENTER);
        header.add(userCard, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===================== CENTRO: KPIs + FONDO =====================
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        
        FondoImagenPanel fondo = new FondoImagenPanel("/Presentacion/Recursos/empresa_fondo.png");
        fondo.setLayout(new BorderLayout(0, 12));
        fondo.setOpaque(false);

        // KPIs arriba, encima del fondo
        JPanel kpis = new JPanel(new GridLayout(1, 3, 12, 12));
        kpis.setOpaque(false);

        kpis.add(kpiCard("Mes actual", "BORRADOR", UITheme.PRIMARY));
        kpis.add(kpiCard("Buses activos", "12", UITheme.SUCCESS));
        kpis.add(kpiCard("Incidencias recientes", "3", UITheme.WARNING));

        // margen para que no pegue arriba
        JPanel kpiWrap = new JPanel(new BorderLayout());
        kpiWrap.setOpaque(false);
        kpiWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        kpiWrap.add(kpis, BorderLayout.NORTH);

        fondo.add(kpiWrap, BorderLayout.NORTH);

        // Si quieres un leve velo para que se vea más “pro”
        // (opcional) puedes descomentar esto:
        
        JPanel veil = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        veil.setOpaque(false);
        veil.setLayout(new BorderLayout());
        veil.add(kpiWrap, BorderLayout.NORTH);
        fondo.add(veil, BorderLayout.CENTER);
        
        center.add(fondo, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    // ===================== KPI CARD =====================
    private RoundedPanel kpiCard(String label, String value, Color accent) {
        RoundedPanel card = new RoundedPanel(18);
        card.setPanelBackground(UITheme.CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.SMALL);
        lbl.setForeground(UITheme.MUTED);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));
        val.setForeground(UITheme.TEXT);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lbl, BorderLayout.WEST);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setBackground(accent);
        dot.setOpaque(true);

        RoundedPanel accentBox = new RoundedPanel(12);
        accentBox.setPanelBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
        accentBox.setPanelBorder(new Color(0, 0, 0, 0));
        accentBox.setLayout(new GridBagLayout());
        accentBox.setPreferredSize(new Dimension(44, 44));
        accentBox.add(dot);

        top.add(accentBox, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);

        return card;
    }

    // ===================== PANEL FONDO CON IMAGEN =====================
    private static class FondoImagenPanel extends JPanel {
        private final Image image;

        FondoImagenPanel(String resourcePath) {
            Image img = null;
            try {
                URL url = getClass().getResource(resourcePath);
                if (url != null) img = new ImageIcon(url).getImage();
            } catch (Exception ignored) {}
            this.image = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) {
                // Placeholder si no existe la imagen
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(180, 180, 180));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.drawString("Fondo empresa no encontrado: /Presentacion/Recursos/empresa_fondo.png", 20, 30);
                g2.dispose();
                return;
            }

            int w = getWidth();
            int h = getHeight();

            int iw = image.getWidth(this);
            int ih = image.getHeight(this);
            if (iw <= 0 || ih <= 0) return;

            // “cover”: escala para cubrir todo (puede recortar)
            double scale = Math.max((double) w / iw, (double) h / ih);
            int nw = (int) (iw * scale);
            int nh = (int) (ih * scale);

            int x = (w - nw) / 2;
            int y = (h - nh) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, x, y, nw, nh, this);

            // Opcional: velo suave para que la UI se lea mejor encima
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRect(0, 0, w, h);

            g2.dispose();
        }
    }
}
