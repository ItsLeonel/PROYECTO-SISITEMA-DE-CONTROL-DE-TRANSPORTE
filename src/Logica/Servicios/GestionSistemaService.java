package Logica.Servicios;

import Logica.DAO.UsuarioDAO;
import Logica.DAO.RolDAO;
import Logica.DAO.PermisoDAO;
import Logica.DAO.UsuarioRolDAO;
import Logica.DAO.RolPermisoDAO;

import java.util.List;

/**
 * Servicio de Gestión del Sistema
 * ACTUALIZADO: Usa métodos estáticos del nuevo PermisoDAO
 */
public class GestionSistemaService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final UsuarioRolDAO usuarioRolDAO = new UsuarioRolDAO();
    private final RolPermisoDAO rolPermisoDAO = new RolPermisoDAO();

    // =====================================================
    // RGS4+5v1.1: REGISTRAR USUARIO
    // =====================================================
    public String registrarUsuario(String login, String correo, String rol, String estado) {
        try {
            // Validar formato de login (6-20 caracteres, alfanumérico, permite _ y .)
            if (!loginValido(login)) {
                return "No se puede registrar el usuario: el nombre de usuario no cumple con el formato.";
            }

            // Validar formato de correo (según Anexo E)
            if (!correoValido(correo)) {
                return "No se puede registrar el usuario: el correo electrónico no cumple el formato.";
            }

            // Validar estado
            if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) {
                return "No se puede registrar el usuario: el estado no es válido.";
            }

            // Verificar si login ya existe
            if (usuarioDAO.existeLogin(login)) {
                return "No se puede registrar el usuario: el nombre de usuario ya se encuentra previamente registrado.";
            }

            // Verificar si correo ya existe
            if (usuarioDAO.existeCorreo(correo)) {
                return "No se puede registrar el usuario: el correo electrónico ya se encuentra previamente registrado.";
            }

            // Verificar que el rol exista y esté activo
            long idRol = rolDAO.obtenerIdPorNombre(rol);
            if (idRol == 0) {
                return "No se puede registrar el usuario: el rol no existe.";
            }

            if (!rolDAO.estaActivo(idRol)) {
                return "No se puede registrar el usuario: el rol se encuentra inactivo.";
            }

            // Generar contraseña temporal
            String temp = PasswordUtil.generarTemporal(10);
            String salt = PasswordUtil.generarSalt();
            String hash = PasswordUtil.hashSHA256(temp, salt);

            // Insertar usuario (con requiere_cambio_clave = 1)
            long idUsuario = usuarioDAO.insertar(login, correo, login, hash, salt);

            // Establecer estado según parámetro
            if (estado.equals("ACTIVO")) {
                usuarioDAO.setActivo(idUsuario, true);
            }

            // Asignar rol
            usuarioRolDAO.asignar(idUsuario, idRol);

            return "Usuario registrado correctamente. Se genera una contraseña temporal para el acceso inicial.";

        } catch (Exception e) {
            return "No se puede registrar el usuario: error del sistema.";
        }
    }

    // =====================================================
    // RGS7v1.1: ACTUALIZAR CORREO ELECTRÓNICO
    // =====================================================
    public String actualizarCorreo(String login, String nuevoCorreo) {
        // Validar formato de correo
        if (!correoValido(nuevoCorreo)) {
            return "No se puede actualizar el correo electrónico: el correo electrónico no cumple el formato.";
        }

        // Verificar que el usuario exista
        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
        if (u == null) {
            return "No se puede actualizar el correo electrónico: el usuario no se encuentra registrado.";
        }

        // Verificar que el correo no esté en uso por otro usuario
        if (usuarioDAO.existeCorreoEnOtro(nuevoCorreo, u.id)) {
            return "No se puede actualizar el correo electrónico: el correo electrónico ya se encuentra previamente registrado.";
        }

        // Actualizar correo
        usuarioDAO.actualizarCorreoPorLogin(login, nuevoCorreo);

        return "Correo electrónico actualizado correctamente.";
    }

    // =====================================================
    // RGS8.1v1.1: ACTIVAR USUARIO
    // =====================================================
    public String activarUsuario(String login) {
        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
        if (u == null) {
            return "El usuario no existe.";
        }

        usuarioDAO.setActivo(u.id, true);
        return "El usuario ha sido activado correctamente.";
    }

    // =====================================================
    // RGS8.2v1.1: DESACTIVAR USUARIO
    // =====================================================
    public String desactivarUsuario(String login) {
        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
        if (u == null) {
            return "El usuario no existe.";
        }

        usuarioDAO.setActivo(u.id, false);
        return "El usuario ha sido desactivado correctamente.";
    }

    // =====================================================
    // RGS3v1.1: CAMBIAR CONTRASEÑA
    // =====================================================
    public void cambiarContrasena(long idUsuario, String actualPlano, String nuevaPlano, String confirmarPlano) {
        // Validar que la nueva contraseña coincida con la confirmación
        if (nuevaPlano == null || !nuevaPlano.equals(confirmarPlano)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        // Validar longitud mínima (según Anexo F)
        if (nuevaPlano.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        // Obtener credenciales actuales
        UsuarioDAO.PasswordData pd = usuarioDAO.obtenerHashSalt(idUsuario);
        if (pd == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        // Verificar contraseña actual
        String hashActual = PasswordUtil.hashSHA256(actualPlano, pd.salt);
        if (!hashActual.equalsIgnoreCase(pd.hash)) {
            throw new IllegalArgumentException("La contraseña actual ingresada es incorrecta.");
        }

        // Actualizar contraseña
        String nuevoSalt = PasswordUtil.generarSalt();
        String nuevoHash = PasswordUtil.hashSHA256(nuevaPlano, nuevoSalt);
        usuarioDAO.actualizarPassword(idUsuario, nuevoHash, nuevoSalt, false);
    }

    // =====================================================
    // RGS9.1v1.1: RESTABLECER CONTRASEÑA
    // =====================================================
    public String restablecerContrasena(String login) {
        // Validar formato de login
        if (!loginValido(login)) {
            return "El usuario no existe.";
        }

        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
        if (u == null) {
            return "El usuario no existe.";
        }

        // Generar contraseña temporal
        String temp = PasswordUtil.generarTemporal(10);
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashSHA256(temp, salt);

        // Actualizar contraseña y marcar como temporal (requiere cambio)
        usuarioDAO.actualizarPassword(u.id, hash, salt, true);

        return "Contraseña restablecida correctamente. Se ha generado una contraseña temporal.\nContraseña temporal: " + temp;
    }

    // =====================================================
    // RGS10.1v1.1: CREAR ROL
    // =====================================================
    public void crearRol(String nombre, boolean activo) {
        // Validar formato de nombre (3-30 caracteres alfanuméricos)
        if (nombre == null || !nombre.matches("[A-Za-záéíóúÁÉÍÓÚñÑ -]{3,30}")) {
            throw new IllegalArgumentException("No se puede crear el rol: el nombre del rol no cumple con el formato.");
        }

        // Verificar si el nombre ya existe
        if (rolDAO.existeNombre(nombre)) {
            throw new IllegalArgumentException("No se puede crear el rol: el nombre del rol ya se encuentra previamente registrado.");
        }

        // Crear rol
        long idRol = rolDAO.insertar(nombre);
        rolDAO.setActivo(idRol, activo);
    }

    // =====================================================
    // RGS10.2v1.1: ACTUALIZAR NOMBRE DE ROL
    // =====================================================
    public String actualizarNombreRol(String nombreActual, String nuevoNombre) {
        if (nombreActual == null || nuevoNombre == null) {
            return "El rol no existe.";
        }

        nombreActual = nombreActual.trim();
        nuevoNombre = nuevoNombre.trim();

        // Verificar que el rol exista
        long idRol = rolDAO.obtenerIdPorNombre(nombreActual);
        if (idRol == 0) {
            return "El rol no existe.";
        }

        // Verificar que el nuevo nombre no esté en uso
        if (rolDAO.existeNombreEnOtro(nuevoNombre, idRol)) {
            return "No se puede actualizar el rol: el nombre del rol ya se encuentra previamente registrado.";
        }

        // Actualizar nombre
        rolDAO.actualizarNombre(idRol, nuevoNombre);

        return "Nombre del rol actualizado correctamente.";
    }

    // =====================================================
    // RGS10.3v1.1: ACTIVAR ROL
    // =====================================================
    public String activarRolPorNombre(String nombreRol) {
        long idRol = rolDAO.obtenerIdPorNombre(nombreRol);
        if (idRol == 0) {
            return "El rol no existe.";
        }

        rolDAO.setActivo(idRol, true);
        return "El rol ha sido activado correctamente.";
    }

    // =====================================================
    // RGS10.4v1.0: DESACTIVAR ROL
    // =====================================================
    public String desactivarRolPorNombre(String nombreRol) {
        long idRol = rolDAO.obtenerIdPorNombre(nombreRol);
        if (idRol == 0) {
            return "El rol no existe.";
        }

        rolDAO.setActivo(idRol, false);
        return "El rol ha sido desactivado correctamente.";
    }

    // =====================================================
    // ASIGNAR ROL A USUARIO
    // =====================================================
    public void asignarRolAUsuario(long idUsuario, String rolNombre) {
        // Verificar que el rol exista
        long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
        if (idRol == 0) {
            throw new IllegalArgumentException("No se pudo modificar la asignación: el usuario o el rol no existen.");
        }

        // Verificar que el rol esté activo
        if (!rolDAO.estaActivo(idRol)) {
            throw new IllegalArgumentException("No se pudo modificar la asignación: el rol se encuentra inactivo.");
        }

        // Revocar rol anterior (un usuario tiene un solo rol)
        usuarioRolDAO.revocarTodos(idUsuario);

        // Asignar nuevo rol
        usuarioRolDAO.asignar(idUsuario, idRol);
    }

    // =====================================================
    // GESTIÓN DE PERMISOS (ACTUALIZADO)
    // =====================================================
    
    /**
     * Lista todos los códigos de permisos activos
     * USA MÉTODO ESTÁTICO del nuevo PermisoDAO
     */
    public List<String> listarCodigosPermisosActivos() {
        return PermisoDAO.listarCodigosPermisosActivos();
    }

    /**
     * Asigna un permiso a un rol
     * USA MÉTODO ESTÁTICO del nuevo PermisoDAO
     */
    public void asignarPermisoARol(String rolNombre, String permisoCodigo) {
        long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
        if (idRol == 0) throw new IllegalArgumentException("No existe el rol: " + rolNombre);

        Long idPerm = PermisoDAO.obtenerIdPorCodigo(permisoCodigo);
        if (idPerm == null || idPerm == 0) {
            throw new IllegalArgumentException("No existe el permiso: " + permisoCodigo);
        }

        rolPermisoDAO.asignar(idRol, idPerm);
    }

    /**
     * Revoca un permiso de un rol
     * USA MÉTODO ESTÁTICO del nuevo PermisoDAO
     */
    public void revocarPermisoARol(String rolNombre, String permisoCodigo) {
        long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
        if (idRol == 0) throw new IllegalArgumentException("No existe el rol: " + rolNombre);

        Long idPerm = PermisoDAO.obtenerIdPorCodigo(permisoCodigo);
        if (idPerm == null || idPerm == 0) {
            throw new IllegalArgumentException("No existe el permiso: " + permisoCodigo);
        }

        rolPermisoDAO.revocar(idRol, idPerm);
    }

    public List<String> verPermisosDeRol(String rolNombre) {
        long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
        if (idRol == 0) {
            throw new IllegalArgumentException("No se pudo realizar la consulta: el rol no existe.");
        }

        if (!rolDAO.estaActivo(idRol)) {
            throw new IllegalArgumentException("No se pudo realizar la consulta: el rol se encuentra inactivo.");
        }

        return rolPermisoDAO.listarPermisosDeRol(idRol);
    }

    public List<RolPermisoDAO.RolPermisoRow> listarPermisosPorRoles() {
        return rolPermisoDAO.listarPermisosPorRoles();
    }

    // =====================================================
    // LISTADOS PARA UI
    // =====================================================
    public List<RolDAO.RolRow> listarRoles() {
        return rolDAO.listar();
    }

    public List<String> listarNombresRoles() {
        return rolDAO.listarNombresActivos();
    }

    // =====================================================
    // VALIDACIONES
    // =====================================================
    private boolean loginValido(String login) {
        // Alfanumérico, 6-20 caracteres, permite _ y ., sin espacios
        return login != null && login.matches("^[A-Za-z0-9_.]{6,20}$");
    }

    private boolean correoValido(String correo) {
        // Formato estándar de email (según Anexo E)
        if (correo == null) return false;
        if (!correo.equals(correo.trim())) return false;
        return correo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}