package Logica.Servicios;

import Logica.DAO.BaseDAO;
import Logica.Entidades.Base;

import java.util.List;
import java.util.regex.Pattern;

public class BaseService {

    private final BaseDAO baseDAO = new BaseDAO();

    public String registrarBase(Base b) {
        StringBuilder errores = new StringBuilder();

        if (b == null) {
            return "No se pudieron obtener los datos de la base.";
        }

        // Validación Código
        int codigo = b.getCodigoBase();
        if (codigo < 0 || codigo > 99) {
            errores.append("- El código debe ser un número entre 00 y 99.\n");
        } else if (baseDAO.existeCodigo(b.getCodigoBase())) {
            errores.append("- El código de la base ya se encuentra registrado.\n");
        }

        // Validación Nombre
        String nombre = (b.getNombre() != null) ? b.getNombre().trim() : "";
        if (!nombre.matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,50}$")) {
            errores.append("- El nombre debe tener hasta 50 caracteres (letras, espacios, guiones).\n");
        } else if (baseDAO.existeNombre(nombre)) {
            errores.append("- El nombre de la base ya se encuentra registrado.\n");
        }

        // Validación Dirección
        String direccion = (b.getDireccion() != null) ? b.getDireccion().trim() : "";
        if (!validarDireccion(direccion)) {
            errores.append(
                    "- La dirección debe tener el formato 'Calle Principal, Calle Secundaria' (ambas alfanuméricas de 1 a 15 caracteres).\n");
        }

        if (errores.length() > 0) {
            return "No se pudo registrar la base operativa debido a los siguientes errores:\n" + errores.toString();
        }

        // Estado por defecto y setters finales
        b.setEstado("Activo");
        b.setNombre(nombre);
        b.setDireccion(direccion);

        boolean ok = baseDAO.insertar(b);

        return ok
                ? "Base operativa registrada correctamente."
                : "No se pudo registrar la base operativa: Error interno en base de datos.";
    }

    /**
     * Anexo B - Validar dirección (estructura definida)
     * Calle principal + Número + Calle secundaria
     * Formato: tipo + dirección alfanumérica (hasta 15 caracteres) + carácter
     * especial +
     * longitud entre 12 y 15 + letras A-Z mayúsculas +
     * letras del 0-9 + carácter especial "-"
     */
    private boolean validarDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            return false;
        }

        // Debe contener dos partes separadas por coma
        String[] partes = direccion.split(",");

        if (partes.length != 2) {
            return false; // no hay calle principal y secundaria
        }

        String callePrincipal = partes[0].trim();
        String calleSecundaria = partes[1].trim();

        // Patrón permitido: letras, números, espacios y ñ (y tildes integradas)
        Pattern patronCalle = Pattern.compile("^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ ]{1,15}$");

        return patronCalle.matcher(callePrincipal).matches()
                && patronCalle.matcher(calleSecundaria).matches();
    }

    public Base buscarPorCodigo(int codigo) {
        if (codigo <= 0)
            return null;
        return baseDAO.buscarPorCodigo(codigo);
    }

    public String consultarBasePorNombre(String nombre) {

        // 1. Validar formato
        if (nombre == null || !nombre.trim().matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,50}$"))
            return "No se pudo consultar el nombre de la base operativa: debe ser una cadena de hasta 50 caracteres, compuesta por letras del alfabeto español, espacios y el carácter guion (-).";

        Base base = baseDAO.buscarPorNombre(nombre.trim());

        // 2. Verificar existencia
        if (base == null)
            return "No se pudo consultar la base operativa: el nombre no existe.";

        // 3. Consulta correcta
        return "Consulta de base operativa realizada correctamente.";
    }

    public List<Base> listarActivas() {
        return baseDAO.listarActivas();
    }

    public String listarBasesActivas() {

        List<Base> lista = baseDAO.listarActivas();

        if (lista == null || lista.isEmpty())
            return "No se pudo generar el listado de bases operativas activas: no existen bases operativas activas registradas.";

        return "Listado de bases operativas activas generado correctamente.";
    }

    public String actualizarNombre(int codigo, String nuevoNombre) {

        // 1. Validar formato del código (dos dígitos)
        if (codigo < 0 || codigo > 99)

            return "No se pudo actualizar el nombre de la base operativa: el código de la base no cumple el formato requerido.";

        // 2. Validar formato del nuevo nombre
        if (nuevoNombre == null || !nuevoNombre.trim().matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ\\- ]{1,50}$"))
            return "No se pudo actualizar el nombre de la base operativa: debe ser una cadena de hasta 50 caracteres, compuesta por letras del alfabeto español, espacios y el carácter guion (-).";

        // 3. Verificar existencia de la base
        Base actual = baseDAO.buscarPorCodigo(codigo);
        if (actual == null)
            return "No se pudo actualizar el nombre de la base operativa: el código de la base no existe.";

        String nombre = nuevoNombre.trim();

        // 4. Verificar duplicidad (en otra base)
        if (!actual.getNombre().equalsIgnoreCase(nombre)
                && baseDAO.existeNombre(nombre))
            return "No se pudo actualizar el nombre de la base operativa: el nombre ya se encuentra registrado en el sistema.";

        // 5. Actualizar
        boolean ok = baseDAO.actualizarNombre(codigo, nombre);

        return ok
                ? "Nombre de la base operativa actualizado correctamente."
                : "No se pudo actualizar el nombre de la base operativa: el código de la base no existe.";
    }

    public String cambiarEstado(int codigo, String estado) {

        // 1. Validar formato del código (dos dígitos)
        if (codigo < 0 || codigo > 99)
            return "No se pudo actualizar el estado de la base operativa: el código de la base no cumple el formato requerido.";

        // 2. Validar estado permitido
        if (estado == null ||
                !(estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Inactivo")))
            return "No se pudo actualizar el estado de la base operativa: el estado ingresado no es válido.";

        // 3. Verificar existencia
        Base actual = baseDAO.buscarPorCodigo(codigo);
        if (actual == null)
            return "No se pudo actualizar el estado de la base operativa: el código de la base no existe.";

        // 4. Actualizar
        boolean ok = baseDAO.cambiarEstado(codigo, estado);

        return ok
                ? "Estado de la base operativa actualizado correctamente."
                : "No se pudo actualizar el estado de la base operativa: el código de la base no existe.";
    }

    // SOLO devuelve el objeto, sin mensajes
    public Base obtenerBasePorNombre(String nombre) {
        return baseDAO.buscarPorNombre(nombre);
    }

    public List<String> listarNombres() {
        return baseDAO.listarNombres();
    }

    public List<Base> listarTodas() {
        return baseDAO.listarTodas();
    }

    public List<Base> listarInactivas() {
        List<Base> todas = baseDAO.listarTodas();
        List<Base> inactivas = new java.util.ArrayList<>();
        if (todas != null) {
            for (Base b : todas) {
                if ("Inactivo".equalsIgnoreCase(b.getEstado())) {
                    inactivas.add(b);
                }
            }
        }
        return inactivas;
    }

}
