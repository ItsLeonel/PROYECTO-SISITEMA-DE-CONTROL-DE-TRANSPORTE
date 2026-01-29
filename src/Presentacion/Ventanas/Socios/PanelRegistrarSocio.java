package Presentacion.Ventanas.Socios;

import Logica.Entidades.Socio;
import Logica.Servicios.ResultadoOperacion;
import Logica.Servicios.SocioService;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para registrar un nuevo socio propietario
 * Requisito: rso1v1.0
 */
public class PanelRegistrarSocio extends JPanel {

    private SocioService socioService;

    // Campos del formulario
    private JTextField txtCodigoSocio;
    private JTextField txtRegistroMunicipal;
    private JTextField txtCedula;
    private JTextField txtNombresCompletos;
    private JTextField txtDireccion;
    private JTextField txtCelular;
    
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(15, 23, 42); // azul oscuro elegante

    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    public PanelRegistrarSocio() {
        this.socioService = new SocioService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel central con formulario
        // ===== PANEL SUPERIOR: VOLVER + TÍTULO =====
JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
panelSuperior.setOpaque(true);
panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));


panelSuperior.setBackground(new Color(245, 247, 250));

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

JLabel lblTitulo = new JLabel("Nuevo Socio Propietario");
lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
lblTitulo.setForeground(PRIMARY_COLOR);

panelSuperior.add(btnVolver);
panelSuperior.add(lblTitulo);

// Agregar al panel principal
add(panelSuperior, BorderLayout.NORTH);

// Panel central con formulario (SIN CAMBIOS)
add(construirFormulario(), BorderLayout.CENTER);

    }

    /**
     * Construir formulario de registro
     */
private JPanel construirFormulario() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
    ));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    int fila = 0;

    // ===== FILA 1: CÓDIGO SOCIO =====
    gbc.gridx = 0;
    gbc.gridy = fila;
    panel.add(crearLabel("Código Socio (2 díg):"), gbc);

    gbc.gridx = 1;
    txtCodigoSocio = crearTextField(5);
    panel.add(txtCodigoSocio, gbc);

    gbc.gridy = ++fila;
    panel.add(crearHint("Numérico, exactamente 2 dígitos (00–99)"), gbc);

    // ===== FILA 2: REGISTRO MUNICIPAL =====
    gbc.gridx = 2;
    gbc.gridy = fila - 1;
    panel.add(crearLabel("Reg. Municipal:"), gbc);

    gbc.gridx = 3;
    txtRegistroMunicipal = crearTextField(8);
    panel.add(txtRegistroMunicipal, gbc);

    gbc.gridy = fila;
    panel.add(crearHint("Numérico, exactamente 4 dígitos"), gbc);

    // ===== FILA 3: CÉDULA =====
    fila++;
    gbc.gridx = 0;
    gbc.gridy = fila;
    panel.add(crearLabel("Cédula Identidad:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 3;
    txtCedula = crearTextField(15);
    txtCedula.setToolTipText("Cédula ecuatoriana válida de 10 dígitos");
    panel.add(txtCedula, gbc);

    fila++;
    gbc.gridy = fila;
    panel.add(crearHint("10 dígitos numéricos, provincia válida (01–24), sin guiones"), gbc);
    gbc.gridwidth = 1;

    // ===== FILA 4: NOMBRES =====
    fila++;
    gbc.gridx = 0;
    gbc.gridy = fila;
    panel.add(crearLabel("Nombres Completos:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 3;
    txtNombresCompletos = crearTextField(30);
    panel.add(txtNombresCompletos, gbc);

    fila++;
    gbc.gridy = fila;
    panel.add(crearHint("Solo letras y espacios, se permite ñ, máx. 100 caracteres"), gbc);
    gbc.gridwidth = 1;

    // ===== FILA 5: DIRECCIÓN =====
    fila++;
    gbc.gridx = 0;
    gbc.gridy = fila;
    panel.add(crearLabel("Dirección:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 3;
    txtDireccion = crearTextField(30);
    panel.add(txtDireccion, gbc);

    fila++;
    gbc.gridy = fila;
    panel.add(crearHint("Calle principal y secundaria, letras, números y espacios (sin símbolos)"), gbc);
    gbc.gridwidth = 1;

    // ===== FILA 6: CELULAR =====
    fila++;
    gbc.gridx = 0;
    gbc.gridy = fila;
    panel.add(crearLabel("Celular (10 díg):"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 3;
    txtCelular = crearTextField(15);
    panel.add(txtCelular, gbc);

    fila++;
    gbc.gridy = fila;
    panel.add(crearHint("Debe iniciar con 09 y contener exactamente 10 dígitos"), gbc);
    gbc.gridwidth = 1;

    // ===== SEPARADOR =====
    fila++;
    gbc.gridx = 0;
    gbc.gridy = fila;
    gbc.gridwidth = 4;
    gbc.insets = new Insets(20, 10, 20, 10);
    panel.add(new JSeparator(), gbc);

    // ===== BOTONES =====
    fila++;
    gbc.gridy = fila;
    gbc.insets = new Insets(10, 10, 10, 10);
    panel.add(construirPanelBotones(), gbc);

    return panel;
}


    /**
     * Crear label con estilo
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    /**
     * Construir panel de botones
     */
    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        // Botón Cancelar
        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        btnCancelar.addActionListener(e -> limpiarFormulario());

        // Botón Guardar
        JButton btnGuardar = new JButton("GUARDAR SOCIO");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(SUCCESS_COLOR);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.addActionListener(e -> registrarSocio());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    /**
     * Registrar socio - Requisito rso1v1.0
     */
    private void registrarSocio() {
        // Obtener datos del formulario
        String codigoSocio = txtCodigoSocio.getText().trim();
        String registroMunicipal = txtRegistroMunicipal.getText().trim();
        String cedula = txtCedula.getText().trim();
        String nombresCompletos = txtNombresCompletos.getText().trim().toUpperCase();
        String direccion = txtDireccion.getText().trim().toUpperCase();
        String celular = txtCelular.getText().trim();

        // Validar que no estén vacíos
        if (codigoSocio.isEmpty() || registroMunicipal.isEmpty() || cedula.isEmpty() ||
                nombresCompletos.isEmpty() || direccion.isEmpty() || celular.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto Socio
        Socio socio = new Socio();
        socio.setCodigoSocio(codigoSocio);
        socio.setRegistroMunicipal(registroMunicipal);
        socio.setCedula(cedula);
        socio.setNombresCompletos(nombresCompletos);
        socio.setDireccion(direccion);
        socio.setNumeroCelular(celular);

        // Registrar mediante el servicio
        ResultadoOperacion resultado = socioService.registrarSocio(socio);

        // Mostrar mensaje según el resultado
        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private JLabel crearHint(String texto) {
    JLabel hint = new JLabel(texto);
    hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
    hint.setForeground(new Color(140, 140, 140));
    return hint;
}


    /**
     * Limpiar formulario
     */
    private void limpiarFormulario() {
        txtCodigoSocio.setText("");
        txtRegistroMunicipal.setText("");
        txtCedula.setText("");
        txtNombresCompletos.setText("");
        txtDireccion.setText("");
        txtCelular.setText("");
        txtCodigoSocio.requestFocus();
    }
}