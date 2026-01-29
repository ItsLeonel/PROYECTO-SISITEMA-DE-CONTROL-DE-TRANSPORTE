package Presentacion.Ventanas;

import Logica.Entidades.AuditoriaEvento;
import Presentacion.Recursos.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DialogAuditoriaDetalle extends JDialog {

    public DialogAuditoriaDetalle(Window owner, AuditoriaEvento ev) {
        super(owner, "Detalle de Auditoría", ModalityType.APPLICATION_MODAL);
        setSize(720, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.setBackground(UITheme.BG);

        JLabel title = new JLabel("Evento #" + ev.getId());
        title.setFont(UITheme.H2);
        title.setForeground(UITheme.TEXT);

        content.add(title, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(0, 2, 10, 8));
        info.setOpaque(false);

        info.add(new JLabel("Fecha/Hora:"));
        info.add(new JLabel(fmt.format(ev.getFechaHora())));

        info.add(new JLabel("Usuario:"));
        info.add(new JLabel(ev.getUsuarioLogin()));

        info.add(new JLabel("Rol:"));
        info.add(new JLabel(ev.getRol()));

        info.add(new JLabel("Módulo:"));
        info.add(new JLabel(ev.getModulo()));

        info.add(new JLabel("Acción:"));
        info.add(new JLabel(ev.getAccion()));

        info.add(new JLabel("Resultado:"));
        info.add(new JLabel(ev.getResultado()));

        content.add(info, BorderLayout.CENTER);

        JTextArea txtDetalle = new JTextArea(ev.getDetalle());
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        txtDetalle.setEditable(false);

        JScrollPane sp = new JScrollPane(txtDetalle);
        sp.setBorder(BorderFactory.createTitledBorder("Detalle"));

        content.add(sp, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(btnCerrar);

        add(footer, BorderLayout.SOUTH);
    }
}

