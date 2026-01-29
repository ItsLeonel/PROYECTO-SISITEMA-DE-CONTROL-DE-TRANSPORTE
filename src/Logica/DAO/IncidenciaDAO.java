package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.Incidencia;
import java.util.List;
import java.util.ArrayList;




import java.sql.*;
public class IncidenciaDAO {

    public boolean busExiste(String codigoBus) throws SQLException {
        String sql = "SELECT 1 FROM bus WHERE codigo = ?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoBus);
            return ps.executeQuery().next();
        }
    }

    public boolean insertar(Incidencia i) throws SQLException {
        String sql = """
            INSERT INTO incidencia
            (codigo_incidencia, codigo_bus, codigo_ruta, codigo_tipo, fecha_evento, estado)
            VALUES (?,?,?,?,?,?)
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, i.getCodigoIncidencia());
            ps.setString(2, i.getCodigoBus());
            ps.setString(3, i.getCodigoRuta());
            ps.setString(4, i.getCodigoTipo());
            ps.setDate(5, Date.valueOf(i.getFechaEvento()));
            ps.setString(6, i.getEstado());

            return ps.executeUpdate() > 0;
        }
    }

    public Incidencia buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM incidencia WHERE codigo_incidencia = ?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Incidencia i = new Incidencia();
            i.setCodigoIncidencia(rs.getString("codigo_incidencia"));
            i.setCodigoBus(rs.getString("codigo_bus"));
            i.setCodigoRuta(rs.getString("codigo_ruta"));
            i.setCodigoTipo(rs.getString("codigo_tipo"));
            i.setFechaEvento(rs.getDate("fecha_evento").toLocalDate());
            i.setEstado(rs.getString("estado"));

            return i;
        }
    }

    public List<Incidencia> listar() throws SQLException {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM incidencia ORDER BY fecha_evento DESC";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Incidencia i = new Incidencia();
                i.setCodigoIncidencia(rs.getString("codigo_incidencia"));
                i.setCodigoBus(rs.getString("codigo_bus"));
                i.setCodigoRuta(rs.getString("codigo_ruta"));
                i.setCodigoTipo(rs.getString("codigo_tipo"));
                i.setFechaEvento(rs.getDate("fecha_evento").toLocalDate());
                i.setEstado(rs.getString("estado"));
                lista.add(i);
            }
        }
        return lista;
    }

    public String obtenerSiguienteCodigo() throws SQLException {
    String sql = "SELECT MAX(codigo_incidencia) AS max_cod FROM incidencia";
    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        int next = 1;

        if (rs.next()) {
            String max = rs.getString("max_cod"); // puede ser null si no hay registros
            if (max != null && max.matches("\\d{3}")) {
                next = Integer.parseInt(max) + 1;
            }
        }

        // vuelve a formatear en 3 dígitos
        return String.format("%03d", next);
    }
}

}

