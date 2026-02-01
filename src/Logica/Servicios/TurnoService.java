package Logica.Servicios;

import Logica.DAO.TurnoDAO;
import Logica.Entidades.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servicio de Turnos - Versión con Distribución Global entre Rutas
 * Implementa Anexos G, H, I con separación por sentido
 */
public class TurnoService {

    private final TurnoDAO dao = new TurnoDAO();

    // Constantes de distribución
    private static final int MIN_DIAS_SEMANA_POR_RUTA = 4;
    private static final int MAX_DIAS_SEMANA_POR_RUTA = 5;
    private static final int MIN_DIAS_MES_POR_RUTA = 19;
    private static final int MAX_DIAS_MES_POR_RUTA = 20;

    // =====================================================
    // 1. GENERAR PLAN MAESTRO MENSUAL (GLOBAL - TODAS LAS RUTAS)
    // =====================================================
    
    /**
     * Genera el plan maestro mensual para TODAS las rutas activas
     * Distribuye inteligentemente el trabajo entre rutas respetando límites
     */
    public String generarPlanMensual(int anio, int mes) {
        // Validar mes
        if (mes < 1 || mes > 12) {
            return "El mes debe estar entre 1 y 12.";
        }

        // Validar que no exista ya un plan
        if (dao.existePlanMensual(anio, mes)) {
            return "Ya existe un plan maestro generado para este mes y año. Si desea consultarlo, use la opción de consulta.";
        }

        // Obtener rutas activas
        List<String> rutasActivas = dao.obtenerRutasActivas();
        if (rutasActivas.isEmpty()) {
            return "No se puede generar el plan: no existen rutas activas en el sistema.";
        }

        // Obtener socios con buses activos
        List<String> sociosActivos = dao.obtenerSociosConBusesActivos();
        if (sociosActivos.isEmpty()) {
            return "No se puede generar el plan: no existen buses activos disponibles para operar.";
        }

        // Crear el plan principal
        TurnoPlanMensual plan = new TurnoPlanMensual(anio, mes);
        int idPlan = dao.insertarPlanMensual(plan);
        
        if (idPlan == -1) {
            return "No se pudo generar el plan maestro: error al crear el registro principal.";
        }

        // Calcular días del mes
        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasDelMes = yearMonth.lengthOfMonth();

        // Estructura de tracking global
        // socio → ruta+sentido → lista de fechas asignadas
        Map<String, Map<String, List<LocalDate>>> asignacionesGlobales = new HashMap<>();
        for (String socio : sociosActivos) {
            asignacionesGlobales.put(socio, new HashMap<>());
        }

        // Generar planificación global para todas las rutas
        List<RutaSentido> rutasSentidos = new ArrayList<>();
        for (String codigoRuta : rutasActivas) {
            rutasSentidos.add(new RutaSentido(codigoRuta, "A_B"));
            rutasSentidos.add(new RutaSentido(codigoRuta, "B_A"));
        }

        // Para cada ruta+sentido, crear registro y distribuir
        for (RutaSentido rs : rutasSentidos) {
            TurnoPlanMensualRuta planRuta = new TurnoPlanMensualRuta(idPlan, rs.codigoRuta, rs.sentido);
            int idPlanRuta = dao.insertarPlanRuta(planRuta);
            
            if (idPlanRuta == -1) {
                System.out.println("Error al insertar ruta " + rs.codigoRuta + " " + rs.sentido);
                continue;
            }

            // Asignar días respetando límites
            asignarDiasParaRuta(idPlanRuta, rs.codigoRuta, rs.sentido, anio, mes, diasDelMes, 
                                sociosActivos, asignacionesGlobales);
        }

        return "El plan maestro mensual fue generado correctamente para todas las rutas activas. " +
               "A partir de ahora podrá consultarlo, pero no modificarlo.";
    }

    /**
     * Asigna días para una ruta+sentido específica respetando límites globales
     */
    private void asignarDiasParaRuta(int idPlanRuta, String codigoRuta, String sentido,
                                       int anio, int mes, int diasDelMes,
                                       List<String> sociosDisponibles,
                                       Map<String, Map<String, List<LocalDate>>> asignacionesGlobales) {
        
        String rutaSentidoKey = codigoRuta + "_" + sentido;
        
        // Inicializar tracking para esta ruta+sentido
        for (String socio : sociosDisponibles) {
            asignacionesGlobales.get(socio).putIfAbsent(rutaSentidoKey, new ArrayList<>());
        }

        // Rotar socios
        int indiceSocio = 0;
        
        for (int dia = 1; dia <= diasDelMes; dia++) {
            LocalDate fecha = LocalDate.of(anio, mes, dia);
            
            // Buscar socio que pueda operar (no haya excedido límites en esta ruta)
            String socioAsignado = null;
            int intentos = 0;
            
            while (socioAsignado == null && intentos < sociosDisponibles.size()) {
                String candidato = sociosDisponibles.get(indiceSocio % sociosDisponibles.size());
                
                List<LocalDate> diasEnEstaRuta = asignacionesGlobales.get(candidato).get(rutaSentidoKey);
                
                // Verificar límite mensual por ruta
                if (diasEnEstaRuta.size() < MAX_DIAS_MES_POR_RUTA) {
                    // Verificar límite semanal por ruta
                    if (puedeOperarEnSemana(diasEnEstaRuta, fecha)) {
                        socioAsignado = candidato;
                        diasEnEstaRuta.add(fecha);
                    }
                }
                
                indiceSocio++;
                intentos++;
            }
            
            // Insertar detalle (opera o no)
            if (socioAsignado != null) {
                TurnoPlanMensualDetalle detalle = new TurnoPlanMensualDetalle(
                    idPlanRuta, socioAsignado, fecha, true
                );
                dao.insertarDetalle(detalle);
            }
        }
    }

    /**
     * Verifica si un socio puede operar en una fecha según límite semanal
     */
    private boolean puedeOperarEnSemana(List<LocalDate> diasAsignados, LocalDate nuevaFecha) {
        // Contar días en la semana de la nueva fecha
        LocalDate inicioSemana = nuevaFecha.with(DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(6);
        
        long diasEnSemana = diasAsignados.stream()
            .filter(f -> !f.isBefore(inicioSemana) && !f.isAfter(finSemana))
            .count();
        
        return diasEnSemana < MAX_DIAS_SEMANA_POR_RUTA;
    }

    // =====================================================
    // 2. CONSULTAR PLAN MENSUAL (ANEXO G)
    // =====================================================
    
    /**
     * Consulta plan mensual según Anexo G
     * Retorna mapa: fecha → socio
     */
    public Map<LocalDate, String> consultarPlanMensual(int anio, int mes, String codigoRuta, String sentido) {
        Map<LocalDate, String> resultado = new LinkedHashMap<>();
        
        TurnoPlanMensual plan = dao.obtenerPlanMensual(anio, mes);
        if (plan == null) {
            return resultado;
        }

        TurnoPlanMensualRuta planRuta = dao.obtenerPlanRuta(plan.getIdPlan(), codigoRuta, sentido);
        if (planRuta == null) {
            return resultado;
        }

        List<TurnoPlanMensualDetalle> detalles = dao.obtenerDetallesPorRuta(planRuta.getIdPlanRuta());
        
        for (TurnoPlanMensualDetalle det : detalles) {
            if (det.isOpera()) {
                resultado.put(det.getFechaOperacion(), det.getCodigoSocio());
            }
        }
        
        return resultado;
    }

    /**
     * Retorna estructura para Anexo G (tabla mensual)
     * Mapa: socio → (día → opera?)
     */
    public Map<String, Map<Integer, Boolean>> obtenerEstructuraAnexoG(int anio, int mes, 
                                                                        String codigoRuta, String sentido) {
        Map<String, Map<Integer, Boolean>> estructura = new LinkedHashMap<>();
        
        Map<LocalDate, String> plan = consultarPlanMensual(anio, mes, codigoRuta, sentido);
        
        if (plan.isEmpty()) {
            return estructura;
        }

        // Obtener todos los socios que operan
        Set<String> socios = new HashSet<>(plan.values());
        
        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasDelMes = yearMonth.lengthOfMonth();
        
        // Inicializar estructura
        for (String socio : socios) {
            Map<Integer, Boolean> dias = new LinkedHashMap<>();
            for (int dia = 1; dia <= diasDelMes; dia++) {
                dias.put(dia, false);
            }
            estructura.put(socio, dias);
        }
        
        // Marcar días que operan
        for (Map.Entry<LocalDate, String> entry : plan.entrySet()) {
            int dia = entry.getKey().getDayOfMonth();
            String socio = entry.getValue();
            estructura.get(socio).put(dia, true);
        }
        
        return estructura;
    }

    // =====================================================
    // 3. CONSULTAR PLAN SEMANAL (ANEXO H)
    // =====================================================
    
    /**
     * Consulta plan semanal para un rango de fechas
     * Retorna estructura para Anexo H
     */
    public Map<String, Map<DayOfWeek, Boolean>> consultarPlanSemanal(LocalDate fechaInicio, 
                                                                       String codigoRuta, String sentido) {
        Map<String, Map<DayOfWeek, Boolean>> estructura = new LinkedHashMap<>();
        
        int anio = fechaInicio.getYear();
        int mes = fechaInicio.getMonthValue();
        
        Map<LocalDate, String> planMensual = consultarPlanMensual(anio, mes, codigoRuta, sentido);
        
        if (planMensual.isEmpty()) {
            return estructura;
        }

        // Calcular rango semanal (lunes a domingo)
        LocalDate lunes = fechaInicio.with(DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);
        
        // Filtrar solo fechas de la semana
        Map<LocalDate, String> planSemanal = new LinkedHashMap<>();
        for (Map.Entry<LocalDate, String> entry : planMensual.entrySet()) {
            LocalDate fecha = entry.getKey();
            if (!fecha.isBefore(lunes) && !fecha.isAfter(domingo)) {
                planSemanal.put(fecha, entry.getValue());
            }
        }
        
        // Obtener socios de la semana
        Set<String> socios = new HashSet<>(planSemanal.values());
        
        // Inicializar estructura
        for (String socio : socios) {
            Map<DayOfWeek, Boolean> dias = new LinkedHashMap<>();
            for (DayOfWeek dia : DayOfWeek.values()) {
                dias.put(dia, false);
            }
            estructura.put(socio, dias);
        }
        
        // Marcar días que operan
        for (Map.Entry<LocalDate, String> entry : planSemanal.entrySet()) {
            DayOfWeek diaSemana = entry.getKey().getDayOfWeek();
            String socio = entry.getValue();
            estructura.get(socio).put(diaSemana, true);
        }
        
        return estructura;
    }

    // =====================================================
    // 4. CONSULTAR PLAN DIARIO (ANEXO I)
    // =====================================================
    
    /**
     * Consulta plan diario según Anexo I con horarios
     * Filtra por estado del bus (solo ACTIVO)
     */
    public List<TurnoOperacionDiaria> consultarPlanDiario(LocalDate fecha, String codigoRuta, String sentido) {
        List<TurnoOperacionDiaria> operaciones = new ArrayList<>();
        
        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();
        
        TurnoPlanMensual plan = dao.obtenerPlanMensual(anio, mes);
        if (plan == null) {
            return operaciones;
        }

        TurnoPlanMensualRuta planRuta = dao.obtenerPlanRuta(plan.getIdPlan(), codigoRuta, sentido);
        if (planRuta == null) {
            return operaciones;
        }

        List<String> sociosAsignados = dao.obtenerSociosOperandoEnFecha(planRuta.getIdPlanRuta(), fecha);
        
        // Filtrar solo socios con buses ACTIVOS
        List<String> sociosDisponibles = new ArrayList<>();
        for (String socio : sociosAsignados) {
            if (dao.socioTieneBusActivo(socio)) {
                sociosDisponibles.add(socio);
            }
        }

        if (sociosDisponibles.isEmpty()) {
            return operaciones;
        }

        // Obtener plantilla horaria
        Integer codigoPlantilla = dao.obtenerCodigoPlantillaDeRuta(codigoRuta);
        if (codigoPlantilla == null) {
            return operaciones;
        }

        String[] horarios = dao.obtenerHorariosPlantilla(codigoPlantilla);
        if (horarios == null || horarios.length < 2) {
            return operaciones;
        }

        LocalTime horaInicio = LocalTime.parse(horarios[0]);
        LocalTime horaFin = LocalTime.parse(horarios[1]);

        List<Integer> intervalos = dao.obtenerIntervalosPlantilla(codigoPlantilla);
        if (intervalos.isEmpty()) {
            return operaciones;
        }

        int intervaloPromedio = intervalos.stream().mapToInt(Integer::intValue).sum() / intervalos.size();

        Integer duracionRuta = dao.obtenerDuracionRuta(codigoRuta);
        if (duracionRuta == null || duracionRuta <= 0) {
            duracionRuta = 45;
        }

        // Generar horarios
        List<LocalTime> horariosSalida = new ArrayList<>();
        LocalTime horaSalida = horaInicio;
        
        while (horaSalida.isBefore(horaFin) || horaSalida.equals(horaFin)) {
            horariosSalida.add(horaSalida);
            horaSalida = horaSalida.plusMinutes(intervaloPromedio);
        }

        int numTurnos = Math.min(horariosSalida.size(), sociosDisponibles.size() * 4);
        
        for (int i = 0; i < numTurnos && i < horariosSalida.size(); i++) {
            String socio = sociosDisponibles.get(i % sociosDisponibles.size());
            LocalTime salida = horariosSalida.get(i);
            LocalTime llegada = salida.plusMinutes(duracionRuta);
            
            TurnoOperacionDiaria op = new TurnoOperacionDiaria();
            op.setFechaOperacion(fecha);
            op.setCodigoRuta(codigoRuta);
            op.setSentido(sentido);
            op.setCodigoSocio(socio);
            op.setHoraSalida(salida);
            op.setHoraLlegadaEstimada(llegada);
            
            operaciones.add(op);
        }

        return operaciones;
    }

    // =====================================================
    // UTILIDADES
    // =====================================================
    
    public boolean existePlan(int anio, int mes) {
        return dao.existePlanMensual(anio, mes);
    }

    public List<String> obtenerRutasActivas() {
        return dao.obtenerRutasActivas();
    }

    public List<String> obtenerSociosConBusesActivos() {
        return dao.obtenerSociosConBusesActivos();
    }

    // Clase interna auxiliar
    private static class RutaSentido {
        String codigoRuta;
        String sentido;
        
        RutaSentido(String codigoRuta, String sentido) {
            this.codigoRuta = codigoRuta;
            this.sentido = sentido;
        }
    }
}
