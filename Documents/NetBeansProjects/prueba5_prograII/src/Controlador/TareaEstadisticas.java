package Controlador;

import Logica.AnalizadorEstadisticas;
import Logica.OperacionCanceladaException;
import Logica.Recorredor;
import Logica.ResultadoEstadisticas;
import java.io.File;


public class TareaEstadisticas extends TareaSegundoPlano<ResultadoEstadisticas> {

    private final AnalizadorEstadisticas analizador;

   
    public TareaEstadisticas(Controlador controlador, Recorredor recorredor, AnalizadorEstadisticas analizador,
            File raiz, int profundidadMaxima) {
        super(controlador, recorredor, raiz, profundidadMaxima);
        this.analizador = analizador;
    }

    @Override
    protected ResultadoEstadisticas calcular() throws OperacionCanceladaException {
        return analizador.analizar(raiz, profundidadMaxima, this);
    }

    @Override
    protected void alTerminar(ResultadoEstadisticas resultado) {
        controlador.mostrarEstadisticas(resultado);
    }

    @Override
    protected String nombre() {
        return "Análisis";
    }
}
