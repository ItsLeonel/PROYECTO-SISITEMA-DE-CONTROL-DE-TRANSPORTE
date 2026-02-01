package Logica.DAO;

import Logica.Conexiones.ConexionBD;
import Logica.Entidades.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TurnoDAO OPTIMIZADO
 * Usa bitmask para reducir de 1000+ registros a ~100 por mes
 */
public class TurnoDAO {

    // =====================================================
    // PLAN MENSUAL
    // =====================================================

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

    // =====================================================
    // DETALLE OPTIMIZADO CON BITMASK
    // =====================================================

    /**
     * Inserta detalle con bitmask (1 registro por socio)
     * diasMes: String de 31 caracteres con '1' (opera) o '0' (no opera)
     */
    public boolean insertarDetalleOptimizado(int idPlanRuta, String codigoSocio, String diasMes) {
        String sql = "INSERT INTO turno_plan_mensual_detalle (id_plan_ruta, codigo_socio, dias_mes, total_dias_asignados) "
                +
                "VALUES (?, ?, ?, ?)";

        // Contar días asignados
        int totalDias = 0;
        for (char c : diasMes.toCharArray()) {
            if (c == '1')
                totalDias++;
        }

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPlanRuta);
            ps.setString(2, codigoSocio);
            ps.setString(3, diasMes);
            ps.setInt(4, totalDias);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error insertarDetalleOptimizado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los detalles de una ruta en formato Map
     * Retorna: Map<codigo_socio, dias_mes_string>
     */
    public java.util.Map<String, String> obtenerDetallesPorRutaOptimizado(int idPlanRuta) {
        java.util.Map<String, String> resultado = new java.util.HashMap<>();
        String sql = "SELECT codigo_socio, dias_mes FROM turno_plan_mensual_detalle WHERE id_plan_ruta = ? ORDER BY codigo_socio";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPlanRuta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.put(rs.getString("codigo_socio"), rs.getString("dias_mes"));
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerDetallesPorRutaOptimizado: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Obtiene socios que operan en una fecha específica
     */
    public List<String> obtenerSociosOperandoEnFecha(int idPlanRuta, LocalDate fecha) {
        List<String> socios = new ArrayList<>();

        int dia = fecha.getDayOfMonth();
        int indice = dia - 1; // Índice 0-based

        String sql = "SELECT codigo_socio, dias_mes FROM turno_plan_mensual_detalle WHERE id_plan_ruta = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPlanRuta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String diasMes = rs.getString("dias_mes");

                // Verificar si opera ese día (índice dentro de rango y valor '1')
                if (indice < diasMes.length() && diasMes.charAt(indice) == '1') {
                    socios.add(rs.getString("codigo_socio"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerSociosOperandoEnFecha: " + e.getMessage());
        }

        return socios;
    }

    // =====================================================
    // MÉTODOS AUXILIARES - FILTRADO POR BASE
    // =====================================================

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

    public java.util.Map<String, String> obtenerRutasActivasMap() {
        java.util.Map<String, String> rutas = new java.util.LinkedHashMap<>();
        String sql = "SELECT codigo_ruta, nombre FROM ruta WHERE estado = 'Activo' ORDER BY codigo_ruta";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rutas.put(rs.getString("codigo_ruta"), rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerRutasActivasMap: " + e.getMessage());
        }

        return rutas;
    }

    public String obtenerNombreBase(int codigoBase) {
        String sql = "SELECT nombre FROM base_operativa WHERE codigo_base = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoBase);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerNombreBase: " + e.getMessage());
        }

        return null;
    }

    public int[] obtenerBasesDeRuta(String codigoRuta) {
        String sql = "SELECT codigo_base_a, codigo_base_b FROM ruta WHERE codigo_ruta = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoRuta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new int[] {
                        rs.getInt("codigo_base_a"),
                        rs.getInt("codigo_base_b")
                };
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerBasesDeRuta: " + e.getMessage());
        }

        return null;
    }

    public List<String> obtenerSociosPorBase(String nombreBase) {
        List<String> socios = new ArrayList<>();
        String sql = "SELECT DISTINCT codigo_socio_fk FROM buses WHERE estado = 'ACTIVO' AND base_asignada = ? ORDER BY codigo_socio_fk";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreBase);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                socios.add(rs.getString("codigo_socio_fk"));
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerSociosPorBase: " + e.getMessage());
        }

        return socios;
    }

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

    // =====================================================
    // PLANTILLAS Y HORARIOS (CORREGIDO)
    // =====================================================

    /**
     * CORREGIDO: Obtiene plantilla asociada a una ruta
     * Intenta dos métodos:
     * 1. Si plantilla_intervalo tiene codigo_ruta
     * 2. Si no, usa ruta.codigo_intervalo
     */
    public Integer obtenerCodigoPlantillaDeRuta(String codigoRuta) {
        // Método 1: Buscar por codigo_ruta en plantilla_intervalo (si existe el campo)
        String sql1 = """
                    SELECT codigo_plantilla
                    FROM plantilla_intervalo
                    WHERE codigo_ruta = ? AND estado = 'Activo'
                    LIMIT 1
                """;

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql1)) {

            ps.setString(1, codigoRuta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("codigo_plantilla");
            }

        } catch (SQLException e) {
            // Si falla, intentar método 2
            System.out.println("Método 1 falló, intentando método 2...");
        }

        // Método 2: Usar ruta.codigo_plantilla como FK
        String sql2 = """
                    SELECT r.codigo_plantilla
                    FROM ruta r
                    INNER JOIN plantilla_intervalo p ON r.codigo_plantilla = p.codigo_plantilla
                    WHERE r.codigo_ruta = ? AND r.estado = 'Activo' AND p.estado = 'Activo'
                """;

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql2)) {

            ps.setString(1, codigoRuta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("codigo_plantilla");
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerCodigoPlantillaDeRuta: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public String[] obtenerHorariosPlantilla(int codigoPlantilla) {
        String sql = "SELECT hora_inicio_operaciones, hora_fin_operaciones FROM plantilla_intervalo WHERE codigo_plantilla = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoPlantilla);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new String[] {
                        rs.getString("hora_inicio_operaciones"),
                        rs.getString("hora_fin_operaciones")
                };
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerHorariosPlantilla: " + e.getMessage());
        }

        return null;
    }

    public List<TurnoFranja> obtenerIntervalosPlantilla(int codigoPlantilla) {
        List<TurnoFranja> franjas = new ArrayList<>();
        String sql = "SELECT franja_id, tiempo_minutos FROM plantilla_intervalo_franja WHERE codigo_plantilla = ? ORDER BY franja_id";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, codigoPlantilla);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TurnoFranja f = new TurnoFranja();
                f.franjaId = rs.getInt("franja_id");
                f.intervalo = rs.getInt("tiempo_minutos");
                franjas.add(f);
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerIntervalosPlantilla: " + e.getMessage());
        }

        return franjas;
    }

    // Clase interna auxiliar para transportar datos
    public static class TurnoFranja {
        public int franjaId;
        public int intervalo;
    }

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