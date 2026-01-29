package Presentacion.Recursos;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    private int radius;
    private Color background = new Color(0, 0, 0, 150);
    private Color border = new Color(255, 255, 255, 80);

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    public void setPanelBackground(Color c) {
        background = c;
    }

    public void setPanelBorder(Color c) {
        border = c;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // sombra
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, radius, radius);

        // fondo
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);

        // borde
        g2.setColor(border);
        g2.drawRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
