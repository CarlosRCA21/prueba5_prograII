package Controlador;

import Logica.Observador;
import Logica.OperacionCanceladaException;
import Logica.Recorredor;
import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;


public abstract class TareaSegundoPlano<T> extends SwingWorker<T, Integer> implements Observador {

    protected final Controlador controlador;
    protected final Recorredor recorredor;
    protected final File raiz;
    protected final int profundidadMaxima;

    private boolean contando = true;
    private long total = 0;
    private long visitados = 0;
    private int ultimoPorcentaje = -1;

   
    public TareaSegundoPlano(Controlador controlador, Recorredor recorredor, File raiz, int profundidadMaxima) {
        this.controlador = controlador;
        this.recorredor = recorredor;
        this.raiz = raiz;
        this.profundidadMaxima = profundidadMaxima;
    }

   
    protected abstract T calcular() throws OperacionCanceladaException;


    protected abstract void alTerminar(T resultado);

   
    protected abstract String nombre();

    @Override
    protected T doInBackground() throws Exception {
        total = recorredor.contarElementos(raiz, profundidadMaxima, this);
        contando = false;
        publish(0);
        return calcular();
    }

   
    @Override
    public void elementoVisitado(File elemento) throws OperacionCanceladaException {
        if (isCancelled()) {
            throw new OperacionCanceladaException();
        }
        if (contando) {
            return;
        }
        visitados++;
        int porcentaje = 100;
        try {
            porcentaje = (int) Math.min(100, visitados * 100 / total);
        } catch (ArithmeticException e) {
            porcentaje = 100;
        }
        if (porcentaje != ultimoPorcentaje) {
            ultimoPorcentaje = porcentaje;
            publish(porcentaje);
        }
    }

    @Override
    protected void process(List<Integer> porcentajes) {
        int ultimo = porcentajes.get(porcentajes.size() - 1);
        controlador.actualizarProgreso(ultimo);
    }

    @Override
    protected void done() {
        try {
            T resultado = get();
            alTerminar(resultado);
            controlador.tareaTerminada("Tarea terminada: " + nombre() + " (" + visitados + " elementos revisados).");
        } catch (CancellationException e) {
            controlador.tareaCancelada(nombre());
        } catch (InterruptedException e) {
            controlador.tareaCancelada(nombre());
        } catch (ExecutionException e) {
            controlador.tareaFallida(nombre(), e.getCause());
        }
    }
}
