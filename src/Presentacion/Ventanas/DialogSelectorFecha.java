package Presentacion.Ventanas;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Diálogo personalizado para seleccionar fechas con estilo moderno.
 * Reemplaza la necesidad de JDateChooser (librería externa).
 */
public class DialogSelectorFecha extends JDialog {

    private final Color COLOR_PRIMARY = new Color(22, 44, 86);
    private final Color COLOR_ACCENT = new Color(0, 120, 215);
    private final Color COLOR_HOVER = new Color(200, 220, 255);
    private final Color COLOR_TEXT = Color.WHITE;

    private LocalDate fechaSeleccionada;
    private LocalDate fechaActualNavegacion;
    private boolean confirmado = false;

    private JLabel lblMesAnio;
    private JPanel panelDias;

    public DialogSelectorFecha(Window owner, LocalDate fechaInicial) {
        super(owner, "Seleccionar Fecha", ModalityType.APPLICATION_MODAL);
        this.fechaSeleccionada = (fechaInicial != null) ? fechaInicial : LocalDate.now();
        this.fechaActualNavegacion = this.fechaSeleccionada;

        initComponents();
        actualizarCalendario();

        setSize(320, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // --- HEADER (Navegación) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnPrev = crearBotonNavegacion("<");
        btnPrev.addActionListener(e -> {
            fechaActualNavegacion = fechaActualNavegacion.minusMonths(1);
            actualizarCalendario();
        });

        JButton btnNext = crearBotonNavegacion(">");
        btnNext.addActionListener(e -> {
            fechaActualNavegacion = fechaActualNavegacion.plusMonths(1);
            actualizarCalendario();
        });

        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMesAnio.setForeground(Color.WHITE);

        headerPanel.add(btnPrev, BorderLayout.WEST);
        headerPanel.add(lblMesAnio, BorderLayout.CENTER);
        headerPanel.add(btnNext, BorderLayout.EAST);

        // --- BODY (Días Semana + Grid) ---
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Color.WHITE);

        // Días de la semana
        JPanel panelSemana = new JPanel(new GridLayout(1, 7));
        panelSemana.setBackground(Color.WHITE);
        String[] dias = { "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom" };
        for (String dia : dias) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(Color.GRAY);
            panelSemana.add(lbl);
        }

        // Grid de días
        panelDias = new JPanel(new GridLayout(0, 7, 2, 2));
        panelDias.setBackground(Color.WHITE);
        panelDias.setBorder(new EmptyBorder(5, 5, 5, 5));

        bodyPanel.add(panelSemana, BorderLayout.NORTH);
        bodyPanel.add(panelDias, BorderLayout.CENTER);

        // --- FOOTER (Hoy / Cancelar) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton btnHoy = new JButton("Hoy");
        estilizarBoton(btnHoy);
        btnHoy.addActionListener(e -> {
            fechaSeleccionada = LocalDate.now();
            confirmado = true;
            dispose();
        });

        footerPanel.add(btnHoy);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void actualizarCalendario() {
        // Actualizar etiqueta Mes Año
        String mes = fechaActualNavegacion.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String anio = String.valueOf(fechaActualNavegacion.getYear());
        lblMesAnio.setText(mes.toUpperCase().charAt(0) + mes.substring(1) + " " + anio);

        panelDias.removeAll();

        YearMonth ym = YearMonth.from(fechaActualNavegacion);
        LocalDate firstOfMonth = ym.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Lun, 7=Dom
        int daysInMonth = ym.lengthOfMonth();

        // Relleno inicial (celdas vacías antes del primer día)
        for (int i = 1; i < dayOfWeek; i++) {
            panelDias.add(new JLabel(""));
        }

        // Días del mes
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate diaActual = ym.atDay(i);
            JButton btnDia = new JButton(String.valueOf(i));

            // Estilo base
            btnDia.setMargin(new Insets(0, 0, 0, 0)); // Evitar "..." si el botón es pequeño
            btnDia.setFocusPainted(false);
            btnDia.setBorderPainted(false);
            btnDia.setContentAreaFilled(false);
            btnDia.setOpaque(true);
            btnDia.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Colores
            if (diaActual.equals(fechaSeleccionada)) {
                btnDia.setBackground(COLOR_ACCENT);
                btnDia.setForeground(Color.WHITE);
            } else if (diaActual.equals(LocalDate.now())) {
                btnDia.setBackground(new Color(230, 240, 255));
                btnDia.setForeground(COLOR_ACCENT);
                btnDia.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT));
                btnDia.setBorderPainted(true);
            } else {
                btnDia.setBackground(Color.WHITE);
                btnDia.setForeground(Color.BLACK);
            }

            // Hover efect
            btnDia.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!diaActual.equals(fechaSeleccionada)) {
                        btnDia.setBackground(COLOR_HOVER);
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (!diaActual.equals(fechaSeleccionada)) {
                        if (diaActual.equals(LocalDate.now())) {
                            btnDia.setBackground(new Color(230, 240, 255));
                        } else {
                            btnDia.setBackground(Color.WHITE);
                        }
                    }
                }
            });

            // Acción
            btnDia.addActionListener(e -> {
                fechaSeleccionada = diaActual;
                confirmado = true;
                dispose();
            });

            panelDias.add(btnDia);
        }

        panelDias.revalidate();
        panelDias.repaint();
    }

    private JButton crearBotonNavegacion(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 255, 255, 50));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void estilizarBoton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_PRIMARY);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setFocusPainted(false);
    }

    public LocalDate getFechaSeleccionada() {
        return confirmado ? fechaSeleccionada : null;
    }

    /**
     * Método estático para usar el diálogo fácilmente
     */
    public static LocalDate mostrar(Window owner, LocalDate fechaInicial) {
        DialogSelectorFecha dialog = new DialogSelectorFecha(owner, fechaInicial);
        dialog.setVisible(true);
        return dialog.getFechaSeleccionada();
    }
}
