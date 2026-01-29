import Presentacion.Ventanas.VentanaLogin;
import Presentacion.Ventanas.VentanaPrincipal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            //new VentanaLogin().setVisible(true);
            new VentanaPrincipal("Admin", "Administrador").setVisible(true);
        });
    }
}

