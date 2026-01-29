
package Logica.Servicios;

public class SessionContext {
    private static String usuarioLogin;   // ej: admin
    private static String nombreVisible;  // ej: Usuario Admin
    private static String rol;            // ej: Administrador

    private SessionContext() {}

    public static void login(String usuarioLogin, String nombreVisible, String rol) {
        SessionContext.usuarioLogin = usuarioLogin;
        SessionContext.nombreVisible = nombreVisible;
        SessionContext.rol = rol;
    }

    public static void logout() {
        SessionContext.usuarioLogin = null;
        SessionContext.nombreVisible = null;
        SessionContext.rol = null;
    }

    public static boolean isLogged() {
        return usuarioLogin != null;
    }

    public static String getUsuarioLogin() {
        return usuarioLogin == null ? "N/A" : usuarioLogin;
    }

    public static String getNombreVisible() {
        return nombreVisible == null ? "N/A" : nombreVisible;
    }

    public static String getRol() {
        return rol == null ? "N/A" : rol;
    }
}
