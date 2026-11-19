package Vista;

import Logica.Formato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class PanelBusqueda extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
   
    public static final String SIN_COINCIDENCIAS = "No se encontraron archivos que coincidan con los criterios.";

    private final VentanaPrincipal ventana;
    private final ModeloTabla modelo = new ModeloTabla(
            new String[]{"Nombre", "Ruta absoluta", "Tamaño (bytes)", "Fecha de modificación"},
            new Class<?>[]{String.class, String.class, Long.class, String.class});
    private final JTable tabla = new JTable(modelo);
    private final JLabel etiquetaResultado = new JLabel("Escribe un texto de búsqueda y pulsa \"Buscar\".");
    private final JButton btnGestion = new JButton("Usar en Gestión");
    private final JButton btnEditor = new JButton("Abrir en el editor");
    private List<File> archivos = new ArrayList<File>();

  
    public PanelBusqueda(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(0, 6));
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(22);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(520);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGestion);
        botones.add(btnEditor);
        add(etiquetaResultado, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        btnGestion.addActionListener(this);
        btnEditor.addActionListener(this);
    }

    
    public void mostrar(List<File> encontrados) {
        archivos = encontrados;
        List<Object[]> filas = new ArrayList<Object[]>();
        for (File archivo : encontrados) {
            filas.add(new Object[]{archivo.getName(), archivo.getAbsolutePath(), archivo.length(),
                Formato.fechaHora(archivo.lastModified())});
        }
        modelo.setFilas(filas);
        if (encontrados.isEmpty()) {
            etiquetaResultado.setForeground(new Color(170, 40, 40));
            etiquetaResultado.setText(SIN_COINCIDENCIAS);
        } else {
            etiquetaResultado.setForeground(Color.BLACK);
            etiquetaResultado.setText("Se encontraron " + encontrados.size() + " archivo(s).");
        }
    }

    private File seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return new File((String) modelo.getValueAt(filaModelo, 1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File archivo = seleccionado();
        if (archivo == null) {
            ventana.mostrarError("Selecciona primero una fila de la tabla.");
        } else if (e.getSource() == btnGestion) {
            ventana.irAGestion(archivo);
        } else if (e.getSource() == btnEditor) {
            ventana.abrirEnEditor(archivo);
        }
    }
}
