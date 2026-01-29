package Logica.Servicios;

import Logica.DAO.IncidenciaDAO;
import Logica.Entidades.Incidencia;

import java.sql.SQLException;
import java.time.LocalDate;


public class IncidenciaServicio {

    private final IncidenciaDAO dao = new IncidenciaDAO();

    public String registrarIncidencia(String codigoBus,
                                      LocalDate fechaEvento,
                                      String codigoRuta,
                                      String codigoTipo) {

        try {

            if (codigoBus == null || codigoBus.isBlank()
        || codigoRuta == null || codigoRuta.isBlank()
        || codigoTipo == null || codigoTipo.isBlank()
        || fechaEvento == null) {
    return "No se pudo registrar la incidencia: los datos ingresados no son válidos.";
}

            if (!dao.busExiste(codigoBus)) {
                return "No se pudo registrar la incidencia: el bus no se encuentra registrado en el sistema.";
            }

            Incidencia i = new Incidencia();
            i.setCodigoIncidencia(dao.obtenerSiguienteCodigo());
            i.setCodigoBus(codigoBus);
            i.setCodigoRuta(codigoRuta);
            i.setCodigoTipo(codigoTipo);
            i.setFechaEvento(fechaEvento);
            i.setEstado("ACTIVA");

            dao.insertar(i);
            return "Incidencia registrada correctamente.";

        } catch (SQLException e) {
            return "No se pudo registrar la incidencia: los datos ingresados no son válidos.";
        }
    }

  //  private String generarCodigo() {
   //     return String.format("%03d", (int) (Math.random() * 900) + 100);
  //  
  
}
