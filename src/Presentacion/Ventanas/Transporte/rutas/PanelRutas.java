package Presentacion.Ventanas.Transporte.rutas;

import Logica.Servicios.RutaService;
import Logica.Entidades.Ruta;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.util.List;

public class PanelRutas extends JPanel {

    // ================== SERVICE ==================
    private final RutaService rutaService = new RutaService();

    // ===== COLORES =====
    private static final Color BG_MAIN = new Color(15, 30, 60);
    private static final Color BG_PANEL = new Color(25, 45, 90);
    private static final Color BTN_BLUE = new Color(52, 120, 246);
    private static final Color BTN_GOLD = new Color(241, 196, 15);
    private static final Color TXT_LIGHT = new Color(220, 220, 220);

    private static final Color BORDER_DARK = new Color(30, 60, 110);

    // ================== CAMPOS ==================
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JComboBox<String> cbEstado;

    private JTextField txtBuscarCodigo;
    private JTextField txtBuscarNombre;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // ================== BOTONES ==================
    private JButton btnRegistrar;
    private JButton btnActNombre;
    private JButton btnActOrigen;
    private JButton btnActDestino;
    private JButton btnEstado;
    private JButton btnExportar;

    public PanelRutas() {

        setLayout(new BorderLayout(15, 15));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarCampos();
        conectarEventos();

        // ================= PANEL IZQUIERDO =================
        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setOpaque(false);

        panelIzquierdo.add(panelConsulta(), BorderLayout.NORTH);
        panelIzquierdo.add(crearTabla(), BorderLayout.CENTER);

        // ================= PANEL DERECHO (CON SCROLL) =================
        JScrollPane scrollRegistro = new JScrollPane(panelRegistro());
        scrollRegistro.setBorder(BorderFactory.createEmptyBorder());
        scrollRegistro.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRegistro.getVerticalScrollBar().setUnitIncrement(16);
        scrollRegistro.getViewport().setBackground(BG_PANEL);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setOpaque(false);
        panelDerecho.setPreferredSize(new Dimension(320, 0));

        panelDerecho.add(scrollRegistro, BorderLayout.CENTER);

        // ================= ARMADO FINAL =================
        add(panelIzquierdo, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
    }

    private void inicializarCampos() {

        txtCodigo = new JTextField();
        txtCodigo.setEditable(true);

        txtNombre = new JTextField();
        txtOrigen = new JTextField();
        txtDestino = new JTextField();
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        // cbEstado.setEnabled(false); REMOVED to allow interaction
        estilizarCampo(txtNombre);
        estilizarCampo(txtOrigen);
        estilizarCampo(txtDestino);
        estilizarCombo(cbEstado);
        txtBuscarCodigo = new JTextField();
        txtBuscarNombre = new JTextField();
        estilizarCampo(txtCodigo);

        btnRegistrar = new JButton("Registrar");
        btnActNombre = new JButton("Actualizar nombre");
        btnActOrigen = new JButton("Actualizar origen");
        btnActDestino = new JButton("Actualizar destino");
        btnEstado = new JButton("Cambiar estado");
        btnExportar = new JButton("Exportar Excel");
    }

    // =====================================================
    // TABLA
    // =====================================================
    private JScrollPane crearTabla() {

        // ===== MODELO =====
        modeloTabla = new DefaultTableModel(
                new Object[] { "Código", "Nombre", "Origen", "Destino", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // ===== TABLA =====
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(36);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(new Color(22, 44, 86));

        // ❌ nada de selección
        tabla.setRowSelectionAllowed(false);
        tabla.setColumnSelectionAllowed(false);
        tabla.setCellSelectionEnabled(false);
        tabla.setFocusable(false);

        // ❌ nada de líneas blancas
        tabla.setShowGrid(false);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getColumnModel().setColumnMargin(0);

        // ===== ZEBRA (IGUAL A BUSES) =====
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, false, false, row, column);

                // Zebra
                if (row % 2 == 0) {
                    c.setBackground(new Color(22, 44, 86));
                } else {
                    c.setBackground(new Color(26, 50, 96));
                }

                c.setForeground(Color.WHITE);
                c.setOpaque(true);

                // 🔥 BORDE AZUL SIEMPRE

                // Padding interno
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_DARK, 1),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));

                return c;
            }
        });

        // ===== COLUMNA ESTADO (SOLO ACTIVO / INACTIVO) =====
        tabla.getColumnModel().getColumn(4).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {

                        JLabel c = (JLabel) super.getTableCellRendererComponent(
                                table, value, false, false, row, column);

                        c.setHorizontalAlignment(SwingConstants.CENTER);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                        c.setOpaque(true);

                        String estado = value.toString().toUpperCase();
                        if ("ACTIVO".equals(estado)) {
                            c.setBackground(new Color(46, 204, 113));
                            c.setForeground(Color.WHITE);
                        } else {
                            c.setBackground(new Color(231, 76, 60));
                            c.setForeground(Color.WHITE);
                        }

                        // 🔥 BORDE AZUL
                        c.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));

                        return c;
                    }
                });

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(20, 45, 85)); // mismo tono buses
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setOpaque(true);
        header.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, false, false, row, column);

                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(new Color(20, 45, 85));
                l.setForeground(Color.WHITE);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));

                return l;
            }
        });

        // ===== SCROLL =====
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(new Color(22, 44, 86));
        scroll.getViewport().setBackground(new Color(22, 44, 86));

        return scroll;
    }

    private JPanel panelConsulta() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 110)),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)));

        // ===== TÍTULO =====
        JLabel titulo = new JLabel("Consulta");
        titulo.setForeground(TXT_LIGHT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(titulo);
        p.add(Box.createVerticalStrut(10));

        // ===== RADIOS =====
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

        p.add(radios);
        p.add(Box.createVerticalStrut(8));

        // ===== CAMPO BUSCAR (MÁS PEQUEÑO) =====
        // ===== CAMPO BUSCAR (TIPO LÍNEA) =====
        JTextField txtBuscar = new JTextField();
        txtBuscar.setOpaque(false);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Color.WHITE // 🔥 solo línea inferior
        ));
        txtBuscar.setMaximumSize(new Dimension(260, 24));
        txtBuscar.setPreferredSize(new Dimension(260, 24));

        p.add(txtBuscar);
        p.add(Box.createVerticalStrut(10));

        // ===== BOTONES (UNO BAJO OTRO) =====
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListar = new JButton("Listar activas");

        estilizarBoton(btnBuscar, BTN_BLUE);
        estilizarBoton(btnListar, BTN_BLUE);

        btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnListar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnBuscar.setMaximumSize(new Dimension(160, 30));
        btnListar.setMaximumSize(new Dimension(160, 30));

        // Eventos (NO se tocan)
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

        p.add(btnBuscar);
        p.add(Box.createVerticalStrut(8));
        p.add(btnListar);

        return p;
    }

    private JPanel panelRegistro() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 90, 140)),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));

        JLabel titulo = new JLabel("Registro / Edición de Ruta");
        titulo.setForeground(TXT_LIGHT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(titulo);
        p.add(Box.createVerticalStrut(8)); // ⬇ antes 12

        // ===== FORM =====
        p.add(campo("Código", txtCodigo));
        p.add(campo("Nombre", txtNombre));
        p.add(campo("Origen", txtOrigen));
        p.add(campo("Destino", txtDestino));
        p.add(campo("Estado", cbEstado));

        p.add(Box.createVerticalStrut(10)); // ⬇ antes 16

        // ===== BOTONES =====
        estilizarBotonVertical(btnRegistrar, BTN_BLUE,
                "Registrar", "Presentacion/Recursos/icons/basse.png");
        p.add(btnRegistrar);
        p.add(Box.createVerticalStrut(4));

        estilizarBotonVertical(btnActNombre, BTN_GOLD,
                "Actualizar nombre", "Presentacion/Recursos/icons/edittt.png");
        p.add(btnActNombre);
        p.add(Box.createVerticalStrut(4));

        estilizarBotonVertical(btnActOrigen, BTN_GOLD,
                "Actualizar origen", "Presentacion/Recursos/icons/location.png");
        p.add(btnActOrigen);
        p.add(Box.createVerticalStrut(4));

        estilizarBotonVertical(btnActDestino, BTN_GOLD,
                "Actualizar destino", "Presentacion/Recursos/icons/route.png");
        p.add(btnActDestino);
        p.add(Box.createVerticalStrut(6));

        estilizarBotonVertical(btnEstado, BTN_BLUE,
                "Cambiar estado", "Presentacion/Recursos/icons/refressh.png");
        p.add(btnEstado);
        p.add(Box.createVerticalStrut(6));
        estilizarBotonVertical(btnExportar, BTN_BLUE, "Exportar Excel", "Presentacion/Recursos/icons/excel.png");
        p.add(btnExportar);

        // ❌ NADA de VerticalGlue
        // ❌ NADA de PreferredSize gigante

        return p;
    }

    // =====================================================
    // ACCIONES
    // =====================================================
    private void registrarRuta() {

        Ruta r = new Ruta();
        r.setCodigoRuta(txtCodigo.getText().trim());

        r.setNombre(txtNombre.getText().trim());
        r.setOrigen(txtOrigen.getText().trim());
        r.setDestino(txtDestino.getText().trim());
        r.setEstado("Activo");

        String mensaje = rutaService.registrarRuta(r);

        JOptionPane.showMessageDialog(this, mensaje, "Resultado",
                JOptionPane.INFORMATION_MESSAGE);

        listarActivas();
        cbEstado.setEnabled(false);
        txtCodigo.setEditable(true);

    }

    private void buscarPorCodigo() {

        String codigo = txtBuscarCodigo.getText().trim();
        modeloTabla.setRowCount(0);

        // Validar formato (2 dígitos)
        if (!codigo.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la ruta: el código de la ruta no cumple el formato requerido.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorCodigo(codigo);

        if (r == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la ruta: el código de la ruta no existe.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        cargarFormulario(r);
        agregarFila(r);

        JOptionPane.showMessageDialog(this,
                "Consulta de ruta realizada correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarPorNombre() {

        String nombre = txtBuscarNombre.getText().trim();
        modeloTabla.setRowCount(0);

        // Validar formato (≤ 50, letras, ñ, guion, espacio)
        if (!nombre.matches("^[A-Za-zñÑ\\- ]{1,50}$")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la ruta: el nombre de la ruta no cumple el formato requerido.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorNombre(nombre);

        if (r == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la ruta: el nombre de la ruta no existe.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        cargarFormulario(r);
        agregarFila(r);

        JOptionPane.showMessageDialog(this,
                "Consulta de ruta realizada correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void listarActivas() {

        modeloTabla.setRowCount(0);
        List<Ruta> lista = rutaService.listarActivas();

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el listado de rutas activas: no existen rutas activas registradas.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Ruta r : lista) {
            agregarFila(r);
        }

        JOptionPane.showMessageDialog(this,
                "Listado de rutas activas generado correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarNombre() {

        String mensaje = rutaService.actualizarNombre(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim());

        JOptionPane.showMessageDialog(this, mensaje,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);

        listarActivas();
    }

    private void actualizarOrigen() {

        String mensaje = rutaService.actualizarOrigen(
                txtCodigo.getText().trim(),
                txtOrigen.getText().trim());

        JOptionPane.showMessageDialog(this, mensaje,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);

        listarActivas();
    }

    private void actualizarDestino() {

        String mensaje = rutaService.actualizarDestino(
                txtCodigo.getText().trim(),
                txtDestino.getText().trim());

        JOptionPane.showMessageDialog(this, mensaje,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);

        listarActivas();
    }

    private void cambiarEstado() {

        String mensaje = rutaService.cambiarEstado(
                txtCodigo.getText().trim(),
                cbEstado.getSelectedItem().toString());

        JOptionPane.showMessageDialog(this, mensaje,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);

        listarActivas();
    }

    // =====================================================
    // UTILIDADES
    // =====================================================
    private void cargarFormulario(Ruta r) {
        txtCodigo.setText(r.getCodigoRuta());
        txtCodigo.setEditable(false);

        txtNombre.setText(r.getNombre());
        txtOrigen.setText(r.getOrigen());
        txtDestino.setText(r.getDestino());

        cbEstado.setSelectedItem(r.getEstado());
        cbEstado.setEnabled(true); // 🔓 HABILITADO SOLO EN EDICIÓN
    }

    private void agregarFila(Ruta r) {
        modeloTabla.addRow(new Object[] {
                r.getCodigoRuta(),
                r.getNombre(),
                r.getOrigen(),
                r.getDestino(),
                r.getEstado()
        });
    }

    private void estilizarBoton(JButton b, Color c) {

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setBackground(c);
        b.setForeground(Color.WHITE); // ✅ BLANCO como Bases

        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); // ✅ mismo tamaño
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarRadio(JRadioButton r) {
        r.setOpaque(false);
        r.setForeground(TXT_LIGHT);
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        return l;
    }

    private void setIcon(JButton b, String path) {
        try {
            java.net.URL url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                b.setIcon(new ImageIcon(url));
                b.setHorizontalAlignment(SwingConstants.LEFT);
                b.setIconTextGap(8);
            }
        } catch (Exception e) {
            // No hace nada si no hay icono
        }
    }

    private JPanel campo(String texto, JComponent input) {

        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(texto);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        input.setPreferredSize(new Dimension(0, 24)); // ⬅ antes 28

        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44)); // ⬅ antes 52
        return p;
    }

    private void estilizarBotonVertical(JButton b, Color c, String texto, String icono) {

        b.setText(texto);
        estilizarBoton(b, c);

        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36)); // ⬅ antes 36
        b.setMinimumSize(new Dimension(0, 36));
        b.setPreferredSize(new Dimension(0, 36));

        setIcon(b, icono);
    }

    private void conectarEventos() {

        btnRegistrar.addActionListener(e -> registrarRuta());
        btnActNombre.addActionListener(e -> actualizarNombre());
        btnActOrigen.addActionListener(e -> actualizarOrigen());
        btnActDestino.addActionListener(e -> actualizarDestino());
        btnEstado.addActionListener(e -> cambiarEstado());
        btnExportar.addActionListener(e -> exportarExcelRutas());
    }

    private void estilizarCampo(JTextField t) {
        t.setOpaque(true);
        t.setBackground(BG_PANEL); // mismo azul del panel
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);

        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 110), 1), // borde azul
                BorderFactory.createEmptyBorder(4, 8, 4, 8) // padding
        ));

        t.setPreferredSize(new Dimension(0, 28));
    }

    private void estilizarCombo(JComboBox<String> cb) {

        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton("▼");
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setForeground(Color.WHITE);
                b.setBackground(BG_PANEL);
                b.setOpaque(true);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g,
                    Rectangle bounds, boolean hasFocus) {
                g.setColor(BG_PANEL); // 🔥 elimina el blanco
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        cb.setBackground(BG_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 110), 1));
        cb.setFocusable(false);

        // Renderer de la lista
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel l = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                l.setOpaque(true);
                l.setForeground(Color.WHITE);

                if (isSelected) {
                    l.setBackground(new Color(30, 60, 110));
                } else {
                    l.setBackground(BG_PANEL);
                }

                l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return l;
            }
        });
    }

    private void exportarExcelRutas() {

    List<Logica.Entidades.Ruta> lista = rutaService.listarTodas();

    if (lista == null || lista.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el archivo .xlsx de rutas: no existen rutas registradas.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser ch = new JFileChooser();
    ch.setSelectedFile(new java.io.File("rutas.xlsx"));
    if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

    try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
         java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Rutas");

        // Header
        org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
        String[] cols = {"Código", "Nombre", "Origen", "Destino", "Estado"};
        for (int c = 0; c < cols.length; c++) {
            h.createCell(c).setCellValue(cols[c]);
        }

        // Datos
        int rowIndex = 1;
        for (Logica.Entidades.Ruta r : lista) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(r.getCodigoRuta());
            row.createCell(1).setCellValue(r.getNombre());
            row.createCell(2).setCellValue(r.getOrigen());
            row.createCell(3).setCellValue(r.getDestino());
            row.createCell(4).setCellValue(r.getEstado());
        }

        for (int c = 0; c < cols.length; c++) sheet.autoSizeColumn(c);

        wb.write(fos);

        JOptionPane.showMessageDialog(this,
                "Archivo .xlsx de rutas exportado correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el archivo .xlsx de rutas: no se pudo generar el archivo de exportación.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }
}


}
