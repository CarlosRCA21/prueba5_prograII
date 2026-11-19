package Logica;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Guarda los filtros de una búsqueda ya validados: texto, extensión, tamaño mínimo y máximo
 * y rango de fechas de modificación. Un filtro vacío significa "sin filtro".
 * Si algún valor es inválido el constructor lanza {@link FiltroInvalidoException}.
 */
public class Filtros {

    private static final String PROHIBIDOS = "\\/:*?\"<>|";

    private final String texto;
    private final String textoMinusculas;
    private final String extension;
    private final long tamanoMinimo;
    private final long tamanoMaximo;
    private final long fechaDesde;
    private final long fechaHasta;

    /**
     * Valida y guarda los filtros escritos por el usuario.
     *
     * @param texto texto a buscar dentro del nombre (obligatorio)
     * @param extension extensión sin punto, por ejemplo "txt" (opcional)
     * @param minKb tamaño mínimo en KB (opcional)
     * @param maxKb tamaño máximo en KB (opcional)
     * @param desde fecha de modificación mínima, formato aaaa-MM-dd (opcional)
     * @param hasta fecha de modificación máxima, formato aaaa-MM-dd (opcional)
     * @throws FiltroInvalidoException si algún valor no es válido
     */
    public Filtros(String texto, String extension, String minKb, String maxKb, String desde, String hasta)
            throws FiltroInvalidoException {
        this.texto = validarTexto(texto);
        this.textoMinusculas = this.texto.toLowerCase(Locale.ROOT);
        this.extension = validarExtension(extension);
        long minimo = leerKb(minKb, "El tamaño mínimo");
        long maximo = leerKb(maxKb, "El tamaño máximo");
        if (minimo >= 0 && maximo >= 0 && minimo > maximo) {
            throw new FiltroInvalidoException("El tamaño mínimo no puede ser mayor que el tamaño máximo.");
        }
        this.tamanoMinimo = minimo;
        this.tamanoMaximo = maximo;
        long inicio = leerFecha(desde, "La fecha desde", true);
        long fin = leerFecha(hasta, "La fecha hasta", false);
        if (inicio >= 0 && fin >= 0 && inicio > fin) {
            throw new FiltroInvalidoException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
        this.fechaDesde = inicio;
        this.fechaHasta = fin;
    }

    /**
     * Convierte el texto de la profundidad máxima a un número.
     *
     * @param texto lo que escribió el usuario
     * @return la profundidad (0 significa ilimitada)
     * @throws FiltroInvalidoException si no es un entero mayor o igual a 0
     */
    public static int leerProfundidad(String texto) throws FiltroInvalidoException {
        int profundidad = 0;
        try {
            profundidad = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new FiltroInvalidoException(
                    "La profundidad debe ser un número entero mayor o igual a 0 (0 = ilimitada).");
        }
        if (profundidad < 0) {
            throw new FiltroInvalidoException("La profundidad no puede ser negativa.");
        }
        return profundidad;
    }

    private static String validarTexto(String texto) throws FiltroInvalidoException {
        String limpio = texto.trim();
        if (limpio.isEmpty()) {
            throw new FiltroInvalidoException("Escribe un texto de búsqueda.");
        }
        if (limpio.length() > 255) {
            throw new FiltroInvalidoException("El texto de búsqueda es demasiado largo (máximo 255 caracteres).");
        }
        for (int i = 0; i < limpio.length(); i++) {
            char letra = limpio.charAt(i);
            if (PROHIBIDOS.indexOf(letra) >= 0) {
                throw new FiltroInvalidoException("El texto de búsqueda no puede contener el carácter '" + letra
                        + "' porque no puede aparecer en el nombre de un archivo.");
            }
        }
        return limpio;
    }

    private static String validarExtension(String extension) throws FiltroInvalidoException {
        String limpia = extension.trim();
        if (limpia.startsWith(".")) {
            limpia = limpia.substring(1);
        }
        for (int i = 0; i < limpia.length(); i++) {
            if (!Character.isLetterOrDigit(limpia.charAt(i))) {
                throw new FiltroInvalidoException("La extensión solo puede tener letras y números (ejemplo: txt).");
            }
        }
        return limpia.toLowerCase(Locale.ROOT);
    }

    private static long leerKb(String texto, String campo) throws FiltroInvalidoException {
        String limpio = texto.trim();
        if (limpio.isEmpty()) {
            return -1;
        }
        long kb = 0;
        try {
            kb = Long.parseLong(limpio);
        } catch (NumberFormatException e) {
            throw new FiltroInvalidoException(campo + " debe ser un número entero de KB (ejemplo: 100).");
        }
        if (kb < 0) {
            throw new FiltroInvalidoException(campo + " no puede ser negativo.");
        }
        try {
            return Math.multiplyExact(kb, 1024L);
        } catch (ArithmeticException e) {
            throw new FiltroInvalidoException(campo + " es demasiado grande.");
        }
    }

    private static long leerFecha(String texto, String campo, boolean inicioDelDia) throws FiltroInvalidoException {
        String limpio = texto.trim();
        if (limpio.isEmpty()) {
            return -1;
        }
        LocalDate fecha = null;
        try {
            fecha = LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new FiltroInvalidoException(campo + " no es válida. Usa el formato aaaa-MM-dd (ejemplo: 2026-11-19).");
        }
        ZoneId zona = ZoneId.systemDefault();
        if (inicioDelDia) {
            return fecha.atStartOfDay(zona).toInstant().toEpochMilli();
        }
        return fecha.plusDays(1).atStartOfDay(zona).toInstant().toEpochMilli() - 1;
    }

    /**
     * Revisa si un archivo cumple TODOS los filtros a la vez.
     * El texto se busca dentro del nombre sin distinguir mayúsculas de minúsculas.
     *
     * @param archivo el archivo a revisar
     * @return true si cumple nombre, extensión, tamaño y fecha
     */
    public boolean cumple(File archivo) {
        String nombre = archivo.getName();
        if (!nombre.toLowerCase(Locale.ROOT).contains(textoMinusculas)) {
            return false;
        }
        if (extension.length() > 0 && !extension.equals(Formato.extensionDe(nombre))) {
            return false;
        }
        long tamano = archivo.length();
        if (tamanoMinimo >= 0 && tamano < tamanoMinimo) {
            return false;
        }
        if (tamanoMaximo >= 0 && tamano > tamanoMaximo) {
            return false;
        }
        long fecha = archivo.lastModified();
        if (fechaDesde >= 0 && fecha < fechaDesde) {
            return false;
        }
        if (fechaHasta >= 0 && fecha > fechaHasta) {
            return false;
        }
        return true;
    }

    /**
     * @return el texto de búsqueda ya limpio
     */
    public String getTexto() {
        return texto;
    }
}
