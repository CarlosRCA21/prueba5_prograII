package Controlador;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class ManejadorErrores implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread hilo, Throwable error) {
        System.err.println("Error inesperado en " + hilo.getName() + ": " + error);
        if (SwingUtilities.isEventDispatchThread()) {
            mostrar(error);
        } else {
            SwingUtilities.invokeLater(new AvisoError(error));
        }
    }

    private static void mostrar(Throwable error) {
        JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado, pero el programa sigue funcionando:\n"
                + error, "Error inesperado", JOptionPane.ERROR_MESSAGE);
    }

    
    private static class AvisoError implements Runnable {

        private final Throwable error;

        AvisoError(Throwable error) {
            this.error = error;
        }

        @Override
        public void run() {
            mostrar(error);
        }
    }
}
