package Logica;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Búsqueda recursiva de archivos por nombre, combinada con los filtros de extensión,
 * tamaño y fecha de modificación.
 */
public class Buscador {

    private final Recorredor recorredor;

    /**
     * @param recorredor herramienta para listar carpetas
     */
    public Buscador(Recorredor recorredor) {
        this.recorredor = recorredor;
    }

    /**
     * Busca archivos que cumplan todos los filtros.
     *
     * @param raiz carpeta raíz
     * @param filtros filtros ya validados
     * @param profundidadMaxima límite de profundidad (0 = ilimitada)
     * @param observador recibe el avance y permite cancelar
     * @return lista de archivos encontrados (vacía si no hay coincidencias)
     * @throws OperacionCanceladaException si el usuario cancela
     */
    public List<File> buscar(File raiz, Filtros filtros, int profundidadMaxima, Observador observador)
            throws OperacionCanceladaException {
        List<File> encontrados = new ArrayList<File>();
        recorrer(raiz, 0, profundidadMaxima, filtros, encontrados, observador);
        return encontrados;
    }

    private void recorrer(File carpeta, int profundidad, int profundidadMaxima, Filtros filtros,
            List<File> encontrados, Observador observador) throws OperacionCanceladaException {
        if (!recorredor.puedeBajar(profundidad, profundidadMaxima)) {
            return;
        }
        File[] hijos = recorredor.listarHijos(carpeta);
        for (File hijo : hijos) {
            observador.elementoVisitado(hijo);
            if (hijo.isDirectory()) {
                recorrer(hijo, profundidad + 1, profundidadMaxima, filtros, encontrados, observador);
            } else if (filtros.cumple(hijo)) {
                encontrados.add(hijo);
            }
        }
    }
}
