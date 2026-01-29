package Presentacion.Recursos;

import javax.swing.*;
import java.awt.*;

public class SidebarButton extends JButton {

    private boolean selected = false;

    // 🎨 Colores (solo texto)
    private static final Color FG_NORMAL = new Color(210, 225, 255);
    private static final Color FG_SELECTED = Color.WHITE;

    public SidebarButton(String text, String iconPath) {
        super(text);

        // Icono grande
        if (iconPath != null) {
            setIcon(cargarIcono(iconPath, 26, 26));
        }

        setHorizontalAlignment(LEFT);
        setIconTextGap(16);

        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setForeground(FG_NORMAL);

        // 🔴 SIN FONDO, SIN HOVER, SIN RESALTADO
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Botón alto (aprovecha espacio)
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
    }

    // Estado seleccionado (solo cambia texto)
    public void setSelectedState(boolean selected) {
        this.selected = selected;
        setForeground(selected ? FG_SELECTED : FG_NORMAL);
        repaint();
    }

    private ImageIcon cargarIcono(String path, int w, int h) {
        var url = getClass().getResource(path);
        if (url == null) return null;

        Image img = new ImageIcon(url).getImage()
                .getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
