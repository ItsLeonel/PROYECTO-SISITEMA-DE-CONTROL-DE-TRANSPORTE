package Presentacion.Ventanas.Transporte.rutas;

import Logica.Servicios.RutaService;
import Logica.Entidades.Ruta;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class PanelRutas extends JPanel {

    private final RutaService rutaService = new RutaService();

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
    private static final Color BORDER_DARK = new Color(30, 60, 110);

    private JTextField txtCodigo, txtNombre, txtOrigen, txtDestino;
    private JComboBox<String> cbEstado;

    private JTextField txtCodigoActualizar;
    private JTextField txtNombreActualizar, txtOrigenActualizar, txtDestinoActualizar;
    private JComboBox<String> cbTipoActualizacion;

    private JTextField txtBuscarCodigo, txtBuscarNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelRutas(CardLayout parentCardLayout, JPanel parentPanel) {
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

        JLabel titulo = new JLabel("Gestión de Rutas", SwingConstants.CENTER);
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
        txtNombre = new JTextField(20);
        txtOrigen = new JTextField(20);
        txtDestino = new JTextField(20);
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        estilizarCampo(txtCodigo);
        estilizarCampo(txtNombre);
        estilizarCampo(txtOrigen);
        estilizarCampo(txtDestino);
        estilizarCombo(cbEstado);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(labelGrande("Código de ruta:"), gbc);
        gbc.gridx = 1;
        p.add(txtCodigo, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("2 dígitos (ej: 01)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(labelGrande("Nombre de la ruta:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombre, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Ruta Sur)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(labelGrande("Origen:"), gbc);
        gbc.gridx = 1;
        p.add(txtOrigen, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Hasta 20 caracteres (ej: Quitumbe)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(labelGrande("Destino:"), gbc);
        gbc.gridx = 1;
        p.add(txtDestino, gbc);
        
        gbc.gridx = 1; gbc.gridy = 7;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Hasta 20 caracteres (ej: Carapungo)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 8;
        p.add(labelGrande("Estado:"), gbc);
        gbc.gridx = 1;
        p.add(cbEstado, gbc);

        JButton btnRegistrar = new JButton(" Registrar Ruta");
        btnRegistrar.setIcon(cargarIcono("add.png", 22, 22));
        estilizarBotonGrande(btnRegistrar, BTN_BLUE);
        btnRegistrar.addActionListener(e -> registrarRuta());

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
                "ACTUALIZAR NOMBRE",
                "ACTUALIZAR ORIGEN",
                "ACTUALIZAR DESTINO"
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
        p.add(labelGrande("Código de ruta:"), gbc);

        txtCodigoActualizar = new JTextField(20);
        estilizarCampoGrande(txtCodigoActualizar);

        gbc.gridx = 1;
        p.add(txtCodigoActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("2 dígitos (ej: 01)"), gbc);

        txtNombreActualizar = new JTextField(20);
        estilizarCampoGrande(txtNombreActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 3;
        p.add(labelGrande("Nuevo nombre:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombreActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Ruta Norte)"), gbc);

        txtOrigenActualizar = new JTextField(20);
        estilizarCampoGrande(txtOrigenActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 5;
        p.add(labelGrande("Nuevo origen:"), gbc);
        gbc.gridx = 1;
        p.add(txtOrigenActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Hasta 20 caracteres (ej: Centro)"), gbc);

        txtDestinoActualizar = new JTextField(20);
        estilizarCampoGrande(txtDestinoActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 7;
        p.add(labelGrande("Nuevo destino:"), gbc);
        gbc.gridx = 1;
        p.add(txtDestinoActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 8;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Hasta 20 caracteres (ej: Terminal)"), gbc);

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
        btnActualizar.addActionListener(e -> ejecutarActualizacion());

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        p.add(btnActualizar, gbc);

        ajustarCamposActualizar();

        return p;
    }

    private void ajustarCamposActualizar() {
        String tipo = (String) cbTipoActualizacion.getSelectedItem();

        txtNombreActualizar.setEditable(false);
        txtOrigenActualizar.setEditable(false);
        txtDestinoActualizar.setEditable(false);

        txtNombreActualizar.setBackground(new Color(40, 50, 70));
        txtOrigenActualizar.setBackground(new Color(40, 50, 70));
        txtDestinoActualizar.setBackground(new Color(40, 50, 70));

        if ("ACTUALIZAR NOMBRE".equals(tipo)) {
            txtNombreActualizar.setEditable(true);
            txtNombreActualizar.setBackground(BG_PANEL);
        } else if ("ACTUALIZAR ORIGEN".equals(tipo)) {
            txtOrigenActualizar.setEditable(true);
            txtOrigenActualizar.setBackground(BG_PANEL);
        } else if ("ACTUALIZAR DESTINO".equals(tipo)) {
            txtDestinoActualizar.setEditable(true);
            txtDestinoActualizar.setBackground(BG_PANEL);
        }
    }

    private void ejecutarActualizacion() {
        String codigo = txtCodigoActualizar.getText().trim();
        
        if (codigo.isEmpty() || !codigo.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un código válido de 2 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = (String) cbTipoActualizacion.getSelectedItem();
        String mensaje = "";

        if ("ACTUALIZAR NOMBRE".equals(tipo)) {
            mensaje = rutaService.actualizarNombre(codigo, txtNombreActualizar.getText().trim());
        } else if ("ACTUALIZAR ORIGEN".equals(tipo)) {
            mensaje = rutaService.actualizarOrigen(codigo, txtOrigenActualizar.getText().trim());
        } else if ("ACTUALIZAR DESTINO".equals(tipo)) {
            mensaje = rutaService.actualizarDestino(codigo, txtDestinoActualizar.getText().trim());
        }

        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        limpiarCamposActualizar();
    }

    private void limpiarCamposActualizar() {
        txtCodigoActualizar.setText("");
        txtNombreActualizar.setText("");
        txtOrigenActualizar.setText("");
        txtDestinoActualizar.setText("");
    }

    private JPanel crearPanelConsultar() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setLayout(new BoxLayout(panelBusqueda, BoxLayout.Y_AXIS));
        panelBusqueda.setBackground(BG_PANEL);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)));

        JLabel titulo = new JLabel("🔍 Consulta de Rutas");
        titulo.setForeground(TXT_LIGHT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBusqueda.add(titulo);
        panelBusqueda.add(Box.createVerticalStrut(10));

        JRadioButton rbCodigo = new JRadioButton("Por código", true);
        JRadioButton rbNombre = new JRadioButton("Por nombre");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbCodigo);
        bg.add(rbNombre);
        estilizarRadio(rbCodigo);
        estilizarRadio(rbNombre);

        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radios.setOpaque(false);
        radios.add(rbCodigo);
        radios.add(rbNombre);
        panelBusqueda.add(radios);
        panelBusqueda.add(Box.createVerticalStrut(8));

        txtBuscarCodigo = new JTextField();
        txtBuscarNombre = new JTextField();

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(300, 32));
        estilizarCampoGrande(txtBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(Box.createVerticalStrut(12));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnBuscar = new JButton(" Buscar");
        btnBuscar.setIcon(cargarIcono("search.png", 22, 22));
        JButton btnListar = new JButton(" Listar Activas");
        btnListar.setIcon(cargarIcono("list.png", 22, 22));
        JButton btnCambiarEstado = new JButton(" Cambiar Estado");
        btnCambiarEstado.setIcon(cargarIcono("refresh.png", 22, 22));

        estilizarBotonGrande(btnBuscar, BTN_BLUE);
        estilizarBotonGrande(btnListar, BTN_BLUE);
        estilizarBotonGrande(btnCambiarEstado, BTN_GOLD);

        btnBuscar.addActionListener(e -> {
            if (rbCodigo.isSelected()) {
                txtBuscarCodigo.setText(txtBuscar.getText());
                buscarPorCodigo();
            } else {
                txtBuscarNombre.setText(txtBuscar.getText());
                buscarPorNombre();
            }
        });
        btnListar.addActionListener(e -> listarActivas());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoSeleccionado());

        panelBotones.add(btnBuscar);
        panelBotones.add(btnListar);
        panelBotones.add(btnCambiarEstado);

        panelBusqueda.add(panelBotones);

        p.add(panelBusqueda, BorderLayout.NORTH);
        p.add(crearTabla(), BorderLayout.CENTER);

        return p;
    }

    private JScrollPane crearTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[] { "Código", "Nombre", "Origen", "Destino", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(new Color(22, 44, 86));
        tabla.setShowGrid(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(70, 140, 255));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(22, 44, 86) : new Color(26, 50, 96));
                }
                c.setForeground(Color.WHITE);
                c.setOpaque(true);
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_DARK, 1),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                return c;
            }
        });

        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(c.getFont().deriveFont(Font.BOLD, 13f));
                c.setOpaque(true);

                if ("ACTIVO".equals(String.valueOf(value).toUpperCase())) {
                    c.setBackground(new Color(46, 204, 113));
                } else {
                    c.setBackground(new Color(231, 76, 60));
                }
                c.setForeground(Color.WHITE);
                c.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
                return c;
            }
        });

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(20, 45, 85));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setOpaque(true);
        header.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(22, 44, 86));
        return scroll;
    }

    private JPanel crearPanelExportar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JButton btnExportar = new JButton(" Exportar a Excel");
        btnExportar.setIcon(cargarIcono("excel.png", 24, 24));
        estilizarBotonGrande(btnExportar, BTN_GREEN);
        btnExportar.setPreferredSize(new Dimension(250, 60));
        btnExportar.addActionListener(e -> exportarExcelRutas());

        p.add(btnExportar);
        return p;
    }

    private void registrarRuta() {
        Ruta r = new Ruta();
        r.setCodigoRuta(txtCodigo.getText().trim());
        r.setNombre(txtNombre.getText().trim());
        r.setOrigen(txtOrigen.getText().trim());
        r.setDestino(txtDestino.getText().trim());
        r.setEstado("Activo");

        String mensaje = rutaService.registrarRuta(r);
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        txtCodigo.setText("");
        txtNombre.setText("");
        txtOrigen.setText("");
        txtDestino.setText("");
    }

    private void buscarPorCodigo() {
        String codigo = txtBuscarCodigo.getText().trim();
        modeloTabla.setRowCount(0);

        if (!codigo.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar la ruta: el código de la ruta no cumple el formato requerido.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorCodigo(codigo);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar la ruta: el código de la ruta no existe.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        agregarFila(r);
        JOptionPane.showMessageDialog(this, "Consulta de ruta realizada correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarPorNombre() {
        String nombre = txtBuscarNombre.getText().trim();
        modeloTabla.setRowCount(0);

        if (!nombre.matches("^[A-Za-zñÑ\\- ]{1,50}$")) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar la ruta: el nombre de la ruta no cumple el formato requerido.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorNombre(nombre);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar la ruta: el nombre de la ruta no existe.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        agregarFila(r);
        JOptionPane.showMessageDialog(this, "Consulta de ruta realizada correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void listarActivas() {
        modeloTabla.setRowCount(0);
        List<Ruta> lista = rutaService.listarActivas();

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el listado de rutas activas: no existen rutas activas registradas.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Ruta r : lista)
            agregarFila(r);
        JOptionPane.showMessageDialog(this, "Listado de rutas activas generado correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una ruta de la tabla para cambiar su estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
        String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";

        String mensaje = rutaService.cambiarEstado(codigo, nuevoEstado);
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        modeloTabla.setValueAt(nuevoEstado, filaSeleccionada, 4);
    }

    private void exportarExcelRutas() {
        List<Ruta> lista = rutaService.listarTodas();
        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo .xlsx de rutas: no existen rutas registradas.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new java.io.File("rutas.xlsx"));
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

            org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Rutas");
            org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
            String[] cols = { "Código", "Nombre", "Origen", "Destino", "Estado" };
            for (int c = 0; c < cols.length; c++)
                h.createCell(c).setCellValue(cols[c]);

            int rowIndex = 1;
            for (Ruta r : lista) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(r.getCodigoRuta());
                row.createCell(1).setCellValue(r.getNombre());
                row.createCell(2).setCellValue(r.getOrigen());
                row.createCell(3).setCellValue(r.getDestino());
                row.createCell(4).setCellValue(r.getEstado());
            }

            for (int c = 0; c < cols.length; c++)
                sheet.autoSizeColumn(c);
            wb.write(fos);

            JOptionPane.showMessageDialog(this, "Archivo .xlsx de rutas exportado correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo .xlsx de rutas: no se pudo generar el archivo de exportación.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarFila(Ruta r) {
        modeloTabla.addRow(new Object[] {
                r.getCodigoRuta(), r.getNombre(), r.getOrigen(), r.getDestino(), r.getEstado()
        });
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
        r.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void estilizarCampo(JTextField t) {
        t.setBackground(BG_PANEL);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        t.setPreferredSize(new Dimension(0, 32));
    }

    private void estilizarCampoGrande(JTextField t) {
        t.setBackground(BG_PANEL);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        t.setPreferredSize(new Dimension(0, 38));
    }

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setBackground(BG_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
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