package Presentacion.Ventanas.Transporte.plantillas;

import Logica.DAO.RutaDAO;

import Logica.Entidades.PlantillaHoraria;
import Logica.Entidades.PlantillaHorariaFranja;
import Logica.Servicios.PlantillaHorariaService;
import Logica.Servicios.RutaService;
import Logica.Entidades.Ruta;
import Logica.Servicios.PlantillaHorariaService.ResultadoPlantilla;
import Logica.Servicios.PlantillaHorariaService.ResultadoListaPlantillas;

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

/**
 * Panel mejorado: Plantillas Horarias con diseño de tarjetas estilo Buses
 */
public class PanelPlantillasHorarias extends JPanel {

    private final PlantillaHorariaService service = new PlantillaHorariaService();
    private final RutaService rutaService = new RutaService();
    // private final RutaDAO rutaDAO = new RutaDAO(); // Usaremos el servicio

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

    // ===== CAMPOS REGISTRAR =====
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JTextField[] txtFranjas;
    private JComboBox<String> cbRuta;
    private JComboBox<String> cbEstado;

    // ===== CAMPOS ACTUALIZAR =====
    private JTextField txtCodigoActualizar;
    private JTextField txtNombreActualizar, txtHoraInicioActualizar, txtHoraFinActualizar;
    private JTextField[] txtFranjasActualizar = new JTextField[6];
    private JTextField txtRutaActualizar;

    // ===== CAMPOS CONSULTAR =====
    private JTextField txtCodigoConsultar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> cbFiltroConsulta;

    public PanelPlantillasHorarias(CardLayout parentCardLayout, JPanel parentPanel) {
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

    // =====================================================
    // FONDO DE PANTALLA
    // =====================================================

    // =====================================================
    // HEADER (SOLO TÍTULO)
    // =====================================================
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Gestión de Plantillas Horarias", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        header.add(titulo, BorderLayout.CENTER);

        return header;
    }

    // =====================================================
    // PANTALLA INICIO (TARJETAS ESTILO BUSES)
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
        btnVolver.setText(" Volver al Menú Principal"); // Texto más descriptivo
        p.add(btnVolver, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TARJETA 1: Registrar
        JPanel card1 = crearTarjeta(
                "Registrar Plantilla",
                "Crear nueva plantilla horaria",
                "add.png",
                BTN_BLUE,
                e -> accionesLayout.show(accionesPanel, "REGISTRAR"));
        p.add(card1, gbc);

        gbc.gridy++;
        // TARJETA 2: Actualizar
        JPanel card2 = crearTarjeta(
                "Actualizar Plantilla",
                "Modificar plantilla existente",
                "edit.png",
                BTN_GOLD,
                e -> accionesLayout.show(accionesPanel, "ACTUALIZAR"));
        p.add(card2, gbc);

        gbc.gridy++;
        // TARJETA 3: Consultar
        JPanel card3 = crearTarjeta(
                "Consultar Plantillas",
                "Buscar y listar plantillas",
                "search.png",
                BTN_GREEN,
                e -> accionesLayout.show(accionesPanel, "CONSULTAR"));
        p.add(card3, gbc);

        gbc.gridy++;
        // TARJETA 4: Exportar
        JPanel card4 = crearTarjeta(
                "Exportar Plantillas",
                "Descargar listado en Excel",
                "excel.png",
                new Color(28, 150, 100),
                e -> accionesLayout.show(accionesPanel, "EXPORTAR"));
        p.add(card4, gbc);

        return p;
    }

    private JPanel crearTarjeta(String titulo, String descripcion, String icono, Color colorFondo,
            java.awt.event.ActionListener accion) {
        // JPanel con transparencia
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
    // PANEL REGISTRAR (CON SCROLL)
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

        // Título
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 20, 10);
        JLabel lblTitulo = new JLabel("Registrar Nueva Plantilla Horaria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        // Initialize text fields
        txtCodigo = new JTextField(15);
        txtNombre = new JTextField(15);
        txtHoraInicio = new JTextField(10);
        txtHoraFin = new JTextField(10);
        cbRuta = new JComboBox<>();
        cargarRutas(cbRuta);

        txtFranjas = new JTextField[6];
        for (int i = 0; i < 6; i++) {
            txtFranjas[i] = new JTextField(5);
            estilizarCampo(txtFranjas[i]);
        }

        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

        estilizarCampo(txtCodigo);
        estilizarCampo(txtNombre);
        estilizarCampo(txtHoraInicio);
        estilizarCampo(txtHoraFin);
        estilizarCombo(cbRuta); // New helper needed or reuse one
        estilizarCombo(cbEstado);

        // PANEL CONTENEDOR DE COLUMNAS
        JPanel panelColumnas = new JPanel(new GridLayout(1, 2, 40, 0));
        panelColumnas.setOpaque(false);

        // === COLUMNA IZQUIERDA (Info General) ===
        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        panelIzquierdo.setOpaque(false);
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0;
        gbcL.gridy = 0;
        gbcL.fill = GridBagConstraints.HORIZONTAL;
        gbcL.weightx = 1.0;
        gbcL.insets = new Insets(5, 0, 5, 0);

        int rowL = 0;
        agregarCampo(panelIzquierdo, gbcL, rowL, "Código / ID:", txtCodigo, "Número entero (01-99)");
        rowL += 2;
        agregarCampo(panelIzquierdo, gbcL, rowL, "Nombre:", txtNombre, "Máximo 50 caracteres");
        rowL += 2;
        agregarCampo(panelIzquierdo, gbcL, rowL, "Hora inicio operaciones:", txtHoraInicio,
                "Formato HH:MM (ej: 05:00)");
        rowL += 2;
        agregarCampo(panelIzquierdo, gbcL, rowL, "Hora fin operaciones:", txtHoraFin, "Formato HH:MM (ej: 21:00)");

        // === COLUMNA DERECHA (Franjas y Detalles) ===
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.gridx = 0;
        gbcR.gridy = 0;
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.weightx = 1.0;
        gbcR.insets = new Insets(5, 0, 5, 0);

        int rowR = 0;
        // Título Franjas
        gbcR.gridx = 0;
        gbcR.gridy = rowR++;
        gbcR.gridwidth = 2;
        gbcR.insets = new Insets(0, 0, 10, 0);
        JLabel lblFranjas = new JLabel("Franjas Horarias (Minutos)");
        lblFranjas.setForeground(BTN_GOLD);
        lblFranjas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFranjas.setHorizontalAlignment(SwingConstants.LEFT);
        panelDerecho.add(lblFranjas, gbcR);

        gbcR.gridwidth = 1; // RESET gridwidth
        gbcR.insets = new Insets(5, 0, 5, 0); // RESET insets

        String[] labelsFranjas = {
                "Franja 1 (Inicio → 08:00):",
                "Franja 2 (08:00 → 11:00):",
                "Franja 3 (11:00 → 13:00):",
                "Franja 4 (13:00 → 15:00):",
                "Franja 5 (15:00 → 19:00):",
                "Franja 6 (19:00 → Fin):"
        };

        for (int i = 0; i < 6; i++) {
            // Nota: agregarCampo aumenta row en 2 (label+field row, help row)
            // Aquí lo hacemos manualmente para compactar o usamos agregarCampo
            agregarCampo(panelDerecho, gbcR, rowR, labelsFranjas[i], txtFranjas[i], "Minutos (ej: 15, 20)");
            rowR += 2;
        }

        // Otros campos derecha
        // agregarCampo(panelDerecho, gbcR, rowR, "Código de ruta:", txtRuta, "2 dígitos
        // (ej: 01)");
        gbcR.gridx = 0;
        gbcR.gridy = rowR;
        gbcR.insets = new Insets(8, 10, 4, 10);
        panelDerecho.add(labelGrande("Ruta Asociada:"), gbcR);
        gbcR.gridx = 1;
        panelDerecho.add(cbRuta, gbcR);
        // No help label needed as it is a combo

        rowR += 2;

        // Estado (Sin ayuda)
        gbcR.gridx = 0;
        gbcR.gridy = rowR;
        gbcR.insets = new Insets(8, 10, 4, 10);
        panelDerecho.add(labelGrande("Estado:"), gbcR);
        gbcR.gridx = 1;
        panelDerecho.add(cbEstado, gbcR);
        rowR++;

        panelColumnas.add(panelIzquierdo);
        panelColumnas.add(panelDerecho);

        // Agregar panelColumnas al principal
        gbc.gridx = 0;
        gbc.gridy = 2; // Debajo del título
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        p.add(panelColumnas, gbc);

        // BOTÓN REGISTRAR
        JButton btnRegistrar = new JButton(" Registrar Plantilla Horaria");
        btnRegistrar.setIcon(cargarIcono("add.png", 22, 22));
        estilizarBotonGrande(btnRegistrar, BTN_BLUE);
        btnRegistrar.addActionListener(e -> registrarPlantilla());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 20, 10);
        p.add(btnRegistrar, gbc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    // =====================================================
    // PANEL ACTUALIZAR (CON SCROLL Y VERIFICAR CÓDIGO)
    // =====================================================
    private JScrollPane crearPanelActualizar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        // REDUCED MARGINS TO PREVENT HORIZONTAL SCROLLBAR
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

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

        // Título (Reduced bottom inset)
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel lblTitulo = new JLabel("Actualizar Plantilla Horaria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        p.add(lblTitulo, gbc);

        // Inicializar campos
        txtCodigoActualizar = new JTextField(10);
        txtNombreActualizar = new JTextField(15);
        txtHoraInicioActualizar = new JTextField(10);
        txtHoraFinActualizar = new JTextField(10);
        txtRutaActualizar = new JTextField(15);
        txtRutaActualizar.setEditable(false);

        for (int i = 0; i < 6; i++) {
            txtFranjasActualizar[i] = new JTextField(8);
            estilizarCampo(txtFranjasActualizar[i]);
        }

        estilizarCampo(txtCodigoActualizar);
        estilizarCampo(txtNombreActualizar);
        estilizarCampo(txtHoraInicioActualizar);
        estilizarCampo(txtHoraFinActualizar);
        estilizarCampo(txtRutaActualizar);
        txtRutaActualizar.setBackground(new Color(40, 50, 70));

        // PANEL CONTENEDOR DE COLUMNAS (REDUCED GAP)
        JPanel panelColumnas = new JPanel(new GridLayout(1, 2, 20, 0));
        panelColumnas.setOpaque(false);

        // === COLUMNA IZQUIERDA (Busqueda + Info General) ===
        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        panelIzquierdo.setOpaque(false);
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0;
        gbcL.gridy = 0;
        gbcL.fill = GridBagConstraints.HORIZONTAL;
        gbcL.weightx = 1.0;
        gbcL.insets = new Insets(5, 0, 5, 0);

        int rowL = 0;
        // BÚSQUEDA CÓDIGO (Span 2 columns)
        gbcL.gridwidth = 2;
        panelIzquierdo.add(labelGrande("Código a Buscar:"), gbcL);
        gbcL.gridy = 1;

        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setOpaque(false);
        panelBusqueda.add(txtCodigoActualizar, BorderLayout.CENTER);

        JButton btnVerificar = new JButton(" Verificar");
        btnVerificar.setIcon(cargarIcono("search.png", 18, 18));
        btnVerificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setBackground(BTN_GREEN);

        // FIX FOR BUTTON APPEARANCE
        btnVerificar.setOpaque(true);
        btnVerificar.setContentAreaFilled(true);
        btnVerificar.setFocusPainted(false);
        btnVerificar.setBorderPainted(false);
        btnVerificar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btnVerificar.addActionListener(e -> verificarYCargarPlantilla());
        panelBusqueda.add(btnVerificar, BorderLayout.EAST);

        panelIzquierdo.add(panelBusqueda, gbcL);
        gbcL.gridy = 2;
        // Reduced bottom margin for help label to tighten layout
        gbcL.insets = new Insets(5, 0, 15, 0);
        panelIzquierdo.add(crearLabelAyuda("Ingrese código y verifique"), gbcL);

        // Reset gridwidth to 1 for the form fields
        gbcL.gridwidth = 1;
        // Reset insets for next fields
        gbcL.insets = new Insets(5, 0, 5, 0);
        rowL = 3;

        // Campos Info
        agregarCampo(panelIzquierdo, gbcL, rowL, "Nombre:", txtNombreActualizar, "Máximo 50 caracteres");
        rowL += 2;
        agregarCampo(panelIzquierdo, gbcL, rowL, "Hora inicio:", txtHoraInicioActualizar, "Formato HH:MM");
        rowL += 2;
        agregarCampo(panelIzquierdo, gbcL, rowL, "Hora fin:", txtHoraFinActualizar, "Formato HH:MM");

        // === COLUMNA DERECHA (Franjas y Detalles) ===
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.gridx = 0;
        gbcR.gridy = 0;
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.weightx = 1.0;
        gbcR.insets = new Insets(5, 0, 5, 0);

        int rowR = 0;
        // Título Franjas
        gbcR.gridx = 0;
        gbcR.gridy = rowR++;
        gbcR.gridwidth = 2;
        gbcR.insets = new Insets(0, 0, 10, 0);
        JLabel lblFranjas = new JLabel("Franjas Horarias (Minutos)");
        lblFranjas.setForeground(BTN_GOLD);
        lblFranjas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFranjas.setHorizontalAlignment(SwingConstants.LEFT);
        panelDerecho.add(lblFranjas, gbcR);

        gbcR.gridwidth = 1;
        gbcR.insets = new Insets(5, 0, 5, 0);

        String[] labelsFranjas = {
                "Franja 1 (Inicio → 08:00):",
                "Franja 2 (08:00 → 11:00):",
                "Franja 3 (11:00 → 13:00):",
                "Franja 4 (13:00 → 15:00):",
                "Franja 5 (15:00 → 19:00):",
                "Franja 6 (19:00 → Fin):"
        };

        for (int i = 0; i < 6; i++) {
            agregarCampo(panelDerecho, gbcR, rowR, labelsFranjas[i], txtFranjasActualizar[i], "Minutos");
            rowR += 2;
        }

        // Ruta
        agregarCampo(panelDerecho, gbcR, rowR, "Ruta (solo lectura):", txtRutaActualizar, "No editable");
        rowR += 2;

        panelColumnas.add(panelIzquierdo);
        panelColumnas.add(panelDerecho);

        // Agregar panelColumnas al principal
        gbc.gridx = 0;
        gbc.gridy = 2; // Debajo del título
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        p.add(panelColumnas, gbc);

        // BOTÓN ACTUALIZAR
        JButton btnActualizar = new JButton(" ACTUALIZAR PLANTILLA");
        btnActualizar.setIcon(cargarIcono("edit.png", 22, 22));
        estilizarBotonGrande(btnActualizar, BTN_GOLD);
        btnActualizar.setForeground(new Color(20, 20, 20)); // Texto oscuro para contraste en botón dorado
        btnActualizar.addActionListener(e -> ejecutarActualizacion());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 20, 10);
        p.add(btnActualizar, gbc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    // =====================================================
    // PANEL CONSULTAR
    // =====================================================
    private JPanel crearPanelConsultar() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Botón volver arriba
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        topPanel.add(btnVolver, BorderLayout.WEST);

        JLabel lblTitulo = new JLabel("Consultar Plantillas Horarias");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 0));
        topPanel.add(lblTitulo, BorderLayout.CENTER);

        p.add(topPanel, BorderLayout.NORTH);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBusqueda.setOpaque(false);

        // COLUMNA IZQUIERDA: Búsqueda por código
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(BG_CARD);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblBusqueda = new JLabel("🔍 Consultar por Código");
        lblBusqueda.setForeground(TXT_LIGHT);
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblBusqueda);
        panelIzq.add(Box.createVerticalStrut(12));

        txtCodigoConsultar = new JTextField();
        txtCodigoConsultar.setPreferredSize(new Dimension(0, 35));
        txtCodigoConsultar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        estilizarCampo(txtCodigoConsultar);
        txtCodigoConsultar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(txtCodigoConsultar);
        panelIzq.add(Box.createVerticalStrut(2));
        panelIzq.add(crearLabelAyuda("2 dígitos (de 01 a 99)"));
        panelIzq.add(Box.createVerticalStrut(10));

        JButton btnConsultar = new JButton(" Consultar Plantilla");
        btnConsultar.setIcon(cargarIcono("search.png", 20, 20));
        estilizarBotonGrande(btnConsultar, BTN_BLUE);
        btnConsultar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConsultar.addActionListener(e -> consultarPorCodigo());
        panelIzq.add(btnConsultar);

        // COLUMNA DERECHA: Listado general
        JPanel panelDer = new JPanel();
        panelDer.setLayout(new BoxLayout(panelDer, BoxLayout.Y_AXIS));
        panelDer.setBackground(BG_CARD);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblListado = new JLabel("📋 Listado General");
        lblListado.setForeground(TXT_LIGHT);
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDer.add(lblListado);
        panelDer.add(Box.createVerticalStrut(12));

        cbFiltroConsulta = new JComboBox<>(new String[] {
                "Todas las plantillas",
                "Plantillas activas",
                "Plantillas inactivas"
        });
        estilizarCombo(cbFiltroConsulta);
        cbFiltroConsulta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbFiltroConsulta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDer.add(cbFiltroConsulta);
        panelDer.add(Box.createVerticalStrut(12));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnListar = new JButton(" Listar");
        btnListar.setIcon(cargarIcono("list.png", 20, 20));
        estilizarBotonGrande(btnListar, BTN_BLUE);
        btnListar.addActionListener(e -> ejecutarListado());

        JButton btnCambiarEstado = new JButton(" Cambiar Estado");
        btnCambiarEstado.setIcon(cargarIcono("refresh.png", 20, 20));
        estilizarBotonGrande(btnCambiarEstado, BTN_GOLD);
        btnCambiarEstado.addActionListener(e -> cambiarEstadoSeleccionado());

        panelBotones.add(btnListar);
        panelBotones.add(btnCambiarEstado);
        panelDer.add(panelBotones);

        panelBusqueda.add(panelIzq);
        panelBusqueda.add(panelDer);

        JPanel wrapperBusqueda = new JPanel(new BorderLayout());
        wrapperBusqueda.setOpaque(false);
        wrapperBusqueda.add(panelBusqueda, BorderLayout.NORTH);

        // PANEL DE ENCABEZADO Y BÚSQUEDA COMBINADOS
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setOpaque(false);

        northContainer.add(topPanel);
        northContainer.add(wrapperBusqueda);

        p.add(northContainer, BorderLayout.NORTH);
        p.add(crearTabla(), BorderLayout.CENTER);

        return p;
    }

    private JScrollPane crearTabla() {
        modelo = new DefaultTableModel(
                new Object[] { "Código", "Nombre", "Hora Inicio", "Hora Fin", "Ruta", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(Color.WHITE);
        tabla.setBackground(new Color(22, 44, 86));
        tabla.setShowGrid(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Color fila1 = new Color(22, 44, 86);
        Color fila2 = new Color(26, 50, 96);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);

                if (s) {
                    l.setBackground(new Color(70, 140, 255));
                } else {
                    l.setBackground(r % 2 == 0 ? fila1 : fila2);
                }
                l.setForeground(Color.WHITE);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_DARK, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return l;
            }
        });

        // Renderizador para columna Estado
        tabla.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {

                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setOpaque(true);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
                l.setForeground(Color.WHITE);

                if ("ACTIVO".equalsIgnoreCase(String.valueOf(v)) || "Activo".equalsIgnoreCase(String.valueOf(v))) {
                    l.setBackground(new Color(46, 204, 113));
                } else {
                    l.setBackground(new Color(231, 76, 60));
                }
                l.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
                return l;
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

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(22, 44, 86));
        tabla.setFillsViewportHeight(true);
        return sp;
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
        gbc.insets = new Insets(0, 0, 20, 0);

        JButton btnVolver = crearBotonVolver(() -> accionesLayout.show(accionesPanel, "INICIO"));
        p.add(btnVolver, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);

        JButton btnExportar = new JButton(" Exportar Plantillas Horarias a Excel");
        btnExportar.setIcon(cargarIcono("excel.png", 28, 28));
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setBackground(BTN_GREEN);
        btnExportar.setPreferredSize(new Dimension(600, 70));
        btnExportar.setFocusPainted(false);
        btnExportar.setBorderPainted(false);
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportar.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        btnExportar.addActionListener(e -> exportarExcel());

        p.add(btnExportar, gbc);
        return p;
    }

    // =====================================================
    // UTILIDAD: Agregar campo con label y ayuda
    // =====================================================
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField campo,
            String ayuda) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0; // Label takes minimal space
        gbc.insets = new Insets(8, 10, 4, 10);
        panel.add(labelGrande(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Field takes remaining space
        panel.add(campo, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 10, 8, 10);
        panel.add(crearLabelAyuda(ayuda), gbc);
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

    // =====================================================
    // ACCIONES: REGISTRAR
    // =====================================================
    private void registrarPlantilla() {
        PlantillaHoraria plantilla = new PlantillaHoraria();
        plantilla.setCodigoPlantilla(parseInt(txtCodigo.getText().trim()));
        plantilla.setNombre(txtNombre.getText().trim());
        plantilla.setHoraInicioOperaciones(txtHoraInicio.getText().trim());
        plantilla.setHoraFinOperaciones(txtHoraFin.getText().trim());
        try {
            String selRuta = (String) cbRuta.getSelectedItem();
            if (selRuta == null || selRuta.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una ruta.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Format: "Code - Name" -> take Code
            // The code is the first part before " - "
            // But wait, Route code is String 2 digits? Yes.
            String[] parts = selRuta.split(" - ");
            // The entity usually expects int? Let's check PlantillaHoraria.
            // Looking at previous code, it doesn't modify code here.
            // We need to pass the code to PlantillaHoraria.
            // PlantillaHoraria likely has setCodigoRuta(int)?
            // Let's assume int based on previous usage patterns or String if Entidad uses
            // String.
            // Wait, in RutaService: getCodigoRuta() returns String.
            // But PlantillaHorariaDAO might expect int.
            // Let's safe parse to int if it's numeric, or keep as String?
            // Checking PlantillaHoraria.java previously... not modified in this session.
            // Checking `registrarPlantilla` original code. `txtRuta` was used.
            // Assuming `p.setCodigoRuta(Integer.parseInt(txtRuta.getText()));` was likely
            // there?
            // Let's inspect `registrarPlantilla` method in a moment if needed.
            // Actually, I can replace the whole method block for `registrarPlantilla`.
            // If `PlantillaHoraria` has `setCodigoRuta(int)`, then:
            plantilla.setCodigoRuta(parts[0]); // Assuming setCodigoRuta takes String based on Ruta.getCodigoRuta()
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código de ruta seleccionado no es válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        plantilla.setEstado(cbEstado.getSelectedItem().toString());

        int[] tiempos = new int[6];
        for (int i = 0; i < 6; i++) {
            tiempos[i] = parseInt(txtFranjas[i].getText().trim());
        }

        String msg = service.registrar(plantilla, tiempos);
        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (msg.contains("correctamente")) {
            limpiarCamposRegistrar();
        }
    }

    private void limpiarCamposRegistrar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        if (cbRuta.getItemCount() > 0)
            cbRuta.setSelectedIndex(0);
        for (int i = 0; i < 6; i++) {
            txtFranjas[i].setText("");
        }
        cbEstado.setSelectedIndex(0);
    }

    private void cargarRutas(JComboBox<String> cb) {
        cb.removeAllItems();
        List<Ruta> rutas = rutaService.listarActivas();
        if (rutas != null) {
            for (Ruta r : rutas) {
                // "01 - NombreRuta"
                cb.addItem(String.format("%s - %s", r.getCodigoRuta(), r.getNombre()));
            }
        }
    }

    // =====================================================
    // ACCIONES: ACTUALIZAR
    // =====================================================
    private void verificarYCargarPlantilla() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        int codigo;
        try {
            codigo = Integer.parseInt(codigoTxt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResultadoPlantilla resultado = service.consultarPorCodigo(codigo);

        if (resultado.getPlantilla() == null) {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
            limpiarCamposActualizar();
            return;
        }

        PlantillaHoraria p = resultado.getPlantilla();
        txtNombreActualizar.setText(p.getNombre());
        txtHoraInicioActualizar.setText(p.getHoraInicioOperaciones());
        txtHoraFinActualizar.setText(p.getHoraFinOperaciones());
        txtRutaActualizar.setText(p.getCodigoRuta());

        List<PlantillaHorariaFranja> franjas = resultado.getFranjas();
        for (PlantillaHorariaFranja franja : franjas) {
            int idx = franja.getFranjaId() - 1;
            if (idx >= 0 && idx < 6) {
                txtFranjasActualizar[idx].setText(String.valueOf(franja.getTiempoMinutos()));
            }
        }

        JOptionPane.showMessageDialog(this, "✅ Plantilla cargada correctamente. Puede proceder a actualizar.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarActualizacion() {
        String codigoTxt = txtCodigoActualizar.getText().trim();

        if (codigoTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe verificar/cargar una plantilla.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = parseInt(codigoTxt);

        PlantillaHoraria p = new PlantillaHoraria();
        p.setCodigoPlantilla(codigo);
        p.setNombre(txtNombreActualizar.getText().trim());
        p.setHoraInicioOperaciones(txtHoraInicioActualizar.getText().trim());
        p.setHoraFinOperaciones(txtHoraFinActualizar.getText().trim());

        if (p.getNombre().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validarHora(p.getHoraInicioOperaciones()) || !validarHora(p.getHoraFinOperaciones())) {
            JOptionPane.showMessageDialog(this,
                    "Las horas deben tener formato HH:MM válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder mensajes = new StringBuilder();
        boolean todoCorrecto = true;

        for (int i = 0; i < 6; i++) {
            int nuevoTiempo = parseInt(txtFranjasActualizar[i].getText().trim());
            if (nuevoTiempo > 0) {
                String msg = service.actualizarIntervaloFranja(codigo, i + 1, nuevoTiempo);
                if (!msg.contains("correctamente")) {
                    todoCorrecto = false;
                    mensajes.append("Franja ").append(i + 1).append(": ").append(msg).append("\n");
                }
            }
        }

        if (todoCorrecto) {
            JOptionPane.showMessageDialog(this,
                    "Plantilla horaria actualizada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCamposActualizar();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Actualización parcial:\n" + mensajes.toString(),
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCamposActualizar() {
        txtCodigoActualizar.setText("");
        txtNombreActualizar.setText("");
        txtHoraInicioActualizar.setText("");
        txtHoraFinActualizar.setText("");
        txtRutaActualizar.setText("");
        for (int i = 0; i < 6; i++) {
            txtFranjasActualizar[i].setText("");
        }
    }

    // =====================================================
    // ACCIONES: CONSULTAR
    // =====================================================
    private void consultarPorCodigo() {
        String codigoTxt = txtCodigoConsultar.getText().trim();

        if (codigoTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un código de plantilla.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = parseInt(codigoTxt);
        ResultadoPlantilla resultado = service.consultarPorCodigo(codigo);

        if (resultado.getPlantilla() == null) {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarTabla();
            return;
        }

        PlantillaHoraria p = resultado.getPlantilla();
        limpiarTabla();
        modelo.addRow(new Object[] {
                p.getCodigoPlantilla(),
                p.getNombre(),
                p.getHoraInicioOperaciones(),
                p.getHoraFinOperaciones(),
                p.getCodigoRuta(),
                p.getEstado()
        });

        mostrarDialogoFranjas(resultado.getFranjas());

        JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDialogoFranjas(List<PlantillaHorariaFranja> franjas) {
        if (franjas.isEmpty())
            return;

        StringBuilder sb = new StringBuilder("FRANJAS CONFIGURADAS:\n\n");
        for (PlantillaHorariaFranja f : franjas) {
            sb.append("• ").append(f.getDescripcionFranja())
                    .append(" → ").append(f.getTiempoMinutos()).append(" minutos\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Detalle de Franjas",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarListado() {
        String seleccion = (String) cbFiltroConsulta.getSelectedItem();
        limpiarTabla();

        List<PlantillaHoraria> lista = null;
        String mensajeVacio = "";

        if ("Todas las plantillas".equals(seleccion)) {
            lista = service.listarTodas();
            mensajeVacio = "No existen plantillas horarias registradas.";
        } else if ("Plantillas activas".equals(seleccion)) {
            ResultadoListaPlantillas r = service.consultarActivas();
            lista = r.getPlantillas();
            mensajeVacio = r.getMensaje();
        } else if ("Plantillas inactivas".equals(seleccion)) {
            ResultadoListaPlantillas r = service.consultarInactivas();
            lista = r.getPlantillas();
            mensajeVacio = r.getMensaje();
        }

        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, mensajeVacio, "Resultado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        cargarTabla(lista);
        JOptionPane.showMessageDialog(this, "Listado generado correctamente.", "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoSeleccionado() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una plantilla de la tabla para cambiar su estado.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) modelo.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modelo.getValueAt(filaSeleccionada, 5);

        String mensaje = "";
        if ("Activo".equalsIgnoreCase(estadoActual)) {
            mensaje = service.inactivar(codigo);
        } else {
            mensaje = service.activar(codigo);
        }

        JOptionPane.showMessageDialog(this, mensaje, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        if (mensaje.contains("correctamente")) {
            String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";
            modelo.setValueAt(nuevoEstado, filaSeleccionada, 5);
        }
    }

    // =====================================================
    // ACCIONES: EXPORTAR
    // =====================================================
    private void exportarExcel() {
        List<PlantillaHoraria> lista = service.listarTodas();

        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de plantillas horarias: no existen plantillas registradas.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new java.io.File("plantillas_horarias.xlsx"));
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        try (org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                java.io.FileOutputStream fos = new java.io.FileOutputStream(ch.getSelectedFile())) {

            org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Plantillas Horarias");
            String[] cols = { "Código", "Nombre", "Hora Inicio", "Hora Fin", "Ruta", "Estado" };

            org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
            for (int c = 0; c < cols.length; c++)
                h.createCell(c).setCellValue(cols[c]);

            int rowIndex = 1;
            for (PlantillaHoraria p : lista) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(p.getCodigoPlantilla());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getHoraInicioOperaciones());
                row.createCell(3).setCellValue(p.getHoraFinOperaciones());
                row.createCell(4).setCellValue(p.getCodigoRuta());
                row.createCell(5).setCellValue(p.getEstado());
            }

            for (int c = 0; c < cols.length; c++)
                sheet.autoSizeColumn(c);

            wb.write(fos);

            JOptionPane.showMessageDialog(this,
                    "Archivo .xlsx de plantillas horarias exportado correctamente.",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el archivo .xlsx de plantillas horarias: error al generar el archivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    // UTILIDADES
    // =====================================================
    private void cargarTabla(List<PlantillaHoraria> lista) {
        limpiarTabla();
        for (PlantillaHoraria p : lista) {
            modelo.addRow(new Object[] {
                    p.getCodigoPlantilla(),
                    p.getNombre(),
                    p.getHoraInicioOperaciones(),
                    p.getHoraFinOperaciones(),
                    p.getCodigoRuta(),
                    p.getEstado()
            });
        }
    }

    private void limpiarTabla() {
        modelo.setRowCount(0);
    }

    private boolean validarHora(String hora) {
        if (hora == null || !hora.matches("^\\d{2}:\\d{2}$")) {
            return false;
        }
        int hh = Integer.parseInt(hora.substring(0, 2));
        int mm = Integer.parseInt(hora.substring(3, 5));
        return hh >= 0 && hh <= 23 && mm >= 0 && mm <= 59;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    // =====================================================
    // ESTILOS Y DISEÑO
    // =====================================================
    private JLabel labelGrande(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void estilizarCampo(JTextField t) {
        t.setBackground(BG_PANEL);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        t.setPreferredSize(new Dimension(0, 35));
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
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
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