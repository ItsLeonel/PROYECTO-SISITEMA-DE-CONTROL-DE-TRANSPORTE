package Logica.Servicios;

/**
 * Clase para encapsular el resultado de operaciones del sistema
 * Permite devolver estado (éxito/fallo), mensaje y datos opcionales
 */
public class ResultadoOperacion {
    
    private boolean exito;
    private String mensaje;
    private Object datos;

    // Constructor básico (sin datos)
    public ResultadoOperacion(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = null;
    }

    // Constructor con datos
    public ResultadoOperacion(boolean exito, String mensaje, Object datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    // Getters
    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Object getDatos() {
        return datos;
    }

    // Setters
    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }
}