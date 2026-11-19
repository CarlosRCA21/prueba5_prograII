package Controlador;

import Logica.AdvertenciaArchivoException;
import Logica.AnalizadorEstadisticas;
import Logica.Bitacora;
import Logica.Buscador;
import Logica.Configuracion;
import Logica.DestinoExistenteException;
import Logica.DetectorDuplicados;
import Logica.EditorTexto;
import Logica.FiltroInvalidoException;
import Logica.Filtros;
import Logica.GestorArchivos;
import Logica.OperacionArchivoException;
import Logica.Recorredor;
import Logica.Reporte;
import Logica.ResultadoEstadisticas;
import Logica.RutaInvalidaException;
import Vista.VentanaPrincipal;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;


public class Controlador {

    private final Bitacora bitacora;
    private final Configuracion configuracion;
    private final Recorredor recorredor;
    private final AnalizadorEstadisticas analizador;
    private final Buscador buscador;
    private final DetectorDuplicados detector;
    private final GestorArchivos gestor;
    private final EditorTexto editor;
    private final Reporte reporte;

    private VentanaPrincipal vista = null;
    private TareaSegundoPlano<?> tareaActual = null;
    private ResultadoEstadisticas ultimaEstadistica = null;

   
    public Controlador() {
        File carpetaDatos = new File(System.getProperty("user.dir"));
        bitacora = new Bitacora(new File(carpetaDatos, "bitacora.log"));
        configuracion = new Configuracion(new File(carpetaDatos, "config.txt"));
        recorredor = new Recorredor(bitacora);
        analizador = new AnalizadorEstadisticas(recorredor);
        buscador = new Buscador(recorredor);
        detector = new DetectorDuplicados(recorredor);
        gestor = new GestorArchivos(recorredor);
        editor = new EditorTexto();
        reporte = new Reporte();
    }

   
    public void setVista(VentanaPrincipal vista) {
        this.vista = vista;
    }

    
    public Map<String, String> cargarConfiguracion() {
        return configuracion.cargar();
    }

    
    public void guardarConfiguracion(Map<String, String> valores) {
        try {
            configuracion.guardar(valores);
        } catch (IOException e) {
            vista.mostrarError("No se pudo guardar la configuración: " + e.getMessage());
        }
    }

    
    public String leerBitacora() {
        try {
            return bitacora.leerTodo();
        } catch (IOException e) {
            return "(La bitácora todavía no tiene registros)";
        }
    }

    
    public String rutaBitacora() {
        return bitacora.getArchivo().getAbsolutePath();
    }

    

    
    public void cargarArbol(String ruta, String profundidad) {
        try {
            File raiz = recorredor.validarRaiz(ruta);
            int max = Filtros.leerProfundidad(profundidad);
            bitacora.registrar("CARGAR_ARBOL", raiz.getAbsolutePath());
            iniciar(new TareaArbol(this, recorredor, raiz, max));
        } catch (RutaInvalidaException | FiltroInvalidoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void analizar(String ruta, String profundidad) {
        try {
            File raiz = recorredor.validarRaiz(ruta);
            int max = Filtros.leerProfundidad(profundidad);
            bitacora.registrar("ANALIZAR", raiz.getAbsolutePath());
            iniciar(new TareaEstadisticas(this, recorredor, analizador, raiz, max));
        } catch (RutaInvalidaException | FiltroInvalidoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void buscar(String ruta, String profundidad, String texto, String extension, String minKb,
            String maxKb, String desde, String hasta) {
        try {
            File raiz = recorredor.validarRaiz(ruta);
            int max = Filtros.leerProfundidad(profundidad);
            Filtros filtros = new Filtros(texto, extension, minKb, maxKb, desde, hasta);
            bitacora.registrar("BUSCAR", raiz.getAbsolutePath() + " (texto: " + filtros.getTexto() + ")");
            iniciar(new TareaBusqueda(this, recorredor, buscador, filtros, raiz, max));
        } catch (RutaInvalidaException | FiltroInvalidoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void buscarDuplicados(String ruta, String profundidad) {
        try {
            File raiz = recorredor.validarRaiz(ruta);
            int max = Filtros.leerProfundidad(profundidad);
            bitacora.registrar("DUPLICADOS", raiz.getAbsolutePath());
            iniciar(new TareaDuplicados(this, recorredor, detector, raiz, max));
        } catch (RutaInvalidaException | FiltroInvalidoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void iniciar(TareaSegundoPlano<?> tarea) {
        if (tareaActual != null && !tareaActual.isDone()) {
            vista.mostrarError("Ya hay una operación en curso. Espera a que termine o cancélala.");
            return;
        }
        tareaActual = tarea;
        vista.iniciarProgreso(tarea.nombre());
        tarea.execute();
    }

  
    public void cancelarTarea() {
        if (tareaActual != null) {
            tareaActual.cancel(true);
        }
    }

  
    public void actualizarProgreso(int porcentaje) {
        vista.actualizarProgreso(porcentaje);
    }

    
    public void tareaTerminada(String mensaje) {
        vista.terminarProgreso(mensaje, true);
    }

  
    public void tareaCancelada(String nombre) {
        vista.terminarProgreso("Tarea cancelada por el usuario: " + nombre + ".", false);
    }

   
    public void tareaFallida(String nombre, Throwable causa) {
        vista.terminarProgreso("La tarea falló: " + nombre + ".", false);
        vista.mostrarError("No se pudo completar la operación (" + nombre + "):\n" + causa);
    }

    
    public void mostrarArbol(DefaultMutableTreeNode raiz) {
        vista.mostrarArbol(raiz);
    }

    
    public void mostrarEstadisticas(ResultadoEstadisticas resultado) {
        ultimaEstadistica = resultado;
        vista.mostrarEstadisticas(resultado);
    }

    
    public void mostrarResultadosBusqueda(List<File> archivos) {
        vista.mostrarResultadosBusqueda(archivos);
    }

    
    public void mostrarDuplicados(Map<String, List<File>> grupos) {
        vista.mostrarDuplicados(grupos);
    }

    
  
    public String nombreReporteSugerido(boolean csv) {
        return reporte.nombreSugerido(csv);
    }


    public void exportarReporte(File destino) {
        if (ultimaEstadistica == null) {
            vista.mostrarError("Primero analiza una carpeta para tener estadísticas que exportar.");
            return;
        }
        File archivo = destino;
        String nombre = destino.getName().toLowerCase();
        if (!nombre.endsWith(".txt") && !nombre.endsWith(".csv")) {
            archivo = new File(destino.getPath() + ".txt");
        }
        if (archivo.exists() && !vista.confirmar("El archivo ya existe:\n" + archivo.getAbsolutePath()
                + "\n¿Deseas sobrescribirlo?")) {
            return;
        }
        try {
            reporte.exportar(ultimaEstadistica, archivo);
            bitacora.registrar("EXPORTAR_REPORTE", archivo.getAbsolutePath());
            vista.mostrarInfo("Reporte guardado en:\n" + archivo.getAbsolutePath());
        } catch (IOException e) {
            vista.mostrarError("No se pudo guardar el reporte: " + e.getMessage());
        }
    }

    
     
    public void crearCarpeta(String elemento, String nombre) {
        try {
            File base = carpetaBase(elemento);
            File creada = gestor.crearCarpeta(base, nombre);
            bitacora.registrar("CREAR_CARPETA", creada.getAbsolutePath());
            vista.mostrarInfo("Carpeta creada:\n" + creada.getAbsolutePath());
            vista.avisarCambios();
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

   
    public void crearArchivo(String elemento, String nombre, String contenido) {
        crearArchivo(elemento, nombre, contenido, false);
    }

    private void crearArchivo(String elemento, String nombre, String contenido, boolean sobrescribir) {
        try {
            File base = carpetaBase(elemento);
            File creado = gestor.crearArchivoTexto(base, nombre, contenido, sobrescribir);
            bitacora.registrar("CREAR_ARCHIVO", creado.getAbsolutePath());
            vista.mostrarInfo("Archivo guardado:\n" + creado.getAbsolutePath());
            vista.avisarCambios();
        } catch (DestinoExistenteException e) {
            if (vista.confirmar(e.getMessage() + "\n¿Deseas sobrescribirlo?")) {
                crearArchivo(elemento, nombre, contenido, true);
            }
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void renombrar(String elemento, String nuevoNombre) {
        renombrar(elemento, nuevoNombre, false);
    }

    private void renombrar(String elemento, String nuevoNombre, boolean sobrescribir) {
        try {
            File origen = elementoExistente(elemento);
            File nuevo = gestor.renombrar(origen, nuevoNombre, sobrescribir);
            bitacora.registrar("RENOMBRAR", origen.getAbsolutePath() + " -> " + nuevo.getAbsolutePath());
            vista.mostrarInfo("Renombrado:\n" + nuevo.getAbsolutePath());
            vista.actualizarElemento(nuevo);
            vista.avisarCambios();
        } catch (DestinoExistenteException e) {
            if (vista.confirmar(e.getMessage() + "\n¿Deseas reemplazarlo?")) {
                renombrar(elemento, nuevoNombre, true);
            }
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void copiar(String elemento, String destino) {
        copiar(elemento, destino, false);
    }

    private void copiar(String elemento, String destino, boolean sobrescribir) {
        try {
            File origen = elementoExistente(elemento);
            File copia = gestor.copiar(origen, carpetaDestino(destino), sobrescribir);
            bitacora.registrar("COPIAR", origen.getAbsolutePath() + " -> " + copia.getAbsolutePath());
            vista.mostrarInfo("Copiado en:\n" + copia.getAbsolutePath());
            vista.avisarCambios();
        } catch (DestinoExistenteException e) {
            if (vista.confirmar(e.getMessage() + "\n¿Deseas sobrescribirlo?")) {
                copiar(elemento, destino, true);
            }
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void mover(String elemento, String destino) {
        mover(elemento, destino, false);
    }

    private void mover(String elemento, String destino, boolean sobrescribir) {
        try {
            File origen = elementoExistente(elemento);
            File nuevo = gestor.mover(origen, carpetaDestino(destino), sobrescribir);
            bitacora.registrar("MOVER", origen.getAbsolutePath() + " -> " + nuevo.getAbsolutePath());
            vista.mostrarInfo("Movido a:\n" + nuevo.getAbsolutePath());
            vista.actualizarElemento(nuevo);
            vista.avisarCambios();
        } catch (DestinoExistenteException e) {
            if (vista.confirmar(e.getMessage() + "\n¿Deseas sobrescribirlo?")) {
                mover(elemento, destino, true);
            }
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    
    public void eliminar(String elemento) {
        try {
            File objetivo = elementoExistente(elemento);
            String pregunta = "¿Eliminar el archivo?\n" + objetivo.getAbsolutePath();
            if (objetivo.isDirectory()) {
                pregunta = "¿Eliminar la carpeta y TODO su contenido?\n" + objetivo.getAbsolutePath();
            }
            if (!vista.confirmar(pregunta + "\nEsta acción no se puede deshacer.")) {
                return;
            }
            gestor.eliminar(objetivo);
            bitacora.registrar("ELIMINAR", objetivo.getAbsolutePath());
            vista.mostrarInfo("Eliminado:\n" + objetivo.getAbsolutePath());
            vista.actualizarElemento(objetivo.getParentFile());
            vista.avisarCambios();
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private File elementoExistente(String ruta) throws OperacionArchivoException {
        String limpia = ruta.trim();
        if (limpia.isEmpty()) {
            throw new OperacionArchivoException("Selecciona primero un archivo o carpeta (campo \"Elemento seleccionado\").");
        }
        File elemento = new File(limpia);
        if (!elemento.exists()) {
            throw new OperacionArchivoException("No existe: " + elemento.getAbsolutePath());
        }
        return elemento;
    }

    private File carpetaBase(String ruta) throws OperacionArchivoException {
        File elemento = elementoExistente(ruta);
        if (elemento.isDirectory()) {
            return elemento;
        }
        return elemento.getAbsoluteFile().getParentFile();
    }

    private File carpetaDestino(String ruta) throws OperacionArchivoException {
        String limpia = ruta.trim();
        if (limpia.isEmpty()) {
            throw new OperacionArchivoException("Escribe o elige la carpeta de destino.");
        }
        return new File(limpia);
    }

    
    public void abrirEnEditor(String ruta) {
        abrirEnEditor(ruta, false);
    }

    private void abrirEnEditor(String ruta, boolean ignorarAdvertencias) {
        try {
            File archivo = elementoExistente(ruta);
            String contenido = editor.abrir(archivo, ignorarAdvertencias);
            bitacora.registrar("ABRIR_ARCHIVO", archivo.getAbsolutePath());
            vista.mostrarEnEditor(archivo, contenido);
        } catch (AdvertenciaArchivoException e) {
            if (vista.confirmar(e.getMessage() + "\n¿Deseas abrirlo de todos modos?")) {
                abrirEnEditor(ruta, true);
            }
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

   
    public void guardarEditor(String ruta, String contenido) {
        try {
            File archivo = elementoExistente(ruta);
            if (archivo.isDirectory()) {
                throw new OperacionArchivoException("No hay un archivo abierto para guardar.");
            }
            if (!vista.confirmar("Se sobrescribirá el archivo:\n" + archivo.getAbsolutePath() + "\n¿Deseas guardar los cambios?")) {
                return;
            }
            editor.guardar(archivo, contenido);
            bitacora.registrar("GUARDAR_ARCHIVO", archivo.getAbsolutePath());
            vista.mostrarInfo("Archivo guardado.");
        } catch (OperacionArchivoException e) {
            vista.mostrarError(e.getMessage());
        }
    }
}
