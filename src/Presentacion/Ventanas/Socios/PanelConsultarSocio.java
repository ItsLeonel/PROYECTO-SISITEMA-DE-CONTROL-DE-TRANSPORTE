package Presentacion.Ventanas.Socios;

import Logica.Entidades.Socio;
import Logica.Servicios.ResultadoOperacion;
import Logica.Servicios.SocioService;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para consultar un socio propietario
 * Requisito: rso2v1.0
 */
public class PanelConsultarSocio extends JPanel {
    
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    //private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    //private static final Color SECONDARY_COLOR = new Color(108, 117, 125);

    private SocioService socioService;
    private JTextField txtCodigoSocio;
    private JPanel panelResultado;
    private JLabel lblCodigoSocio, lblRegistroMunicipal, lblCedula;
    private JLabel lblNombres, lblDireccion, lblCelular, lblPlacaBus;

    public PanelConsultarSocio() {
        this.socioService = new SocioService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de consulta
        // ===== PANEL SUPERIOR (VOLVER + CONSULTA) =====
JPanel panelSuperior = new JPanel(new BorderLayout());
panelSuperior.setBackground(new Color(245, 247, 250));
panelSuperior.setOpaque(true);


// ---- Barra superior con botón Volver ----
JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
barra.setBackground(new Color(245, 247, 250));
barra.setOpaque(true);


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

barra.add(btnVolver);

// ---- Panel de consulta original ----
JPanel panelConsulta = construirPanelConsulta();

// Ensamblar
panelSuperior.add(barra, BorderLayout.NORTH);
panelSuperior.add(panelConsulta, BorderLayout.CENTER);

// Colocar arriba
add(panelSuperior, BorderLayout.NORTH);


        // Panel de resultado
        panelResultado = construirPanelResultado();
        panelResultado.setVisible(false);
        add(panelResultado, BorderLayout.CENTER);
    }

    /**
     * Panel de búsqueda
     */
    private JPanel construirPanelConsulta() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel lblTitulo = new JLabel("Consultar Socio Propietario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(PRIMARY_COLOR);

        JLabel lblCodigo = new JLabel("Código de Socio:");
        lblCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtCodigoSocio = new JTextField(10);
        txtCodigoSocio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCodigoSocio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setBackground(PRIMARY_COLOR);
        btnConsultar.setBorderPainted(false);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConsultar.setPreferredSize(new Dimension(130, 35));
        btnConsultar.addActionListener(e -> consultarSocio());

        panel.add(lblTitulo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblCodigo);
        panel.add(txtCodigoSocio);
        panel.add(btnConsultar);

        return panel;
    }

    /**
     * Panel de resultado
     */
    private JPanel construirPanelResultado() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        JLabel lblTitulo = new JLabel("Información del Socio");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Campos de información
        int fila = 1;

        // Código Socio
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Código de Socio:"), gbc);
        gbc.gridx = 1;
        lblCodigoSocio = crearLabelValor("");
        panel.add(lblCodigoSocio, gbc);

        // Registro Municipal
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Registro Municipal:"), gbc);
        gbc.gridx = 1;
        lblRegistroMunicipal = crearLabelValor("");
        panel.add(lblRegistroMunicipal, gbc);

        // Cédula
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Cédula:"), gbc);
        gbc.gridx = 1;
        lblCedula = crearLabelValor("");
        panel.add(lblCedula, gbc);

        // Nombres
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Nombre:"), gbc);
        gbc.gridx = 1;
        lblNombres = crearLabelValor("");
        panel.add(lblNombres, gbc);

        // Dirección
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Dirección:"), gbc);
        gbc.gridx = 1;
        lblDireccion = crearLabelValor("");
        panel.add(lblDireccion, gbc);

        // Celular
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Celular:"), gbc);
        gbc.gridx = 1;
        lblCelular = crearLabelValor("");
        panel.add(lblCelular, gbc);

        // Placa Bus Asociado
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(crearLabelCampo("Placa Bus:"), gbc);
        gbc.gridx = 1;
        lblPlacaBus = crearLabelValor("");
        panel.add(lblPlacaBus, gbc);

        return panel;
    }

    /**
     * Crear label para campo
     */
    private JLabel crearLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    /**
     * Crear label para valor
     */
    private JLabel crearLabelValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    /**
     * Consultar socio - Requisito rso2v1.0
     */
    private void consultarSocio() {
        String codigoSocio = txtCodigoSocio.getText().trim();

        if (codigoSocio.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese el código del socio.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Consultar mediante el servicio
        ResultadoOperacion resultado = socioService.consultarSocioPorCodigo(codigoSocio);

        if (resultado.isExito()) {
            Socio socio = (Socio) resultado.getDatos();
            mostrarInformacionSocio(socio);
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Socio no encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
            panelResultado.setVisible(false);
        }
    }

    /**
     * Mostrar información del socio en el panel de resultado
     */
    private void mostrarInformacionSocio(Socio socio) {
        lblCodigoSocio.setText(socio.getCodigoSocio());
        lblRegistroMunicipal.setText(socio.getRegistroMunicipal());
        lblCedula.setText(socio.getCedula());
        lblNombres.setText(socio.getNombresCompletos());
        lblDireccion.setText(socio.getDireccion());
        lblCelular.setText(socio.getNumeroCelular());
        lblPlacaBus.setText(socio.getPlacaBusAsociado() != null ? 
                socio.getPlacaBusAsociado() : "Sin bus asignado");

        panelResultado.setVisible(true);
    }
}