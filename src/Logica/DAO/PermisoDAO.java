package Logica.DAO;

import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAO {

    public List<String> listarCodigos() {
        String sql = "SELECT codigo FROM permiso WHERE activo=1 ORDER BY codigo";
        List<String> out = new ArrayList<>();
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString(1));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar códigos de permisos.", e);
        }
    }

    public long obtenerIdPorCodigo(String codigo) {
        String sql = "SELECT id_permiso FROM permiso WHERE codigo=? LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener id del permiso.", e);
        }
    }
}
