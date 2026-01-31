package Logica.Conexiones;

import Logica.DAO.RolDAO;
import Logica.DAO.UsuarioDAO;
import Logica.Servicios.PasswordUtil;
import Logica.Servicios.GestionSistemaService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConexion {
    public static void main(String[] args) {
        try (Connection c = ConexionBD.conectar();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT DATABASE()")) {

            rs.next();
            System.out.println("OK: Conectado. BD actual = " + rs.getString(1));

            RolDAO rdao = new RolDAO();
            System.out.println("Roles en BD: " + rdao.listarNombresRoles());

            // ===== PRUEBA UsuarioDAO =====
            UsuarioDAO udao = new UsuarioDAO();

            String temp = PasswordUtil.generarTemporal(10);
            String salt = PasswordUtil.generarSalt();
            String hash = PasswordUtil.hashSHA256(temp, salt);

            long idNuevo = udao.insertar("Usuario Prueba", "prueba@sctet.com", "prueba01", hash, salt);
            System.out.println("Usuario insertado ID=" + idNuevo + " tempPass=" + temp);

            System.out.println("Usuarios: " + udao.listar().size());

            GestionSistemaService gs = new GestionSistemaService();
            String temp2 = gs.restablecerClave(1);
            System.out.println("Reset clave usuario 1: temp=" + temp2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
