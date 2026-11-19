package Logica;

/**
 * Se lanza para detener un recorrido cuando el usuario pulsa el botón Cancelar.
 */
public class OperacionCanceladaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción de cancelación.
     */
    public OperacionCanceladaException() {
        super("Operación cancelada por el usuario.");
    }
}
