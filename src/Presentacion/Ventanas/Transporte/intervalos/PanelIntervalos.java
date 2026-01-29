package Presentacion.Ventanas.Transporte.intervalos;
import Logica.DAO.RutaDAO;
import Logica.Entidades.Ruta;
import Logica.Entidades.Intervalo;
import Logica.Servicios.IntervaloService;
import Logica.Servicios.ResultadoIntervalo;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.SwingConstants;

import javax.swing.ScrollPaneConstants;


import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URL;
import java.util.List;

public class PanelIntervalos extends JPanel {

    // ================== SERVICE ==================
    private final IntervaloService service = new IntervaloService();

    // ===== COLORES (IGUAL ESTILO) =====
    private static final Color BG_MAIN  = new Color(15, 30, 60);
    private static final Color BG_PANEL = new Color(25, 45, 90);
    private static final Color BTN_BLUE = new Color(52, 120, 246);
    private static final Color BTN_GOLD = new Color(241, 196, 15);
    private static final Color TXT_LIGHT = new Color(220, 220, 220);
    private static final Color BORDER = new Color(45, 80, 130);
    private final RutaDAO rutaDAO = new RutaDAO();

    // ================== CAMPOS ==================
    private JTextField txtCodigo;
    private JTextField txtTiempo;
    private JTextField txtFranja;
    private JTextField txtRuta;
    private JComboBox<String> cbEstado;

    // Consulta
    private JTextField txtConsulta;
    private JRadioButton rbRuta;
    private JRadioButton rbFranja;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    // Botones
    private JButton btnRegistrar;
    private JButton btnActTiempo;
    private JButton btnActFranja;
    private JButton btnEstado;
    private JButton btnExportar;




 public PanelIntervalos() {

    setLayout(new BorderLayout(15, 15));
    setBackground(BG_MAIN);
    setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    inicializarComponentes();
    conectarEventos();

    // ================== COLUMNA IZQUIERDA ==================
    JPanel columnaIzquierda = new JPanel(new BorderLayout(0, 12));
    columnaIzquierda.setOpaque(false);
    columnaIzquierda.add(panelConsulta(), BorderLayout.NORTH);
    columnaIzquierda.add(crearTabla(), BorderLayout.CENTER);

    // ================== CONTENEDOR PRINCIPAL ==================
    JPanel contenedor = new JPanel(new BorderLayout(15, 0));
    contenedor.setOpaque(false);
    contenedor.add(columnaIzquierda, BorderLayout.CENTER);
    JScrollPane scrollDerecho = new JScrollPane(panelDerechoVertical());
scrollDerecho.setBorder(BorderFactory.createEmptyBorder());
scrollDerecho.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
scrollDerecho.getVerticalScrollBar().setUnitIncrement(16); // rueda más fluida
scrollDerecho.getViewport().setBackground(BG_PANEL);

contenedor.add(scrollDerecho, BorderLayout.EAST);


    add(contenedor, BorderLayout.CENTER);

    listarActivosSinMensaje();

}


  private void inicializarComponentes() {

    txtCodigo = new JTextField();
    txtTiempo = new JTextField();
    txtFranja = new JTextField();
    txtRuta   = new JTextField();

    cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});

    txtConsulta = new JTextField();

    rbRuta = new JRadioButton("Por ruta (código)", true);
    rbFranja = new JRadioButton("Por franja (HH:MM)");

    btnRegistrar = new JButton("Registrar");
    btnActTiempo = new JButton("Actualizar tiempo");
    btnActFranja = new JButton("Actualizar franja");
    btnEstado    = new JButton("Cambiar estado");
    btnExportar = new JButton("Exportar Excel");





    // 🔥 ESTILO CAMPOS (CLAVE)
    estilizarCampo(txtCodigo);
    estilizarCampo(txtTiempo);
    estilizarCampo(txtFranja);
    estilizarCampo(txtRuta);
    estilizarCombo(cbEstado);
}


    // ===================== UI: CONSULTA =====================
 private JPanel panelConsulta() {

    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBackground(BG_PANEL);
    p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(12, 14, 14, 14)
    ));

    // ===== TÍTULO =====
    JLabel titulo = new JLabel("Consulta");
    titulo.setForeground(TXT_LIGHT);
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    p.add(titulo);
    p.add(Box.createVerticalStrut(8));

    // ✅ TEXTO PRIMORDIAL (NO SE QUITA)
    JLabel ayuda = new JLabel("Buscar por ruta (código) o por franja exacta (HH:MM).");
    ayuda.setForeground(TXT_LIGHT);
    ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    ayuda.setAlignmentX(Component.LEFT_ALIGNMENT);

    p.add(ayuda);
    p.add(Box.createVerticalStrut(10));

    // ===== RADIOS (RUTA / FRANJA) =====
    ButtonGroup bg = new ButtonGroup();
    bg.add(rbRuta);
    bg.add(rbFranja);

    estilizarRadio(rbRuta);
    estilizarRadio(rbFranja);

    JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    radios.setOpaque(false);
    radios.add(rbRuta);
    radios.add(rbFranja);

    p.add(radios);
    p.add(Box.createVerticalStrut(10));

    // ===== CAMPO BUSCAR (TIPO LÍNEA) =====
    txtConsulta.setOpaque(false);
    txtConsulta.setForeground(Color.WHITE);
    txtConsulta.setCaretColor(Color.WHITE);
    txtConsulta.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));
    txtConsulta.setMaximumSize(new Dimension(320, 24));
    txtConsulta.setPreferredSize(new Dimension(320, 24));
    txtConsulta.setAlignmentX(Component.CENTER_ALIGNMENT);

    p.add(txtConsulta);
    p.add(Box.createVerticalStrut(14));

    // ===== BOTONES (UNO BAJO OTRO) =====
    JButton btnBuscar = new JButton("Buscar");
    JButton btnActivos = new JButton("Listar activos");

    estilizarBoton(btnBuscar, BTN_BLUE);
    estilizarBoton(btnActivos, BTN_BLUE);

    btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnActivos.setAlignmentX(Component.CENTER_ALIGNMENT);

    btnBuscar.setMaximumSize(new Dimension(180, 34));
    btnActivos.setMaximumSize(new Dimension(180, 34));

    p.add(btnBuscar);
    p.add(Box.createVerticalStrut(10));
    p.add(btnActivos);

    // ===== EVENTOS (NO SE TOCAN) =====
    btnBuscar.addActionListener(e -> {
        String v = txtConsulta.getText().trim();
        if (rbRuta.isSelected()) {
            listarPorRuta(v);
        } else {
            listarPorFranja(v);
        }
    });

    btnActivos.addActionListener(e -> listarActivos());

    return p;
}


    // ===================== UI: PANEL DERECHO VERTICAL =====================
   private JPanel panelDerechoVertical() {

    JPanel cont = new JPanel();
    cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
    cont.setPreferredSize(new Dimension(320, 0));
    cont.setBackground(BG_PANEL);
    cont.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(12, 14, 14, 14)
    ));

JLabel titulo = new JLabel("Registro / Edición de Intervalo", SwingConstants.CENTER);
titulo.setForeground(TXT_LIGHT);
titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    cont.add(titulo);
    cont.add(Box.createVerticalStrut(12));
    cont.add(panelFormulario());
    cont.add(Box.createVerticalStrut(16));
    cont.add(panelAccionesVertical());
    cont.add(Box.createVerticalGlue());

    return cont;
}


    private JPanel panelFormulario() {

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setOpaque(false);

        form.add(label("Código"));
        form.add(txtCodigo);

        form.add(label("Tiempo (min)"));
        form.add(txtTiempo);

        form.add(label("Franja (HH:MM)"));
        form.add(txtFranja);

        form.add(label("Ruta (código)"));
        form.add(txtRuta);

        form.add(label("Estado"));
        form.add(cbEstado);

        return form;
    }
private JPanel panelAccionesVertical() {

    JPanel acciones = new JPanel();
    acciones.setOpaque(false);
    acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));

    // 🔥 márgenes laterales correctos
    acciones.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

    // ===== ESTILOS + ICONOS (NO SE TOCAN) =====
    estilizarBoton(btnRegistrar, BTN_BLUE);
    estilizarBoton(btnActTiempo, BTN_GOLD);
    estilizarBoton(btnActFranja, BTN_GOLD);
    estilizarBoton(btnEstado, BTN_BLUE);
    estilizarBoton(btnExportar, BTN_BLUE);


    setIcon(btnRegistrar, "Presentacion/Recursos/icons/basse.png");
    setIcon(btnActTiempo, "Presentacion/Recursos/icons/powwer_off.png");
    setIcon(btnActFranja, "Presentacion/Recursos/icons/edittt.png");
    setIcon(btnEstado, "Presentacion/Recursos/icons/refressh.png");

    setIcon(btnExportar, "Presentacion/Recursos/icons/excel.png");


    // ===== BOTONES MÁS GRANDES =====
// ===== BOTONES MÁS PEQUEÑOS Y SIMÉTRICOS =====
// ===== BOTONES SIMÉTRICOS Y CON ANCHO COMPLETO =====
Dimension maxSize = new Dimension(Integer.MAX_VALUE, 40);

JButton[] botones = {
    btnRegistrar, btnActTiempo, btnActFranja, btnEstado, btnExportar
};

for (JButton b : botones) {
    b.setMaximumSize(maxSize);
    b.setMinimumSize(new Dimension(260, 40));   // ✅ asegura ancho mínimo visible
    b.setPreferredSize(new Dimension(260, 40)); // ✅ evita que quede angosto

    b.setAlignmentX(Component.CENTER_ALIGNMENT);
    b.setHorizontalAlignment(SwingConstants.LEFT);
    b.setIconTextGap(12);

    b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
}



    // ===== DISTRIBUCIÓN VERTICAL =====
    acciones.add(btnRegistrar);
acciones.add(Box.createVerticalStrut(8));

acciones.add(btnActTiempo);
acciones.add(Box.createVerticalStrut(8));

acciones.add(btnActFranja);
acciones.add(Box.createVerticalStrut(8));

acciones.add(btnEstado);
acciones.add(Box.createVerticalStrut(12));

acciones.add(btnExportar);

// empuja todo hacia arriba, evita que se “pierdan”
acciones.add(Box.createVerticalGlue());

return acciones;

}




    // ===================== TABLA =====================
private JScrollPane crearTabla() {

    modelo = new DefaultTableModel(
            new Object[]{"Código", "Tiempo(min)", "Franja", "Ruta", "Estado"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    tabla = new JTable(modelo);
    tabla.setRowHeight(28);
    tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    tabla.setForeground(Color.WHITE);
    tabla.setBackground(new Color(22, 44, 86));

    // ❌ quitar selección/foco
    tabla.setRowSelectionAllowed(false);
    tabla.setColumnSelectionAllowed(false);
    tabla.setCellSelectionEnabled(false);
    tabla.setFocusable(false);

    // ❌ quitar grid de Swing (porque nosotros dibujamos el borde)
    tabla.setShowGrid(false);
    tabla.setShowVerticalLines(false);
    tabla.setIntercellSpacing(new Dimension(0, 0));
    tabla.getColumnModel().setColumnMargin(0);

    Color fila1 = new Color(22, 44, 86);
    Color fila2 = new Color(26, 50, 96);

    // ✅ RENDER GENERAL: zebra + BORDE AZUL (cajillas)
    tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {

            JLabel l = (JLabel) super.getTableCellRendererComponent(
                    t, v, false, false, r, c);

            l.setOpaque(true);
            l.setForeground(Color.WHITE);
            l.setBackground(r % 2 == 0 ? fila1 : fila2);

            // ✅ borde azul + padding interno (esto crea la "cajilla" y la línea de abajo)
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));

            return l;
        }
    });

    // ✅ COLUMNA ESTADO: verde/rojo + BORDE AZUL
    tabla.getColumnModel().getColumn(4).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean s, boolean f, int r, int c) {

                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                            t, v, false, false, r, c);

                    l.setOpaque(true);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                    l.setForeground(Color.WHITE);

                    if ("ACTIVO".equalsIgnoreCase(String.valueOf(v))) {
                        l.setBackground(new Color(46, 204, 113));
                    } else {
                        l.setBackground(new Color(231, 76, 60));
                    }

                    // ✅ borde azul también aquí
                    l.setBorder(BorderFactory.createLineBorder(BORDER, 1));
                    return l;
                }
            }
    );

    // ✅ HEADER (cajillas azules también)
    JTableHeader header = tabla.getTableHeader();
    header.setBackground(new Color(20, 45, 85));
    header.setForeground(Color.WHITE);
    header.setFont(new Font("Segoe UI", Font.BOLD, 12));
    header.setOpaque(true);
    header.setReorderingAllowed(false);

    header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, false, false, row, column);

            l.setOpaque(true);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setBackground(new Color(20, 45, 85));
            l.setForeground(Color.WHITE);
            l.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            return l;
        }
    });

    // ✅ SCROLL sin blanco
    JScrollPane sp = new JScrollPane(tabla);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.getViewport().setBackground(new Color(22, 44, 86));
    sp.setBackground(new Color(22, 44, 86));

    return sp;
}


    // ===================== EVENTOS =====================
    private void conectarEventos() {

        btnRegistrar.addActionListener(e -> {
            Intervalo i = new Intervalo();

            i.setCodigoIntervalo(parseInt(txtCodigo.getText().trim()));
            i.setTiempoMinutos(parseInt(txtTiempo.getText().trim()));
            i.setFranjaHoraria(txtFranja.getText().trim());
            i.setCodigoRuta(txtRuta.getText().trim());
            i.setEstado(cbEstado.getSelectedItem().toString());

            String msg = service.registrar(i);
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            listarActivos();
        });

        btnActTiempo.addActionListener(e -> {
    int codigo = parseInt(txtCodigo.getText().trim());
    int tiempo = parseInt(txtTiempo.getText().trim());

    String codigoRuta = txtRuta.getText().trim();
    Ruta ruta = rutaDAO.buscarPorCodigo(codigoRuta);

    if (ruta == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo registrar el intervalo de salida: el código de la ruta no existe.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    String msg = service.actualizarTiempo(codigo, tiempo);
    JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
    listarActivos();
});


        btnActFranja.addActionListener(e -> {
    int codigo = parseInt(txtCodigo.getText().trim());
    String franja = txtFranja.getText().trim();

    String codigoRuta = txtRuta.getText().trim();
    Ruta ruta = rutaDAO.buscarPorCodigo(codigoRuta);

    if (ruta == null) {
        JOptionPane.showMessageDialog(this,
                "No se pudo registrar el intervalo de salida: el código de la ruta no existe.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Validar franja aquí para que el mensaje sea el de "registrar"
    if (!franja.matches("^\\d{2}:\\d{2}$")) {
        JOptionPane.showMessageDialog(this,
                "No se pudo registrar el intervalo de salida: la franja horaria no cumple el formato requerido.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    int hh = Integer.parseInt(franja.substring(0, 2));
    int mm = Integer.parseInt(franja.substring(3, 5));
    if (hh < 0 || hh > 23 || mm < 0 || mm > 59) {
        JOptionPane.showMessageDialog(this,
                "No se pudo registrar el intervalo de salida: la franja horaria no cumple el formato requerido.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    String msg = service.actualizarFranja(codigo, franja);
    JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
    listarActivos();
});


        btnEstado.addActionListener(e -> {
            int codigo = parseInt(txtCodigo.getText().trim());
            String estado = cbEstado.getSelectedItem().toString();

            String msg = service.cambiarEstado(codigo, estado);
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            listarActivos();
        });

        btnExportar.addActionListener(e -> exportarExcelIntervalos());

    }

    // ===================== ACCIONES LISTADO =====================
private void listarActivos() {

    ResultadoIntervalo r = service.listarActivos();

    // ✅ Mostrar siempre el mensaje del requisito
    JOptionPane.showMessageDialog(this,
            r.getMensaje(),
            "Resultado",
            JOptionPane.INFORMATION_MESSAGE);

    // ✅ Si hay datos, cargar tabla, si no, limpiar
    if (r.getDatos() != null && !r.getDatos().isEmpty()) {
        cargarTabla((List<Intervalo>) r.getDatos());
    } else {
        limpiarTabla();
    }
}
private void listarActivosSinMensaje() {

    ResultadoIntervalo r = service.listarActivos();

    if (r.getDatos() != null && !r.getDatos().isEmpty()) {
        cargarTabla((List<Intervalo>) r.getDatos());
    } else {
        limpiarTabla();
    }
}

private void listarPorRuta(String codigoRuta) {

    if (codigoRuta == null || codigoRuta.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese el código de la ruta.",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    ResultadoIntervalo r = service.consultarPorRuta(codigoRuta.trim());

    JOptionPane.showMessageDialog(this, r.getMensaje(),
            "Resultado", JOptionPane.INFORMATION_MESSAGE);

    if (!r.getDatos().isEmpty()) {
        cargarTabla((List<Intervalo>) r.getDatos());
    } else {
        limpiarTabla();
    }
}
private void listarPorFranja(String franja) {

    if (franja == null || franja.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingrese la franja horaria (HH:MM).",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    ResultadoIntervalo r = service.consultarPorFranja(franja.trim());

    JOptionPane.showMessageDialog(this, r.getMensaje(),
            "Resultado", JOptionPane.INFORMATION_MESSAGE);

    if (!r.getDatos().isEmpty()) {
        cargarTabla((List<Intervalo>) r.getDatos());
    } else {
        limpiarTabla();
    }
}





    private void cargarTabla(List<Intervalo> lista) {
        modelo.setRowCount(0);
        for (Intervalo i : lista) {
            modelo.addRow(new Object[]{
                    i.getCodigoIntervalo(),
                    i.getTiempoMinutos(),
                    i.getFranjaHoraria(),
                    i.getCodigoRuta(),
                    i.getEstado()
            });
        }
    }

    // ===================== UI HELPERS =====================
    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private void estilizarBoton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }



    private void estilizarRadio(JRadioButton r) {
        r.setOpaque(false);
        r.setForeground(TXT_LIGHT);
        r.setFocusPainted(false);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void setIcon(JButton b, String path) {
        try {
            URL url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                b.setIcon(new ImageIcon(url));
                b.setHorizontalAlignment(SwingConstants.LEFT);
                b.setIconTextGap(10);
                b.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
            } else {
                b.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
            }
        } catch (Exception ignored) { }
    }
    private void estilizarCampo(JTextField t) {
    t.setOpaque(true);
    t.setBackground(BG_PANEL);           // mismo azul del panel
    t.setForeground(Color.WHITE);
    t.setCaretColor(Color.WHITE);

    t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1), // borde azul
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
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
        public void paintCurrentValueBackground(
                Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(BG_PANEL);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    });

    cb.setBackground(BG_PANEL);
    cb.setForeground(Color.WHITE);
    cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
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
            l.setBackground(isSelected ? BORDER : BG_PANEL);
            l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return l;
        }
    });
}


    private int parseInt(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return -1; }
    }
    private void limpiarTabla() {
    modelo.setRowCount(0);
}
private void exportarExcelIntervalos() {

    List<Logica.Entidades.Intervalo> lista = service.listarTodos();

    if (lista == null || lista.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el archivo .xlsx de intervalos: no existen intervalos registrados.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser ch = new JFileChooser();
    ch.setSelectedFile(new java.io.File("intervalos.xlsx"));
    if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

    try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
         java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Intervalos");

        String[] cols = {"Código", "Tiempo(min)", "Franja", "Ruta", "Estado"};

        org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
        for (int c = 0; c < cols.length; c++) h.createCell(c).setCellValue(cols[c]);

        int rowIndex = 1;
        for (Logica.Entidades.Intervalo i : lista) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(i.getCodigoIntervalo());
            row.createCell(1).setCellValue(i.getTiempoMinutos());
            row.createCell(2).setCellValue(i.getFranjaHoraria());
            row.createCell(3).setCellValue(i.getCodigoRuta());
            row.createCell(4).setCellValue(i.getEstado());
        }

        for (int c = 0; c < cols.length; c++) sheet.autoSizeColumn(c);

        wb.write(fos);

        JOptionPane.showMessageDialog(this,
                "Archivo .xlsx de intervalos exportado correctamente.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "No se pudo exportar el archivo .xlsx de intervalos: no se pudo generar el archivo de exportación.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }
}



}