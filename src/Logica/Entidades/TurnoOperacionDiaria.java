package Logica.Entidades;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa una operación diaria específica con horarios
 * Tabla: turno_operacion_diaria
 */
public class TurnoOperacionDiaria {

    private LocalDate fechaOperacion;
    private String codigoRuta;
    private String sentido;
    private String codigoSocio;
    private LocalTime horaSalida;
    private LocalTime horaLlegadaEstimada;

    public TurnoOperacionDiaria() {
    }

    // Getters y Setters
    public LocalDate getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(LocalDate fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public LocalTime getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    public void setHoraLlegadaEstimada(LocalTime horaLlegadaEstimada) {
        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    @Override
    public String toString() {
        return "TurnoOperacionDiaria{" +
                "fechaOperacion=" + fechaOperacion +
                ", codigoRuta='" + codigoRuta + '\'' +
                ", sentido='" + sentido + '\'' +
                ", codigoSocio='" + codigoSocio + '\'' +
                ", horaSalida=" + horaSalida +
                ", horaLlegadaEstimada=" + horaLlegadaEstimada +
                '}';
    }
}
