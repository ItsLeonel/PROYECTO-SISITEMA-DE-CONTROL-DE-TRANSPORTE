package Presentacion.Ventanas.Buses;

import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panel para asignar bases operativas a buses
 * Requisito: ru12v1.1
 */
public class PanelAsignarBases extends JPanel {
    private BusService busService;
    private JTextField txtPlaca;
    private JTextField txtNuevaBase;

    public PanelAsignarBases() {
        this.busService = new BusService();
        setLayout(new GridBagLayout());
        setBackground(new Color(11, 22, 38));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(18, 36, 64));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Asignar Base Operativa");
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
        txtPlaca = crearCampoTexto();
        panel.add(txtPlaca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblBase = new JLabel("Nueva Base:");
        lblBase.setForeground(new Color(190, 200, 215));
        panel.add(lblBase, gbc);

        gbc.gridx = 1;
        txtNuevaBase = crearCampoTexto();
        panel.add(txtNuevaBase, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton btnAsignar = new JButton("Asignar Base");
        btnAsignar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAsignar.setForeground(Color.WHITE);
        btnAsignar.setBackground(new Color(33, 90, 190));
        btnAsignar.setBorderPainted(false);
        btnAsignar.setFocusPainted(false);
        btnAsignar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAsignar.setPreferredSize(new Dimension(180, 40));
        btnAsignar.addActionListener(e -> asignarBase());
        panel.add(btnAsignar, gbc);

        add(panel);
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(new Color(21, 44, 82));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }

    private void asignarBase() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        String nuevaBase = txtNuevaBase.getText().trim();

        if (placa.isEmpty() || nuevaBase.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoOperacion resultado = busService.asignarBase(placa, nuevaBase);

        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            txtPlaca.setText("");
            txtNuevaBase.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}