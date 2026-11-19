package Logica;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Funciones pequeñas para dar formato a tamaños, fechas, porcentajes y extensiones.
 * Todos los números usan punto decimal (Locale.US) para que el CSV no se confunda.
 */
public class Formato {

    private static final String[] UNIDADES = {"B", "KB", "MB", "GB", "TB"};
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FECHA_NOMBRE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Convierte bytes a un texto legible: B, KB, MB, GB o TB.
     *
     * @param bytes cantidad de bytes
     * @return por ejemplo "1.50 MB"
     */
    public static String tamano(long bytes) {
        double valor = bytes;
        int unidad = 0;
        while (valor >= 1024 && unidad < UNIDADES.length - 1) {
            valor = valor / 1024;
            unidad++;
        }
        if (unidad == 0) {
            return bytes + " B";
        }
        return String.format(Locale.US, "%.2f %s", valor, UNIDADES[unidad]);
    }

    /**
     * Da formato a una fecha guardada en milisegundos.
     *
     * @param milisegundos fecha en milisegundos desde 1970
     * @return texto con formato aaaa-MM-dd HH:mm:ss
     */
    public static String fechaHora(long milisegundos) {
        LocalDateTime fecha = LocalDateTime.ofInstant(Instant.ofEpochMilli(milisegundos), ZoneId.systemDefault());
        return fecha.format(FECHA_HORA);
    }

    /**
     * @return la fecha y hora actuales con formato aaaa-MM-dd HH:mm:ss
     */
    public static String ahora() {
        return LocalDateTime.now().format(FECHA_HORA);
    }

    /**
     * @return la fecha y hora actuales con formato aaaaMMdd_HHmmss (sirve para nombres de archivo)
     */
    public static String ahoraParaNombre() {
        return LocalDateTime.now().format(FECHA_NOMBRE);
    }

    /**
     * Obtiene la extensión de un nombre de archivo en minúsculas y sin el punto.
     * Criterio documentado: los archivos sin punto, los que solo tienen el punto al inicio
     * (como ".gitignore") y los que terminan en punto se consideran SIN extensión.
     *
     * @param nombre nombre del archivo
     * @return la extensión, o "" si no tiene
     */
    public static String extensionDe(String nombre) {
        int punto = nombre.lastIndexOf('.');
        if (punto <= 0 || punto == nombre.length() - 1) {
            return "";
        }
        return nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * Calcula un porcentaje con dos decimales. Si el total es cero devuelve 0
     * (la división entre cero lanza ArithmeticException y se atrapa).
     *
     * @param parte la parte
     * @param total el total
     * @return porcentaje entre 0 y 100
     */
    public static double porcentaje(long parte, long total) {
        try {
            long centesimas = (parte * 10000 + total / 2) / total;
            return centesimas / 100.0;
        } catch (ArithmeticException e) {
            return 0.0;
        }
    }

    /**
     * @param valor porcentaje
     * @return texto como "12.50 %"
     */
    public static String textoPorcentaje(double valor) {
        return String.format(Locale.US, "%.2f %%", valor);
    }
}
