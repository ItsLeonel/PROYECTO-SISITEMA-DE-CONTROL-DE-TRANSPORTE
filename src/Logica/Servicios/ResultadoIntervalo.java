package Logica.Servicios;
import java.util.List;

public class ResultadoIntervalo {

    private final String mensaje;
    private final List<?> datos;

    public ResultadoIntervalo(String mensaje, List<?> datos) {
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<?> getDatos() {
        return datos;
    }
}
