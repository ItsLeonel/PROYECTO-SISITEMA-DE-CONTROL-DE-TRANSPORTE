package Logica.Entidades;

import java.time.LocalDateTime;

public class AuditoriaEvento {
    private final long id;
    private final LocalDateTime fechaHora;
    private final String usuarioLogin;
    private final String rol;
    private final String modulo;
    private final String accion;
    private final String resultado;
    private final String detalle;

    public AuditoriaEvento(long id, LocalDateTime fechaHora, String usuarioLogin, String rol,
                           String modulo, String accion, String resultado, String detalle) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.usuarioLogin = usuarioLogin;
        this.rol = rol;
        this.modulo = modulo;
        this.accion = accion;
        this.resultado = resultado;
        this.detalle = detalle;
    }

    public long getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getUsuarioLogin() { return usuarioLogin; }
    public String getRol() { return rol; }
    public String getModulo() { return modulo; }
    public String getAccion() { return accion; }
    public String getResultado() { return resultado; }
    public String getDetalle() { return detalle; }
}
