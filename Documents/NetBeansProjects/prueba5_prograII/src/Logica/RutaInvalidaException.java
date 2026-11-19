package Logica;

/**
 * Se lanza cuando la ruta del directorio raíz está vacía, no existe, no es un directorio o no se puede leer.
 */
public class RutaInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje listo para mostrarse al usuario.
     *
     * @param mensaje explicación del problema
     */
    public RutaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
