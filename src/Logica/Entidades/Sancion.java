package Logica.Entidades;

import java.time.LocalDate;

public class Sancion {

    private String codigoSancion;
    private String codigoIncidencia;
    private LocalDate fechaAplicacion;
    private String responsable;
    private int posicionesRetroceso;
    private String estado;

    public String getCodigoSancion() {
        return codigoSancion;
    }

    public void setCodigoSancion(String codigoSancion) {
        this.codigoSancion = codigoSancion;
    }

    public String getCodigoIncidencia() {
        return codigoIncidencia;
    }

    public void setCodigoIncidencia(String codigoIncidencia) {
        this.codigoIncidencia = codigoIncidencia;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public int getPosicionesRetroceso() {
        return posicionesRetroceso;
    }

    public void setPosicionesRetroceso(int posicionesRetroceso) {
        this.posicionesRetroceso = posicionesRetroceso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
