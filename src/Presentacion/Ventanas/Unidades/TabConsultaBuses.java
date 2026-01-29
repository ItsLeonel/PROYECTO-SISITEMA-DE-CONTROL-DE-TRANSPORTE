package Presentacion.Ventanas.Unidades;

import java.awt.Dimension;
import java.awt.Image;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Pestaña 2: Consulta Detallada
 * - Búsqueda por código o placa
 * - Muestra detalles en tarjeta estilizada con scroll
 * - Panel de búsqueda compacto
 * - Muestra 8 campos completos
 */
public class TabConsultaBuses extends JPanel {

    // ===== SERVICIOS =====
    private final BusService busService = new BusService();

    // ===== UI =====
    private JRadioButton rbCodigo;
    private JRadioButton rbPlaca;
    private JTextField txtBusqueda;
    private JPanel panelResultado;

    // ===== COLORES =====
    private final Color BG_MAIN   = new Color(11, 22, 38);
    private final Color BG_PANEL  = new Color(18, 36, 64);
    private final Color BG_CARD   = new Color(21, 44, 82);
    private final Color BTN_MAIN  = new Color(33, 90, 190);
    private final Color TXT_SEC   = new Color(190, 200, 215);
    private final Color BORDER    = new Color(45, 80, 130);
    private final Color ESTADO_ACTIVO = new Color(46, 204, 113);
    private final Color ESTADO_DESACTIVADO = new Color(231, 76, 60);
    private final Color ESTADO_MANT = new Color(241, 196, 15);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public TabConsultaBuses() {
        setLayout(new BorderLayout(16, 16));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearPanelResultado(), BorderLayout.CENTER);
    }

    // =====================================================
    // PANEL DE BÚSQUEDA (COMPACTO)
    // =====================================================
 private JPanel crearPanelBusqueda() {
    JPanel panel = new JPanel(new BorderLayout(12, 0));
    panel.setBackground(BG_PANEL);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER),
        BorderFactory.createEmptyBorder(12, 16, 12, 16)
    ));

    // ===== IZQUIERDA: TÍTULO =====
    JLabel titulo = new JLabel("🔍 Buscar Unidad");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
    titulo.setForeground(Color.WHITE);

    // ===== CENTRO: OPCIONES + CAMPO =====
    JPanel centro = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    centro.setOpaque(false);

    rbCodigo = new JRadioButton("Por código", true);
    rbPlaca  = new JRadioButton("Por placa");

    configurarRadio(rbCodigo);
    configurarRadio(rbPlaca);

    ButtonGroup bg = new ButtonGroup();
    bg.add(rbCodigo);
    bg.add(rbPlaca);

    txtBusqueda = new JTextField(14);
    txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtBusqueda.setBackground(BG_CARD);
    txtBusqueda.setForeground(Color.WHITE);
    txtBusqueda.setCaretColor(Color.WHITE);
    txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER),
        BorderFactory.createEmptyBorder(6, 10, 6, 10)
    ));

   JButton btnBuscar = boton("Consultar");
btnBuscar.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

// 🔴 ESTA LÍNEA FALTABA
btnBuscar.addActionListener(e -> realizarBusqueda());

txtBusqueda.addActionListener(e -> realizarBusqueda());


    centro.add(rbCodigo);
    centro.add(rbPlaca);
    centro.add(txtBusqueda);
    centro.add(btnBuscar);

    panel.add(titulo, BorderLayout.WEST);
    panel.add(centro, BorderLayout.CENTER);

    return panel;
}


    // =====================================================
    // PANEL DE RESULTADO (CON SCROLL)
    // =====================================================
    private JPanel crearPanelResultado() {
        panelResultado = new JPanel(new BorderLayout());
        panelResultado.setOpaque(false);

        // Mensaje inicial
        mostrarMensajeInicial();

        return panelResultado;
    }

    private void mostrarMensajeInicial() {
        JLabel mensaje = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<span style='font-size: 48px;'>🔍</span><br><br>" +
            "<span style='font-size: 16px; color: rgb(190,200,215);'>" +
            "Ingresa un código o placa para consultar" +
            "</span></div></html>"
        );
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        mensaje.setVerticalAlignment(SwingConstants.CENTER);

        panelResultado.removeAll();
        panelResultado.add(mensaje, BorderLayout.CENTER);
        panelResultado.revalidate();
        panelResultado.repaint();
    }

    // =====================================================
    // LÓGICA DE BÚSQUEDA
    // =====================================================
  private void realizarBusqueda() {
    String valor = txtBusqueda.getText().trim();

    if (valor.isBlank()) {
        JOptionPane.showMessageDialog(this,
            "Por favor ingresa un valor para buscar.",
            "Validación",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        // 🔴 LIMPIAR SIEMPRE ANTES
        panelResultado.removeAll();

        Bus bus = rbCodigo.isSelected()
            ? busService.consultarPorCodigo(valor)
            : busService.consultarPorPlaca(valor);

        // 🔴 MOSTRAR RESULTADO
        mostrarResultado(bus);

        JOptionPane.showMessageDialog(this,
            "Consulta de unidad realizada correctamente.",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        panelResultado.removeAll();
        mostrarError();

        JOptionPane.showMessageDialog(this,
            e.getMessage(),
            "Error",
            JOptionPane.WARNING_MESSAGE);
    }
}

private void mostrarResultado(Bus bus) {
    panelResultado.removeAll();

    // ===== TARJETA =====
    JPanel tarjeta = new JPanel();
    tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
    tarjeta.setBackground(BG_PANEL);
    tarjeta.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER, 2),
        BorderFactory.createEmptyBorder(24, 32, 24, 32)
    ));

    // ===== TÍTULO =====
    JLabel titulo = new JLabel("Unidad " + bus.getPlaca());
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titulo.setForeground(Color.WHITE);
    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    titulo.setHorizontalAlignment(SwingConstants.CENTER);



    JSeparator sep = new JSeparator();
    sep.setForeground(BORDER);
   sep.setPreferredSize(new Dimension(420, 1));
sep.setMaximumSize(new Dimension(420, 1));
sep.setAlignmentX(Component.CENTER_ALIGNMENT);


    // ===== CUERPO =====
    JPanel cuerpo = new JPanel(new BorderLayout(24, 0));
    cuerpo.setOpaque(false);

    // ===== DATOS (4 COLUMNAS) =====
    JPanel datos = new JPanel(new GridLayout(0, 4, 12, 12));
    datos.setOpaque(false);

    datos.add(crearEtiqueta("Código:"));
    datos.add(crearValor(bus.getCodigo()));
    datos.add(crearEtiqueta("Placa:"));
    datos.add(crearValor(bus.getPlaca()));

    datos.add(crearEtiqueta("Dueño:"));
    datos.add(crearValor(bus.getDueno()));
    datos.add(crearEtiqueta("Marca:"));
    datos.add(crearValor(bus.getMarca()));

    datos.add(crearEtiqueta("Modelo:"));
    datos.add(crearValor(bus.getModelo()));
    datos.add(crearEtiqueta("Año:"));
    datos.add(crearValor(String.valueOf(bus.getAnioFabricacion())));

    datos.add(crearEtiqueta("Base:"));
    datos.add(crearValor(bus.getBase()));
    datos.add(crearEtiqueta("Estado:"));
    datos.add(crearPanelEstado(bus.getEstado()));

    // ===== IMAGEN =====
    JPanel imagenPanel = new JPanel(new BorderLayout());
    imagenPanel.setOpaque(false);
    imagenPanel.setPreferredSize(new Dimension(220, 220));

    JLabel imagen = new JLabel();
    imagen.setHorizontalAlignment(SwingConstants.CENTER);

    try {
        ImageIcon icono = new ImageIcon(
            getClass().getResource("/Presentacion/Recursos/logoo_empresa.png")
        );
        Image img = icono.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        imagen.setIcon(new ImageIcon(img));
    } catch (Exception e) {
        imagen.setText("Sin imagen");
        imagen.setForeground(TXT_SEC);
    }

    imagenPanel.add(imagen, BorderLayout.CENTER);

    cuerpo.add(datos, BorderLayout.CENTER);
    cuerpo.add(imagenPanel, BorderLayout.EAST);

    tarjeta.add(titulo);
    tarjeta.add(Box.createVerticalStrut(16));
    tarjeta.add(sep);
    tarjeta.add(Box.createVerticalStrut(20));
    tarjeta.add(cuerpo);

    // ===== SCROLL (CLAVE) =====
    JScrollPane scroll = new JScrollPane(tarjeta);
    scroll.setBorder(null);
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    // 👉 ESTO ES LO CORRECTO
    panelResultado.add(scroll, BorderLayout.CENTER);

    panelResultado.revalidate();
    panelResultado.repaint();
}


    private void mostrarError() {
        JLabel mensaje = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<span style='font-size: 48px;'>❌</span><br><br>" +
            "<span style='font-size: 16px; color: rgb(231,76,60);'>" +
            "No se encontró la unidad" +
            "</span></div></html>"
        );
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        panelResultado.removeAll();
        panelResultado.add(mensaje, BorderLayout.CENTER);
        panelResultado.revalidate();
        panelResultado.repaint();
    }

    // =====================================================
    // HELPERS PARA LA TARJETA
    // =====================================================
    
private JLabel crearEtiqueta(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lbl.setForeground(new Color(205, 215, 230));
    return lbl;
}


    private JLabel crearValor(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // 🔹 menos pesado
    lbl.setForeground(Color.WHITE);
    return lbl;
}


    private JPanel crearPanelEstado(String estado) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));

        panel.setOpaque(false);

        JLabel lbl = new JLabel(" " + estado + " ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);

        switch (estado) {
            case "ACTIVO":
                lbl.setBackground(ESTADO_ACTIVO);
                break;
            case "INACTIVO":
                lbl.setBackground(ESTADO_DESACTIVADO);
                break;
            case "MANTENIMIENTO":
                lbl.setBackground(ESTADO_MANT);
                lbl.setForeground(Color.BLACK);
                break;
        }

        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(lbl.getBackground().darker(), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        panel.add(lbl);
        return panel;
    }

    // =====================================================
    // UI HELPERS
    // =====================================================
    
    private void configurarRadio(JRadioButton r) {
        r.setForeground(Color.WHITE);
        r.setOpaque(false);
        r.setFocusPainted(false);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private JButton boton(String txt) {
        JButton b = new JButton(txt);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(BTN_MAIN);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return b;
    }
}