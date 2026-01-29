package Presentacion.Ventanas.Unidades;

import Logica.Entidades.Bus;
import Logica.Servicios.BusService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Ventana exclusiva para el registro de Unidades
 * Cumple RU1+2+3 del documento
 */
public class VentanaRegistroUnidades extends JFrame {

    // ===== SERVICIO =====
    private final BusService busService = new BusService();

    // ===== COLORES =====
    private final Color BG_MAIN  = new Color(11, 22, 38);
    private final Color BG_PANEL = new Color(18, 36, 64);
    private final Color BTN_MAIN = new Color(33, 90, 190);
    private final Color TXT_SEC  = new Color(190, 200, 215);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public VentanaRegistroUnidades() {
        setTitle("Registro de Unidades");
        setSize(520, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(16, 16));
        getContentPane().setBackground(BG_MAIN);

        add(crearHeader(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    // =====================================================
    // HEADER
    // =====================================================
    private JPanel crearHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JLabel titulo = new JLabel("Registro de Unidades");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
            "Registrar una nueva unidad en el sistema"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(TXT_SEC);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitulo);

        return panel;
    }

    // =====================================================
    // CONTENIDO
    // =====================================================
    private JPanel crearContenido() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        panel.setOpaque(false);

        JButton btnRegistrar = botonPrimario("Registrar Unidad");
        btnRegistrar.addActionListener(e -> abrirDialogoRegistro());

        panel.add(btnRegistrar);
        return panel;
    }

    // =====================================================
    // LÓGICA
    // =====================================================
    private void abrirDialogoRegistro() {
        DialogBus dialogo = new DialogBus(this, "Registrar Unidad", null);
        dialogo.setVisible(true);

        Bus nueva = dialogo.getResult();
        if (nueva == null) return;

        try {
            busService.registrar(nueva);

            JOptionPane.showMessageDialog(
                this,
                "Bus registrado correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // UI HELPERS
    // =====================================================
    private JButton botonPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(BTN_MAIN);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(14, 32, 14, 32));
        return b;
    }
}
