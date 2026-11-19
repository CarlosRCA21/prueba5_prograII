package Vista;

import Controlador.Controlador;
import Logica.Formato;
import Logica.ResultadoEstadisticas;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;


public class PanelEstadisticas extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Controlador controlador;
    private final ModeloTabla modelo = new ModeloTabla(
            new String[]{"Tipo", "Cantidad", "Tamaño total", "% de archivos", "% del tamaño"},
            new Class<?>[]{String.class, Long.class, String.class, String.class, String.class});
    private final JTable tabla = new JTable(modelo);
    private final JTextArea areaGlobal = new JTextArea(8, 40);
    private final JButton btnExportar = new JButton("Exportar reporte (.txt / .csv)...");

   
    public PanelEstadisticas(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 8));
        tabla.setRowHeight(24);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(400, 150));
        areaGlobal.setEditable(false);
        areaGlobal.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaGlobal.setText("Pulsa \"Estadísticas\" para analizar la carpeta.");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnExportar);
        add(scrollTabla, BorderLayout.NORTH);
        add(new JScrollPane(areaGlobal), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        btnExportar.addActionListener(this);
    }

   
    public void mostrar(ResultadoEstadisticas resultado) {
        List<Object[]> filas = new ArrayList<Object[]>();
        for (int i = 0; i < ResultadoEstadisticas.CATEGORIAS.length; i++) {
            filas.add(new Object[]{ResultadoEstadisticas.CATEGORIAS[i], resultado.getCantidad(i),
                Formato.tamano(resultado.getTamano(i)), Formato.textoPorcentaje(resultado.porcentajeCantidad(i)),
                Formato.textoPorcentaje(resultado.porcentajeTamano(i))});
        }
        filas.add(new Object[]{"TOTAL", resultado.getTotalArchivos(), Formato.tamano(resultado.getTotalTamano()),
            "100.00 %", "100.00 %"});
        modelo.setFilas(filas);
        areaGlobal.setText(resultado.textoGlobal());
        areaGlobal.setCaretPosition(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte");
        FileNameExtensionFilter filtroTxt = new FileNameExtensionFilter("Texto (*.txt)", "txt");
        FileNameExtensionFilter filtroCsv = new FileNameExtensionFilter("CSV (*.csv)", "csv");
        selector.addChoosableFileFilter(filtroCsv);
        selector.addChoosableFileFilter(filtroTxt);
        selector.setFileFilter(filtroTxt);
        selector.setSelectedFile(new File(controlador.nombreReporteSugerido(false)));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File elegido = selector.getSelectedFile();
            if (selector.getFileFilter() == filtroCsv && !elegido.getName().toLowerCase().endsWith(".csv")
                    && !elegido.getName().toLowerCase().endsWith(".txt")) {
                elegido = new File(elegido.getPath() + ".csv");
            }
            controlador.exportarReporte(elegido);
        }
    }
}
