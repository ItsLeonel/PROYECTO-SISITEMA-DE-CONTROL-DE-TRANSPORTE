package Logica.Entidades;

/**
 * Entidad Socio Propietario
 * Módulo de Socios - Sistema SCTET
 * Especificación ERS v1.0 - Requisitos rso1v1.0 a ru6v1.1
 */
public class Socio {
    
    // Atributos según documento ERS
    private String codigoSocio;        // Código único de 2 dígitos (00-99)
    private String registroMunicipal;  // Registro municipal de 4 dígitos
    private String cedula;             // Cédula ecuatoriana de 10 dígitos (Anexo A)
    private String nombresCompletos;   // Nombres completos (hasta 100 caracteres)
    private String direccion;          // Dirección estructurada según Anexo B
    private String numeroCelular;      // Celular de 10 dígitos según Anexo C
    private String estado;             // Estado: ACTIVO, INACTIVO
    private String placaBusAsociado;   // Placa del bus asociado (puede ser null)
    
    // Constructor vacío
    public Socio() {
        this.estado = "ACTIVO";
    }
    
    // Constructor completo
    public Socio(String codigoSocio, String registroMunicipal, String cedula, 
                 String nombresCompletos, String direccion, String numeroCelular) {
        this.codigoSocio = codigoSocio;
        this.registroMunicipal = registroMunicipal;
        this.cedula = cedula;
        this.nombresCompletos = nombresCompletos;
        this.direccion = direccion;
        this.numeroCelular = numeroCelular;
        this.estado = "ACTIVO";
    }

    // Getters y Setters
    public String getCodigoSocio() {
        return codigoSocio;
    }

    public void setCodigoSocio(String codigoSocio) {
        this.codigoSocio = codigoSocio;
    }

    public String getRegistroMunicipal() {
        return registroMunicipal;
    }

    public void setRegistroMunicipal(String registroMunicipal) {
        this.registroMunicipal = registroMunicipal;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombresCompletos() {
        return nombresCompletos;
    }

    public void setNombresCompletos(String nombresCompletos) {
        this.nombresCompletos = nombresCompletos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPlacaBusAsociado() {
        return placaBusAsociado;
    }

    public void setPlacaBusAsociado(String placaBusAsociado) {
        this.placaBusAsociado = placaBusAsociado;
    }

    @Override
    public String toString() {
        return "Socio{" +
                "codigoSocio='" + codigoSocio + '\'' +
                ", cedula='" + cedula + '\'' +
                ", nombresCompletos='" + nombresCompletos + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}