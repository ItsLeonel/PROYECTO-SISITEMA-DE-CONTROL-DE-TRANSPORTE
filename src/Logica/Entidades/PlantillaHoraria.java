package Logica.Entidades;

/**
 * Entidad que representa una Plantilla Horaria (tabla madre).
 * Contiene información general de la plantilla y referencias a sus franjas.
 */
public class PlantillaHoraria {

    private int codigoPlantilla;           // PK
    private String nombre;                 // hasta 50 caracteres
    private String horaInicioOperaciones;  // HH:MM formato 24h
    private String horaFinOperaciones;     // HH:MM formato 24h
    private String codigoRuta;             // FK a ruta
    private String estado;                 // Activo / Inactivo

    // Constructor vacío
    public PlantillaHoraria() {
    }

    // Constructor completo
    public PlantillaHoraria(int codigoPlantilla, String nombre, String horaInicioOperaciones,
                            String horaFinOperaciones, String codigoRuta, String estado) {
        this.codigoPlantilla = codigoPlantilla;
        this.nombre = nombre;
        this.horaInicioOperaciones = horaInicioOperaciones;
        this.horaFinOperaciones = horaFinOperaciones;
        this.codigoRuta = codigoRuta;
        this.estado = estado;
    }

    // Getters y Setters
    public int getCodigoPlantilla() {
        return codigoPlantilla;
    }

    public void setCodigoPlantilla(int codigoPlantilla) {
        this.codigoPlantilla = codigoPlantilla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHoraInicioOperaciones() {
        return horaInicioOperaciones;
    }

    public void setHoraInicioOperaciones(String horaInicioOperaciones) {
        this.horaInicioOperaciones = horaInicioOperaciones;
    }

    public String getHoraFinOperaciones() {
        return horaFinOperaciones;
    }

    public void setHoraFinOperaciones(String horaFinOperaciones) {
        this.horaFinOperaciones = horaFinOperaciones;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "PlantillaHoraria{" +
                "codigoPlantilla=" + codigoPlantilla +
                ", nombre='" + nombre + '\'' +
                ", horaInicioOperaciones='" + horaInicioOperaciones + '\'' +
                ", horaFinOperaciones='" + horaFinOperaciones + '\'' +
                ", codigoRuta='" + codigoRuta + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}