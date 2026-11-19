package Logica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Operaciones sobre archivos y carpetas: crear carpeta (también anidadas), crear archivo de texto,
 * renombrar, copiar, mover y eliminar.
 *
 * <p>Esta clase NUNCA pregunta nada al usuario. Si el destino ya existe lanza
 * {@link DestinoExistenteException}; quien la llame decide si pide confirmación y repite
 * la operación con sobrescribir = true.</p>
 */
public class GestorArchivos {

    private static final String PROHIBIDOS = "\\/:*?\"<>|";
    private static final String[] RESERVADOS = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4",
        "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7",
        "LPT8", "LPT9"};

    private final Recorredor recorredor;

    /**
     * @param recorredor se usa para listar carpetas al copiar y para detectar enlaces al eliminar
     */
    public GestorArchivos(Recorredor recorredor) {
        this.recorredor = recorredor;
    }

    /**
     * Revisa que un nombre sea válido para un archivo o carpeta.
     *
     * @param nombre el nombre a revisar
     * @throws OperacionArchivoException si está vacío, es muy largo, tiene caracteres prohibidos,
     * termina en punto o espacio, o es un nombre reservado de Windows
     */
    public static void validarNombre(String nombre) throws OperacionArchivoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new OperacionArchivoException("El nombre no puede estar vacío.");
        }
        if (!nombre.equals(nombre.trim())) {
            throw new OperacionArchivoException("El nombre no puede empezar ni terminar con espacios.");
        }
        if (nombre.length() > 255) {
            throw new OperacionArchivoException("El nombre es demasiado largo (máximo 255 caracteres).");
        }
        for (int i = 0; i < nombre.length(); i++) {
            char letra = nombre.charAt(i);
            if (PROHIBIDOS.indexOf(letra) >= 0 || letra < 32) {
                throw new OperacionArchivoException("El nombre \"" + nombre
                        + "\" tiene un carácter no permitido. No uses: \\ / : * ? \" < > |");
            }
        }
        if (nombre.endsWith(".")) {
            throw new OperacionArchivoException("El nombre no puede terminar en punto.");
        }
        String base = nombre;
        int punto = nombre.indexOf('.');
        if (punto >= 0) {
            base = nombre.substring(0, punto);
        }
        for (String reservado : RESERVADOS) {
            if (reservado.equalsIgnoreCase(base)) {
                throw new OperacionArchivoException("\"" + nombre + "\" es un nombre reservado del sistema.");
            }
        }
    }

    /**
     * Crea una carpeta. El nombre puede ser anidado, por ejemplo "a/b/c" crea las tres carpetas.
     *
     * @param base carpeta donde se crea
     * @param nombre nombre o ruta relativa de la nueva carpeta (separadores / o \)
     * @return la carpeta creada
     * @throws OperacionArchivoException si el nombre es inválido, ya existe o no se pudo crear
     */
    public File crearCarpeta(File base, String nombre) throws OperacionArchivoException {
        validarCarpetaExistente(base);
        String[] partes = nombre.split("[/\\\\]");
        File destino = base;
        for (String parte : partes) {
            validarNombre(parte);
            destino = new File(destino, parte);
        }
        if (destino.exists()) {
            throw new OperacionArchivoException("Ya existe: " + destino.getAbsolutePath());
        }
        if (!destino.mkdirs()) {
            throw new OperacionArchivoException("No se pudo crear la carpeta (¿faltan permisos?).");
        }
        return destino;
    }

    /**
     * Crea un archivo de texto y escribe el contenido con FileWriter.
     *
     * @param base carpeta donde se crea
     * @param nombre nombre del archivo
     * @param contenido texto a escribir
     * @param sobrescribir true si se permite reemplazar un archivo que ya existe
     * @return el archivo creado
     * @throws DestinoExistenteException si ya existe y no se pidió sobrescribir
     * @throws OperacionArchivoException si el nombre es inválido o no se pudo escribir
     */
    public File crearArchivoTexto(File base, String nombre, String contenido, boolean sobrescribir)
            throws OperacionArchivoException {
        validarCarpetaExistente(base);
        validarNombre(nombre);
        File archivo = new File(base, nombre);
        if (archivo.isDirectory()) {
            throw new OperacionArchivoException("Ya existe una carpeta con ese nombre.");
        }
        if (archivo.exists() && !sobrescribir) {
            throw new DestinoExistenteException("Ya existe el archivo: " + archivo.getAbsolutePath());
        }
        try (FileWriter escritor = new FileWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.write(contenido);
        } catch (IOException e) {
            throw new OperacionArchivoException("No se pudo escribir el archivo: " + e.getMessage());
        }
        return archivo;
    }

    /**
     * Cambia el nombre de un archivo o carpeta (queda en la misma carpeta).
     *
     * @param origen elemento a renombrar
     * @param nuevoNombre nuevo nombre
     * @param sobrescribir true si se permite reemplazar un archivo que ya tenga ese nombre
     * @return el elemento con su nuevo nombre
     * @throws DestinoExistenteException si ya existe un archivo con ese nombre y no se pidió sobrescribir
     * @throws OperacionArchivoException si el nombre es inválido o no se pudo renombrar
     */
    public File renombrar(File origen, String nuevoNombre, boolean sobrescribir) throws OperacionArchivoException {
        validarExistente(origen);
        validarNombre(nuevoNombre);
        File padre = origen.getAbsoluteFile().getParentFile();
        if (padre == null) {
            throw new OperacionArchivoException("No se puede renombrar una unidad raíz.");
        }
        File destino = new File(padre, nuevoNombre);
        if (destino.equals(origen.getAbsoluteFile())) {
            throw new OperacionArchivoException("El nuevo nombre es igual al actual.");
        }
        if (destino.exists() && !mismoElemento(origen, destino)) {
            prepararDestino(destino, sobrescribir);
        }
        if (!origen.renameTo(destino)) {
            throw new OperacionArchivoException("No se pudo renombrar (¿está en uso o faltan permisos?).");
        }
        return destino;
    }

    /**
     * Copia un archivo (con flujos de bytes) o una carpeta completa (recursivamente) dentro de otra carpeta.
     *
     * @param origen archivo o carpeta a copiar
     * @param carpetaDestino carpeta donde quedará la copia
     * @param sobrescribir true si se permite reemplazar lo que ya exista en el destino
     * @return la copia creada
     * @throws DestinoExistenteException si ya existe en el destino y no se pidió sobrescribir
     * @throws OperacionArchivoException si algo falla
     */
    public File copiar(File origen, File carpetaDestino, boolean sobrescribir) throws OperacionArchivoException {
        validarExistente(origen);
        validarCarpetaExistente(carpetaDestino);
        File destino = new File(carpetaDestino, origen.getName());
        if (mismoElemento(origen, destino)) {
            throw new OperacionArchivoException("El origen y el destino son el mismo elemento.");
        }
        if (origen.isDirectory()) {
            validarNoDentroDeSiMisma(origen, carpetaDestino);
        }
        if (destino.exists()) {
            if (destino.isDirectory() != origen.isDirectory()) {
                throw new OperacionArchivoException("En el destino ya existe un elemento con ese nombre "
                        + "pero de otro tipo (archivo o carpeta).");
            }
            if (!sobrescribir) {
                throw new DestinoExistenteException("En el destino ya existe: " + destino.getAbsolutePath());
            }
        }
        copiarElemento(origen, destino);
        return destino;
    }

    /**
     * Mueve un archivo o carpeta a otra carpeta.
     *
     * @param origen archivo o carpeta a mover
     * @param carpetaDestino carpeta donde quedará
     * @param sobrescribir true si se permite reemplazar un archivo que ya exista en el destino
     * @return el elemento en su nueva ubicación
     * @throws DestinoExistenteException si ya existe un archivo igual y no se pidió sobrescribir
     * @throws OperacionArchivoException si algo falla
     */
    public File mover(File origen, File carpetaDestino, boolean sobrescribir) throws OperacionArchivoException {
        validarExistente(origen);
        validarCarpetaExistente(carpetaDestino);
        File destino = new File(carpetaDestino, origen.getName());
        if (mismoElemento(origen, destino)) {
            throw new OperacionArchivoException("El origen y el destino son el mismo elemento.");
        }
        if (origen.isDirectory()) {
            validarNoDentroDeSiMisma(origen, carpetaDestino);
        }
        if (destino.exists()) {
            if (origen.isDirectory() || destino.isDirectory()) {
                throw new OperacionArchivoException("En el destino ya existe una carpeta o un elemento con ese "
                        + "nombre y no se puede reemplazar. Cambia el nombre o elige otro destino.");
            }
            prepararDestino(destino, sobrescribir);
        }
        if (!origen.renameTo(destino)) {
            copiarElemento(origen, destino);
            borrarRecursivo(origen);
        }
        return destino;
    }

    /**
     * Elimina un archivo o una carpeta con todo su contenido.
     *
     * @param objetivo elemento a eliminar
     * @throws OperacionArchivoException si no existe, es una unidad raíz o no se pudo eliminar
     */
    public void eliminar(File objetivo) throws OperacionArchivoException {
        validarExistente(objetivo);
        if (objetivo.getAbsoluteFile().getParentFile() == null) {
            throw new OperacionArchivoException("No se puede eliminar una unidad raíz.");
        }
        borrarRecursivo(objetivo);
    }

    private void borrarRecursivo(File objetivo) throws OperacionArchivoException {
        if (objetivo.isDirectory() && !recorredor.esEnlaceSimbolico(objetivo)) {
            File[] hijos = objetivo.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    borrarRecursivo(hijo);
                }
            }
        }
        if (!objetivo.delete()) {
            throw new OperacionArchivoException("No se pudo eliminar: " + objetivo.getAbsolutePath());
        }
    }

    private void copiarElemento(File origen, File destino) throws OperacionArchivoException {
        if (origen.isDirectory()) {
            if (!destino.isDirectory() && !destino.mkdirs()) {
                throw new OperacionArchivoException("No se pudo crear la carpeta: " + destino.getAbsolutePath());
            }
            File[] hijos = recorredor.listarHijos(origen);
            for (File hijo : hijos) {
                copiarElemento(hijo, new File(destino, hijo.getName()));
            }
        } else {
            copiarArchivo(origen, destino);
        }
    }

    private void copiarArchivo(File origen, File destino) throws OperacionArchivoException {
        byte[] buffer = new byte[8192];
        try (FileInputStream entrada = new FileInputStream(origen);
                FileOutputStream salida = new FileOutputStream(destino)) {
            int leidos = entrada.read(buffer);
            while (leidos != -1) {
                salida.write(buffer, 0, leidos);
                leidos = entrada.read(buffer);
            }
        } catch (IOException e) {
            throw new OperacionArchivoException("No se pudo copiar " + origen.getName() + ": " + e.getMessage());
        }
    }

    private void prepararDestino(File destino, boolean sobrescribir) throws OperacionArchivoException {
        if (destino.isDirectory()) {
            throw new OperacionArchivoException("Ya existe una carpeta con ese nombre y no se puede reemplazar.");
        }
        if (!sobrescribir) {
            throw new DestinoExistenteException("Ya existe: " + destino.getAbsolutePath());
        }
        if (!destino.delete()) {
            throw new OperacionArchivoException("No se pudo reemplazar el archivo existente.");
        }
    }

    private boolean mismoElemento(File a, File b) {
        try {
            return a.getCanonicalFile().equals(b.getCanonicalFile());
        } catch (IOException e) {
            return false;
        }
    }

    private void validarNoDentroDeSiMisma(File carpetaOrigen, File carpetaDestino) throws OperacionArchivoException {
        try {
            String origen = carpetaOrigen.getCanonicalPath();
            String destino = carpetaDestino.getCanonicalPath();
            if (destino.equals(origen) || destino.startsWith(origen + File.separator)) {
                throw new OperacionArchivoException("No se puede copiar o mover una carpeta dentro de sí misma.");
            }
        } catch (IOException e) {
            throw new OperacionArchivoException("No se pudo comprobar la ruta: " + e.getMessage());
        }
    }

    private void validarExistente(File elemento) throws OperacionArchivoException {
        if (!elemento.exists()) {
            throw new OperacionArchivoException("No existe: " + elemento.getAbsolutePath());
        }
    }

    private void validarCarpetaExistente(File carpeta) throws OperacionArchivoException {
        if (!carpeta.isDirectory()) {
            throw new OperacionArchivoException("La carpeta no existe: " + carpeta.getAbsolutePath());
        }
    }
}
