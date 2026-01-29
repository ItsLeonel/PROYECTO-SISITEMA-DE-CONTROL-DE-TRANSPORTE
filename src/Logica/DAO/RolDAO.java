package Logica.DAO;

import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public static class RolRow {
        public long id;
        public String nombre;
        public boolean activo;

        public RolRow(long id, String nombre, boolean activo) {
            this.id = id;
            this.nombre = nombre;
            this.activo = activo;
        }
    }

    public List<RolRow> listar() {
        String sql = "SELECT id_rol, nombre, activo FROM rol ORDER BY id_rol";
        List<RolRow> out = new ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new RolRow(
                        rs.getLong("id_rol"),
                        rs.getString("nombre"),
                        rs.getBoolean("activo")
                ));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar roles.", e);
        }
    }

    public List<String> listarNombresRoles() {
        String sql = "SELECT nombre FROM rol ORDER BY nombre";
        List<String> out = new ArrayList<>();
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString(1));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar nombres de roles.", e);
        }
    }

    public RolRow obtenerPorId(long idRol) {
        String sql = "SELECT id_rol, nombre, activo FROM rol WHERE id_rol=? LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new RolRow(rs.getLong("id_rol"), rs.getString("nombre"), rs.getBoolean("activo"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener rol.", e);
        }
    }

    public long obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id_rol FROM rol WHERE nombre=? LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener id del rol.", e);
        }
    }

    public boolean existeNombre(String nombre) {
        String sql = "SELECT 1 FROM rol WHERE LOWER(nombre)=LOWER(?) LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar nombre de rol.", e);
        }
    }

    public boolean existeNombreEnOtro(String nombre, long idRol) {
        String sql = "SELECT 1 FROM rol WHERE LOWER(nombre)=LOWER(?) AND id_rol<>? LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setLong(2, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar nombre de rol.", e);
        }
    }

    public long insertar(String nombre) {
        String sql = "INSERT INTO rol(nombre, activo) VALUES(?, 0)";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar rol.", e);
        }
    }

    public void actualizarNombre(long idRol, String nuevoNombre) {
        String sql = "UPDATE rol SET nombre=? WHERE id_rol=?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setLong(2, idRol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar rol.", e);
        }
    }

    public void setActivo(long idRol, boolean activo) {
        String sql = "UPDATE rol SET activo=? WHERE id_rol=?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setLong(2, idRol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado del rol.", e);
        }
 
    }
 public List<String> listarNombresActivos() {
    List<String> lista = new ArrayList<>();
    String sql = "SELECT nombre FROM rol WHERE activo = 1 ORDER BY nombre";

    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(rs.getString("nombre"));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error al listar nombres de roles activos.", e);
    }

    return lista;
}


    public boolean estaActivo(long idRol) {
    String sql = "SELECT activo FROM rol WHERE id_rol = ?";

    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setLong(1, idRol);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return false;
            return rs.getBoolean("activo");
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error al consultar estado del rol.", e);
    }
}

}

