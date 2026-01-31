package Logica.Servicios;

import Logica.DAO.SocioDAO;
import Logica.Entidades.Socio;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio para el Módulo de Socios
 * Contiene la lógica de negocio y validaciones según ERS v1.0
 * Implementa validaciones de Anexos A, B, C y D
 */
public class SocioService {
    
    private SocioDAO socioDAO;
    
    // Patrones de validación según anexos
    private static final Pattern PATTERN_CODIGO_SOCIO = Pattern.compile("^\\d{2}$");
    private static final Pattern PATTERN_REGISTRO_MUNICIPAL = Pattern.compile("^\\d{4}$");
    //private static final Pattern PATTERN_CEDULA = Pattern.compile("^\\d{10}$");
    private static final Pattern PATTERN_CELULAR = Pattern.compile("^09\\d{8}$");
    private static final Pattern PATTERN_NOMBRES = Pattern.compile("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]{1,100}$");

    public SocioService() {
        this.socioDAO = new SocioDAO();
    }

    /**
     * rso1v1.0 - Registrar un socio propietario con validaciones completas
     */
    public ResultadoOperacion registrarSocio(Socio socio) {
        try {
            // Validar código de socio (2 dígitos)
            if (!validarCodigoSocio(socio.getCodigoSocio())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: el código de socio debe ser numérico de 2 dígitos.");
            }
            
            // Validar registro municipal (4 dígitos)
            if (!validarRegistroMunicipal(socio.getRegistroMunicipal())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: el registro municipal debe ser numérico de 4 dígitos.");
            }
            
            // Validar cédula ecuatoriana según Anexo A
            if (!validarCedulaEcuatoriana(socio.getCedula())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: la cédula ingresada es inválida.");
            }
            
            // Validar nombres completos
            if (!validarNombresCompletos(socio.getNombresCompletos())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: los nombres deben contener solo letras y espacios (máximo 100 caracteres).");
            }
            
            // Validar dirección según Anexo B
            if (!validarDireccion(socio.getDireccion())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: la dirección no cumple con el formato requerido.");
            }
            
            // Validar número de celular según Anexo C
            if (!validarNumeroCelular(socio.getNumeroCelular())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: el número de celular ingresado es inválido.");
            }
            
            // Verificar duplicados
            if (socioDAO.existeSocioPorCodigo(socio.getCodigoSocio())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: el código de socio ya se encuentra previamente registrado.");
            }
            
            if (socioDAO.existeRegistroMunicipal(socio.getRegistroMunicipal())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: el registro municipal ya se encuentra previamente registrado.");
            }
            
            if (socioDAO.existeCedula(socio.getCedula())) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: la cédula de ciudadanía ya se encuentra previamente registrada.");
            }
            
            // Registrar el socio
            boolean registrado = socioDAO.registrarSocio(socio);
            
            if (registrado) {
                return new ResultadoOperacion(true, 
                    "Socio propietario registrado correctamente.");
            } else {
                return new ResultadoOperacion(false, 
                    "No se pudo registrar el socio. Inténtelo nuevamente.");
            }
            
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * rso2v1.0 - Consultar un socio propietario por código
     */
    public ResultadoOperacion consultarSocioPorCodigo(String codigoSocio) {
        try {
            if (!validarCodigoSocio(codigoSocio)) {
                return new ResultadoOperacion(false, 
                    "El código de socio debe ser numérico de 2 dígitos.");
            }
            
            Socio socio = socioDAO.consultarSocioPorCodigo(codigoSocio);
            
            if (socio != null) {
                return new ResultadoOperacion(true, 
                    "Socio encontrado correctamente.", socio);
            } else {
                return new ResultadoOperacion(false, 
                    "No se encontró un socio propietario con el código ingresado.");
            }
            
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * rso3v1.0 - Actualizar número de celular del socio
     */
    public ResultadoOperacion actualizarNumeroCelular(String codigoSocio, String nuevoNumeroCelular) {
        try {
            if (!validarCodigoSocio(codigoSocio)) {
                return new ResultadoOperacion(false, 
                    "El código de socio debe ser numérico de 2 dígitos.");
            }
            
            if (!validarNumeroCelular(nuevoNumeroCelular)) {
                return new ResultadoOperacion(false, 
                    "No se puede registrar el socio: el número de celular ingresado es inválido.");
            }
            
            // Verificar que el socio existe
            if (!socioDAO.existeSocioPorCodigo(codigoSocio)) {
                return new ResultadoOperacion(false, 
                    "No se encontró un socio propietario con el código ingresado.");
            }
            
            boolean actualizado = socioDAO.actualizarNumeroCelular(codigoSocio, nuevoNumeroCelular);
            
            if (actualizado) {
                return new ResultadoOperacion(true, 
                    "El número de celular del socio ha sido actualizado correctamente.");
            } else {
                return new ResultadoOperacion(false, 
                    "No se pudo actualizar el número de celular.");
            }
            
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * rso4v1.0 - Actualizar dirección del socio
     */
    public ResultadoOperacion actualizarDireccion(String codigoSocio, String nuevaDireccion) {
        try {
            if (!validarCodigoSocio(codigoSocio)) {
                return new ResultadoOperacion(false, 
                    "El código de socio debe ser numérico de 2 dígitos.");
            }
            
            if (!validarDireccion(nuevaDireccion)) {
                return new ResultadoOperacion(false, 
                    "La dirección no cumple con el formato requerido.");
            }
            
            // Verificar que el socio existe
            if (!socioDAO.existeSocioPorCodigo(codigoSocio)) {
                return new ResultadoOperacion(false, 
                    "No se encontró un socio propietario con el código ingresado.");
            }
            
            boolean actualizado = socioDAO.actualizarDireccion(codigoSocio, nuevaDireccion);
            
            if (actualizado) {
                return new ResultadoOperacion(true, 
                    "La dirección del socio ha sido actualizada correctamente.");
            } else {
                return new ResultadoOperacion(false, 
                    "No se pudo actualizar la dirección.");
            }
            
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * ru5v1.0 - Listar socios propietarios registrados
     */
    public ResultadoOperacion listarSocios() {
        try {
            List<Socio> socios = socioDAO.listarSocios();
            
            if (socios.isEmpty()) {
                return new ResultadoOperacion(false, 
                    "No hay socios propietarios registrados en el sistema.");
            }
            
            return new ResultadoOperacion(true, 
                "Listado de socios obtenido correctamente.", socios);
                
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * Listar socios por estado
     */
    public ResultadoOperacion listarSociosPorEstado(String estado) {
        try {
            List<Socio> socios = socioDAO.listarSociosPorEstado(estado);
            
            if (socios.isEmpty()) {
                return new ResultadoOperacion(false, 
                    "No hay socios propietarios " + estado.toLowerCase() + "s en el sistema.");
            }
            
            return new ResultadoOperacion(true, 
                "Listado de socios " + estado.toLowerCase() + "s obtenido correctamente.", socios);
                
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    /**
     * Activar un socio
     */
    public ResultadoOperacion activarSocio(String codigoSocio) {
        return cambiarEstadoSocio(codigoSocio, "ACTIVO");
    }

    /**
     * Desactivar un socio
     */
    public ResultadoOperacion desactivarSocio(String codigoSocio) {
        return cambiarEstadoSocio(codigoSocio, "INACTIVO");
    }

    /**
     * Cambiar estado del socio
     */
    private ResultadoOperacion cambiarEstadoSocio(String codigoSocio, String nuevoEstado) {
        try {
            if (!validarCodigoSocio(codigoSocio)) {
                return new ResultadoOperacion(false, 
                    "El código de socio debe ser numérico de 2 dígitos.");
            }
            
            Socio socio = socioDAO.consultarSocioPorCodigo(codigoSocio);
            
            if (socio == null) {
                return new ResultadoOperacion(false, 
                    "No se encontró un socio propietario con el código ingresado.");
            }
            
            if (socio.getEstado().equals(nuevoEstado)) {
                return new ResultadoOperacion(false, 
                    "El socio ya se encuentra " + nuevoEstado.toLowerCase() + ".");
            }
            
            boolean actualizado = socioDAO.actualizarEstado(codigoSocio, nuevoEstado);
            
            if (actualizado) {
                return new ResultadoOperacion(true, 
                    "Estado del socio actualizado a " + nuevoEstado + " correctamente.");
            } else {
                return new ResultadoOperacion(false, 
                    "No se pudo actualizar el estado del socio.");
            }
            
        } catch (SQLException e) {
            return new ResultadoOperacion(false, 
                "Error en la base de datos: " + e.getMessage());
        }
    }

    // ==================== MÉTODOS DE VALIDACIÓN ====================

    /**
     * Validar código de socio (2 dígitos: 00-99)
     */
    private boolean validarCodigoSocio(String codigo) {
        return codigo != null && PATTERN_CODIGO_SOCIO.matcher(codigo).matches();
    }

    /**
     * Validar registro municipal (4 dígitos: 0000-9999)
     */
    private boolean validarRegistroMunicipal(String registro) {
        return registro != null && PATTERN_REGISTRO_MUNICIPAL.matcher(registro).matches();
    }

    /**
     * Validar nombres completos (solo letras, espacios, hasta 100 caracteres)
     */
    private boolean validarNombresCompletos(String nombres) {
        return nombres != null && !nombres.trim().isEmpty() && 
               PATTERN_NOMBRES.matcher(nombres).matches();
    }

    /**
     * Anexo A - Validar cédula ecuatoriana (10 dígitos con algoritmo del módulo 10)
     */
 private boolean validarCedulaEcuatoriana(String cedula) {
    if (cedula == null || !cedula.matches("\\d{10}")) {
        return false;
    }

    // 1. Validar provincia (01–24)
    int provincia = Integer.parseInt(cedula.substring(0, 2));
    if (provincia < 1 || provincia > 24) {
        return false;
    }

    // 2. Validar tercer dígito (0–5)
    int tercerDigito = Character.getNumericValue(cedula.charAt(2));
    if (tercerDigito < 0 || tercerDigito > 5) {
        return false;
    }

    // 3. Algoritmo módulo 10
    int[] coeficientes = {2,1,2,1,2,1,2,1,2};
    int suma = 0;

    for (int i = 0; i < 9; i++) {
        int digito = Character.getNumericValue(cedula.charAt(i));
        int producto = digito * coeficientes[i];
        if (producto >= 10) {
            producto -= 9;
        }
        suma += producto;
    }

    int residuo = suma % 10;
    int digitoVerificadorCalculado = (residuo == 0) ? 0 : 10 - residuo;
    int digitoVerificadorReal = Character.getNumericValue(cedula.charAt(9));

    return digitoVerificadorCalculado == digitoVerificadorReal;
}


    /**
     * Anexo B - Validar dirección (estructura definida)
     * Calle principal + Número + Calle secundaria
     * Formato: tipo + dirección alfanumérica (hasta 15 caracteres) + carácter especial + 
     *          longitud entre 12 y 15 + letras A-Z mayúsculas + 
     *          letras del 0-9 + carácter especial "-"
     */
   private boolean validarDireccion(String direccion) {
    if (direccion == null || direccion.trim().isEmpty()) {
        return false;
    }

    // Debe contener dos partes separadas por coma
    String[] partes = direccion.split(",");

    if (partes.length != 2) {
        return false; // no hay calle principal y secundaria
    }

    String callePrincipal = partes[0].trim();
    String calleSecundaria = partes[1].trim();

    // Patrón permitido: letras, números, espacios y ñ
    Pattern patronCalle = Pattern.compile("^[a-zA-Z0-9ñÑ ]{1,15}$");

    return patronCalle.matcher(callePrincipal).matches()
        && patronCalle.matcher(calleSecundaria).matches();
}

    /**
     * Anexo C - Validar número de celular ecuatoriano
     * 10 dígitos que debe iniciar con "09"
     * Formato: 09XXXXXXXX
     */
    private boolean validarNumeroCelular(String celular) {
        return celular != null && PATTERN_CELULAR.matcher(celular).matches();
    }
}