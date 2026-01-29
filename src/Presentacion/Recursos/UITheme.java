package Presentacion.Recursos;

import java.awt.Color;
import java.awt.Font;

public class UITheme {
    public static final Color BG = new Color(245, 248, 252);

    // Gradiente fondo login
    public static final Color LOGIN_TOP = new Color(13, 37, 94);
    public static final Color LOGIN_BOTTOM = new Color(28, 86, 190);

    // Sidebar
    public static final Color SB_TOP = new Color(15, 48, 120);
    public static final Color SB_BOTTOM = new Color(24, 70, 170);

    // Cards / bordes
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(222, 230, 242);

    // Colores base
    public static final Color PRIMARY = new Color(33, 98, 230);
    public static final Color SUCCESS = new Color(21, 155, 86);
    public static final Color WARNING = new Color(220, 150, 20);

    // Textos
    public static final Color TEXT = new Color(18, 33, 52);
    public static final Color MUTED = new Color(110, 125, 145);

    // Fuentes
    public static final Font H1 = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private UITheme() {}
}

