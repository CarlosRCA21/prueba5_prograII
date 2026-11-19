package Vista;

import Controlador.Controlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class PanelGestion extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Controlador controlador;
    private final JTextField txtElemento = new JTextField(48);
    private final JTextField txtNombre = new JTextField(28);
    private final JTextField txtDestino = new JTextField(48);
    private final JTextArea areaContenido = new JTextArea(8, 40);
    private final JButton btnElegirElemento = new JButton("Examinar...");
    private final JButton btnElegirDestino = new JButton("Examinar...");
    private final JButton btnCrearCarpeta = new JButton("Crear carpeta");
    private final JButton btnCrearArchivo = new JButton("Crear archivo de texto");
    private final JButton btnRenombrar = new JButton("Renombrar");
    private final JButton btnCopiar = new JButton("Copiar");
    private final JButton btnMover = new JButton("Mover");
    private final JButton btnEliminar = new JButton("Eliminar");

   
    public PanelGestion(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 8));

        JPanel campos = new JPanel(new GridLayout(3, 1));
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Elemento seleccionado:"));
        fila1.add(txtElemento);
        fila1.add(btnElegirElemento);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Nombre (carpeta nueva, archivo nuevo o nuevo nombre; \"a/b/c\" crea carpetas anidadas):"));
        fila2.add(txtNombre);
        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila3.add(new JLabel("Carpeta de destino (copiar / mover):"));
        fila3.add(txtDestino);
        fila3.add(btnElegirDestino);
        campos.add(fila1);
        campos.add(fila2);
        campos.add(fila3);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createTitledBorder("Contenido del archivo de texto a crear"));
        centro.add(new JScrollPane(areaContenido), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnCrearCarpeta);
        botones.add(btnCrearArchivo);
        botones.add(btnRenombrar);
        botones.add(btnCopiar);
        botones.add(btnMover);
        botones.add(btnEliminar);

        JPanel nota = new JPanel(new BorderLayout());
        nota.add(botones, BorderLayout.NORTH);
        nota.add(new JLabel("  Si el elemento seleccionado es un archivo, \"crear\" usa la carpeta donde está."),
                BorderLayout.SOUTH);

        add(campos, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(nota, BorderLayout.SOUTH);

        btnElegirElemento.addActionListener(this);
        btnElegirDestino.addActionListener(this);
        btnCrearCarpeta.addActionListener(this);
        btnCrearArchivo.addActionListener(this);
        btnRenombrar.addActionListener(this);
        btnCopiar.addActionListener(this);
        btnMover.addActionListener(this);
        btnEliminar.addActionListener(this);
    }

    
    public void setElemento(File elemento) {
        txtElemento.setText(elemento.getAbsolutePath());
    }

    private String elegirRuta(int modo) {
        JFileChooser selector = new JFileChooser(txtElemento.getText());
        selector.setFileSelectionMode(modo);
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == btnElegirElemento) {
            String ruta = elegirRuta(JFileChooser.FILES_AND_DIRECTORIES);
            if (ruta != null) {
                txtElemento.setText(ruta);
            }
        } else if (origen == btnElegirDestino) {
            String ruta = elegirRuta(JFileChooser.DIRECTORIES_ONLY);
            if (ruta != null) {
                txtDestino.setText(ruta);
            }
        } else if (origen == btnCrearCarpeta) {
            controlador.crearCarpeta(txtElemento.getText(), txtNombre.getText());
        } else if (origen == btnCrearArchivo) {
            controlador.crearArchivo(txtElemento.getText(), txtNombre.getText(), areaContenido.getText());
        } else if (origen == btnRenombrar) {
            controlador.renombrar(txtElemento.getText(), txtNombre.getText());
        } else if (origen == btnCopiar) {
            controlador.copiar(txtElemento.getText(), txtDestino.getText());
        } else if (origen == btnMover) {
            controlador.mover(txtElemento.getText(), txtDestino.getText());
        } else if (origen == btnEliminar) {
            controlador.eliminar(txtElemento.getText());
        }
    }
}
