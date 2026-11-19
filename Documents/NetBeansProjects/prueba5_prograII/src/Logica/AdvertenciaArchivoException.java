package Logica;

/**
 * Se lanza al abrir un archivo que es demasiado grande o que no parece de texto.
 * No es un error grave: el usuario puede decidir abrirlo de todos modos.
 */
public class AdvertenciaArchivoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje descripción de la advertencia
     */
    public AdvertenciaArchivoException(String mensaje) {
        super(mensaje);
    }
}
