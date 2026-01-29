package Presentacion.Ventanas.Unidades;
import java.awt.Dimension;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Panel principal del módulo Unidades
 * Navegación mediante botones (sin pestañas)
 * Usa CardLayout para cambiar vistas
 */
public class PanelUnidades extends JPanel {

    // ===== COLORES SISTEMA =====
    private final Color BG_MAIN   = new Color(11, 22, 38);
    private final Color BG_PANEL  = new Color(18, 36, 64);
    private final Color BTN_MAIN  = new Color(33, 90, 190);
    private final Color TXT_SEC   = new Color(190, 200, 215);

    // ===== COMPONENTES =====
    private JPanel panelContenido;
    private CardLayout cardLayout;

    private TabGestionBuses tabGestion;
    private TabConsultaBuses tabConsulta;
    private TabReportes tabReportes;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public PanelUnidades() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
JPanel top = new JPanel(new BorderLayout());
top.setOpaque(false);

top.add(crearHeader(), BorderLayout.NORTH);
top.add(crearBarraBotones(), BorderLayout.SOUTH);

add(top, BorderLayout.NORTH);
add(crearContenido(), BorderLayout.CENTER);

    }

    // =====================================================
    // HEADER
    // =====================================================
    private JPanel crearHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel titulo = new JLabel("Unidades");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JLabel subt = new JLabel("");
        subt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subt.setForeground(TXT_SEC);

        p.add(titulo);
        p.add(Box.createVerticalStrut(6));
        p.add(subt);

        return p;
    }
////////////////////////////////////
    // =====================================================
    // BARRA DE BOTONES (NAVEGACIÓN)
    // =====================================================
 private JPanel crearBarraBotones() {
    JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

    panel.add(botonRegistro()); // 👈 NUEVO
    panel.add(botonModulo("Gestión de Flota", "GESTION"));
    panel.add(botonModulo("Consulta Detallada", "CONSULTA"));
    panel.add(botonModulo("Reportes", "REPORTES"));

    return panel;
}
private JButton botonRegistro() {
    JButton btn = new JButton("Registro de Unidades");

    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);
    btn.setOpaque(true);
    btn.setBackground(new Color(33, 90, 190));
    btn.setForeground(Color.WHITE);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    btn.addActionListener(e -> {
    DialogBus dialog = new DialogBus(
        SwingUtilities.getWindowAncestor(this),
        "Registrar Unidad",
        null
    );
    dialog.setVisible(true);
});


    return btn;
}




    // =====================================================
    // CONTENIDO CENTRAL (CardLayout)
    // =====================================================
    private JPanel crearContenido() {
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setOpaque(false);

        tabGestion = new TabGestionBuses();
        tabConsulta = new TabConsultaBuses();
        tabReportes = new TabReportes();

        panelContenido.add(tabGestion, "GESTION");
        panelContenido.add(tabConsulta, "CONSULTA");
        panelContenido.add(tabReportes, "REPORTES");

        cardLayout.show(panelContenido, "GESTION");

        return panelContenido;
    }

    // =====================================================
    // BOTÓN DE MÓDULO
    // =====================================================
private JButton botonModulo(String texto, String card) {
    JButton btn = new JButton(texto);

    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);
    btn.setOpaque(true);

    // 🔵 MISMO ESTILO QUE USUARIOS / ROLES / PERMISOS
    btn.setBackground(new Color(33, 90, 190));
    btn.setForeground(Color.WHITE);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // 🔒 MISMA ALTURA Y FORMA
    btn.setPreferredSize(new Dimension(220, 44));
    btn.setMinimumSize(new Dimension(220, 44));

    btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

    btn.addActionListener(e -> {
        cardLayout.show(panelContenido, card);
        if ("REPORTES".equals(card)) {
            tabReportes.actualizarEstadisticas();
        }
    });

    return btn;
}


    // =====================================================
    // MÉTODO PÚBLICO DE REFRESCO
    // =====================================================
    public void refrescarTodo() {
        tabGestion.refrescarTabla();
        tabReportes.actualizarEstadisticas();
    }
}
