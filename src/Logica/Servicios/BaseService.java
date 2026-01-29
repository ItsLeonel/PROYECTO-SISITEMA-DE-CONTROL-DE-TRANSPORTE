package Logica.Servicios;

import Logica.DAO.BaseDAO;
import Logica.Entidades.Base;

import java.util.List;

public class BaseService {

    private final BaseDAO baseDAO = new BaseDAO();

    public String registrarBase(Base b) {

        if (b == null)
            return "No se pudo registrar la base operativa: el código de la base no cumple el formato requerido.";

       // String codigoStr = String.valueOf(b.getCodigoBase());

        int codigo = b.getCodigoBase();
        if (codigo < 0 || codigo > 99)
            return "No se pudo registrar la base operativa: el código de la base no cumple el formato requerido.";

        if (baseDAO.existeCodigo(b.getCodigoBase()))
            return "No se pudo registrar la base operativa: el código de la base ya se encuentra registrado.";

        if (b.getNombre() == null || !b.getNombre().trim().matches("^[A-Za-zñÑ\\- ]{1,50}$"))
            return "No se pudo registrar la base operativa: el nombre de la base no cumple el formato requerido.";

        String nombre = b.getNombre().trim();
        if (baseDAO.existeNombre(nombre))
            return "No se pudo registrar la base operativa: el nombre de la base ya se encuentra registrado.";

        if (b.getDireccion() == null || !b.getDireccion().trim().matches("^[A-Za-z0-9ñÑ\\.\\- ]{1,100}$"))
            return "No se pudo registrar la base operativa: la dirección no cumple el formato requerido.";

        String direccion = b.getDireccion().trim();

        // Estado por defecto
        b.setEstado("Activo");
        b.setNombre(nombre);
        b.setDireccion(direccion);

        boolean ok = baseDAO.insertar(b);

        return ok
                ? "Base operativa registrada correctamente."
                : "No se pudo registrar la base operativa: el código de la base ya se encuentra registrado.";
    }

    public Base buscarPorCodigo(int codigo) {
        if (codigo <= 0)
            return null;
        return baseDAO.buscarPorCodigo(codigo);
    }

    public String consultarBasePorNombre(String nombre) {

        // 1. Validar formato
        if (nombre == null || !nombre.trim().matches("^[A-Za-zñÑ\\- ]{1,50}$"))
            return "No se pudo consultar la base operativa: el nombre no cumple el formato requerido.";

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
        if (nuevoNombre == null || !nuevoNombre.trim().matches("^[A-Za-zñÑ\\- ]{1,50}$"))
            return "No se pudo actualizar el nombre de la base operativa: el nombre no cumple el formato requerido.";

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


}
