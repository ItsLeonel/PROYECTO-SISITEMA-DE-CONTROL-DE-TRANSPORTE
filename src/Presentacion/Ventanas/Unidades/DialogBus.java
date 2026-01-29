package Presentacion.Ventanas.Unidades;

import Logica.Entidades.Bus;
import Logica.Entidades.Base;
import Logica.Servicios.BaseService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.List;

public class DialogBus extends JDialog {

    private Bus result;

    private JTextField txtCodigo;
    private JTextField txtPlaca;
    private JTextField txtDueno;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JSpinner spinnerAnio;
    private JComboBox<String> cbBase;
    private JComboBox<String> cbEstado;

    private final BaseService baseService = new BaseService();

    public DialogBus(Component parent, String titulo, Bus existente) {
        super(SwingUtilities.getWindowAncestor(parent), titulo, ModalityType.APPLICATION_MODAL);
        setSize(550, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; 
        c.gridy = 0;

        // ===== CÓDIGO =====
        content.add(new JLabel("Código de la unidad:"), c);
        c.gridx = 1;
        txtCodigo = new JTextField();
        content.add(txtCodigo, c);

        // Ayuda para código
        c.gridx = 1; c.gridy++;
        JLabel lblAyudaCodigo = new JLabel("4 dígitos (ej: 0001, 1234, 9999)");
        lblAyudaCodigo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblAyudaCodigo.setForeground(Color.GRAY);
        content.add(lblAyudaCodigo, c);

        // ===== PLACA =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Placa de la unidad:"), c);
        c.gridx = 1;
        txtPlaca = new JTextField();
        content.add(txtPlaca, c);

        // Ayuda para placa
        c.gridx = 1; c.gridy++;
        JLabel lblAyudaPlaca = new JLabel("<html>Formato: <b>PPP-1234</b> (primera letra debe ser <b>P</b>)</html>");
        lblAyudaPlaca.setFont(new Font("Arial", Font.ITALIC, 11));
        lblAyudaPlaca.setForeground(Color.GRAY);
        content.add(lblAyudaPlaca, c);

        // ===== DUEÑO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Dueño de la unidad:"), c);
        c.gridx = 1;
        txtDueno = new JTextField();
        content.add(txtDueno, c);

        // ===== MARCA =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Marca:"), c);
        c.gridx = 1;
        txtMarca = new JTextField();
        content.add(txtMarca, c);

        // Ayuda para marca
        c.gridx = 1; c.gridy++;
        JLabel lblAyudaMarca = new JLabel("Ej: Hino, Chevrolet, Isuzu, Mercedes-Benz");
        lblAyudaMarca.setFont(new Font("Arial", Font.ITALIC, 11));
        lblAyudaMarca.setForeground(Color.GRAY);
        content.add(lblAyudaMarca, c);

        // ===== MODELO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Modelo:"), c);
        c.gridx = 1;
        txtModelo = new JTextField();
        content.add(txtModelo, c);

        // Ayuda para modelo
        c.gridx = 1; c.gridy++;
        JLabel lblAyudaModelo = new JLabel("Ej: AK, NQR, FRR, OH-1526");
        lblAyudaModelo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblAyudaModelo.setForeground(Color.GRAY);
        content.add(lblAyudaModelo, c);

        // ===== AÑO DE FABRICACIÓN =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Año de fabricación:"), c);
        c.gridx = 1;
        
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        SpinnerNumberModel modeloAnio = new SpinnerNumberModel(anioActual, 1990, anioActual + 1, 1);
        spinnerAnio = new JSpinner(modeloAnio);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerAnio, "#");
        spinnerAnio.setEditor(editor);
        content.add(spinnerAnio, c);

        // ===== BASE (100% DINÁMICA) =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Base asignada:"), c);
        c.gridx = 1;
        cbBase = new JComboBox<>();
        cargarBasesDesdeDB();
        content.add(cbBase, c);

        // ===== ESTADO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Estado inicial:"), c);
        c.gridx = 1;
        cbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        content.add(cbEstado, c);

        add(content, BorderLayout.CENTER);

        // Si es edición (actualmente solo se usa para registrar nuevo)
        if (existente != null) {
            txtCodigo.setText(existente.getCodigo());
            txtCodigo.setEnabled(false);
            txtPlaca.setText(existente.getPlaca());
            txtDueno.setText(existente.getDueno());
            txtMarca.setText(existente.getMarca());
            txtModelo.setText(existente.getModelo());
            spinnerAnio.setValue(existente.getAnioFabricacion());
            cbBase.setSelectedItem(existente.getBase());
            cbEstado.setSelectedItem(existente.getEstado());
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> {
            result = null;
            dispose();
        });

        footer.add(btnGuardar);
        footer.add(btnCancelar);
        add(footer, BorderLayout.SOUTH);
    }

    /**
     * Carga las bases activas desde la BD usando BaseService
     * SIN DATOS QUEMADOS - TODO DINÁMICO
     */
    private void cargarBasesDesdeDB() {
        try {
            List<Base> bases = baseService.listarActivas();
            
            if (bases == null || bases.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay bases operativas activas configuradas en el sistema.\n" +
                    "Por favor, configure al menos una base activa.",
                    "Error de Configuración",
                    JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            cbBase.removeAllItems();
            
            for (Base base : bases) {
                cbBase.addItem(base.getNombre());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar bases operativas desde la base de datos:\n" + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    /**
     * Validación simplificada - Solo campos vacíos
     * Las validaciones de formato las hace BusService
     */
    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String placa  = txtPlaca.getText().trim();
        String dueno  = txtDueno.getText().trim();
        String marca  = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        int anio      = (Integer) spinnerAnio.getValue();
        String base   = String.valueOf(cbBase.getSelectedItem());
        String estado = String.valueOf(cbEstado.getSelectedItem());

        if (codigo.isBlank() || placa.isBlank() || dueno.isBlank() || 
            marca.isBlank() || modelo.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new Bus(codigo, placa, dueno, marca, modelo, anio, base, estado);
        dispose();
    }

    public Bus getResult() {
        return result;
    }
}