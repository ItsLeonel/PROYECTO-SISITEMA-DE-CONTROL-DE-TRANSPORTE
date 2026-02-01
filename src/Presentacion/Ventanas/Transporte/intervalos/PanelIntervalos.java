package Presentacion.Ventanas.Transporte.intervalos;

import Logica.DAO.RutaDAO;
import Logica.Entidades.Ruta;
import Logica.Entidades.Intervalo;
import Logica.Servicios.IntervaloService;
import Logica.Servicios.ResultadoIntervalo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class PanelIntervalos extends JPanel {

    private final IntervaloService service = new IntervaloService();
    private final RutaDAO rutaDAO = new RutaDAO();

    private final CardLayout parentCardLayout;
    private final JPanel parentPanel;

    private CardLayout accionesLayout;
    private JPanel accionesPanel;

    private static final Color BG_MAIN = new Color(15, 30, 60);
    private static final Color BG_PANEL = new Color(25, 45, 90);
    private static final Color BTN_BLUE = new Color(52, 120, 246);
    private static final Color BTN_GOLD = new Color(241, 196, 15);
    private static final Color BTN_GREEN = new Color(46, 204, 113);
    private static final Color BTN_RED = new Color(231, 76, 60);
    private static final Color TXT_LIGHT = new Color(220, 220, 220);
    private static final Color TXT_HELP = new Color(170, 170, 170);
    private static final Color BORDER = new Color(45, 80, 130);

    private JTextField txtCodigo, txtTiempo, txtFranja, txtRuta;
    private JComboBox<String> cbEstado;

    private JTextField txtCodigoActualizar;
    private JTextField txtTiempoActualizar, txtFranjaActualizar, txtRutaActualizar;
    private JComboBox<String> cbTipoActualizacion;

    private JTextField txtConsulta;
    private JRadioButton rbRuta, rbFranja;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelIntervalos(CardLayout parentCardLayout, JPanel parentPanel) {
        this.parentCardLayout = parentCardLayout;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        add(crearHeader(), BorderLayout.NORTH);

        accionesLayout = new CardLayout();
        accionesPanel = new JPanel(accionesLayout);
        accionesPanel.setOpaque(false);

        accionesPanel.add(crearPanelRegistrar(), "REGISTRAR");
        accionesPanel.add(crearPanelActualizar(), "ACTUALIZAR");
        accionesPanel.add(crearPanelConsultar(), "CONSULTAR");
        accionesPanel.add(crearPanelExportar(), "EXPORTAR");

        add(accionesPanel, BorderLayout.CENTER);

        accionesLayout.show(accionesPanel, "REGISTRAR");
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JButton btnVolver = new JButton(" Volver");
        btnVolver.setIcon(cargarIcono("dashboard.png", 20, 20));
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(BTN_RED);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVolver.addActionListener(e -> parentCardLayout.show(parentPanel, "INICIO"));

        JLabel titulo = new JLabel("Gestión de Intervalos de Salida", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        JPanel navAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        navAcciones.setOpaque(false);

        String[] textos = { " Registrar", " Actualizar", " Consultar", " Exportar" };
        String[] iconos = { "add.png", "edit.png", "search.png", "excel.png" };
        String[] cards = { "REGISTRAR", "ACTUALIZAR", "CONSULTAR", "EXPORTAR" };

        for (int i = 0; i < textos.length; i++) {
            final String card = cards[i];
            JButton btn = crearBotonNav(textos[i]);
            btn.setIcon(cargarIcono(iconos[i], 22, 22));
            btn.addActionListener(e -> accionesLayout.show(accionesPanel, card));
            navAcciones.add(btn);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(btnVolver, BorderLayout.WEST);
        top.add(titulo, BorderLayout.CENTER);

        header.add(top, BorderLayout.NORTH);
        header.add(navAcciones, BorderLayout.SOUTH);

        return header;
    }

    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(BTN_BLUE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        return btn;
    }

    private JPanel crearPanelRegistrar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = new JTextField(20);
        txtTiempo = new JTextField(20);
        txtFranja = new JTextField(20);
        txtRuta = new JTextField(20);
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        estilizarCampo(txtCodigo);
        estilizarCampo(txtTiempo);
        estilizarCampo(txtFranja);
        estilizarCampo(txtRuta);
        estilizarCombo(cbEstado);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(labelGrande("Código de intervalo:"), gbc);
        gbc.gridx = 1;
        p.add(txtCodigo, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("2 dígitos (ej: 05)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(labelGrande("Tiempo (minutos):"), gbc);
        gbc.gridx = 1;
        p.add(txtTiempo, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Minutos (ej: 10)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(labelGrande("Franja horaria (HH:MM):"), gbc);
        gbc.gridx = 1;
        p.add(txtFranja, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Formato HH:MM (ej: 06:00)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(labelGrande("Ruta asociada (código):"), gbc);
        gbc.gridx = 1;
        p.add(txtRuta, gbc);
        
        gbc.gridx = 1; gbc.gridy = 7;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("2 dígitos (ej: 01)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 8;
        p.add(labelGrande("Estado:"), gbc);
        gbc.gridx = 1;
        p.add(cbEstado, gbc);

        JButton btnRegistrar = new JButton(" Registrar Intervalo");
        btnRegistrar.setIcon(cargarIcono("add.png", 22, 22));
        estilizarBotonGrande(btnRegistrar, BTN_BLUE);
        btnRegistrar.addActionListener(e -> registrarIntervalo());

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        p.add(btnRegistrar, gbc);

        return p;
    }

    private JPanel crearPanelActualizar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 2, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTipo = new JLabel("Tipo de actualización:");
        lblTipo.setForeground(Color.WHITE);
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(lblTipo, gbc);

        cbTipoActualizacion = new JComboBox<>(new String[] {
                "ACTUALIZAR TIEMPO",
                "ACTUALIZAR FRANJA"
        });

        cbTipoActualizacion.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cbTipoActualizacion.setPreferredSize(new Dimension(350, 45));
        cbTipoActualizacion.setBackground(new Color(70, 140, 255));
        cbTipoActualizacion.setForeground(Color.WHITE);
        cbTipoActualizacion.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        cbTipoActualizacion.setFocusable(false);
        cbTipoActualizacion.addActionListener(e -> ajustarCamposActualizar());

        gbc.gridx = 1;
        p.add(cbTipoActualizacion, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(12, 15, 2, 15);
        p.add(labelGrande("Código de intervalo:"), gbc);

        JPanel panelCodigo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCodigo.setOpaque(false);

        txtCodigoActualizar = new JTextField(12);
        estilizarCampoGrande(txtCodigoActualizar);

        JButton btnVerificar = new JButton(" Verificar / Cargar");
        btnVerificar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setBackground(BTN_GREEN);
        btnVerificar.setFocusPainted(false);
        btnVerificar.setBorderPainted(false);
        btnVerificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerificar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVerificar.addActionListener(e -> verificarYCargarIntervalo());

        panelCodigo.add(txtCodigoActualizar);
        panelCodigo.add(btnVerificar);

        gbc.gridx = 1;
        p.add(panelCodigo, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("2 dígitos (ej: 05)"), gbc);

        txtTiempoActualizar = new JTextField(20);
        estilizarCampoGrande(txtTiempoActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 3;
        p.add(labelGrande("Nuevo tiempo (minutos):"), gbc);
        gbc.gridx = 1;
        p.add(txtTiempoActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Minutos (ej: 15)"), gbc);

        txtFranjaActualizar = new JTextField(20);
        estilizarCampoGrande(txtFranjaActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 5;
        p.add(labelGrande("Nueva franja (HH:MM):"), gbc);
        gbc.gridx = 1;
        p.add(txtFranjaActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Formato HH:MM (ej: 07:30)"), gbc);

        txtRutaActualizar = new JTextField(20);
        txtRutaActualizar.setEditable(false);
        estilizarCampoGrande(txtRutaActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 7;
        p.add(labelGrande("Ruta (solo lectura):"), gbc);
        gbc.gridx = 1;
        p.add(txtRutaActualizar, gbc);

        JButton btnActualizar = new JButton(" ACTUALIZAR");
        btnActualizar.setIcon(cargarIcono("edit.png", 26, 26));
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnActualizar.setForeground(new Color(20, 20, 20));
        btnActualizar.setBackground(new Color(255, 215, 0));
        btnActualizar.setPreferredSize(new Dimension(300, 55));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setBorder(BorderFactory.createLineBorder(new Color(200, 170, 0), 3));
        btnActualizar.addActionListener(e -> ejecutarActualizacionIntervalo());

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        p.add(btnActualizar, gbc);

        ajustarCamposActualizar();

        return p;
    }

    private void ajustarCamposActualizar() {
        String tipo = (String) cbTipoActualizacion.getSelectedItem();

        txtTiempoActualizar.setEditable(false);
        txtFranjaActualizar.setEditable(false);

        txtTiempoActualizar.setBackground(new Color(40, 50, 70));
        txtFranjaActualizar.setBackground(new Color(40, 50, 70));
        txtRutaActualizar.setBackground(new Color(40, 50, 70));

        if ("ACTUALIZAR TIEMPO".equals(tipo)) {
            txtTiempoActualizar.setEditable(true);
            txtTiempoActualizar.setBackground(BG_PANEL);
        } else if ("ACTUALIZAR FRANJA".equals(tipo)) {
            txtFranjaActualizar.setEditable(true);
            txtFranjaActualizar.setBackground(BG_PANEL);
        }
    }

    private void verificarYCargarIntervalo() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        int codigo;
        try {
            codigo = Integer.parseInt(codigoTxt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Intervalo i = service.buscarPorCodigo(codigo);

        if (i == null) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar: el código del intervalo no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            limpiarCamposActualizar();
            return;
        }

        Ruta ruta = rutaDAO.buscarPorCodigo(i.getCodigoRuta());
        if (ruta == null) {
            JOptionPane.showMessageDialog(this, "Error: El intervalo está asociado a una ruta inexistente.", "Error", JOptionPane.ERROR_MESSAGE);
            limpiarCamposActualizar();
            return;
        }

        txtTiempoActualizar.setText(String.valueOf(i.getTiempoMinutos()));
        txtFranjaActualizar.setText(i.getFranjaHoraria());
        txtRutaActualizar.setText(i.getCodigoRuta());

        JOptionPane.showMessageDialog(this, "✅ Intervalo cargado correctamente. Puede proceder a actualizar.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarActualizacionIntervalo() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        if (codigoTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe verificar/cargar un intervalo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = Integer.parseInt(codigoTxt);
        String tipo = (String) cbTipoActualizacion.getSelectedItem();
        String mensaje = "";

        String codigoRuta = txtRutaActualizar.getText().trim();
        Ruta ruta = rutaDAO.buscarPorCodigo(codigoRuta);
        if (ruta == null) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el intervalo: el código de la ruta no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("ACTUALIZAR TIEMPO".equals(tipo)) {
            int tiempo = parseInt(txtTiempoActualizar.getText().trim());
            mensaje = service.actualizarTiempo(codigo, tiempo);
        } else if ("ACTUALIZAR FRANJA".equals(tipo)) {
            String franja = txtFranjaActualizar.getText().trim();

            if (!franja.matches("^\\d{2}:\\d{2}$")) {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la franja horaria del intervalo: la franja horaria no cumple el formato requerido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int hh = Integer.parseInt(franja.substring(0, 2));
            int mm = Integer.parseInt(franja.substring(3, 5));
            if (hh < 0 || hh > 23 || mm < 0 || mm > 59) {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la franja horaria del intervalo: la franja horaria no cumple el formato requerido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            mensaje = service.actualizarFranja(codigo, franja);
        }

        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        limpiarCamposActualizar();
    }

    private void limpiarCamposActualizar() {
        txtCodigoActualizar.setText("");
        txtTiempoActualizar.setText("");
        txtFranjaActualizar.setText("");
        txtRutaActualizar.setText("");
    }

    private JPanel crearPanelConsultar() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new BoxLayout(panelBusqueda, BoxLayout.Y_AXIS));
        panelBusqueda.setBackground(BG_PANEL);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)));

        JLabel titulo = new JLabel("🔍 Consulta de Intervalos");
        titulo.setForeground(TXT_LIGHT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBusqueda.add(titulo);
        panelBusqueda.add(Box.createVerticalStrut(10));

        JLabel ayuda = new JLabel("Buscar por ruta (código) o por franja exacta (HH:MM).");
        ayuda.setForeground(TXT_LIGHT);
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBusqueda.add(ayuda);
        panelBusqueda.add(Box.createVerticalStrut(10));

        rbRuta = new JRadioButton("Por ruta (código)", true);
        rbFranja = new JRadioButton("Por franja (HH:MM)");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbRuta);
        bg.add(rbFranja);
        estilizarRadio(rbRuta);
        estilizarRadio(rbFranja);

        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radios.setOpaque(false);
        radios.add(rbRuta);
        radios.add(rbFranja);
        panelBusqueda.add(radios);
        panelBusqueda.add(Box.createVerticalStrut(8));

        txtConsulta = new JTextField();
        txtConsulta.setPreferredSize(new Dimension(300, 32));
        estilizarCampoGrande(txtConsulta);
        panelBusqueda.add(txtConsulta);
        panelBusqueda.add(Box.createVerticalStrut(12));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnBuscar = new JButton(" Buscar");
        btnBuscar.setIcon(cargarIcono("search.png", 22, 22));
        JButton btnActivos = new JButton(" Listar Activos");
        btnActivos.setIcon(cargarIcono("list.png", 22, 22));
        JButton btnCambiarEstado = new JButton(" Cambiar Estado");
        btnCambiarEstado.setIcon(cargarIcono("refresh.png", 22, 22));

        estilizarBotonGrande(btnBuscar, BTN_BLUE);
        estilizarBotonGrande(btnActivos, BTN_BLUE);
        estilizarBotonGrande(btnCambiarEstado, BTN_GOLD);

        btnBuscar.addActionListener(e -> {
            String v = txtConsulta.getText().trim();
            if (rbRuta.isSelected()) {
                listarPorRuta(v);
            } else {
                listarPorFranja(v);
            }
        });
        btnActivos.addActionListener(e -> listarActivos());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoSeleccionado());

        panelBotones.add(btnBuscar);
        panelBotones.add(btnActivos);
        panelBotones.add(btnCambiarEstado);

        panelBusqueda.add(panelBotones);

        p.add(panelBusqueda, BorderLayout.NORTH);
        p.add(crearTabla(), BorderLayout.CENTER);

        return p;
    }

    private JScrollPane crearTabla() {
        modelo = new DefaultTableModel(
                new Object[] { "Código", "Tiempo(min)", "Franja", "Ruta", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(new Color(22, 44, 86));
        tabla.setShowGrid(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Color fila1 = new Color(22, 44, 86);
        Color fila2 = new Color(26, 50, 96);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);

                if (s) {
                    l.setBackground(new Color(70, 140, 255));
                } else {
                    l.setBackground(r % 2 == 0 ? fila1 : fila2);
                }
                l.setForeground(Color.WHITE);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return l;
            }
        });

        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);

                l.setOpaque(true);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
                l.setForeground(Color.WHITE);

                if ("ACTIVO".equalsIgnoreCase(String.valueOf(v))) {
                    l.setBackground(new Color(46, 204, 113));
                } else {
                    l.setBackground(new Color(231, 76, 60));
                }
                l.setBorder(BorderFactory.createLineBorder(BORDER, 1));
                return l;
            }
        });

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(20, 45, 85));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(true);
        header.setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(22, 44, 86));
        return sp;
    }

    private JPanel crearPanelExportar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JButton btnExportar = new JButton(" Exportar a Excel");
        btnExportar.setIcon(cargarIcono("excel.png", 24, 24));
        estilizarBotonGrande(btnExportar, BTN_GREEN);
        btnExportar.setPreferredSize(new Dimension(250, 60));
        btnExportar.addActionListener(e -> exportarExcelIntervalos());

        p.add(btnExportar);
        return p;
    }

    private void registrarIntervalo() {
        Intervalo i = new Intervalo();
        i.setCodigoIntervalo(parseInt(txtCodigo.getText().trim()));
        i.setTiempoMinutos(parseInt(txtTiempo.getText().trim()));
        i.setFranjaHoraria(txtFranja.getText().trim());
        i.setCodigoRuta(txtRuta.getText().trim());
        i.setEstado(cbEstado.getSelectedItem().toString());

        String msg = service.registrar(i);
        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        txtCodigo.setText("");
        txtTiempo.setText("");
        txtFranja.setText("");
        txtRuta.setText("");
    }

    private void listarActivos() {
        ResultadoIntervalo r = service.listarActivos();
        JOptionPane.showMessageDialog(this, r.getMensaje(), "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (r.getDatos() != null && !r.getDatos().isEmpty()) {
            cargarTabla((List<Intervalo>) r.getDatos());
        } else {
            limpiarTabla();
        }
    }

    private void listarPorRuta(String codigoRuta) {
        if (codigoRuta == null || codigoRuta.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código de la ruta.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoIntervalo r = service.consultarPorRuta(codigoRuta.trim());
        JOptionPane.showMessageDialog(this, r.getMensaje(), "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (!r.getDatos().isEmpty()) {
            cargarTabla((List<Intervalo>) r.getDatos());
        } else {
            limpiarTabla();
        }
    }

    private void listarPorFranja(String franja) {
        if (franja == null || franja.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la franja horaria (HH:MM).", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoIntervalo r = service.consultarPorFranja(franja.trim());
        JOptionPane.showMessageDialog(this, r.getMensaje(), "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (!r.getDatos().isEmpty()) {
            cargarTabla((List<Intervalo>) r.getDatos());
        } else {
            limpiarTabla();
        }
    }

    private void cambiarEstadoSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un intervalo de la tabla para cambiar su estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) modelo.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modelo.getValueAt(filaSeleccionada, 4);
        String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";

        String mensaje = service.cambiarEstado(codigo, nuevoEstado);
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        modelo.setValueAt(nuevoEstado, filaSeleccionada, 4);
    }

    private void exportarExcelIntervalos() {
        List<Intervalo> lista = service.listarTodos();
        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo .xlsx de intervalos: no existen intervalos registrados.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new java.io.File("intervalos.xlsx"));
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

            org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Intervalos");
            String[] cols = { "Código", "Tiempo(min)", "Franja", "Ruta", "Estado" };

            org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
            for (int c = 0; c < cols.length; c++)
                h.createCell(c).setCellValue(cols[c]);

            int rowIndex = 1;
            for (Intervalo i : lista) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(i.getCodigoIntervalo());
                row.createCell(1).setCellValue(i.getTiempoMinutos());
                row.createCell(2).setCellValue(i.getFranjaHoraria());
                row.createCell(3).setCellValue(i.getCodigoRuta());
                row.createCell(4).setCellValue(i.getEstado());
            }

            for (int c = 0; c < cols.length; c++)
                sheet.autoSizeColumn(c);
            wb.write(fos);

            JOptionPane.showMessageDialog(this, "Archivo .xlsx de intervalos exportado correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo .xlsx de intervalos: no se pudo generar el archivo de exportación.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cargarTabla(List<Intervalo> lista) {
        modelo.setRowCount(0);
        for (Intervalo i : lista) {
            modelo.addRow(new Object[] {
                    i.getCodigoIntervalo(), i.getTiempoMinutos(), i.getFranjaHoraria(), i.getCodigoRuta(), i.getEstado()
            });
        }
    }

    private void limpiarTabla() {
        modelo.setRowCount(0);
    }

    private JLabel labelGrande(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return l;
    }

    private JLabel crearLabelAyuda(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(TXT_HELP);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        return l;
    }

    private void estilizarBotonGrande(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
    }

    private void estilizarRadio(JRadioButton r) {
        r.setOpaque(false);
        r.setForeground(TXT_LIGHT);
        r.setFocusPainted(false);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void estilizarCampo(JTextField t) {
        t.setBackground(BG_PANEL);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        t.setPreferredSize(new Dimension(0, 32));
    }

    private void estilizarCampoGrande(JTextField t) {
        t.setBackground(BG_PANEL);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        t.setPreferredSize(new Dimension(0, 38));
    }

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setBackground(BG_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    private Icon cargarIcono(String nombre, int w, int h) {
        try {
            URL url = getClass().getResource("/Presentacion/Recursos/icons/" + nombre);
            if (url == null) return null;
            ImageIcon icono = new ImageIcon(url);
            Image img = icono.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}