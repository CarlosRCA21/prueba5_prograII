package Logica;

/**
 * Se lanza cuando el archivo o carpeta de destino ya existe y no se pidió sobrescribirlo.
 * Quien la captura puede pedir confirmación al usuario y repetir la operación sobrescribiendo.
 */
public class DestinoExistenteException extends OperacionArchivoException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicación del problema
     */
    public DestinoExistenteException(String mensaje) {
        super(mensaje);
    }
}
