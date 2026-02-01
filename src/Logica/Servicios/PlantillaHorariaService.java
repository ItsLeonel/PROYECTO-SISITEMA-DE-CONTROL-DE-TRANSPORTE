package Logica.Servicios;

import Logica.DAO.PlantillaHorariaDAO;
import Logica.DAO.RutaDAO;
import Logica.Entidades.PlantillaHoraria;
import Logica.Entidades.PlantillaHorariaFranja;
import Logica.Entidades.Ruta;

import java.util.List;

/**
 * Servicio para gestionar Plantillas Horarias con sus 6 franjas estándar
 */
public class PlantillaHorariaService {

    private final PlantillaHorariaDAO plantillaDAO = new PlantillaHorariaDAO();
    private final RutaDAO rutaDAO = new RutaDAO();

    // =====================================================
    // rtr18v2.0 – Registrar plantilla horaria
    // =====================================================
    /**
     * Registra una nueva plantilla horaria con sus 6 franjas estándar
     * 
     * @param plantilla Datos básicos de la plantilla
     * @param tiemposFranjas Array de 6 enteros con los minutos para cada franja (índice 0-5 = franja 1-6)
     * @return Mensaje de éxito o error
     */
    public String registrar(PlantillaHoraria plantilla, int[] tiemposFranjas) {
        StringBuilder errores = new StringBuilder();

        // Validar datos básicos
        if (plantilla == null) {
            return "No se recibieron datos para la plantilla horaria.";
        }

        // 1. Validar código duplicado
        if (plantillaDAO.existeCodigo(plantilla.getCodigoPlantilla())) {
            errores.append("- El código de la plantilla ya se encuentra registrado.\n");
        }

        // 2. Validar nombre
        String nombre = plantilla.getNombre() == null ? "" : plantilla.getNombre().trim();
        if (nombre.isEmpty()) {
            errores.append("- El nombre de la plantilla no puede estar vacío.\n");
        } else if (nombre.length() > 50) {
            errores.append("- El nombre de la plantilla no puede exceder 50 caracteres.\n");
        }

        // 3. Validar ruta
        String codigoRuta = plantilla.getCodigoRuta() == null ? "" : plantilla.getCodigoRuta().trim();
        Ruta ruta = rutaDAO.buscarPorCodigo(codigoRuta);
        if (ruta == null) {
            errores.append("- El código de la ruta asociada no existe.\n");
        }

        // 4. Validar horas de operación
        String horaInicio = plantilla.getHoraInicioOperaciones();
        String horaFin = plantilla.getHoraFinOperaciones();

        if (!esHoraValida(horaInicio)) {
            errores.append("- La hora de inicio de operaciones debe tener el formato HH:MM (00:00 - 23:59).\n");
        }
        if (!esHoraValida(horaFin)) {
            errores.append("- La hora de finalización de operaciones debe tener el formato HH:MM (00:00 - 23:59).\n");
        }

        // Validar que inicio < fin
        if (esHoraValida(horaInicio) && esHoraValida(horaFin)) {
            if (!esHoraInicioMenorQueFin(horaInicio, horaFin)) {
                errores.append("- La hora de inicio debe ser menor que la hora de finalización.\n");
            }
        }

        // 5. Validar tiempos de franjas (deben ser exactamente 6 valores > 0)
        if (tiemposFranjas == null || tiemposFranjas.length != 6) {
            errores.append("- Debe configurar los 6 tiempos de franja obligatoriamente.\n");
        } else {
            for (int i = 0; i < 6; i++) {
                if (tiemposFranjas[i] <= 0) {
                    errores.append("- El intervalo de la Franja ").append(i + 1)
                           .append(" debe ser un valor entero positivo mayor a 0.\n");
                }
            }
        }

        // Si hay errores, retornar
        if (errores.length() > 0) {
            return "No se pudo registrar la plantilla horaria debido a los siguientes errores:\n" + errores.toString();
        }

        // Establecer valores normalizados
        plantilla.setNombre(nombre);
        plantilla.setCodigoRuta(codigoRuta);
        plantilla.setEstado("Activo"); // Estado por defecto

        // Insertar plantilla madre
        boolean okPlantilla = plantillaDAO.insertarPlantilla(plantilla);
        if (!okPlantilla) {
            return "No se pudo registrar la plantilla horaria. Error interno.";
        }

        // Insertar las 6 franjas hijas
        for (int i = 0; i < 6; i++) {
            PlantillaHorariaFranja franja = new PlantillaHorariaFranja();
            franja.setCodigoPlantilla(plantilla.getCodigoPlantilla());
            franja.setFranjaId(i + 1); // 1 a 6
            franja.setTiempoMinutos(tiemposFranjas[i]);

            boolean okFranja = plantillaDAO.insertarFranja(franja);
            if (!okFranja) {
                return "Error al registrar la Franja " + (i + 1) + ". Plantilla registrada parcialmente.";
            }
        }

        return "Plantilla horaria registrada correctamente con sus 6 franjas estándar.";
    }

    // =====================================================
    // rtr19v1.1 – Actualizar intervalos por franja
    // =====================================================
    /**
     * Actualiza el tiempo (minutos) de una franja específica de una plantilla
     */
    public String actualizarIntervaloFranja(int codigoPlantilla, int franjaId, int nuevoTiempo) {
        // 1. Verificar que la plantilla existe
        PlantillaHoraria plantilla = plantillaDAO.buscarPorCodigo(codigoPlantilla);
        if (plantilla == null) {
            return "No se puede actualizar: la plantilla horaria indicada no existe.";
        }

        // 2. Validar rango de franja (1-6)
        if (franjaId < 1 || franjaId > 6) {
            return "No se puede actualizar: la franja debe estar entre 1 y 6.";
        }

        // 3. Validar tiempo positivo
        if (nuevoTiempo <= 0) {
            return "No se puede actualizar: el intervalo de salida ingresado es inválido.";
        }

        // 4. Actualizar
        boolean ok = plantillaDAO.actualizarTiempoFranja(codigoPlantilla, franjaId, nuevoTiempo);
        return ok
                ? "Intervalo de la franja actualizado correctamente."
                : "No se pudo actualizar el intervalo de la franja. Error interno.";
    }

    // =====================================================
    // rtr20v1.1 – Consultar plantilla por código
    // =====================================================
    /**
     * Consulta una plantilla completa (datos básicos + sus 6 franjas)
     */
    public ResultadoPlantilla consultarPorCodigo(int codigo) {
        PlantillaHoraria plantilla = plantillaDAO.buscarPorCodigo(codigo);
        
        if (plantilla == null) {
            return new ResultadoPlantilla(
                "No se puede consultar: la plantilla horaria indicada no existe.",
                null,
                List.of()
            );
        }

        // Obtener las franjas
        List<PlantillaHorariaFranja> franjas = plantillaDAO.obtenerFranjasPorPlantilla(codigo);

        return new ResultadoPlantilla(
            "Consulta de plantilla horaria realizada correctamente.",
            plantilla,
            franjas
        );
    }

    // =====================================================
    // rtr21.1v1.1 – Activar plantilla
    // =====================================================
    public String activar(int codigo) {
        PlantillaHoraria plantilla = plantillaDAO.buscarPorCodigo(codigo);
        
        if (plantilla == null) {
            return "No se puede activar: la plantilla horaria indicada no existe.";
        }

        if ("Activo".equalsIgnoreCase(plantilla.getEstado())) {
            return "La plantilla horaria ya se encuentra activa.";
        }

        boolean ok = plantillaDAO.cambiarEstado(codigo, "Activo");
        return ok
                ? "Plantilla horaria activada correctamente."
                : "No se pudo activar la plantilla horaria. Error interno.";
    }

    // =====================================================
    // rtr21.2v1.1 – Inactivar plantilla
    // =====================================================
    public String inactivar(int codigo) {
        PlantillaHoraria plantilla = plantillaDAO.buscarPorCodigo(codigo);
        
        if (plantilla == null) {
            return "No se puede inactivar: la plantilla horaria indicada no existe.";
        }

        if ("Inactivo".equalsIgnoreCase(plantilla.getEstado())) {
            return "La plantilla horaria ya se encuentra inactiva.";
        }

        boolean ok = plantillaDAO.cambiarEstado(codigo, "Inactivo");
        return ok
                ? "Plantilla horaria inactivada correctamente."
                : "No se pudo inactivar la plantilla horaria. Error interno.";
    }

    // =====================================================
    // rtr24.1v1.1 – Consultar plantillas activas
    // =====================================================
    public ResultadoListaPlantillas consultarActivas() {
        List<PlantillaHoraria> lista = plantillaDAO.listarActivas();

        if (lista.isEmpty()) {
            return new ResultadoListaPlantillas(
                "No se pudo generar el listado de plantillas activas: no existen plantillas activas registradas.",
                List.of()
            );
        }

        return new ResultadoListaPlantillas(
            "Listado de plantillas activas generado correctamente.",
            lista
        );
    }

    // =====================================================
    // rtr24.2v1.1 – Consultar plantillas inactivas
    // =====================================================
    public ResultadoListaPlantillas consultarInactivas() {
        List<PlantillaHoraria> lista = plantillaDAO.listarInactivas();

        if (lista.isEmpty()) {
            return new ResultadoListaPlantillas(
                "No se pudo generar el listado de plantillas inactivas: no existen plantillas inactivas registradas.",
                List.of()
            );
        }

        return new ResultadoListaPlantillas(
            "Listado de plantillas inactivas generado correctamente.",
            lista
        );
    }

    // =====================================================
    // rtr22v1.0 – Listar todas las plantillas
    // =====================================================
    public List<PlantillaHoraria> listarTodas() {
        return plantillaDAO.listarTodas();
    }

    // =====================================================
    // Método auxiliar para búsqueda
    // =====================================================
    public PlantillaHoraria buscarPorCodigo(int codigo) {
        return plantillaDAO.buscarPorCodigo(codigo);
    }

    public List<PlantillaHorariaFranja> obtenerFranjas(int codigoPlantilla) {
        return plantillaDAO.obtenerFranjasPorPlantilla(codigoPlantilla);
    }

    // =====================================================
    // VALIDACIONES INTERNAS
    // =====================================================
    
    private boolean esHoraValida(String hora) {
        if (hora == null || !hora.matches("^\\d{2}:\\d{2}$")) {
            return false;
        }
        int hh = Integer.parseInt(hora.substring(0, 2));
        int mm = Integer.parseInt(hora.substring(3, 5));
        return hh >= 0 && hh <= 23 && mm >= 0 && mm <= 59;
    }

    private boolean esHoraInicioMenorQueFin(String inicio, String fin) {
        int hhInicio = Integer.parseInt(inicio.substring(0, 2));
        int mmInicio = Integer.parseInt(inicio.substring(3, 5));
        int hhFin = Integer.parseInt(fin.substring(0, 2));
        int mmFin = Integer.parseInt(fin.substring(3, 5));

        int minutosInicio = hhInicio * 60 + mmInicio;
        int minutosFin = hhFin * 60 + mmFin;

        return minutosInicio < minutosFin;
    }

    // =====================================================
    // CLASES DE RESULTADO
    // =====================================================

    /**
     * Resultado para consultar una plantilla individual con sus franjas
     */
    public static class ResultadoPlantilla {
        private final String mensaje;
        private final PlantillaHoraria plantilla;
        private final List<PlantillaHorariaFranja> franjas;

        public ResultadoPlantilla(String mensaje, PlantillaHoraria plantilla, List<PlantillaHorariaFranja> franjas) {
            this.mensaje = mensaje;
            this.plantilla = plantilla;
            this.franjas = franjas;
        }

        public String getMensaje() { return mensaje; }
        public PlantillaHoraria getPlantilla() { return plantilla; }
        public List<PlantillaHorariaFranja> getFranjas() { return franjas; }
    }

    /**
     * Resultado para listar múltiples plantillas
     */
    public static class ResultadoListaPlantillas {
        private final String mensaje;
        private final List<PlantillaHoraria> plantillas;

        public ResultadoListaPlantillas(String mensaje, List<PlantillaHoraria> plantillas) {
            this.mensaje = mensaje;
            this.plantillas = plantillas;
        }

        public String getMensaje() { return mensaje; }
        public List<PlantillaHoraria> getPlantillas() { return plantillas; }
    }
}
