package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panel para consultar un bus por placa
 * Requisito: ru4+5v1.1
 */
public class PanelConsultarBus extends JPanel {

    private BusService busService;
    private JTextField txtPlaca;
    private JPanel panelResultado;

    // Colores
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);
    private static final Color BORDER_COLOR = new Color(45, 80, 130);

    public PanelConsultarBus() {
        this.busService = new BusService();

        setLayout(new BorderLayout(16, 16));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearPanelResultado(), BorderLayout.CENTER);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel titulo = new JLabel("🔍 Buscar Bus por Placa");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        centro.setOpaque(false);

        txtPlaca = new JTextField(14);
        txtPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPlaca.setBackground(new Color(21, 44, 82));
        txtPlaca.setForeground(Color.WHITE);
        txtPlaca.setCaretColor(Color.WHITE);
        txtPlaca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton btnBuscar = crearBoton("Consultar");
        btnBuscar.addActionListener(e -> realizarBusqueda());

        txtPlaca.addActionListener(e -> realizarBusqueda());

        centro.add(new JLabel("Placa:") {{
            setForeground(TEXT_SECONDARY);
        }});
        centro.add(txtPlaca);
        centro.add(btnBuscar);

        panel.add(titulo, BorderLayout.WEST);
        panel.add(centro, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelResultado() {
        panelResultado = new JPanel(new BorderLayout());
        panelResultado.setOpaque(false);

        JLabel mensaje = new JLabel(
                "<html><div style='text-align: center;'>" +
                "<span style='font-size: 48px;'>🔍</span><br><br>" +
                "<span style='font-size: 16px; color: rgb(190,200,215);'>" +
                "Ingresa una placa para consultar" +
                "</span></div></html>"
        );
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        panelResultado.add(mensaje, BorderLayout.CENTER);
        return panelResultado;
    }

    private void realizarBusqueda() {
        String placa = txtPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa una placa.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoOperacion resultado = busService.consultarBusPorPlaca(placa);

        panelResultado.removeAll();

        if (resultado.isExito()) {
            Bus bus = (Bus) resultado.getDatos();
            mostrarResultado(bus);
        } else {
            JLabel error = new JLabel(
                    "<html><div style='text-align: center;'>" +
                    "<span style='font-size: 48px;'>❌</span><br><br>" +
                    "<span style='font-size: 16px; color: rgb(231,76,60);'>" +
                    resultado.getMensaje() +
                    "</span></div></html>"
            );
            error.setHorizontalAlignment(SwingConstants.CENTER);
            panelResultado.add(error, BorderLayout.CENTER);
        }

        panelResultado.revalidate();
        panelResultado.repaint();
    }

    private void mostrarResultado(Bus bus) {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(BG_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        // Título
        JLabel titulo = new JLabel("Bus " + bus.getPlaca());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 4;
        tarjeta.add(titulo, gbc);

        gbc.gridwidth = 1;

        // Datos del bus
        agregarCampo(tarjeta, gbc, fila++, "Placa:", bus.getPlaca());
        agregarCampo(tarjeta, gbc, fila++, "Marca:", bus.getMarca());
        agregarCampo(tarjeta, gbc, fila++, "Modelo:", bus.getModelo());
        agregarCampo(tarjeta, gbc, fila++, "Año:", String.valueOf(bus.getAnioFabricacion()));
        agregarCampo(tarjeta, gbc, fila++, "Capacidad:", bus.getCapacidadPasajeros() + " pasajeros");
        agregarCampo(tarjeta, gbc, fila++, "Base Asignada:", bus.getBaseAsignada());
        agregarCampo(tarjeta, gbc, fila++, "Estado:", bus.getEstado());

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 10, 15, 10);
        tarjeta.add(sep, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Datos del socio propietario (del JOIN)
        JLabel lblSocio = new JLabel("Información del Socio Propietario");
        lblSocio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSocio.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 4;
        tarjeta.add(lblSocio, gbc);

        gbc.gridwidth = 1;

  agregarCampo(tarjeta, gbc, fila++, "Código Socio:", bus.getCodigoSocioFk());
agregarCampo(tarjeta, gbc, fila++, "Nombres:", bus.getNombresPropietario());
agregarCampo(tarjeta, gbc, fila++, "Teléfono:", bus.getTelefonoPropietario());


        JScrollPane scroll = new JScrollPane(tarjeta);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        panelResultado.add(scroll, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, String valor) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(205, 215, 230));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(Color.WHITE);
        panel.add(val, gbc);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(PRIMARY_COLOR);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(120, 32));
        return boton;
    }
}