package Logica.Servicios;

import Logica.DAO.RutaDAO;
import Logica.Entidades.Ruta;
import java.util.List;

/**
 * Servicio actualizado con validaciones RN-TR para Rutas
 * - RN-TR-09: Unicidad código ruta
 * - RN-TR-10: Unicidad nombre ruta
 * - RN-TR-11: Bases distintas (Base A ≠ Base B)
 * - RN-TR-12: Plantilla activa
 * - RN-TR-13: Estado solo Activo/Inactivo
 */
public class RutaService {

    private final RutaDAO dao = new RutaDAO();

    // =====================================================
    // rtr1+2+3 - REGISTRAR RUTA
    // =====================================================

    public String registrarRuta(Ruta r) {
        StringBuilder errores = new StringBuilder();

        // RN-TR-09: Validar código (2 dígitos 01-99)
        if (!r.getCodigoRuta().matches("\\d{2}")) {
            errores.append("- El código de la ruta debe ser una cadena numérica de dos (2) dígitos (01-99).\n");
        } else if (dao.existeCodigo(r.getCodigoRuta())) {
            errores.append("- El código de la ruta ya se encuentra registrado.\n");
        }

        // RN-TR-10: Validar nombre (formato y unicidad)
        String nombre = r.getNombre() == null ? "" : r.getNombre().trim();
        if (!nombre.matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,50}$")) {
            errores.append(
                    "- El nombre de la ruta debe tener entre 1 y 50 caracteres (letras, espacios, guiones, ñ, tildes).\n");
        } else if (dao.existeNombre(nombre)) {
            errores.append("- El nombre de la ruta ya se encuentra registrado.\n");
        }

        // Origen (formato)
        if (r.getOrigen() == null || !r.getOrigen().matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,100}$")) {
            errores.append("- El origen debe tener entre 1 y 100 caracteres (letras, espacios, guiones).\n");
        }

        // Destino (formato)
        if (r.getDestino() == null || !r.getDestino().matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,100}$")) {
            errores.append("- El destino debe tener entre 1 y 100 caracteres (letras, espacios, guiones).\n");
        }

        // Validar Base A existe
        if (!dao.existeBase(r.getCodigoBaseA())) {
            errores.append("- No se puede registrar: la Base A indicada no existe.\n");
        }

        // Validar Base B existe
        if (!dao.existeBase(r.getCodigoBaseB())) {
            errores.append("- No se puede registrar: la Base B indicada no existe.\n");
        }

        // RN-TR-11: Validar Base A ≠ Base B
        if (r.getCodigoBaseA() == r.getCodigoBaseB()) {
            errores.append("- No se puede registrar: la Base A y la Base B deben ser diferentes.\n");
        }

        // Validar plantilla existe (Solo si se seleccionó una, código > 0)
        if (r.getCodigoIntervalo() > 0) {
            if (!dao.existePlantilla(r.getCodigoIntervalo())) {
                errores.append("- No se puede registrar: la plantilla horaria indicada no existe.\n");
            } else {
                // RN-TR-12: Validar plantilla está Activa
                if (!dao.esPlantillaActiva(r.getCodigoIntervalo())) {
                    errores.append(
                            "- No se puede registrar la ruta: la plantilla horaria debe estar en estado Activo.\n");
                }
            }
        }
        // Si es <= 0, se permite NULL (romper dependencia circular)

        // Validar duración estimada > 0
        if (r.getDuracionEstimadaMinutos() <= 0) {
            errores.append("- La duración estimada debe ser un valor entero positivo mayor a 0 (en minutos).\n");
        }

        // RN-TR-13: Validar estado
        if (r.getEstado() == null ||
                (!r.getEstado().equals("Activo") && !r.getEstado().equals("Inactivo"))) {
            errores.append("- El estado debe ser Activo o Inactivo.\n");
        }

        if (errores.length() > 0) {
            return "No se pudo registrar la ruta debido a los siguientes errores:\n" + errores.toString();
        }

        // Normalizar nombre
        r.setNombre(nombre);

        // Insertar
        boolean exito = dao.insertar(r);
        return exito
                ? "Ruta registrada correctamente."
                : "No se pudo registrar la ruta. Error interno.";
    }

    // =====================================================
    // rtr6 - ACTUALIZAR BASE A ASOCIADA
    // =====================================================

    public String actualizarBaseA(String codigoRuta, int nuevaBaseA) {
        // Verificar que la ruta existe
        Ruta r = dao.buscarPorCodigo(codigoRuta);
        if (r == null) {
            return "No se pudo actualizar: la ruta indicada no existe.";
        }

        // Verificar que la nueva base existe
        if (!dao.existeBase(nuevaBaseA)) {
            return "No se pudo actualizar: la Base A indicada no existe.";
        }

        // RN-TR-11: Verificar que nueva Base A ≠ Base B actual
        if (nuevaBaseA == r.getCodigoBaseB()) {
            return "No se pudo actualizar: la Base A debe ser diferente de la Base B.";
        }

        boolean exito = dao.actualizarBaseA(codigoRuta, nuevaBaseA);
        return exito
                ? "Base A de la ruta actualizada correctamente."
                : "No se pudo actualizar la Base A. Error interno.";
    }

    // =====================================================
    // rtr7 - ACTUALIZAR BASE B ASOCIADA
    // =====================================================

    public String actualizarBaseB(String codigoRuta, int nuevaBaseB) {
        // Verificar que la ruta existe
        Ruta r = dao.buscarPorCodigo(codigoRuta);
        if (r == null) {
            return "No se pudo actualizar: la ruta indicada no existe.";
        }

        // Verificar que la nueva base existe
        if (!dao.existeBase(nuevaBaseB)) {
            return "No se pudo actualizar: la Base B indicada no existe.";
        }

        // RN-TR-11: Verificar que nueva Base B ≠ Base A actual
        if (nuevaBaseB == r.getCodigoBaseA()) {
            return "No se pudo actualizar: la Base B debe ser diferente de la Base A.";
        }

        boolean exito = dao.actualizarBaseB(codigoRuta, nuevaBaseB);
        return exito
                ? "Base B de la ruta actualizada correctamente."
                : "No se pudo actualizar la Base B. Error interno.";
    }

    // =====================================================
    // ACTUALIZAR NOMBRE (mantenido del código original)
    // =====================================================

    public String actualizarNombre(String codigo, String nuevoNombre) {
        Ruta r = dao.buscarPorCodigo(codigo);
        if (r == null) {
            return "No se pudo actualizar el nombre de la ruta: el código de la ruta no existe.";
        }

        String nombre = nuevoNombre == null ? "" : nuevoNombre.trim();
        if (!nombre.matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,50}$")) {
            return "No se pudo actualizar el nombre de la ruta: debe ser una cadena de hasta 50 caracteres, compuesta por letras del alfabeto español, espacios y el carácter guion (-).";
        }

        // RN-TR-10: Verificar unicidad del nombre (excepto si es el mismo nombre
        // actual)
        if (!r.getNombre().equals(nombre) && dao.existeNombre(nombre)) {
            return "No se pudo actualizar el nombre de la ruta: el nombre ya se encuentra registrado en otra ruta.";
        }

        boolean exito = dao.actualizarNombre(codigo, nombre);
        return exito
                ? "Nombre de la ruta actualizado correctamente."
                : "No se pudo actualizar el nombre de la ruta. Error interno.";
    }

    // =====================================================
    // rtr8.1 / rtr8.2 - ACTIVAR / INACTIVAR RUTA
    // =====================================================

    public String cambiarEstado(String codigo, String nuevoEstado) {
        Ruta r = dao.buscarPorCodigo(codigo);
        if (r == null) {
            return "No se pudo actualizar el estado de la ruta: el código de la ruta no existe.";
        }

        // RN-TR-13: Validar estado
        if (!nuevoEstado.equals("Activo") && !nuevoEstado.equals("Inactivo")) {
            return "No se pudo actualizar el estado de la ruta: el estado ingresado no es válido.";
        }

        // Verificar si ya tiene ese estado
        if (r.getEstado().equals(nuevoEstado)) {
            if ("Activo".equals(nuevoEstado)) {
                return "La ruta ya se encuentra activa.";
            } else {
                return "La ruta ya se encuentra inactiva.";
            }
        }

        boolean exito = dao.actualizarEstado(codigo, nuevoEstado);
        return exito
                ? "Estado de la ruta actualizado correctamente."
                : "No se pudo actualizar el estado de la ruta. Error interno.";
    }

    // =====================================================
    // rtr10 - CONSULTAR RUTA POR CÓDIGO
    // =====================================================

    public Ruta buscarRutaPorCodigo(String codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public Ruta buscarRutaPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    // =====================================================
    // rtr11.1 / rtr11.2 / rtr11.3 - LISTADOS
    // =====================================================

    public List<Ruta> listarActivas() {
        return dao.listarActivas();
    }

    public List<Ruta> listarInactivas() {
        return dao.listarInactivas();
    }

    public List<Ruta> listarTodas() {
        return dao.listarTodas();
    }

    // =====================================================
    // UTILIDADES
    // =====================================================

    /**
     * Obtiene el nombre de una base por su código (para mostrar en UI)
     */
    public String obtenerNombreBase(int codigoBase) {
        return dao.obtenerNombreBase(codigoBase);
    }
}
