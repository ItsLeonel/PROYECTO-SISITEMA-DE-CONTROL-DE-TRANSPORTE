package Logica.DAO;

import Logica.Entidades.Socio;
import Logica.Entidades.SocioDisponible;
import Logica.Conexiones.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para el Módulo de Socios
 * Gestiona todas las operaciones CRUD sobre la tabla socios_propietarios
 * Implementa requisitos rso1v1.0 a ru6v1.1
 * Base de datos: MySQL
 */
public class SocioDAO {

    /**
     * rso1v1.0 - Registrar un socio propietario
     */
    public boolean registrarSocio(Socio socio) throws SQLException {
        String sql = "INSERT INTO socios_propietarios " +
                    "(codigo_socio, registro_municipal, cedula, nombres_completos, " +
                    "direccion, numero_celular, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, socio.getCodigoSocio());
            pstmt.setString(2, socio.getRegistroMunicipal());
            pstmt.setString(3, socio.getCedula());
            pstmt.setString(4, socio.getNombresCompletos());
            pstmt.setString(5, socio.getDireccion());
            pstmt.setString(6, socio.getNumeroCelular());
            pstmt.setString(7, socio.getEstado());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * rso2v1.0 - Consultar un socio por código
     */
 public Socio consultarSocioPorCodigo(String codigoSocio) throws SQLException {
    String sql = "SELECT s.*, b.placa AS placa_bus_asociado " +
                 "FROM socios_propietarios s " +
                 "LEFT JOIN buses b ON b.codigo_socio_fk = s.codigo_socio " +
                 "WHERE s.codigo_socio = ?";

    try (Connection conn = ConexionBD.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, codigoSocio);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return mapearSocio(rs);
        }
        return null;
    }
}


    /**
     * Verificar si existe un socio por código
     */
    public boolean existeSocioPorCodigo(String codigoSocio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM socios_propietarios WHERE codigo_socio = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigoSocio);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    /**
     * Verificar si existe un registro municipal
     */
    public boolean existeRegistroMunicipal(String registroMunicipal) throws SQLException {
        String sql = "SELECT COUNT(*) FROM socios_propietarios WHERE registro_municipal = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, registroMunicipal);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    /**
     * Verificar si existe una cédula
     */
    public boolean existeCedula(String cedula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM socios_propietarios WHERE cedula = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    /**
     * rso3v1.0 - Actualizar número de celular del socio
     */
    public boolean actualizarNumeroCelular(String codigoSocio, String nuevoNumeroCelular) 
            throws SQLException {
        String sql = "UPDATE socios_propietarios SET numero_celular = ? WHERE codigo_socio = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoNumeroCelular);
            pstmt.setString(2, codigoSocio);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * rso4v1.0 - Actualizar dirección del socio
     */
    public boolean actualizarDireccion(String codigoSocio, String nuevaDireccion) 
            throws SQLException {
        String sql = "UPDATE socios_propietarios SET direccion = ? WHERE codigo_socio = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevaDireccion);
            pstmt.setString(2, codigoSocio);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * ru5v1.0 - Listar todos los socios propietarios registrados
     */
    public List<Socio> listarSocios() throws SQLException {
        String sql = "SELECT s.*, b.placa AS placa_bus_asociado " +
                    "FROM socios_propietarios s " +
                    "LEFT JOIN buses b ON b.codigo_socio_fk = s.codigo_socio\r\n" + //
                                                " " +
                    "ORDER BY s.codigo_socio";
        
        List<Socio> socios = new ArrayList<>();
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                socios.add(mapearSocio(rs));
            }
        }
        
        return socios;
    }

    /**
     * Listar socios por estado
     */
    public List<Socio> listarSociosPorEstado(String estado) throws SQLException {
        String sql = "SELECT s.*, b.placa AS placa_bus_asociado " +
                    "FROM socios_propietarios s " +
                    "LEFT JOIN buses b ON b.codigo_socio_fk = s.codigo_socio\r\n" + //
                                                " " +
                    "WHERE s.estado = ? " +
                    "ORDER BY s.codigo_socio";
        
        List<Socio> socios = new ArrayList<>();
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                socios.add(mapearSocio(rs));
            }
        }
        
        return socios;
    }

    /**
     * Actualizar estado del socio
     */
    public boolean actualizarEstado(String codigoSocio, String nuevoEstado) throws SQLException {
        String sql = "UPDATE socios_propietarios SET estado = ? WHERE codigo_socio = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado);
            pstmt.setString(2, codigoSocio);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Mapear ResultSet a objeto Socio
     */
    private Socio mapearSocio(ResultSet rs) throws SQLException {
        Socio socio = new Socio();
        socio.setCodigoSocio(rs.getString("codigo_socio"));
        socio.setRegistroMunicipal(rs.getString("registro_municipal"));
        socio.setCedula(rs.getString("cedula"));
        socio.setNombresCompletos(rs.getString("nombres_completos"));
        socio.setDireccion(rs.getString("direccion"));
        socio.setNumeroCelular(rs.getString("numero_celular"));
        socio.setEstado(rs.getString("estado"));
        
        // Placa del bus asociado (puede ser null si no tiene bus asignado)
        try {
            socio.setPlacaBusAsociado(rs.getString("placa_bus_asociado"));
        } catch (SQLException e) {
            socio.setPlacaBusAsociado(null);
        }
        
        return socio;
    }
    public boolean existeCodigoSocio(String codigoSocio) throws SQLException {
    String sql = "SELECT 1 FROM socios_propietarios WHERE codigo_socio = ?";

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, codigoSocio);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
public List<SocioDisponible> obtenerSociosDisponibles() throws SQLException {
    String sql = """
        SELECT 
            s.codigo_socio,
            s.registro_municipal,
            s.nombres_completos
        FROM socios_propietarios s
        WHERE s.codigo_socio NOT IN (
            SELECT codigo_socio_fk FROM buses
        )
        AND s.estado = 'ACTIVO'
        ORDER BY s.nombres_completos
    """;

    List<SocioDisponible> socios = new ArrayList<>();

    try (Connection cn = ConexionBD.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            SocioDisponible s = new SocioDisponible();
            s.setCodigoSocio(rs.getString("codigo_socio"));
            s.setRegistroMunicipal(rs.getString("registro_municipal"));
            s.setNombresCompletos(rs.getString("nombres_completos"));
            socios.add(s);
        }
    }
    return socios;
}



}