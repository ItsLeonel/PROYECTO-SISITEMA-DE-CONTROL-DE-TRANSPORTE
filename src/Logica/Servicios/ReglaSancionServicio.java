package Logica.Servicios;

import Logica.DAO.ReglaSancionDAO;
import Logica.Entidades.ReglaSancion;

import java.sql.SQLException;

public class ReglaSancionServicio {

    private final ReglaSancionDAO dao = new ReglaSancionDAO();

    // =========================
    // rsa13 – Registrar regla
    // =========================
    public String registrarRegla(String codigoTipo, int posiciones) {

        // Validación ERS
        if (codigoTipo == null || !codigoTipo.matches("\\d{3}") || posiciones < 1) {
            return "No se pudo registrar la regla de sanción: los datos ingresados no son válidos.";
        }

        try {
            // Ya existe
            if (dao.buscarPorTipo(codigoTipo) != null) {
                return "No se pudo registrar la regla de sanción: el tipo de incidencia ya tiene una regla asociada.";
            }

            ReglaSancion r = new ReglaSancion();
            r.setCodigoTipoIncidencia(codigoTipo);
            r.setPosicionesRetroceso(posiciones);

            dao.insertar(r);
            return "Regla de sanción registrada correctamente.";

        } catch (SQLException e) {
            return "No se pudo registrar la regla de sanción: los datos ingresados no son válidos.";
        }
    }

    // =========================
    // rsa14 – Actualizar regla
    // =========================
    public String actualizarRegla(String codigoTipo, int posiciones) {

        if (codigoTipo == null || !codigoTipo.matches("\\d{3}") || posiciones < 1) {
            return "No se pudo actualizar la regla de sanción: los datos ingresados no son válidos.";
        }

        try {
            ReglaSancion existente = dao.buscarPorTipo(codigoTipo);
            if (existente == null) {
                return "No se pudo actualizar la regla de sanción: el tipo de incidencia no tiene una regla registrada.";
            }

            existente.setPosicionesRetroceso(posiciones);
            dao.actualizar(existente);

            return "Regla de sanción actualizada correctamente.";

        } catch (SQLException e) {
            return "No se pudo actualizar la regla de sanción: los datos ingresados no son válidos.";
        }
    }
}
