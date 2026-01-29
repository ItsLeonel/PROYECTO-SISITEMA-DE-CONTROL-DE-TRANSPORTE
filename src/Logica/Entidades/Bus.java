package Logica.Entidades;

/**
 * Entidad Bus
 * Módulo de Buses - Sistema SCTET
 */
public class Bus {

    // ===== CAMPOS PERSISTENTES (tabla buses) =====
    private String placa;                  // PK
    private String marca;
    private String modelo;
    private int anioFabricacion;
    private int capacidadPasajeros;
    private String baseAsignada;
    private String estado;
    private String codigoSocioFk;          // FK → socios_propietarios.codigo_socio

    // ===== CAMPOS DE APOYO (JOIN, no persistentes) =====
    private String nombresPropietario;
    private String telefonoPropietario;
    // private String codigoSocio;

    // Constructor vacío
    public Bus() {
        this.estado = "ACTIVO";
    }

    // Constructor para registro
    public Bus(String placa, String marca, String modelo,
               int anioFabricacion, int capacidadPasajeros,
               String baseAsignada, String estado, String codigoSocioFk) {

        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.capacidadPasajeros = capacidadPasajeros;
        this.baseAsignada = baseAsignada;
        this.estado = estado;
        this.codigoSocioFk = codigoSocioFk;
    }

    // ===== GETTERS Y SETTERS =====

    public String getPlaca() {
        return placa;
    }
    public String getCodigoSocio() {
    return codigoSocioFk;
}

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }
    
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnioFabricacion() {
        return anioFabricacion;
    }

    public void setAnioFabricacion(int anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    public int getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(int capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public String getBaseAsignada() {
        return baseAsignada;
    }

    public void setBaseAsignada(String baseAsignada) {
        this.baseAsignada = baseAsignada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoSocioFk() {
        return codigoSocioFk;
    }

    public void setCodigoSocioFk(String codigoSocioFk) {
        this.codigoSocioFk = codigoSocioFk;
    }

    public String getNombresPropietario() {
        return nombresPropietario;
    }

    public void setNombresPropietario(String nombresPropietario) {
        this.nombresPropietario = nombresPropietario;
    }

    public String getTelefonoPropietario() {
        return telefonoPropietario;
    }

    public void setTelefonoPropietario(String telefonoPropietario) {
        this.telefonoPropietario = telefonoPropietario;
    }

    
    public boolean isDisponible() {
        return "ACTIVO".equalsIgnoreCase(estado);

    }

    @Override
    public String toString() {
        return "Bus{" +
                "placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
