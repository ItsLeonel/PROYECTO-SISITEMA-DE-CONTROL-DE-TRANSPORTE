package Presentacion.Ventanas.Transporte.bases;

import Logica.Entidades.Base;
import Logica.Servicios.BaseService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class PanelBases extends JPanel {

    private final BaseService baseService = new BaseService();

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

    private JTextField txtCodigo, txtNombre, txtDireccion;
    private JComboBox<String> cbEstado;

    private JTextField txtCodigoActualizar;
    private JTextField txtNombreActualizar;

    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelBases(CardLayout parentCardLayout, JPanel parentPanel) {
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

        JLabel titulo = new JLabel("Gestión de Bases Operativas", SwingConstants.CENTER);
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
        txtDireccion = new JTextField(20);
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        estilizarCampo(txtCodigo);
        estilizarCampo(txtNombre);
        estilizarCampo(txtDireccion);
        estilizarCombo(cbEstado);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(labelGrande("Código de base:"), gbc);
        gbc.gridx = 1;
        p.add(txtCodigo, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("2 dígitos (ej: 02)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 2;
        p.add(labelGrande("Nombre de la base:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombre, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Base Carapungo)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 4;
        p.add(labelGrande("Dirección:"), gbc);
        gbc.gridx = 1;
        p.add(txtDireccion, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Hasta 100 caracteres (ej: Av. Principal y Calle Secundaria)"), gbc);

        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0; gbc.gridy = 6;
        p.add(labelGrande("Estado:"), gbc);
        gbc.gridx = 1;
        p.add(cbEstado, gbc);

        JButton btnRegistrar = new JButton(" Registrar Base");
        JButton btnLimpiar = new JButton(" Limpiar");

        estilizarBotonGrande(btnRegistrar, BTN_BLUE);
        estilizarBotonGrande(btnLimpiar, BTN_GOLD);

        btnRegistrar.setIcon(cargarIcono("add.png", 22, 22));
        btnLimpiar.setIcon(cargarIcono("reset.png", 22, 22));

        btnRegistrar.addActionListener(e -> registrarBase());
        btnLimpiar.addActionListener(e -> limpiarFormularioRegistro());

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 10, 10, 10);
        p.add(btnRegistrar, gbc);
        gbc.gridx = 1;
        p.add(btnLimpiar, gbc);

        return p;
    }

    private JPanel crearPanelActualizar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 2, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("✏️ ACTUALIZAR NOMBRE DE BASE");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(12, 15, 2, 15);
        p.add(labelGrande("Código de base:"), gbc);

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
        btnVerificar.addActionListener(e -> verificarYCargarBase());

        panelCodigo.add(txtCodigoActualizar);
        panelCodigo.add(btnVerificar);

        gbc.gridx = 1;
        p.add(panelCodigo, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("2 dígitos (ej: 02)"), gbc);

        txtNombreActualizar = new JTextField(20);
        estilizarCampoGrande(txtNombreActualizar);

        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0; gbc.gridy = 3;
        p.add(labelGrande("Nuevo nombre:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombreActualizar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Base Norte)"), gbc);

        JButton btnActualizar = new JButton(" ACTUALIZAR NOMBRE");
        btnActualizar.setIcon(cargarIcono("edit.png", 26, 26));
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnActualizar.setForeground(new Color(20, 20, 20));
        btnActualizar.setBackground(new Color(255, 215, 0));
        btnActualizar.setPreferredSize(new Dimension(350, 55));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setBorder(BorderFactory.createLineBorder(new Color(200, 170, 0), 3));
        btnActualizar.addActionListener(e -> ejecutarActualizarNombre());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        p.add(btnActualizar, gbc);

        return p;
    }

    private void verificarYCargarBase() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        if (!codigoTxt.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this, "El código debe tener 2 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int codigo = Integer.parseInt(codigoTxt);
        Base b = baseService.buscarPorCodigo(codigo);

        if (b == null) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar: el código de la base no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombreActualizar.setText("");
            return;
        }

        txtNombreActualizar.setText(b.getNombre());
        JOptionPane.showMessageDialog(this, "✅ Base cargada correctamente. Puede proceder a actualizar.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarActualizarNombre() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        if (codigoTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe verificar/cargar una base.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = Integer.parseInt(codigoTxt);
        String mensaje = baseService.actualizarNombre(codigo, txtNombreActualizar.getText().trim());

        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        txtCodigoActualizar.setText("");
        txtNombreActualizar.setText("");
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

        JLabel titulo = new JLabel("🔍 Consulta de Bases Operativas");
        titulo.setForeground(TXT_LIGHT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBusqueda.add(titulo);
        panelBusqueda.add(Box.createVerticalStrut(10));

        JLabel lblBuscar = new JLabel("Buscar por nombre:");
        lblBuscar.setForeground(TXT_LIGHT);
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(Box.createVerticalStrut(8));

        txtBuscar = new JTextField();
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

        btnBuscar.addActionListener(e -> buscar());
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
                new Object[] { "Código", "Nombre", "Dirección", "Estado" }, 0) {
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

                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

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

        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

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
        btnExportar.addActionListener(e -> exportarExcelBases());

        p.add(btnExportar);
        return p;
    }

    private void registrarBase() {
        String codigoTxt = txtCodigo.getText().trim();

        if (!codigoTxt.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la base operativa: el código de la base no cumple el formato requerido.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Base b = new Base();
        b.setNombre(txtNombre.getText().trim());
        b.setDireccion(txtDireccion.getText().trim());
        b.setCodigoBase(Integer.parseInt(codigoTxt));
        b.setEstado(cbEstado.getSelectedItem().toString());

        String msg = baseService.registrarBase(b);
        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormularioRegistro();
    }

    private void buscar() {
        modeloTabla.setRowCount(0);
        String nombre = txtBuscar.getText().trim();
        String msg = baseService.consultarBasePorNombre(nombre);

        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (!msg.equals("Consulta de base operativa realizada correctamente."))
            return;

        Base b = baseService.obtenerBasePorNombre(nombre);
        if (b == null)
            return;

        agregarFila(b);
    }

    private void listarActivas() {
        modeloTabla.setRowCount(0);
        List<Base> lista = baseService.listarActivas();

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el listado de bases operativas activas: no existen bases operativas activas registradas.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Base b : lista)
            agregarFila(b);
        JOptionPane.showMessageDialog(this, "Listado de bases operativas activas generado correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de la tabla para cambiar su estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
        String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";

        String mensaje = baseService.cambiarEstado(codigo, nuevoEstado);
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        modeloTabla.setValueAt(nuevoEstado, filaSeleccionada, 3);
    }

    private void exportarExcelBases() {
        List<Base> lista = baseService.listarTodas();
        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo .xlsx de bases operativas: no existen bases operativas registradas.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new java.io.File("bases_operativas.xlsx"));
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

            org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Bases");
            String[] cols = { "Código", "Nombre", "Dirección", "Estado" };

            org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
            for (int c = 0; c < cols.length; c++)
                h.createCell(c).setCellValue(cols[c]);

            int rowIndex = 1;
            for (Base b : lista) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(String.format("%02d", b.getCodigoBase()));
                row.createCell(1).setCellValue(b.getNombre());
                row.createCell(2).setCellValue(b.getDireccion());
                row.createCell(3).setCellValue(b.getEstado());
            }

            for (int c = 0; c < cols.length; c++)
                sheet.autoSizeColumn(c);
            wb.write(fos);

            JOptionPane.showMessageDialog(this, "Archivo .xlsx de bases operativas exportado correctamente.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo exportar el archivo .xlsx de bases operativas: no se pudo generar el archivo de exportación.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limpiarFormularioRegistro() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        cbEstado.setSelectedItem("Activo");
    }

    private void agregarFila(Base b) {
        modeloTabla.addRow(new Object[] {
                b.getCodigoBase(), b.getNombre(), b.getDireccion(), b.getEstado()
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