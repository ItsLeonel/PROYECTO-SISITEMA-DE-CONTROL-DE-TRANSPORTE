package Logica.Servicios;

import Logica.DAO.BusDAO;
import Logica.Entidades.Bus;
import Logica.Entidades.Base;

import java.util.List;

public class BusService {

    private final BusDAO dao = new BusDAO();
    private final BaseService baseService = new BaseService();

    // =========================
    // RU1+2+3 – REGISTRAR BUS
    // =========================
    private boolean esBaseValida(String nombreBase) {
        if (nombreBase == null || nombreBase.isBlank()) {
            return false;
        }

        Base base = baseService.obtenerBasePorNombre(nombreBase.trim());
        return base != null;
    }

    public void registrar(Bus b) throws Exception {

        if (!b.getCodigo().matches("\\d{4}"))
            throw new Exception("No se pudo registrar la unidad: el código no cumple el formato requerido.");

        if (!validarPlaca(b.getPlaca()))
            throw new Exception("No se pudo registrar la unidad: la placa no cumple el formato requerido.");

        if (dao.obtenerPorCodigo(b.getCodigo()) != null)
            throw new Exception("No se pudo registrar la unidad: el código ya se encuentra registrado.");

        if (dao.obtenerPorPlaca(b.getPlaca()) != null)
            throw new Exception("No se pudo registrar la unidad: la placa ya se encuentra registrada.");

        if (!esBaseValida(b.getBase()))
            throw new Exception("No se pudo registrar la unidad: la base asignada no es válida.");

        dao.insertar(b);
    }

    // =========================
    // RU4+5 – CONSULTAR
    // =========================
    public Bus consultarPorCodigo(String codigo) throws Exception {
        
        if (!codigo.matches("\\d{4}"))
            throw new Exception("No se pudo consultar la unidad: el código dla unidad no cumple el formato requerido.");

        Bus b = dao.obtenerPorCodigo(codigo);
        if (b == null)
            throw new Exception("No se pudo consultar la unidad: no existe un unidad con el criterio ingresado.");

        return b;
    }

    public Bus consultarPorPlaca(String placa) throws Exception {
        
        if (!validarPlaca(placa))
            throw new Exception("No se pudo consultar la unidad: la placa dla unidad no cumple el formato requerido.");

        Bus b = dao.obtenerPorPlaca(placa);
        if (b == null)
            throw new Exception("No se pudo consultar la unidad: no existe un unidad con el criterio ingresado.");

        return b;
    }

    // =========================
    // RU6+7+8 – LISTAR FILTRADO
    // =========================
    public List<Bus> listar(String base, String estado) throws Exception {

        if (base != null && !esBaseValida(base))
            throw new Exception("No se pudo generar el listado de unidad: la base asignada no es válida.");

        if (estado != null && !List.of("ACTIVO", "INACTIVO", "MANTENIMIENTO").contains(estado))
            throw new Exception("No se pudo generar el listado de unidades: el estado ingresado no es válido.");

        List<Bus> lista = dao.listarFiltrado(base, estado);

        if (lista.isEmpty())
            throw new Exception("No se pudo generar el listado de unidades: no existen unidades que coincidan con los filtros ingresados.");

        return lista;
    }

    // =========================
    // RU9+16+17 – DISPONIBLES
    // =========================
    public List<Bus> listarDisponibles(String base) throws Exception {
        
        if (base != null && !esBaseValida(base))
            throw new Exception("No se pudo generar el listado de unidades disponibles: la base asignada no es válida.");

        List<Bus> lista = dao.listarDisponibles(base);

        if (lista.isEmpty())
            throw new Exception("No se pudo generar el listado de unidades disponibles: no existen unidades disponibles para la asignación de turnos.");

        return lista;
    } 

    // =========================
    // RU10+11 – ACTUALIZAR PLACA
    // =========================
  

    // =========================
    // RU12 – ACTUALIZAR BASE
    // =========================
    public void actualizarBase(String codigo, String nuevaBase) throws Exception {

        if (!codigo.matches("\\d{4}"))
            throw new Exception("No se pudo actualizar la base asignada dla unidad: el código dla unidad no cumple el formato requerido.");

        if (!esBaseValida(nuevaBase))
            throw new Exception("No se pudo actualizar la base asignada dla unidad: la base asignada no es válida.");

        Bus b = dao.obtenerPorCodigo(codigo);
        if (b == null)
            throw new Exception("No se pudo actualizar la base asignada dla unidad: el código dla unidad no existe.");

        int rows = dao.actualizarBase(codigo, nuevaBase);
        if (rows == 0)
            throw new Exception("No se pudo actualizar la base asignada dla unidad: el código dla unidad no existe.");
    }

    // =========================
    // RU13 – ACTIVAR BUS
    // =========================
    public void activar(String codigo) throws Exception {
        
        if (!codigo.matches("\\d{4}"))
            throw new Exception("No se pudo activar la unidad: el código dla unidad no cumple el formato requerido.");

        Bus b = dao.obtenerPorCodigo(codigo);
        if (b == null)
            throw new Exception("No se pudo activar la  unidad: el código dla unidad no existe.");

        if ("ACTIVO".equals(b.getEstado()))
            throw new Exception("No se pudo activar la unidad: la unidad ya se encuentra activo.");

        dao.actualizarEstado(codigo, "ACTIVO");
    }

    // =========================
    // RU14 – DESACTIVAR BUS
    // =========================
    public void desactivar(String codigo) throws Exception {
        
        if (!codigo.matches("\\d{4}"))
            throw new Exception("No se pudo inactivar la unidad: el código dla unidad no cumple el formato requerido.");

        Bus b = dao.obtenerPorCodigo(codigo);
        if (b == null)
            throw new Exception("No se pudo inactivar la unidad: el código dla unidad no existe.");

        if ("INACTIVO".equals(b.getEstado()))
            throw new Exception("No se pudo inactivar la unidad: la unidad ya se encuentra inactivada.");

        dao.actualizarEstado(codigo, "INACTIVO");
    }

    // =========================
    // RU15 – MANTENIMIENTO
    // =========================
    public void mantenimiento(String codigo) throws Exception {
        
        if (!codigo.matches("\\d{4}"))
            throw new Exception("No se pudo enviar la unidad a mantenimiento: el código dla unidad no cumple el formato requerido.");

        Bus b = dao.obtenerPorCodigo(codigo);
        if (b == null)
            throw new Exception("No se pudo enviar la unidad a mantenimiento: el código dla unidad no existe.");

        if ("MANTENIMIENTO".equals(b.getEstado()))
            throw new Exception("No se pudo enviar la unidad a mantenimiento: la unidad ya se encuentra en mantenimiento.");

        dao.actualizarEstado(codigo, "MANTENIMIENTO");
    }

    // =========================
    // VALIDACIÓN DE PLACA (ANEXO C)
    // =========================
    /**
     * Valida el formato de placa según Anexo C:
     * 
     * ESTRUCTURA: PPP-NNNN o PPPNNNN (guion opcional)
     * 
     * Donde:
     * - P = Primera letra OBLIGATORIAMENTE "P" (servicio público)
     * - P = Segunda letra A-Z (excepto Ñ)
     * - P = Tercera letra A-Z (excepto Ñ)
     * - Guion = Opcional (solo presentación)
     * - NNNN = 4 dígitos numéricos (0-9)
     * 
     * Ejemplos válidos:
     * ✅ PBD-7777
     * ✅ PBD7777
     * ✅ PGX-1234
     * ✅ PKL-0001
     * 
     * Ejemplos inválidos:
     * ❌ ABD-7777  (primera letra no es P)
     * ❌ PBÑ-7777  (contiene Ñ)
     * ❌ PB-77777  (solo 2 letras)
     * ❌ PBD-77    (solo 2 dígitos)
     * ❌ pbd-7777  (minúsculas)
     */
    private boolean validarPlaca(String placa) {
        if (placa == null || placa.isBlank())
            return false;

        // Normalizar: trim + mayúsculas
        placa = placa.trim().toUpperCase();

        // Regex flexible: guion opcional
        // P[A-Z]{2} = P + 2 letras (sin Ñ porque no está en A-Z)
        // -? = guion opcional
        // \\d{4} = 4 dígitos
        return placa.matches("P[A-Z]{2}-?\\d{4}");
    }
}