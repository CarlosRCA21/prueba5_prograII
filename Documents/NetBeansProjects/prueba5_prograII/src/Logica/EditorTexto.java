package Logica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lee y guarda archivos de texto con FileReader y FileWriter (siempre en UTF-8).
 * Antes de abrir un archivo avisa si es demasiado grande o si no parece de texto.
 */
public class EditorTexto {

    /** A partir de este tamaño (1 MB) se advierte que abrir el archivo puede ser lento. */
    public static final long LIMITE_ADVERTENCIA = 1024 * 1024;

    /** Por encima de este tamaño (50 MB) el archivo no se abre en el editor. */
    public static final long LIMITE_MAXIMO = 50L * 1024 * 1024;

    private static final String[] EXTENSIONES_BINARIAS = {"pdf", "png", "jpg", "jpeg", "gif", "bmp", "ico",
        "zip", "rar", "7z", "jar", "class", "exe", "dll", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "mp3", "mp4", "avi", "mkv", "wav", "iso"};

    /**
     * Lee un archivo de texto completo con FileReader.
     *
     * @param archivo el archivo a leer
     * @return todo el contenido
     * @throws IOException si no se puede leer
     */
    public static String leerTexto(File archivo) throws IOException {
        StringBuilder texto = new StringBuilder();
        char[] buffer = new char[8192];
        try (FileReader lector = new FileReader(archivo, StandardCharsets.UTF_8)) {
            int leidos = lector.read(buffer);
            while (leidos != -1) {
                texto.append(buffer, 0, leidos);
                leidos = lector.read(buffer);
            }
        }
        return texto.toString();
    }

    /**
     * Abre un archivo para editarlo.
     *
     * @param archivo el archivo a abrir
     * @param ignorarAdvertencias true si el usuario ya confirmó abrirlo aunque sea grande o no sea de texto
     * @return el contenido del archivo
     * @throws OperacionArchivoException si no existe, es una carpeta, es enorme o no se puede leer
     * @throws AdvertenciaArchivoException si es grande o no parece de texto y no se ignoraron las advertencias
     */
    public String abrir(File archivo, boolean ignorarAdvertencias)
            throws OperacionArchivoException, AdvertenciaArchivoException {
        if (!archivo.isFile()) {
            throw new OperacionArchivoException("El archivo no existe o es una carpeta: " + archivo.getAbsolutePath());
        }
        if (archivo.length() > LIMITE_MAXIMO) {
            throw new OperacionArchivoException("El archivo pesa " + Formato.tamano(archivo.length())
                    + " y supera el máximo permitido en el editor (" + Formato.tamano(LIMITE_MAXIMO) + ").");
        }
        try {
            if (!ignorarAdvertencias) {
                avisarSiHaceFalta(archivo);
            }
            return leerTexto(archivo);
        } catch (IOException e) {
            throw new OperacionArchivoException("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private void avisarSiHaceFalta(File archivo) throws IOException, AdvertenciaArchivoException {
        String mensaje = "";
        if (archivo.length() > LIMITE_ADVERTENCIA) {
            mensaje = mensaje + "El archivo es grande (" + Formato.tamano(archivo.length())
                    + ") y abrirlo puede tardar. ";
        }
        if (!pareceTexto(archivo)) {
            mensaje = mensaje + "El archivo no parece ser de texto y podría mostrarse con símbolos raros. ";
        }
        if (mensaje.length() > 0) {
            throw new AdvertenciaArchivoException(mensaje.trim());
        }
    }

    /**
     * Decide si un archivo parece de texto: su extensión no es de un formato binario conocido
     * y sus primeros 4096 bytes no contienen bytes en cero.
     *
     * @param archivo el archivo a revisar
     * @return true si parece de texto
     * @throws IOException si no se puede leer
     */
    public boolean pareceTexto(File archivo) throws IOException {
        String extension = Formato.extensionDe(archivo.getName());
        for (String binaria : EXTENSIONES_BINARIAS) {
            if (binaria.equals(extension)) {
                return false;
            }
        }
        byte[] muestra = new byte[4096];
        int leidos = 0;
        try (FileInputStream entrada = new FileInputStream(archivo)) {
            leidos = entrada.read(muestra);
        }
        for (int i = 0; i < leidos; i++) {
            if (muestra[i] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Guarda el texto en el archivo con FileWriter (reemplaza el contenido anterior).
     *
     * @param archivo el archivo destino
     * @param contenido el texto a guardar
     * @throws OperacionArchivoException si no se puede escribir
     */
    public void guardar(File archivo, String contenido) throws OperacionArchivoException {
        try (FileWriter escritor = new FileWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.write(contenido);
        } catch (IOException e) {
            throw new OperacionArchivoException("No se pudo guardar el archivo: " + e.getMessage());
        }
    }
}
