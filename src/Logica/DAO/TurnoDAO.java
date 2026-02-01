package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestión de turnos (plan mensual y operación diaria)
 */
public class TurnoDAO {

    // =====================================================
    // PLAN MENSUAL - CRUD BÁSICO
    // =====================================================
    
    /**
     * Verifica si ya existe un plan para el mes/año especificado
     */
    public boolean existePlanMensual(int anio, int mes) {
        String sql = "SELECT 1 FROM turno_plan_mensual WHERE anio = ? AND mes = ? LIMIT 1";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, anio);
            ps.setInt(2, mes);
            
            return ps.executeQuery().next();
            
        } catch (SQLException e) {
            System.out.println("Error existePlanMensual: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta un nuevo plan mensual y retorna su ID generado
     */
    public int insertarPlanMensual(TurnoPlanMensual plan) {
        String sql = "INSERT INTO turno_plan_mensual (anio, mes, fecha_generacion) VALUES (?, ?, NOW())";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, plan.getAnio());
            ps.setInt(2, plan.getMes());
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error insertarPlanMensual: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Obtiene el plan mensual por mes/año
     */
    public TurnoPlanMensual obtenerPlanMensual(int anio, int mes) {
        String sql = "SELECT * FROM turno_plan_mensual WHERE anio = ? AND mes = ?";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, anio);
            ps.setInt(2, mes);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                TurnoPlanMensual plan = new TurnoPlanMensual();
                plan.setIdPlan(rs.getInt("id_plan"));
                plan.setAnio(rs.getInt("anio"));
                plan.setMes(rs.getInt("mes"));
                plan.setFechaGeneracion(rs.getTimestamp("fecha_generacion").toLocalDateTime());
                return plan;
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerPlanMensual: " + e.getMessage());
        }
        
        return null;
    }

    // =====================================================
    // RUTAS DEL PLAN
    // =====================================================
    
    /**
     * Inserta una ruta en el plan mensual
     */
    public int insertarPlanRuta(TurnoPlanMensualRuta planRuta) {
        String sql = "INSERT INTO turno_plan_mensual_ruta (id_plan, codigo_ruta, sentido) VALUES (?, ?, ?)";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, planRuta.getIdPlan());
            ps.setString(2, planRuta.getCodigoRuta());
            ps.setString(3, planRuta.getSentido());
            
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error insertarPlanRuta: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Obtiene las rutas asociadas a un plan mensual
     */
    public List<TurnoPlanMensualRuta> obtenerRutasDePlan(int idPlan) {
        List<TurnoPlanMensualRuta> lista = new ArrayList<>();
        String sql = "SELECT * FROM turno_plan_mensual_ruta WHERE id_plan = ?";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPlan);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TurnoPlanMensualRuta pr = new TurnoPlanMensualRuta();
                pr.setIdPlanRuta(rs.getInt("id_plan_ruta"));
                pr.setIdPlan(rs.getInt("id_plan"));
                pr.setCodigoRuta(rs.getString("codigo_ruta"));
                pr.setSentido(rs.getString("sentido"));
                lista.add(pr);
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerRutasDePlan: " + e.getMessage());
        }
        
        return lista;
    }

    /**
     * Busca una ruta específica del plan por ruta y sentido
     */
    public TurnoPlanMensualRuta obtenerPlanRuta(int idPlan, String codigoRuta, String sentido) {
        String sql = "SELECT * FROM turno_plan_mensual_ruta WHERE id_plan = ? AND codigo_ruta = ? AND sentido = ?";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPlan);
            ps.setString(2, codigoRuta);
            ps.setString(3, sentido);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                TurnoPlanMensualRuta pr = new TurnoPlanMensualRuta();
                pr.setIdPlanRuta(rs.getInt("id_plan_ruta"));
                pr.setIdPlan(rs.getInt("id_plan"));
                pr.setCodigoRuta(rs.getString("codigo_ruta"));
                pr.setSentido(rs.getString("sentido"));
                return pr;
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerPlanRuta: " + e.getMessage());
        }
        
        return null;
    }
    // DETALLE DEL PLAN (ASIGNACIONES DIARIAS)
    // =====================================================
    
    /**
     * Inserta un detalle (asignación diaria)
     */
    public boolean insertarDetalle(TurnoPlanMensualDetalle detalle) {
        String sql = "INSERT INTO turno_plan_mensual_detalle (id_plan_ruta, codigo_socio, fecha_operacion, opera) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, detalle.getIdPlanRuta());
            ps.setString(2, detalle.getCodigoSocio());
            ps.setDate(3, Date.valueOf(detalle.getFechaOperacion()));
            ps.setBoolean(4, detalle.isOpera());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error insertarDetalle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene los detalles (asignaciones) de una ruta específica del plan
     */
    public List<TurnoPlanMensualDetalle> obtenerDetallesPorRuta(int idPlanRuta) {
        List<TurnoPlanMensualDetalle> lista = new ArrayList<>();
        String sql = "SELECT * FROM turno_plan_mensual_detalle WHERE id_plan_ruta = ? ORDER BY fecha_operacion, codigo_socio";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPlanRuta);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TurnoPlanMensualDetalle det = new TurnoPlanMensualDetalle();
                det.setIdPlanRuta(rs.getInt("id_plan_ruta"));
                det.setCodigoSocio(rs.getString("codigo_socio"));
                det.setFechaOperacion(rs.getDate("fecha_operacion").toLocalDate());
                det.setOpera(rs.getBoolean("opera"));
                lista.add(det);
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerDetallesPorRuta: " + e.getMessage());
        }
        
        return lista;
    }

    /**
     * Obtiene detalles de un plan completo (todas las rutas)
     * Útil para consulta mensual
     */
    public List<TurnoPlanMensualDetalle> obtenerDetallesPorPlan(int idPlan) {
        List<TurnoPlanMensualDetalle> lista = new ArrayList<>();
        String sql = """
            SELECT d.* 
            FROM turno_plan_mensual_detalle d
            INNER JOIN turno_plan_mensual_ruta r ON d.id_plan_ruta = r.id_plan_ruta
            WHERE r.id_plan = ?
            ORDER BY d.fecha_operacion, d.codigo_socio
        """;
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPlan);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TurnoPlanMensualDetalle det = new TurnoPlanMensualDetalle();
                det.setIdPlanRuta(rs.getInt("id_plan_ruta"));
                det.setCodigoSocio(rs.getString("codigo_socio"));
                det.setFechaOperacion(rs.getDate("fecha_operacion").toLocalDate());
                det.setOpera(rs.getBoolean("opera"));
                lista.add(det);
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerDetallesPorPlan: " + e.getMessage());
        }
        
        return lista;
    }

    /**
     * Obtiene los socios que operan en una fecha específica para una ruta/sentido
     */
    public List<String> obtenerSociosOperandoEnFecha(int idPlanRuta, LocalDate fecha) {
        List<String> socios = new ArrayList<>();
        String sql = """
            SELECT codigo_socio 
            FROM turno_plan_mensual_detalle 
            WHERE id_plan_ruta = ? AND fecha_operacion = ? AND opera = 1
            ORDER BY codigo_socio
        """;
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPlanRuta);
            ps.setDate(2, Date.valueOf(fecha));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                socios.add(rs.getString("codigo_socio"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerSociosOperandoEnFecha: " + e.getMessage());
        }
        
        return socios;
    }

    // =====================================================
    // AUXILIARES - OBTENER DATOS DE OTRAS TABLAS
    // =====================================================
    
    /**
     * Obtiene todas las rutas activas
     */
    public List<String> obtenerRutasActivas() {
        List<String> rutas = new ArrayList<>();
        String sql = "SELECT codigo_ruta FROM ruta WHERE estado = 'Activo' ORDER BY codigo_ruta";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                rutas.add(rs.getString("codigo_ruta"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerRutasActivas: " + e.getMessage());
        }
        
        return rutas;
    }

    /**
     * Obtiene todos los socios con buses activos
     */
    public List<String> obtenerSociosConBusesActivos() {
        List<String> socios = new ArrayList<>();
        String sql = """
            SELECT DISTINCT codigo_socio_fk 
            FROM buses 
            WHERE estado = 'ACTIVO' 
            ORDER BY codigo_socio_fk
        """;
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                socios.add(rs.getString("codigo_socio_fk"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerSociosConBusesActivos: " + e.getMessage());
        }
        
        return socios;
    }

    /**
     * Verifica si un socio tiene al menos un bus activo
     */
    public boolean socioTieneBusActivo(String codigoSocio) {
        String sql = "SELECT 1 FROM buses WHERE codigo_socio_fk = ? AND estado = 'ACTIVO' LIMIT 1";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codigoSocio);
            return ps.executeQuery().next();
            
        } catch (SQLException e) {
            System.out.println("Error socioTieneBusActivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la plantilla horaria activa asociada a una ruta
     */
    public Integer obtenerCodigoPlantillaDeRuta(String codigoRuta) {
        String sql = """
            SELECT r.codigo_intervalo 
            FROM ruta r
            INNER JOIN plantilla_intervalo p ON r.codigo_intervalo = p.codigo_plantilla
            WHERE r.codigo_ruta = ? AND r.estado = 'Activo' AND p.estado = 'Activo'
        """;
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codigoRuta);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("codigo_intervalo");
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerCodigoPlantillaDeRuta: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Obtiene horarios de una plantilla
     */
    public String[] obtenerHorariosPlantilla(int codigoPlantilla) {
        String sql = "SELECT hora_inicio_operaciones, hora_fin_operaciones FROM plantilla_intervalo WHERE codigo_plantilla = ?";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, codigoPlantilla);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    rs.getString("hora_inicio_operaciones"),
                    rs.getString("hora_fin_operaciones")
                };
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerHorariosPlantilla: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Obtiene los intervalos por franja de una plantilla
     */
    public List<Integer> obtenerIntervalosPlantilla(int codigoPlantilla) {
        List<Integer> intervalos = new ArrayList<>();
        String sql = "SELECT tiempo_minutos FROM plantilla_intervalo_franja WHERE codigo_plantilla = ? ORDER BY franja_id";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, codigoPlantilla);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                intervalos.add(rs.getInt("tiempo_minutos"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerIntervalosPlantilla: " + e.getMessage());
        }
        
        return intervalos;
    }

    /**
     * Obtiene la duración estimada de una ruta
     */
    public Integer obtenerDuracionRuta(String codigoRuta) {
        String sql = "SELECT duracion_estimada_minutos FROM ruta WHERE codigo_ruta = ?";
        
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, codigoRuta);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("duracion_estimada_minutos");
            }
            
        } catch (SQLException e) {
            System.out.println("Error obtenerDuracionRuta: " + e.getMessage());
        }
        
        return null;
    }
}
