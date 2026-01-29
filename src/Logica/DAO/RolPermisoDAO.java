package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolPermisoDAO {

    // ================= DTO =================
    public static class RolPermisoRow {
        public final String rol;
        public final String permiso;

        public RolPermisoRow(String rol, String permiso) {
            this.rol = rol;
            this.permiso = permiso;
        }
    }

    // ================= ASIGNAR =================
    public void asignar(long idRol, long idPermiso) {
        String sql = "INSERT IGNORE INTO rol_permiso(id_rol, id_permiso) VALUES(?, ?)";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idRol);
            ps.setLong(2, idPermiso);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar permiso a rol.", e);
        }
    }

    // ================= REVOCAR =================
    public void revocar(long idRol, long idPermiso) {
        String sql = "DELETE FROM rol_permiso WHERE id_rol=? AND id_permiso=?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idRol);
            ps.setLong(2, idPermiso);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al revocar permiso a rol.", e);
        }
    }

    // ================= PERMISOS DE UN ROL =================
    public List<String> listarPermisosDeRol(long idRol) {

        String sql = """
            SELECT p.codigo
            FROM rol_permiso rp
            JOIN permiso p ON p.id_permiso = rp.id_permiso
            WHERE rp.id_rol = ?
            ORDER BY p.codigo
        """;

        List<String> out = new ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getString("codigo"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar permisos del rol.", e);
        }

        return out;
    }

    // ================= TODOS LOS ROLES + PERMISOS =================
    public List<RolPermisoRow> listarPermisosPorRoles() {

        String sql = """
            SELECT r.nombre AS rol, p.codigo AS permiso
            FROM rol_permiso rp
            JOIN rol r ON r.id_rol = rp.id_rol
            JOIN permiso p ON p.id_permiso = rp.id_permiso
            ORDER BY r.nombre, p.codigo
        """;

        List<RolPermisoRow> lista = new ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new RolPermisoRow(
                        rs.getString("rol"),
                        rs.getString("permiso")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar permisos por roles.", e);
        }

        return lista;
    }
}

