package Presentacion.Ventanas;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Diálogo modal para mostrar tablas en pantalla completa
 * Soluciona el problema de las tablas pequeñas
 */
public class DialogVentanaGrande extends JDialog {

    public DialogVentanaGrande(JFrame parent, String titulo, JTable tabla) {
        super(parent, titulo, true);

        setLayout(new BorderLayout());
        setSize(1400, 800);
        setLocationRelativeTo(parent);

        // Panel superior con título
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(lblTitulo, BorderLayout.CENTER);

        // Clonar tabla para no afectar original
        JTable tablaGrande = clonarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tablaGrande);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(new Color(22, 44, 86));

        // Panel botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(15, 30, 60));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(231, 76, 60));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setPreferredSize(new Dimension(120, 40));
        btnCerrar.addActionListener(e -> dispose());

        bottomPanel.add(btnCerrar);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(15, 30, 60));
    }

    private JTable clonarTabla(JTable original) {
        JTable tabla = new JTable(original.getModel());
        tabla.setRowHeight(original.getRowHeight());
        tabla.setFont(original.getFont());
        tabla.setForeground(original.getForeground());
        tabla.setBackground(original.getBackground());
        tabla.setShowHorizontalLines(original.getShowHorizontalLines());
        tabla.setShowVerticalLines(original.getShowVerticalLines());
        tabla.setGridColor(original.getGridColor());
        tabla.setDefaultRenderer(Object.class, original.getDefaultRenderer(Object.class));

        JTableHeader header = tabla.getTableHeader();
        header.setDefaultRenderer(original.getTableHeader().getDefaultRenderer());
        header.setReorderingAllowed(false);

        return tabla;
    }
}
