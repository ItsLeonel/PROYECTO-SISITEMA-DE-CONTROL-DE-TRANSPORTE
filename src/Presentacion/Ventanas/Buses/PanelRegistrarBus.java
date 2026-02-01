package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Entidades.SocioDisponible;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

/**
 * Panel para registrar un nuevo bus
 * Requisito: ru1+2+3v1.1
 * ✅ CORREGIDO: Controles interactivos y formato de placa CON guion
 */
public class PanelRegistrarBus extends JPanel {

    private BusService busService;
    private PanelBuses parent; 

    // Campos del formulario
    private JTextField txtPlaca;
    private JComboBox<String> cmbSocio;  // ✅ SOLO ID del socio
    private JLabel lblCantidadSocios;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JSpinner spinnerAnio;         // ✅ Spinner para año
    private JSpinner spinnerCapacidad;    // ✅ Spinner para capacidad
    private JComboBox<String> cmbBase;    // ✅ ComboBox para base
    private JComboBox<String> cmbEstado;  // ✅ ComboBox para estado
    private JFormattedTextField txtFechaIngreso;  // ✅ Fecha de ingreso

    // Colores del tema
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);
    
    // ✅ Bases disponibles (hardcoded según requisitos)
    private static final String[] BASES_DISPONIBLES = {"Base Norte", "Base Sur"};
    
    // ✅ Estados disponibles
    private static final String[] ESTADOS_DISPONIBLES = {"ACTIVO", "INACTIVO", "MANTENIMIENTO"};

    public PanelRegistrarBus(PanelBuses parent) {
        this.parent = parent;
        this.busService = new BusService();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        add(construirFormulario(), BorderLayout.CENTER);

        JButton btnVolver = new JButton(" Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(PRIMARY_COLOR);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setPreferredSize(new Dimension(120, 35));
        btnVolver.addActionListener(e -> parent.mostrar(PanelBuses.MENU));
        
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSur.setOpaque(false);
        panelSur.add(btnVolver);
        add(panelSur, BorderLayout.SOUTH);

        // Recargar socios cuando sea visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                cargarSociosDisponibles();
            }
        });
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

        // Título
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

        // ===== FILA 1: PLACA =====
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

        // ===== FILA 2: CÓDIGO SOCIO (SOLO ID) =====
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(crearLabel("Código Socio:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JPanel panelSocio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelSocio.setOpaque(false);
        
        cmbSocio = new JComboBox<>();
        cmbSocio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbSocio.setBackground(new Color(21, 44, 82));
        cmbSocio.setForeground(Color.WHITE);
        cmbSocio.setPreferredSize(new Dimension(150, 35));
        cargarSociosDisponibles();
        panelSocio.add(cmbSocio);
        
        lblCantidadSocios = new JLabel();
        lblCantidadSocios.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCantidadSocios.setForeground(TEXT_SECONDARY);
        actualizarContadorSocios();
        panelSocio.add(lblCantidadSocios);
        
        panel.add(panelSocio, gbc);
        gbc.gridwidth = 1;

        // ===== FILA 3: MARCA Y MODELO =====
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

        // Ayuda
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        JLabel lblAyudaMarcaModelo = new JLabel("Marca: máx 15 alfabéticos | Modelo: máx 15 alfanuméricos");
        lblAyudaMarcaModelo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaMarcaModelo.setForeground(TEXT_SECONDARY);
        panel.add(lblAyudaMarcaModelo, gbc);
        gbc.gridwidth = 1;

        // ===== FILA 5: AÑO (SPINNER) Y CAPACIDAD (SPINNER) =====
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(crearLabel("Año Fabricación:"), gbc);

        gbc.gridx = 1;
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        int anioMinimo = anioActual - 10;
        SpinnerNumberModel modeloAnio = new SpinnerNumberModel(anioActual, anioMinimo, anioActual, 1);
        spinnerAnio = new JSpinner(modeloAnio);
        spinnerAnio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinnerAnio.getEditor()).getTextField().setBackground(new Color(21, 44, 82));
        ((JSpinner.DefaultEditor) spinnerAnio.getEditor()).getTextField().setForeground(Color.WHITE);
        ((JSpinner.DefaultEditor) spinnerAnio.getEditor()).getTextField().setCaretColor(Color.WHITE);
        spinnerAnio.setPreferredSize(new Dimension(120, 35));
        panel.add(spinnerAnio, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Capacidad:"), gbc);

        gbc.gridx = 3;
        SpinnerNumberModel modeloCapacidad = new SpinnerNumberModel(40, 1, 100, 1);
        spinnerCapacidad = new JSpinner(modeloCapacidad);
        spinnerCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinnerCapacidad.getEditor()).getTextField().setBackground(new Color(21, 44, 82));
        ((JSpinner.DefaultEditor) spinnerCapacidad.getEditor()).getTextField().setForeground(Color.WHITE);
        ((JSpinner.DefaultEditor) spinnerCapacidad.getEditor()).getTextField().setCaretColor(Color.WHITE);
        spinnerCapacidad.setPreferredSize(new Dimension(120, 35));
        panel.add(spinnerCapacidad, gbc);

        // ===== FILA 6: BASE (COMBOBOX) Y ESTADO (COMBOBOX) =====
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(crearLabel("Base Asignada:"), gbc);

        gbc.gridx = 1;
        cmbBase = new JComboBox<>(BASES_DISPONIBLES);
        cmbBase.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbBase.setBackground(new Color(21, 44, 82));
        cmbBase.setForeground(Color.WHITE);
        cmbBase.setPreferredSize(new Dimension(180, 35));
        panel.add(cmbBase, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Estado:"), gbc);

        gbc.gridx = 3;
        cmbEstado = new JComboBox<>(ESTADOS_DISPONIBLES);
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbEstado.setBackground(new Color(21, 44, 82));
        cmbEstado.setForeground(Color.WHITE);
        cmbEstado.setPreferredSize(new Dimension(180, 35));
        panel.add(cmbEstado, gbc);

        // ===== FILA 7: FECHA DE INGRESO =====
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(crearLabel("Fecha Ingreso:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        
        // Fecha actual por defecto
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        txtFechaIngreso = new JFormattedTextField();
        txtFechaIngreso.setText(hoy.format(formatter));
        txtFechaIngreso.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFechaIngreso.setBackground(new Color(21, 44, 82));
        txtFechaIngreso.setForeground(Color.WHITE);
        txtFechaIngreso.setCaretColor(Color.WHITE);
        txtFechaIngreso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 80, 130), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtFechaIngreso.setPreferredSize(new Dimension(150, 35));
        
        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelFecha.setOpaque(false);
        panelFecha.add(txtFechaIngreso);
        
        JLabel lblAyudaFecha = new JLabel("(dd/MM/yyyy)");
        lblAyudaFecha.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaFecha.setForeground(TEXT_SECONDARY);
        panelFecha.add(lblAyudaFecha);
        
        panel.add(panelFecha, gbc);
        gbc.gridwidth = 1;

        // ===== SEPARADOR =====
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 10, 20, 10);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(45, 80, 130));
        panel.add(sep, gbc);

        // ===== BOTONES =====
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(construirPanelBotones(), gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        return scroll;
    }

    /**
     * ✅ Cargar SOLO IDs de socios disponibles
     */
    private void cargarSociosDisponibles() {
        ResultadoOperacion resultado = busService.obtenerSociosDisponibles();

        if (resultado.isExito()) {
            @SuppressWarnings("unchecked")
            List<SocioDisponible> socios = (List<SocioDisponible>) resultado.getDatos();

            cmbSocio.removeAllItems();
            for (SocioDisponible socio : socios) {
                // ✅ SOLO agregar el código/ID del socio
                cmbSocio.addItem(socio.getCodigoSocio());
            }
            
            actualizarContadorSocios();
        } else {
            cmbSocio.removeAllItems();
            actualizarContadorSocios();
        }
    }

    private void actualizarContadorSocios() {
        if (lblCantidadSocios != null && cmbSocio != null) {
            int cantidad = cmbSocio.getItemCount();
            lblCantidadSocios.setText("(" + cantidad + " disponibles)");
        }
    }

    public void recargarSocios() {
        cargarSociosDisponibles();
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

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

    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        btnCancelar.addActionListener(e -> limpiarFormulario());

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
     * ✅ Registrar bus con TODAS las nuevas validaciones
     */
    private void registrarBus() {
        // ✅ Placa CON guion obligatorio
        String placa = txtPlaca.getText().trim();
        String codigoSocio = (String) cmbSocio.getSelectedItem();
        String marca = txtMarca.getText().trim().toUpperCase();
        String modelo = txtModelo.getText().trim().toUpperCase();
        int anio = (Integer) spinnerAnio.getValue();
        int capacidad = (Integer) spinnerCapacidad.getValue();
        String base = (String) cmbBase.getSelectedItem();
        String estado = (String) cmbEstado.getSelectedItem();
        String fechaIngreso = txtFechaIngreso.getText().trim();

        // Validar campos vacíos
        if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || fechaIngreso.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar socio seleccionado
        if (codigoSocio == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un socio propietario.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ✅ Validar formato de placa CON guion: PPP-1234
        if (!placa.matches("^P[A-Z]{2}-[0-9]{4}$")) {
            JOptionPane.showMessageDialog(this,
                    "La placa debe tener el formato PPP-1234 (ej: PBD-7777)\n" +
                    "- Debe empezar con P\n" +
                    "- Seguido de 2 letras mayúsculas\n" +
                    "- Luego un guion (-)\n" +
                    "- Terminar con 4 dígitos",
                    "Placa inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar marca
        if (marca.length() > 15 || !marca.matches("[A-ZÁÉÍÓÚÑ ]+")) {
            JOptionPane.showMessageDialog(this,
                    "La marca debe contener solo letras y máximo 15 caracteres.",
                    "Marca inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar modelo
        if (modelo.length() > 15 || !modelo.matches("[A-Z0-9 ]+")) {
            JOptionPane.showMessageDialog(this,
                    "El modelo debe ser alfanumérico y máximo 15 caracteres.",
                    "Modelo inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar fecha
        if (!fechaIngreso.matches("\\d{2}/\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                    "La fecha debe tener el formato dd/MM/yyyy",
                    "Fecha inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear bus
        Bus bus = new Bus();
        bus.setPlaca(placa);
        bus.setCodigoSocioFk(codigoSocio);
        bus.setMarca(marca);
        bus.setModelo(modelo);
        bus.setAnioFabricacion(anio);
        bus.setCapacidadPasajeros(capacidad);
        bus.setBaseAsignada(base);
        bus.setEstado(estado);
        bus.setFechaIngreso(fechaIngreso);  // ✅ Nuevo atributo

        // Registrar
        ResultadoOperacion resultado = busService.registrarBus(bus);

        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarSociosDisponibles();
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        spinnerAnio.setValue(anioActual);
        spinnerCapacidad.setValue(40);
        
        cmbBase.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtFechaIngreso.setText(hoy.format(formatter));
        
        if (cmbSocio.getItemCount() > 0) {
            cmbSocio.setSelectedIndex(0);
        }
        
        txtPlaca.requestFocus();
    }
}