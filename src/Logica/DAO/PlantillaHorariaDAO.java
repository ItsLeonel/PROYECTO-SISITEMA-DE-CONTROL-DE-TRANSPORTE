package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.PlantillaHoraria;
import Logica.Entidades.PlantillaHorariaFranja;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar Plantillas Horarias (tabla madre) y sus Franjas (tabla
 * hija)
 */
public class PlantillaHorariaDAO {

    // =====================================================
    // OPERACIONES SOBRE PLANTILLA HORARIA (MADRE)
    // =====================================================

    /**
     * Verifica si existe una plantilla con el código dado
     */
    public boolean existeCodigo(int codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    /**
     * Busca una plantilla por su código (sin franjas)
     */
    public PlantillaHoraria buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM plantilla_intervalo WHERE codigo_plantilla = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapPlantilla(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error buscarPorCodigo(PlantillaHoraria): " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserta una nueva plantilla horaria (solo tabla madre)
     */
    public boolean insertarPlantilla(PlantillaHoraria p) {
        String sql = """
                    INSERT INTO plantilla_intervalo
                    (codigo_plantilla, nombre, hora_inicio_operaciones, hora_fin_operaciones, codigo_ruta, estado)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getCodigoPlantilla());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getHoraInicioOperaciones());
            ps.setString(4, p.getHoraFinOperaciones());
            ps.setString(5, p.getCodigoRuta());
            ps.setString(6, p.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertarPlantilla(PlantillaHoraria): " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos básicos de una plantilla (nombre, horas de inicio/fin)
     */
    public boolean actualizarPlantilla(PlantillaHoraria p) {
        String sql = """
                    UPDATE plantilla_intervalo
                    SET nombre = ?, hora_inicio_operaciones = ?, hora_fin_operaciones = ?
                    WHERE codigo_plantilla = ?
                """;
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getHoraInicioOperaciones());
            ps.setString(3, p.getHoraFinOperaciones());
            ps.setInt(4, p.getCodigoPlantilla());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarPlantilla(PlantillaHoraria): " + e.getMessage());
            return false;
        }
    }

    /**
     * Cambia el estado de una plantilla (Activo/Inactivo)
     */
    public boolean cambiarEstado(int codigo, String estado) {
        String sql = "UPDATE plantilla_intervalo SET estado = ? WHERE codigo_plantilla = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error cambiarEstado(PlantillaHoraria): " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todas las plantillas activas
     */
    public List<PlantillaHoraria> listarActivas() {
        return listarPorEstado("Activo");
    }

    /**
     * Lista todas las plantillas inactivas
     */
    public List<PlantillaHoraria> listarInactivas() {
        return listarPorEstado("Inactivo");
    }

    /**
     * Lista todas las plantillas (sin importar estado)
     */
    public List<PlantillaHoraria> listarTodas() {
        List<PlantillaHoraria> lista = new ArrayList<>();
        String sql = "SELECT * FROM plantilla_intervalo ORDER BY codigo_plantilla";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                lista.add(mapPlantilla(rs));

        } catch (SQLException e) {
            System.out.println("Error listarTodas(PlantillaHoraria): " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista plantillas por estado
     */
    private List<PlantillaHoraria> listarPorEstado(String estado) {
        List<PlantillaHoraria> lista = new ArrayList<>();
        String sql = "SELECT * FROM plantilla_intervalo WHERE estado = ? ORDER BY codigo_plantilla";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    lista.add(mapPlantilla(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error listarPorEstado(PlantillaHoraria): " + e.getMessage());
        }
        return lista;
    }

    // =====================================================
    // OPERACIONES SOBRE FRANJAS (HIJA)
    // =====================================================

    /**
     * Inserta una franja asociada a una plantilla
     */
    public boolean insertarFranja(PlantillaHorariaFranja franja) {
        String sql = """
                    INSERT INTO plantilla_intervalo_franja
                    (codigo_plantilla, franja_id, tiempo_minutos)
                    VALUES (?, ?, ?)
                """;
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, franja.getCodigoPlantilla());
            ps.setInt(2, franja.getFranjaId());
            ps.setInt(3, franja.getTiempoMinutos());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertarFranja(PlantillaHorariaFranja): " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el tiempo (minutos) de una franja específica
     */
    public boolean actualizarTiempoFranja(int codigoPlantilla, int franjaId, int nuevoTiempo) {
        String sql = """
                    UPDATE plantilla_intervalo_franja
                    SET tiempo_minutos = ?
                    WHERE codigo_plantilla = ? AND franja_id = ?
                """;
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoTiempo);
            ps.setInt(2, codigoPlantilla);
            ps.setInt(3, franjaId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarTiempoFranja(PlantillaHorariaFranja): " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las franjas (1-6) de una plantilla específica
     */
    public List<PlantillaHorariaFranja> obtenerFranjasPorPlantilla(int codigoPlantilla) {
        List<PlantillaHorariaFranja> lista = new ArrayList<>();
        String sql = """
                    SELECT * FROM plantilla_intervalo_franja
                    WHERE codigo_plantilla = ?
                    ORDER BY franja_id
                """;

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoPlantilla);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    lista.add(mapFranja(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerFranjasPorPlantilla: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Elimina todas las franjas de una plantilla (útil para actualización completa)
     */
    public boolean eliminarFranjasPorPlantilla(int codigoPlantilla) {
        String sql = "DELETE FROM plantilla_intervalo_franja WHERE codigo_plantilla = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoPlantilla);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error eliminarFranjasPorPlantilla: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // MAPPERS (ResultSet → Entidad)
    // =====================================================

    private PlantillaHoraria mapPlantilla(ResultSet rs) throws SQLException {
        PlantillaHoraria p = new PlantillaHoraria();
        p.setCodigoPlantilla(rs.getInt("codigo_plantilla"));
        p.setNombre(rs.getString("nombre"));
        p.setHoraInicioOperaciones(rs.getString("hora_inicio_operaciones"));
        p.setHoraFinOperaciones(rs.getString("hora_fin_operaciones"));
        p.setCodigoRuta(rs.getString("codigo_ruta"));
        p.setEstado(rs.getString("estado"));
        return p;
    }

    private PlantillaHorariaFranja mapFranja(ResultSet rs) throws SQLException {
        PlantillaHorariaFranja f = new PlantillaHorariaFranja();
        f.setCodigoPlantilla(rs.getInt("codigo_plantilla"));
        f.setFranjaId(rs.getInt("franja_id"));
        f.setTiempoMinutos(rs.getInt("tiempo_minutos"));
        return f;
    }
}
