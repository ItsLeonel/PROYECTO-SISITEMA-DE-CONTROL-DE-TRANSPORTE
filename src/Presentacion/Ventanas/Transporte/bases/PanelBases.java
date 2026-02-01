package Presentacion.Ventanas.Transporte.bases;

import Logica.Entidades.Base;
import Logica.Servicios.BaseService;

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

public class PanelBases extends JPanel {

    private final BaseService baseService = new BaseService();

    private final CardLayout parentCardLayout;
    private final JPanel parentPanel;

    private CardLayout accionesLayout;
    private JPanel accionesPanel;

    // Colores (Sin transparencia)
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

    private JTextField txtCodigo, txtNombre, txtDireccion;
    private JComboBox<String> cbEstado;

    private JTextField txtCodigoActualizar;
    private JTextField txtNombreActualizar;

    private JTextField txtBuscarNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbFiltroConsulta;

    public PanelBases(CardLayout parentCardLayout, JPanel parentPanel) {
        this.parentCardLayout = parentCardLayout;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        // cargarImagenFondo(); // REMOVIDO

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

        JLabel titulo = new JLabel("Gestión de Bases Operativas", SwingConstants.CENTER);
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

        // BOTÓN VOLVER (SALIR DEL MÓDULO)
        JButton btnVolver = crearBotonVolver(() -> parentCardLayout.show(parentPanel, "INICIO"));
        btnVolver.setText(" Volver al Menú Principal");
        p.add(btnVolver, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TARJETA 1
        p.add(crearTarjeta("Registrar Base", "Registrar nueva base operativa", "add.png", BTN_BLUE,
                e -> accionesLayout.show(accionesPanel, "REGISTRAR")), gbc);

        gbc.gridy++;
        // TARJETA 2
        p.add(crearTarjeta("Actualizar Base", "Modificar nombre de la base", "edit.png", BTN_GOLD,
                e -> accionesLayout.show(accionesPanel, "ACTUALIZAR")), gbc);

        gbc.gridy++;
        // TARJETA 3
        p.add(crearTarjeta("Consultar Bases", "Buscar y listar bases", "search.png", BTN_GREEN,
                e -> accionesLayout.show(accionesPanel, "CONSULTAR")), gbc);

        gbc.gridy++;
        // TARJETA 4
        p.add(crearTarjeta("Exportar Bases", "Descargar listado en Excel", "excel.png", new Color(28, 150, 100),
                e -> accionesLayout.show(accionesPanel, "EXPORTAR")), gbc);

        return p;
    }

    private JPanel crearTarjeta(String titulo, String descripcion, String icono, Color colorFondo,
            java.awt.event.ActionListener accion) {
        // Usamos un JPanel con paintComponent personalizado para transparencia
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bordes redondeados
                g2.dispose();
            }
        };
        card.setOpaque(false); // Importante para transparencia
        card.setLayout(new BorderLayout(20, 10));
        card.setPreferredSize(new Dimension(500, 100));
        card.setMaximumSize(new Dimension(500, 100));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Borde vacio
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

    // REMOVIDA LÓGICA DE FONDO DE PANTALLA

    // =====================================================
    // BOTÓN VOLVER PARA PANELES INTERNOS
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
        JLabel lblTitulo = new JLabel("Registrar Nueva Base");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 10, 2, 10);

        txtCodigo = new JTextField(20);
        txtNombre = new JTextField(20);
        txtDireccion = new JTextField(20);
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        estilizarCampo(txtCodigo);
        estilizarCampo(txtNombre);
        estilizarCampo(txtDireccion);
        estilizarCombo(cbEstado);

        // 1. CÓDIGO
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Código de base:"), gbc);
        gbc.gridx = 1;
        p.add(txtCodigo, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("2 dígitos (ej: 02)"), gbc);

        // 2. NOMBRE
        row++;
        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Nombre de la base:"), gbc);
        gbc.gridx = 1;
        p.add(txtNombre, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Base Carapungo)"), gbc);

        // 3. DIRECCIÓN
        row++;
        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Dirección:"), gbc);
        gbc.gridx = 1;
        p.add(txtDireccion, gbc);

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 10, 6, 10);
        p.add(crearLabelAyuda("Formato: Calle Principal, Calle Secundaria"), gbc);

        // 4. ESTADO
        row++;
        gbc.insets = new Insets(6, 10, 2, 10);
        gbc.gridx = 0;
        gbc.gridy = row;
        p.add(labelGrande("Estado:"), gbc);
        gbc.gridx = 1;
        p.add(cbEstado, gbc);

        // BOTONES
        row++;
        JButton btnRegistrar = new JButton(" Registrar Base");
        JButton btnLimpiar = new JButton(" Limpiar");

        estilizarBotonGrande(btnRegistrar, BTN_BLUE);
        estilizarBotonGrande(btnLimpiar, BTN_GOLD);

        btnRegistrar.setIcon(cargarIcono("add.png", 22, 22));
        btnLimpiar.setIcon(cargarIcono("reset.png", 22, 22));

        btnRegistrar.addActionListener(e -> registrarBase());
        btnLimpiar.addActionListener(e -> limpiarFormularioRegistro());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 20, 10);
        p.add(panelBotones, gbc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

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
        JLabel lblTitulo = new JLabel("Actualizar Nombre de Base");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        // 1. CÓDIGO A BUSCAR
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(12, 15, 2, 15);
        p.add(labelGrande("Código de base:"), gbc);

        JPanel panelCodigo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCodigo.setOpaque(false);

        txtCodigoActualizar = new JTextField(12);
        estilizarCampoGrande(txtCodigoActualizar);

        JButton btnVerificar = new JButton(" Verificar codigo");
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

        row++;
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 15, 8, 15);
        p.add(crearLabelAyuda("2 dígitos (ej: 02)"), gbc);

        txtNombreActualizar = new JTextField(20);
        estilizarCampoGrande(txtNombreActualizar);

        // 2. NUEVO NOMBRE
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
        p.add(crearLabelAyuda("Hasta 50 caracteres (ej: Base Norte)"), gbc);

        // BOTÓN
        row++;
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
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        p.add(btnActualizar, gbc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
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
            JOptionPane.showMessageDialog(this, "No se pudo actualizar: el código de la base no existe.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtNombreActualizar.setText("");
            return;
        }

        txtNombreActualizar.setText(b.getNombre());
        JOptionPane.showMessageDialog(this, "✅ Base cargada correctamente. Puede proceder a actualizar.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarActualizarNombre() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        if (codigoTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe verificar/cargar una base.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
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
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HEADER: Botón Volver + Título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));

        JLabel lblTitulo = new JLabel("Consultar Bases", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        headerPanel.add(btnVolver, BorderLayout.WEST);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);

        p.add(headerPanel, BorderLayout.NORTH);

        // CONTENIDO CENTRAL
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);

        // Panel Principal dividido en 2 columnas
        JPanel panelTop = new JPanel(new GridLayout(1, 2, 15, 0));
        panelTop.setOpaque(false);

        // --- COLUMNA IZQUIERDA: Búsqueda Específica ---
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

        JLabel lblNombre = new JLabel("Ingrese el nombre de la base:");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblNombre);
        panelIzq.add(Box.createVerticalStrut(5));

        txtBuscarNombre = new JTextField();
        txtBuscarNombre.setPreferredSize(new Dimension(0, 30));
        txtBuscarNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        estilizarCampo(txtBuscarNombre);
        txtBuscarNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(txtBuscarNombre);
        panelIzq.add(Box.createVerticalStrut(2));
        panelIzq.add(crearLabelAyuda("Hasta 50 caracteres (ej: Base Central)"));
        panelIzq.add(Box.createVerticalStrut(10));

        JButton btnBuscar = new JButton(" Buscar");
        btnBuscar.setIcon(cargarIcono("search.png", 20, 20));
        estilizarBotonGrande(btnBuscar, BTN_BLUE);
        btnBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBuscar.addActionListener(e -> buscarPorNombre());

        JPanel panelBtnBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelBtnBuscar.setOpaque(false);
        panelBtnBuscar.add(btnBuscar);
        panelBtnBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(panelBtnBuscar);

        // --- COLUMNA DERECHA: Listado General ---
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
                "Todas las bases",
                "Bases activas",
                "Bases inactivas"
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

        // Agregar paneles al top
        panelTop.add(panelIzq);
        panelTop.add(panelDer);

        contentPanel.add(panelTop, BorderLayout.NORTH);
        contentPanel.add(crearTabla(), BorderLayout.CENTER);

        p.add(contentPanel, BorderLayout.CENTER);

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

                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);

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

                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);

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

    private JPanel crearPanelExportar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Botón volver
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        p.add(btnVolver, gbc);

        // Espaciador vertical
        gbc.gridy = 1;
        p.add(Box.createVerticalStrut(40), gbc);

        // Título de la sección
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblTitulo = new JLabel("Exportar Datos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        // Botón Exportar
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton btnExportar = new JButton(" Exportar listado de bases");
        btnExportar.setIcon(cargarIcono("excel.png", 24, 24));
        estilizarBotonGrande(btnExportar, BTN_GREEN);
        btnExportar.setPreferredSize(new Dimension(500, 60));
        btnExportar.addActionListener(e -> exportarExcelBases());

        p.add(btnExportar, gbc);

        return p;
    }

    private void registrarBase() {
        String codigoTxt = txtCodigo.getText().trim();

        if (!codigoTxt.matches("\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la base operativa: el código de la base no cumple el formato requerido.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
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

    private void buscarPorNombre() {
        modeloTabla.setRowCount(0);
        String nombre = txtBuscarNombre.getText().trim();
        Base b = baseService.consultarBasePorNombre(nombre).startsWith("Consulta")
                ? baseService.obtenerBasePorNombre(nombre)
                : null;

        if (b != null) {
            agregarFila(b);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna base operativa con ese nombre.", "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void ejecutarListado() {
        String seleccion = (String) cbFiltroConsulta.getSelectedItem();
        modeloTabla.setRowCount(0);
        List<Base> lista = null;
        String mensajeVacio = "";

        if ("Todas las bases".equals(seleccion)) {
            lista = baseService.listarTodas();
            mensajeVacio = "No existen bases operativas registradas.";
        } else if ("Bases activas".equals(seleccion)) {
            lista = baseService.listarActivas();
            mensajeVacio = "No existen bases operativas activas registradas.";
        } else if ("Bases inactivas".equals(seleccion)) {
            lista = baseService.listarInactivas();
            mensajeVacio = "No existen bases operativas inactivas registradas.";
        }

        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, mensajeVacio, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Base b : lista)
            agregarFila(b);

        JOptionPane.showMessageDialog(this, "Listado generado correctamente.", "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de la tabla para cambiar su estado.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de bases operativas: no existen bases operativas registradas.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
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

            JOptionPane.showMessageDialog(this, "Archivo .xlsx de bases operativas exportado correctamente.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de bases operativas: no se pudo generar el archivo de exportación.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
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