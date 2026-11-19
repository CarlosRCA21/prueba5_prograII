package Logica;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Exporta las estadísticas a un archivo .txt o .csv. El reporte incluye la fecha y hora
 * en que se generó.
 */
public class Reporte {

    /**
     * Sugiere un nombre de archivo con fecha y hora.
     *
     * @param csv true para .csv, false para .txt
     * @return por ejemplo "reporte_20261119_070000.txt"
     */
    public String nombreSugerido(boolean csv) {
        String extension = ".txt";
        if (csv) {
            extension = ".csv";
        }
        return "reporte_" + Formato.ahoraParaNombre() + extension;
    }

    /**
     * Escribe el reporte. Si el nombre termina en .csv se escribe en CSV, en otro caso en texto.
     *
     * @param resultado estadísticas a exportar
     * @param destino archivo de salida
     * @throws IOException si no se puede escribir
     */
    public void exportar(ResultadoEstadisticas resultado, File destino) throws IOException {
        String contenido = "";
        if (destino.getName().toLowerCase().endsWith(".csv")) {
            contenido = generarCsv(resultado);
        } else {
            contenido = generarTexto(resultado);
        }
        try (FileWriter escritor = new FileWriter(destino, StandardCharsets.UTF_8)) {
            escritor.write(contenido);
        }
    }

    /**
     * @param resultado estadísticas
     * @return el reporte en texto plano
     */
    public String generarTexto(ResultadoEstadisticas resultado) {
        StringBuilder texto = new StringBuilder();
        texto.append("REPORTE DE ESTADÍSTICAS\n");
        texto.append("Fecha y hora del reporte: ").append(Formato.ahora()).append("\n\n");
        texto.append(String.format("%-8s %10s %14s %12s %12s%n", "Tipo", "Cantidad", "Tamaño", "% archivos", "% tamaño"));
        for (int i = 0; i < ResultadoEstadisticas.CATEGORIAS.length; i++) {
            texto.append(String.format("%-8s %10d %14s %12s %12s%n", ResultadoEstadisticas.CATEGORIAS[i],
                    resultado.getCantidad(i), Formato.tamano(resultado.getTamano(i)),
                    Formato.textoPorcentaje(resultado.porcentajeCantidad(i)),
                    Formato.textoPorcentaje(resultado.porcentajeTamano(i))));
        }
        texto.append(String.format("%-8s %10d %14s%n", "TOTAL", resultado.getTotalArchivos(),
                Formato.tamano(resultado.getTotalTamano())));
        texto.append("\n").append(resultado.textoGlobal());
        return texto.toString();
    }

    /**
     * @param resultado estadísticas
     * @return el reporte en formato CSV (separado por comas)
     */
    public String generarCsv(ResultadoEstadisticas resultado) {
        StringBuilder csv = new StringBuilder();
        csv.append("Reporte de estadisticas\n");
        csv.append("Fecha y hora,").append(Formato.ahora()).append("\n");
        csv.append("Carpeta analizada,").append(entrecomillar(resultado.getCarpeta())).append("\n");
        csv.append("Profundidad maxima de recorrido (0 = ilimitada),").append(resultado.getProfundidadLimite()).append("\n\n");
        csv.append("Tipo,Cantidad,Tamano (bytes),Tamano legible,Porcentaje de archivos,Porcentaje de tamano\n");
        for (int i = 0; i < ResultadoEstadisticas.CATEGORIAS.length; i++) {
            csv.append(ResultadoEstadisticas.CATEGORIAS[i]).append(",")
                    .append(resultado.getCantidad(i)).append(",")
                    .append(resultado.getTamano(i)).append(",")
                    .append(Formato.tamano(resultado.getTamano(i))).append(",")
                    .append(String.format(java.util.Locale.US, "%.2f", resultado.porcentajeCantidad(i))).append(",")
                    .append(String.format(java.util.Locale.US, "%.2f", resultado.porcentajeTamano(i))).append("\n");
        }
        csv.append("TOTAL,").append(resultado.getTotalArchivos()).append(",")
                .append(resultado.getTotalTamano()).append(",")
                .append(Formato.tamano(resultado.getTotalTamano())).append(",100.00,100.00\n\n");
        if (resultado.getArchivoMasGrande() == null) {
            csv.append("Archivo mas grande,(no hay archivos)\n");
            csv.append("Archivo mas reciente,(no hay archivos)\n");
        } else {
            csv.append("Archivo mas grande,").append(entrecomillar(resultado.getArchivoMasGrande().getAbsolutePath()))
                    .append("\n");
            csv.append("Tamano del archivo mas grande,").append(Formato.tamano(resultado.getTamanoMasGrande())).append("\n");
            csv.append("Archivo mas reciente,").append(entrecomillar(resultado.getArchivoMasReciente().getAbsolutePath()))
                    .append("\n");
            csv.append("Fecha del archivo mas reciente,").append(Formato.fechaHora(resultado.getFechaMasReciente()))
                    .append("\n");
        }
        csv.append("Profundidad maxima alcanzada,").append(resultado.getProfundidadMaxima()).append("\n");
        csv.append("Numero de carpetas (sin contar la raiz),").append(resultado.getCarpetas()).append("\n");
        csv.append("Carpetas vacias,").append(resultado.getCarpetasVacias()).append("\n");
        return csv.toString();
    }

    private String entrecomillar(String texto) {
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
