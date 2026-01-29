package Logica.Servicios;

import Logica.DAO.RutaDAO;

//import Logica.DAO.RutaDAO;
import Logica.Entidades.Ruta;
import java.util.List;

public class RutaService {

    private final RutaDAO dao = new RutaDAO();
    private final RutaDAO rutaDAO = new RutaDAO();

    public String registrarRuta(Ruta r) {

        // Código (2 dígitos)
        if (!r.getCodigoRuta().matches("\\d{2}")) {
            return "No se pudo registrar la ruta: el código de la ruta no cumple el formato requerido.";
        }

        // Nombre (formato)
        if (!r.getNombre().matches("^[A-Za-zñÑ\\- ]{1,50}$")) {
            return "No se pudo registrar la ruta: el nombre de la ruta no cumple el formato requerido.";
        }

        // Origen (formato)
        if (!r.getOrigen().matches("^[A-Za-zñÑ\\- ]{1,20}$")) {
            return "No se pudo registrar la ruta: el origen no cumple el formato requerido.";
        }

        // Destino (formato)
        if (!r.getDestino().matches("^[A-Za-zñÑ\\- ]{1,20}$")) {
            return "No se pudo registrar la ruta: el destino no cumple el formato requerido.";
        }

        // Código duplicado
        if (dao.existeCodigo(r.getCodigoRuta())) {
            return "No se pudo registrar la ruta: el código de la ruta ya se encuentra registrado.";
        }

        // Nombre duplicado
        if (dao.existeNombre(r.getNombre())) {
            return "No se pudo registrar la ruta: el nombre de la ruta ya se encuentra registrado.";
        }
        dao.insertar(r);
        return "Ruta registrada correctamente.";

    }

    public Ruta buscarRutaPorCodigo(String codigo) {
        return dao.buscarPorCodigo(codigo);
    }

    public Ruta buscarRutaPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    public String cambiarEstado(String codigo, String estado) {

        if (!estado.equals("Activo") && !estado.equals("Inactivo")) {
            return "No se pudo actualizar el estado de la ruta: el estado ingresado no es válido.";
        }

        Ruta r = dao.buscarPorCodigo(codigo);
        if (r == null) {
            return "No se pudo actualizar el estado de la ruta: el código de la ruta no existe.";
        }

        if (dao.actualizarEstado(codigo, estado)) {
            return "Estado de la ruta actualizado correctamente.";
        } else {
            return "No se pudo actualizar el estado de la ruta: ocurrió un error interno.";
        }
    }

    public String actualizarNombre(String codigo, String nuevoNombre) {

        Ruta r = dao.buscarPorCodigo(codigo);
        if (r == null) {
            return "No se pudo actualizar el nombre de la ruta: el código de la ruta no existe.";
        }

        if (!nuevoNombre.matches("^[A-Za-zñÑ\\- ]{1,50}$")) {
            return "No se pudo actualizar el nombre de la ruta: el nombre no cumple el formato requerido.";
        }

        if (dao.existeNombre(nuevoNombre)) {
            return "No se pudo actualizar el nombre de la ruta: el nombre ya se encuentra registrado en otra ruta.";
        }

        dao.actualizarNombre(codigo, nuevoNombre);
        return "Nombre de la ruta actualizado correctamente.";
    }

    public String actualizarDestino(String codigo, String destino) {

        Ruta r = rutaDAO.buscarPorCodigo(codigo);
        if (r == null) {
            return "No se pudo actualizar el destino de la ruta: el código de la ruta no existe.";
        }

        if (!destino.matches("^[A-Za-zñÑ\\- ]{1,20}$")) {
            return "No se pudo actualizar el destino de la ruta: el destino no cumple el formato requerido.";
        }

        boolean actualizado = rutaDAO.actualizarDestino(codigo, destino);

        return actualizado
                ? "Destino de la ruta actualizado correctamente."
                : "Destino de la ruta actualizado correctamente.";
    }

    public String actualizarOrigen(String codigo, String origen) {

        Ruta r = rutaDAO.buscarPorCodigo(codigo);
        if (r == null) {
            return "No se pudo actualizar el origen de la ruta: el código de la ruta no existe.";
        }

        if (!origen.matches("^[A-Za-zñÑ\\- ]{1,20}$")) {
            return "No se pudo actualizar el origen de la ruta: el origen no cumple el formato requerido.";
        }

        boolean actualizado = rutaDAO.actualizarOrigen(codigo, origen);

        return actualizado
                ? "Origen de la ruta actualizado correctamente."
                : "Origen de la ruta actualizado correctamente.";
    }

    public List<Ruta> listarActivas() {
        return dao.listarActivas();
    }
    public List<Ruta> listarTodas() {
    return dao.listarTodas(); // lo creamos en el DAO
}

}
