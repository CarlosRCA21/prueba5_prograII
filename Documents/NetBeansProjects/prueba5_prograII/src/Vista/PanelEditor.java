package Vista;

import Controlador.Controlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PanelEditor extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final VentanaPrincipal ventana;
    private final Controlador controlador;
    private final JLabel etiquetaArchivo = new JLabel("Ningún archivo abierto");
    private final JTextArea area = new JTextArea();
    private final JButton btnAbrir = new JButton("Abrir archivo...");
    private final JButton btnGuardar = new JButton("Guardar cambios");
    private String rutaActual = "";

    
    public PanelEditor(VentanaPrincipal ventana, Controlador controlador) {
        this.ventana = ventana;
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 6));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnAbrir);
        barra.add(btnGuardar);
        barra.add(etiquetaArchivo);
        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
        btnAbrir.addActionListener(this);
        btnGuardar.addActionListener(this);
    }

   
    public void mostrar(File archivo, String contenido) {
        rutaActual = archivo.getAbsolutePath();
        etiquetaArchivo.setText("  Editando: " + rutaActual);
        area.setText(contenido);
        area.setCaretPosition(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAbrir) {
            JFileChooser selector = new JFileChooser(rutaActual);
            selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                controlador.abrirEnEditor(selector.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getSource() == btnGuardar) {
            if (rutaActual.isEmpty()) {
                ventana.mostrarError("Primero abre un archivo para poder guardarlo.");
            } else {
                controlador.guardarEditor(rutaActual, area.getText());
            }
        }
    }
}
