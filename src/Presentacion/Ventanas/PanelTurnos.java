package Presentacion.Ventanas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class PanelTurnos extends JPanel {

    public PanelTurnos() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Turnos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subt = new JLabel("Prototipo alineado a RTU1–RTU32 (sin lógica real, solo UI).");
        subt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subt.setForeground(Color.DARK_GRAY);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titulo);
        header.add(Box.createVerticalStrut(6));
        header.add(subt);

        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabs.addTab("Mes de Planificación", tabMesPlanificacion());
        tabs.addTab("Planificación Operativa", tabPlanificacionOperativa());
        tabs.addTab("Consultas y Exportación", tabConsultasExportacion());

        add(tabs, BorderLayout.CENTER);
    }

    // ============================================================
    // TAB 1: MES (rtu1–rtu11, rtu31–rtu32, rtu28–rtu30)
    // ============================================================
    private JPanel tabMesPlanificacion() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        // Fila 1: Crear mes + Estado del mes
        JPanel fila1 = new JPanel(new GridLayout(1, 2, 12, 0));
        fila1.setOpaque(false);

        // ===================== CREAR MES =====================
        JPanel crear = new JPanel(new GridBagLayout());
        crear.setBorder(BorderFactory.createTitledBorder("Crear mes (rtu1–rtu3, rtu31–rtu32, rtu4)"));
        crear.setPreferredSize(new Dimension(0, 185));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        JComboBox<String> cbMes = new JComboBox<>(new String[]{
                "01 - Enero","02 - Febrero","03 - Marzo","04 - Abril","05 - Mayo","06 - Junio",
                "07 - Julio","08 - Agosto","09 - Septiembre","10 - Octubre","11 - Noviembre","12 - Diciembre"
        });
        cbMes.setPreferredSize(new Dimension(190, 34));

        JTextField txtAnio = new JTextField("2026", 6);
        txtAnio.setPreferredSize(new Dimension(110, 34));

        JButton btnCrearBorrador = botonGrande("Crear (BORRADOR)");
        btnCrearBorrador.setPreferredSize(new Dimension(220, 36));

        JCheckBox chkOrdenMesAnterior = new JCheckBox("Usar orden del mes anterior (rtu4)");
        JCheckBox chkSoloBusesDisponibles = new JCheckBox("Generar solo con buses disponibles (rtu3)");
        chkSoloBusesDisponibles.setSelected(true);

        JLabel reglasGen = new JLabel("Reglas: requiere rutas ACTIVAS (rtu31) e intervalos vigentes (rtu32).");
        reglasGen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reglasGen.setForeground(Color.DARK_GRAY);

        c.gridx = 0; c.weightx = 0;
        crear.add(new JLabel("Mes:"), c);
        c.gridx = 1;
        crear.add(cbMes, c);

        c.gridx = 2;
        crear.add(new JLabel("Año:"), c);
        c.gridx = 3;
        crear.add(txtAnio, c);

        c.gridx = 4; c.weightx = 1.0;
        crear.add(new JLabel(), c);

        c.gridx = 5; c.weightx = 0;
        crear.add(btnCrearBorrador, c);

        c.gridy = 1; c.gridx = 0; c.gridwidth = 6;
        crear.add(chkOrdenMesAnterior, c);

        c.gridy = 2;
        crear.add(chkSoloBusesDisponibles, c);

        c.gridy = 3;
        crear.add(reglasGen, c);

        btnCrearBorrador.addActionListener(e -> msg(
                "Prototipo: Crear mes (rtu1), estado BORRADOR (rtu2), reglas rtu3/rtu4/rtu31/rtu32."
        ));

        // ===================== ESTADO DEL MES (alineado) =====================
        JPanel estado = new JPanel(new GridBagLayout());
        estado.setBorder(BorderFactory.createTitledBorder("Estado del mes (rtu2, rtu5–rtu11, rtu6, rtu8)"));
        estado.setPreferredSize(new Dimension(0, 185));

        GridBagConstraints es = new GridBagConstraints();
        es.insets = new Insets(8, 10, 8, 10);
        es.fill = GridBagConstraints.BOTH;
        es.weighty = 1;

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblEstadoValor = new JLabel("BORRADOR");
        lblEstadoValor.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblEstadoRtu = new JLabel("(rtu2)");
        lblEstadoRtu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstadoRtu.setForeground(Color.DARK_GRAY);

        JLabel notaPub = new JLabel("<html>Publicar requiere turnos completos (rtu6).<br>Al publicar genera versión (rtu9).</html>");
        notaPub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notaPub.setForeground(Color.DARK_GRAY);

        JLabel notaCierre = new JLabel("Mes cerrado bloquea modificaciones (rtu8).");
        notaCierre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notaCierre.setForeground(Color.DARK_GRAY);

        info.add(new JLabel("Estado actual:"));
        info.add(Box.createVerticalStrut(4));
        info.add(lblEstadoValor);
        info.add(lblEstadoRtu);
        info.add(Box.createVerticalStrut(10));
        info.add(notaPub);
        info.add(Box.createVerticalStrut(6));
        info.add(notaCierre);

        JPanel accionesEstado = new JPanel(new GridLayout(4, 1, 10, 10));
        accionesEstado.setOpaque(false);

        JButton btnPublicar = botonGrande("Publicar (rtu5)");
        JButton btnCerrar = botonGrande("Cerrar mes (rtu7)");
        JButton btnConsultarVersiones = botonGrande("Consultar versiones (rtu11)");
        JButton btnNuevaVersion = botonGrande("Nueva versión (rtu10)");

        accionesEstado.add(btnPublicar);
        accionesEstado.add(btnCerrar);
        accionesEstado.add(btnConsultarVersiones);
        accionesEstado.add(btnNuevaVersion);

        es.gridx = 0; es.gridy = 0; es.weightx = 0.65;
        estado.add(info, es);

        es.gridx = 1; es.weightx = 0.35;
        estado.add(accionesEstado, es);

        btnPublicar.addActionListener(e -> msg("Prototipo rtu5–rtu6: Publicar mes (validación UI de turnos completos)."));
        btnCerrar.addActionListener(e -> msg("Prototipo rtu7–rtu8: Cerrar mes (bloquear modificaciones)."));
        btnConsultarVersiones.addActionListener(e -> msg("Prototipo rtu11: Consultar versiones del mes."));
        btnNuevaVersion.addActionListener(e -> msg("Prototipo rtu10: Generar nueva versión por cambios operativos."));

        // IMPORTANTE: aquí sí se agrega a la fila
        fila1.add(crear);
        fila1.add(estado);

        // ===================== REGLAS OPERATIVAS =====================
        JPanel reglas = new JPanel(new GridLayout(1, 3, 10, 10));
        reglas.setBorder(BorderFactory.createTitledBorder("Reglas operativas del mes (rtu28–rtu30)"));
        reglas.setPreferredSize(new Dimension(0, 95));

        JCheckBox chkRtu28 = new JCheckBox("Cada bus 1 vez en PRIMER turno (rtu28)");
        JCheckBox chkRtu29 = new JCheckBox("Cada bus 1 vez en ÚLTIMO turno (rtu29)");

        JPanel lim = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JCheckBox chkRtu30 = new JCheckBox("Límite días por ruta (rtu30)");
        JTextField txtLimiteDias = new JTextField("5", 4);
        txtLimiteDias.setPreferredSize(new Dimension(60, 30));
        lim.add(chkRtu30);
        lim.add(new JLabel("Límite:"));
        lim.add(txtLimiteDias);
        lim.add(new JLabel("días"));

        reglas.add(chkRtu28);
        reglas.add(chkRtu29);
        reglas.add(lim);

        top.add(fila1);
        top.add(Box.createVerticalStrut(12));
        top.add(reglas);

        // ===================== TABLA VERSIONES =====================
        JPanel versiones = new JPanel(new BorderLayout());
        versiones.setBorder(BorderFactory.createTitledBorder("Versiones del mes (rtu9–rtu11)"));

        String[] colsV = {"Versión", "Fecha/Hora", "Mes/Año", "Motivo"};
        DefaultTableModel modelV = new DefaultTableModel(colsV, 0) {
            @Override public boolean isCellEditable(int r, int c2) { return false; }
        };
        JTable tablaV = new JTable(modelV);
        tablaV.setRowHeight(24);

        modelV.addRow(new Object[]{"v1", "2026-01-01 08:10", "01/2026", "Publicación inicial (rtu9)"});
        modelV.addRow(new Object[]{"v2", "2026-01-10 17:45", "01/2026", "Cambio operativo (rtu10)"});

        JScrollPane spV = new JScrollPane(tablaV);
        spV.setPreferredSize(new Dimension(0, 230));
        versiones.add(spV, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);
        root.add(versiones, BorderLayout.CENTER);

        return root;
    }

    // ============================================================
    // TAB 2: PLANIFICACIÓN OPERATIVA (rtu12–rtu19)
    // ============================================================
    private JPanel tabPlanificacionOperativa() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel ident = new JPanel(new GridBagLayout());
        ident.setBorder(BorderFactory.createTitledBorder("Identificación del turno: Día + Turno# (rtu12–rtu14)"));

        ident.setPreferredSize(new Dimension(0, 150));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        JTextField txtDia = new JTextField("2026-01-05", 10);
        txtDia.setPreferredSize(new Dimension(160, 34));

        JTextField txtNroTurno = new JTextField("1", 4);
        txtNroTurno.setPreferredSize(new Dimension(70, 34));

        JComboBox<String> cbRuta = new JComboBox<>(new String[]{"Ruta 101", "Ruta 102", "Ruta 103"});
        cbRuta.setPreferredSize(new Dimension(160, 34));

        JComboBox<String> cbBase = new JComboBox<>(new String[]{"Base Norte", "Base Centro", "Base Sur"});
        cbBase.setPreferredSize(new Dimension(170, 34));

        JTextField txtHora = new JTextField("06:00", 6);
        txtHora.setPreferredSize(new Dimension(120, 34));

        JComboBox<String> cbBus = new JComboBox<>(new String[]{
                "B-001 (ACTIVO)", "B-004 (ACTIVO)", "B-010 (MANTENIMIENTO)", "B-020 (DESACTIVADO)"
        });
        cbBus.setPreferredSize(new Dimension(220, 34));

        c.gridx = 0;
        ident.add(new JLabel("Día:"), c);
        c.gridx = 1;
        ident.add(txtDia, c);

        c.gridx = 2;
        ident.add(new JLabel("Turno#:"), c);
        c.gridx = 3;
        ident.add(txtNroTurno, c);

        c.gridx = 4;
        ident.add(new JLabel("Ruta:"), c);
        c.gridx = 5;
        ident.add(cbRuta, c);

        c.gridx = 6;
        ident.add(new JLabel("Base:"), c);
        c.gridx = 7;
        ident.add(cbBase, c);

        c.gridy = 1; c.gridx = 0;
        ident.add(new JLabel("Hora:"), c);
        c.gridx = 1;
        ident.add(txtHora, c);

        c.gridx = 2;
        ident.add(new JLabel("Bus:"), c);
        c.gridx = 3; c.gridwidth = 2;
        ident.add(cbBus, c);
        c.gridwidth = 1;

        c.gridx = 6; c.gridwidth = 2;
        JLabel reglas = new JLabel("Reglas UI: no asignar DESACTIVADO/MANTENIMIENTO (rtu15–rtu16).");
        reglas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reglas.setForeground(Color.DARK_GRAY);
        ident.add(reglas, c);
        c.gridwidth = 1;

        JPanel acciones = new JPanel(new GridLayout(2, 6, 10, 10));
        acciones.setBorder(BorderFactory.createTitledBorder("Acciones operativas (rtu12–rtu19)"));
        acciones.setPreferredSize(new Dimension(0, 165));

        JButton btnAsignar = botonGrande("Asignar (rtu12)");
        JButton btnCambiar = botonGrande("Cambiar (rtu13)");
        JButton btnEliminar = botonGrande("Eliminar (rtu14)");
        JButton btnSuplencia = botonGrande("Suplencia (rtu17)");
        JButton btnCambioTurno = botonGrande("Cambio turno (rtu18)");
        JButton btnMotivo = botonGrande("Motivo (rtu19)");

        JTextField txtMotivo = new JTextField();
        txtMotivo.setPreferredSize(new Dimension(200, 36));

        acciones.add(btnAsignar);
        acciones.add(btnCambiar);
        acciones.add(btnEliminar);
        acciones.add(btnSuplencia);
        acciones.add(btnCambioTurno);
        acciones.add(btnMotivo);

        acciones.add(new JLabel("Motivo (texto breve):"));
        acciones.add(txtMotivo);
        acciones.add(new JLabel());
        acciones.add(new JLabel());
        acciones.add(new JLabel());
        acciones.add(new JLabel());

        JPanel tablaWrap = new JPanel(new BorderLayout());
        tablaWrap.setBorder(BorderFactory.createTitledBorder("Planificación (prototipo)"));

        String[] cols = {"Día", "Turno#", "Ruta", "Base", "Hora", "Bus", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c2) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setRowHeight(24);

        model.addRow(new Object[]{"2026-01-05", "1", "Ruta 101", "Base Norte", "06:00", "B-001", "ASIGNADO"});
        model.addRow(new Object[]{"2026-01-05", "2", "Ruta 101", "Base Norte", "06:10", "-", "PENDIENTE"});

        tablaWrap.add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnAsignar.addActionListener(e -> msg("Prototipo: rtu12 (asignar) + rtu15–rtu16 (restricciones UI)."));
        btnCambiar.addActionListener(e -> msg("Prototipo: rtu13 (cambiar bus) por día + turno#."));
        btnEliminar.addActionListener(e -> msg("Prototipo: rtu14 (eliminar asignación) por día + turno#."));
        btnSuplencia.addActionListener(e -> msg("Prototipo: rtu17 (registrar suplencia)."));
        btnCambioTurno.addActionListener(e -> msg("Prototipo: rtu18 (cambio de turno)."));
        btnMotivo.addActionListener(e -> msg("Prototipo: rtu19 (motivo como texto breve)."));

        top.add(ident);
        top.add(Box.createVerticalStrut(12));
        top.add(acciones);

        root.add(top, BorderLayout.NORTH);
        root.add(tablaWrap, BorderLayout.CENTER);

        return root;
    }

    // ============================================================
    // TAB 3: CONSULTAS Y EXPORTACIÓN (rtu20–rtu27)
    // ============================================================
    private JPanel tabConsultasExportacion() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        // ===================== CONSULTAS RTU20–RTU24 (alineado) =====================
        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setBorder(BorderFactory.createTitledBorder("Consultas RTU20–RTU24"));
        filtros.setPreferredSize(new Dimension(0, 170));

        GridBagConstraints q = new GridBagConstraints();
        q.insets = new Insets(8, 12, 8, 12);
        q.fill = GridBagConstraints.HORIZONTAL;
        q.weighty = 0;

        JTextField txtDia = new JTextField("2026-01-05");
        JTextField txtSemana = new JTextField("2026-W02");
        JTextField txtMes = new JTextField("01/2026");
        JTextField txtBus = new JTextField("B-001");
        JTextField txtRuta = new JTextField("Ruta 101");
        JTextField txtDesde = new JTextField("2026-01-01");
        JTextField txtHasta = new JTextField("2026-01-31");

        Dimension field = new Dimension(260, 34);
        txtDia.setPreferredSize(field);
        txtSemana.setPreferredSize(field);
        txtMes.setPreferredSize(field);
        txtBus.setPreferredSize(field);
        txtRuta.setPreferredSize(field);
        txtDesde.setPreferredSize(new Dimension(160, 34));
        txtHasta.setPreferredSize(new Dimension(160, 34));

        // Fila 0
        q.gridy = 0;
        q.gridx = 0; q.weightx = 1;
        filtros.add(labelField("Día (rtu20):", txtDia), q);

        q.gridx = 1;
        filtros.add(labelField("Semana (rtu21):", txtSemana), q);

        q.gridx = 2;
        filtros.add(labelField("Mes (rtu22):", txtMes), q);

        // Fila 1
        q.gridy = 1;
        q.gridx = 0;
        filtros.add(labelField("Bus (rtu23):", txtBus), q);

        q.gridx = 1;
        filtros.add(labelField("Ruta (rtu24):", txtRuta), q);

        q.gridx = 2;
        JPanel periodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 18));
        periodo.setOpaque(false);
        periodo.add(new JLabel("Período (rtu23/rtu24):"));
        periodo.add(new JLabel("Desde"));
        periodo.add(txtDesde);
        periodo.add(new JLabel("Hasta"));
        periodo.add(txtHasta);
        filtros.add(periodo, q);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        botones.setOpaque(false);
        JButton btnCDia = botonGrande("Consultar día");
        JButton btnCSem = botonGrande("Consultar semana");
        JButton btnCMes = botonGrande("Consultar mes");
        JButton btnCBus = botonGrande("Consultar por bus");
        JButton btnCRuta = botonGrande("Consultar por ruta");
        botones.add(btnCDia);
        botones.add(btnCSem);
        botones.add(btnCMes);
        botones.add(btnCBus);
        botones.add(btnCRuta);

        JPanel exporta = new JPanel(new GridLayout(1, 3, 10, 10));
        exporta.setBorder(BorderFactory.createTitledBorder("Exportación RTU25–RTU27"));
        exporta.setPreferredSize(new Dimension(0, 80));
        JButton btnEDia = botonGrande("Exportar diario (rtu25)");
        JButton btnESem = botonGrande("Exportar semanal (rtu26)");
        JButton btnEMes = botonGrande("Exportar mensual (rtu27)");
        exporta.add(btnEDia);
        exporta.add(btnESem);
        exporta.add(btnEMes);

        // Tabla
        JPanel tablaWrap = new JPanel(new BorderLayout());
        tablaWrap.setBorder(BorderFactory.createTitledBorder("Resultados de consulta (prototipo)"));

        String[] cols = {"Fecha", "Tipo", "Ruta", "Hora", "Bus", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c2) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setRowHeight(24);

        model.addRow(new Object[]{"2026-01-05", "Día", "Ruta 101", "06:00", "B-001", "ASIGNADO"});
        model.addRow(new Object[]{"2026-01-05", "Día", "Ruta 101", "06:10", "-", "PENDIENTE"});

        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(0, 260));
        tablaWrap.add(sp, BorderLayout.CENTER);

        // Mensajes prototipo (opcional)
        btnCDia.addActionListener(e -> msg("rtu20: consultar por día = " + txtDia.getText()));
        btnCSem.addActionListener(e -> msg("rtu21: consultar por semana = " + txtSemana.getText()));
        btnCMes.addActionListener(e -> msg("rtu22: consultar por mes = " + txtMes.getText()));
        btnCBus.addActionListener(e -> msg("rtu23: consultar por bus = " + txtBus.getText()));
        btnCRuta.addActionListener(e -> msg("rtu24: consultar por ruta = " + txtRuta.getText()));
        btnEDia.addActionListener(e -> msg("rtu25: exportar diario (prototipo)."));
        btnESem.addActionListener(e -> msg("rtu26: exportar semanal (prototipo)."));
        btnEMes.addActionListener(e -> msg("rtu27: exportar mensual (prototipo)."));

        top.add(filtros);
        top.add(Box.createVerticalStrut(8));
        top.add(botones);
        top.add(Box.createVerticalStrut(10));
        top.add(exporta);

        root.add(top, BorderLayout.NORTH);
        root.add(tablaWrap, BorderLayout.CENTER);

        return root;
    }

    // ===================== HELPERS =====================
    private JButton botonGrande(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(190, 36));
        b.setFocusPainted(false);
        return b;
    }

    private void msg(String text) {
        JOptionPane.showMessageDialog(this, text, "Prototipo", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel labelField(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }
}
