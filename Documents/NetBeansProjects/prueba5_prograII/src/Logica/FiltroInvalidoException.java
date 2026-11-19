package Logica;

/**
 * Se lanza cuando un filtro o la profundidad tienen un valor inválido (texto, números o fechas).
 */
public class FiltroInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje listo para mostrarse al usuario.
     *
     * @param mensaje explicación del problema
     */
    public FiltroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
