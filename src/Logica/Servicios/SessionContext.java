package Logica.Servicios;

import Logica.DAO.PermisoDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la sesión actual del usuario autenticado
 * y sus permisos en el sistema
 */
public class SessionContext {

    // ================= DATOS DE SESIÓN =================
    private static Long idUsuario;
    private static String usuarioLogin;
    private static String nombreCompleto;
    private static String rol;
    private static List<String> permisos = new ArrayList<>();
    private static boolean logged = false;

    // =====================================================
    // INICIAR SESIÓN
    // =====================================================
    public static void iniciarSesion(Long id, String login, String nombre, String nombreRol) {
        idUsuario = id;
        usuarioLogin = login;
        nombreCompleto = nombre;
        rol = nombreRol;
        logged = true;
        
        // 🔥 CLAVE: Cargar permisos del usuario desde BD
        cargarPermisos();
    }

    // =====================================================
    // CARGAR PERMISOS DEL USUARIO
    // =====================================================
    private static void cargarPermisos() {
        permisos.clear();
        
        if (idUsuario == null) return;
        
        try {
            // Obtener todos los permisos del usuario desde BD
            permisos = PermisoDAO.obtenerCodigosPermisosPorUsuario(idUsuario);
            
            System.out.println("✅ Permisos cargados para " + usuarioLogin + ": " + permisos);
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar permisos: " + e.getMessage());
            permisos = new ArrayList<>();
        }
    }

    // =====================================================
    // VERIFICAR SI TIENE PERMISO (MÉTODO PRINCIPAL)
    // =====================================================
    /**
     * Verifica si el usuario actual tiene un permiso específico
     * @param codigoPermiso Código del permiso (ej: RGS10.1, RGB01)
     * @return true si tiene el permiso, false si no
     */
    public static boolean tienePermiso(String codigoPermiso) {
        if (!logged || codigoPermiso == null) {
            return false;
        }
        
        return permisos.contains(codigoPermiso);
    }

    // =====================================================
    // VERIFICAR SI TIENE AL MENOS UNO DE VARIOS PERMISOS
    // =====================================================
    /**
     * Verifica si el usuario tiene al menos uno de los permisos especificados
     * Útil cuando un módulo tiene varios sub-permisos
     */
    public static boolean tieneAlgunoDeEstosPermisos(String... codigos) {
        for (String codigo : codigos) {
            if (tienePermiso(codigo)) {
                return true;
            }
        }
        return false;
    }

    // =====================================================
    // VERIFICAR SI TIENE TODOS LOS PERMISOS
    // =====================================================
    /**
     * Verifica si el usuario tiene TODOS los permisos especificados
     */
    public static boolean tieneTodosLosPermisos(String... codigos) {
        for (String codigo : codigos) {
            if (!tienePermiso(codigo)) {
                return false;
            }
        }
        return true;
    }

    // =====================================================
    // RECARGAR PERMISOS (útil después de cambios)
    // =====================================================
    /**
     * Recarga los permisos desde BD
     * Usar cuando se modifiquen permisos del rol actual
     */
    public static void recargarPermisos() {
        cargarPermisos();
    }

    // =====================================================
    // CERRAR SESIÓN
    // =====================================================
    public static void cerrarSesion() {
        idUsuario = null;
        usuarioLogin = null;
        nombreCompleto = null;
        rol = null;
        permisos.clear();
        logged = false;
        
        System.out.println("✅ Sesión cerrada correctamente");
    }

    /**
     * Alias de cerrarSesion() para compatibilidad con código existente
     */
    public static void logout() {
        cerrarSesion();
    }

    // =====================================================
    // GETTERS
    // =====================================================
    public static boolean isLogged() {
        return logged;
    }

    public static Long getIdUsuario() {
        return idUsuario;
    }

    public static String getUsuarioLogin() {
        return usuarioLogin;
    }

    public static String getNombreCompleto() {
        return nombreCompleto;
    }

    public static String getRol() {
        return rol;
    }

    public static List<String> getPermisos() {
        return new ArrayList<>(permisos); // Copia defensiva
    }

    // =====================================================
    // MÉTODOS DE UTILIDAD
    // =====================================================
    
    /**
     * Obtiene una descripción legible de los permisos del usuario
     */
    public static String getDescripcionPermisos() {
        if (permisos.isEmpty()) {
            return "Sin permisos asignados";
        }
        return String.join(", ", permisos);
    }

    /**
     * Cuenta cuántos permisos tiene el usuario
     */
    public static int getCantidadPermisos() {
        return permisos.size();
    }
}