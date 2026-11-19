package Logica;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Localiza archivos con el mismo nombre y el mismo tamaño en distintas carpetas
 * y los agrupa en un Map. La clave del mapa es "nombre|tamaño en bytes" y el nombre
 * se compara sin distinguir mayúsculas de minúsculas.
 */
public class DetectorDuplicados {

    private final Recorredor recorredor;

    /**
     * @param recorredor herramienta para listar carpetas
     */
    public DetectorDuplicados(Recorredor recorredor) {
        this.recorredor = recorredor;
    }

    /**
     * Busca grupos de archivos duplicados.
     *
     * @param raiz carpeta raíz
     * @param profundidadMaxima límite de profundidad (0 = ilimitada)
     * @param observador recibe el avance y permite cancelar
     * @return solo los grupos con 2 o más archivos en carpetas distintas
     * @throws OperacionCanceladaException si el usuario cancela
     */
    public Map<String, List<File>> buscar(File raiz, int profundidadMaxima, Observador observador)
            throws OperacionCanceladaException {
        Map<String, List<File>> todos = new TreeMap<String, List<File>>();
        recorrer(raiz, 0, profundidadMaxima, todos, observador);
        return soloDuplicados(todos);
    }

    private void recorrer(File carpeta, int profundidad, int profundidadMaxima,
            Map<String, List<File>> todos, Observador observador) throws OperacionCanceladaException {
        if (!recorredor.puedeBajar(profundidad, profundidadMaxima)) {
            return;
        }
        File[] hijos = recorredor.listarHijos(carpeta);
        for (File hijo : hijos) {
            observador.elementoVisitado(hijo);
            if (hijo.isDirectory()) {
                recorrer(hijo, profundidad + 1, profundidadMaxima, todos, observador);
            } else {
                agregar(hijo, todos);
            }
        }
    }

    private void agregar(File archivo, Map<String, List<File>> todos) {
        String clave = archivo.getName().toLowerCase(Locale.ROOT) + "|" + archivo.length();
        List<File> grupo = todos.get(clave);
        if (grupo == null) {
            grupo = new ArrayList<File>();
            todos.put(clave, grupo);
        }
        grupo.add(archivo);
    }

    private Map<String, List<File>> soloDuplicados(Map<String, List<File>> todos) {
        Map<String, List<File>> duplicados = new LinkedHashMap<String, List<File>>();
        for (Map.Entry<String, List<File>> grupo : todos.entrySet()) {
            if (estanEnDistintasCarpetas(grupo.getValue())) {
                duplicados.put(grupo.getKey(), grupo.getValue());
            }
        }
        return duplicados;
    }

    private boolean estanEnDistintasCarpetas(List<File> archivos) {
        if (archivos.size() < 2) {
            return false;
        }
        File primeraCarpeta = archivos.get(0).getParentFile();
        for (File archivo : archivos) {
            if (!archivo.getParentFile().equals(primeraCarpeta)) {
                return true;
            }
        }
        return false;
    }
}
