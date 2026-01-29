package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.ReglaSancion;

import java.sql.*;

public class ReglaSancionDAO {

    public ReglaSancion buscarPorTipo(String codigoTipo) throws SQLException {
        String sql = "SELECT * FROM regla_sancion WHERE codigo_tipo = ?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigoTipo);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            ReglaSancion r = new ReglaSancion();
            r.setCodigoTipoIncidencia(rs.getString("codigo_tipo"));
            r.setPosicionesRetroceso(rs.getInt("posiciones_retroceso"));
            return r;
        }
    }

    public boolean insertar(ReglaSancion r) throws SQLException {
        String sql = "INSERT INTO regla_sancion VALUES (?,?)";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, r.getCodigoTipoIncidencia());
            ps.setInt(2, r.getPosicionesRetroceso());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(ReglaSancion r) throws SQLException {
        String sql = "UPDATE regla_sancion SET posiciones_retroceso=? WHERE codigo_tipo=?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, r.getPosicionesRetroceso());
            ps.setString(2, r.getCodigoTipoIncidencia());
            return ps.executeUpdate() > 0;
        }
    }
    public java.util.List<String> listarTipos() throws SQLException {
    java.util.List<String> lista = new java.util.ArrayList<>();
    String sql = "SELECT codigo_tipo FROM regla_sancion ORDER BY codigo_tipo ASC";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(rs.getString("codigo_tipo"));
        }
    }
    return lista;
}

}


