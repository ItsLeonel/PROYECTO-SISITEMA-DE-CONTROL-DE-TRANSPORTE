
package Logica.DAO;

import Logica.Entidades.Ruta;

public class TestRutaDAO {
    public static void main(String[] args) {
        RutaDAO dao = new RutaDAO();
        Ruta r = dao.buscarPorCodigo("01");

        if (r != null) {
            System.out.println("Nombre: " + r.getNombre());
        } else {
            System.out.println("No encontrada");
        }
    }
}
