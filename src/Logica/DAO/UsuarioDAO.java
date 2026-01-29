package Logica.DAO;

import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ===== DTO interno para listar en tablas =====
    public static class UsuarioRow {
        public long id;
        public String nombre;
        public String correo;
        public String login;
        public boolean activo;
        public boolean requiereCambioClave;

        public UsuarioRow(long id, String nombre, String correo, String login, boolean activo, boolean requiereCambioClave) {
            this.id = id;
            this.nombre = nombre;
            this.correo = correo;
            this.login = login;
            this.activo = activo;
            this.requiereCambioClave = requiereCambioClave;
        }
    }

    // ===== LISTAR =====
    public List<UsuarioRow> listar() {
        String sql = """
            SELECT id_usuario, nombre, correo, login, activo, requiere_cambio_clave
            FROM usuario
            ORDER BY id_usuario
            """;
        List<UsuarioRow> out = new ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios.", e);
        }
        return out;
    }

    // ===== OBTENER POR ID =====
    public UsuarioRow obtenerPorId(long id) {
        String sql = """
            SELECT id_usuario, nombre, correo, login, activo, requiere_cambio_clave
            FROM usuario
            WHERE id_usuario = ?
            """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar usuario por ID.", e);
        }
    }

    // ===== LISTAR POR ROL (para rgs6) =====
    public List<UsuarioRow> listarPorRolNombre(String rolNombre) {
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.login, u.activo, u.requiere_cambio_clave
            FROM usuario u
            JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
            JOIN rol r ON r.id_rol = ur.id_rol
            WHERE r.nombre = ?
            ORDER BY u.id_usuario
            """;

        List<UsuarioRow> out = new ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, rolNombre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios por rol.", e);
        }

        return out;
    }

    // ===== VALIDACIONES DUPLICADOS (rgs5 / rgs7) =====
    public boolean existeCorreo(String correo) {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(correo)=LOWER(?) LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando correo duplicado.", e);
        }
    }

    public boolean existeLogin(String login) {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(login)=LOWER(?) LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando login duplicado.", e);
        }
    }

    public boolean existeCorreoEnOtro(String correo, long idUsuario) {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(correo)=LOWER(?) AND id_usuario<>? LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setLong(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando correo duplicado (update).", e);
        }
    }

    // ===== INSERTAR (rgs4: INACTIVO por defecto / requiere cambio clave = 1) =====
    public long insertar(String nombre, String correo, String login, String passwordHash, String passwordSalt) {
        String sql = """
            INSERT INTO usuario(nombre, correo, login, password_hash, password_salt, activo, requiere_cambio_clave)
            VALUES (?, ?, ?, ?, ?, 0, 1)
            """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, login);
            ps.setString(4, passwordHash);
            ps.setString(5, passwordSalt);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario.", e);
        }
    }

    // ===== ACTUALIZAR DATOS (rgs7) =====
    // En tu prototipo actualizas nombre/correo por ID.
    public void actualizarDatos(long idUsuario, String nombre, String correo) {
        String sql = "UPDATE usuario SET nombre=?, correo=? WHERE id_usuario=?";

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setLong(3, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario.", e);
        }
    }

    // ===== ACTIVAR / DESACTIVAR (rgs8) =====
    public void setActivo(long idUsuario, boolean activo) {
        String sql = "UPDATE usuario SET activo=? WHERE id_usuario=?";

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setLong(2, idUsuario);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado del usuario.", e);
        }
    }

    // ===== PASSWORD (rgs3 / rgs9) =====
    public static class PasswordData {
        public final String hash;
        public final String salt;
        public PasswordData(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
    }

    public PasswordData obtenerHashSalt(long idUsuario) {
        String sql = "SELECT password_hash, password_salt FROM usuario WHERE id_usuario=?";

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new PasswordData(rs.getString("password_hash"), rs.getString("password_salt"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo credenciales del usuario.", e);
        }
    }

    public void actualizarPassword(long idUsuario, String nuevoHash, String nuevoSalt, boolean requiereCambio) {
        String sql = "UPDATE usuario SET password_hash=?, password_salt=?, requiere_cambio_clave=? WHERE id_usuario=?";

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nuevoHash);
            ps.setString(2, nuevoSalt);
            ps.setBoolean(3, requiereCambio);
            ps.setLong(4, idUsuario);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar contraseña.", e);
        }
    }

    // ===== Mapper =====
    private UsuarioRow mapRow(ResultSet rs) throws SQLException {
        return new UsuarioRow(
                rs.getLong("id_usuario"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("login"),
                rs.getBoolean("activo"),
                rs.getBoolean("requiere_cambio_clave")
        );
    }
        // ===== Para SessionContext: obtener ID por login =====
    public long obtenerIdPorLogin(String login) {
        String sql = "SELECT id_usuario FROM usuario WHERE LOWER(login)=LOWER(?) LIMIT 1";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener id por login.", e);
        }
    }

    // ===== Para tabla: traemos roles concatenados en una sola consulta =====
    public static class UsuarioTablaRow {
        public long id;
        public String nombre;
        public String correo;
        public String login;
        public String roles;     // Ej: "AdministradorDelSistema,IngenieroDeTI"
        public boolean activo;

        public UsuarioTablaRow(long id, String nombre, String correo, String login, String roles, boolean activo) {
            this.id = id;
            this.nombre = nombre;
            this.correo = correo;
            this.login = login;
            this.roles = roles == null ? "" : roles;
            this.activo = activo;
        }
    }

    public java.util.List<UsuarioTablaRow> listarParaTabla() {
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.login, u.activo,
                   GROUP_CONCAT(r.nombre ORDER BY r.nombre SEPARATOR ',') AS roles
            FROM usuario u
            LEFT JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
            LEFT JOIN rol r ON r.id_rol = ur.id_rol
            GROUP BY u.id_usuario, u.nombre, u.correo, u.login, u.activo
            ORDER BY u.id_usuario
            """;

        java.util.List<UsuarioTablaRow> out = new java.util.ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new UsuarioTablaRow(
                        rs.getLong("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("login"),
                        rs.getString("roles"),
                        rs.getBoolean("activo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios para tabla.", e);
        }
        return out;
    }

    public UsuarioTablaRow obtenerParaTablaPorId(long idUsuario) {
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.login, u.activo,
                   GROUP_CONCAT(r.nombre ORDER BY r.nombre SEPARATOR ',') AS roles
            FROM usuario u
            LEFT JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
            LEFT JOIN rol r ON r.id_rol = ur.id_rol
            WHERE u.id_usuario = ?
            GROUP BY u.id_usuario, u.nombre, u.correo, u.login, u.activo
            LIMIT 1
            """;

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new UsuarioTablaRow(
                        rs.getLong("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("login"),
                        rs.getString("roles"),
                        rs.getBoolean("activo")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener usuario para tabla por ID.", e);
        }
    }

    public java.util.List<UsuarioTablaRow> listarParaTablaPorRol(String rolNombre) {
        String sql = """
            SELECT u.id_usuario, u.nombre, u.correo, u.login, u.activo,
                   GROUP_CONCAT(r2.nombre ORDER BY r2.nombre SEPARATOR ',') AS roles
            FROM usuario u
            JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
            JOIN rol r ON r.id_rol = ur.id_rol
            LEFT JOIN usuario_rol ur2 ON ur2.id_usuario = u.id_usuario
            LEFT JOIN rol r2 ON r2.id_rol = ur2.id_rol
            WHERE r.nombre = ?
            GROUP BY u.id_usuario, u.nombre, u.correo, u.login, u.activo
            ORDER BY u.id_usuario
            """;

        java.util.List<UsuarioTablaRow> out = new java.util.ArrayList<>();

        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, rolNombre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new UsuarioTablaRow(
                            rs.getLong("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("login"),
                            rs.getString("roles"),
                            rs.getBoolean("activo")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios para tabla por rol.", e);
        }

        return out;
    }
    public UsuarioRow obtenerPorLogin(String login) {
    String sql = """
        SELECT id_usuario, nombre, correo, login, activo, requiere_cambio_clave
        FROM usuario
        WHERE LOWER(login) = LOWER(?)
        LIMIT 1
        """;

    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, login);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return null;
            return mapRow(rs);
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error al consultar usuario por login.", e);
    }
}
public boolean estaActivoPorLogin(String login) {
    String sql = "SELECT activo FROM usuario WHERE LOWER(login)=LOWER(?) LIMIT 1";

    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, login);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return false;
            return rs.getBoolean("activo");
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error al consultar estado del usuario.", e);
    }
}
public void actualizarCorreoPorLogin(String login, String correo) {
    String sql = "UPDATE usuario SET correo=? WHERE LOWER(login)=LOWER(?)";

    try (Connection c = ConexionBD.conectar();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, correo);
        ps.setString(2, login);
        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Error al actualizar correo por login.", e);
    }
}




}
