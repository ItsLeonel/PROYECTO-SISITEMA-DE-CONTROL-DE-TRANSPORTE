package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.Ruta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO actualizado para Ruta con soporte para:
 * - Base A y Base B
 * - Plantilla horaria (codigo_intervalo)
 * - Duración estimada
 */
public class RutaDAO {

    // =====================================================
    // CONSULTAS
    // =====================================================

    public Ruta buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM ruta WHERE codigo_ruta = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRuta(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error buscarPorCodigo: " + e.getMessage());
        }
        return null;
    }

    public Ruta buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM ruta WHERE nombre = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRuta(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error buscarPorNombre: " + e.getMessage());
        }
        return null;
    }

    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    public boolean existeNombre(String nombre) {
        String sql = "SELECT 1 FROM ruta WHERE nombre = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    // =====================================================
    // INSERCIÓN
    // =====================================================

    public boolean insertar(Ruta r) {
        String sql = """
                    INSERT INTO ruta
                    (codigo_ruta, nombre, codigo_base_a, codigo_base_b, codigo_plantilla,
                     duracion_estimada_minutos, origen, destino, estado)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getCodigoRuta());
            ps.setString(2, r.getNombre());
            ps.setInt(3, r.getCodigoBaseA());
            ps.setInt(4, r.getCodigoBaseB());

            // Allow null interval if <= 0 (Breaking Circular Dependency)
            if (r.getCodigoIntervalo() <= 0) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, r.getCodigoIntervalo());
            }

            ps.setInt(6, r.getDuracionEstimadaMinutos());
            ps.setString(7, r.getOrigen());
            ps.setString(8, r.getDestino());
            ps.setString(9, r.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertar ruta: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // ACTUALIZACIONES
    // =====================================================

    public boolean actualizarNombre(String codigo, String nombre) {
        String sql = "UPDATE ruta SET nombre = ? WHERE codigo_ruta = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarNombre: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarBaseA(String codigo, int codigoBaseA) {
        String sql = "UPDATE ruta SET codigo_base_a = ? WHERE codigo_ruta = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoBaseA);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarBaseA: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarBaseB(String codigo, int codigoBaseB) {
        String sql = "UPDATE ruta SET codigo_base_b = ? WHERE codigo_ruta = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoBaseB);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarBaseB: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(String codigo, String estado) {
        String sql = "UPDATE ruta SET estado = ? WHERE codigo_ruta = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, codigo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarEstado: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // LISTADOS
    // =====================================================

    public List<Ruta> listarTodas() {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ruta ORDER BY codigo_ruta";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRuta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error listarTodas(Ruta): " + e.getMessage());
        }

        return lista;
    }

    public List<Ruta> listarActivas() {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE estado = 'Activo' ORDER BY codigo_ruta";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRuta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error listarActivas: " + e.getMessage());
        }
        return lista;
    }

    public List<Ruta> listarInactivas() {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE estado = 'Inactivo' ORDER BY codigo_ruta";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRuta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error listarInactivas: " + e.getMessage());
        }
        return lista;
    }

    // =====================================================
    // VALIDACIONES AUXILIARES
    // =====================================================

    /**
     * Verifica si existe una base operativa con el código dado
     */
    public boolean existeBase(int codigoBase) {
        String sql = "SELECT 1 FROM base_operativa WHERE codigo_base = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoBase);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            System.out.println("Error existeBase: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si existe una plantilla horaria con el código dado
     */
    public boolean existePlantilla(int codigoIntervalo) {
        String sql = "SELECT 1 FROM plantilla_intervalo WHERE codigo_plantilla = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoIntervalo);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            System.out.println("Error existePlantilla: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si una plantilla horaria está activa (RN-TR-12)
     */
    public boolean esPlantillaActiva(int codigoIntervalo) {
        String sql = "SELECT estado FROM plantilla_intervalo WHERE codigo_plantilla = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoIntervalo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String estado = rs.getString("estado");
                return "Activo".equalsIgnoreCase(estado);
            }

        } catch (SQLException e) {
            System.out.println("Error esPlantillaActiva: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene el nombre de una base por su código
     */
    public String obtenerNombreBase(int codigoBase) {
        String sql = "SELECT nombre FROM base_operativa WHERE codigo_base = ?";
        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoBase);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerNombreBase: " + e.getMessage());
        }
        return null;
    }

    // =====================================================
    // MAPPER
    // =====================================================

    private Ruta mapRuta(ResultSet rs) throws SQLException {
        Ruta r = new Ruta();
        r.setCodigoRuta(rs.getString("codigo_ruta"));
        r.setNombre(rs.getString("nombre"));
        r.setCodigoBaseA(rs.getInt("codigo_base_a"));
        r.setCodigoBaseB(rs.getInt("codigo_base_b"));
        r.setCodigoIntervalo(rs.getInt("codigo_plantilla"));
        r.setDuracionEstimadaMinutos(rs.getInt("duracion_estimada_minutos"));
        r.setOrigen(rs.getString("origen"));
        r.setDestino(rs.getString("destino"));
        r.setEstado(rs.getString("estado"));
        return r;
    }
}