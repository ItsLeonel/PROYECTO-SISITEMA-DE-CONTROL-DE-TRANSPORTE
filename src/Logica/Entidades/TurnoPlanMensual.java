package Logica.Entidades;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Plan Maestro Mensual de Turnos
 * Tabla: turno_plan_mensual
 */
public class TurnoPlanMensual {

    private int idPlan;
    private int anio;
    private int mes;
    private LocalDateTime fechaGeneracion;

    public TurnoPlanMensual() {
    }

    public TurnoPlanMensual(int anio, int mes) {
        this.anio = anio;
        this.mes = mes;
        this.fechaGeneracion = LocalDateTime.now();
    }

    // Getters y Setters
    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    @Override
    public String toString() {
        return "TurnoPlanMensual{" +
                "idPlan=" + idPlan +
                ", anio=" + anio +
                ", mes=" + mes +
                ", fechaGeneracion=" + fechaGeneracion +
                '}';
    }
}
