package Logica.Entidades;

public class Intervalo {

    private int codigoIntervalo;
    private int tiempoMinutos;
    private String franjaHoraria;   // HH:MM
    private String codigoRuta;      // FK a ruta.codigo_ruta
    private String estado;          // Activo / Inactivo

    public int getCodigoIntervalo() { return codigoIntervalo; }
    public void setCodigoIntervalo(int codigoIntervalo) { this.codigoIntervalo = codigoIntervalo; }

    public int getTiempoMinutos() { return tiempoMinutos; }
    public void setTiempoMinutos(int tiempoMinutos) { this.tiempoMinutos = tiempoMinutos; }

    public String getFranjaHoraria() { return franjaHoraria; }
    public void setFranjaHoraria(String franjaHoraria) { this.franjaHoraria = franjaHoraria; }

    public String getCodigoRuta() { return codigoRuta; }
    public void setCodigoRuta(String codigoRuta) { this.codigoRuta = codigoRuta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

