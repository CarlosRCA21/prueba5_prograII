package Logica;

/**
 * Se lanza cuando falla una operación sobre archivos o carpetas (crear, renombrar, copiar, mover, eliminar, abrir, guardar).
 */
public class OperacionArchivoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje listo para mostrarse al usuario.
     *
     * @param mensaje explicación del problema
     */
    public OperacionArchivoException(String mensaje) {
        super(mensaje);
    }
}
