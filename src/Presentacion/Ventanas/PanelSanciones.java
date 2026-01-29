package Presentacion.Ventanas;

import Logica.DAO.IncidenciaDAO;
import Logica.DAO.SancionDAO;
import Logica.Entidades.Incidencia;
import Logica.Entidades.Sancion;
import Logica.Servicios.IncidenciaServicio;
import Logica.Servicios.SancionServicio;
import Logica.Servicios.ReglaSancionServicio;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

// ===== Apache POI =====
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PanelSanciones extends JPanel {

    // ===== COLORES (igual Transporte) =====
    private static final Color BG_MAIN  = new Color(15, 30, 60);
    private static final Color BG_PANEL = new Color(25, 45, 90);
    private static final Color BG_CARD  = new Color(22, 44, 86);
    private static final Color BTN_BLUE = new Color(52, 120, 246);
    private static final Color BTN_GOLD = new Color(241, 196, 15);
    private static final Color TXT_LIGHT = new Color(220, 220, 220);
    private static final Color BORDER = new Color(45, 80, 130);

    private DefaultTableModel modelIncidencias;
    private DefaultTableModel modelSanciones;

    // Registro Incidencia
    private JTextField txtBus, txtFechaInc, txtRuta;
    private JTextField txtTipoRegla;
    private JTextField txtPosiciones;
    private JComboBox<String> cbTipo;
    // Filtros consultas
private JTextField txtFiltroBus;
private JTextField txtFiltroRuta;
private JTextField txtPeriodoIni;
private JTextField txtPeriodoFin;
private JComboBox<String> cbEstadoFiltro;
private JTable tablaSancionesConsulta;
private JComboBox<String> cbPeriodo;      // "N/A", "Día", "Mes"
private JTextField txtPeriodoValor;       // valor del periodo (YYYY-MM-DD o YYYY-MM)


// "Todos", "ACTIVA", "ANULADA"


    // Aplicar / Anular
    private JTextField txtIdIncidencia, txtFechaSancion, txtResponsable;

    public PanelSanciones() {

        setLayout(new BorderLayout(12, 12));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(crearHeader(), BorderLayout.NORTH);

        modelIncidencias = new DefaultTableModel(
                new String[]{"Código", "Bus", "Fecha", "Ruta", "Tipo", "Estado"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        modelSanciones = new DefaultTableModel(
                new String[]{"Código", "Incidencia", "Fecha", "Responsable", "Estado"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTabbedPane tabs = new JTabbedPane();
        estilizarTabs(tabs);

        // ===== INICIALIZAR FILTROS CONSULTAS (para que no salgan "no declarada") =====
txtFiltroBus = new JTextField();
txtFiltroRuta = new JTextField();
txtPeriodoIni = new JTextField();
txtPeriodoFin = new JTextField();

cbPeriodo = new JComboBox<>(new String[]{"N/A", "Día", "Mes"});
txtPeriodoValor = new JTextField();

cbEstadoFiltro = new JComboBox<>(new String[]{"Todos", "ACTIVA", "ANULADA"});

// Estilo igual al resto
estilizarCampo(txtFiltroBus);
estilizarCampo(txtFiltroRuta);
estilizarCampo(txtPeriodoIni);
estilizarCampo(txtPeriodoFin);
estilizarCampo(txtPeriodoValor);

estilizarCombo(cbPeriodo);
estilizarCombo(cbEstadoFiltro);

// Tamaños para que se vean bien abajo
txtFiltroBus.setPreferredSize(new Dimension(70, 32));
txtFiltroRuta.setPreferredSize(new Dimension(60, 32));
txtPeriodoIni.setPreferredSize(new Dimension(95, 32));
txtPeriodoFin.setPreferredSize(new Dimension(95, 32));
txtPeriodoValor.setPreferredSize(new Dimension(90, 32));
cbPeriodo.setPreferredSize(new Dimension(70, 32));
cbEstadoFiltro.setPreferredSize(new Dimension(95, 32));


        tabs.addTab("Registro", crearTabRegistro());
        tabs.addTab("Consultas", crearTabConsultas());

        add(tabs, BorderLayout.CENTER);
        cargarTiposIncidenciaDesdeReglas();

        // Carga inicial SIN mensajes
        cargarIncidencias();
        cargarSanciones();
    }

    // ================= HEADER =================
    private JPanel crearHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sanciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        JLabel subt = new JLabel("Registro, aplicación, consultas y exportación");
        subt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subt.setForeground(TXT_LIGHT);

        p.add(titulo);
        p.add(Box.createVerticalStrut(6));
        p.add(subt);

        return p;
    }

    // =====================================================
    // TAB REGISTRO
    // =====================================================
    private JPanel crearTabRegistro() {

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setOpaque(false);

        // ===== Campos =====
        txtBus = new JTextField();
        txtFechaInc = new JTextField(LocalDate.now().toString());
        txtRuta = new JTextField();
        cbTipo = new JComboBox<>();

        txtIdIncidencia = new JTextField();
        txtFechaSancion = new JTextField(LocalDate.now().toString());
        txtResponsable = new JTextField();
        // ===== Regla sanción =====
txtTipoRegla = new JTextField();
txtPosiciones = new JTextField();

estilizarCampo(txtTipoRegla);
estilizarCampo(txtPosiciones);



        estilizarCampo(txtBus);
        estilizarCampo(txtFechaInc);
        estilizarCampo(txtRuta);
        estilizarCombo(cbTipo);

        estilizarCampo(txtIdIncidencia);
        estilizarCampo(txtFechaSancion);
        estilizarCampo(txtResponsable);

        // ===== ARRIBA: 2 cards =====
        JPanel arriba = new JPanel(new GridLayout(1, 3, 14, 0));
arriba.setOpaque(false);


        // ---- Registrar Incidencia ----
        JPanel formInc = new JPanel(new GridLayout(4, 2, 10, 10));
        formInc.setOpaque(false);

        formInc.add(label("Bus"));
        formInc.add(txtBus);
        formInc.add(label("Fecha"));
        formInc.add(txtFechaInc);
        formInc.add(label("Ruta"));
        formInc.add(txtRuta);
        formInc.add(label("Tipo Incidencia"));
        formInc.add(cbTipo);

        JButton btnRegistrarInc = new JButton("Registrar Incidencia");
        estilizarBoton(btnRegistrarInc, BTN_BLUE, "Presentacion/Recursos/icons/basse.png");
        ajustarBotonGrande(btnRegistrarInc);
        btnRegistrarInc.addActionListener(e -> registrarIncidencia());

        JPanel cardInc = cardSimple("Registrar Incidencia", formInc, btnRegistrarInc);


        // ---- Aplicar / Anular ----
        JPanel formSan = new JPanel(new GridLayout(3, 2, 10, 10));
        formSan.setOpaque(false);

        formSan.add(label("Código Incidencia"));
        formSan.add(txtIdIncidencia);
        formSan.add(label("Fecha Aplicación"));
        formSan.add(txtFechaSancion);
        formSan.add(label("Responsable"));
        formSan.add(txtResponsable);

        JButton btnAplicar = new JButton("Aplicar Sanción");
        JButton btnAnular  = new JButton("Anular Sanción");

        estilizarBoton(btnAplicar, BTN_GOLD, "Presentacion/Recursos/icons/edittt.png");
        estilizarBoton(btnAnular,  BTN_BLUE, "Presentacion/Recursos/icons/powwer_off.png");
        ajustarBotonGrande(btnAplicar);
        ajustarBotonGrande(btnAnular);

        btnAplicar.addActionListener(e -> aplicarSancion());
        btnAnular.addActionListener(e -> anularSancion());

        JPanel acciones = new JPanel(new GridLayout(2, 1, 0, 10));
        acciones.setOpaque(false);
        acciones.add(btnAplicar);
        acciones.add(btnAnular);

        JPanel cardSan = cardSimple("Aplicar / Anular Sanción", formSan, acciones);

        // ---- Regla de sanción (rsa13, rsa14) ----
JPanel formRegla = new JPanel(new GridLayout(2, 2, 10, 10));
formRegla.setOpaque(false);

formRegla.add(label("Tipo Incidencia (3 dígitos)"));
formRegla.add(txtTipoRegla);
formRegla.add(label("Posiciones (>= 1)"));
formRegla.add(txtPosiciones);

JButton btnRegistrarRegla = new JButton("Registrar Regla");
JButton btnActualizarRegla = new JButton("Actualizar Regla");

estilizarBoton(btnRegistrarRegla, BTN_BLUE, "Presentacion/Recursos/icons/basse.png");
estilizarBoton(btnActualizarRegla, BTN_GOLD, "Presentacion/Recursos/icons/edittt.png");
ajustarBotonGrande(btnRegistrarRegla);
ajustarBotonGrande(btnActualizarRegla);

btnRegistrarRegla.addActionListener(e -> registrarRegla());
btnActualizarRegla.addActionListener(e -> actualizarRegla());

JPanel accionesRegla = new JPanel(new GridLayout(2, 1, 0, 10));
accionesRegla.setOpaque(false);
accionesRegla.add(btnRegistrarRegla);
accionesRegla.add(btnActualizarRegla);

JPanel cardRegla = cardSimple("Reglas de Sanción", formRegla, accionesRegla);


        arriba.add(cardInc);
arriba.add(cardSan);
arriba.add(cardRegla);

        // ===== ABAJO: 2 tablas =====
        JPanel abajo = new JPanel(new GridLayout(1, 2, 14, 0));
        abajo.setOpaque(false);

        abajo.add(tablaCard("Incidencias", crearTablaIncidencias()));
        abajo.add(tablaCard("Sanciones", crearTablaSancionesRegistro()));

        root.add(arriba, BorderLayout.NORTH);
        root.add(abajo, BorderLayout.CENTER);

        return root;
    }

    // =====================================================
    // TAB CONSULTAS
    // =====================================================
   private JPanel crearTabConsultas() {

    JPanel root = new JPanel(new BorderLayout(10, 10));
    root.setOpaque(false);
    root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // ===== TABLA =====
    tablaSancionesConsulta = new JTable(modelSanciones);
    estilizarTabla(tablaSancionesConsulta);

    JScrollPane scroll = new JScrollPane(tablaSancionesConsulta);
    scroll.setBorder(BorderFactory.createLineBorder(BORDER));
    scroll.getViewport().setBackground(BG_CARD);
    scroll.setBackground(BG_CARD);

    root.add(scroll, BorderLayout.CENTER);

    // ===== BARRA INFERIOR =====
    JPanel barraInferior = new JPanel(new BorderLayout(12, 0));
    barraInferior.setBackground(BG_PANEL);
    barraInferior.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    // ---- BOTÓN CONSULTAR (izquierda) ----
    JButton btnConsultar = new JButton("Consultar");
    estilizarBoton(btnConsultar, BTN_BLUE, "Presentacion/Recursos/icons/searchh.png");
    btnConsultar.setPreferredSize(new Dimension(170, 42));
    btnConsultar.addActionListener(e -> consultarSancionesFiltrado());

    JPanel panelConsultar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panelConsultar.setOpaque(false);
    panelConsultar.add(btnConsultar);

    barraInferior.add(panelConsultar, BorderLayout.WEST);

    // ---- FILTROS (centro) ----
    JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    filtros.setOpaque(false);

    filtros.add(label("Bus"));
    filtros.add(txtFiltroBus);

    filtros.add(label("Ruta"));
    filtros.add(txtFiltroRuta);

    filtros.add(label("Período"));
    filtros.add(cbPeriodo);
    filtros.add(txtPeriodoValor);

    filtros.add(label("Estado"));
    filtros.add(cbEstadoFiltro);

    barraInferior.add(filtros, BorderLayout.CENTER);

    // ---- EXPORTAR EXCEL (derecha) ----
    JButton btnExportar = new JButton("Exportar Excel (rsa12)");
    estilizarBoton(btnExportar, BTN_BLUE, "Presentacion/Recursos/icons/excel.png");
    btnExportar.setPreferredSize(new Dimension(260, 42));
    btnExportar.addActionListener(e -> exportarExcelSanciones());

    JPanel panelExportar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    panelExportar.setOpaque(false);
    panelExportar.add(btnExportar);

    barraInferior.add(panelExportar, BorderLayout.EAST);

    root.add(barraInferior, BorderLayout.SOUTH);

    return root;
}







    // =====================================================
    // ACCIONES
    // =====================================================
    private void registrarIncidencia() {

        String bus = txtBus.getText().trim();
        String ruta = txtRuta.getText().trim();
        String tipo = cbTipo.getSelectedItem() == null ? "" : cbTipo.getSelectedItem().toString().trim();
        String fechaTxt = txtFechaInc.getText().trim();

        if (bus.isEmpty() || ruta.isEmpty() || tipo.isEmpty() || fechaTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la incidencia: los datos ingresados no son válidos.");
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTxt);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la incidencia: los datos ingresados no son válidos.");
            return;
        }

        IncidenciaServicio servicio = new IncidenciaServicio();
        String msg = servicio.registrarIncidencia(bus, fecha, ruta, tipo);

        JOptionPane.showMessageDialog(this, msg);
        cargarIncidencias();
    }

    private void aplicarSancion() {

        String codInc = txtIdIncidencia.getText().trim();
        String fechaTxt = txtFechaSancion.getText().trim();
        String responsable = txtResponsable.getText().trim();

        if (codInc.isEmpty() || fechaTxt.isEmpty() || responsable.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo aplicar la sanción: los datos ingresados no son válidos.");
            return;
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTxt);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo aplicar la sanción: los datos ingresados no son válidos.");
            return;
        }

        SancionServicio servicio = new SancionServicio();
        String msg = servicio.aplicarSancion(codInc, fecha, responsable);

        JOptionPane.showMessageDialog(this, msg);
        cargarSanciones();
    }

    private void anularSancion() {

        String codigo = JOptionPane.showInputDialog(this, "Código de la sanción");
        if (codigo == null) return;

        codigo = codigo.trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo anular la sanción: la sanción no existe.");
            return;
        }

        SancionServicio servicio = new SancionServicio();
        String msg = servicio.anularSancion(codigo);

        JOptionPane.showMessageDialog(this, msg);
        cargarSanciones();
    }
    private void registrarRegla() {
    try {
        String tipo = txtTipoRegla.getText().trim();
        int posiciones = Integer.parseInt(txtPosiciones.getText().trim());

        ReglaSancionServicio s = new ReglaSancionServicio();
        String msg = s.registrarRegla(tipo, posiciones);

        JOptionPane.showMessageDialog(this, msg);

        cargarTiposIncidenciaDesdeReglas();


    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo registrar la regla de sanción: los datos ingresados no son válidos.");
    }
}

private void actualizarRegla() {
    try {
        String tipo = txtTipoRegla.getText().trim();
        int posiciones = Integer.parseInt(txtPosiciones.getText().trim());

        ReglaSancionServicio s = new ReglaSancionServicio();
        String msg = s.actualizarRegla(tipo, posiciones);

        JOptionPane.showMessageDialog(this, msg);
        cargarTiposIncidenciaDesdeReglas();


    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo actualizar la regla de sanción: los datos ingresados no son válidos.");
    }
}


    // =====================================================
    // CARGA
    // =====================================================
    private void cargarIncidencias() {
        try {
            modelIncidencias.setRowCount(0);
            IncidenciaDAO dao = new IncidenciaDAO();
            List<Incidencia> lista = dao.listar();

            for (Incidencia i : lista) {
                modelIncidencias.addRow(new Object[]{
                        i.getCodigoIncidencia(),
                        i.getCodigoBus(),
                        i.getFechaEvento(),
                        i.getCodigoRuta(),
                        i.getCodigoTipo(),
                        i.getEstado()
                });
            }
        } catch (Exception ignored) { }
    }

    private void cargarSanciones() {
        try {
            modelSanciones.setRowCount(0);
            SancionDAO dao = new SancionDAO();
            List<Sancion> lista = dao.consultar();

            for (Sancion s : lista) {
                modelSanciones.addRow(new Object[]{
                        s.getCodigoSancion(),
                        s.getCodigoIncidencia(),
                        s.getFechaAplicacion(),
                        s.getResponsable(),
                        s.getEstado()
                });
            }
        } catch (Exception ignored) { }
    }
    private void cargarTiposIncidenciaDesdeReglas() {
    try {
        cbTipo.removeAllItems();

        java.util.List<String> tipos = new Logica.DAO.ReglaSancionDAO().listarTipos();

        if (tipos == null || tipos.isEmpty()) {
            // No inventamos mensajes: solo dejamos el combo vacío
            return;
        }

        for (String t : tipos) {
            cbTipo.addItem(t);
        }

        cbTipo.setSelectedIndex(0);

    } catch (Exception e) {
        // si falla, lo dejamos como está sin reventar la UI
    }
    
}
private void consultarSancionesFiltrado() {

    String bus = txtFiltroBus.getText().trim();
    String ruta = txtFiltroRuta.getText().trim();

    String periodo = cbPeriodo.getSelectedItem() == null ? "N/A" : cbPeriodo.getSelectedItem().toString();
    String periodoVal = txtPeriodoValor.getText().trim();

    String estadoSel = cbEstadoFiltro.getSelectedItem() == null ? "Todos" : cbEstadoFiltro.getSelectedItem().toString();
    String estado = "Todos".equals(estadoSel) ? null : estadoSel;

    // Validación ERS: si no hay filtros => inválido
    boolean hayFiltro = false;

    if (!bus.isEmpty()) {
        hayFiltro = true;
        // tu incidencia usa codigo_bus char(4) normalmente
        if (!bus.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar la consulta: los datos ingresados no son válidos.");
            return;
        }
    }

    if (!ruta.isEmpty()) {
        hayFiltro = true;
        if (!ruta.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar la consulta: los datos ingresados no son válidos.");
            return;
        }
    }

    java.time.LocalDate dia = null;
    java.time.YearMonth mes = null;

    if (!"N/A".equals(periodo)) {
        hayFiltro = true;

        try {
            if ("Día".equals(periodo)) {
                // YYYY-MM-DD
                if (periodoVal.isEmpty()) throw new RuntimeException();
                dia = java.time.LocalDate.parse(periodoVal);
            } else if ("Mes".equals(periodo)) {
                // YYYY-MM
                if (periodoVal.isEmpty()) throw new RuntimeException();
                mes = java.time.YearMonth.parse(periodoVal);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar la consulta: los datos ingresados no son válidos.");
            return;
        }
    }

    if (estado != null) hayFiltro = true;

    if (!hayFiltro) {
        JOptionPane.showMessageDialog(this,
                "No se pudo realizar la consulta: los datos ingresados no son válidos.");
        return;
    }

    try {
        List<Sancion> lista = new SancionDAO().consultarFiltrado(bus, ruta, dia, mes, estado);

        modelSanciones.setRowCount(0);

        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No existen sanciones que cumplan con el criterio consultado.");
            return;
        }

        for (Sancion s : lista) {
            modelSanciones.addRow(new Object[]{
                    s.getCodigoSancion(),
                    s.getCodigoIncidencia(),
                    s.getFechaAplicacion(),
                    s.getResponsable(),
                    s.getEstado()
            });
        }

        JOptionPane.showMessageDialog(this,
                "Consulta de sanciones realizada correctamente.");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo realizar la consulta: los datos ingresados no son válidos.");
    }
}



    // =====================================================
    // EXPORTAR EXCEL (rsa12)
    // =====================================================
    private void exportarExcelSanciones() {

    // ERS: si no hay info en la tabla
    if (modelSanciones.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el listado de sanciones: no existe información para exportar.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new java.io.File("sanciones.xlsx"));
    chooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
        return;

    java.io.File file = chooser.getSelectedFile();
    if (!file.getName().toLowerCase().endsWith(".xlsx")) {
        file = new java.io.File(file.getAbsolutePath() + ".xlsx");
    }

    try (Workbook wb = new XSSFWorkbook();
         FileOutputStream fos = new FileOutputStream(file)) {

        Sheet sheet = wb.createSheet("Sanciones");

        // Header bold
        CellStyle headStyle = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setBold(true);
        headStyle.setFont(font);

        Row header = sheet.createRow(0);
        for (int c = 0; c < modelSanciones.getColumnCount(); c++) {
            Cell cell = header.createCell(c);
            cell.setCellValue(modelSanciones.getColumnName(c));
            cell.setCellStyle(headStyle);
        }

        // Datos desde la TABLA (no depende de DAO)
        for (int r = 0; r < modelSanciones.getRowCount(); r++) {
            Row row = sheet.createRow(r + 1);
            for (int c = 0; c < modelSanciones.getColumnCount(); c++) {
                row.createCell(c).setCellValue(String.valueOf(modelSanciones.getValueAt(r, c)));
            }
        }

        for (int c = 0; c < modelSanciones.getColumnCount(); c++) {
            sheet.autoSizeColumn(c);
        }

        wb.write(fos);

        JOptionPane.showMessageDialog(this,
                "Archivo .xlsx de sanciones exportado correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el listado de sanciones: no existe información para exportar.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }
}



    // =====================================================
    // UI HELPERS
    // =====================================================
    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private void estilizarCampo(JTextField t) {
        t.setBackground(new Color(18, 36, 64));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setUI(new BasicComboBoxUI() {
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
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(BG_PANEL);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        cb.setBackground(BG_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        cb.setFocusable(false);
    }

    private void estilizarBoton(JButton b, Color c, String icon) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);

        try {
            URL u = getClass().getClassLoader().getResource(icon);
            if (u != null) b.setIcon(new ImageIcon(u));
        } catch (Exception ignored) { }
    }

    private void ajustarBotonGrande(JButton b) {
        b.setPreferredSize(new Dimension(0, 44));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(12);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private JPanel cardSimple(String titulo, JPanel contenido, JComponent footer) {

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel t = new JLabel(titulo, SwingConstants.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));

        p.add(t, BorderLayout.NORTH);
        p.add(contenido, BorderLayout.CENTER);
        if (footer != null) p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    private JPanel tablaCard(String titulo, JScrollPane tablaSP) {

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel t = new JLabel(titulo);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));

        p.add(t, BorderLayout.NORTH);
        p.add(tablaSP, BorderLayout.CENTER);

        return p;
    }

    private JScrollPane crearTablaIncidencias() {
        JTable t = new JTable(modelIncidencias);
        estilizarTabla(t);

        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);
        return sp;
    }

    private JScrollPane crearTablaSancionesRegistro() {
        JTable t = new JTable(modelSanciones);
        estilizarTabla(t);

        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);
        return sp;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(26);
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(BG_CARD);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer();
        cell.setOpaque(true);
        cell.setForeground(Color.WHITE);
        cell.setBackground(BG_CARD);
        cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(cell);
        }

        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel l = new JLabel(String.valueOf(value));
                l.setOpaque(true);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(new Color(20, 45, 85));
                l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setBorder(BorderFactory.createLineBorder(BORDER, 1));
                return l;
            }
        });
    }

    private void estilizarTabs(JTabbedPane tabs) {
        tabs.setOpaque(true);
        tabs.setBackground(BG_MAIN);
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(6, 6, 6, 6);
                contentBorderInsets = new Insets(0, 0, 0, 0);
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? BG_PANEL : BG_MAIN);
                g.fillRect(x, y, w, h);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                g.setColor(BORDER);
                g.drawRect(0, 0, tabs.getWidth() - 1, tabs.getHeight() - 1);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                g.setColor(BORDER);
                g.drawRect(x, y, w, h);
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                               Rectangle[] rects, int tabIndex,
                                               Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                // sin foco blanco
            }
        });
    }
}


