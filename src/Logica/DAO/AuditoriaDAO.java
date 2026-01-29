package Logica.DAO;

import Logica.Entidades.AuditoriaEvento;
import Logica.Conexiones.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public void insertar(AuditoriaEvento ev) {

        String sql = """
            INSERT INTO auditoria
            (fecha_hora, usuario_login, rol, modulo, accion, resultado, detalle)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(ev.getFechaHora()));
            ps.setString(2, ev.getUsuarioLogin());
            ps.setString(3, ev.getRol());
            ps.setString(4, ev.getModulo());
            ps.setString(5, ev.getAccion());
            ps.setString(6, ev.getResultado());
            ps.setString(7, ev.getDetalle());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar auditoría", e);
        }
    }

    public List<AuditoriaEvento> listarTodos() {

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
            throw new RuntimeException("Error al listar auditoría", e);
        }

        return lista;
    }
}
