package Logica;

import java.io.File;

/**
 * Resultado de analizar una carpeta: cantidad y tamaño por extensión (.txt, .java, .pdf, otros)
 * y estadísticas globales.
 */
public class ResultadoEstadisticas {

    /** Nombres de las categorías, en el mismo orden que los arreglos internos. */
    public static final String[] CATEGORIAS = {".txt", ".java", ".pdf", "otros"};

    private final String carpeta;
    private final int profundidadLimite;
    private final long[] cantidades = new long[CATEGORIAS.length];
    private final long[] tamanos = new long[CATEGORIAS.length];
    private File archivoMasGrande = null;
    private long tamanoMasGrande = -1;
    private File archivoMasReciente = null;
    private long fechaMasReciente = -1;
    private int profundidadMaxima = 0;
    private long carpetas = 0;
    private long carpetasVacias = 0;

    /**
     * @param carpeta ruta de la carpeta analizada
     * @param profundidadLimite límite de profundidad usado (0 = ilimitada)
     */
    public ResultadoEstadisticas(String carpeta, int profundidadLimite) {
        this.carpeta = carpeta;
        this.profundidadLimite = profundidadLimite;
    }

    /**
     * Suma un archivo a las estadísticas (categoría, tamaño, más grande y más reciente).
     *
     * @param archivo el archivo encontrado
     */
    public void agregarArchivo(File archivo) {
        long tamano = archivo.length();
        long fecha = archivo.lastModified();
        int categoria = categoriaDe(archivo.getName());
        cantidades[categoria]++;
        tamanos[categoria] = tamanos[categoria] + tamano;
        if (tamano > tamanoMasGrande) {
            tamanoMasGrande = tamano;
            archivoMasGrande = archivo;
        }
        if (fecha > fechaMasReciente) {
            fechaMasReciente = fecha;
            archivoMasReciente = archivo;
        }
    }

    /**
     * Suma una carpeta encontrada.
     *
     * @param vacia true si la carpeta no tiene nada dentro
     */
    public void agregarCarpeta(boolean vacia) {
        carpetas++;
        if (vacia) {
            carpetasVacias++;
        }
    }

    /**
     * Guarda la profundidad más grande vista hasta ahora.
     *
     * @param profundidad profundidad de un elemento encontrado
     */
    public void registrarProfundidad(int profundidad) {
        if (profundidad > profundidadMaxima) {
            profundidadMaxima = profundidad;
        }
    }

    private int categoriaDe(String nombre) {
        String extension = Formato.extensionDe(nombre);
        if (extension.equals("txt")) {
            return 0;
        }
        if (extension.equals("java")) {
            return 1;
        }
        if (extension.equals("pdf")) {
            return 2;
        }
        return 3;
    }

    public long getCantidad(int categoria) {
        return cantidades[categoria];
    }

    public long getTamano(int categoria) {
        return tamanos[categoria];
    }

    public long getTotalArchivos() {
        long total = 0;
        for (long cantidad : cantidades) {
            total = total + cantidad;
        }
        return total;
    }

    public long getTotalTamano() {
        long total = 0;
        for (long tamano : tamanos) {
            total = total + tamano;
        }
        return total;
    }

    /**
     * @param categoria número de categoría
     * @return porcentaje de archivos de esa categoría sobre el total
     */
    public double porcentajeCantidad(int categoria) {
        return Formato.porcentaje(cantidades[categoria], getTotalArchivos());
    }

    /**
     * @param categoria número de categoría
     * @return porcentaje del tamaño de esa categoría sobre el tamaño total
     */
    public double porcentajeTamano(int categoria) {
        return Formato.porcentaje(tamanos[categoria], getTotalTamano());
    }

    public String getCarpeta() {
        return carpeta;
    }

    public int getProfundidadLimite() {
        return profundidadLimite;
    }

    public File getArchivoMasGrande() {
        return archivoMasGrande;
    }

    public long getTamanoMasGrande() {
        return tamanoMasGrande;
    }

    public File getArchivoMasReciente() {
        return archivoMasReciente;
    }

    public long getFechaMasReciente() {
        return fechaMasReciente;
    }

    public int getProfundidadMaxima() {
        return profundidadMaxima;
    }

    public long getCarpetas() {
        return carpetas;
    }

    public long getCarpetasVacias() {
        return carpetasVacias;
    }

    /**
     * Arma el texto de las estadísticas globales, que se usa en pantalla y en el reporte.
     *
     * @return varias líneas con archivo más grande, más reciente, profundidad, carpetas y vacías
     */
    public String textoGlobal() {
        StringBuilder texto = new StringBuilder();
        texto.append("Carpeta analizada: ").append(carpeta).append("\n");
        texto.append("Profundidad máxima de recorrido (0 = ilimitada): ").append(profundidadLimite).append("\n");
        if (archivoMasGrande == null) {
            texto.append("Archivo más grande: (no hay archivos)\n");
            texto.append("Archivo más reciente: (no hay archivos)\n");
        } else {
            texto.append("Archivo más grande: ").append(archivoMasGrande.getAbsolutePath())
                    .append(" (").append(Formato.tamano(tamanoMasGrande)).append(")\n");
            texto.append("Archivo más reciente: ").append(archivoMasReciente.getAbsolutePath())
                    .append(" (").append(Formato.fechaHora(fechaMasReciente)).append(")\n");
        }
        texto.append("Profundidad máxima alcanzada: ").append(profundidadMaxima).append("\n");
        texto.append("Número de carpetas (sin contar la raíz): ").append(carpetas).append("\n");
        texto.append("Carpetas vacías: ").append(carpetasVacias).append("\n");
        return texto.toString();
    }
}
