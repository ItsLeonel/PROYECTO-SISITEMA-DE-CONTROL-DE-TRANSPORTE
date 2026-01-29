package Logica.Entidades;

public class Bus {
    private String codigo;           // PK único (4 dígitos)
    private String placa;            // Única (formato PPP-1234)
    private String dueno;            // Propietario del bus
    private String marca;            // Ej: Hino, Chevrolet, Isuzu
    private String modelo;           // Ej: AK, NQR, FRR
    private int anioFabricacion;     // YEAR en BD
    private String base;             // FK a base_operativa(nombre)
    private String estado;           // ACTIVO/DESACTIVADO/MANTENIMIENTO

    // Constructor completo
    public Bus(String codigo, String placa, String dueno, String marca, 
               String modelo, int anioFabricacion, String base, String estado) {
        this.codigo = codigo;
        this.placa = placa;
        this.dueno = dueno;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.base = base;
        this.estado = estado;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getPlaca() { return placa; }
    public String getDueno() { return dueno; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnioFabricacion() { return anioFabricacion; }
    public String getBase() { return base; }
    public String getEstado() { return estado; }

    // Setters (solo los modificables)
    public void setPlaca(String placa) { 
        this.placa = placa; 
    }
    
    public void setBase(String base) { 
        this.base = base; 
    }
    
    public void setEstado(String estado) { 
        this.estado = estado; 
    }
    
    // Método auxiliar para verificar si está disponible para turnos
    public boolean isDisponible() {
        return "ACTIVO".equalsIgnoreCase(estado);
    }
}