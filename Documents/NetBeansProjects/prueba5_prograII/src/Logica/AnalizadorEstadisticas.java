package Logica;

import java.io.File;

/**
 * Recorre una carpeta de forma recursiva y calcula las estadísticas por extensión y globales.
 */
public class AnalizadorEstadisticas {

    private final Recorredor recorredor;

    /**
     * @param recorredor herramienta para listar carpetas
     */
    public AnalizadorEstadisticas(Recorredor recorredor) {
        this.recorredor = recorredor;
    }

    /**
     * Analiza la carpeta raíz y todas sus subcarpetas.
     *
     * @param raiz carpeta raíz
     * @param profundidadMaxima límite de profundidad (0 = ilimitada)
     * @param observador recibe el avance y permite cancelar
     * @return las estadísticas calculadas
     * @throws OperacionCanceladaException si el usuario cancela
     */
    public ResultadoEstadisticas analizar(File raiz, int profundidadMaxima, Observador observador)
            throws OperacionCanceladaException {
        ResultadoEstadisticas resultado = new ResultadoEstadisticas(raiz.getAbsolutePath(), profundidadMaxima);
        recorrer(raiz, 0, profundidadMaxima, resultado, observador);
        return resultado;
    }

    private void recorrer(File carpeta, int profundidad, int profundidadMaxima,
            ResultadoEstadisticas resultado, Observador observador) throws OperacionCanceladaException {
        if (!recorredor.puedeBajar(profundidad, profundidadMaxima)) {
            return;
        }
        File[] hijos = recorredor.listarHijos(carpeta);
        for (File hijo : hijos) {
            observador.elementoVisitado(hijo);
            int profundidadHijo = profundidad + 1;
            resultado.registrarProfundidad(profundidadHijo);
            if (hijo.isDirectory()) {
                resultado.agregarCarpeta(estaVacia(hijo));
                recorrer(hijo, profundidadHijo, profundidadMaxima, resultado, observador);
            } else {
                resultado.agregarArchivo(hijo);
            }
        }
    }

    private boolean estaVacia(File carpeta) {
        String[] nombres = carpeta.list();
        return nombres != null && nombres.length == 0;
    }
}
