package Logica.Entidades;

/**
 * Entidad Ruta actualizada según nuevo modelo funcional
 * - Base A y Base B (bidireccionalidad operacional)
 * - Plantilla horaria asociada (debe estar Activa)
 * - Duración estimada en minutos
 */
public class Ruta {

    private String codigoRuta;           // PK varchar(20) - validar 2 dígitos
    private String nombre;               // hasta 50 caracteres, único
    private int codigoBaseA;             // FK a base_operativa
    private int codigoBaseB;             // FK a base_operativa (debe ser != baseA)
    private int codigoIntervalo;         // FK a plantilla_intervalo (debe estar Activa)
    private int duracionEstimadaMinutos; // entero positivo > 0
    private String origen;               // varchar(120) - opcional/informativo
    private String destino;              // varchar(120) - opcional/informativo
    private String estado;               // ENUM('Activo','Inactivo')

    // Constructor vacío
    public Ruta() {
    }

    // Getters y Setters
    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCodigoBaseA() {
        return codigoBaseA;
    }

    public void setCodigoBaseA(int codigoBaseA) {
        this.codigoBaseA = codigoBaseA;
    }

    public int getCodigoBaseB() {
        return codigoBaseB;
    }

    public void setCodigoBaseB(int codigoBaseB) {
        this.codigoBaseB = codigoBaseB;
    }

    public int getCodigoIntervalo() {
        return codigoIntervalo;
    }

    public void setCodigoIntervalo(int codigoIntervalo) {
        this.codigoIntervalo = codigoIntervalo;
    }

    public int getDuracionEstimadaMinutos() {
        return duracionEstimadaMinutos;
    }

    public void setDuracionEstimadaMinutos(int duracionEstimadaMinutos) {
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "codigoRuta='" + codigoRuta + '\'' +
                ", nombre='" + nombre + '\'' +
                ", codigoBaseA=" + codigoBaseA +
                ", codigoBaseB=" + codigoBaseB +
                ", codigoIntervalo=" + codigoIntervalo +
                ", duracionEstimadaMinutos=" + duracionEstimadaMinutos +
                ", estado='" + estado + '\'' +
                '}';
    }
}



















