package Controlador;

import Vista.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ManejadorErrores());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el aspecto del sistema: " + e.getMessage());
        }
        SwingUtilities.invokeLater(new Arranque());
    }

   
    private static class Arranque implements Runnable {

        @Override
        public void run() {
            Controlador controlador = new Controlador();
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            controlador.setVista(ventana);
            ventana.setVisible(true);
        }
    }
}
