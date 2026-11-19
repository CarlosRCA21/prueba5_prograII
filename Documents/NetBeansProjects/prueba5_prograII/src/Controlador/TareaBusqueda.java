package Controlador;

import Logica.Buscador;
import Logica.Filtros;
import Logica.OperacionCanceladaException;
import Logica.Recorredor;
import java.io.File;
import java.util.List;


public class TareaBusqueda extends TareaSegundoPlano<List<File>> {

    private final Buscador buscador;
    private final Filtros filtros;

    
    public TareaBusqueda(Controlador controlador, Recorredor recorredor, Buscador buscador, Filtros filtros,
            File raiz, int profundidadMaxima) {
        super(controlador, recorredor, raiz, profundidadMaxima);
        this.buscador = buscador;
        this.filtros = filtros;
    }

    @Override
    protected List<File> calcular() throws OperacionCanceladaException {
        return buscador.buscar(raiz, filtros, profundidadMaxima, this);
    }

    @Override
    protected void alTerminar(List<File> resultado) {
        controlador.mostrarResultadosBusqueda(resultado);
    }

    @Override
    protected String nombre() {
        return "Búsqueda";
    }
}
