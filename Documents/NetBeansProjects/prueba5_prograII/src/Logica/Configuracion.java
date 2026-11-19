package Logica;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Archivo de configuración (config.txt). Guarda líneas con el formato clave=valor
 * para recordar la última ruta y los filtros usados.
 */
public class Configuracion {

    private final File archivo;

    /**
     * @param archivo archivo de configuración
     */
    public Configuracion(File archivo) {
        this.archivo = archivo;
    }

    /**
     * Escribe todos los valores en el archivo (reemplaza lo anterior).
     *
     * @param valores pares clave y valor
     * @throws IOException si no se puede escribir el archivo
     */
    public void guardar(Map<String, String> valores) throws IOException {
        try (FileWriter escritor = new FileWriter(archivo, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> par : valores.entrySet()) {
                String valor = par.getValue().replace('\n', ' ').replace('\r', ' ');
                escritor.write(par.getKey() + "=" + valor + System.lineSeparator());
            }
        }
    }

    /**
     * Lee el archivo de configuración. Si todavía no existe devuelve un mapa vacío.
     *
     * @return los pares clave y valor encontrados
     */
    public Map<String, String> cargar() {
        Map<String, String> valores = new LinkedHashMap<String, String>();
        try {
            String contenido = EditorTexto.leerTexto(archivo);
            String[] lineas = contenido.split("\\R");
            for (String linea : lineas) {
                int igual = linea.indexOf('=');
                if (igual > 0) {
                    valores.put(linea.substring(0, igual), linea.substring(igual + 1));
                }
            }
        } catch (FileNotFoundException e) {
            return valores;
        } catch (IOException e) {
            System.err.println("No se pudo leer la configuración: " + e.getMessage());
        }
        return valores;
    }
}
