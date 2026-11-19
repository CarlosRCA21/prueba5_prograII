package Vista;

import Logica.Formato;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PanelDuplicados extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextArea area = new JTextArea();

    public PanelDuplicados() {
        setLayout(new BorderLayout());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setText("Pulsa \"Duplicados\" para buscar archivos con el mismo nombre y tamaño en distintas carpetas.");
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    
    public void mostrar(Map<String, List<File>> grupos) {
        if (grupos.isEmpty()) {
            area.setText("No se encontraron archivos duplicados.");
            return;
        }
        StringBuilder texto = new StringBuilder();
        texto.append("Grupos de duplicados encontrados: ").append(grupos.size()).append("\n\n");
        int numero = 1;
        for (Map.Entry<String, List<File>> grupo : grupos.entrySet()) {
            List<File> archivos = grupo.getValue();
            File primero = archivos.get(0);
            texto.append("Grupo ").append(numero).append(": ").append(primero.getName())
                    .append(" (").append(Formato.tamano(primero.length())).append(") - ")
                    .append(archivos.size()).append(" copias\n");
            for (File archivo : archivos) {
                texto.append("    ").append(archivo.getAbsolutePath()).append("\n");
            }
            texto.append("\n");
            numero++;
        }
        area.setText(texto.toString());
        area.setCaretPosition(0);
    }
}
