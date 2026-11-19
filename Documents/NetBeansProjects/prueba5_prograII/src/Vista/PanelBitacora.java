package Vista;

import Controlador.Controlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PanelBitacora extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Controlador controlador;
    private final JTextArea area = new JTextArea();
    private final JButton btnActualizar = new JButton("Actualizar");

    
    public PanelBitacora(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 6));
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(btnActualizar);
        barra.add(new JLabel("Archivo: " + controlador.rutaBitacora()));
        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
        btnActualizar.addActionListener(this);
    }

    
    public void actualizar() {
        area.setText(controlador.leerBitacora());
        area.setCaretPosition(area.getDocument().getLength());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actualizar();
    }
}
