package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.Base;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseDAO {

    public boolean existeCodigo(int codigo) {
        String sql = "SELECT 1 FROM base_operativa WHERE codigo_base = ?";
;
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existeNombre(String nombre) {
        String sql = "SELECT 1 FROM base_operativa WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean insertar(Base b) {
        String sql = "INSERT INTO base_operativa (codigo_base, nombre, direccion, estado) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, b.getCodigoBase());
            ps.setString(2, b.getNombre());
            ps.setString(3, b.getDireccion());
            ps.setString(4, b.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertar(Base): " + e.getMessage());
            return false;
        }
    }

    public Base buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM base_operativa WHERE codigo_base = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.out.println("Error buscarPorCodigo(Base): " + e.getMessage());
        }
        return null;
    }

    public Base buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM base_operativa WHERE nombre = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return map(rs);

        } catch (SQLException e) {
            System.out.println("Error buscarPorNombre(Base): " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarNombre(int codigo, String nuevoNombre) {
        String sql = "UPDATE base_operativa SET nombre = ? WHERE codigo_base = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoNombre);
            ps.setInt(2, codigo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error actualizarNombre(Base): " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int codigo, String estado) {
        String sql = "UPDATE base_operativa SET estado = ? WHERE codigo_base = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, codigo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error cambiarEstado(Base): " + e.getMessage());
            return false;
        }
    }

    public List<Base> listarActivas() {
        List<Base> lista = new ArrayList<>();
         String sql = "SELECT * FROM base_operativa WHERE estado = 'Activo' ORDER BY codigo_base";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException e) {
            System.out.println("Error listarActivas(Base): " + e.getMessage());
        }
        return lista;
    }

    private Base map(ResultSet rs) throws SQLException {
        Base b = new Base();
        b.setCodigoBase(rs.getInt("codigo_base"));
        b.setNombre(rs.getString("nombre"));
        b.setDireccion(rs.getString("direccion"));
        b.setEstado(rs.getString("estado"));
        return b;
    }
    public List<String> listarNombres() {

    List<String> nombres = new ArrayList<>();

    String sql = "SELECT nombre FROM base_operativa WHERE estado = 'Activo' ORDER BY nombre";


    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            nombres.add(rs.getString("nombre"));
        }

    } catch (SQLException e) {
        System.out.println("Error listarNombres(Base): " + e.getMessage());
    }

    return nombres;
}
public List<Base> listarTodas() {
    List<Base> lista = new ArrayList<>();
    String sql = "SELECT * FROM base_operativa ORDER BY codigo_base";

    try (Connection con = ConexionBD.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(map(rs));
        }

    } catch (SQLException e) {
        System.out.println("Error listarTodas(Base): " + e.getMessage());
    }

    return lista;
}
}
