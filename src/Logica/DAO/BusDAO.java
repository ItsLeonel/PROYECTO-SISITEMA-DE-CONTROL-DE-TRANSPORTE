package Logica.DAO;

import Logica.Entidades.Bus;
import Logica.Entidades.SocioDisponible;
import Logica.Conexiones.ConexionBD;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusDAO {

    /* =========================
       REGISTRAR BUS
       ========================= */
    public void insertar(Bus b) throws SQLException {
        String sql = "INSERT INTO buses " +
                "(placa, marca, modelo, anio_fabricacion, capacidad_pasajeros, base_asignada, estado, codigo_socio_fk) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, b.getPlaca());
            ps.setString(2, b.getMarca());
            ps.setString(3, b.getModelo());
            ps.setInt(4, b.getAnioFabricacion());
            ps.setInt(5, b.getCapacidadPasajeros());
            ps.setString(6, b.getBaseAsignada());
            ps.setString(7, b.getEstado());
            ps.setString(8, b.getCodigoSocioFk());

            ps.executeUpdate();
        }
    }

    /* =========================
       CONSULTAR POR PLACA
       ========================= */
    public Bus obtenerPorPlaca(String placa) throws SQLException {
        String sql = """
            SELECT b.*, s.codigo_socio, s.nombres_completos, s.numero_celular
            FROM buses b
            INNER JOIN socios_propietarios s
                ON b.codigo_socio_fk = s.codigo_socio
            WHERE b.placa = ?
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, placa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapBusConSocio(rs);
            }
        }
        return null;
    }

    /* =========================
       VALIDACIONES
       ========================= */
    public boolean existePorPlaca(String placa) throws SQLException {
        String sql = "SELECT 1 FROM buses WHERE placa = ?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, placa);
            return ps.executeQuery().next();
        }
    }

    public boolean socioTieneBus(String codigoSocio) throws SQLException {
        String sql = "SELECT 1 FROM buses WHERE codigo_socio_fk = ?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigoSocio);
            return ps.executeQuery().next();
        }
    }

    /* =========================
       LISTADOS
       ========================= */
    public List<Bus> listarTodos() throws SQLException {
        String sql = """
            SELECT b.*, s.codigo_socio, s.nombres_completos, s.numero_celular
            FROM buses b
            INNER JOIN socios_propietarios s
                ON b.codigo_socio_fk = s.codigo_socio
            ORDER BY b.placa
        """;

        List<Bus> buses = new ArrayList<>();

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                buses.add(mapBusConSocio(rs));
            }
        }
        return buses;
    }

    public List<Bus> listarFiltrado(String base, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT b.*, s.codigo_socio, s.nombres_completos, s.numero_celular
            FROM buses b
            INNER JOIN socios_propietarios s
                ON b.codigo_socio_fk = s.codigo_socio
            WHERE 1=1
        """);

        if (base != null && !base.isBlank()) {
    sql.append(" AND b.base_asignada = ?");
}
if (estado != null && !estado.isBlank()) {
    sql.append(" AND b.estado = ?");
}

        sql.append(" ORDER BY b.placa");

        List<Bus> buses = new ArrayList<>();

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int i = 1;
            if (base != null) ps.setString(i++, base);
            if (estado != null) ps.setString(i, estado);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                buses.add(mapBusConSocio(rs));
            }
        }
        return buses;
    }

    /* =========================
       MAPEO
       ========================= */
    private Bus mapBusConSocio(ResultSet rs) throws SQLException {
        Bus bus = new Bus();

        bus.setPlaca(rs.getString("placa"));
        bus.setMarca(rs.getString("marca"));
        bus.setModelo(rs.getString("modelo"));
        bus.setAnioFabricacion(rs.getInt("anio_fabricacion"));
        bus.setCapacidadPasajeros(rs.getInt("capacidad_pasajeros"));
        bus.setBaseAsignada(rs.getString("base_asignada"));
        bus.setEstado(rs.getString("estado"));
        bus.setCodigoSocioFk(rs.getString("codigo_socio_fk"));

        bus.setNombresPropietario(rs.getString("nombres_completos"));
        bus.setTelefonoPropietario(rs.getString("numero_celular"));

        return bus;
    }
    public int actualizarBase(String placa, String nuevaBase) throws SQLException {
    String sql = "UPDATE buses SET base_asignada = ? WHERE placa = ?";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, nuevaBase);
        ps.setString(2, placa);
        return ps.executeUpdate();
    }
}
public List<SocioDisponible> obtenerSociosDisponibles() throws SQLException {
    String sql = """
        SELECT codigo_socio, nombres_completos
        FROM socios_propietarios
        WHERE estado = 'ACTIVO'
        AND codigo_socio NOT IN (
            SELECT codigo_socio_fk FROM buses
        )
        ORDER BY nombres_completos
    """;

    List<SocioDisponible> socios = new ArrayList<>();

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            SocioDisponible s = new SocioDisponible();
            s.setCodigoSocio(rs.getString("codigo_socio"));
            s.setNombresCompletos(rs.getString("nombres_completos"));
            socios.add(s);
        }
    }
    return socios;
}
public void actualizarEstado(String placa, String estado) throws SQLException {
    String sql = "UPDATE buses SET estado = ? WHERE placa = ?";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, estado);
        ps.setString(2, placa);
        ps.executeUpdate();
    }
}


}
