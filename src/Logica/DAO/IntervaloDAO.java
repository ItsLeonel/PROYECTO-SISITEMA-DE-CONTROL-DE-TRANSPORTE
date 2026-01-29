package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.Intervalo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class IntervaloDAO {

    public boolean existeCodigo(int codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    public Intervalo buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM intervalo WHERE codigo_intervalo = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error buscarPorCodigo(Intervalo): " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Intervalo i) {
        String sql = """
            INSERT INTO intervalo (codigo_intervalo, tiempo_minutos, franja_horaria, codigo_ruta, estado)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, i.getCodigoIntervalo());
            ps.setInt(2, i.getTiempoMinutos());
            ps.setString(3, i.getFranjaHoraria());
            ps.setString(4, i.getCodigoRuta());
            ps.setString(5, i.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertar(Intervalo): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarTiempo(int codigo, int nuevoTiempo) {
        String sql = "UPDATE intervalo SET tiempo_minutos = ? WHERE codigo_intervalo = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoTiempo);
            ps.setInt(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarTiempo(Intervalo): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarFranja(int codigo, String nuevaFranja) {
        String sql = "UPDATE intervalo SET franja_horaria = ? WHERE codigo_intervalo = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevaFranja);
            ps.setInt(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarFranja(Intervalo): " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int codigo, String estado) {
        String sql = "UPDATE intervalo SET estado = ? WHERE codigo_intervalo = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, codigo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error cambiarEstado(Intervalo): " + e.getMessage());
            return false;
        }
    }

    public List<Intervalo> listarActivos() {
        List<Intervalo> lista = new ArrayList<>();
        String sql = "SELECT * FROM intervalo WHERE estado = 'Activo' ORDER BY codigo_intervalo";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException e) {
            System.out.println("Error listarActivos(Intervalo): " + e.getMessage());
        }
        return lista;
    }

    public List<Intervalo> listarPorRuta(String codigoRuta) {
        List<Intervalo> lista = new ArrayList<>();
        String sql = "SELECT * FROM intervalo WHERE codigo_ruta = ? ORDER BY franja_horaria";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoRuta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error listarPorRuta(Intervalo): " + e.getMessage());
        }
        return lista;
    }

    public List<Intervalo> listarPorFranja(String franja) {
        List<Intervalo> lista = new ArrayList<>();
        String sql = "SELECT * FROM intervalo WHERE franja_horaria = ? ORDER BY codigo_ruta";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, franja);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error listarPorFranja(Intervalo): " + e.getMessage());
        }
        return lista;
    }

    private Intervalo map(ResultSet rs) throws SQLException {
        Intervalo i = new Intervalo();
        i.setCodigoIntervalo(rs.getInt("codigo_intervalo"));
        i.setTiempoMinutos(rs.getInt("tiempo_minutos"));
        i.setFranjaHoraria(rs.getString("franja_horaria"));
        i.setCodigoRuta(rs.getString("codigo_ruta"));
        i.setEstado(rs.getString("estado"));
        return i;
    }
    public List<Intervalo> listarTodos() {
    List<Intervalo> lista = new java.util.ArrayList<>();
    String sql = "SELECT * FROM intervalo ORDER BY codigo_intervalo";

    try (java.sql.Connection con = Logica.Conexiones.ConexionBD.conectar();
         java.sql.PreparedStatement ps = con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Intervalo i = new Intervalo();
            i.setCodigoIntervalo(rs.getInt("codigo_intervalo"));
            i.setTiempoMinutos(rs.getInt("tiempo_minutos"));
            i.setFranjaHoraria(rs.getString("franja_horaria"));
            i.setCodigoRuta(rs.getString("codigo_ruta"));
            i.setEstado(rs.getString("estado"));
            lista.add(i);
        }

    } catch (java.sql.SQLException e) {
        System.out.println("Error listarTodos(Intervalo): " + e.getMessage());
    }

    return lista;
}

}
