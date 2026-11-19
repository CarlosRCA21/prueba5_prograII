package Logica;

import java.io.File;

/**
 * Permite avisar el avance de un recorrido sin depender de ninguna clase gráfica.
 * Quien implemente esta interfaz también puede cancelar el recorrido lanzando
 * una {@link OperacionCanceladaException}.
 */
public interface Observador {

    /**
     * Se llama una vez por cada archivo o carpeta visitado.
     *
     * @param elemento el archivo o carpeta visitado
     * @throws OperacionCanceladaException si el usuario canceló la operación
     */
    void elementoVisitado(File elemento) throws OperacionCanceladaException;
}
