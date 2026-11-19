package Controlador;

import Logica.DetectorDuplicados;
import Logica.OperacionCanceladaException;
import Logica.Recorredor;
import java.io.File;
import java.util.List;
import java.util.Map;


public class TareaDuplicados extends TareaSegundoPlano<Map<String, List<File>>> {

    private final DetectorDuplicados detector;

   
    public TareaDuplicados(Controlador controlador, Recorredor recorredor, DetectorDuplicados detector,
            File raiz, int profundidadMaxima) {
        super(controlador, recorredor, raiz, profundidadMaxima);
        this.detector = detector;
    }

    @Override
    protected Map<String, List<File>> calcular() throws OperacionCanceladaException {
        return detector.buscar(raiz, profundidadMaxima, this);
    }

    @Override
    protected void alTerminar(Map<String, List<File>> resultado) {
        controlador.mostrarDuplicados(resultado);
    }

    @Override
    protected String nombre() {
        return "Detección de duplicados";
    }
}
