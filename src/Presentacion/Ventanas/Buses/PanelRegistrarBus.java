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
    private PanelBuses parent; 

    // Campos del formulario
    private JTextField txtPlaca;
    private JComboBox<SocioDisponible> cmbSocio;
    private JLabel lblCantidadSocios;  // ✅ Variable de instancia
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtAnio;          // ✅ Ahora es TextField
    private JTextField txtCapacidad;     // ✅ Ahora es TextField
    private JTextField txtBase;
    private JTextField txtEstado;        // ✅ Ahora es TextField

    // Colores del tema
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);

    public PanelRegistrarBus(PanelBuses parent) {
        this.parent = parent;
        this.busService = new BusService();

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        add(construirFormulario(), BorderLayout.CENTER);

        JButton btnVolver = new JButton("⬅ Volver");
        btnVolver.addActionListener(e -> parent.mostrar(PanelBuses.MENU));
        add(btnVolver, BorderLayout.SOUTH);

        // ✅ Recargar socios cada vez que el panel se hace visible
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

        // Fila 2: Código del Socio Propietario (✅ con contador)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(crearLabel("Código del Socio Propietario:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JPanel panelSocio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelSocio.setOpaque(false);
        
        cmbSocio = new JComboBox<>();
        cmbSocio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbSocio.setBackground(new Color(21, 44, 82));
        cmbSocio.setForeground(Color.WHITE);
        cmbSocio.setPreferredSize(new Dimension(300, 35));
        cargarSociosDisponibles();
        panelSocio.add(cmbSocio);
        
        // ✅ Crear label como variable de instancia
        lblCantidadSocios = new JLabel();
        lblCantidadSocios.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCantidadSocios.setForeground(TEXT_SECONDARY);
        actualizarContadorSocios();
        panelSocio.add(lblCantidadSocios);
        
        panel.add(panelSocio, gbc);

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
        JLabel lblAyudaMarcaModelo = new JLabel("Marca: máx 15 caracteres alfabéticos | Modelo: máx 15 caracteres alfanuméricos");
        lblAyudaMarcaModelo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaMarcaModelo.setForeground(TEXT_SECONDARY);
        panel.add(lblAyudaMarcaModelo, gbc);

        gbc.gridwidth = 1;

        // Fila 5: Año y Capacidad (✅ AHORA SON CAMPOS DE TEXTO)
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(crearLabel("Año de Fabricación:"), gbc);

        gbc.gridx = 1;
        txtAnio = crearTextField(8);
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        txtAnio.setText(String.valueOf(anioActual));
        JPanel panelAnio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelAnio.setOpaque(false);
        panelAnio.add(txtAnio);
        JLabel lblAyudaAnio = new JLabel("(4 dígitos, ≤ " + anioActual + ", antigüedad ≤ 10 años)");
        lblAyudaAnio.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaAnio.setForeground(TEXT_SECONDARY);
        panelAnio.add(lblAyudaAnio);
        panel.add(panelAnio, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Capacidad Pasajeros:"), gbc);

        gbc.gridx = 3;
        txtCapacidad = crearTextField(8);
        txtCapacidad.setText("40");
        JPanel panelCapacidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelCapacidad.setOpaque(false);
        panelCapacidad.add(txtCapacidad);
        JLabel lblAyudaCapacidad = new JLabel("(entero > 0)");
        lblAyudaCapacidad.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaCapacidad.setForeground(TEXT_SECONDARY);
        panelCapacidad.add(lblAyudaCapacidad);
        panel.add(panelCapacidad, gbc);

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
        JLabel lblAyudaBase = new JLabel("(Nombre de la base operativa)");
        lblAyudaBase.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaBase.setForeground(TEXT_SECONDARY);
        panelBase.add(lblAyudaBase);
        panel.add(panelBase, gbc);

        gbc.gridwidth = 1;

        // Fila 7: Estado (✅ AHORA ES CAMPO DE TEXTO)
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(crearLabel("Estado:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtEstado = crearTextField(15);
        txtEstado.setText("ACTIVO");
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelEstado.setOpaque(false);
        panelEstado.add(txtEstado);
        JLabel lblAyudaEstado = new JLabel("(ACTIVO, INACTIVO o MANTENIMIENTO)");
        lblAyudaEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyudaEstado.setForeground(TEXT_SECONDARY);
        panelEstado.add(lblAyudaEstado);
        panel.add(panelEstado, gbc);

        gbc.gridwidth = 1;

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
            
            // ✅ Actualizar contador después de cargar
            actualizarContadorSocios();
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            cmbSocio.removeAllItems();
            actualizarContadorSocios();
        }
    }

    /**
     * ✅ Actualizar el contador de socios disponibles
     */
    private void actualizarContadorSocios() {
        if (lblCantidadSocios != null && cmbSocio != null) {
            int cantidad = cmbSocio.getItemCount();
            lblCantidadSocios.setText("(" + cantidad + " socios disponibles)");
        }
    }

    /**
     * ✅ MÉTODO PÚBLICO: Recargar socios disponibles
     * Llamar este método cuando se registre un nuevo socio
     */
    public void recargarSocios() {
        System.out.println("🔄 Recargando socios disponibles...");
        cargarSociosDisponibles();
        System.out.println("✅ Socios recargados: " + cmbSocio.getItemCount());
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
     * ✅ CUMPLE CON TODOS LOS REQUISITOS Y MENSAJES EXACTOS
     */
    private void registrarBus() {
        // ===== OBTENER DATOS =====
        // ✅ NO convertir placa a mayúsculas - debe venir en mayúsculas
        String placa = txtPlaca.getText().trim();  // SIN .toUpperCase()
        SocioDisponible socioSeleccionado = (SocioDisponible) cmbSocio.getSelectedItem();
        String marca = txtMarca.getText().trim().toUpperCase();
        String modelo = txtModelo.getText().trim().toUpperCase();
        String anioTexto = txtAnio.getText().trim();
        String capacidadTexto = txtCapacidad.getText().trim();
        String base = txtBase.getText().trim();
        String estado = txtEstado.getText().trim().toUpperCase();

        // ===== VALIDACIONES SEGÚN REQUISITOS =====
        
        // Validar que no estén vacíos
        if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || 
            anioTexto.isEmpty() || capacidadTexto.isEmpty() || base.isEmpty() || estado.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar socio seleccionado
        if (socioSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un socio propietario.\n" +
                    "Si no aparecen socios, debe registrar uno primero en el Módulo de Socios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar año de fabricación
        int anio;
        try {
            anio = Integer.parseInt(anioTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El año de fabricación debe ser un número de 4 dígitos.",
                    "Año inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que el año tenga 4 dígitos
        if (anioTexto.length() != 4) {
            JOptionPane.showMessageDialog(this,
                    "El año de fabricación debe ser un número de 4 dígitos.",
                    "Año inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que el año sea menor o igual al año actual
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        if (anio > anioActual) {
            JOptionPane.showMessageDialog(this,
                    "El año de fabricación no puede ser mayor al año actual (" + anioActual + ").",
                    "Año inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que la antigüedad no supere los 10 años
        if ((anioActual - anio) > 10) {
            JOptionPane.showMessageDialog(this,
                    "El año de fabricación no puede tener una antigüedad mayor a 10 años.",
                    "Año inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar capacidad de pasajeros
        int capacidad;
        try {
            capacidad = Integer.parseInt(capacidadTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La capacidad de pasajeros debe ser un número entero.",
                    "Capacidad inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que la capacidad sea mayor que cero
        if (capacidad <= 0) {
            JOptionPane.showMessageDialog(this,
                    "La capacidad de pasajeros debe ser un valor mayor que cero.",
                    "Capacidad inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar marca (máx 15 caracteres alfabéticos)
        if (marca.length() > 15 || !marca.matches("[A-ZÁÉÍÓÚÑ ]+")) {
            JOptionPane.showMessageDialog(this,
                    "La marca debe contener solo caracteres alfabéticos y tener máximo 15 caracteres.",
                    "Marca inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar modelo (máx 15 caracteres alfanuméricos)
        if (modelo.length() > 15 || !modelo.matches("[A-Z0-9 ]+")) {
            JOptionPane.showMessageDialog(this,
                    "El modelo debe contener solo caracteres alfanuméricos y tener máximo 15 caracteres.",
                    "Modelo inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar estado
        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO") && !estado.equals("MANTENIMIENTO")) {
            JOptionPane.showMessageDialog(this,
                    "El estado debe ser: ACTIVO, INACTIVO o MANTENIMIENTO.",
                    "Estado inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ===== CREAR OBJETO BUS =====
        Bus bus = new Bus();
        bus.setPlaca(placa);
        bus.setCodigoSocioFk(socioSeleccionado.getCodigoSocio());
        bus.setMarca(marca);
        bus.setModelo(modelo);
        bus.setAnioFabricacion(anio);
        bus.setCapacidadPasajeros(capacidad);
        bus.setBaseAsignada(base);
        bus.setEstado(estado);

        // ===== REGISTRAR MEDIANTE EL SERVICIO =====
        ResultadoOperacion resultado = busService.registrarBus(bus);

        // ===== MOSTRAR MENSAJE SEGÚN EL RESULTADO =====
        // Los mensajes exactos están en el servicio según requisitos
        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(), // "Bus registrado correctamente."
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarSociosDisponibles(); // Recargar combo
        } else {
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(), // Mensajes de error según requisitos
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
        txtAnio.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        txtCapacidad.setText("40");
        txtBase.setText("");
        txtEstado.setText("ACTIVO");
        if (cmbSocio.getItemCount() > 0) {
            cmbSocio.setSelectedIndex(0);
        }
        txtPlaca.requestFocus();
    }
}