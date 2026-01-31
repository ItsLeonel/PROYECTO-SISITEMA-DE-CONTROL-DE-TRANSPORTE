package testing;

import Logica.Servicios.PasswordUtil;

/**
 * EJECUTA ESTE ARCHIVO DESDE TU IDE
 * 
 * 1. Crea carpeta: src/testing/
 * 2. Pon este archivo ahí: GeneradorHashesBD.java
 * 3. Clic derecho → Run (o Shift+F10)
 * 4. Copia los SQL de la consola
 * 5. Pégalos en MySQL Workbench
 * 6. Ejecútalos
 * 7. ¡Listo! Podrás hacer login
 */
public class GeneradorHashesBD {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("HASHES PARA RESETEAR CONTRASEÑAS");
        System.out.println("==============================================\n");

        // Usuario 1: Leonel
        generarSQL("Leonel", "leonel123");
        
        // Usuario 2: prueba01
        generarSQL("prueba01", "prueba123");
        
        // Usuario 3: test
        generarSQL("test", "test123");

        System.out.println("\n==============================================");
        System.out.println("COPIA Y EJECUTA ESTOS UPDATE EN MYSQL");
        System.out.println("==============================================");
    }

    private static void generarSQL(String login, String password) {
        // Usa TU PasswordUtil
        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.hashSHA256(password, salt);
        
        System.out.println("-- Usuario: " + login + " | Password: " + password);
        System.out.println("UPDATE usuario");
        System.out.println("SET password_hash = '" + hash + "',");
        System.out.println("    password_salt = '" + salt + "',");
        System.out.println("    requiere_cambio_clave = 0");
        System.out.println("WHERE login = '" + login + "';");
        System.out.println();
    }
}