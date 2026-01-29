package Presentacion.Ventanas.Socios;

import Logica.Servicios.ResultadoOperacion;
import Logica.Servicios.SocioService;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para actualizar datos del socio propietario
 * Requisitos: rso3v1.0 (actualizar celular), rso4v1.0 (actualizar dirección)
 */
public class PanelActualizarSocio extends JPanel {
    
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    //private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    //private static final Color SECONDARY_COLOR = new Color(108, 117, 125);

    private SocioService socioService;
    private JTabbedPane tabbedPane;

    public PanelActualizarSocio() {
        this.socioService = new SocioService();
    
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
 // ===== PANEL SUPERIOR: VOLVER + TÍTULO =====
JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
panelTitulo.setBackground(new Color(245, 247, 250));

// Botón Volver
JButton btnVolver = new JButton("Volver");
btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
btnVolver.setFocusPainted(false);
btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

btnVolver.addActionListener(e -> {
    Container parent = SwingUtilities.getAncestorOfClass(PanelSocios.class, this);
    if (parent instanceof PanelSocios) {
        ((PanelSocios) parent).mostrarVista(PanelSocios.MENU);
    }
});

// Título
JLabel lblTitulo = new JLabel("Actualizar Datos del Socio");
lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
lblTitulo.setForeground(PRIMARY_COLOR);

// Agregar al panel
panelTitulo.add(btnVolver);
panelTitulo.add(lblTitulo);

// Colocar arriba
add(panelTitulo, BorderLayout.NORTH);


        // Pestañas para cada tipo de actualización
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.addTab("📱 Actualizar Celular", construirPanelActualizarCelular());
        tabbedPane.addTab("🏠 Actualizar Dirección", construirPanelActualizarDireccion());

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Panel para actualizar celular - Requisito rso3v1.0
     */
    private JPanel construirPanelActualizarCelular() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Código Socio
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(crearLabel("Código de Socio:"), gbc);

        gbc.gridx = 1;
        JTextField txtCodigoSocio = crearTextField(10);
        panel.add(txtCodigoSocio, gbc);

        // Nuevo número de celular
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(crearLabel("Nuevo Número de Celular:"), gbc);

        gbc.gridx = 1;
        JTextField txtNuevoCelular = crearTextField(15);
        panel.add(txtNuevoCelular, gbc);

        // Botones
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

        JButton btnLimpiar = crearBoton("Limpiar", new Color(108, 117, 125));
        btnLimpiar.addActionListener(e -> {
            txtCodigoSocio.setText("");
            txtNuevoCelular.setText("");
        });

        JButton btnActualizar = crearBoton("Actualizar Celular", PRIMARY_COLOR);
        btnActualizar.addActionListener(e -> {
            String codigo = txtCodigoSocio.getText().trim();
            String nuevoCelular = txtNuevoCelular.getText().trim();

            if (codigo.isEmpty() || nuevoCelular.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Todos los campos son obligatorios.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultadoOperacion resultado = socioService.actualizarNumeroCelular(codigo, nuevoCelular);

            if (resultado.isExito()) {
                JOptionPane.showMessageDialog(panel,
                        resultado.getMensaje(),
                        "Actualización exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                txtCodigoSocio.setText("");
                txtNuevoCelular.setText("");
            } else {
                JOptionPane.showMessageDialog(panel,
                        resultado.getMensaje(),
                        "Error en actualización",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnActualizar);
        panel.add(panelBotones, gbc);

        return panel;
    }

    /**
     * Panel para actualizar dirección - Requisito rso4v1.0
     */
    private JPanel construirPanelActualizarDireccion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Código Socio
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(crearLabel("Código de Socio:"), gbc);

        gbc.gridx = 1;
        JTextField txtCodigoSocio = crearTextField(10);
        panel.add(txtCodigoSocio, gbc);

        // Nueva dirección
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(crearLabel("Nueva Dirección:"), gbc);

        gbc.gridx = 1;
        JTextField txtNuevaDireccion = crearTextField(30);
        panel.add(txtNuevaDireccion, gbc);

        // Botones
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

        JButton btnLimpiar = crearBoton("Limpiar", new Color(108, 117, 125));
        btnLimpiar.addActionListener(e -> {
            txtCodigoSocio.setText("");
            txtNuevaDireccion.setText("");
        });

        JButton btnActualizar = crearBoton("Actualizar Dirección", PRIMARY_COLOR);
        btnActualizar.addActionListener(e -> {
            String codigo = txtCodigoSocio.getText().trim();
            String nuevaDireccion = txtNuevaDireccion.getText().trim().toUpperCase();

            if (codigo.isEmpty() || nuevaDireccion.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Todos los campos son obligatorios.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultadoOperacion resultado = socioService.actualizarDireccion(codigo, nuevaDireccion);

            if (resultado.isExito()) {
                JOptionPane.showMessageDialog(panel,
                        resultado.getMensaje(),
                        "Actualización exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                txtCodigoSocio.setText("");
                txtNuevaDireccion.setText("");
            } else {
                JOptionPane.showMessageDialog(panel,
                        resultado.getMensaje(),
                        "Error en actualización",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnActualizar);
        panel.add(panelBotones, gbc);

        return panel;
    }

    /**
     * Crear label con estilo
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    /**
     * Crear campo de texto con estilo
     */
    private JTextField crearTextField(int columnas) {
        JTextField field = new JTextField(columnas);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    /**
     * Crear botón con estilo
     */
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(170, 38));
        return boton;
    }
}