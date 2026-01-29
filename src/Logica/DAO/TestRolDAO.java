package Logica.DAO;

public class TestRolDAO {
    public static void main(String[] args) {
        RolDAO dao = new RolDAO();
        System.out.println("Roles en BD: " + dao.listarNombresRoles());
    }
}
