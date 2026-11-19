package Logica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Herramientas comunes para recorrer carpetas de forma recursiva.
 *
 * <p>Criterios documentados:</p>
 * <ul>
 * <li>Profundidad: los elementos dentro de la raíz tienen profundidad 1, los de sus subcarpetas 2,
 * y así sucesivamente. Un límite de 0 significa ilimitada.</li>
 * <li>Carpetas sin permiso (listFiles() devuelve null): se omiten y se registran en la bitácora.</li>
 * <li>Enlaces simbólicos a carpetas: se omiten (y se registran) para evitar bucles infinitos.
 * Un enlace a un archivo se trata como un archivo normal.</li>
 * <li>Archivos ocultos: SÍ se incluyen en todos los recorridos.</li>
 * <li>Archivos sin extensión: se cuentan en la categoría "otros" y no cumplen un filtro de extensión.</li>
 * </ul>
 */
public class Recorredor {

    private final Bitacora bitacora;

    /**
     * @param bitacora bitácora donde se registran carpetas sin permiso y enlaces omitidos
     */
    public Recorredor(Bitacora bitacora) {
        this.bitacora = bitacora;
    }

    /**
     * Valida y normaliza la ruta del directorio raíz.
     *
     * @param ruta lo que escribió el usuario
     * @return la carpeta raíz con su ruta canónica
     * @throws RutaInvalidaException si está vacía, no existe, no es directorio o no se puede leer
     */
    public File validarRaiz(String ruta) throws RutaInvalidaException {
        String limpia = ruta.trim();
        if (limpia.isEmpty()) {
            throw new RutaInvalidaException("La ruta no puede estar vacía.");
        }
        File carpeta = new File(limpia);
        try {
            carpeta = carpeta.getCanonicalFile();
        } catch (IOException e) {
            throw new RutaInvalidaException("La ruta tiene un formato inválido: " + e.getMessage());
        } catch (SecurityException e) {
            throw new RutaInvalidaException("No hay permiso para acceder a la ruta.");
        }
        if (!carpeta.exists()) {
            throw new RutaInvalidaException("La ruta no existe: " + carpeta.getAbsolutePath());
        }
        if (!carpeta.isDirectory()) {
            throw new RutaInvalidaException("La ruta no es un directorio: " + carpeta.getAbsolutePath());
        }
        if (!carpeta.canRead()) {
            throw new RutaInvalidaException("No hay permiso de lectura en: " + carpeta.getAbsolutePath());
        }
        return carpeta;
    }

    /**
     * Indica si todavía se puede entrar a los hijos de una carpeta según el límite de profundidad.
     *
     * @param profundidadCarpeta profundidad de la carpeta actual (la raíz es 0)
     * @param profundidadMaxima límite de profundidad (0 = ilimitada)
     * @return true si se pueden recorrer sus hijos
     */
    public boolean puedeBajar(int profundidadCarpeta, int profundidadMaxima) {
        return profundidadMaxima == 0 || profundidadCarpeta < profundidadMaxima;
    }

    /**
     * Lista el contenido de una carpeta omitiendo (y registrando) carpetas sin permiso y enlaces simbólicos a carpetas.
     *
     * @param carpeta la carpeta a listar
     * @return los hijos válidos (arreglo vacío si no se pudo leer)
     */
    public File[] listarHijos(File carpeta) {
        return leerHijos(carpeta, true);
    }

    /**
     * Igual que {@link #listarHijos(File)} pero sin escribir en la bitácora ni cancelar nada.
     * Se usa para contar elementos antes del trabajo real.
     *
     * @param carpeta la carpeta a listar
     * @param registrar true para escribir en la bitácora
     * @return los hijos válidos
     */
    private File[] leerHijos(File carpeta, boolean registrar) {
        File[] contenido = carpeta.listFiles();
        if (contenido == null) {
            if (registrar) {
                bitacora.registrar("SIN_PERMISO", carpeta.getAbsolutePath());
            }
            return new File[0];
        }
        List<File> validos = new ArrayList<File>();
        for (File elemento : contenido) {
            if (elemento.isDirectory() && esEnlaceSimbolico(elemento)) {
                if (registrar) {
                    bitacora.registrar("ENLACE_OMITIDO", elemento.getAbsolutePath());
                }
            } else {
                validos.add(elemento);
            }
        }
        return validos.toArray(new File[0]);
    }

    /**
     * Detecta enlaces simbólicos usando solo File: si la ruta canónica (la real) no coincide
     * con la ruta absoluta, el elemento es un enlace.
     *
     * @param elemento archivo o carpeta a revisar
     * @return true si es un enlace simbólico
     */
    public boolean esEnlaceSimbolico(File elemento) {
        try {
            File absoluto = elemento.getAbsoluteFile();
            File padre = absoluto.getParentFile();
            File candidato = absoluto;
            if (padre != null) {
                candidato = new File(padre.getCanonicalFile(), absoluto.getName());
            }
            return !candidato.getCanonicalFile().equals(candidato.getAbsoluteFile());
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Cuenta recursivamente cuántos archivos y carpetas se visitarán (sirve para la barra de progreso).
     *
     * @param raiz carpeta raíz
     * @param profundidadMaxima límite de profundidad (0 = ilimitada)
     * @param observador se avisa por cada elemento, así se puede cancelar
     * @return cantidad de elementos que se visitarán
     * @throws OperacionCanceladaException si el usuario cancela
     */
    public long contarElementos(File raiz, int profundidadMaxima, Observador observador)
            throws OperacionCanceladaException {
        return contar(raiz, 0, profundidadMaxima, observador);
    }

    private long contar(File carpeta, int profundidad, int profundidadMaxima, Observador observador)
            throws OperacionCanceladaException {
        long total = 0;
        if (!puedeBajar(profundidad, profundidadMaxima)) {
            return 0;
        }
        File[] hijos = leerHijos(carpeta, false);
        for (File hijo : hijos) {
            observador.elementoVisitado(hijo);
            total++;
            if (hijo.isDirectory()) {
                total = total + contar(hijo, profundidad + 1, profundidadMaxima, observador);
            }
        }
        return total;
    }
}
