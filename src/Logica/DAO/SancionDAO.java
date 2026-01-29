package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.Sancion;
import java.util.List;
import java.util.ArrayList;


import java.sql.*;
//import java.util.ArrayList;

public class SancionDAO {

    public boolean insertar(Sancion s) throws SQLException {
        String sql = """
            INSERT INTO sancion
            (codigo_sancion, codigo_incidencia, fecha_aplicacion,
             responsable, posiciones_retroceso, estado)
            VALUES (?,?,?,?,?,?)
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, s.getCodigoSancion());
            ps.setString(2, s.getCodigoIncidencia());
            ps.setDate(3, Date.valueOf(s.getFechaAplicacion()));
            ps.setString(4, s.getResponsable());
            ps.setInt(5, s.getPosicionesRetroceso());
            ps.setString(6, s.getEstado());

            return ps.executeUpdate() > 0;
        }
    }

    public Sancion buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM sancion WHERE codigo_sancion = ?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Sancion s = new Sancion();
            s.setCodigoSancion(rs.getString("codigo_sancion"));
            s.setCodigoIncidencia(rs.getString("codigo_incidencia"));
            s.setFechaAplicacion(rs.getDate("fecha_aplicacion").toLocalDate());
            s.setResponsable(rs.getString("responsable"));
            s.setPosicionesRetroceso(rs.getInt("posiciones_retroceso"));
            s.setEstado(rs.getString("estado"));

            return s;
        }
    }

    public boolean actualizarEstado(String codigo, String estado) throws SQLException {
        String sql = "UPDATE sancion SET estado=? WHERE codigo_sancion=?";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        }
    }
    public List<Sancion> consultar() throws SQLException {
    List<Sancion> lista = new ArrayList<>();
    String sql = "SELECT * FROM sancion ORDER BY fecha_aplicacion DESC";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Sancion s = new Sancion();
            s.setCodigoSancion(rs.getString("codigo_sancion"));
            s.setCodigoIncidencia(rs.getString("codigo_incidencia"));
            s.setFechaAplicacion(rs.getDate("fecha_aplicacion").toLocalDate());
            s.setResponsable(rs.getString("responsable"));
            s.setEstado(rs.getString("estado"));
            lista.add(s);
        }
    }
    return lista;
}
public String obtenerSiguienteCodigo() throws SQLException {
    String sql = "SELECT MAX(codigo_sancion) AS max_cod FROM sancion";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        int siguiente = 1;

        if (rs.next()) {
            String max = rs.getString("max_cod");
            if (max != null && max.matches("\\d{3}")) {
                siguiente = Integer.parseInt(max) + 1;
            }
        }

        return String.format("%03d", siguiente);
    }
}
public List<Sancion> consultarFiltrado(String codigoBus,
                                       String codigoRuta,
                                       java.time.LocalDate dia,
                                       java.time.YearMonth mes,
                                       String estado) throws SQLException {

    List<Sancion> lista = new ArrayList<>();

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT s.codigo_sancion, s.codigo_incidencia, s.fecha_aplicacion, s.responsable, s.estado ")
       .append("FROM sancion s ")
       .append("JOIN incidencia i ON i.codigo_incidencia = s.codigo_incidencia ")
       .append("WHERE 1=1 ");

    java.util.List<Object> params = new java.util.ArrayList<>();

    if (codigoBus != null && !codigoBus.isBlank()) {
        sql.append(" AND i.codigo_bus = ? ");
        params.add(codigoBus.trim());
    }

    if (codigoRuta != null && !codigoRuta.isBlank()) {
        sql.append(" AND i.codigo_ruta = ? ");
        params.add(codigoRuta.trim());
    }

    if (estado != null && !estado.isBlank()) {
        sql.append(" AND s.estado = ? ");
        params.add(estado.trim()); // "ACTIVA" o "ANULADA"
    }

    // Filtro por día o por mes
    if (dia != null) {
        sql.append(" AND s.fecha_aplicacion = ? ");
        params.add(java.sql.Date.valueOf(dia));
    } else if (mes != null) {
        java.time.LocalDate ini = mes.atDay(1);
        java.time.LocalDate fin = mes.atEndOfMonth();
        sql.append(" AND s.fecha_aplicacion BETWEEN ? AND ? ");
        params.add(java.sql.Date.valueOf(ini));
        params.add(java.sql.Date.valueOf(fin));
    }

    sql.append(" ORDER BY s.fecha_aplicacion DESC ");

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sancion s = new Sancion();
                s.setCodigoSancion(rs.getString("codigo_sancion"));
                s.setCodigoIncidencia(rs.getString("codigo_incidencia"));
                s.setFechaAplicacion(rs.getDate("fecha_aplicacion").toLocalDate());
                s.setResponsable(rs.getString("responsable"));
                s.setEstado(rs.getString("estado"));
                lista.add(s);
            }
        }
    }

    return lista;
}


}

