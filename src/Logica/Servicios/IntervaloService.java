package Logica.Servicios;

import Logica.DAO.IntervaloDAO;
import Logica.DAO.RutaDAO;
import Logica.Entidades.Intervalo;
import Logica.Entidades.Ruta;

import java.util.List;

public class IntervaloService {

    private final IntervaloDAO intervaloDAO = new IntervaloDAO();
    private final RutaDAO rutaDAO = new RutaDAO();

    // =====================================================
    // rtr18 v1.1 – Registrar intervalo de salida
    // =====================================================
    public String registrar(Intervalo i) {

        if (i == null)
            return "No se pudo registrar el intervalo de salida: el tiempo del intervalo no cumple el formato requerido.";

        // Código duplicado
        if (intervaloDAO.existeCodigo(i.getCodigoIntervalo()))
            return "No se pudo registrar el intervalo de salida: el código del intervalo ya se encuentra registrado.";

        // Validar ruta
        String codigoRuta = i.getCodigoRuta() == null ? "" : i.getCodigoRuta().trim();
        Ruta ruta = rutaDAO.buscarPorCodigo(codigoRuta);
        if (ruta == null)
            return "No se pudo registrar el intervalo de salida: el código de la ruta no existe.";

        // Validar tiempo
        if (i.getTiempoMinutos() <= 0)
            return "No se pudo registrar el intervalo de salida: el tiempo del intervalo no cumple el formato requerido.";

        // Validar franja
        String franja = i.getFranjaHoraria() == null ? "" : i.getFranjaHoraria().trim();
        if (!esFranjaValida(franja))
            return "No se pudo registrar el intervalo de salida: la franja horaria no cumple el formato requerido.";

        // Estado por defecto
        i.setEstado("Activo");
        i.setCodigoRuta(codigoRuta);
        i.setFranjaHoraria(franja);

        boolean ok = intervaloDAO.insertar(i);
        return ok
                ? "Intervalo de salida registrado correctamente."
                : "No se pudo registrar el intervalo de salida: el tiempo del intervalo no cumple el formato requerido.";
    }

    // =====================================================
    // rtr19 v1.0 – Actualizar tiempo del intervalo
    // =====================================================

public String actualizarTiempo(int codigo, int nuevoTiempo) {

    // 1) Verificar existencia (igual que franja/estado)
    Intervalo actual = intervaloDAO.buscarPorCodigo(codigo);
    if (actual == null)
        return "No se pudo actualizar el tiempo del intervalo: el código del intervalo no existe.";

    // 2) Validar tiempo
    if (nuevoTiempo <= 0)
        return "No se pudo actualizar el tiempo del intervalo: el tiempo del intervalo no cumple el formato requerido.";

    // 3) Actualizar
    boolean ok = intervaloDAO.actualizarTiempo(codigo, nuevoTiempo);

    return ok
            ? "Tiempo del intervalo actualizado correctamente."
            : "No se pudo actualizar el tiempo del intervalo: el tiempo del intervalo no cumple el formato requerido.";
}


    // =====================================================
    // rtr20 v1.0 – Actualizar franja horaria
    // =====================================================
    public String actualizarFranja(int codigo, String nuevaFranja) {

        Intervalo actual = intervaloDAO.buscarPorCodigo(codigo);
        if (actual == null)
            return "No se pudo actualizar la franja horaria del intervalo: el código del intervalo no existe.";

        String franja = nuevaFranja == null ? "" : nuevaFranja.trim();
        if (!esFranjaValida(franja))
            return "No se pudo actualizar la franja horaria del intervalo: la franja horaria no cumple el formato requerido.";

        boolean ok = intervaloDAO.actualizarFranja(codigo, franja);
        return ok
                ? "Franja horaria del intervalo actualizada correctamente."
                : "No se pudo actualizar la franja horaria del intervalo: la franja horaria no cumple el formato requerido.";
    }

    // =====================================================
    // rtr21 v1.0 – Cambiar estado del intervalo
    // =====================================================
    public String cambiarEstado(int codigo, String estado) {

        Intervalo actual = intervaloDAO.buscarPorCodigo(codigo);
        if (actual == null)
            return "No se pudo actualizar el estado del intervalo: el código del intervalo no existe.";

        if (!(estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Inactivo")))
            return "No se pudo actualizar el estado del intervalo: el estado ingresado no es válido.";

        boolean ok = intervaloDAO.cambiarEstado(codigo, capitalizar(estado));
        return ok
                ? "Estado del intervalo actualizado correctamente."
                : "No se pudo actualizar el estado del intervalo: el estado ingresado no es válido.";
    }

    // =====================================================
    // rtr22 v1.0 – Consultar intervalos por ruta
    // =====================================================
   public ResultadoIntervalo consultarPorRuta(String codigoRuta) {

    if (codigoRuta == null || !codigoRuta.matches("^\\d{2}$")) {
        return new ResultadoIntervalo(
            "No se pudo consultar los intervalos: el código de la ruta no existe.",
            List.of()
        );
    }

    Ruta ruta = rutaDAO.buscarPorCodigo(codigoRuta);
    if (ruta == null) {
        return new ResultadoIntervalo(
            "No se pudo consultar los intervalos: el código de la ruta no existe.",
            List.of()
        );
    }

    List<Intervalo> lista = intervaloDAO.listarPorRuta(codigoRuta);
    if (lista.isEmpty()) {
        return new ResultadoIntervalo(
            "No se pudo consultar los intervalos: no existen intervalos asociados a la ruta ingresada.",
            List.of()
        );
    }

    return new ResultadoIntervalo(
        "Consulta de intervalos realizada correctamente.",
        lista
    );
}

    // =====================================================
    // rtr23 v1.0 – Consultar intervalos por franja horaria
    // =====================================================
   public ResultadoIntervalo consultarPorFranja(String franja) {

    if (franja == null || !esFranjaValida(franja.trim())) {
        return new ResultadoIntervalo(
            "No se pudo consultar los intervalos: la franja horaria no cumple el formato requerido.",
            List.of()
        );
    }

    List<Intervalo> lista = intervaloDAO.listarPorFranja(franja.trim());
    if (lista.isEmpty()) {
        return new ResultadoIntervalo(
            "No se pudo consultar los intervalos: no existen intervalos con la franja horaria ingresada.",
            List.of()
        );
    }

    return new ResultadoIntervalo(
        "Consulta de intervalos realizada correctamente.",
        lista
    );
}

    // =====================================================
    // rtr24 v1.0 – Listar intervalos activos
    // =====================================================
    public ResultadoIntervalo listarActivos() {

    List<Intervalo> lista = intervaloDAO.listarActivos();

    if (lista.isEmpty()) {
        return new ResultadoIntervalo(
            "No se pudo generar el listado de intervalos activos: no existen intervalos activos registrados.",
            List.of()
        );
    }

    return new ResultadoIntervalo(
        "Listado de intervalos activos generado correctamente.",
        lista
    );
}


    // =====================================================
    // Validaciones internas
    // =====================================================
    private boolean esFranjaValida(String franja) {
        if (!franja.matches("^\\d{2}:\\d{2}$")) return false;
        int hh = Integer.parseInt(franja.substring(0, 2));
        int mm = Integer.parseInt(franja.substring(3, 5));
        return hh >= 0 && hh <= 23 && mm >= 0 && mm <= 59;
    }

    private String capitalizar(String s) {
        s = s.toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    public List<Intervalo> listarTodos() {
    return intervaloDAO.listarTodos();
}
// Agregar a IntervaloService.java

public Intervalo buscarPorCodigo(int codigo) {
    return intervaloDAO.buscarPorCodigo(codigo);
}

}

