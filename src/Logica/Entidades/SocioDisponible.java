package Logica.Entidades;

/**
 * DTO para representar un Socio Disponible (sin bus asignado)
 * Usado en el JComboBox del formulario de registro de buses
 */
public class SocioDisponible {
    
    private String codigoSocio;
    private String registroMunicipal;
    private String nombresCompletos;
    
    public SocioDisponible() {
    }
    
    public SocioDisponible(String codigoSocio, String registroMunicipal, String nombresCompletos) {
        this.codigoSocio = codigoSocio;
        this.registroMunicipal = registroMunicipal;
        this.nombresCompletos = nombresCompletos;
    }

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

    public String getNombresCompletos() {
        return nombresCompletos;
    }

    public void setNombresCompletos(String nombresCompletos) {
        this.nombresCompletos = nombresCompletos;
    }

    /**
     * Método toString para el JComboBox
     * Formato: "1001 - JUAN PEREZ LOPEZ"
     */
    @Override
    public String toString() {
        return registroMunicipal + " - " + nombresCompletos;
    }
}