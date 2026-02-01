package Presentacion.Ventanas.Transporte.rutas;

import Logica.Servicios.RutaService;
import Logica.Entidades.Ruta;
import Logica.DAO.RutaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import java.awt.*;
import java.net.URL;
import java.util.List;
import Logica.Servicios.BaseService;
import Logica.Servicios.PlantillaHorariaService;
import Logica.Entidades.Base;
import Logica.Entidades.PlantillaHoraria;

/**
 * Panel de Rutas actualizado según nuevo modelo funcional:
 * - Base A y Base B (en lugar de origen/destino como campos principales)
 * - Plantilla Horaria (debe estar Activa)
 * - Duración estimada en minutos
 * - Origen/Destino opcionales/informativos
 */
public class PanelRutas extends JPanel {

    private final RutaService rutaService = new RutaService();
    private final BaseService baseService = new BaseService();
    private final PlantillaHorariaService plantillaService = new PlantillaHorariaService();
    private final RutaDAO rutaDAO = new RutaDAO();

    private final CardLayout parentCardLayout;
    private final JPanel parentPanel;

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

    // CAMPOS REGISTRAR
    private JTextField txtCodigo, txtNombre;
    private JComboBox<String> cbBaseA, cbBaseB;
    private JComboBox<String> cbPlantilla;
    private JTextField txtDuracion;
    // private JTextField txtOrigen, txtDestino; // Eliminados por redundancia
    private JComboBox<String> cbEstado;

    // CAMPOS ACTUALIZAR
    private JTextField txtCodigoActualizar;
    private JTextField txtNombreActualizar;
    // private JTextField txtBaseAActualizar, txtBaseBActualizar; // Eliminados
    private JComboBox<String> cbBaseAActualizar, cbBaseBActualizar;
    private JComboBox<String> cbTipoActualizacion;

    // CAMPOS CONSULTAR
    private JTextField txtBuscarCodigo;
    private JTextField txtBuscarNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbFiltroConsulta;

    public PanelRutas(CardLayout parentCardLayout, JPanel parentPanel) {
        this.parentCardLayout = parentCardLayout;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        add(crearHeader(), BorderLayout.NORTH);

        accionesLayout = new CardLayout();
        accionesPanel = new JPanel(accionesLayout);
        accionesPanel.setOpaque(false);

        accionesPanel.add(crearPantallaInicio(), "INICIO");
        accionesPanel.add(crearPanelRegistrar(), "REGISTRAR");
        accionesPanel.add(crearPanelActualizar(), "ACTUALIZAR");
        accionesPanel.add(crearPanelConsultar(), "CONSULTAR");
        accionesPanel.add(crearPanelExportar(), "EXPORTAR");

        add(accionesPanel, BorderLayout.CENTER);

        accionesLayout.show(accionesPanel, "INICIO");
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Gestión de Rutas de Transporte", SwingConstants.CENTER);
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
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // BOTÓN VOLVER
        JButton btnVolver = crearBotonVolver(() -> parentCardLayout.show(parentPanel, "INICIO"));
        btnVolver.setText(" Volver al Menú Principal");
        p.add(btnVolver, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TARJETAS
        p.add(crearTarjeta("Registrar Ruta", "Registrar nueva ruta con bases operativas", "add.png", BTN_BLUE,
                e -> accionesLayout.show(accionesPanel, "REGISTRAR")), gbc);

        gbc.gridy++;
        p.add(crearTarjeta("Actualizar Ruta", "Modificar bases o nombre", "edit.png", BTN_GOLD,
                e -> accionesLayout.show(accionesPanel, "ACTUALIZAR")), gbc);

        gbc.gridy++;
        p.add(crearTarjeta("Consultar Rutas", "Buscar y listar rutas", "search.png", BTN_GREEN,
                e -> accionesLayout.show(accionesPanel, "CONSULTAR")), gbc);

        gbc.gridy++;
        p.add(crearTarjeta("Exportar Rutas", "Descargar listado en Excel", "excel.png", new Color(28, 150, 100),
                e -> accionesLayout.show(accionesPanel, "EXPORTAR")), gbc);

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
        card.setPreferredSize(new Dimension(500, 100));
        card.setMaximumSize(new Dimension(500, 100));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ICONO
        JLabel lblIcono = new JLabel(cargarIcono(icono, 50, 50));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);

        // TEXTOS
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

        // HOVER EFFECT
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

    // =====================================================
    // BOTÓN VOLVER
    // =====================================================
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

    // PANEL REGISTRAR (ACTUALIZADO CON BASES Y PLANTILLA)
    // =====================================================
    private JScrollPane crearPanelRegistrar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Botón volver
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        p.add(btnVolver, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        // Título
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        JLabel lblTitulo = new JLabel("Registrar Nueva Ruta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 10, 2, 10);

        // Inicializar campos
        txtCodigo = new JTextField(20);
        txtNombre = new JTextField(20);

        cbBaseA = new JComboBox<>();
        cbBaseB = new JComboBox<>();
        cargarBases(cbBaseA);
        cargarBases(cbBaseB);

        cbPlantilla = new JComboBox<>();
        cargarPlantillas(cbPlantilla);

        txtDuracion = new JTextField(20);
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        estilizarCampo(txtCodigo);
        estilizarCampo(txtNombre);
        estilizarCombo(cbBaseA);
        estilizarCombo(cbBaseB);
        estilizarCombo(cbPlantilla);
        estilizarCampo(txtDuracion);
        estilizarCombo(cbEstado);

        // 1. CÓDIGO
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Código de ruta:"), gbc);
        gbc.gridx = 1;
        p.add(txtCodigo, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 10, 15, 10);
        p.add(crearLabelAyuda("2 dígitos (ej: 01)"), gbc);

        // 2. NOMBRE
        row++;
        gbc.insets = new Insets(15, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Nombre de la ruta:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombre, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 10, 15, 10);
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Ruta Norte-Sur)"), gbc);

        // 3. BASE OPERATIVA ORIGEN
        row++;
        gbc.insets = new Insets(15, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Base Operativa Origen:"), gbc);
        gbc.gridx = 1;
        p.add(cbBaseA, gbc);

        // 4. BASE OPERATIVA DESTINO
        row++;
        gbc.insets = new Insets(15, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Base Operativa Destino:"), gbc);
        gbc.gridx = 1;
        p.add(cbBaseB, gbc);

        // 5. PLANTILLA HORARIA
        row++;
        gbc.insets = new Insets(15, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Plantilla Horaria (código):"), gbc);
        gbc.gridx = 1;
        p.add(cbPlantilla, gbc);

        // 6. DURACIÓN ESTIMADA
        row++;
        gbc.insets = new Insets(15, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Duración estimada (minutos):"), gbc);
        gbc.gridx = 1;
        p.add(txtDuracion, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Tiempo estimado del recorrido (ej: 45)"), gbc);

        // 7. Y 8. DIRECCIÓN ORIGEN/DESTINO (ELIMINADOS) y reemplazados por ayuda
        // Se asume que la dirección viene de la base seleccionada

        // 7. Y 8. DIRECCIÓN ORIGEN/DESTINO (ELIMINADOS) y reemplazados por ayuda
        // Se asume que la dirección viene de la base seleccionada

        // Nota eliminada por solicitud

        // 9. ESTADO
        row++;
        gbc.insets = new Insets(15, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Estado:"), gbc);
        gbc.gridx = 1;
        p.add(cbEstado, gbc);

        // BOTÓN REGISTRAR
        row++;
        JButton btnRegistrar = new JButton(" Registrar Ruta");
        btnRegistrar.setIcon(cargarIcono("add.png", 22, 22));
        estilizarBotonGrande(btnRegistrar, BTN_BLUE);
        btnRegistrar.addActionListener(e -> registrarRuta());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 20, 10);
        p.add(btnRegistrar, gbc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    // PANEL ACTUALIZAR (CON BASES A Y B)
    // =====================================================
    private JScrollPane crearPanelActualizar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Botón volver
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        p.add(btnVolver, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        // Título
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        JLabel lblTitulo = new JLabel("Actualizar Ruta Existente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        // TIPO ACTUALIZACIÓN
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(12, 15, 2, 15);
        JLabel lblTipo = new JLabel("Tipo de actualización:");
        lblTipo.setForeground(Color.WHITE);
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(lblTipo, gbc);

        cbTipoActualizacion = new JComboBox<>(new String[] {
                "ACTUALIZAR NOMBRE",
                "ACTUALIZAR BASE ORIGEN",
                "ACTUALIZAR BASE DESTINO"
        });

        cbTipoActualizacion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbTipoActualizacion.setPreferredSize(new Dimension(350, 40));
        estilizarComboGrande(cbTipoActualizacion);
        cbTipoActualizacion.addActionListener(e -> ajustarCamposActualizar());

        gbc.gridx = 1;
        p.add(cbTipoActualizacion, gbc);

        // CÓDIGO
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(12, 15, 2, 15);
        p.add(labelGrande("Código de ruta:"), gbc);

        JPanel panelCodigo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCodigo.setOpaque(false);

        txtCodigoActualizar = new JTextField(12);
        estilizarCampoGrande(txtCodigoActualizar);

        JButton btnVerificar = new JButton(" Verificar código");
        btnVerificar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setBackground(BTN_GREEN);
        btnVerificar.setFocusPainted(false);
        btnVerificar.setBorderPainted(false);
        btnVerificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerificar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVerificar.addActionListener(e -> verificarYCargarRuta());

        panelCodigo.add(txtCodigoActualizar);
        panelCodigo.add(btnVerificar);

        gbc.gridx = 1;
        p.add(panelCodigo, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("2 dígitos (ej: 01)"), gbc);

        // Inicializar campos
        txtNombreActualizar = new JTextField(20);
        cbBaseAActualizar = new JComboBox<>();
        cbBaseBActualizar = new JComboBox<>();

        cargarBases(cbBaseAActualizar);
        cargarBases(cbBaseBActualizar);

        estilizarCampoGrande(txtNombreActualizar);
        estilizarComboGrande(cbBaseAActualizar);
        estilizarComboGrande(cbBaseBActualizar);

        // NUEVO NOMBRE
        row++;
        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Nuevo nombre:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombreActualizar, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Hasta 50 caracteres"), gbc);

        // NUEVA BASE A
        row++;
        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Nueva Base Origen (código):"), gbc);
        gbc.gridx = 1;
        p.add(cbBaseAActualizar, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Debe ser diferente de Base Destino"), gbc);

        // NUEVA BASE B
        row++;
        gbc.insets = new Insets(8, 15, 2, 15);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Nueva Base Destino (código):"), gbc);
        gbc.gridx = 1;
        p.add(cbBaseBActualizar, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("Debe ser diferente de Base Origen"), gbc);

        // BOTÓN ACTUALIZAR
        row++;
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

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        p.add(btnActualizar, gbc);

        ajustarCamposActualizar();

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    private void verificarYCargarRuta() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        if (!codigoTxt.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this, "El código debe tener 2 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorCodigo(codigoTxt);

        if (r == null) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar: el código de la ruta no existe.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            limpiarCamposActualizar();
            return;
        }

        // Cargar datos
        txtNombreActualizar.setText(r.getNombre());
        txtNombreActualizar.setText(r.getNombre());
        seleccionarBaseEnCombo(cbBaseAActualizar, r.getCodigoBaseA());
        seleccionarBaseEnCombo(cbBaseBActualizar, r.getCodigoBaseB());

        JOptionPane.showMessageDialog(this, "✅ Ruta cargada correctamente. Puede proceder a actualizar.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void ajustarCamposActualizar() {
        String tipo = (String) cbTipoActualizacion.getSelectedItem();

        txtNombreActualizar.setEditable(false);
        txtNombreActualizar.setEditable(false);
        cbBaseAActualizar.setEnabled(false);
        cbBaseBActualizar.setEnabled(false);

        txtNombreActualizar.setBackground(new Color(40, 50, 70));
        // Combos disabled look is automatic

        if ("ACTUALIZAR NOMBRE".equals(tipo)) {
            txtNombreActualizar.setEditable(true);
            txtNombreActualizar.setBackground(BG_PANEL);
        } else if ("ACTUALIZAR BASE ORIGEN".equals(tipo)) {
            cbBaseAActualizar.setEnabled(true);
        } else if ("ACTUALIZAR BASE DESTINO".equals(tipo)) {
            cbBaseBActualizar.setEnabled(true);
        }
    }

    private void ejecutarActualizacion() {
        String codigo = txtCodigoActualizar.getText().trim();

        if (codigo.isEmpty() || !codigo.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un código válido de 2 dígitos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = (String) cbTipoActualizacion.getSelectedItem();
        String mensaje = "";

        if ("ACTUALIZAR NOMBRE".equals(tipo)) {
            mensaje = rutaService.actualizarNombre(codigo, txtNombreActualizar.getText().trim());
        } else if ("ACTUALIZAR BASE ORIGEN".equals(tipo)) {
            try {
                String sel = (String) cbBaseAActualizar.getSelectedItem();
                int baseA = Integer.parseInt(sel.split(" - ")[0]);
                mensaje = rutaService.actualizarBaseA(codigo, baseA);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una Base Origen válida.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if ("ACTUALIZAR BASE DESTINO".equals(tipo)) {
            try {
                String sel = (String) cbBaseBActualizar.getSelectedItem();
                int baseB = Integer.parseInt(sel.split(" - ")[0]);
                mensaje = rutaService.actualizarBaseB(codigo, baseB);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una Base Destino válida.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (mensaje.contains("correctamente")) {
            limpiarCamposActualizar();
        }
    }

    private void limpiarCamposActualizar() {
        txtCodigoActualizar.setText("");
        txtNombreActualizar.setText("");
        txtNombreActualizar.setText("");
        if (cbBaseAActualizar.getItemCount() > 0)
            cbBaseAActualizar.setSelectedIndex(0);
        if (cbBaseBActualizar.getItemCount() > 0)
            cbBaseBActualizar.setSelectedIndex(0);
    }

    // PANEL CONSULTAR
    // =====================================================
    private JPanel crearPanelConsultar() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));

        JLabel lblTitulo = new JLabel("Consultar Rutas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        headerPanel.add(btnVolver, BorderLayout.WEST);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        p.add(headerPanel, BorderLayout.NORTH);

        // CONTENIDO
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        JPanel panelTop = new JPanel(new GridLayout(1, 2, 15, 0));
        panelTop.setOpaque(false);

        // COLUMNA IZQUIERDA: Búsqueda
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(BG_PANEL);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK),
                BorderFactory.createEmptyBorder(8, 12, 10, 12)));

        JLabel lblBusqueda = new JLabel("🔍 Búsqueda Específica");
        lblBusqueda.setForeground(TXT_LIGHT);
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblBusqueda);
        panelIzq.add(Box.createVerticalStrut(10));

        JRadioButton rbCodigo = new JRadioButton("Por código", true);
        JRadioButton rbNombre = new JRadioButton("Por nombre");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbCodigo);
        bg.add(rbNombre);
        estilizarRadio(rbCodigo);
        estilizarRadio(rbNombre);

        // Label de ayuda dinámico
        JLabel lblAyudaBusqueda = crearLabelAyuda("2 dígitos (de 01 a 99)");

        // Listeners para cambiar el texto de ayuda
        rbCodigo.addActionListener(e -> lblAyudaBusqueda.setText("2 dígitos (de 01 a 99)"));
        rbNombre.addActionListener(e -> lblAyudaBusqueda.setText("Hasta 50 caracteres (ej: Ruta Norte-Sur)"));

        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radios.setOpaque(false);
        radios.add(rbCodigo);
        radios.add(Box.createHorizontalStrut(15));
        radios.add(rbNombre);
        radios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(radios);
        panelIzq.add(Box.createVerticalStrut(8));

        txtBuscarCodigo = new JTextField();
        txtBuscarNombre = new JTextField();

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(0, 30));
        txtBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        estilizarCampo(txtBuscar);
        txtBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(txtBuscar);
        panelIzq.add(Box.createVerticalStrut(2)); // Espacio pequeño
        panelIzq.add(lblAyudaBusqueda); // Label de ayuda
        panelIzq.add(Box.createVerticalStrut(10));

        JButton btnBuscar = new JButton(" Buscar");
        btnBuscar.setIcon(cargarIcono("search.png", 20, 20));
        estilizarBotonGrande(btnBuscar, BTN_BLUE);
        btnBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBuscar.addActionListener(e -> {
            if (rbCodigo.isSelected()) {
                txtBuscarCodigo.setText(txtBuscar.getText());
                buscarPorCodigo();
            } else {
                txtBuscarNombre.setText(txtBuscar.getText());
                buscarPorNombre();
            }
        });

        JPanel panelBtnBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelBtnBuscar.setOpaque(false);
        panelBtnBuscar.add(btnBuscar);
        panelBtnBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(panelBtnBuscar);

        // COLUMNA DERECHA: Listado
        JPanel panelDer = new JPanel();
        panelDer.setLayout(new BoxLayout(panelDer, BoxLayout.Y_AXIS));
        panelDer.setBackground(BG_PANEL);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK),
                BorderFactory.createEmptyBorder(8, 12, 10, 12)));

        JLabel lblListado = new JLabel("📋 Listado General");
        lblListado.setForeground(TXT_LIGHT);
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblListado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDer.add(lblListado);
        panelDer.add(Box.createVerticalStrut(10));

        JLabel lblFiltrar = new JLabel("Filtrar listado por:");
        lblFiltrar.setForeground(Color.WHITE);
        lblFiltrar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFiltrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDer.add(lblFiltrar);
        panelDer.add(Box.createVerticalStrut(4));

        cbFiltroConsulta = new JComboBox<>(new String[] {
                "Todas las rutas",
                "Rutas activas",
                "Rutas inactivas"
        });
        estilizarCombo(cbFiltroConsulta);
        cbFiltroConsulta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cbFiltroConsulta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDer.add(cbFiltroConsulta);
        panelDer.add(Box.createVerticalStrut(10));

        JPanel panelBotonesDer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotonesDer.setOpaque(false);
        panelBotonesDer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnListar = new JButton(" Listar");
        btnListar.setIcon(cargarIcono("list.png", 20, 20));
        estilizarBotonGrande(btnListar, BTN_BLUE);
        btnListar.addActionListener(e -> ejecutarListado());

        JButton btnCambiarEstado = new JButton(" Cambiar Estado");
        btnCambiarEstado.setIcon(cargarIcono("refresh.png", 20, 20));
        estilizarBotonGrande(btnCambiarEstado, BTN_GOLD);
        btnCambiarEstado.addActionListener(e -> cambiarEstadoSeleccionado());

        panelBotonesDer.add(btnListar);
        panelBotonesDer.add(btnCambiarEstado);

        panelDer.add(panelBotonesDer);

        panelTop.add(panelIzq);
        panelTop.add(panelDer);

        contentPanel.add(panelTop, BorderLayout.NORTH);
        contentPanel.add(crearTabla(), BorderLayout.CENTER);

        p.add(contentPanel, BorderLayout.CENTER);

        return p;
    }

    private JScrollPane crearTabla() {
        // Columnas actualizadas con Base A, Base B, Plantilla
        modeloTabla = new DefaultTableModel(
                new Object[] { "Código", "Nombre", "Base Origen", "Base Destino", "Plantilla", "Duración", "Estado" },
                0) {
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

        // Renderizador columna Estado
        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                l.setBackground(new Color(20, 45, 85));
                l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_DARK, 1),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(22, 44, 86));
        tabla.setFillsViewportHeight(true);
        return scroll;
    }

    // =====================================================
    // PANEL EXPORTAR
    // =====================================================
    private JPanel crearPanelExportar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        p.add(btnVolver, gbc);

        gbc.gridy = 1;
        p.add(Box.createVerticalStrut(40), gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblTitulo = new JLabel("Exportar Datos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton btnExportar = new JButton(" Exportar listado de rutas");
        btnExportar.setIcon(cargarIcono("excel.png", 24, 24));
        estilizarBotonGrande(btnExportar, BTN_GREEN);
        btnExportar.setPreferredSize(new Dimension(500, 60));
        btnExportar.addActionListener(e -> exportarExcelRutas());

        p.add(btnExportar, gbc);

        return p;
    }

    // ACCIONES: REGISTRAR
    // =====================================================
    private void registrarRuta() {
        Ruta r = new Ruta();
        r.setCodigoRuta(txtCodigo.getText().trim());
        r.setNombre(txtNombre.getText().trim());

        // Parsear Base A y B
        int codigoA = -1;
        int codigoB = -1;

        try {
            String selA = (String) cbBaseA.getSelectedItem();
            if (selA == null || selA.isEmpty())
                throw new Exception();
            codigoA = Integer.parseInt(selA.split(" - ")[0]);
            r.setCodigoBaseA(codigoA);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione Base Origen válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String selB = (String) cbBaseB.getSelectedItem();
            if (selB == null || selB.isEmpty())
                throw new Exception();
            codigoB = Integer.parseInt(selB.split(" - ")[0]);
            r.setCodigoBaseB(codigoB);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione Base Destino válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // VALIDAR QUE BASES SEAN DISTINTAS
        if (codigoA == codigoB) {
            JOptionPane.showMessageDialog(this, "La Base Origen y Base Destino no pueden ser la misma.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // AUTO-POPULAR ORIGEN Y DESTINO CON NOMBRES DE BASES (O Dirección si disponible
        // en objeto Base)
        // Para simplificar y evitar buscar objetos de nuevo, usaremos el nombre del
        // combo o buscaremos.
        // Lo correcto es buscar la base para obtener su nombre/dirección limpios.
        Base objBaseA = baseService.buscarPorCodigo(codigoA);
        Base objBaseB = baseService.buscarPorCodigo(codigoB);

        if (objBaseA != null)
            r.setOrigen(objBaseA.getNombre()); // O usar getDireccion() si se prefiere
        else
            r.setOrigen("Origen " + codigoA);

        if (objBaseB != null)
            r.setDestino(objBaseB.getNombre());
        else
            r.setDestino("Destino " + codigoB);

        // Parsear Plantilla (Combo)
        try {
            String selP = (String) cbPlantilla.getSelectedItem();
            if (selP == null || selP.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una Plantilla Horaria.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Detectar "0 - Sin Asignar"
            if (selP.startsWith("0 -")) {
                r.setCodigoIntervalo(-1);
            } else {
                int codigoP = Integer.parseInt(selP.split(" - ")[0]);
                r.setCodigoIntervalo(codigoP);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Plantilla Horaria inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parsear Duración
        try {
            r.setDuracionEstimadaMinutos(Integer.parseInt(txtDuracion.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La duración estimada debe ser un número válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        r.setEstado(cbEstado.getSelectedItem().toString());

        String mensaje = rutaService.registrarRuta(r);
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (mensaje.contains("correctamente")) {
            limpiarCamposRegistrar();
        }
    }

    private void limpiarCamposRegistrar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        if (cbBaseA.getItemCount() > 0)
            cbBaseA.setSelectedIndex(0);
        if (cbBaseB.getItemCount() > 0)
            cbBaseB.setSelectedIndex(0);
        if (cbPlantilla.getItemCount() > 0)
            cbPlantilla.setSelectedIndex(0);
        txtDuracion.setText("");
        cbEstado.setSelectedIndex(0);

        // Recargar combos
        cargarBases(cbBaseA);
        cargarBases(cbBaseB);
        cargarPlantillas(cbPlantilla);
    }

    private void cargarBases(JComboBox<String> cb) {
        cb.removeAllItems();
        List<Base> bases = baseService.listarActivas();
        if (bases != null) {
            for (Base b : bases) {
                // Formato: "01 - NombreBase"
                cb.addItem(String.format("%02d - %s", b.getCodigoBase(), b.getNombre()));
            }
        }
    }

    private void cargarPlantillas(JComboBox<String> cb) {
        cb.removeAllItems();
        // Opción para romper dependencia circular
        cb.addItem("0 - Sin Asignar");

        List<PlantillaHoraria> lista = plantillaService.consultarActivas().getPlantillas();
        if (lista != null) {
            for (PlantillaHoraria ph : lista) {
                cb.addItem(ph.getCodigoPlantilla() + " - " + ph.getNombre());
            }
        }
    }

    // =====================================================
    // ACCIONES: CONSULTAR
    // =====================================================
    private void buscarPorCodigo() {
        String codigo = txtBuscarCodigo.getText().trim();
        modeloTabla.setRowCount(0);

        if (!codigo.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la ruta: el código de la ruta no cumple el formato requerido (2 dígitos).",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorCodigo(codigo);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar: la ruta indicada no existe.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        agregarFila(r);
        JOptionPane.showMessageDialog(this, "Consulta de ruta realizada correctamente.", "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarPorNombre() {
        String nombre = txtBuscarNombre.getText().trim();
        modeloTabla.setRowCount(0);

        if (!nombre.matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,50}$")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo consultar la ruta: el nombre de la ruta no cumple el formato requerido.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ruta r = rutaService.buscarRutaPorNombre(nombre);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar la ruta: el nombre de la ruta no existe.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        agregarFila(r);
        JOptionPane.showMessageDialog(this, "Consulta de ruta realizada correctamente.", "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarListado() {
        String seleccion = (String) cbFiltroConsulta.getSelectedItem();
        modeloTabla.setRowCount(0);
        List<Ruta> lista = null;
        String mensajeVacio = "";

        if ("Todas las rutas".equals(seleccion)) {
            lista = rutaService.listarTodas();
            mensajeVacio = "No existen rutas registradas en el sistema.";
        } else if ("Rutas activas".equals(seleccion)) {
            lista = rutaService.listarActivas();
            mensajeVacio = "No existen rutas activas registradas.";
        } else if ("Rutas inactivas".equals(seleccion)) {
            lista = rutaService.listarInactivas();
            mensajeVacio = "No existen rutas inactivas registradas.";
        }

        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, mensajeVacio, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Ruta r : lista)
            agregarFila(r);

        JOptionPane.showMessageDialog(this, "Listado generado correctamente.", "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una ruta de la tabla para cambiar su estado.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 6);
        String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";

        String mensaje = rutaService.cambiarEstado(codigo, nuevoEstado);
        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (mensaje.contains("correctamente")) {
            modeloTabla.setValueAt(nuevoEstado, filaSeleccionada, 6);
        }
    }

    // =====================================================
    // ACCIONES: EXPORTAR
    // =====================================================
    private void exportarExcelRutas() {
        List<Ruta> lista = rutaService.listarTodas();
        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de rutas: no existen rutas registradas.", "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
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
            String[] cols = { "Código", "Nombre", "Base Origen", "Base Destino", "Plantilla", "Duración (min)",
                    "Estado" };
            for (int c = 0; c < cols.length; c++)
                h.createCell(c).setCellValue(cols[c]);

            int rowIndex = 1;
            for (Ruta r : lista) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(r.getCodigoRuta());
                row.createCell(1).setCellValue(r.getNombre());

                // Fetch names
                Base bA = baseService.buscarPorCodigo(r.getCodigoBaseA());
                String nBaseA = bA != null ? bA.getNombre() : "ID " + r.getCodigoBaseA();
                Base bB = baseService.buscarPorCodigo(r.getCodigoBaseB());
                String nBaseB = bB != null ? bB.getNombre() : "ID " + r.getCodigoBaseB();

                row.createCell(2).setCellValue(nBaseA);
                row.createCell(3).setCellValue(nBaseB);
                row.createCell(4).setCellValue(r.getCodigoIntervalo()); // Export code is fine, or name? User asked for
                                                                        // table. Let's keep code or name? I'll use name
                                                                        // to match table.

                String nPlantilla = "Sin Asignar";
                if (r.getCodigoIntervalo() > 0) {
                    PlantillaHoraria ph = plantillaService.buscarPorCodigo(r.getCodigoIntervalo());
                    nPlantilla = ph != null ? ph.getNombre() : "ID " + r.getCodigoIntervalo();
                }
                row.createCell(4).setCellValue(nPlantilla);

                row.createCell(5).setCellValue(r.getDuracionEstimadaMinutos());
                row.createCell(6).setCellValue(r.getEstado());
            }

            for (int c = 0; c < cols.length; c++)
                sheet.autoSizeColumn(c);
            wb.write(fos);

            JOptionPane.showMessageDialog(this, "Archivo .xlsx de rutas exportado correctamente.", "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de rutas: no se pudo generar el archivo de exportación.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarFila(Ruta r) {
        // Obtener nombres para Bases
        Base bA = baseService.buscarPorCodigo(r.getCodigoBaseA());
        String nombreBaseA = (bA != null) ? bA.getNombre() : "Desc. (" + r.getCodigoBaseA() + ")";

        Base bB = baseService.buscarPorCodigo(r.getCodigoBaseB());
        String nombreBaseB = (bB != null) ? bB.getNombre() : "Desc. (" + r.getCodigoBaseB() + ")";

        // Obtener nombre para Plantilla
        String nombrePlantilla = "0 - Sin Asignar";
        if (r.getCodigoIntervalo() > 0) {
            PlantillaHoraria ph = plantillaService.buscarPorCodigo(r.getCodigoIntervalo());
            if (ph != null) {
                nombrePlantilla = r.getCodigoIntervalo() + " - " + ph.getNombre();
            } else {
                nombrePlantilla = r.getCodigoIntervalo() + " - (No Existe)";
            }
        }

        modeloTabla.addRow(new Object[] {
                r.getCodigoRuta(),
                r.getNombre(),
                nombreBaseA,
                nombreBaseB,
                nombrePlantilla,
                r.getDuracionEstimadaMinutos() + " min",
                r.getEstado()
        });
    }

    // =====================================================
    // UTILIDADES Y ESTILOS
    // =====================================================
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
        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
                return popup;
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton();
                btn.setIcon(cargarIcono("arrow_down.png", 12, 12));
                btn.setBackground(BG_PANEL);
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(BG_PANEL);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        cb.setBackground(BG_PANEL);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
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

    private void estilizarComboGrande(JComboBox<String> cb) {
        cb.setPreferredSize(new Dimension(350, 45)); // Más grande
        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
                return popup;
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (comboBox.isEnabled()) {
                            g2.setColor(new Color(70, 140, 255)); // Blue
                        } else {
                            g2.setColor(new Color(40, 50, 70)); // Dark disabled
                        }
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(Color.WHITE);
                        int w = getWidth();
                        int h = getHeight();
                        int arrowSize = 12; // Bigger arrow
                        int x = (w - arrowSize) / 2;
                        int y = (h - 6) / 2;
                        Polygon p = new Polygon();
                        p.addPoint(x, y);
                        p.addPoint(x + arrowSize, y);
                        p.addPoint(x + arrowSize / 2, y + 7);
                        g2.fillPolygon(p);
                        g2.dispose();
                    }
                };
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                if (comboBox.isEnabled()) {
                    g.setColor(new Color(70, 140, 255));
                } else {
                    g.setColor(new Color(40, 50, 70));
                }
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        cb.setBackground(new Color(70, 140, 255));
        cb.setForeground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
        cb.setFocusable(false);

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(new Color(40, 80, 180));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(70, 140, 255));
                    setForeground(Color.WHITE);
                }
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                return this;
            }
        });
    }

    private void seleccionarBaseEnCombo(JComboBox<String> cb, int codigo) {
        String p1 = codigo + " -";
        String p2 = String.format("%02d -", codigo);
        for (int i = 0; i < cb.getItemCount(); i++) {
            String item = cb.getItemAt(i);
            if (item.startsWith(p1) || item.startsWith(p2)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
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