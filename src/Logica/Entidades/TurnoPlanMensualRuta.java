package Logica.Entidades;

/**
 * Entidad que representa una ruta asignada en el plan mensual
 * Tabla: turno_plan_mensual_ruta
 */
public class TurnoPlanMensualRuta {

    private int idPlanRuta;
    private int idPlan;
    private String codigoRuta;
    private String sentido; // 'A_B' o 'B_A'

    public TurnoPlanMensualRuta() {
    }

    public TurnoPlanMensualRuta(int idPlan, String codigoRuta, String sentido) {
        this.idPlan = idPlan;
        this.codigoRuta = codigoRuta;
        this.sentido = sentido;
    }

    // Getters y Setters
    public int getIdPlanRuta() {
        return idPlanRuta;
    }

    public void setIdPlanRuta(int idPlanRuta) {
        this.idPlanRuta = idPlanRuta;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    @Override
    public String toString() {
        return "TurnoPlanMensualRuta{" +
                "idPlanRuta=" + idPlanRuta +
                ", idPlan=" + idPlan +
                ", codigoRuta='" + codigoRuta + '\'' +
                ", sentido='" + sentido + '\'' +
                '}';
    }
}
