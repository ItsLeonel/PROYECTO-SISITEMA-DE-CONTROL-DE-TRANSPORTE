package Presentacion.Ventanas.Transporte.bases;

import Logica.Entidades.Base;
import Logica.Servicios.BaseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;


public class PanelBases extends JPanel {

    private final BaseService baseService = new BaseService();

    private static final Color BG_MAIN = new Color(15, 30, 60);
    private static final Color BG_PANEL = new Color(25, 45, 90);
    private static final Color BTN_BLUE = new Color(52, 120, 246);
    private static final Color BTN_GOLD = new Color(241, 196, 15);
    private static final Color TXT_LIGHT = new Color(220, 220, 220);

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JComboBox<String> cbEstado;
    private JTextField txtBuscar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JButton btnRegistrar;
    private JButton btnActualizarNombre;
    private JButton btnCambiarEstado;
    private JButton btnBuscar;
    private JButton btnListarActivas;
    private JButton btnNuevo;
    private JButton btnExportar;


    public PanelBases() {

        setLayout(new BorderLayout(15, 15));
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarComponentes();

        JPanel izquierda = new JPanel(new BorderLayout(0, 12));
        izquierda.setOpaque(false);
        izquierda.add(panelConsulta(), BorderLayout.NORTH);
        izquierda.add(crearTabla(), BorderLayout.CENTER);

        JPanel derecha = panelDerecho();

        add(izquierda, BorderLayout.CENTER);
        add(derecha, BorderLayout.EAST);

        conectarEventos();
    }

    // ================= INIT =================
    private void inicializarComponentes() {

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDireccion = new JTextField();
        txtBuscar = new JTextField();

        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        // cbEstado.setEnabled(false); REMOVED

        btnRegistrar = new JButton("Registrar");
        btnActualizarNombre = new JButton("Actualizar nombre");
        btnCambiarEstado = new JButton("Cambiar estado");
        btnBuscar = new JButton("Buscar");
        btnListarActivas = new JButton("Listar activas");
        btnNuevo = new JButton("Nuevo / Limpiar");
estilizarBoton(btnNuevo, BTN_BLUE, "Presentacion/Recursos/icons/rrefressh.png");

        estilizarCampo(txtCodigo);
        estilizarCampo(txtNombre);
        estilizarCampo(txtDireccion);
        estilizarCampo(txtBuscar);
        estilizarCombo(cbEstado);

        estilizarBoton(btnRegistrar, BTN_BLUE, "Presentacion/Recursos/icons/basse.png");
        estilizarBoton(btnActualizarNombre, BTN_GOLD, "Presentacion/Recursos/icons/edittt.png");
        estilizarBoton(btnCambiarEstado, BTN_GOLD, "Presentacion/Recursos/icons/powwer_off.png");
        estilizarBoton(btnBuscar, BTN_BLUE, "Presentacion/Recursos/icons/searchh.png");
        estilizarBoton(btnListarActivas, BTN_BLUE, "Presentacion/Recursos/icons/rrefressh.png");
       

        btnExportar = new JButton("Exportar Excel");
estilizarBoton(btnExportar, BTN_BLUE, "Presentacion/Recursos/icons/excel.png");

    }

    // ================= PANEL DERECHO =================
    private JPanel panelDerecho() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(360, 0));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 110)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titulo = new JLabel("Registro / Edición de Base");
        titulo.setForeground(TXT_LIGHT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        p.add(titulo);
        p.add(Box.createVerticalStrut(16));
        p.add(panelFormulario());
        p.add(Box.createVerticalStrut(20));
        p.add(panelAcciones());
        p.add(Box.createVerticalGlue());

        return p;
    }

    private JPanel panelFormulario() {

        JPanel f = new JPanel(new GridLayout(4, 2, 8, 12));
        f.setOpaque(false);

        f.add(label("Código"));
        f.add(txtCodigo);
        f.add(label("Nombre"));
        f.add(txtNombre);
        f.add(label("Dirección"));
        f.add(txtDireccion);
        f.add(label("Estado"));
        f.add(cbEstado);

        return f;
    }

    private JPanel panelAcciones() {

        JPanel a = new JPanel();
        a.setOpaque(false);
        a.setLayout(new BoxLayout(a, BoxLayout.Y_AXIS));

        a.add(btnRegistrar);
        a.add(Box.createVerticalStrut(12));
        a.add(btnActualizarNombre);
        a.add(Box.createVerticalStrut(8));
        a.add(btnCambiarEstado);
        a.add(Box.createVerticalStrut(8));
a.add(btnNuevo);
a.add(Box.createVerticalStrut(12));
a.add(btnExportar);
Dimension size = new Dimension(Integer.MAX_VALUE, 42);

JButton[] botones = {
        btnRegistrar,
        btnActualizarNombre,
        btnCambiarEstado,
        btnNuevo,
        btnExportar
};

for (JButton b : botones) {
    b.setMaximumSize(size);

    // ✅ IMPORTANTE: NUNCA uses width=0 porque se encogen
    b.setPreferredSize(new Dimension(260, 42));
    b.setMinimumSize(new Dimension(260, 42));

    b.setAlignmentX(Component.CENTER_ALIGNMENT);
    b.setHorizontalAlignment(SwingConstants.LEFT);
    b.setIconTextGap(12);

    b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
}



        return a;
    }

    // ================= PANEL CONSULTA =================
    private JPanel panelConsulta() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 110)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel t = new JLabel("Consulta por nombre");
        t.setForeground(TXT_LIGHT);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtBuscar.setOpaque(false);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

        btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnListarActivas.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(t);
        p.add(Box.createVerticalStrut(12));
        p.add(txtBuscar);
        p.add(Box.createVerticalStrut(16));
        p.add(btnBuscar);
        p.add(Box.createVerticalStrut(10));
        p.add(btnListarActivas);

        return p;
    }

    // ================= TABLA =================
    private JScrollPane crearTabla() {

        modeloTabla = new DefaultTableModel(
                new Object[] { "Código", "Nombre", "Dirección", "Estado" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(new Color(22, 44, 86));
        tabla.setShowGrid(false);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(22, 44, 86));

        return sp;
    }

    // ================= EVENTOS =================
    private void conectarEventos() {
        btnRegistrar.addActionListener(e -> registrarBase());
        btnActualizarNombre.addActionListener(e -> actualizarNombre());
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
        btnBuscar.addActionListener(e -> buscar());
        btnListarActivas.addActionListener(e -> listarActivas());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnExportar.addActionListener(e -> exportarExcelBases());


    }

    // ================= ACCIONES =================

    private void buscar() {

        modeloTabla.setRowCount(0);

        String nombre = txtBuscar.getText().trim();

        // 1. Pedir el MENSAJE al Service (rtr16)
        String msg = baseService.consultarBasePorNombre(nombre);

        // 2. Mostrar SIEMPRE el mensaje
        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        // 3. Si la consulta NO fue correcta, salir
        if (!msg.equals("Consulta de base operativa realizada correctamente."))
            return;

        // 4. Obtener el objeto (solo si fue correcta)
        Base b = baseService.obtenerBasePorNombre(nombre);

        if (b == null)
            return; // seguridad extra

        cargarFormulario(b);
        agregarFila(b);
    }

    private void actualizarNombre() {

        int codigo;
        try {
            codigo = Integer.parseInt(txtCodigo.getText().trim());
        } catch (Exception e) {
            return; // NO inventar mensaje (IEEE no define)
        }

        String msg = baseService.actualizarNombre(
                codigo,
                txtNombre.getText().trim());

        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        listarActivas();
    }

    private void cambiarEstado() {

        String codigoTxt = txtCodigo.getText().trim(); // 👈 mantener "01"

        String estado = cbEstado.getSelectedItem().toString();

        String msg = baseService.cambiarEstado(
                Integer.parseInt(codigoTxt), // el Service valida formato
                estado);

        JOptionPane.showMessageDialog(
                this,
                msg,
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);

        if (msg.equals("Estado de la base operativa actualizado correctamente.")) {
            listarActivas();
        }
    }

    private void registrarBase() {

    String codigoTxt = txtCodigo.getText().trim();

    // VALIDAR FORMATO IEEE (2 dígitos exactos)
    if (!codigoTxt.matches("\\d{2}")) {
        JOptionPane.showMessageDialog(this,
                "No se pudo registrar la base operativa: el código de la base no cumple el formato requerido.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    Base b = new Base();
    b.setNombre(txtNombre.getText().trim());
    b.setDireccion(txtDireccion.getText().trim());
    b.setCodigoBase(Integer.parseInt(codigoTxt));
    b.setEstado(cbEstado.getSelectedItem().toString());

    String msg = baseService.registrarBase(b);

    JOptionPane.showMessageDialog(this,
            msg,
            "Resultado",
            JOptionPane.INFORMATION_MESSAGE);

    limpiarFormulario();
    listarActivas();
}


    private void listarActivas() {

        modeloTabla.setRowCount(0);
        List<Base> lista = baseService.listarActivas();

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el listado de bases operativas activas: no existen bases operativas activas registradas.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Base b : lista)
            agregarFila(b);

        JOptionPane.showMessageDialog(this,
                "Listado de bases operativas activas generado correctamente.",
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= UTILIDADES =================
    private void cargarFormulario(Base b) {

        txtCodigo.setText(String.format("%02d", b.getCodigoBase()));

        txtCodigo.setEditable(false);

        txtNombre.setText(b.getNombre());
        txtDireccion.setText(b.getDireccion());

        cbEstado.setSelectedItem(b.getEstado());
        cbEstado.setEnabled(true);
    }

    private void limpiarFormulario() {
    txtCodigo.setText("");
    txtCodigo.setEditable(true);

    txtNombre.setText("");
    txtDireccion.setText("");

    // Activo por defecto, pero editable
    cbEstado.setSelectedItem("Activo");
    cbEstado.setEnabled(true);
}


    private void agregarFila(Base b) {
        modeloTabla.addRow(new Object[] {
                b.getCodigoBase(), b.getNombre(), b.getDireccion(), b.getEstado()
        });
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        return l;
    }

    private void estilizarCampo(JTextField t) {
        t.setBackground(new Color(18, 36, 64));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(new Color(45, 80, 130)));
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
            g.setColor(BG_PANEL); // quita blanco
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    });

    cb.setBackground(BG_PANEL);
    cb.setForeground(Color.WHITE);
    cb.setBorder(BorderFactory.createLineBorder(new Color(45, 80, 130), 1));
    cb.setFocusable(false);

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
                l.setBackground(new Color(45, 80, 130));
            } else {
                l.setBackground(BG_PANEL);
            }

            l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return l;
        }
    });
}


    private void estilizarBoton(JButton b, Color c, String icon) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            URL u = getClass().getClassLoader().getResource(icon);
            if (u != null)
                b.setIcon(new ImageIcon(u));
        } catch (Exception ignored) {
        }
    }
    private void exportarExcelBases() {

    List<Base> lista = baseService.listarTodas();

    if (lista == null || lista.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el archivo .xlsx de bases operativas: no existen bases operativas registradas.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser ch = new JFileChooser();
    ch.setSelectedFile(new java.io.File("bases_operativas.xlsx"));
    if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

    try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
         java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Bases");

        String[] cols = {"Código", "Nombre", "Dirección", "Estado"};

        org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
        for (int c = 0; c < cols.length; c++) {
            h.createCell(c).setCellValue(cols[c]);
        }

        int rowIndex = 1;
        for (Base b : lista) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(String.format("%02d", b.getCodigoBase()));
            row.createCell(1).setCellValue(b.getNombre());
            row.createCell(2).setCellValue(b.getDireccion());
            row.createCell(3).setCellValue(b.getEstado());
        }

        for (int c = 0; c < cols.length; c++) sheet.autoSizeColumn(c);

        wb.write(fos);

        JOptionPane.showMessageDialog(this,
                "Archivo .xlsx de bases operativas exportado correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el archivo .xlsx de bases operativas: no se pudo generar el archivo de exportación.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }
}


}
