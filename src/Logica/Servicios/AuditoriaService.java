package Logica.Servicios;

import Logica.Entidades.AuditoriaEvento;
import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaService {

    private AuditoriaService() {}

    // ===== INSERTAR AUDITORÍA EN BD =====
    public static void registrar(String modulo, String accion, String resultado, String detalle) {

        String sql = """
            INSERT INTO auditoria
            (fecha_hora, usuario_login, rol, modulo, accion, resultado, detalle)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, SessionContext.getUsuarioLogin());
            ps.setString(3, SessionContext.getRol());
            ps.setString(4, modulo);
            ps.setString(5, accion);
            ps.setString(6, resultado);
            ps.setString(7, detalle);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR AUDITORIA BD: " + e.getMessage());
        }
    }

    // ===== LISTAR DESDE BD =====
    public static List<AuditoriaEvento> listarTodos() {

        List<AuditoriaEvento> lista = new ArrayList<>();

        String sql = """
            SELECT id_auditoria, fecha_hora, usuario_login, rol,
                   modulo, accion, resultado, detalle
            FROM auditoria
            ORDER BY fecha_hora DESC
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new AuditoriaEvento(
                        rs.getLong("id_auditoria"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime(),
                        rs.getString("usuario_login"),
                        rs.getString("rol"),
                        rs.getString("modulo"),
                        rs.getString("accion"),
                        rs.getString("resultado"),
                        rs.getString("detalle")
                ));
            }

        } catch (SQLException e) {
            System.err.println("ERROR LISTAR AUDITORIA: " + e.getMessage());
        }

        return lista;
    }
}
