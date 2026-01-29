package Logica.DAO;

import Logica.Entidades.Bus;
import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusDAO {

    // =========================
    // RU1+2+3 – REGISTRAR BUS
    // =========================
    public void insertar(Bus b) throws SQLException {
        String sql = """
            INSERT INTO unidad (codigo, placa, dueno, marca, modelo, anio_fabricacion, base, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, b.getCodigo());
            ps.setString(2, b.getPlaca());
            ps.setString(3, b.getDueno());
            ps.setString(4, b.getMarca());
            ps.setString(5, b.getModelo());
            ps.setInt(6, b.getAnioFabricacion());
            ps.setString(7, b.getBase());
            ps.setString(8, b.getEstado());

            ps.executeUpdate();
        }
    }

    // =========================
    // RU4 – CONSULTAR POR CÓDIGO
    // =========================
    public Bus obtenerPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM unidad WHERE codigo = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapBus(rs);
        }
        return null;
    }

    // =========================
    // RU5 – CONSULTAR POR PLACA
    // =========================
    public Bus obtenerPorPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM unidad WHERE placa = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, placa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapBus(rs);
        }
        return null;
    }

    // =========================
    // RU12 – ACTUALIZAR BASE
    // =========================
    public int actualizarBase(String codigo, String nuevaBase) throws SQLException {

        String sql = "UPDATE unidad SET base = ? WHERE codigo = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nuevaBase);
            ps.setString(2, codigo);

            return ps.executeUpdate();
        }
    }

    // =========================
    // RU6+7+8 – LISTAR FILTRADO
    // =========================
    public List<Bus> listarFiltrado(String base, String estado) throws SQLException {
        List<Bus> lista = new ArrayList<>();

        String sql = "SELECT * FROM unidad WHERE 1=1";
        if (base != null) sql += " AND base = ?";
        if (estado != null) sql += " AND estado = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int i = 1;
            if (base != null) ps.setString(i++, base);
            if (estado != null) ps.setString(i++, estado);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapBus(rs));
        }
        return lista;
    }

    // =========================
    // RU9+16+17 – DISPONIBLES
    // =========================
    public List<Bus> listarDisponibles(String base) throws SQLException {
        List<Bus> lista = new ArrayList<>();

        String sql = "SELECT * FROM unidad WHERE estado = 'ACTIVO'";
        if (base != null) sql += " AND base = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (base != null) ps.setString(1, base);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapBus(rs));
        }
        return lista;
    }

    // =========================
    // RU10+11 – ACTUALIZAR PLACA
    // =========================
  
    // =========================
    // RU13 / RU14 / RU15 – ACTUALIZAR ESTADO
    // =========================
    public void actualizarEstado(String codigo, String estado) throws SQLException {
        String sql = "UPDATE unidad SET estado = ? WHERE codigo = ?";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, codigo);
            ps.executeUpdate();
        }
    }

    // =========================
    // MAPEO
    // =========================
    private Bus mapBus(ResultSet rs) throws SQLException {
        return new Bus(
                rs.getString("codigo"),
                rs.getString("placa"),
                rs.getString("dueno"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getInt("anio_fabricacion"),
                rs.getString("base"),
                rs.getString("estado")
        );
    }
}