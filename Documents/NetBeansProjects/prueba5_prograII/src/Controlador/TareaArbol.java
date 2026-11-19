package Controlador;

import Logica.OperacionCanceladaException;
import Logica.Recorredor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;


public class TareaArbol extends TareaSegundoPlano<DefaultMutableTreeNode> {

    
    public TareaArbol(Controlador controlador, Recorredor recorredor, File raiz, int profundidadMaxima) {
        super(controlador, recorredor, raiz, profundidadMaxima);
    }

    @Override
    protected DefaultMutableTreeNode calcular() throws OperacionCanceladaException {
        DefaultMutableTreeNode nodoRaiz = new DefaultMutableTreeNode(raiz);
        construir(nodoRaiz, raiz, 0);
        return nodoRaiz;
    }

    private void construir(DefaultMutableTreeNode nodoPadre, File carpeta, int profundidad)
            throws OperacionCanceladaException {
        if (!recorredor.puedeBajar(profundidad, profundidadMaxima)) {
            return;
        }
        List<File> hijos = ordenar(recorredor.listarHijos(carpeta));
        for (File hijo : hijos) {
            elementoVisitado(hijo);
            DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(hijo);
            nodoPadre.add(nodo);
            if (hijo.isDirectory()) {
                construir(nodo, hijo, profundidad + 1);
            }
        }
    }

    
    private List<File> ordenar(File[] hijos) {
        List<File> carpetas = new ArrayList<File>();
        List<File> archivos = new ArrayList<File>();
        for (File hijo : hijos) {
            if (hijo.isDirectory()) {
                carpetas.add(hijo);
            } else {
                archivos.add(hijo);
            }
        }
        Collections.sort(carpetas);
        Collections.sort(archivos);
        carpetas.addAll(archivos);
        return carpetas;
    }

    @Override
    protected void alTerminar(DefaultMutableTreeNode resultado) {
        controlador.mostrarArbol(resultado);
    }

    @Override
    protected String nombre() {
        return "Carga del árbol";
    }
}
