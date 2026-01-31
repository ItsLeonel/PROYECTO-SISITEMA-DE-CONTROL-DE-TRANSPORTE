package Logica.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar permisos y verificar acceso de usuarios
 */
public class PermisoDAO {

    // =====================================================
    // OBTENER CÓDIGOS DE PERMISOS DE UN USUARIO
    // =====================================================
    /**
     * Obtiene todos los códigos de permisos de un usuario
     * basándose en sus roles asignados
     * 
     * @param idUsuario ID del usuario
     * @return Lista de códigos de permisos (ej: ["RGS10.1", "RGB01"])
     */
    public static List<String> obtenerCodigosPermisosPorUsuario(Long idUsuario) {
        List<String> permisos = new ArrayList<>();
        
        String sql = """
            SELECT DISTINCT p.codigo
            FROM usuario u
            INNER JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario
            INNER JOIN rol r ON ur.id_rol = r.id_rol
            INNER JOIN rol_permiso rp ON r.id_rol = rp.id_rol
            INNER JOIN permiso p ON rp.id_permiso = p.id_permiso
            WHERE u.id_usuario = ?
            AND u.activo = TRUE
            AND r.activo = TRUE
            AND p.activo = TRUE
            ORDER BY p.codigo
        """;

        try (Connection conn = Logica.Conexiones.ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permisos.add(rs.getString("codigo"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener permisos del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return permisos;
    }

    // =====================================================
    // VERIFICAR SI USUARIO TIENE PERMISO ESPECÍFICO
    // =====================================================
    /**
     * Verifica directamente en BD si un usuario tiene un permiso
     * Útil para validaciones críticas
     */
    public static boolean usuarioTienePermiso(Long idUsuario, String codigoPermiso) {
        String sql = """
            SELECT COUNT(*) as tiene_permiso
            FROM Usuario u
            INNER JOIN Usuario_Rol ur ON u.id_usuario = ur.id_usuario
            INNER JOIN Rol r ON ur.id_rol = r.id_rol
            INNER JOIN Rol_Permiso rp ON r.id_rol = rp.id_rol
            INNER JOIN Permiso p ON rp.id_permiso = p.id_permiso
            WHERE u.id_usuario = ?
            AND p.codigo = ?
            AND u.activo = TRUE
            AND r.activo = TRUE
            AND p.activo = TRUE
        """;

        try (Connection conn = Logica.Conexiones.ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idUsuario);
            ps.setString(2, codigoPermiso);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tiene_permiso") > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar permiso: " + e.getMessage());
        }
        
        return false;
    }

    // =====================================================
    // OBTENER INFORMACIÓN COMPLETA DE PERMISOS DE USUARIO
    // =====================================================
    /**
     * Obtiene información detallada de permisos del usuario
     */
    public static class PermisoInfo {
        public String codigo;
        public String nombre;
        public String descripcion;
        public String modulo;
        
        public PermisoInfo(String codigo, String nombre, String descripcion, String modulo) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.modulo = modulo;
        }
    }
    
    public static List<PermisoInfo> obtenerPermisosDetalladosPorUsuario(Long idUsuario) {
        List<PermisoInfo> permisos = new ArrayList<>();
        
        String sql = """
            SELECT DISTINCT 
                p.codigo,
                p.nombre,
                p.descripcion,
                p.modulo
            FROM Usuario u
            INNER JOIN Usuario_Rol ur ON u.id_usuario = ur.id_usuario
            INNER JOIN Rol r ON ur.id_rol = r.id_rol
            INNER JOIN Rol_Permiso rp ON r.id_rol = rp.id_rol
            INNER JOIN Permiso p ON rp.id_permiso = p.id_permiso
            WHERE u.id_usuario = ?
            AND u.activo = TRUE
            AND r.activo = TRUE
            AND p.activo = TRUE
            ORDER BY p.modulo, p.codigo
        """;

        try (Connection conn = Logica.Conexiones.ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permisos.add(new PermisoInfo(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("modulo")
                    ));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener permisos detallados: " + e.getMessage());
        }
        
        return permisos;
    }

    // =====================================================
    // LISTAR TODOS LOS PERMISOS ACTIVOS
    // =====================================================
    public static List<String> listarCodigosPermisosActivos() {
        List<String> permisos = new ArrayList<>();
        
        String sql = "SELECT codigo FROM Permiso WHERE activo = TRUE ORDER BY codigo";

        try (Connection conn = Logica.Conexiones.ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                permisos.add(rs.getString("codigo"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al listar permisos: " + e.getMessage());
        }
        
        return permisos;
    }

    // =====================================================
    // OBTENER ID DE PERMISO POR CÓDIGO
    // =====================================================
    public static Long obtenerIdPorCodigo(String codigo) {
        String sql = "SELECT id_permiso FROM Permiso WHERE codigo = ? AND activo = TRUE";

        try (Connection conn = Logica.Conexiones.ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, codigo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_permiso");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID de permiso: " + e.getMessage());
        }
        
        return null;
    }

    // =====================================================
    // VERIFICAR SI PERMISO EXISTE
    // =====================================================
    public static boolean existePermiso(String codigo) {
        String sql = "SELECT COUNT(*) as existe FROM Permiso WHERE codigo = ? AND activo = TRUE";

        try (Connection conn = Logica.Conexiones.ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, codigo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("existe") > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de permiso: " + e.getMessage());
        }
        
        return false;
    }
}