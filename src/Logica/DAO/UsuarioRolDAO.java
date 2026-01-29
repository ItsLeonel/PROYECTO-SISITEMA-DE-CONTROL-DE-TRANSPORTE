package Logica.DAO;

import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRolDAO {

    public void asignar(long idUsuario, long idRol) {
        String sql = "INSERT IGNORE INTO usuario_rol(id_usuario, id_rol) VALUES(?, ?)";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            ps.setLong(2, idRol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar rol a usuario.", e);
        }
    }

    public void revocar(long idUsuario, long idRol) {
        String sql = "DELETE FROM usuario_rol WHERE id_usuario=? AND id_rol=?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            ps.setLong(2, idRol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al revocar rol a usuario.", e);
        }
    }

    public List<String> listarRolesDeUsuario(long idUsuario) {
        String sql = """
            SELECT r.nombre
            FROM usuario_rol ur
            JOIN rol r ON r.id_rol = ur.id_rol
            WHERE ur.id_usuario = ?
            ORDER BY r.nombre
            """;

        List<String> out = new ArrayList<>();
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getString(1));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar roles del usuario.", e);
        }
    }
public void eliminarRolesDeUsuario(long idUsuario) {
    String sql = "DELETE FROM usuario_rol WHERE id_usuario = ?";

    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setLong(1, idUsuario);
        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Error al eliminar roles del usuario.", e);
    }
}
public void revocarTodos(long idUsuario) {
    String sql = "DELETE FROM usuario_rol WHERE id_usuario=?";
    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setLong(1, idUsuario);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Error al revocar roles del usuario.", e);
    }
}



}
