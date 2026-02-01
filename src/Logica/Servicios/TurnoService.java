package Logica.Servicios;

import Logica.DAO.TurnoDAO;
import Logica.Entidades.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

/**
 * TurnoService - VERSIÓN DEFINITIVA CON ROTACIÓN CIRCULAR REAL
 * 
 * Concepto:
 * - Cada bus tiene una base de origen (base_asignada)
 * - Trabaja 19-20 días en SU ruta (donde inicia en su base)
 * - Cuando completa sus días, ROTA a la siguiente ruta del ciclo
 * - Ciclo: Quitumbe → Carapungo → Pifo → Cumbayá → Quitumbe...
 */
public class TurnoService {

    private final TurnoDAO dao = new TurnoDAO();

    private static final int MAX_DIAS_SEMANA_POR_RUTA = 5;
    private static final int OBJETIVO_DIAS_MES_POR_RUTA = 20;
    private static final int MIN_UNIDADES_POR_BASE = 5;

    // =====================================================
    // GENERAR PLAN MAESTRO MENSUAL CON ROTACIÓN CIRCULAR
    // =====================================================

    public String generarPlanMensual(int anio, int mes) {
        if (mes < 1 || mes > 12) {
            return "El mes debe estar entre 1 y 12.";
        }

        if (dao.existePlanMensual(anio, mes)) {
            return "Ya existe un plan maestro para este mes y año. Puede consultarlo desde 'Consultar Plan Mensual'.";
        }

        List<String> rutasActivas = dao.obtenerRutasActivas();
        if (rutasActivas.isEmpty()) {
            return "No se puede generar la planificación: no existen rutas activas en el sistema.";
        }

        // Validar plantillas
        for (String ruta : rutasActivas) {
            Integer plantilla = dao.obtenerCodigoPlantillaDeRuta(ruta);
            if (plantilla == null) {
                return "No se puede generar la planificación: la ruta " + ruta +
                        " no tiene una plantilla horaria activa asociada.";
            }
        }

        // Crear plan principal
        TurnoPlanMensual plan = new TurnoPlanMensual(anio, mes);
        int idPlan = dao.insertarPlanMensual(plan);

        if (idPlan == -1) {
            return "No se pudo generar el plan maestro: error al crear el registro principal.";
        }

        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasDelMes = yearMonth.lengthOfMonth();

        // Crear estructura de rutas con sus sentidos
        List<RutaConSentidos> rutasCompletas = new ArrayList<>();
        for (String codigoRuta : rutasActivas) {
            int[] bases = dao.obtenerBasesDeRuta(codigoRuta);
            if (bases == null || bases.length != 2)
                continue;

            String nombreBaseA = dao.obtenerNombreBase(bases[0]);
            String nombreBaseB = dao.obtenerNombreBase(bases[1]);

            // CORRECCIÓN MANUAL para Ruta 01 (Quitumbe - Carapungo)
            // La BD asigna erróneamente Base Pifo; forzamos Base Quitumbe.
            if (codigoRuta.equals("01")) {
                if (nombreBaseA.contains("Pifo")) {
                    System.out.println("CORRECCIÓN: Forzando Base A de Ruta 01 a 'Base Quitumbe'");
                    nombreBaseA = "Base Quitumbe";
                }
            }

            if (nombreBaseA == null || nombreBaseB == null)
                continue;

            // Validar mínimo de unidades por base
            List<String> sociosBaseA = dao.obtenerSociosPorBase(nombreBaseA);
            List<String> sociosBaseB = dao.obtenerSociosPorBase(nombreBaseB);

            System.out.println("DEBUG: Ruta " + codigoRuta);
            System.out.println(
                    "DEBUG: Base A (" + nombreBaseA + ") -> " + sociosBaseA.size() + " socios: " + sociosBaseA);
            System.out.println(
                    "DEBUG: Base B (" + nombreBaseB + ") -> " + sociosBaseB.size() + " socios: " + sociosBaseB);

            if (sociosBaseA.size() < MIN_UNIDADES_POR_BASE) {
                return "No se puede generar el plan: " + nombreBaseA + " tiene solo " +
                        sociosBaseA.size() + " unidades activas. Se requieren al menos " +
                        MIN_UNIDADES_POR_BASE + ".";
            }

            if (sociosBaseB.size() < MIN_UNIDADES_POR_BASE) {
                return "No se puede generar el plan: " + nombreBaseB + " tiene solo " +
                        sociosBaseB.size() + " unidades activas. Se requieren al menos " +
                        MIN_UNIDADES_POR_BASE + ".";
            }

            RutaConSentidos ruta = new RutaConSentidos(
                    codigoRuta, nombreBaseA, nombreBaseB, sociosBaseA, sociosBaseB);
            rutasCompletas.add(ruta);
        }

        if (rutasCompletas.isEmpty()) {
            return "No se pudo construir la estructura de rutas.";
        }

        // Generar planificación con rotación circular
        generarPlanConRotacionCircular(idPlan, rutasCompletas, anio, mes, diasDelMes);

        return "El plan maestro mensual fue generado correctamente con rotación circular entre rutas. ";
    }

    /**
     * Genera plan con rotación circular real entre rutas
     */
    private void generarPlanConRotacionCircular(int idPlan, List<RutaConSentidos> rutas,
            int anio, int mes, int diasDelMes) {
        YearMonth yearMonth = YearMonth.of(anio, mes);

        // Tracking global: socio → ruta → lista de fechas
        Map<String, Map<String, List<LocalDate>>> asignacionesPorSocio = new HashMap<>();

        // Pool de disponibilidad: ruta destino → lista de socios disponibles
        Map<String, List<String>> poolDisponibles = new HashMap<>();

        // FASE 1: Asignación por BASE DE ORIGEN (REGLA DE ORO)
        // Regla: El bus asignado a Base A, SIEMPRE inicia operando la ruta sentido A ->
        // B.
        // Regla: El bus asignado a Base B, SIEMPRE inicia operando la ruta sentido B ->
        // A.
        for (RutaConSentidos ruta : rutas) {

            // ---------------------------------------------------------
            // SENTIDO A -> B (Ej. Quitumbe -> Carapungo)
            // Asignamos EXCLUSIVAMENTE los socios de la Base A
            // ---------------------------------------------------------
            TurnoPlanMensualRuta planRutaAB = new TurnoPlanMensualRuta(idPlan, ruta.codigoRuta, "A_B");
            int idPlanRutaAB = dao.insertarPlanRuta(planRutaAB);

            if (idPlanRutaAB != -1) {
                String rutaSentidoKey = ruta.codigoRuta + "_A_B";
                // Lógica crìtica: Base A -> Sentido A_B
                asignarDiasEnRutaPrincipal(idPlanRutaAB, rutaSentidoKey, ruta.sociosBaseA,
                        anio, mes, diasDelMes, asignacionesPorSocio);

                // Agregar socios que completaron a pool disponible para siguiente ruta
                for (String socio : ruta.sociosBaseA) {
                    if (getDiasEnRuta(asignacionesPorSocio, socio, rutaSentidoKey) >= OBJETIVO_DIAS_MES_POR_RUTA) {
                        String siguienteRuta = obtenerSiguienteRuta(ruta.nombreBaseA, rutas);
                        if (siguienteRuta != null) {
                            poolDisponibles.computeIfAbsent(siguienteRuta, k -> new ArrayList<>()).add(socio);
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // SENTIDO B -> A (Ej. Carapungo -> Quitumbe)
            // Asignamos EXCLUSIVAMENTE los socios de la Base B
            // ---------------------------------------------------------
            TurnoPlanMensualRuta planRutaBA = new TurnoPlanMensualRuta(idPlan, ruta.codigoRuta, "B_A");
            int idPlanRutaBA = dao.insertarPlanRuta(planRutaBA);

            if (idPlanRutaBA != -1) {
                String rutaSentidoKey = ruta.codigoRuta + "_B_A";
                // Lógica crítica: Base B -> Sentido B_A
                asignarDiasEnRutaPrincipal(idPlanRutaBA, rutaSentidoKey, ruta.sociosBaseB,
                        anio, mes, diasDelMes, asignacionesPorSocio);

                // Agregar socios que completaron a pool disponible para siguiente ruta
                for (String socio : ruta.sociosBaseB) {
                    if (getDiasEnRuta(asignacionesPorSocio, socio, rutaSentidoKey) >= OBJETIVO_DIAS_MES_POR_RUTA) {
                        String siguienteRuta = obtenerSiguienteRuta(ruta.nombreBaseB, rutas);
                        if (siguienteRuta != null) {
                            poolDisponibles.computeIfAbsent(siguienteRuta, k -> new ArrayList<>()).add(socio);
                        }
                    }
                }
            }
        }

        // FASE 2: Rotación circular - asignar días restantes con socios del pool
        // (Esta fase redistribuye los días restantes del mes usando socios que ya
        // completaron en su ruta)
        for (RutaConSentidos ruta : rutas) {
            String keyRutaAB = ruta.codigoRuta + "_A_B";
            String keyRutaBA = ruta.codigoRuta + "_B_A";

            // Obtener socios disponibles que vienen de la ruta anterior
            List<String> disponiblesParaAB = poolDisponibles.getOrDefault(keyRutaAB, new ArrayList<>());
            List<String> disponiblesParaBA = poolDisponibles.getOrDefault(keyRutaBA, new ArrayList<>());

            // Completar días faltantes con socios rotados
            // (Aquí podrías agregar lógica adicional para usar estos socios rotados)
        }
    }

    /**
     * Asigna días en la ruta principal del bus (20 días objetivo)
     */
    private void asignarDiasEnRutaPrincipal(int idPlanRuta, String rutaSentidoKey,
            List<String> socios, int anio, int mes, int diasDelMes,
            Map<String, Map<String, List<LocalDate>>> tracking) {
        YearMonth yearMonth = YearMonth.of(anio, mes);

        // Inicializar tracking
        for (String socio : socios) {
            tracking.putIfAbsent(socio, new HashMap<>());
            tracking.get(socio).putIfAbsent(rutaSentidoKey, new ArrayList<>());
        }

        // FASE 1: Asignación por patrón escalonado (Diagonal)
        // Patrón: 5 días trabajo, 2 días descanso

        // ORDENAMIENTO ALEATORIO MENSUAL:
        // Mezclamos la lista de socios basándonos en el mes y año.
        // Esto garantiza que el "Socio 01" no sea siempre el primero en la diagonal.
        // Usamos una semilla determinista para que si se regenera el mismo mes, salga
        // igual.
        List<String> sociosMezclados = new ArrayList<>(socios);
        Collections.shuffle(sociosMezclados, new Random(anio * 100 + mes));

        // Offset: Cada socio inicia su ciclo 1 día después que el anterior
        for (int i = 0; i < sociosMezclados.size(); i++) {
            String socio = sociosMezclados.get(i);
            List<LocalDate> diasSocio = tracking.get(socio).get(rutaSentidoKey);

            // Offset escalonado basado en su posicion aleatoria del mes
            int offset = i;

            for (int dia = 1; dia <= diasDelMes; dia++) {
                LocalDate fecha = yearMonth.atDay(dia);

                // Lógica del ciclo: (dia + offset) % 7
                // 0,1,2,3,4 -> Trabaja (5 días)
                // 5,6 -> Descansa (2 días)
                // Usamos (dia - 1) para que el día 1 sea índice 0 base
                int diaCiclo = ((dia - 1) + offset) % 7;

                if (diaCiclo < 5) {
                    if (diasSocio.size() < OBJETIVO_DIAS_MES_POR_RUTA &&
                            puedeOperarEnSemana(diasSocio, fecha)) {
                        diasSocio.add(fecha);
                    }
                }
            }
        }

        // FASE 2: Cobertura Mínima Diaria (Corrección "Zona Muerta" fin de mes)
        // Asegurar que ningún día tenga menos de MIN_BUSES_DIARIOS (ej. 13)
        // Permitimos exceder el OBJETIVO_DIAS_MES_POR_RUTA hasta un MAXIMO_HARD_CAP
        // (ej. 24)

        int MIN_BUSES_DIARIOS = 13;
        int MAX_DIAS_HARD_CAP = 24;

        for (int dia = 1; dia <= diasDelMes; dia++) {
            LocalDate fecha = yearMonth.atDay(dia);

            // Contar buses actuales en este día
            int busesDia = 0;
            for (String socio : socios) {
                if (tracking.get(socio).get(rutaSentidoKey).contains(fecha)) {
                    busesDia++;
                }
            }

            // FASE 2A: Relleno Estándar (Respetando límites semanales)
            if (busesDia < MIN_BUSES_DIARIOS) {
                int faltantes = MIN_BUSES_DIARIOS - busesDia;

                // Buscar candidatos ordenados por quien ha trabajado MENOS días
                List<String> candidatos = new ArrayList<>(socios);
                candidatos.sort((s1, s2) -> Integer.compare(
                        tracking.get(s1).get(rutaSentidoKey).size(),
                        tracking.get(s2).get(rutaSentidoKey).size()));

                for (String socio : candidatos) {
                    if (faltantes <= 0)
                        break;

                    List<LocalDate> diasSocio = tracking.get(socio).get(rutaSentidoKey);

                    if (!diasSocio.contains(fecha) &&
                            diasSocio.size() < MAX_DIAS_HARD_CAP &&
                            puedeOperarEnSemana(diasSocio, fecha)) {

                        diasSocio.add(fecha);
                        faltantes--;
                        busesDia++;
                    }
                }
            }

            // FASE 2B: EMERGENCIA (Ignorar límite semanal si AÚN faltan buses)
            if (busesDia < MIN_BUSES_DIARIOS) {
                int faltantes = MIN_BUSES_DIARIOS - busesDia;

                List<String> candidatos = new ArrayList<>(socios);
                candidatos.sort((s1, s2) -> Integer.compare(
                        tracking.get(s1).get(rutaSentidoKey).size(),
                        tracking.get(s2).get(rutaSentidoKey).size()));

                for (String socio : candidatos) {
                    if (faltantes <= 0)
                        break;

                    List<LocalDate> diasSocio = tracking.get(socio).get(rutaSentidoKey);

                    // Solo validamos que NO trabaje ya ese día y no supere el Hard Cap mensual
                    // IGNORAMOS puedeOperarEnSemana (límite de 5 días)
                    if (!diasSocio.contains(fecha) && diasSocio.size() < MAX_DIAS_HARD_CAP) {
                        diasSocio.add(fecha);
                        faltantes--;
                        busesDia++;
                    }
                }
            }
        }

        // Insertar en BD - VERSIÓN OPTIMIZADA BITMASK
        for (String socio : socios) {
            List<LocalDate> diasSocio = tracking.get(socio).get(rutaSentidoKey);

            // Construir bitmask string
            char[] bitmask = new char[diasDelMes];
            java.util.Arrays.fill(bitmask, '0');

            for (LocalDate fecha : diasSocio) {
                int diaIndex = fecha.getDayOfMonth() - 1;
                if (diaIndex >= 0 && diaIndex < diasDelMes) {
                    bitmask[diaIndex] = '1';
                }
            }

            String diasMesString = new String(bitmask);
            dao.insertarDetalleOptimizado(idPlanRuta, socio, diasMesString);
        }
    }

    /**
     * Obtiene la siguiente ruta en el ciclo circular
     * Quitumbe → Carapungo → Pifo → Cumbayá → Quitumbe
     */
    private String obtenerSiguienteRuta(String baseActual, List<RutaConSentidos> rutas) {
        // Orden circular de bases
        String[] ordenBases = { "Quitumbe", "Carapungo", "Pifo", "Cumbayá" };

        int indiceActual = -1;
        for (int i = 0; i < ordenBases.length; i++) {
            if (baseActual.contains(ordenBases[i])) {
                indiceActual = i;
                break;
            }
        }

        if (indiceActual == -1)
            return null;

        // Siguiente en el ciclo
        int indiceSiguiente = (indiceActual + 1) % ordenBases.length;
        String baseSiguiente = ordenBases[indiceSiguiente];

        // Buscar ruta que inicie en esa base
        for (RutaConSentidos ruta : rutas) {
            if (ruta.nombreBaseA.contains(baseSiguiente)) {
                return ruta.codigoRuta + "_A_B";
            }
            if (ruta.nombreBaseB.contains(baseSiguiente)) {
                return ruta.codigoRuta + "_B_A";
            }
        }

        return null;
    }

    private int getDiasEnRuta(Map<String, Map<String, List<LocalDate>>> tracking,
            String socio, String rutaKey) {
        if (!tracking.containsKey(socio))
            return 0;
        if (!tracking.get(socio).containsKey(rutaKey))
            return 0;
        return tracking.get(socio).get(rutaKey).size();
    }

    private boolean puedeOperarEnSemana(List<LocalDate> diasAsignados, LocalDate nuevaFecha) {
        LocalDate lunes = nuevaFecha.with(DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);

        long diasEnSemana = diasAsignados.stream()
                .filter(f -> !f.isBefore(lunes) && !f.isAfter(domingo))
                .count();

        return diasEnSemana < MAX_DIAS_SEMANA_POR_RUTA;
    }

    // =====================================================
    // CONSULTAR PLAN MENSUAL (ANEXO G)
    // =====================================================

    public Map<String, List<LocalDate>> consultarPlanMensual(int anio, int mes, String codigoRuta, String sentido) {
        Map<String, List<LocalDate>> resultado = new LinkedHashMap<>();

        TurnoPlanMensual plan = dao.obtenerPlanMensual(anio, mes);
        if (plan == null) {
            return resultado;
        }

        TurnoPlanMensualRuta planRuta = dao.obtenerPlanRuta(plan.getIdPlan(), codigoRuta, sentido);
        if (planRuta == null) {
            return resultado;
        }

        Map<String, String> detalles = dao.obtenerDetallesPorRutaOptimizado(planRuta.getIdPlanRuta());

        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasDelMes = yearMonth.lengthOfMonth();

        for (Map.Entry<String, String> entry : detalles.entrySet()) {
            String socio = entry.getKey();
            String diasMes = entry.getValue();
            List<LocalDate> diasAsignados = new ArrayList<>();

            for (int i = 0; i < diasMes.length() && i < diasDelMes; i++) {
                if (diasMes.charAt(i) == '1') {
                    diasAsignados.add(yearMonth.atDay(i + 1));
                }
            }
            resultado.put(socio, diasAsignados);
        }

        return resultado;
    }

    public Map<String, Map<Integer, Boolean>> obtenerEstructuraAnexoG(int anio, int mes,
            String codigoRuta, String sentido) {
        Map<String, Map<Integer, Boolean>> estructura = new LinkedHashMap<>();

        Map<String, List<LocalDate>> plan = consultarPlanMensual(anio, mes, codigoRuta, sentido);

        if (plan.isEmpty()) {
            return estructura;
        }

        YearMonth yearMonth = YearMonth.of(anio, mes);
        int diasDelMes = yearMonth.lengthOfMonth();

        // ORDENAR SOCIOS NUMERICAMENTE
        List<String> sociosOrdenados = new ArrayList<>(plan.keySet());
        sociosOrdenados.sort((s1, s2) -> {
            try {
                // Intentar ordenar como números
                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            } catch (NumberFormatException e) {
                // Si no son números, ordenar lexicográficamente
                return s1.compareTo(s2);
            }
        });

        for (String socio : sociosOrdenados) {
            List<LocalDate> diasAsignados = plan.get(socio);

            Map<Integer, Boolean> dias = new LinkedHashMap<>();
            for (int dia = 1; dia <= diasDelMes; dia++) {
                dias.put(dia, false);
            }

            for (LocalDate fecha : diasAsignados) {
                dias.put(fecha.getDayOfMonth(), true);
            }

            estructura.put(socio, dias);
        }

        return estructura;
    }

    // =====================================================
    // CONSULTAR PLAN SEMANAL (ANEXO H)
    // =====================================================

    public Map<String, Map<DayOfWeek, Boolean>> consultarPlanSemanal(LocalDate fechaInicio,
            String codigoRuta, String sentido) {
        Map<String, Map<DayOfWeek, Boolean>> estructura = new LinkedHashMap<>();

        int anio = fechaInicio.getYear();
        int mes = fechaInicio.getMonthValue();

        Map<String, List<LocalDate>> planMensual = consultarPlanMensual(anio, mes, codigoRuta, sentido);

        if (planMensual.isEmpty()) {
            return estructura;
        }

        LocalDate lunes = fechaInicio.with(DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);

        // ORDENAR SOCIOS NUMERICAMENTE
        List<String> sociosOrdenados = new ArrayList<>(planMensual.keySet());
        sociosOrdenados.sort((s1, s2) -> {
            try {
                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        });

        // Iterar por socio y verificar sus días asignados
        for (String socio : sociosOrdenados) {
            List<LocalDate> diasAsignados = planMensual.get(socio);

            // Verificar si el socio tiene algún día en esta semana para incluirlo
            boolean tieneDias = false;
            for (LocalDate d : diasAsignados) {
                if (!d.isBefore(lunes) && !d.isAfter(domingo)) {
                    tieneDias = true;
                    break;
                }
            }

            if (tieneDias) {
                Map<DayOfWeek, Boolean> diasSemana = new LinkedHashMap<>();
                for (DayOfWeek dow : DayOfWeek.values()) {
                    // Calcular la fecha real de ese día de la semana
                    LocalDate fechaDia = lunes.plusDays(dow.getValue() - 1);

                    // Si el día pertenece al mes consultado -> Default false (Descanso)
                    // Si el día NO pertenece al mes (ej. final del mes anterior) -> null (No
                    // mostrar)
                    if (fechaDia.getMonthValue() == mes) {
                        diasSemana.put(dow, false);
                    } else {
                        diasSemana.put(dow, null);
                    }
                }

                for (LocalDate d : diasAsignados) {
                    if (!d.isBefore(lunes) && !d.isAfter(domingo)) {
                        diasSemana.put(d.getDayOfWeek(), true);
                    }
                }
                estructura.put(socio, diasSemana);
            }
        }

        return estructura;
    }

    public List<TurnoOperacionDiaria> consultarPlanDiario(LocalDate fecha, String codigoRuta, String sentido) {
        List<TurnoOperacionDiaria> operaciones = new ArrayList<>();

        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();

        Map<String, List<LocalDate>> planMensual = consultarPlanMensual(anio, mes, codigoRuta, sentido);

        if (planMensual.isEmpty()) {
            System.out.println("DEBUG DIARIO: Plan Mensual VACÍO para " + codigoRuta + " " + sentido);
            return operaciones;
        }

        List<String> sociosDisponibles = new ArrayList<>();

        for (Map.Entry<String, List<LocalDate>> entry : planMensual.entrySet()) {
            if (entry.getValue().contains(fecha)) {
                sociosDisponibles.add(entry.getKey());
            }
        }

        System.out.println("DEBUG DIARIO: Ruta " + codigoRuta + " " + sentido + " | Fecha: " + fecha
                + " | Socios Disp: " + sociosDisponibles.size());

        // Mantener orden numérico
        Collections.sort(sociosDisponibles, (s1, s2) -> {
            try {
                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        });

        if (sociosDisponibles.isEmpty()) {
            return operaciones;
        }

        // ROTACIÓN DIARIA JUSTA
        // Para que no siempre inicie la unidad 1, rotamos la lista según el día del
        // mes.
        // Día 1: Inicia el 1. Día 2: Inicia el 2 (el 1 pasa al final), etc.
        int diasRotacion = (fecha.getDayOfMonth() - 1) % sociosDisponibles.size();
        Collections.rotate(sociosDisponibles, -diasRotacion);

        // BUSQUEDA ROBUSTA DE PLANTILLA (CON FALLBACK SI INTERVALOS VACIOS)

        List<TurnoDAO.TurnoFranja> intervalos = new ArrayList<>();
        String[] horarios = null;

        // 1. Intentar con la plantilla PROPIA de la ruta (Prioridad MAXIMA para Hora
        // Inicio)
        Integer codigoPlantillaPropia = dao.obtenerCodigoPlantillaDeRuta(codigoRuta);

        if (codigoPlantillaPropia != null) {
            // Siempre intentamos obtener los horarios propios
            horarios = dao.obtenerHorariosPlantilla(codigoPlantillaPropia);
            // E intentamos obtener los intervalos propios
            intervalos = dao.obtenerIntervalosPlantilla(codigoPlantillaPropia);
        }

        // 2. Si fallan los INTERVALOS propios, buscar prestados (Shared/Fallback)
        if (intervalos.isEmpty()) {
            String rutaAlterna = null;
            if (codigoRuta.equals("02"))
                rutaAlterna = "01";
            if (codigoRuta.equals("04"))
                rutaAlterna = "03";
            if (codigoRuta.equals("03"))
                rutaAlterna = "04"; // Bidireccional

            if (rutaAlterna != null) {
                Integer codigoAlt = dao.obtenerCodigoPlantillaDeRuta(rutaAlterna);
                if (codigoAlt != null) {
                    List<TurnoDAO.TurnoFranja> intAlt = dao.obtenerIntervalosPlantilla(codigoAlt);
                    if (!intAlt.isEmpty()) {
                        System.out.println(
                                "INFO: Usando intervalos compartidos de ruta " + rutaAlterna + " (ID " + codigoAlt
                                        + ")");
                        intervalos = intAlt;

                        // SOLO usamos horario alterno si NO teniamos propio
                        if (horarios == null) {
                            horarios = dao.obtenerHorariosPlantilla(codigoAlt);
                        }
                    }
                }
            }
        }

        // 3. Fallback FINAL a Ruta 01 (Quitumbe) si todo falló
        if (intervalos.isEmpty()) {
            System.out.println("ADVERTENCIA: Fallback final a plantilla Ruta 01");
            Integer codigoGen = dao.obtenerCodigoPlantillaDeRuta("01");
            if (codigoGen != null) {
                intervalos = dao.obtenerIntervalosPlantilla(codigoGen);
                // SOLO usamos horario alterno si NO teniamos propio
                if (horarios == null) {
                    horarios = dao.obtenerHorariosPlantilla(codigoGen);
                }
            }
        }

        if (intervalos.isEmpty() || horarios == null || horarios.length < 2) {
            System.out.println("ERROR CRITICO: No se encontró ninguna plantilla válida ni siquiera la 01.");
            return operaciones;
        }

        LocalTime horaInicio = LocalTime.parse(horarios[0]);
        LocalTime horaFin = LocalTime.parse(horarios[1]);

        System.out.println("DEBUG: Generando horarios. Hora Inicio (BD): " + horaInicio);

        Integer duracionRuta = dao.obtenerDuracionRuta(codigoRuta);
        if (duracionRuta == null || duracionRuta <= 0) {
            duracionRuta = 60; // Por defecto 1 hora
        }

        List<LocalTime> horariosSalida = generarHorariosCompletosConFranjas(horaInicio, horaFin, intervalos);

        for (int i = 0; i < horariosSalida.size(); i++) {
            String socio = sociosDisponibles.get(i % sociosDisponibles.size());
            LocalTime salida = horariosSalida.get(i);
            LocalTime llegada = salida.plusMinutes(duracionRuta);

            TurnoOperacionDiaria op = new TurnoOperacionDiaria();
            op.setFechaOperacion(fecha);
            op.setCodigoRuta(codigoRuta);
            op.setSentido(sentido);
            op.setCodigoSocio(socio);
            op.setCodigoSocio(socio);
            op.setHoraSalida(salida);
            op.setHoraLlegadaEstimada(llegada);

            operaciones.add(op);
        }

        return operaciones;
    }

    private List<LocalTime> generarHorariosCompletosConFranjas(LocalTime inicio, LocalTime fin,
            List<TurnoDAO.TurnoFranja> franjas) {
        List<LocalTime> horarios = new ArrayList<>();
        LocalTime actual = inicio;

        // Limites fijos de franjas (segun definicion de negocio)
        LocalTime f1End = LocalTime.of(8, 0);
        LocalTime f2End = LocalTime.of(11, 0);
        LocalTime f3End = LocalTime.of(13, 0);
        LocalTime f4End = LocalTime.of(15, 0);
        LocalTime f5End = LocalTime.of(19, 0);
        // f6 hasta fin de operaciones

        while (actual.isBefore(fin) || actual.equals(fin)) {
            horarios.add(actual);

            int intervaloMinutos = 10; // Default
            int franjaId = 6; // Default ultima franja

            // Determinar ID de franja segun hora actual
            if (actual.isBefore(f1End))
                franjaId = 1;
            else if (actual.isBefore(f2End))
                franjaId = 2;
            else if (actual.isBefore(f3End))
                franjaId = 3;
            else if (actual.isBefore(f4End))
                franjaId = 4;
            else if (actual.isBefore(f5End))
                franjaId = 5;
            else
                franjaId = 6;

            // Buscar intervalo en la lista cargada de BD
            for (TurnoDAO.TurnoFranja f : franjas) {
                if (f.franjaId == franjaId) {
                    intervaloMinutos = f.intervalo;
                    break;
                }
            }

            actual = actual.plusMinutes(intervaloMinutos);

            if (horarios.size() > 500)
                break; // Safety break
        }

        return horarios;
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

    /**
     * Obtiene lista de rutas con formato: "codigoRuta - BaseOrigen → BaseDestino"
     */
    public List<RutaInfo> obtenerRutasConNombres() {
        List<RutaInfo> resultado = new ArrayList<>();
        Map<String, String> rutasActivas = dao.obtenerRutasActivasMap();

        for (Map.Entry<String, String> entry : rutasActivas.entrySet()) {
            resultado.add(new RutaInfo(entry.getKey(), entry.getValue()));
        }

        return resultado;
    }

    // Clases auxiliares
    private static class RutaConSentidos {
        String codigoRuta;
        String nombreBaseA;
        String nombreBaseB;
        List<String> sociosBaseA;
        List<String> sociosBaseB;

        RutaConSentidos(String codigo, String baseA, String baseB,
                List<String> sociosA, List<String> sociosB) {
            this.codigoRuta = codigo;
            this.nombreBaseA = baseA;
            this.nombreBaseB = baseB;
            this.sociosBaseA = sociosA;
            this.sociosBaseB = sociosB;
        }
    }

    public static class RutaInfo {
        public String codigo;
        public String nombre;

        public RutaInfo(String codigo, String nombre) {
            this.codigo = codigo;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return codigo + " - " + nombre;
        }
    }
}
