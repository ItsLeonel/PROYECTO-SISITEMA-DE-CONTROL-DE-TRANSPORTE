package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Entidades.SocioDisponible;
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
import java.util.Calendar;
import java.util.List;

/**
 * Panel para registrar un nuevo bus
 * Requisito: ru1+2+3v1.1
 * Integrado con Módulo de Socios
 */
public class PanelRegistrarBus extends JPanel {

    private BusService busService;

    // Campos del formulario
    private JTextField txtPlaca;
    private JComboBox<SocioDisponible> cmbSocio;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JSpinner spinnerAnio;
    private JSpinner spinnerCapacidad;
    private JTextField txtBase;
    private JComboBox<String> cmbEstado;

    // Colores del tema
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);

    public PanelRegistrarBus() {
        this.busService = new BusService();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel central con formulario
        add(construirFormulario(), BorderLayout.CENTER);
    }

    /**
     * Construir formulario de registro
     */
    private JScrollPane construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del formulario
        JLabel lblTitulo = new JLabel("Registrar Nuevo Bus");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Fila 1: Placa
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(crearLabel("Placa:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtPlaca = crearTextField(15);
        JPanel panelPlaca = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPlaca.setOpaque(false);
        panelPlaca.add(txtPlaca);
        JLabel lblAyudaPlaca = new JLabel("(Formato: PPP-1234, ej: PBD-7777)");
        lblAyudaPlaca.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaPlaca.setForeground(TEXT_SECONDARY);
        panelPlaca.add(lblAyudaPlaca);
        panel.add(panelPlaca, gbc);

        gbc.gridwidth = 1;

        // Fila 2: Socio Propietario (COMBO DINÁMICO)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(crearLabel("Socio Propietario:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        cmbSocio = new JComboBox<>();
        cmbSocio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbSocio.setBackground(new Color(21, 44, 82));
        cmbSocio.setForeground(Color.WHITE);
        cargarSociosDisponibles(); // Cargar dinámicamente
        panel.add(cmbSocio, gbc);

        gbc.gridwidth = 1;

        // Fila 3: Marca y Modelo
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(crearLabel("Marca:"), gbc);

        gbc.gridx = 1;
        txtMarca = crearTextField(15);
        panel.add(txtMarca, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Modelo:"), gbc);

        gbc.gridx = 3;
        txtModelo = crearTextField(15);
        panel.add(txtModelo, gbc);

        // Ayuda para marca y modelo
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        JLabel lblAyudaMarcaModelo = new JLabel("Ej: Marca=HINO, Modelo=AK | Marca=CHEVROLET, Modelo=NQR");
        lblAyudaMarcaModelo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaMarcaModelo.setForeground(TEXT_SECONDARY);
        panel.add(lblAyudaMarcaModelo, gbc);

        gbc.gridwidth = 1;

        // Fila 5: Año y Capacidad
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(crearLabel("Año de Fabricación:"), gbc);

        gbc.gridx = 1;
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        SpinnerNumberModel modeloAnio = new SpinnerNumberModel(anioActual, anioActual - 10, anioActual, 1);
        spinnerAnio = new JSpinner(modeloAnio);
        spinnerAnio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinnerAnio.getEditor()).getTextField().setBackground(new Color(21, 44, 82));
        ((JSpinner.DefaultEditor) spinnerAnio.getEditor()).getTextField().setForeground(Color.WHITE);
        panel.add(spinnerAnio, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Capacidad Pasajeros:"), gbc);

        gbc.gridx = 3;
        SpinnerNumberModel modeloCapacidad = new SpinnerNumberModel(40, 1, 100, 1);
        spinnerCapacidad = new JSpinner(modeloCapacidad);
        spinnerCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinnerCapacidad.getEditor()).getTextField().setBackground(new Color(21, 44, 82));
        ((JSpinner.DefaultEditor) spinnerCapacidad.getEditor()).getTextField().setForeground(Color.WHITE);
        panel.add(spinnerCapacidad, gbc);

        // Fila 6: Base Asignada
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(crearLabel("Base Asignada:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtBase = crearTextField(30);
        JPanel panelBase = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBase.setOpaque(false);
        panelBase.add(txtBase);
        JLabel lblAyudaBase = new JLabel("(Ej: Terminal Norte, Terminal Sur)");
        lblAyudaBase.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaBase.setForeground(TEXT_SECONDARY);
        panelBase.add(lblAyudaBase);
        panel.add(panelBase, gbc);

        gbc.gridwidth = 1;

        // Fila 7: Estado Inicial
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(crearLabel("Estado Inicial:"), gbc);

        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbEstado.setBackground(new Color(21, 44, 82));
        cmbEstado.setForeground(Color.WHITE);
        panel.add(cmbEstado, gbc);

        // Separador
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 10, 20, 10);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(45, 80, 130));
        panel.add(sep, gbc);

        // Botones
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(construirPanelBotones(), gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        return scroll;
    }

    /**
     * Cargar socios disponibles (sin bus asignado) en el combo
     */
    private void cargarSociosDisponibles() {
        ResultadoOperacion resultado = busService.obtenerSociosDisponibles();

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<SocioDisponible> socios = (List<SocioDisponible>) resultado.getDatos();

            cmbSocio.removeAllItems();
            for (SocioDisponible socio : socios) {
                cmbSocio.addItem(socio);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            cmbSocio.removeAllItems();
        }
    }

    /**
     * Crear label con estilo
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /**
     * Crear campo de texto con estilo
     */
    private JTextField crearTextField(int columnas) {
        JTextField field = new JTextField(columnas);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(21, 44, 82));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 1),
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
        JButton btnGuardar = new JButton("REGISTRAR BUS");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(SUCCESS_COLOR);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.addActionListener(e -> registrarBus());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    /**
     * Registrar bus - Requisito ru1+2+3v1.1
     */
    private void registrarBus() {
        // Obtener datos del formulario
        String placa = txtPlaca.getText().trim().toUpperCase();
        SocioDisponible socioSeleccionado = (SocioDisponible) cmbSocio.getSelectedItem();
        String marca = txtMarca.getText().trim().toUpperCase();
        String modelo = txtModelo.getText().trim().toUpperCase();
        int anio = (Integer) spinnerAnio.getValue();
        int capacidad = (Integer) spinnerCapacidad.getValue();
        String base = txtBase.getText().trim();
        String estado = (String) cmbEstado.getSelectedItem();

        // Validar que se haya seleccionado un socio
        if (socioSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un socio propietario.\n" +
                    "Si no aparecen socios, debe registrar uno primero en el Módulo de Socios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar que no estén vacíos
        if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || base.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear objeto Bus
     // Crear objeto Bus
Bus bus = new Bus();
bus.setPlaca(placa);
bus.setCodigoSocioFk(socioSeleccionado.getCodigoSocio());
bus.setMarca(marca);
bus.setModelo(modelo);
bus.setAnioFabricacion(anio);
bus.setCapacidadPasajeros(capacidad);
bus.setBaseAsignada(base);
bus.setEstado(estado);

        // Registrar mediante el servicio
        ResultadoOperacion resultado = busService.registrarBus(bus);

        // Mostrar mensaje según el resultado
        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarSociosDisponibles(); // Recargar combo (el socio ya no estará disponible)
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpiar formulario
     */
    private void limpiarFormulario() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        spinnerAnio.setValue(Calendar.getInstance().get(Calendar.YEAR));
        spinnerCapacidad.setValue(40);
        txtBase.setText("");
        cmbEstado.setSelectedIndex(0);
        if (cmbSocio.getItemCount() > 0) {
            cmbSocio.setSelectedIndex(0);
        }
        txtPlaca.requestFocus();
    }
}