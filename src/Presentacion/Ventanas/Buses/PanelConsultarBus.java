package Presentacion.Ventanas.Buses;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;
import Logica.Servicios.ResultadoOperacion;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para consultar buses por placa
 * Requisito: ru4+5v1.1
 * DISEÑO: Formato tipo carnet/tarjeta visual
 */
public class PanelConsultarBus extends JPanel {
    
    private BusService busService;
    private PanelBuses parent;
    private JTextField txtPlaca;
    private JPanel panelResultado;

    // Colores del tema
    private static final Color BG_MAIN = new Color(11, 22, 38);
    private static final Color BG_PANEL = new Color(18, 36, 64);
    private static final Color BG_CARD = new Color(21, 44, 82);
    private static final Color PRIMARY_COLOR = new Color(33, 90, 190);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color TEXT_SECONDARY = new Color(190, 200, 215);
    private static final Color BORDER_COLOR = new Color(45, 80, 130);

    public PanelConsultarBus(PanelBuses parent) {
        this.parent = parent;
        this.busService = new BusService();

        setLayout(new BorderLayout(0, 20));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        add(construirPanelBusqueda(), BorderLayout.NORTH);
        add(construirPanelResultado(), BorderLayout.CENTER);
        add(construirPanelBotones(), BorderLayout.SOUTH);
    }

    /**
     * Panel superior con campo de búsqueda
     */
    private JPanel construirPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Consultar Bus");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(lblTitulo, gbc);

        // Label Placa
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 10, 0, 10);
        JLabel lblPlaca = new JLabel("Placa:");
        lblPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPlaca.setForeground(TEXT_SECONDARY);
        panel.add(lblPlaca, gbc);

        // Campo placa
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPlaca = crearCampoTexto(15);
        panel.add(txtPlaca, gbc);

        // Botón Buscar
        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton btnConsultar = crearBoton("Consultar", SUCCESS_COLOR);
        btnConsultar.addActionListener(e -> consultarBus());
        panel.add(btnConsultar, gbc);

        return panel;
    }

    /**
     * Panel central para mostrar resultado (tipo carnet)
     */
    private JScrollPane construirPanelResultado() {
        panelResultado = new JPanel(new GridBagLayout());
        panelResultado.setBackground(BG_MAIN);

        // Mensaje inicial
        mostrarMensajeInicial();

        JScrollPane scroll = new JScrollPane(panelResultado);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    /**
     * Panel inferior con botones
     */
    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);

        JButton btnLimpiar = crearBoton("Limpiar", PRIMARY_COLOR);
        btnLimpiar.addActionListener(e -> limpiar());

        JButton btnVolver = crearBoton("⬅ Volver", PRIMARY_COLOR);
        btnVolver.addActionListener(e -> parent.mostrar(PanelBuses.MENU));

        panel.add(btnLimpiar);
        panel.add(btnVolver);

        return panel;
    }

    /**
     * Mostrar mensaje inicial
     */
    private void mostrarMensajeInicial() {
        panelResultado.removeAll();

        JLabel mensaje = new JLabel("Ingrese una placa y presione Consultar");
        mensaje.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        mensaje.setForeground(TEXT_SECONDARY);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        panelResultado.add(mensaje, gbc);
        panelResultado.revalidate();
        panelResultado.repaint();
    }

    /**
     * Consultar bus
     */
    private void consultarBus() {
        // ✅ NO convertir a mayúsculas - debe venir en mayúsculas
        String placa = txtPlaca.getText().trim();

        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese una placa.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoOperacion resultado = busService.consultarBusPorPlaca(placa);

        if (resultado.isExito()) {
            Bus bus = (Bus) resultado.getDatos();
            mostrarDatosBus(bus);
        } else {
            mostrarMensajeError(resultado.getMensaje());
            JOptionPane.showMessageDialog(this,
                    resultado.getMensaje(),
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Mostrar datos del bus en formato carnet
     */
    private void mostrarDatosBus(Bus bus) {
        panelResultado.removeAll();

        // Panel tipo carnet
        JPanel carnet = new JPanel(new GridBagLayout());
        carnet.setBackground(BG_PANEL);
        carnet.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        carnet.setPreferredSize(new Dimension(600, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Título del carnet
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("INFORMACIÓN DEL BUS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        carnet.add(lblTitulo, gbc);

        // Separador
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 15, 10);
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(BORDER_COLOR);
        carnet.add(sep1, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Fila 1: Placa
        gbc.gridy = 2;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Placa:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(bus.getPlaca()), gbc);

        // Fila 2: Código Socio
        gbc.gridy = 3;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Código Socio:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(bus.getCodigoSocioFk()), gbc);

        // Fila 3: Marca
        gbc.gridy = 4;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Marca:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(bus.getMarca()), gbc);

        // Fila 4: Modelo
        gbc.gridy = 5;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Modelo:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(bus.getModelo()), gbc);

        // Fila 5: Año de Fabricación
        gbc.gridy = 6;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Año de Fabricación:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(String.valueOf(bus.getAnioFabricacion())), gbc);

        // Fila 6: Capacidad
        gbc.gridy = 7;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Capacidad:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(bus.getCapacidadPasajeros() + " pasajeros"), gbc);

        // Fila 7: Base Asignada
        gbc.gridy = 8;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Base Asignada:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelValor(bus.getBaseAsignada()), gbc);

        // Fila 8: Estado (con color)
        gbc.gridy = 9;
        gbc.gridx = 0;
        carnet.add(crearLabelCampo("Estado:"), gbc);
        gbc.gridx = 1;
        carnet.add(crearLabelEstado(bus.getEstado()), gbc);

        // Separador final
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(BORDER_COLOR);
        carnet.add(sep2, gbc);

        // Centrar el carnet
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        gbcMain.anchor = GridBagConstraints.CENTER;

        panelResultado.add(carnet, gbcMain);
        panelResultado.revalidate();
        panelResultado.repaint();
    }

    /**
     * Mostrar mensaje de error
     */
    private void mostrarMensajeError(String mensaje) {
        panelResultado.removeAll();

        JLabel lblError = new JLabel(mensaje);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblError.setForeground(new Color(231, 76, 60));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        panelResultado.add(lblError, gbc);
        panelResultado.revalidate();
        panelResultado.repaint();
    }

    /**
     * Crear label para nombre de campo
     */
    private JLabel crearLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /**
     * Crear label para valor del campo
     */
    private JLabel crearLabelValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    /**
     * Crear label para estado con color
     */
    private JLabel crearLabelEstado(String estado) {
        JLabel label = new JLabel(estado);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        switch (estado) {
            case "ACTIVO":
                label.setBackground(new Color(46, 204, 113));
                label.setForeground(Color.WHITE);
                break;
            case "INACTIVO":
                label.setBackground(new Color(231, 76, 60));
                label.setForeground(Color.WHITE);
                break;
            case "MANTENIMIENTO":
                label.setBackground(new Color(241, 196, 15));
                label.setForeground(Color.BLACK);
                break;
            default:
                label.setForeground(Color.WHITE);
        }

        return label;
    }

    /**
     * Crear campo de texto
     */
    private JTextField crearCampoTexto(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(BG_CARD);
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return campo;
    }

    /**
     * Crear botón
     */
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(140, 38));
        return boton;
    }

    /**
     * Limpiar formulario
     */
    private void limpiar() {
        txtPlaca.setText("");
        mostrarMensajeInicial();
        txtPlaca.requestFocus();
    }
}