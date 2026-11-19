package Logica;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Bitácora de operaciones (bitacora.log). Cada línea tiene fecha, hora, acción y ruta,
 * y siempre se agrega al final del archivo (modo append).
 */
public class Bitacora {

    private final File archivo;

    /**
     * @param archivo archivo donde se guarda la bitácora
     */
    public Bitacora(File archivo) {
        this.archivo = archivo;
    }

    /**
     * Agrega una línea al final de la bitácora. Si no se puede escribir, el programa
     * sigue funcionando y solo se avisa por la consola.
     *
     * @param accion nombre de la acción (por ejemplo BUSCAR)
     * @param ruta ruta sobre la que se hizo la acción
     */
    public synchronized void registrar(String accion, String ruta) {
        try (FileWriter escritor = new FileWriter(archivo, StandardCharsets.UTF_8, true)) {
            escritor.write(Formato.ahora() + " | " + accion + " | " + ruta + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("No se pudo escribir en la bitácora: " + e.getMessage());
        }
    }

    /**
     * Lee toda la bitácora.
     *
     * @return el contenido completo del archivo
     * @throws IOException si el archivo no existe todavía o no se puede leer
     */
    public synchronized String leerTodo() throws IOException {
        return EditorTexto.leerTexto(archivo);
    }

    /**
     * @return el archivo de la bitácora
     */
    public File getArchivo() {
        return archivo;
    }
}
