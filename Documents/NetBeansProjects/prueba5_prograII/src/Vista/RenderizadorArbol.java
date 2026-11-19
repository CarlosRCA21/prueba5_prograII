package Vista;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


public class RenderizadorArbol extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree arbol, Object valor, boolean seleccionado,
            boolean expandido, boolean hoja, int fila, boolean conFoco) {
        super.getTreeCellRendererComponent(arbol, valor, seleccionado, expandido, hoja, fila, conFoco);
        Object dato = ((DefaultMutableTreeNode) valor).getUserObject();
        if (dato instanceof File) {
            File elemento = (File) dato;
            String texto = elemento.getName();
            if (texto.isEmpty()) {
                texto = elemento.getPath();
            }
            if (valor == arbol.getModel().getRoot()) {
                texto = elemento.getPath();
            }
            if (elemento.isHidden()) {
                texto = texto + " (oculto)";
            }
            setText(texto);
            Icon icono = null;
            if (elemento.isDirectory()) {
                icono = UIManager.getIcon("FileView.directoryIcon");
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                icono = UIManager.getIcon("FileView.fileIcon");
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            if (icono != null) {
                setIcon(icono);
            }
        }
        return this;
    }
}
