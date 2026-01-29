//Anrior
package Logica.Servicios;

import Logica.DAO.UsuarioDAO;
import Logica.DAO.RolDAO;
import Logica.DAO.PermisoDAO;
import Logica.DAO.UsuarioRolDAO;
//import Logica.Servicios.GestionSistemaService.RegistroResult;
import Logica.DAO.RolPermisoDAO;

import java.util.List;


public class GestionSistemaService {
        // =========================
    // RESULTADO DE REGISTRO (RGS4–RGS5)
    // =========================
    public static class RegistroResult {
        public final long idUsuario;
        public final String passwordTemporal;

        public RegistroResult(long idUsuario, String passwordTemporal) {
            this.idUsuario = idUsuario;
            this.passwordTemporal = passwordTemporal;
        }
    }


    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final PermisoDAO permisoDAO = new PermisoDAO();
    private final UsuarioRolDAO usuarioRolDAO = new UsuarioRolDAO();
    private final RolPermisoDAO rolPermisoDAO = new RolPermisoDAO();


    // =========================
    // RGS4–RGS5: Registrar usuario (INACTIVO) + validar duplicados
    // =========================
 public String registrarUsuario(
        String login,
        String correo,
        String rol,
        String estado
) {
    try {
        if (!loginValido(login) || !correoValido(correo))
            return "No se pudo registrar el usuario: los datos ingresados no son válidos.";

        if (!estado.equals("Activo") && !estado.equals("Inactivo"))
            return "No se pudo registrar el usuario: los datos ingresados no son válidos.";

        if (usuarioDAO.existeLogin(login))
            return "No se pudo registrar el usuario: el nombre de usuario ya se encuentra registrado.";

        if (usuarioDAO.existeCorreo(correo))
            return "No se pudo registrar el usuario: el correo electrónico ya se encuentra registrado.";

        long idRol = rolDAO.obtenerIdPorNombre(rol);
        if (idRol == 0 || !rolDAO.estaActivo(idRol))
            return "No se pudo registrar el usuario: el rol especificado no existe o se encuentra inactivo.";

        String temp = PasswordUtil.generarTemporal(10);
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashSHA256(temp, salt);

        long idUsuario = usuarioDAO.insertar(login, correo, login, hash, salt);
        usuarioRolDAO.asignar(idUsuario, idRol);

        return "Usuario registrado correctamente.";

    } catch (Exception e) {
        return "No se pudo registrar el usuario: los datos ingresados no son válidos.";
    }
}

public String restablecerContrasena(String login) {

    // Validación de datos
    if (login == null || !login.matches("^[A-Za-z0-9_]{5,20}$")) {
        return "No se pudo restablecer la contraseña: el usuario no existe.";
    }

    UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
    if (u == null) {
        return "No se pudo restablecer la contraseña: el usuario no existe.";
    }

    // Generar contraseña temporal
    String temp = PasswordUtil.generarTemporal(10);
    String salt = PasswordUtil.generarSalt();
    String hash = PasswordUtil.hashSHA256(temp, salt);

    // Guardar contraseña y forzar cambio
    usuarioDAO.actualizarPassword(u.id, hash, salt, true);

    // 🔥 MENSAJE + CONTRASEÑA TEMPORAL
    return "Contraseña restablecida correctamente.\nContraseña temporal: " + temp;
}



    // Devuelve contraseña temporal para mostrarla en UI en un diálogo
    public String restablecerClave(long idUsuario) {
        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorId(idUsuario);
        if (u == null) throw new IllegalArgumentException("No existe el usuario con ID " + idUsuario);

        String temp = PasswordUtil.generarTemporal(10);
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashSHA256(temp, salt);

        // requiere cambio clave = true
        usuarioDAO.actualizarPassword(idUsuario, hash, salt, true);
        return temp;
    }

    // =========================
    // RGS7: Actualizar datos (validar correo no duplicado)
    // =========================
    public void actualizarUsuario(long idUsuario, String nuevoNombre, String nuevoCorreo) {
        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorId(idUsuario);
        if (u == null) throw new IllegalArgumentException("No existe el usuario con ID " + idUsuario);

        if (nuevoNombre == null || nuevoNombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        if (nuevoCorreo == null || nuevoCorreo.trim().isEmpty())
            throw new IllegalArgumentException("El correo es obligatorio.");

        if (usuarioDAO.existeCorreoEnOtro(nuevoCorreo, idUsuario)) {
            throw new IllegalArgumentException("El correo ya pertenece a otro usuario.");
        }

        usuarioDAO.actualizarDatos(idUsuario, nuevoNombre.trim(), nuevoCorreo.trim());
    }

    // =========================
    // RGS8: Activar / desactivar usuario
    // =========================
    public void setActivo(long idUsuario, boolean activo) {
        UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorId(idUsuario);
        if (u == null) throw new IllegalArgumentException("No existe el usuario con ID " + idUsuario);

        usuarioDAO.setActivo(idUsuario, activo);
    }

    // =========================
    // RGS3: Cambio de contraseña (validar actual, confirmar, etc.)
    // =========================
  public void cambiarContrasena(long idUsuario, String actualPlano,
                              String nuevaPlano, String confirmarPlano) {

    if (nuevaPlano == null || !nuevaPlano.equals(confirmarPlano))
        throw new IllegalArgumentException(
            "No se pudo cambiar la contraseña: la nueva contraseña no cumple el formato requerido."
        );

    if (nuevaPlano.length() < 8)
        throw new IllegalArgumentException(
            "No se pudo cambiar la contraseña: la nueva contraseña no cumple el formato requerido."
        );

    UsuarioDAO.PasswordData pd = usuarioDAO.obtenerHashSalt(idUsuario);
    if (pd == null)
        throw new IllegalArgumentException(
            "No se pudo cambiar la contraseña: el usuario no existe."
        );

    String hashActual = PasswordUtil.hashSHA256(actualPlano, pd.salt);
    if (!hashActual.equalsIgnoreCase(pd.hash))
        throw new IllegalArgumentException(
            "No se pudo cambiar la contraseña: la contraseña actual no corresponde al usuario."
        );

    String nuevoSalt = PasswordUtil.generarSalt();
    String nuevoHash = PasswordUtil.hashSHA256(nuevaPlano, nuevoSalt);

    usuarioDAO.actualizarPassword(idUsuario, nuevoHash, nuevoSalt, false);
}


   
    // =========================
// RGS10–RGS11: Roles
// =========================
public void crearRol(String nombre, boolean activo) {

    if (nombre == null || !nombre.matches("[A-Za-záéíóúÁÉÍÓÚñÑ -]{3,30}")) {
        throw new IllegalArgumentException(
                "No se pudo crear el rol: los datos ingresados no son válidos."
        );
    }

    if (rolDAO.existeNombre(nombre)) {
        throw new IllegalArgumentException(
                "No se pudo crear el rol: el nombre de rol ya se encuentra registrado."
        );
    }

    long idRol = rolDAO.insertar(nombre);
    rolDAO.setActivo(idRol, activo);
}


public void renombrarRol(long idRol, String nuevoNombre) {

    RolDAO.RolRow rol = rolDAO.obtenerPorId(idRol);
    if (rol == null) {
        throw new IllegalArgumentException(
            "No se pudo actualizar el rol: el rol no existe."
        );
    }

    if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "No se pudo actualizar el rol: el nuevo nombre ya se encuentra registrado."
        );
    }

    if (!nuevoNombre.matches("[A-Za-záéíóúÁÉÍÓÚñÑ -]{3,30}")) {
        throw new IllegalArgumentException(
            "No se pudo actualizar el rol: el nuevo nombre ya se encuentra registrado."
        );
    }

    if (rolDAO.existeNombreEnOtro(nuevoNombre, idRol)) {
        throw new IllegalArgumentException(
            "No se pudo actualizar el rol: el nuevo nombre ya se encuentra registrado."
        );
    }

    rolDAO.actualizarNombre(idRol, nuevoNombre.trim());
}


   


// =========================
// RGS12–RGS14: Roles por usuario
// =========================
public void asignarRolAUsuario(long idUsuario, String rolNombre) {

    // 🔹 Validar rol existe
    long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
    if (idRol == 0) {
        throw new IllegalArgumentException(
            "No se pudo modificar la asignación: el usuario o el rol no existen."
        );
    }

    // 🔹 VALIDACIÓN CLAVE (CP-04)
    if (!rolDAO.estaActivo(idRol)) {
        throw new IllegalArgumentException(
            "No se pudo modificar la asignación: el rol se encuentra inactivo."
        );
    }

    // 🔹 Revocar rol anterior (implícito)
    usuarioRolDAO.revocarTodos(idUsuario);

    // 🔹 Asignar nuevo rol
    usuarioRolDAO.asignar(idUsuario, idRol);
}



// =========================
// RGS13–RGS14: Permisos por rol
// =========================
public List<String> listarCodigosPermisosActivos() {
    return permisoDAO.listarCodigos();
}

public void asignarPermisoARol(String rolNombre, String permisoCodigo) {
    long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
    if (idRol == 0) throw new IllegalArgumentException("No existe el rol: " + rolNombre);

    long idPerm = permisoDAO.obtenerIdPorCodigo(permisoCodigo);
    if (idPerm == 0) throw new IllegalArgumentException("No existe el permiso: " + permisoCodigo);

    rolPermisoDAO.asignar(idRol, idPerm);
}

public void revocarPermisoARol(String rolNombre, String permisoCodigo) {
    long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
    if (idRol == 0) throw new IllegalArgumentException("No existe el rol: " + rolNombre);

    long idPerm = permisoDAO.obtenerIdPorCodigo(permisoCodigo);
    if (idPerm == 0) throw new IllegalArgumentException("No existe el permiso: " + permisoCodigo);

    rolPermisoDAO.revocar(idRol, idPerm);
}

public List<String> verPermisosDeRol(String rolNombre) {

    long idRol = rolDAO.obtenerIdPorNombre(rolNombre);
    if (idRol == 0)
        throw new IllegalArgumentException(
            "No se pudo realizar la consulta: el rol no existe."
        );

    if (!rolDAO.estaActivo(idRol))
        throw new IllegalArgumentException(
            "No se pudo realizar la consulta: el rol se encuentra inactivo."
        );

    return rolPermisoDAO.listarPermisosDeRol(idRol);
}

private boolean loginValido(String login) {
    return login != null && login.matches("^[A-Za-z0-9_]{5,20}$");
}
// =========================
// RGS10.2 – Actualizar nombre de rol (por nombre)
// =========================
public String actualizarNombreRol(String nombreActual, String nuevoNombre) {

    if (nombreActual == null || nuevoNombre == null)
        return "No se pudo actualizar el rol: el rol no existe.";

    nombreActual = nombreActual.trim();
    nuevoNombre = nuevoNombre.trim();

    long idRol = rolDAO.obtenerIdPorNombre(nombreActual);
    if (idRol == 0)
        return "No se pudo actualizar el rol: el rol no existe.";

    if (rolDAO.existeNombreEnOtro(nuevoNombre, idRol))
        return "No se pudo actualizar el rol: el nuevo nombre ya se encuentra registrado.";

    rolDAO.actualizarNombre(idRol, nuevoNombre);

    return "Nombre de rol actualizado correctamente.";
}



private boolean correoValido(String correo) {
    if (correo == null) return false;
    if (!correo.equals(correo.trim())) return false;
    return correo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
}
public String actualizarCorreo(String login, String nuevoCorreo) {

    if (!loginValido(login) || !correoValido(nuevoCorreo))
        return "No se pudo actualizar el correo electrónico: los datos ingresados no son válidos.";

    UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
    if (u == null)
        return "No se pudo actualizar el correo electrónico: el usuario no existe.";

    if (usuarioDAO.existeCorreoEnOtro(nuevoCorreo, u.id))
        return "No se pudo actualizar el correo electrónico: el correo electrónico ya se encuentra registrado.";

    usuarioDAO.actualizarCorreoPorLogin(login, nuevoCorreo);
    return "Correo electrónico actualizado correctamente.";
}
public String activarUsuario(String login) {

    UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
    if (u == null)
        return "No se pudo activar el usuario: el usuario no existe.";

    if (u.activo)
        return "No se pudo activar el usuario: el usuario ya se encuentra activo.";

    usuarioDAO.setActivo(u.id, true);
    return "Usuario activado correctamente.";
}
// =========================
// RGS10 – Listar roles
// =========================
public List<RolDAO.RolRow> listarRoles() {
    return rolDAO.listar();
}

// =========================
// RGS10.3 / RGS10.4 – Activar / Desactivar rol
// =========================
public void setRolActivo(long idRol, boolean activar) {

    RolDAO.RolRow rol = rolDAO.obtenerPorId(idRol);
    if (rol == null) {
        throw new IllegalArgumentException(
            activar
                ? "No se pudo activar el rol: el rol no existe."
                : "No se pudo desactivar el rol: el rol no existe."
        );
    }

    if (activar && rol.activo) {
        throw new IllegalArgumentException(
            "No se pudo activar el rol: el rol ya se encuentra activo."
        );
    }

    if (!activar && !rol.activo) {
        throw new IllegalArgumentException(
            "No se pudo desactivar el rol: el rol ya se encuentra inactivo."
        );
    }

    rolDAO.setActivo(idRol, activar);
}
// RGS10.3 – Activar rol
public String activarRolPorNombre(String nombreRol) {

    long idRol = rolDAO.obtenerIdPorNombre(nombreRol);
    if (idRol == 0)
        return "No se pudo activar el rol: el rol no existe.";

    RolDAO.RolRow rol = rolDAO.obtenerPorId(idRol);
    if (rol.activo)
        return "No se pudo activar el rol: el rol ya se encuentra activo.";

    rolDAO.setActivo(idRol, true);
    return "Rol activado correctamente.";
}

// RGS10.4 – Desactivar rol
public String desactivarRolPorNombre(String nombreRol) {

    long idRol = rolDAO.obtenerIdPorNombre(nombreRol);
    if (idRol == 0)
        return "No se pudo desactivar el rol: el rol no existe.";

    RolDAO.RolRow rol = rolDAO.obtenerPorId(idRol);
    if (!rol.activo)
        return "No se pudo desactivar el rol: el rol ya se encuentra inactivo.";

    rolDAO.setActivo(idRol, false);
    return "Rol desactivado correctamente.";
}

public String desactivarUsuario(String login) {

    UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorLogin(login);
    if (u == null)
        return "No se pudo desactivar el usuario: el usuario no existe.";

    if (!u.activo)
        return "No se pudo desactivar el usuario: el usuario ya se encuentra inactivo.";

    usuarioDAO.setActivo(u.id, false);
    return "Usuario desactivado correctamente.";
}

// =========================
// RGS10 – Listar nombres de roles (para UI)
// =========================
public List<String> listarNombresRoles() {
    return rolDAO.listarNombresActivos();
}

public List<RolPermisoDAO.RolPermisoRow> listarPermisosPorRoles() {
    return rolPermisoDAO.listarPermisosPorRoles();
}



}
