package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Entidades.Base;
import Logica.Entidades.SocioDisponible;
import Logica.Servicios.BaseService;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

public class DialogBus extends JDialog {

    private Bus result;

    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JSpinner spinnerAnio;
    private JSpinner spinnerCapacidad;
    private JComboBox<String> cbBase;
    private JComboBox<String> cbEstado;
    private JComboBox<SocioDisponible> cbSocios;

    private final BaseService baseService = new BaseService();
    private final BusService busService = new BusService();

    public DialogBus(Component parent, String titulo, Bus existente) {
        super(SwingUtilities.getWindowAncestor(parent), titulo, ModalityType.APPLICATION_MODAL);
        setSize(550, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        // ===== PLACA =====
        content.add(new JLabel("Placa:"), c);
        c.gridx = 1;
        txtPlaca = new JTextField();
        content.add(txtPlaca, c);

        // ===== MARCA =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Marca:"), c);
        c.gridx = 1;
        txtMarca = new JTextField();
        content.add(txtMarca, c);

        // ===== MODELO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Modelo:"), c);
        c.gridx = 1;
        txtModelo = new JTextField();
        content.add(txtModelo, c);

        // ===== AÑO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Año de fabricación:"), c);
        c.gridx = 1;
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        spinnerAnio = new JSpinner(new SpinnerNumberModel(anioActual, 1990, anioActual + 1, 1));
        content.add(spinnerAnio, c);

        // ===== CAPACIDAD =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Capacidad pasajeros:"), c);
        c.gridx = 1;
        spinnerCapacidad = new JSpinner(new SpinnerNumberModel(40, 10, 100, 1));
        content.add(spinnerCapacidad, c);

        // ===== SOCIO PROPIETARIO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Socio propietario:"), c);
        c.gridx = 1;
        cbSocios = new JComboBox<>();
        cargarSociosDisponibles();
        content.add(cbSocios, c);

        // ===== BASE =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Base asignada:"), c);
        c.gridx = 1;
        cbBase = new JComboBox<>();
        cargarBasesDesdeDB();
        content.add(cbBase, c);

        // ===== ESTADO =====
        c.gridx = 0; c.gridy++;
        content.add(new JLabel("Estado:"), c);
        c.gridx = 1;
        cbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        content.add(cbEstado, c);

        add(content, BorderLayout.CENTER);

        // Modo edición
        if (existente != null) {
            txtPlaca.setText(existente.getPlaca());
            txtPlaca.setEnabled(false);
            txtMarca.setText(existente.getMarca());
            txtModelo.setText(existente.getModelo());
            spinnerAnio.setValue(existente.getAnioFabricacion());
            spinnerCapacidad.setValue(existente.getCapacidadPasajeros());
            cbBase.setSelectedItem(existente.getBaseAsignada());
            cbEstado.setSelectedItem(existente.getEstado());
            cbSocios.setEnabled(false); // no se cambia socio en edición
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

    // ================= MÉTODOS AUXILIARES =================

    private void cargarBasesDesdeDB() {
        try {
            List<Base> bases = baseService.listarActivas();
            cbBase.removeAllItems();
            for (Base b : bases) {
                cbBase.addItem(b.getNombre());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar bases:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void cargarSociosDisponibles() {
        ResultadoOperacion res = busService.obtenerSociosDisponibles();

        if (!res.isExito()) {
            JOptionPane.showMessageDialog(this,
                    res.getMensaje(),
                    "Socios no disponibles",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        @SuppressWarnings("unchecked")
        List<SocioDisponible> socios = (List<SocioDisponible>) res.getDatos();

        cbSocios.removeAllItems();
        for (SocioDisponible s : socios) {
            cbSocios.addItem(s);
        }
    }

    private void guardar() {
        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        int anio = (Integer) spinnerAnio.getValue();
        int capacidad = (Integer) spinnerCapacidad.getValue();
        String base = (String) cbBase.getSelectedItem();
        String estado = (String) cbEstado.getSelectedItem();

        SocioDisponible socio = (SocioDisponible) cbSocios.getSelectedItem();

        if (placa.isBlank() || marca.isBlank() || modelo.isBlank() || socio == null) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios (incluido el socio).",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoSocioFk = socio.getCodigoSocio();

        result = new Bus(
                placa,
                marca,
                modelo,
                anio,
                capacidad,
                base,
                estado,
                codigoSocioFk
        );

        dispose();
    }

    public Bus getResult() {
        return result;
    }
}
