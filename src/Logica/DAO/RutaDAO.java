package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.Ruta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {

    public Ruta buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM ruta WHERE codigo_ruta = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ruta r = new Ruta();
                r.setCodigoRuta(rs.getString("codigo_ruta"));
                r.setNombre(rs.getString("nombre"));
                r.setOrigen(rs.getString("origen"));
                r.setDestino(rs.getString("destino"));
                r.setEstado(rs.getString("estado"));
                return r;
            }
        } catch (SQLException e) {
            System.out.println("Error buscarPorCodigo: " + e.getMessage());
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

    public boolean insertar(Ruta r) {
        String sql = """
            INSERT INTO ruta (codigo_ruta, nombre, origen, destino, estado)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getCodigoRuta());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getOrigen());
            ps.setString(4, r.getDestino());
            ps.setString(5, r.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertar ruta: " + e.getMessage());
            return false;
        }
    }
    public Ruta buscarPorNombre(String nombre) {
    String sql = "SELECT * FROM ruta WHERE nombre = ?";
    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Ruta r = new Ruta();
            r.setCodigoRuta(rs.getString("codigo_ruta"));
            r.setNombre(rs.getString("nombre"));
            r.setOrigen(rs.getString("origen"));
            r.setDestino(rs.getString("destino"));
            r.setEstado(rs.getString("estado"));
            return r;
        }
    } catch (SQLException e) {
        System.out.println("Error buscarPorNombre: " + e.getMessage());
    }
    return null;
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

public boolean actualizarDestino(String codigo, String destino) {

    String sql = "UPDATE ruta SET destino = ? WHERE codigo_ruta = ?";

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, destino);
        ps.setString(2, codigo);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println("Error actualizarDestino: " + e.getMessage());
        return false;
    }
}
public boolean actualizarOrigen(String codigo, String origen) {

    String sql = "UPDATE ruta SET origen = ? WHERE codigo_ruta = ?";

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, origen);
        ps.setString(2, codigo);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println("Error actualizarOrigen: " + e.getMessage());
        return false;
    }
}




    public List<Ruta> listarActivas() {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE estado = 'Activo'";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ruta r = new Ruta();
                r.setCodigoRuta(rs.getString("codigo_ruta"));
                r.setNombre(rs.getString("nombre"));
                r.setOrigen(rs.getString("origen"));
                r.setDestino(rs.getString("destino"));
                r.setEstado(rs.getString("estado"));
                lista.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Error listarActivas: " + e.getMessage());
        }
        return lista;
    }
    public List<Ruta> listarTodas() {
    List<Ruta> lista = new java.util.ArrayList<>();
    String sql = "SELECT * FROM ruta ORDER BY codigo_ruta";

    try (java.sql.Connection con = Logica.Conexiones.ConexionBD.conectar();
         java.sql.PreparedStatement ps = con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Ruta r = new Ruta();
            r.setCodigoRuta(rs.getString("codigo_ruta"));
            r.setNombre(rs.getString("nombre"));
            r.setOrigen(rs.getString("origen"));
            r.setDestino(rs.getString("destino"));
            r.setEstado(rs.getString("estado"));
            lista.add(r);
        }

    } catch (java.sql.SQLException e) {
        System.out.println("Error listarTodas(Ruta): " + e.getMessage());
    }

    return lista;
}

}
