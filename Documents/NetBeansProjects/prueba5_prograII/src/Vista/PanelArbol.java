package Vista;

import Controlador.Controlador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class PanelArbol extends JPanel implements TreeSelectionListener, ActionListener {

    private static final long serialVersionUID = 1L;

    private final VentanaPrincipal ventana;
    private final JTree arbol = new JTree(new DefaultMutableTreeNode("Pulsa \"Cargar árbol\" para ver la estructura"));
    private final JButton btnEditor = new JButton("Abrir seleccionado en el editor");
    private final JButton btnGestion = new JButton("Usar en Gestión");

   
    public PanelArbol(VentanaPrincipal ventana, Controlador controlador) {
        this.ventana = ventana;
        setLayout(new BorderLayout(0, 6));
        arbol.setCellRenderer(new RenderizadorArbol());
        arbol.addTreeSelectionListener(this);
        add(new JScrollPane(arbol), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGestion);
        botones.add(btnEditor);
        add(botones, BorderLayout.SOUTH);
        btnEditor.addActionListener(this);
        btnGestion.addActionListener(this);
    }

   
    public void mostrar(DefaultMutableTreeNode raiz) {
        arbol.setModel(new DefaultTreeModel(raiz));
        arbol.expandRow(0);
    }

    private File seleccionado() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
        if (nodo != null && nodo.getUserObject() instanceof File) {
            return (File) nodo.getUserObject();
        }
        return null;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        File elemento = seleccionado();
        if (elemento != null) {
            ventana.actualizarElemento(elemento);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File elemento = seleccionado();
        if (elemento == null) {
            ventana.mostrarError("Selecciona primero un archivo o carpeta del árbol.");
        } else if (e.getSource() == btnEditor) {
            ventana.abrirEnEditor(elemento);
        } else if (e.getSource() == btnGestion) {
            ventana.irAGestion(elemento);
        }
    }
}
