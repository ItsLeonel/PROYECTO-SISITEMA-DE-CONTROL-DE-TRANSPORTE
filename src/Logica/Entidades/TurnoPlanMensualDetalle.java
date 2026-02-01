package Logica.Entidades;

import java.time.LocalDate;

/**
 * Entidad que representa el detalle diario del plan mensual
 * Indica qué socio opera en qué fecha
 * Tabla: turno_plan_mensual_detalle
 */
public class TurnoPlanMensualDetalle {

    private int idPlanRuta;
    private String codigoSocio;
    private LocalDate fechaOperacion;
    private boolean opera; // true = opera (1), false = no opera (0)

    public TurnoPlanMensualDetalle() {
    }

    public TurnoPlanMensualDetalle(int idPlanRuta, String codigoSocio, LocalDate fechaOperacion, boolean opera) {
        this.idPlanRuta = idPlanRuta;
        this.codigoSocio = codigoSocio;
        this.fechaOperacion = fechaOperacion;
        this.opera = opera;
    }

    // Getters y Setters
    public int getIdPlanRuta() {
        return idPlanRuta;
    }

    public void setIdPlanRuta(int idPlanRuta) {
        this.idPlanRuta = idPlanRuta;
    }

    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public LocalDate getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(LocalDate fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public boolean isOpera() {
        return opera;
    }

    public void setOpera(boolean opera) {
        this.opera = opera;
    }

    @Override
    public String toString() {
        return "TurnoPlanMensualDetalle{" +
                "idPlanRuta=" + idPlanRuta +
                ", codigoSocio='" + codigoSocio + '\'' +
                ", fechaOperacion=" + fechaOperacion +
                ", opera=" + opera +
                '}';
    }
}
