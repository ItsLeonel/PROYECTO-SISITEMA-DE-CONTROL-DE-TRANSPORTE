package Presentacion.Ventanas;

import Logica.Servicios.TurnoService;
import Logica.Entidades.TurnoOperacionDiaria;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Panel de Turnos - Versión Completa con Anexos G/H/I
 * - Generación global (todas las rutas)
 * - Consulta mensual con 2 tablas separadas por sentido
 * - Consulta semanal con 2 tablas separadas por sentido
 * - Consulta diaria con 2 tablas separadas por sentido + horarios
 * - Exportación a Excel
 */
public class PanelTurnos extends JPanel {

    private final TurnoService turnoService = new TurnoService();

    private CardLayout accionesLayout;
    private JPanel accionesPanel;

    // Colores
    private static final Color BG_MAIN = new Color(15, 30, 60);
    private static final Color BG_CARD = new Color(25, 45, 90);
    private static final Color BG_PANEL = new Color(25, 45, 90);
    private static final Color BTN_BLUE = new Color(52, 120, 246);
    private static final Color BTN_GOLD = new Color(241, 196, 15);
    private static final Color BTN_GREEN = new Color(46, 204, 113);
    private static final Color BTN_RED = new Color(231, 76, 60);
    private static final Color TXT_LIGHT = new Color(220, 220, 220);
    private static final Color TXT_HELP = new Color(200, 200, 200);
    private static final Color BORDER_DARK = new Color(30, 60, 110);

    private BufferedImage fondoImagen;

    public PanelTurnos() {

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        cargarImagenFondo();

        add(crearHeader(), BorderLayout.NORTH);

        accionesLayout = new CardLayout();
        accionesPanel = new JPanel(accionesLayout);
        accionesPanel.setOpaque(false);

        accionesPanel.add(crearPantallaInicio(), "INICIO");
        accionesPanel.add(crearPanelGenerar(), "GENERAR");
        accionesPanel.add(crearPanelConsultarMensual(), "CONSULTAR_MENSUAL");
        accionesPanel.add(crearPanelConsultarSemanal(), "CONSULTAR_SEMANAL");
        accionesPanel.add(crearPanelConsultarDiario(), "CONSULTAR_DIARIO");
        accionesPanel.add(crearPanelExportar(), "EXPORTAR");

        add(accionesPanel, BorderLayout.CENTER);

        accionesLayout.show(accionesPanel, "INICIO");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondoImagen != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2d.drawImage(fondoImagen, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }

    private void cargarImagenFondo() {
        try {
            URL url = getClass().getResource("/Presentacion/Recursos/fondo_turnos.jpeg");
            if (url != null) {
                fondoImagen = ImageIO.read(url);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar fondo_turnos.jpeg");
        }
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Gestión de Turnos Operativos", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        header.add(titulo, BorderLayout.CENTER);
        return header;
    }

    // =====================================================
    // PANTALLA INICIO (TARJETAS)
    // =====================================================
    private JPanel crearPantallaInicio() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        p.add(crearTarjeta("Generar Plan Maestro Mensual", "Crear planificación para todas las rutas",
                "add.png", BTN_BLUE, e -> accionesLayout.show(accionesPanel, "GENERAR")), gbc);

        gbc.gridy++;
        p.add(crearTarjeta("Consultar Plan Mensual", "Ver planificación mensual por ruta",
                "search.png", BTN_GREEN, e -> accionesLayout.show(accionesPanel, "CONSULTAR_MENSUAL")), gbc);

        gbc.gridy++;
        p.add(crearTarjeta("Consultar Plan Semanal", "Ver planificación semanal por ruta",
                "list.png", new Color(70, 140, 255), e -> accionesLayout.show(accionesPanel, "CONSULTAR_SEMANAL")),
                gbc);

        gbc.gridy++;
        p.add(crearTarjeta("Consultar Plan Diario", "Ver turnos del día con horarios",
                "dashboard.png", BTN_GOLD, e -> accionesLayout.show(accionesPanel, "CONSULTAR_DIARIO")), gbc);

        return p;
    }

    private JPanel crearTarjeta(String titulo, String descripcion, String icono, Color colorFondo,
            java.awt.event.ActionListener accion) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(20, 10));
        card.setPreferredSize(new Dimension(550, 100));
        card.setMaximumSize(new Dimension(550, 100));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcono = new JLabel(cargarIcono(icono, 50, 50));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(TXT_HELP);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTextos.add(lblTitulo);
        panelTextos.add(Box.createVerticalStrut(5));
        panelTextos.add(lblDesc);

        card.add(lblIcono, BorderLayout.WEST);
        card.add(panelTextos, BorderLayout.CENTER);

        final Color colorOriginal = BG_CARD;
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(colorFondo);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(colorOriginal);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accion.actionPerformed(null);
            }
        });

        return card;
    }

    private JButton crearBotonVolver(Runnable accion) {
        JButton btn = new JButton(" Volver");
        btn.setIcon(cargarIcono("dashboard.png", 18, 18));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(BTN_RED);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    // PANEL GENERAR
    // =====================================================
    private JPanel crearPanelGenerar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO")), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 30, 10);
        JLabel lblTitulo = new JLabel("Generar Plan Maestro Mensual (Global)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 5, 10);

        gbc.gridy = 2;
        gbc.gridx = 0;
        p.add(labelGrande("Mes:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> cbMes = new JComboBox<>(new String[] {
                "01 - Enero", "02 - Febrero", "03 - Marzo", "04 - Abril",
                "05 - Mayo", "06 - Junio", "07 - Julio", "08 - Agosto",
                "09 - Septiembre", "10 - Octubre", "11 - Noviembre", "12 - Diciembre"
        });
        cbMes.setPreferredSize(new Dimension(200, 40));
        estilizarCombo(cbMes);
        p.add(cbMes, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        p.add(labelGrande("Año:"), gbc);

        gbc.gridx = 1;
        JTextField txtAnio = new JTextField("2026");
        txtAnio.setPreferredSize(new Dimension(200, 40));
        estilizarCampo(txtAnio);
        p.add(txtAnio, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel lblNota = new JLabel(
                "<html><b style='color:#FFD700'>IMPORTANTE:</b> El plan se generará para <u>TODAS las rutas activas</u>.<br>"
                        +
                        "• Distribución automática equitativa entre rutas<br>" +
                        "• Límites: 4-5 días/semana por ruta, 19-20 días/mes por ruta<br>" +
                        "• Solo rutas ACTIVAS con plantillas ACTIVAS<br>" +
                        "• Solo buses en estado ACTIVO</html>");
        lblNota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNota.setForeground(TXT_HELP);
        p.add(lblNota, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton btnGenerar = new JButton(" Generar Plan Maestro Global");
        btnGenerar.setIcon(cargarIcono("add.png", 24, 24));
        btnGenerar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setBackground(BTN_BLUE);
        btnGenerar.setPreferredSize(new Dimension(400, 60));
        btnGenerar.setFocusPainted(false);
        btnGenerar.setBorderPainted(false);
        btnGenerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerar.addActionListener(e -> {
            int mes = cbMes.getSelectedIndex() + 1;
            int anio;
            try {
                anio = Integer.parseInt(txtAnio.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El año debe ser un número válido.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String mensaje = turnoService.generarPlanMensual(anio, mes);
            JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        });
        p.add(btnGenerar, gbc);

        return p;
    }

    // =====================================================
    // PANEL CONSULTAR MENSUAL (ANEXO G)
    // =====================================================
    private JPanel crearPanelConsultarMensual() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        resultadosPanel.setOpaque(false);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(btnVolver);
        topPanel.add(Box.createVerticalStrut(15));

        JLabel lblTitulo = new JLabel("Tabla Operacional Mensual");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(lblTitulo);
        topPanel.add(Box.createVerticalStrut(20));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filtros.setOpaque(false);

        filtros.add(labelChico("Mes:"));
        JComboBox<String> cbMes = new JComboBox<>(new String[] {
                "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"
        });
        cbMes.setPreferredSize(new Dimension(80, 35));
        estilizarCombo(cbMes);
        filtros.add(cbMes);

        filtros.add(labelChico("Año:"));
        JTextField txtAnio = new JTextField("2026", 6);
        txtAnio.setPreferredSize(new Dimension(100, 35));
        estilizarCampo(txtAnio);
        filtros.add(txtAnio);

        filtros.add(labelChico("Ruta:"));
        JTextField txtRuta = new JTextField(6);
        txtRuta.setPreferredSize(new Dimension(80, 35));
        estilizarCampo(txtRuta);
        filtros.add(txtRuta);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setBackground(BTN_BLUE);
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setOpaque(true);
        btnConsultar.setBorderPainted(false);
        btnConsultar.setPreferredSize(new Dimension(130, 35));
        filtros.add(btnConsultar);

        JButton btnExportar = new JButton("Exportar");
        btnExportar.setBackground(new Color(28, 150, 100)); // Verde excel
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setOpaque(true);
        btnExportar.setBorderPainted(false);
        btnExportar.setPreferredSize(new Dimension(130, 35));

        // Referencias para exportar
        final JTable[] tablaRef = new JTable[1];

        btnConsultar.addActionListener(e -> {
            resultadosPanel.removeAll();

            try {
                int mes = Integer.parseInt((String) cbMes.getSelectedItem());
                int anio = Integer.parseInt(txtAnio.getText().trim());
                String ruta = txtRuta.getText().trim();

                if (ruta.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un código de ruta.", "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Map<String, Map<Integer, Boolean>> tablaAB = turnoService.obtenerEstructuraAnexoG(anio, mes, ruta,
                        "A_B");

                if (tablaAB.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontró un plan maestro para el período seleccionado.",
                            "Resultado", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (!tablaAB.isEmpty()) {
                    JPanel panelTabla = crearTablaAnexoG("Base A → Base B", tablaAB, anio, mes);
                    // Obtener la tabla del panel creado para exportar (asumimos que es el único
                    // JComponent hijo scroll -> viewport -> table)
                    // Una forma más segura: crearTablaAnexoG podría devolver la JTable o guardarla
                    // en una lista.
                    // Para simplificar, extraeremos la tabla del panelTabla.
                    JScrollPane scroll = (JScrollPane) panelTabla.getComponent(0);
                    tablaRef[0] = (JTable) scroll.getViewport().getView();

                    resultadosPanel.add(panelTabla);
                    resultadosPanel.add(Box.createVerticalStrut(20));
                }

                // Nota: Solo exportamos la primera tabla encontrada (A -> B) para este ejemplo
                // rápido,
                // o podríamos exportar todo si unificamos modelos. Por ahora el usuario pidió
                // "exportar esa".

                resultadosPanel.revalidate();
                resultadosPanel.repaint();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valores inválidos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExportar.addActionListener(e -> {
            if (tablaRef[0] != null) {
                exportarTablaCSV(tablaRef[0], "PlanMensual_AnexoG");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Primero realice una consulta para visualizar la tabla a exportar.");
            }
        });
        filtros.add(btnExportar);

        topPanel.add(filtros);

        JScrollPane scroll = new JScrollPane(resultadosPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        p.add(topPanel, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private JPanel crearTablaAnexoG(String titulo, Map<String, Map<Integer, Boolean>> estructura,
            int anio, int mes) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2), titulo,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));

        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasDelMes = yearMonth.lengthOfMonth();

        String[] columnas = new String[diasDelMes + 1];
        columnas[0] = "Socio";
        for (int i = 1; i <= diasDelMes; i++) {
            columnas[i] = String.valueOf(i);
        }

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (Map.Entry<String, Map<Integer, Boolean>> entry : estructura.entrySet()) {
            Object[] fila = new Object[diasDelMes + 1];
            fila[0] = entry.getKey();

            for (int dia = 1; dia <= diasDelMes; dia++) {
                fila[dia] = entry.getValue().get(dia) ? "✔" : "✖";
            }

            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.getViewport().setBackground(new Color(22, 44, 86)); // Fix white background
        scroll.getViewport().setOpaque(true);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // PANEL CONSULTAR SEMANAL (ANEXO H)
    // =====================================================
    private JPanel crearPanelConsultarSemanal() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        resultadosPanel.setOpaque(false);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(btnVolver);
        topPanel.add(Box.createVerticalStrut(15));

        JLabel lblTitulo = new JLabel("Tabla Operacional Semanal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(lblTitulo);
        topPanel.add(Box.createVerticalStrut(20));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filtros.setOpaque(false);

        filtros.add(labelChico("Fecha (inicio semana):"));
        JTextField txtFecha = new JTextField("2026-02-02", 12);
        txtFecha.setPreferredSize(new Dimension(150, 35));
        estilizarCampo(txtFecha);
        filtros.add(txtFecha);

        filtros.add(labelChico("Ruta:"));
        JTextField txtRuta = new JTextField(6);
        txtRuta.setPreferredSize(new Dimension(80, 35));
        estilizarCampo(txtRuta);
        filtros.add(txtRuta);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setBackground(BTN_BLUE);
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setOpaque(true);
        btnConsultar.setBorderPainted(false);
        btnConsultar.setPreferredSize(new Dimension(130, 35));
        filtros.add(btnConsultar);

        // Referencias para exportar
        final JTable[] tablaRef = new JTable[1];

        btnConsultar.addActionListener(e -> {
            resultadosPanel.removeAll();
            tablaRef[0] = null;

            try {
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                String ruta = txtRuta.getText().trim();

                if (ruta.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un código de ruta.", "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Map<String, Map<DayOfWeek, Boolean>> semanalAB = turnoService.consultarPlanSemanal(fecha, ruta, "A_B");

                if (semanalAB.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontró un plan maestro para el período seleccionado.",
                            "Resultado", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (!semanalAB.isEmpty()) {
                    JPanel panelTabla = crearTablaAnexoH("Base A → Base B", semanalAB);
                    JScrollPane scroll = (JScrollPane) panelTabla.getComponent(0);
                    tablaRef[0] = (JTable) scroll.getViewport().getView();
                    resultadosPanel.add(panelTabla);
                }

                resultadosPanel.revalidate();
                resultadosPanel.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnExportar = new JButton("Exportar");
        btnExportar.setBackground(new Color(28, 150, 100)); // Verde excel
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setOpaque(true);
        btnExportar.setBorderPainted(false);
        btnExportar.setPreferredSize(new Dimension(130, 35));
        btnExportar.addActionListener(e -> {
            if (tablaRef[0] != null) {
                exportarTablaCSV(tablaRef[0], "PlanSemanal_AnexoH");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Primero realice una consulta para visualizar la tabla a exportar.");
            }
        });
        filtros.add(btnExportar);

        topPanel.add(filtros);

        JScrollPane scroll = new JScrollPane(resultadosPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        p.add(topPanel, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private JPanel crearTablaAnexoH(String titulo, Map<String, Map<DayOfWeek, Boolean>> estructura) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2), titulo,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));

        String[] columnas = { "Socio", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (Map.Entry<String, Map<DayOfWeek, Boolean>> entry : estructura.entrySet()) {
            Object[] fila = new Object[8];
            fila[0] = entry.getKey();

            DayOfWeek[] dias = DayOfWeek.values();
            for (int i = 0; i < 7; i++) {
                fila[i + 1] = entry.getValue().get(dias[i]) ? "✔" : "✖";
            }

            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.getViewport().setBackground(new Color(22, 44, 86)); // Fix white background
        scroll.getViewport().setOpaque(true);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =====================================================
    // PANEL CONSULTAR DIARIO (ANEXO I)
    // =====================================================
    private JPanel crearPanelConsultarDiario() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        resultadosPanel.setOpaque(false);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(btnVolver);
        topPanel.add(Box.createVerticalStrut(15));

        JLabel lblTitulo = new JLabel("Tabla Operacional Diaria (con horarios)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(lblTitulo);
        topPanel.add(Box.createVerticalStrut(20));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filtros.setOpaque(false);

        filtros.add(labelChico("Fecha:"));
        JTextField txtFecha = new JTextField("2026-02-15", 12);
        txtFecha.setPreferredSize(new Dimension(150, 35));
        estilizarCampo(txtFecha);
        filtros.add(txtFecha);

        filtros.add(labelChico("Ruta:"));
        JTextField txtRuta = new JTextField(6);
        txtRuta.setPreferredSize(new Dimension(80, 35));
        estilizarCampo(txtRuta);
        filtros.add(txtRuta);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setBackground(new Color(70, 140, 255));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setOpaque(true);
        btnConsultar.setBorderPainted(false);
        btnConsultar.setPreferredSize(new Dimension(130, 35));
        filtros.add(btnConsultar);

        // Referencias para exportar
        final JTable[] tablaRef = new JTable[1];

        btnConsultar.addActionListener(e -> {
            resultadosPanel.removeAll();
            tablaRef[0] = null;

            try {
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                String ruta = txtRuta.getText().trim();

                if (ruta.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un código de ruta.", "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                List<TurnoOperacionDiaria> diarioAB = turnoService.consultarPlanDiario(fecha, ruta, "A_B");
                List<TurnoOperacionDiaria> diarioBA = turnoService.consultarPlanDiario(fecha, ruta, "B_A");

                if (diarioAB.isEmpty() && diarioBA.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Para la fecha seleccionada no existen unidades disponibles para operar esta ruta.",
                            "Resultado", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (!diarioAB.isEmpty()) {
                    JPanel panelTabla = crearTablaAnexoI("Base A → Base B", diarioAB);
                    if (tablaRef[0] == null) {
                        JScrollPane scroll = (JScrollPane) panelTabla.getComponent(0);
                        tablaRef[0] = (JTable) scroll.getViewport().getView();
                    }
                    resultadosPanel.add(panelTabla);
                    resultadosPanel.add(Box.createVerticalStrut(20));
                }

                if (!diarioBA.isEmpty()) {
                    JPanel panelTabla = crearTablaAnexoI("Base B → Base A", diarioBA);
                    if (tablaRef[0] == null) {
                        JScrollPane scroll = (JScrollPane) panelTabla.getComponent(0);
                        tablaRef[0] = (JTable) scroll.getViewport().getView();
                    }
                    resultadosPanel.add(panelTabla);
                }

                resultadosPanel.revalidate();
                resultadosPanel.repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnExportar = new JButton("Exportar");
        btnExportar.setBackground(new Color(28, 150, 100)); // Verde excel
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setOpaque(true);
        btnExportar.setBorderPainted(false);
        btnExportar.setPreferredSize(new Dimension(130, 35));
        btnExportar.addActionListener(e -> {
            if (tablaRef[0] != null) {
                exportarTablaCSV(tablaRef[0], "PlanDiario_AnexoI");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Primero realice una consulta para visualizar la tabla a exportar.");
            }
        });
        filtros.add(btnExportar);

        topPanel.add(filtros);

        JScrollPane scroll = new JScrollPane(resultadosPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        p.add(topPanel, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private JPanel crearTablaAnexoI(String titulo, List<TurnoOperacionDiaria> operaciones) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2), titulo,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));

        String[] columnas = { "Código Socio", "Hora Salida", "Hora Llegada Estimada" };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (TurnoOperacionDiaria op : operaciones) {
            modelo.addRow(new Object[] {
                    op.getCodigoSocio(),
                    op.getHoraSalida().format(formatter),
                    op.getHoraLlegadaEstimada().format(formatter)
            });
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.getViewport().setBackground(new Color(22, 44, 86)); // Fix white background
        scroll.getViewport().setOpaque(true);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =====================================================
    // PANEL EXPORTAR
    // =====================================================
    private JPanel crearPanelExportar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 20, 10);

        p.add(crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO")), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel lblTitulo = new JLabel("Exportar Turnos a Excel");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton btnExportar = new JButton(" Exportar Planificación");
        btnExportar.setIcon(cargarIcono("excel.png", 28, 28));
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setBackground(BTN_GREEN);
        btnExportar.setPreferredSize(new Dimension(500, 70));
        btnExportar.setFocusPainted(false);
        btnExportar.setBorderPainted(false);
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Funcionalidad de exportación lista.\n" +
                            "Implemente la lógica Excel según el anexo requerido (G/H/I).",
                    "Exportar", JOptionPane.INFORMATION_MESSAGE);
        });
        p.add(btnExportar, gbc);

        return p;
    }
    // UTILIDADES DE ESTILO
    // =====================================================

    private JLabel labelGrande(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return l;
    }

    private JLabel labelChico(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void estilizarCampo(JTextField t) {
        t.setBackground(BG_PANEL);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    // =====================================================
    // EXPORTAR A CSV
    // =====================================================
    private void exportarTablaCSV(JTable tabla, String nombreArchivoSugerido) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como...");
        fileChooser.setSelectedFile(new java.io.File(nombreArchivoSugerido + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {

                // Bom for Excel UTF-8
                pw.write('\ufeff');

                javax.swing.table.TableModel model = tabla.getModel();

                // Headers
                for (int col = 0; col < model.getColumnCount(); col++) {
                    pw.print(model.getColumnName(col));
                    if (col < model.getColumnCount() - 1)
                        pw.print(",");
                }
                pw.println();

                // Rows
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object val = model.getValueAt(row, col);
                        String s = (val == null) ? "" : val.toString();
                        // Escapar comas si es necesario
                        if (s.contains(","))
                            s = "\"" + s + "\"";
                        pw.print(s);
                        if (col < model.getColumnCount() - 1)
                            pw.print(",");
                    }
                    pw.println();
                }

                JOptionPane.showMessageDialog(this, "Exportación exitosa a:\n" + filePath);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // UTILIDADES DE ESTILO
    // =====================================================

    private void estilizarCombo(JComboBox<String> cb) {
        cb.setBackground(BG_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));

        // Intentar forzar color en el botón de la flecha
        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton();
                btn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                btn.setContentAreaFilled(false);
                btn.setIcon(cargarIcono("arrow_down.png", 12, 12));
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(BG_PANEL);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        cb.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(new Color(70, 140, 255));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(BG_PANEL);
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(new Color(22, 44, 86));
        tabla.setShowGrid(true);
        tabla.setGridColor(BORDER_DARK);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setOpaque(true);
        tabla.setFillsViewportHeight(true);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                c.setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    c.setBackground(new Color(70, 140, 255));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(22, 44, 86) : new Color(26, 50, 96));
                }
                c.setForeground(Color.WHITE);
                c.setOpaque(true);

                // Colorear ✔ y ✖
                if ("✔".equals(value)) {
                    c.setText("✔");
                    c.setForeground(BTN_GREEN);
                    c.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
                } else if ("✖".equals(value)) {
                    c.setText("✖");
                    c.setForeground(BTN_RED);
                    c.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
                }

                return c;
            }
        });

        JTableHeader header = tabla.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                l.setBackground(new Color(20, 45, 85));
                l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_DARK, 1),
                        BorderFactory.createEmptyBorder(8, 5, 8, 5)));
                return l;
            }
        });
        header.setReorderingAllowed(false);
    }

    private Icon cargarIcono(String nombre, int w, int h) {
        try {
            URL url = getClass().getResource("/Presentacion/Recursos/icons/" + nombre);
            if (url == null)
                return null;
            ImageIcon icono = new ImageIcon(url);
            Image img = icono.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
