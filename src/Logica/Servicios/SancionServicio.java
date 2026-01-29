package Logica.Servicios;

import Logica.DAO.IncidenciaDAO;
import Logica.DAO.ReglaSancionDAO;
import Logica.DAO.SancionDAO;
import Logica.Entidades.Incidencia;
import Logica.Entidades.ReglaSancion;
import Logica.Entidades.Sancion;

import java.sql.SQLException;
import java.time.LocalDate;

public class SancionServicio {

    private final IncidenciaDAO incidenciaDAO = new IncidenciaDAO();
    private final ReglaSancionDAO reglaDAO = new ReglaSancionDAO();
    private final SancionDAO sancionDAO = new SancionDAO();

    public String aplicarSancion(String codigoIncidencia,
                                 LocalDate fechaAplicacion,
                                 String responsable) {

        try {
            if (codigoIncidencia == null || codigoIncidencia.isBlank()
                    || fechaAplicacion == null
                    || responsable == null || responsable.isBlank()) {
                return "No se pudo aplicar la sanción: los datos ingresados no son válidos.";
            }

            Incidencia i = incidenciaDAO.buscarPorCodigo(codigoIncidencia);
            if (i == null) {
                return "No se pudo aplicar la sanción: la incidencia no existe.";
            }

            if ("ANULADA".equals(i.getEstado())) {
                return "No se pudo aplicar la sanción: la incidencia se encuentra anulada.";
            }

            ReglaSancion regla = reglaDAO.buscarPorTipo(i.getCodigoTipo());
            if (regla == null) {
                return "No se pudo aplicar la sanción: no existe una regla de sanción para el tipo de incidencia.";
            }

            Sancion s = new Sancion();
            s.setCodigoSancion(sancionDAO.obtenerSiguienteCodigo());

            s.setCodigoIncidencia(codigoIncidencia);
            s.setFechaAplicacion(fechaAplicacion);
            s.setResponsable(responsable);
            s.setPosicionesRetroceso(regla.getPosicionesRetroceso());
            s.setEstado("ACTIVA");

            sancionDAO.insertar(s);
            return "Sanción aplicada correctamente.";

        } catch (SQLException e) {
            return "No se pudo aplicar la sanción: los datos ingresados no son válidos.";
        }
    }

    public String anularSancion(String codigoSancion) {

        try {
            Sancion s = sancionDAO.buscarPorCodigo(codigoSancion);
            if (s == null) {
                return "No se pudo anular la sanción: la sanción no existe.";
            }

            if ("ANULADA".equals(s.getEstado())) {
                return "No se pudo anular la sanción: la sanción ya se encuentra anulada.";
            }

            sancionDAO.actualizarEstado(codigoSancion, "ANULADA");
            return "Sanción anulada correctamente.";

        } catch (SQLException e) {
            return "No se pudo anular la sanción: la sanción no existe.";
        }
    }

 
}
