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
     * ✅ MENSAJES EXACTOS SEGÚN REQUISITOS
     */
    public ResultadoOperacion registrarBus(Bus bus) {
        try {
            // ✅ CASO 3,4,5,6,7,8,9 - Validar formato de placa
            if (!validarPlaca(bus.getPlaca())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la placa ingresada es inválida.");
            }

            // ✅ CASO 2 - Placa duplicada
            if (busDAO.existePorPlaca(bus.getPlaca())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la placa ya se encuentra previamente registrada.");
            }

            // Validar que el socio existe
            if (!socioDAO.existeCodigoSocio(bus.getCodigoSocioFk())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el socio propietario no existe en el sistema.");
            }

            // Validar que el socio no tenga bus
            if (busDAO.socioTieneBus(bus.getCodigoSocioFk())) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el socio propietario ya tiene un bus asignado.");
            }

            // Validar marca (máx 15 caracteres alfabéticos)
            if (bus.getMarca() == null || bus.getMarca().trim().isEmpty() ||
                    bus.getMarca().length() > 15) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la marca debe tener entre 1 y 15 caracteres.");
            }

            // Validar modelo (máx 15 caracteres alfanuméricos)
            if (bus.getModelo() == null || bus.getModelo().trim().isEmpty() ||
                    bus.getModelo().length() > 15) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el modelo debe tener entre 1 y 15 caracteres.");
            }

            // ✅ CASO 10,11 - Validar año de fabricación
            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            if (bus.getAnioFabricacion() > anioActual ||
                    bus.getAnioFabricacion() < (anioActual - 10)) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: el año de fabricación debe estar entre "
                                + (anioActual - 10) + " y " + anioActual + ".");
            }

            // ✅ CASO 12,13 - Validar capacidad
            if (bus.getCapacidadPasajeros() <= 0) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: la capacidad debe ser mayor que cero.");
            }

            // Validar base asignada
            if (bus.getBaseAsignada() == null || bus.getBaseAsignada().trim().isEmpty()) {
                return new ResultadoOperacion(false,
                        "No se puede registrar el bus: debe especificar una base operativa.");
            }

            busDAO.insertar(bus);

            // ✅ CASO 1 - Registro exitoso
            return new ResultadoOperacion(true, "Bus registrado correctamente.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * ru4+5v1.1 - Consultar bus por placa
     */
    public ResultadoOperacion consultarBusPorPlaca(String placa) {
        try {
            if (!validarPlaca(placa)) {
                return new ResultadoOperacion(false,
                        "La placa ingresada no cumple con el formato requerido.");
            }

            Bus bus = busDAO.obtenerPorPlaca(placa);

            // ✅ CASO 16 - Bus no existe
            if (bus == null) {
                return new ResultadoOperacion(false,
                        "No se encontró un bus registrado con la placa ingresada.");
            }

            // ✅ CASO 15 - Bus encontrado
            return new ResultadoOperacion(true,
                    "Bus encontrado correctamente.", bus);

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * ru12v1.1 - Asignar base operativa
     */
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

            // ✅ CASO 17 - Asignación exitosa
            return new ResultadoOperacion(true,
                    "La base ha sido asignada correctamente al bus.");

        } catch (SQLException e) {
            return new ResultadoOperacion(false,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * ru21v1.1 - Listar todos los buses
     */
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

    /**
     * ru21,22,23,24v1.1 - Listar buses con filtros
     */
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

    /**
     * Obtener socios disponibles (sin bus asignado)
     */
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

    /**
     * ✅ VALIDACIÓN ESTRICTA DE PLACA SEGÚN ANEXO D
     * 
     * Formato EXACTO: P + 2 letras MAYÚSCULAS + 4 dígitos
     * Regex: ^P[A-Z]{2}[0-9]{4}$
     * 
     * ✅ VÁLIDOS:
     * - PAB1234
     * - PBC0001
     * - PZZ9999
     * 
     * ❌ INVÁLIDOS:
     * - Pab1234 (minúsculas)
     * - PAB-1234 (guion)
     * - PAB 1234 (espacio)
     * - ABC1234 (no empieza con P)
     * - PAB123 (muy corta)
     * - PAB12345 (muy larga)
     * - PAB12A4 (letra en zona numérica)
     */
    private boolean validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }
        
        // ✅ NO convertir a mayúsculas - debe venir en mayúsculas
        String placaOriginal = placa.trim();
        
        // ✅ Regex EXACTO según Anexo D
        // - ^ = inicio de cadena
        // - P = letra P mayúscula obligatoria
        // - [A-Z]{2} = exactamente 2 letras mayúsculas
        // - [0-9]{4} = exactamente 4 dígitos
        // - $ = fin de cadena
        return placaOriginal.matches("^P[A-Z]{2}[0-9]{4}$");
    }

    /**
     * ru13v1.2 - Activar bus
     */
    public ResultadoOperacion activarBus(String placa) {
        // ✅ CASO 18 - Mensaje exacto
        return cambiarEstadoBus(placa, "ACTIVO",
                "El estado del bus ha sido cambiado a ACTIVO.");
    }

    /**
     * ru14v1.2 - Desactivar bus
     */
    public ResultadoOperacion desactivarBus(String placa) {
        // ✅ CASO 19 - Mensaje exacto
        return cambiarEstadoBus(placa, "INACTIVO",
                "El estado del bus ha sido cambiado a INACTIVO.");
    }

    /**
     * ru15v1.2 - Poner bus en mantenimiento
     */
    public ResultadoOperacion mantenimientoBus(String placa) {
        // ✅ CASO 20 - Mensaje exacto
        return cambiarEstadoBus(placa, "MANTENIMIENTO",
                "El estado del bus ha sido cambiado a MANTENIMIENTO.");
    }

    /**
     * Método auxiliar para cambiar estado de un bus
     */
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