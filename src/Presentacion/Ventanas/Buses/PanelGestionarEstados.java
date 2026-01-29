package Presentacion.Ventanas.Buses;

import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panel para gestionar estados de buses
 * Requisitos: ru13v1.2, ru14v1.2, ru15v1.2
 */
public class PanelGestionarEstados extends JPanel {
    private BusService busService;
    private JTextField txtPlaca;
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color BTN_ACTIVO = new Color(46, 204, 113);
    private static final Color BTN_INACTIVO = new Color(231, 76, 60);
    private static final Color BTN_MANT = new Color(241, 196, 15);

    public PanelGestionarEstados() {
        this.busService = new BusService();
        setLayout(new GridBagLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Gestionar Estados de Buses");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel lblPlaca = new JLabel("Placa del Bus:");
        lblPlaca.setForeground(new Color(190, 200, 215));
        panel.add(lblPlaca, gbc);

        gbc.gridx = 1;
        txtPlaca = new JTextField(15);
        txtPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPlaca.setBackground(new Color(21, 44, 82));
        txtPlaca.setForeground(Color.WHITE);
        txtPlaca.setCaretColor(Color.WHITE);
        panel.add(txtPlaca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);

        JButton btnActivo = crearBotonEstado("Activar", BTN_ACTIVO);
        btnActivo.addActionListener(e -> cambiarEstado("ACTIVO"));

        JButton btnInactivo = crearBotonEstado("Desactivar", BTN_INACTIVO);
        btnInactivo.addActionListener(e -> cambiarEstado("INACTIVO"));

        JButton btnMant = crearBotonEstado("Mantenimiento", BTN_MANT);
        btnMant.setForeground(Color.BLACK);
        btnMant.addActionListener(e -> cambiarEstado("MANTENIMIENTO"));

        panelBotones.add(btnActivo);
        panelBotones.add(btnInactivo);
        panelBotones.add(btnMant);
        panel.add(panelBotones, gbc);

        add(panel);
    }

    private void cambiarEstado(String estado) {
        String placa = txtPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la placa del bus.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoOperacion resultado;
        if ("ACTIVO".equals(estado)) {
            resultado = busService.activarBus(placa);
        } else if ("INACTIVO".equals(estado)) {
            resultado = busService.desactivarBus(placa);
        } else {
            resultado = busService.mantenimientoBus(placa);
        }

        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtPlaca.setText("");
        } else {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBotonEstado(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(150, 40));
        return boton;
    }
}