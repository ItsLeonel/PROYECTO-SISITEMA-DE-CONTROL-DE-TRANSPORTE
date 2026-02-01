package Logica.Entidades;

/**
 * Entidad que representa una Franja dentro de una Plantilla Horaria (tabla hija).
 * Cada plantilla tiene exactamente 6 franjas estándar (1 a 6).
 */
public class PlantillaHorariaFranja {

    private int codigoPlantilla;  // FK a plantilla_horaria
    private int franjaId;         // 1 a 6 (PK compuesta)
    private int tiempoMinutos;    // intervalo de salida en minutos

    // Constructor vacío
    public PlantillaHorariaFranja() {
    }

    // Constructor completo
    public PlantillaHorariaFranja(int codigoPlantilla, int franjaId, int tiempoMinutos) {
        this.codigoPlantilla = codigoPlantilla;
        this.franjaId = franjaId;
        this.tiempoMinutos = tiempoMinutos;
    }

    // Getters y Setters
    public int getCodigoPlantilla() {
        return codigoPlantilla;
    }

    public void setCodigoPlantilla(int codigoPlantilla) {
        this.codigoPlantilla = codigoPlantilla;
    }

    public int getFranjaId() {
        return franjaId;
    }

    public void setFranjaId(int franjaId) {
        this.franjaId = franjaId;
    }

    public int getTiempoMinutos() {
        return tiempoMinutos;
    }

    public void setTiempoMinutos(int tiempoMinutos) {
        this.tiempoMinutos = tiempoMinutos;
    }

    /**
     * Retorna la descripción legible de la franja según su ID
     */
    public String getDescripcionFranja() {
        return switch (franjaId) {
            case 1 -> "Inicio → 08:00";
            case 2 -> "08:00 → 11:00";
            case 3 -> "11:00 → 13:00";
            case 4 -> "13:00 → 15:00";
            case 5 -> "15:00 → 19:00";
            case 6 -> "19:00 → Fin";
            default -> "Franja desconocida";
        };
    }

    @Override
    public String toString() {
        return "PlantillaHorariaFranja{" +
                "codigoPlantilla=" + codigoPlantilla +
                ", franjaId=" + franjaId +
                ", tiempoMinutos=" + tiempoMinutos +
                ", descripcion='" + getDescripcionFranja() + '\'' +
                '}';
    }
}