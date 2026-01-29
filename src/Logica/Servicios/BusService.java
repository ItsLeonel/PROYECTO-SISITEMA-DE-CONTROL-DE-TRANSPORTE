package Logica.Servicios;


import Logica.DAO.BusDAO;
import Logica.DAO.SocioDAO;
import Logica.Entidades.Bus;
import Logica.Entidades.SocioDisponible;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class BusService {

    private BusDAO busDAO;
    private SocioDAO socioDAO;

    public BusService() {
        this.busDAO = new BusDAO();
        this.socioDAO = new SocioDAO();
    }

    /**
     * ru1+2+3v1.1 - Registrar un bus
     */
    public ResultadoOperacion registrarBus(Bus bus) {
        try {
            if (!validarPlaca(bus.getPlaca())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la placa ingresada es inválida.");
            }

            if (busDAO.existePorPlaca(bus.getPlaca())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la placa ya se encuentra previamente registrada.");
            }

            if (!socioDAO.existeCodigoSocio(bus.getCodigoSocioFk())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el socio propietario no existe en el sistema.");
            }

            if (busDAO.socioTieneBus(bus.getCodigoSocioFk())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el socio propietario ya tiene un bus asignado.");
            }

            if (bus.getMarca() == null || bus.getMarca().trim().isEmpty() ||
                    bus.getMarca().length() > 15) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la marca debe tener entre 1 y 15 caracteres.");
            }

            if (bus.getModelo() == null || bus.getModelo().trim().isEmpty() ||
                    bus.getModelo().length() > 15) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el modelo debe tener entre 1 y 15 caracteres.");
            }

            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            if (bus.getAnioFabricacion() > anioActual ||
                    bus.getAnioFabricacion() < (anioActual - 10)) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el año de fabricación debe estar entre "
                                + (anioActual - 10) + " y " + anioActual + ".");
            }

            if (bus.getCapacidadPasajeros() <= 0) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la capacidad debe ser mayor que cero.");
            }

            if (bus.getBaseAsignada() == null || bus.getBaseAsignada().trim().isEmpty()) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: debe especificar una base operativa.");
            }

            busDAO.insertar(bus);

            return new ResultadoOperacion(true, "Bus registrado correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion consultarBusPorPlaca(String placa) {
        try {
            if (!validarPlaca(placa)) {
                return new ResultadoOperacion(false,
                        "La placa ingresada no cumple con el formato requerido.");
            }

            Bus bus = busDAO.obtenerPorPlaca(placa);

            if (bus == null) {
                return new ResultadoOperacion(false,
                        "No se encontró un bus registrado con la placa ingresada.");
            }

            return new ResultadoOperacion(true,
                    "Bus encontrado correctamente.", bus);

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion asignarBase(String placa, String nuevaBase) {
        try {
            if (!validarPlaca(placa)) {
                return new ResultadoOperacion(false,
                        "La placa ingresada no cumple con el formato requerido.");
            }

            if (nuevaBase == null || nuevaBase.trim().isEmpty()) {
                return new ResultadoOperacion(false,
                        "Debe especificar una base operativa válida.");
            }

            int filas = busDAO.actualizarBase(placa, nuevaBase);
if (filas == 0) {
    return new ResultadoOperacion(false,
            "No se pudo asignar la base: el bus no existe.");
}


            return new ResultadoOperacion(true,
                    "La base ha sido asignada correctamente al bus.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion listarTodosBuses() {
        try {
            List<Bus> buses = busDAO.listarTodos();

            if (buses.isEmpty()) {
                return new ResultadoOperacion(false,
                        "No hay buses registrados en el sistema.");
            }

            return new ResultadoOperacion(true,
                    "Listado de buses obtenido correctamente.", buses);

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion listarBusesFiltrado(String base, String estado) {
        try {
            List<Bus> buses = busDAO.listarFiltrado(base, estado);

            if (buses.isEmpty()) {
                return new ResultadoOperacion(false,
                        "No se encontraron buses con los criterios especificados.");
            }

            return new ResultadoOperacion(true,
                    "Listado de buses obtenido correctamente.", buses);

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    public ResultadoOperacion obtenerSociosDisponibles() {
        try {
            List<SocioDisponible> socios = busDAO.obtenerSociosDisponibles();

            if (socios.isEmpty()) {
                return new ResultadoOperacion(false,
                        "No hay socios disponibles sin bus asignado.");
            }

            return new ResultadoOperacion(true,
                    "Socios disponibles obtenidos correctamente.", socios);

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    private boolean validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) return false;
        placa = placa.trim().toUpperCase();
        return placa.matches("P[A-Z]{2}-?\\d{4}");
    }
    public ResultadoOperacion activarBus(String placa) {
    return cambiarEstadoBus(placa, "ACTIVO",
            "El estado del bus ha sido cambiado a ACTIVO.");
}

public ResultadoOperacion desactivarBus(String placa) {
    return cambiarEstadoBus(placa, "INACTIVO",
            "El estado del bus ha sido cambiado a INACTIVO.");
}

public ResultadoOperacion mantenimientoBus(String placa) {
    return cambiarEstadoBus(placa, "MANTENIMIENTO",
            "El estado del bus ha sido cambiado a MANTENIMIENTO");
}

private ResultadoOperacion cambiarEstadoBus(String placa, String nuevoEstado, String mensajeOk) {
    try {
        if (!validarPlaca(placa)) {
            return new ResultadoOperacion(false,
                    "La placa ingresada no es válida.");
        }

        Bus bus = busDAO.obtenerPorPlaca(placa);
        if (bus == null) {
            return new ResultadoOperacion(false,
                    "No existe un bus con la placa ingresada.");
        }

        if (nuevoEstado.equals(bus.getEstado())) {
            return new ResultadoOperacion(false,
                    "El bus ya se encuentra en estado " + nuevoEstado + ".");
        }

        busDAO.actualizarEstado(placa, nuevoEstado);

        return new ResultadoOperacion(true, mensajeOk);

    } catch (SQLException e) {
        return new ResultadoOperacion(false,
                "Error en la base de datos: " + e.getMessage());
    }
}

}
